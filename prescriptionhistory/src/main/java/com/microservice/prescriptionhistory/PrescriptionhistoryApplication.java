package com.microservice.prescriptionhistory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PrescriptionhistoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrescriptionhistoryApplication.class, args);
	}

}
