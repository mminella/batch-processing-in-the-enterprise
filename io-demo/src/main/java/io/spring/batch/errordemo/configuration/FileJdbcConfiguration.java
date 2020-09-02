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
public class FileJdbcConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	@StepScope
	public FlatFileItemReader<Item> itemReader(@Value("#{jobParameters['batch.input']}") Resource resource) {
		return new FlatFileItemReaderBuilder<Item>()
				.resource(resource)
				.name("itemReader")
				.delimited()
				.names("first", "last", "phone")
				.targetType(Item.class)
				.build();
	}

	@Bean
	public ItemProcessor<Item, Item> itemProcessor() {
		return new ItemProcessor<Item, Item>() {
			@Override
			public Item process(Item item) throws Exception {
				return new Item(item.getFirst(),
						item.getLast().toUpperCase(),
						item.getPhone());
			}
		};
	}

	@Bean
	public JdbcBatchItemWriter<Item> itemWriter(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Item>()
				.dataSource(dataSource)
				.sql("INSERT INTO ITEM VALUES (:first, :last, :phone)")
				.beanMapped()
				.build();
	}

	@Bean
	public Job job(Step step1) {
		return this.jobBuilderFactory.get("job")
				.incrementer(new RunIdIncrementer())
				.start(step1)
				.build();
	}

	@Bean
	public Step step1(ItemReader<Item> itemReader,
			ItemProcessor<Item, Item> itemProcessor,
			ItemWriter<Item> itemWriter) {
		return this.stepBuilderFactory.get("step1")
				.<Item, Item>chunk(100)
				.reader(itemReader)
				.processor(itemProcessor)
				.writer(itemWriter)
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
