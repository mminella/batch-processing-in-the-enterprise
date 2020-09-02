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

package io.spring.batch.errordemo.configuration;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * @author Michael Minella
 */
@Configuration
public class SkipConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	@StepScope
	public FlatFileItemReader<RestartConfiguration.Item> itemReader(@Value("#{jobParameters['batch.input']}") Resource resource) {
		return new FlatFileItemReaderBuilder<RestartConfiguration.Item>()
				.resource(resource)
				.name("itemReader")
				.delimited()
				.names("first", "last", "phone")
				.targetType(RestartConfiguration.Item.class)
				.build();
	}

	@Bean
	public ItemProcessor<RestartConfiguration.Item, RestartConfiguration.Item> itemProcessor() {
		return new ItemProcessor<RestartConfiguration.Item, RestartConfiguration.Item>() {
			@Override
			public RestartConfiguration.Item process(RestartConfiguration.Item item) throws Exception {
				return new RestartConfiguration.Item(item.getFirst(),
						item.getLast().toUpperCase(),
						item.getPhone());
			}
		};
	}

	@Bean
	public JdbcBatchItemWriter<RestartConfiguration.Item> itemWriter(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<RestartConfiguration.Item>()
				.dataSource(dataSource)
				.sql("INSERT INTO ITEM VALUES (:first, :last, :phone)")
				.beanMapped()
				.build();
	}

	@Bean
	public Job skipJob(Step step1) {
		return this.jobBuilderFactory.get("skipJob")
				.incrementer(new RunIdIncrementer())
				.start(step1)
				.build();
	}

	@Bean
	public Step step1(ItemReader<RestartConfiguration.Item> itemReader,
			ItemProcessor<RestartConfiguration.Item, RestartConfiguration.Item> itemProcessor,
			ItemWriter<RestartConfiguration.Item> itemWriter) {
		return this.stepBuilderFactory.get("step1")
				.<RestartConfiguration.Item, RestartConfiguration.Item>chunk(10)
				.reader(itemReader)
				.processor(itemProcessor)
				.writer(itemWriter)
				.faultTolerant()
				.skip(Exception.class)
				.skipLimit(5)
				.build();
	}

	public static class Item {
		private String first;
		private String last;
		private String phone;

		public Item() {
		}

		public Item(String first, String last, String phone) {
			this.first = first;
			this.last = last;
			this.phone = phone;
		}

		public String getFirst() {
			return first;
		}

		public void setFirst(String first) {
			this.first = first;
		}

		public String getLast() {
			return last;
		}

		public void setLast(String last) {
			this.last = last;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}
	}
}
