package com.atguigu.gmall.gateway.config;

import com.atguigu.core.utils.JwtUtils;
import java.net.HttpCookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthGatewayFilter implements GatewayFilter, Ordered {

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //获取cookie中的token信息
        MultiValueMap<String, org.springframework.http.HttpCookie> cookies = request.getCookies();
        //判断是否存在，不存在重定向到登录页面
        if (cookies == null || !cookies.containsKey(jwtProperties.getCookieName())) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();//设置想用状态码为认证，结束请求
        }
        //若存在，尝试解析
        org.springframework.http.HttpCookie cookie = cookies.getFirst(this.jwtProperties.getCookieName());
        try {
            JwtUtils.getInfoFromToken(cookie.getValue(),this.jwtProperties.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();//设置响应状态码为未认证，结束请求
        }
        return chain.filter(exchange);
    }

    //数字越小优先级越高
    @Override
    public int getOrder() {
        return 0;
    }
}
