/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.batch.initialdemo.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Michael Minella
 */
@Configuration
public class TransitionsConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job multiStepJob(Step step1, Step step2, Step step3) {
		return this.jobBuilderFactory.get("multiStepJob")
				.incrementer(new RunIdIncrementer())
				.start(step1)
				.on("FAILED").to(step2)
				.from(step1).on("*").to(step3)
				.end()
				.build();
	}

	@Bean
	public Step step1() {
		return this.stepBuilderFactory.get("step1")
				.tasklet((stepContribution, chunkContext) -> {
					System.out.println("Step1 was executed!");

					throw new Exception("Let's see what happens...");
//					return RepeatStatus.FINISHED;
				}).build();
	}

	@Bean
	public Step step2() {
		return this.stepBuilderFactory.get("step2")
				.tasklet((stepContribution, chunkContext) -> {
					System.out.println("Step2 was executed!");

					return RepeatStatus.FINISHED;
				}).build();
	}

	@Bean
	public Step step3() {
		return this.stepBuilderFactory.get("step3")
				.tasklet((stepContribution, chunkContext) -> {
					System.out.println("Step3 was executed!");

					return RepeatStatus.FINISHED;
				}).build();
	}
}
