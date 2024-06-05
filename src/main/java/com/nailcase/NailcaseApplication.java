package com.nailcase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = {"classpath:/application.properties"})
public class NailcaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(NailcaseApplication.class, args);
	}

}
