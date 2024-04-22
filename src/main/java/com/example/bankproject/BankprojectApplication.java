package com.example.bankproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication(scanBasePackages = {"com.example.bankproject", "com.example.bankproject.repositorys"})
public class BankprojectApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankprojectApplication.class, args);
	}

}