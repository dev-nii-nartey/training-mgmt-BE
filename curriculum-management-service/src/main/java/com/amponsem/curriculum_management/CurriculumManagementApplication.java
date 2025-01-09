package com.amponsem.curriculum_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class CurriculumManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurriculumManagementApplication.class, args);
	}

}
