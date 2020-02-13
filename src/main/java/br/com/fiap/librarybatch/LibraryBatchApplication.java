package br.com.fiap.librarybatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.io.File;
import java.nio.file.Paths;

@SpringBootApplication
@EnableBatchProcessing
public class LibraryBatchApplication {

    Logger logger = LoggerFactory.getLogger(LibraryBatchApplication.class.getName());

    @Bean
    public Tasklet deleteFile(@Value("${file.path}") String path) {
        return ((contribution, chunkContext) -> {

            File file = Paths.get(path).toFile();

            logger.info(path);

            if(file.delete())
                logger.info("Arquivo deletado");

            return RepeatStatus.FINISHED;
        });
    }

    @Bean
    @Scope("prototype")
    public Step deletStep(StepBuilderFactory stepBuilderFactory,
                          Tasklet tasklet)
    {
        return stepBuilderFactory.get("Delete file step")
                .tasklet(tasklet)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Job deleteJob(JobBuilderFactory jobBuilderFactory,
                         Step step)
    {
        return jobBuilderFactory.get("delete job")
                .start(step)
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(LibraryBatchApplication.class, args);
    }



}
