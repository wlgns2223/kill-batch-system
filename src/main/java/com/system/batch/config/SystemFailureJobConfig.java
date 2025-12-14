//package com.system.batch.config;
//
//import lombok.Data;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.StepScope;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.batch.item.Chunk;
//import org.springframework.batch.item.ItemWriter;
//import org.springframework.batch.item.file.FlatFileItemReader;
//import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
//import org.springframework.batch.item.file.transform.Range;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.transaction.PlatformTransactionManager;
//
//import java.beans.PropertyEditor;
//import java.beans.PropertyEditorSupport;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Map;
//
//@Slf4j
//@Configuration
//@RequiredArgsConstructor
//public class SystemFailureJobConfig {
//
//    private final JobRepository jobRepository;
//
//    private final PlatformTransactionManager transactionManager;
//
//    @Bean
//    public Job systemFailureJob(Step systemFailureStep){
//        return new JobBuilder("systemFailureJob", jobRepository)
//                .start(systemFailureStep)
//                .build();
//    }
//
//    @Bean
//    public Step systemFailureStep(
//            FlatFileItemReader<SystemFailure> systemFailureItemReader,
//            SystemFailureStdoutItemWriter systemFailureStdoutItemWriter
//    ){
//        return new StepBuilder("systemFailureStep", jobRepository)
//                .<SystemFailure, SystemFailure>chunk(10, transactionManager)
//                .reader(systemFailureItemReader)
//                .writer(systemFailureStdoutItemWriter)
//                .build();
//    }
//
//
////    @Bean
////    @StepScope
////    public FlatFileItemReader<SystemFailure> systemFailureItemRead(
////            @Value("#{jobParameters['inputFile']}") String inputFile
////    ){
////
////        return new FlatFileItemReaderBuilder<SystemFailure>()
////                .name("systemFailureItemReader")
////                .resource(new FileSystemResource(inputFile))
////                .delimited()
////                .delimiter(",")
////                .names("errorId", "errorDateTime", "severity", "processId", "errorMessage")
////                .targetType(SystemFailure.class)
////                .linesToSkip(1)
////                .build();
////
////    }
//
//    @Bean
//    @StepScope
//    public FlatFileItemReader<SystemFailure> systemFailureItemReader(
//            @Value("#{jobParameters['inputFile']}") String inputFile
//    ) {
//        return new FlatFileItemReaderBuilder<SystemFailure>()
//                .name("systemFailureItemReader")
//                .resource(new FileSystemResource(inputFile))
//                .fixedLength()
//                .columns(new Range[]{
//                        new Range(1, 8),
//                        new Range(9, 29),
//                        new Range(30, 39),
//                        new Range(40, 45),
//                        new Range(46, 66)
//                })
//                .names("errorId", "errorDateTime", "severity", "processId", "errorMessage")
//                .targetType(SystemFailure.class)
//                .customEditors(Map.of(LocalDateTime.class,dateTimeEditor()))
//                .build();
//    }
//
//    private PropertyEditor dateTimeEditor(){
//        return new PropertyEditorSupport() {
//
//            @Override
//            public void setAsText(String text) {
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-HH-dd HH:mm:ss");
//                setValue(LocalDateTime.parse(text, formatter));
//            }
//        };
//    }
//
//
//    @Bean
//    public SystemFailureStdoutItemWriter systemFailureStdoutItemWriter(){
//        return new SystemFailureStdoutItemWriter();
//    }
//
//    public static class SystemFailureStdoutItemWriter implements ItemWriter<SystemFailure>{
//        @Override
//        public void write(Chunk<? extends SystemFailure> chunk) throws Exception {
//            for(SystemFailure failure: chunk){
//                log.info("Processing System Failure: {}",failure);
//            }
//        }
//    }
//
//    @Data
//    public static class SystemFailure{
//        private String errorId;
//        private String errorDateTime;
//        private String severity;
//        private Integer processId;
//        private String errorMessage;
//    }
//
//
//}
