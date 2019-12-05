package com.erin.sun.system.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.erin.sun.common.config.RabbitConfig;
import com.erin.sun.common.util.SendEmailUtil;
import com.erin.sun.common.util.SendEmailUtilMq;
import com.erin.sun.system.domain.MailTemplate;
import com.erin.sun.system.domain.MsgLog;
import com.erin.sun.system.service.IMsgLogService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
@Slf4j
public class MailConsumer {

    @Autowired
    private IMsgLogService iMsgLogService;

    @RabbitListener(queues = RabbitConfig.MAIL_QUEUE_NAME)
    public void consume(Message message, Channel channel) throws IOException {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        msg = msg.replaceAll("\\\\", "");
        msg = msg.substring(1, msg.length() - 1);
        MailTemplate mailTemplate = JSON.parseObject(msg, MailTemplate.class);
        log.info("收到消息: {}", mailTemplate.toString());

        String msgId = mailTemplate.getMsgId();
        QueryWrapper<MsgLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("msg_id", msgId);
        UpdateWrapper<MsgLog> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("msg_id", msgId);
        MsgLog msgLog = iMsgLogService.getOne(queryWrapper);

        // 消费幂等性
        if (null == msgLog || msgLog.getStatus().equals(RabbitConfig.MAIL_CONSUMED_SUCCESS)) {
            log.info("重复消费, msgId: {}", msgId);
            return;
        }

        MessageProperties properties = message.getMessageProperties();
        long tag = properties.getDeliveryTag();

        boolean success = new SendEmailUtilMq(mailTemplate, new ArrayList<>()).send();
        if (success) {
            msgLog.setStatus(RabbitConfig.MAIL_CONSUMED_SUCCESS);
            msgLog.setUpdateTime(LocalDateTime.now());
            iMsgLogService.update(msgLog, updateWrapper);
            log.info("消费成功！");
            channel.basicAck(tag, false);// 消费确认
        } else {
            msgLog.setStatus(RabbitConfig.MAIL_DELIVER_FAIL);
            msgLog.setUpdateTime(LocalDateTime.now());
            iMsgLogService.update(msgLog, updateWrapper);
            log.info("投递失败！");
            channel.basicAck(tag, false);// 消费确认
//            channel.basicNack(tag, false, true);
        }
    }
}