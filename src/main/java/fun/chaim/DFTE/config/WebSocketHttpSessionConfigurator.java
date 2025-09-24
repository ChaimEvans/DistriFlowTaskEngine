package fun.chaim.DFTE.config;

import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;

public class WebSocketHttpSessionConfigurator extends ServerEndpointConfig.Configurator {
    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        HttpServletRequest httpRequest = (HttpServletRequest) request.getHttpSession();
        if (httpRequest != null) {
            config.getUserProperties().put("ip", httpRequest.getRemoteAddr());
            config.getUserProperties().put("mac",
                    Optional.ofNullable(request.getHeaders().get("X-MAC"))
                            .filter(list -> !list.isEmpty())
                            .map(list -> list.get(0))
                            .orElse(null));
            config.getUserProperties().put("node_name",
                    Optional.ofNullable(request.getHeaders().get("X-NODE-NAME"))
                            .filter(list -> !list.isEmpty())
                            .map(list -> list.get(0))
                            .orElse(null));
        }
    }
}
