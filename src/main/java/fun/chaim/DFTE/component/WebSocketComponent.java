package fun.chaim.DFTE.component;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import fun.chaim.DFTE.config.WebSocketHttpSessionConfigurator;
import fun.chaim.DFTE.converter.ProcessingNodeStatusDataDecoder;
import fun.chaim.DFTE.dto.ProcessingNodeStatusData;
import fun.chaim.DFTE.service.ProcessingNodeService;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@CrossOrigin(origins = "*")
@ServerEndpoint(value = "/processing-node", configurator = WebSocketHttpSessionConfigurator.class, decoders = ProcessingNodeStatusDataDecoder.class)
public class WebSocketComponent {

    private ProcessingNodeService service;

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        service = SpringContext.getBean(ProcessingNodeService.class);
        Map<String, Object> userProperties = config.getUserProperties();
        service.register(session, (String)userProperties.get("name"), (String)userProperties.get("ip"), (String)userProperties.get("mac"));
        log.info("与节点建立连接: {}", session.getId());
    }

    @OnMessage
    public void onMessage(ProcessingNodeStatusData message, Session session) {
        log.info("收到消息: ({}) {}", session.getId(), message);
        if (message != null) {
            service.setStatusData(session.getId(), message);
        }
    }

    @OnClose
    public void onClose(Session session) {
        service.unregister(session.getId());
        log.info("连接关闭: {}", session.getId());
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误: ({}) {}", session.getId(), error.getMessage());
    }

}
