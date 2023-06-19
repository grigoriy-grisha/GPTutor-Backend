package com.chatgpt.interceptors;

import com.chatgpt.services.AuthCheckerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.Objects;

@Component
public class WebSocketInterceptor implements HandshakeInterceptor {
    @Autowired
    AuthCheckerService authCheckerService;

    @Value("${auth.skip}")
    boolean skipAuth;

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler wsHandler, @NonNull Map<String,  Object> attributes) throws Exception {
        if (skipAuth) {
            attributes.put("vkUserId", "0");
            return true;
        }

        String authorizationHeader = Objects.requireNonNull(request.getHeaders().get("origin")).get(0);

        if (authorizationHeader != null) {
            boolean isSignSuccess = authCheckerService.checkAuthorizationHeader(authorizationHeader);

            if (isSignSuccess) {
                attributes.put("vkUserId",  authCheckerService.getVkUserId(
                        authCheckerService.splitBearer(authorizationHeader)
                ));

                return true;
            }
        }

        return false;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler wsHandler, Exception exception) {
    }
}