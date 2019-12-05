package com.erin.sun.common.util;

import com.erin.sun.system.domain.MailTemplate;
import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * 邮件发送工具类。
 * 以下邮件中的配置参数，请在实际环境中，根据需要采取合适的配置方式。
 * 发送邮件依赖 com.sun.mail（1.6.1） 包、javax.mail（1.5.0-b01） 包。
 * 如果使用 Idea 运行，请将这两个包（可以直接到Maven目录下面去找）添加到项目的 Libraries 里面（快捷键：Ctrl + Alt + Shift + S）
 *
 * @author Zebe
 */
public class SendEmailUtilMq {

    /**
     * 发件人别名（可以为空）
     */
    private final static String fromAliasName = "青岛国际院士港";

    /**
     * 登录用户名
     */
    private String ACCOUNT;

    /**
     * 登录密码
     */
    private String PASSWORD;

    /**
     * 邮件服务器地址
     */
    //QQ企业邮箱：smtp.exmail.qq.com
    //网易企业邮箱：smtphz.qiye.163.com
    private String HOST;

    /**
     * 发信端口
     */
    //QQ企业邮箱：465
    //网易企业邮箱：994
    private String PORT;

    /**
     * 发信协议
     */
    private final static String PROTOCOL = "ssl";

    /**
     * 收件人
     */
    private String to;

    /**
     * 收件人名称
     */
    private String toName;

    /**
     * 主题
     */
    private String subject;

    /**
     * 内容
     */
    private String content;

    /**
     * 附件列表（可以为空）
     */
    private List<String> attachFileList;

    /**
     * 构造器
     *
     * @param attachFileList 附件列表
     */
    public SendEmailUtilMq(MailTemplate mailTemplate, List<String> attachFileList) {
        this.to = mailTemplate.getTo();
        this.toName = mailTemplate.getToName();
        this.subject = mailTemplate.getSubject();
        this.content =
                "<div style=\"background:url('https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575274292469&di=2240541b0ae3f2315c595dae9d6f2a6c&imgtype=0&src=http%3A%2F%2Fimg29.photophoto.cn%2F20131127%2F0025008354227032_s.jpg');" +
                        "background-size:100% 100%;width:500px;height:700px\">" +
                        "<h1 style=\"color:blue;\">" + mailTemplate.getToName() + " :</h1>" +
                        "<h4 style=\"padding-left:50px\">" + mailTemplate.getSubject() + "</h4>" +
                        "<p>" + mailTemplate.getContent() + "</p ><br></div><div><includetail><!--<![endif]--></includetail></div>";
        this.attachFileList = attachFileList;
        this.ACCOUNT = mailTemplate.getAccount();
        this.PASSWORD = mailTemplate.getPassword();
        switch (mailTemplate.getSendType()) {
            case "qq":
                this.HOST = "smtp.exmail.qq.com";
                this.PORT = "465";
                break;
            case "163":
                this.HOST = "smtp.ym.163.com";
                this.PORT = "994";
                break;
        }
    }

    /**
     * 认证信息
     */
    static class MyAuthenticator extends Authenticator {

        /**
         * 用户名
         */
        String username = null;

        /**
         * 密码
         */
        String password = null;

        /**
         * 构造器
         *
         * @param username 用户名
         * @param password 密码
         */
        public MyAuthenticator(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    }

    /**
     * 发送邮件
     */
    public boolean send() {
        // 设置邮件属性
        Properties prop = new Properties();
        prop.setProperty("mail.transport.protocol", PROTOCOL);
        prop.setProperty("mail.smtp.host", HOST);
        prop.setProperty("mail.smtp.port", PORT);
        prop.setProperty("mail.smtp.auth", "true");
        MailSSLSocketFactory sslSocketFactory = null;
        try {
            sslSocketFactory = new MailSSLSocketFactory();
            sslSocketFactory.setTrustAllHosts(true);
        } catch (GeneralSecurityException e1) {
            e1.printStackTrace();
        }
        if (sslSocketFactory == null) {
            System.err.println("开启 MailSSLSocketFactory 失败");
        } else {
            prop.put("mail.smtp.ssl.enable", "true");
            prop.put("mail.smtp.ssl.socketFactory", sslSocketFactory);
            // 创建邮件会话（注意，如果要在一个进程中切换多个邮箱账号发信，应该用 Session.getInstance）
            Session session = Session.getDefaultInstance(prop, new MyAuthenticator(ACCOUNT, PASSWORD));
            // 开启调试模式（生产环境中请不要开启此项）
            session.setDebug(true);
            try {
                MimeMessage mimeMessage = new MimeMessage(session);
                // 设置发件人别名（如果未设置别名就默认为发件人邮箱）
                mimeMessage.setFrom(new InternetAddress(ACCOUNT, fromAliasName));
                // 设置主题和收件人、发信时间等信息
                mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to, toName));
                mimeMessage.setSubject(subject);
                mimeMessage.setSentDate(new Date());

               /* //图片
                MimeBodyPart img = new MimeBodyPart();
                DataHandler dh = new DataHandler(new FileDataSource("src/main/resources/2.jpg"));//图片路径
                img.setDataHandler(dh);
                img.setContentID("img");
                //正文
                MimeBodyPart text = new MimeBodyPart();
                text.setContent("这里正文内容  for img<img src='cid:img'><br/>", "text/html;charset=utf-8"); //注意编码问题

                //描述数据关系
                MimeMultipart mm = new MimeMultipart();
                mm.addBodyPart(text);
                mm.addBodyPart(img);
                mm.setSubType("mixd");

                //图片-文本--复合--转普通节点
                MimeBodyPart tex_image_tPart=new MimeBodyPart();
                tex_image_tPart.setContent(mm);*/

                // 如果有附件信息，则添加附件
                if (!attachFileList.isEmpty()) {
                    Multipart multipart = new MimeMultipart();
                    MimeBodyPart body = new MimeBodyPart();
                    body.setContent(content, "text/html; charset=UTF-8");
                    multipart.addBodyPart(body);
                    // 添加所有附件（添加时判断文件是否存在）
                    for (String filePath : attachFileList) {
                        if (Files.exists(Paths.get(filePath))) {
                            MimeBodyPart tempBodyPart = new MimeBodyPart();
                            tempBodyPart.attachFile(filePath);
                            multipart.addBodyPart(tempBodyPart);
                        }
                    }
                    mimeMessage.setContent(multipart);
                } else {
                    Multipart multipart = new MimeMultipart();
                    MimeBodyPart body = new MimeBodyPart();
                    body.setContent(content, "text/html; charset=UTF-8");
                    multipart.addBodyPart(body);
//                    MimeMultipart multipart=new MimeMultipart();
//                    multipart.addBodyPart(tex_image_tPart);
//                    multipart.setSubType("mixd");//混合关系
                    mimeMessage.setContent(multipart, "text/html; charset=UTF-8");
                    //mimeMessage.setText(content);
                }
                //要求阅读回执
                mimeMessage.setHeader("Disposition-Notification-To", "1");
                // 开始发信
                mimeMessage.saveChanges();
                Transport.send(mimeMessage);
                return true;
            } catch (MessagingException | IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}