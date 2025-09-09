package org.netway.dongnehankki.global.config;

import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActuatorConfig {

    @Bean
    public HttpExchangeRepository httpExchangeRepository() {
        // API 호출 기록을 메모리에 100개까지 저장하도록 설정
        return new InMemoryHttpExchangeRepository();
    }
}
