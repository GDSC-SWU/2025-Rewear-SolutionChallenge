package com.example.rewear.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        RestTemplate rt = builder.build();
        rt.getInterceptors().add(logInterceptor());
        return rt;
    }

    private ClientHttpRequestInterceptor logInterceptor() {
        return (request, body, execution) -> {
            System.out.println("▶▶▶ AI 요청 URL: " + request.getMethod() + " " + request.getURI());
            System.out.println("▶▶▶ 페이로드: " + new String(body, StandardCharsets.UTF_8));
            ClientHttpResponse response = execution.execute(request, body);
            System.out.println("◀◀◀ HTTP 응답 상태: " + response.getStatusCode());
            return response;
        };
    }
}
