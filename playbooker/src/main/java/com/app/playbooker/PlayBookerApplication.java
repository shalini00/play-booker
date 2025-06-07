package com.app.playbooker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "entityAuditor")
@EnableAsync
public class PlayBookerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlayBookerApplication.class, args);
	}

}
