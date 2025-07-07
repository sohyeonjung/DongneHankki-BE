package org.netway.dongnehankki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class DongneHankkiBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(DongneHankkiBeApplication.class, args);
	}

}