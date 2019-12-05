package com.erin.sun.system.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息投递日志
 *
 * @author jxd
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
public class MsgLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息唯一标识
     */
    private String msgId;

    /**
     * 消息体, json格式化
     */
    private String msg;

    /**
     * 交换机
     */
    private String exchange;

    /**
     * 路由键
     */
    private String routingKey;

    /**
     * 状态: 0投递中 1投递成功 2投递失败 3已消费
     */
    private Integer status;

    /**
     * 重试次数
     */
    private Integer tryCount;

    /**
     * 下一次重试时间
     */
    private LocalDateTime nextTryTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    public MsgLog(String msgId, String msg, String exchange, String routingKey, LocalDateTime createTime) {
        this.msgId = msgId;
        this.msg = msg;
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.createTime = createTime;
    }
}
