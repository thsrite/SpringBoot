package com.erin.sun.system.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class MailTemplate implements Serializable {

    private String msgId;
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
     * 邮箱账号
     */
    private String account;

    /**
     * 邮箱密码
     */
    private String password;

    /**
     * 邮箱类型
     */
    private String sendType;

    /**
     * 构造器
     *
     * @param to      收件人
     * @param subject 主题
     * @param content 内容
     */
    public MailTemplate(String account, String password, String sendType, String to, String toName, String subject, String content) {
        this.account = account;
        this.password = password;
        this.sendType = sendType;
        this.to = to;
        this.toName = toName;
        this.subject = subject;
        this.content = content;
    }

    /**
     * 构造器
     *
     * @param to             收件人
     * @param subject        主题
     * @param content        内容
     * @param attachFileList 附件列表
     */
    public MailTemplate(String account, String password, String sendType, String to, String toName, String subject, String content, List<String> attachFileList) {
        this(account, password, sendType, to, toName, subject, content);
        this.attachFileList = attachFileList;
    }
}
