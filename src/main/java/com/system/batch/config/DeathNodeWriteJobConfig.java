package com.system.batch.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemWriterBuilder;
import org.springframework.batch.item.file.transform.RecordFieldExtractor;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DeathNodeWriteJobConfig {

    @Bean
    public Job deathNoteWriteJob(
            JobRepository jobRepository,
            Step deathNoteWriteStep
    ){
        return new JobBuilder("deathNoteJob", jobRepository)
                .start(deathNoteWriteStep)
                .build();
    }

    @Bean
    public Step deathNoteWriteStep(
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager,
            ListItemReader<DeathNote> listItemReader,
            JsonFileItemWriter<DeathNote> jsonFileItemWriter
    ){
        return new StepBuilder("deathNoteWriteStep", jobRepository)
                .<DeathNote, DeathNote>chunk(10, platformTransactionManager)
                .reader(listItemReader)
                .writer(jsonFileItemWriter)
                .build();
    }

    @Bean
    public ListItemReader<DeathNote> deathNoteListItemReader(){
        List<DeathNote> deathNotes = new ArrayList<>();
        for(int i=0 ;i<15; i++){
            String id = String.format("KILL-%03d", i);
            LocalDateTime date = LocalDateTime.now().plusDays(i);
            deathNotes.add(new DeathNote(
                    id,
                    "피해자: " + i,
                    date.format(DateTimeFormatter.ISO_DATE),
                    "처형사유" + i
            ));
        }

        return new ListItemReader<>(deathNotes);
    }

//    @Bean
//    @StepScope
//    public MultiResourceItemWriter<DeathNote> multiResourceItemWriter(
//            @Value("#{jobParameters['outputDir']}") String outputDir
//    ) {
//        return new MultiResourceItemWriterBuilder<DeathNote>()
//                .name("multiDeathNoteWriter")
//                .resource(new FileSystemResource(outputDir + "/death_note"))
//                .itemCountLimitPerResource(10)
//                .delegate(deathNoteJsonWriter(null))
//                .resourceSuffixCreator(index -> String.format("_%03d.text", index))
//                .build();
//
//
//    }


//    @Bean
//    public FlatFileItemWriter<DeathNote> deathNoteWrite() {
//        return new FlatFileItemWriterBuilder<DeathNote>()
//                .name("deathNoteWriter")
//                .formatted()
//                .format("처형 ID: %s | 처형일자: %s | 피해자: %s | 사인: %s")
//                .sourceType(DeathNote.class)
//                .names("victimId","executionDate","victimName","causeOfDeath")
//                .headerCallback(writer -> writer.write("=========처형 기록부========="))
//                .footerCallback(writer -> writer.write("=========처형 완료========="))
//                .build();
//
//    }

    @Bean
    @StepScope
    public JsonFileItemWriter<DeathNote> deathNoteJsonWriter(
            @Value("#{jobParameters['outputDir']}") String outputDir
    ){
        System.out.print(outputDir + "/death_notes.json");
        return new JsonFileItemWriterBuilder<DeathNote>()
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .resource(new FileSystemResource(outputDir + "/death_notes.json"))
                .name("logEntryJsonWriter")
                .build();
    }

    public RecordFieldExtractor<DeathNote> fieldExtractor(){
        RecordFieldExtractor<DeathNote> fieldExtractor = new RecordFieldExtractor<>(DeathNote.class);
        fieldExtractor.setNames("victimId","executionDate","causeOfDeath");
        return fieldExtractor;
    }

    public record DeathNote (
         String victimId,
         String victimName,
         String executionDate,
         String causeOfDeath
    ) {}
}
