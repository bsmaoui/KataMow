package com.kata.mow.config;

import com.kata.mow.model.InputData;
import com.kata.mow.utils.BatchStepSkipper;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
@EnableBatchProcessing
@PropertySource("classpath:application.properties")
public class MowJobConfig {

  private final JobRepository jobRepository;

  public MowJobConfig(JobRepository jobRepository) {
    this.jobRepository = jobRepository;
  }

  @Value("${inputData}")
  private Resource inputFeed;
  
  @Autowired
  private BatchStepSkipper skipper;
  
  @Bean(name="mowJob")
  public Job mowJob(Step step) {

    var name = "Tondeuse";
    var builder = new JobBuilder(name, jobRepository);

    return builder.start(step)
        .build();
  }

  @Bean
  public Step step(ItemReader<InputData> reader,
                    ItemWriter<InputData> writer,
                    ItemProcessor<InputData, InputData> processor,
                    PlatformTransactionManager txManager) {
    var name = "Tondeuse mouvement";
    var builder = new StepBuilder(name, jobRepository);
    return builder
        .<InputData, InputData>chunk(1, txManager)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .faultTolerant()
        .skipPolicy(skipper)
        .build();
  }



  @Bean
  public DefaultLineMapper<InputData> lineMapper(LineTokenizer tokenizer,
                                              FieldSetMapper<InputData> mapper) {
    var lineMapper = new DefaultLineMapper<InputData>();
    lineMapper.setLineTokenizer(tokenizer);
    lineMapper.setFieldSetMapper(mapper);
    return lineMapper;
  }

  @Bean
  public BeanWrapperFieldSetMapper<InputData> fieldSetMapper() {
    var fieldSetMapper = new BeanWrapperFieldSetMapper<InputData>();
    fieldSetMapper.setTargetType(InputData.class);
    return fieldSetMapper;
  }

  @Bean
  public DelimitedLineTokenizer tokenizer() {
    var tokenizer = new DelimitedLineTokenizer();
    tokenizer.setNames("line");
    tokenizer.setStrict(false);
    return tokenizer;
  }

}