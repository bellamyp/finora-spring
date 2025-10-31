package com.bellamyphan.finora_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FinoraSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinoraSpringApplication.class, args);
	}

}
