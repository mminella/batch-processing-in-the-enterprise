package io.spring.batch.initialdemo;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class InitialDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(InitialDemoApplication.class, args);
	}

}
