package com.erin.sun.system.controller;

import com.alibaba.fastjson.JSON;
import com.erin.sun.common.config.RabbitConfig;
import com.erin.sun.system.domain.MailTemplate;
import com.erin.sun.system.domain.MsgLog;
import com.erin.sun.system.service.IMsgLogService;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@RestController
public class MailController {


    @Autowired
    private IMsgLogService iMsgLogService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping("test")
    public void test(String account, String password, String sendType) {
        try {
            for (int j = 1; j <= 1; j++) {
                for (int i = 1; i <= 1; i++) {
                    for (int num = 1; num <= 1; num++) {
                        // 设置发信参数
                        final String toName = "我是" + num + "号";
                        final String to = "test" + num + "@forexgwg.com";
                        String subject = num + " 第" + num + "次发送测试邮件标题";
                        final String content = "<p style='color:red'>这是邮件内容正文。</p></br>";
                        MailTemplate mailTemplate = new MailTemplate();
                        String msgId = UUID.randomUUID().toString();
                        mailTemplate.setMsgId(msgId);
                        mailTemplate.setAccount(account);
                        mailTemplate.setPassword(password);
                        mailTemplate.setSendType(sendType);
                        mailTemplate.setToName(toName);
                        mailTemplate.setTo(to);
                        mailTemplate.setSubject(subject);
                        mailTemplate.setContent(content);
                        mailTemplate.setAttachFileList(new ArrayList<>());

                        MsgLog msgLog = new MsgLog(msgId, JSON.toJSONString(mailTemplate), RabbitConfig.MAIL_EXCHANGE_NAME, RabbitConfig.MAIL_ROUTING_KEY_NAME, LocalDateTime.now());
                        iMsgLogService.save(msgLog);
                        CorrelationData correlationData = new CorrelationData(msgId);
                        Thread.sleep(1000);
                        rabbitTemplate.convertAndSend(RabbitConfig.MAIL_EXCHANGE_NAME, RabbitConfig.MAIL_ROUTING_KEY_NAME, JSON.toJSONString(mailTemplate), correlationData);// 发送消息
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("错误： " + e);
        }
    }
}
