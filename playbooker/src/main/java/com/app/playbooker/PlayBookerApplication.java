package com.app.playbooker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "entityAuditor")
public class PlayBookerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlayBookerApplication.class, args);
	}

}
