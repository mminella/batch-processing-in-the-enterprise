package io.spring.batch.errordemo;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class ErrorDemoApplication {

	public static void main(String[] args) {
		String[] realArgs = new String [] {
				"batch.input=/invalid-input.csv",
				"run.id=1"
		};

		SpringApplication.run(ErrorDemoApplication.class, realArgs);
	}

}
