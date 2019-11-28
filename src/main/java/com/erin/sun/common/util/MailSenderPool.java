package com.erin.sun.common.util;

import java.util.concurrent.*;

/**
 * 邮件发送线程池
 *
 * @author Zebe
 */
public class MailSenderPool {

    /**
     * 邮件发送器线程数量
     */
    private static int SENDER_TOTAL = 10;

    /**
     * 线程工厂（生产环境中建议使用带命名参数的线程工厂）
     */
    private static ThreadFactory threadFactory = Executors.defaultThreadFactory();

    /**
     * 线程池执行器（采用了Spring提供的CustomizableThreadFactory，如果不需要请使用默认线程工厂）
     */
    private static ExecutorService executor = new ThreadPoolExecutor(SENDER_TOTAL, 200,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1024), threadFactory,
            new ThreadPoolExecutor.AbortPolicy());

    /**
     * 内部静态实例
     */
    private static class Inner {
        private static MailSenderPool instance = new MailSenderPool();
    }

    /**
     * 获取实例
     *
     * @return 返回邮件发送线程池实例
     */
    public static MailSenderPool getInstance() {
        return Inner.instance;
    }

    /**
     * 通过线程发送
     *
     * @param sender 邮件发送器
     * @return 返回自身
     */
    public MailSenderPool sendByThread(MailSender sender) {
        executor.execute(sender);
        return getInstance();
    }

    /**
     * 关闭线程池
     */
    public void shutDown() {
        executor.shutdown();
    }

}