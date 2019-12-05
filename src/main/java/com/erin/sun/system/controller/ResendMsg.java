package com.erin.sun.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.erin.sun.common.config.RabbitConfig;
import com.erin.sun.system.domain.MsgLog;
import com.erin.sun.system.service.IMsgLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class ResendMsg {

    @Autowired
    private IMsgLogService iMsgLogService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // 最大投递次数
    private static final int MAX_TRY_COUNT = 3;

    /**
     * 每60s拉取投递失败的消息, 重新投递
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void resend() {
        log.info("开始执行定时任务(重新投递消息)");
        UpdateWrapper<MsgLog> updateWrapper = new UpdateWrapper<>();
        QueryWrapper<MsgLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", RabbitConfig.MAIL_DELIVER_FAIL)
                    .lt("try_count",3);

        List<MsgLog> msgLogs = iMsgLogService.list(queryWrapper);
        msgLogs.forEach(msgLog -> {
            String msgId = msgLog.getMsgId();
            updateWrapper.eq("msg_id", msgId);
            if (msgLog.getTryCount() >= MAX_TRY_COUNT) {
                msgLog.setStatus(RabbitConfig.MAIL_DELIVER_FAIL);
                msgLog.setUpdateTime(LocalDateTime.now());
                iMsgLogService.update(msgLog, updateWrapper);
                log.info("超过最大重试次数, 消息投递失败, msgId: {}", msgId);
            } else {
                msgLog.setTryCount(msgLog.getTryCount() + 1);
                msgLog.setUpdateTime(LocalDateTime.now());
                msgLog.setNextTryTime(LocalDateTime.now().plusSeconds(60));
                iMsgLogService.update(msgLog, updateWrapper);// 投递次数+1

                CorrelationData correlationData = new CorrelationData(msgId);
                rabbitTemplate.convertAndSend(msgLog.getExchange(), msgLog.getRoutingKey(), msgLog.getMsg(), correlationData);// 重新投递
                log.info("第 " + (msgLog.getTryCount()) + " 次重新投递消息");
            }
        });
        log.info("定时任务执行结束(重新投递消息)");
    }
}