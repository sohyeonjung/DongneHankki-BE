package org.netway.dongnehankki.global.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "org.netway.dongnehankki",
	excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "org.netway.dongnehankki.global.auth.jwt.*Repository"))
@Configuration
public class JpaConfig {
}
