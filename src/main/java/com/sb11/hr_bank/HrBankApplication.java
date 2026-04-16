package com.sb11.hr_bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HrBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(HrBankApplication.class, args);
	}

}
