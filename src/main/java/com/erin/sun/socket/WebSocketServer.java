package com.erin.sun.socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * WebSocket服务
 *
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 */
@Slf4j
@RestController
@RequestMapping("/websocket")
@ServerEndpoint(value = "/websocket/{userId}", configurator = MyEndpointConfigure.class)
public class WebSocketServer {

    // ConcurrentHashMap用来存放每个客户端对应的WebSocketServer对象。
    private static ConcurrentHashMap<String, Session> webSocketSet = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        webSocketSet.put(userId, session);
    }

    private void sendMessage(String userId, String message) {
        try {
            Session currentSession = webSocketSet.get(userId);
            if (currentSession != null) {
                currentSession.getBasicRemote().sendText(message);
            }
            log.info("推送消息成功，消息为：" + message);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 自定义消息
     */
    public static void sendInfo(String message) {
        for (Map.Entry<String, Session> entry : webSocketSet.entrySet()) {
            if (entry.getValue().isOpen()) {
                try {
                    entry.getValue().getBasicRemote().sendText(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 用户退出时，连接关闭调用的方法
     */
    public static void onCloseConection(String userId) {
        webSocketSet.remove(userId); // 从set中删除
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        log.info("一个客户端关闭连接");
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message) {
        //todo message
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("websocket出现错误");
    }
}
