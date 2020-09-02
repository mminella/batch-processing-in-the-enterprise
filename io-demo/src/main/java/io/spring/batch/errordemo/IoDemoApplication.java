package io.spring.batch.errordemo;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class IoDemoApplication {

	public static void main(String[] args) {
		String[] realArgs = new String [] {
				"batch.input=/input.csv"
		};

		SpringApplication.run(IoDemoApplication.class, realArgs);
	}

}
