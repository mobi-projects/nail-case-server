package com.nailcase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class NailcaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(NailcaseApplication.class, args);
	}

}
