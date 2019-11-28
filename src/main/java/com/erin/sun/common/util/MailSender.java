package com.erin.sun.common.util;

import java.util.List;

/**
 * 邮件发送器
 *
 * @author Zebe
 */
public class MailSender implements Runnable {

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
     * 附件列表
     */
    private List<String> attachFileList;

    /**
     * 构造器
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     */
    public MailSender(String to,String toName, String subject, String content) {
        this.to = to;
        this.toName = toName;
        this.subject = subject;
        this.content = content;
    }

    /**
     * 构造器
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     * @param attachFileList 附件列表
     */
    public MailSender(String to, String toName,String subject, String content, List<String> attachFileList) {
        this(to, toName, subject, content);
        this.attachFileList = attachFileList;
    }

    @Override
    public void run() {
        new SendEmailUtil(to,toName, subject, content, attachFileList).send();
    }

}