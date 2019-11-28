package com.erin.sun.system.controller;



import com.erin.sun.common.util.MailSender;
import com.erin.sun.common.util.MailSenderPool;

import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class MailController {

public static void main(String[] args) {
        try {
            int num;
            // 通过线程池发送邮件
            MailSenderPool pool = MailSenderPool.getInstance();
            for (num=1;num<=73;num++){
                // 设置发信参数
                final String fromAliasName = "青岛国际院士港";
                final String toName = "我是" + num + "号";
                final String to = "test" + num + "@forexgwg.com";
                String subject = num + " 第一次发送测试邮件标题";
                final String content = "<p style='color:red'>这是邮件内容正文。</p>";
                pool.sendByThread(new MailSender(to,toName, subject, content, new ArrayList<>()));
                subject = num + " 第一次发送测试邮件标题";
                pool.sendByThread(new MailSender(to,toName, subject, content, new ArrayList<>()));
            }
            pool.shutDown();
        }catch (Exception e){
            System.out.println("错误： " + e);
        }
    }
}
