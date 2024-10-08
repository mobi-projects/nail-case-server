package com.nailcase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class NailcaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(NailcaseApplication.class, args);
	}

}
