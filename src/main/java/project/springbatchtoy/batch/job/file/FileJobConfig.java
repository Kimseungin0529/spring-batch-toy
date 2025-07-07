package project.springbatchtoy.batch.job.file;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import project.springbatchtoy.batch.chunk.processor.FileItemProcessor;
import project.springbatchtoy.batch.domain.Product;
import project.springbatchtoy.batch.domain.ProductVO;

@Configuration
@RequiredArgsConstructor
public class FileJobConfig {

    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job fileJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("fileJob", jobRepository)
                .start(fileStep(jobRepository, transactionManager))
                .build();

    }

    @Bean
    public Step fileStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("fileStep", jobRepository)
                .<ProductVO, Product>chunk(10, transactionManager)
                .reader(fileItemReader(null)) // 런타임 요청 중 바인딩 처리 예정(프록시 사용), 따라서 null 처리. 대신 Scope 설정으로 동적 선언 필요
                .processor(fileItemProcessor())
                .writer(fileItemWriter())
                .build();
    }


    @Bean
    public ItemReader<ProductVO> fileItemReader(@Value("#{jobParameters['requestDate']}") String requestDate) { // 런타임 요청 시점에 파라미터 바인딩
        return new FlatFileItemReaderBuilder<ProductVO>() // 파일 타입
                .name("flatFile")
                .resource(new ClassPathResource("product_" + requestDate + ".csv")) // 리소스 경로 제공
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>())
                .targetType(ProductVO.class) // I 타입 선언
                .linesToSkip(1) // csv 이므로 첫 줄 스킵 처리
                .delimited().delimiter(",") // csv 특성 상, ',' 단위로 데이터 읽기
                .names("id", "name", "price", "type") // 각 데이터 변수로 변환하기
                .build();
    }


    @Bean
    public ItemProcessor<ProductVO, Product> fileItemProcessor() {
        return new FileItemProcessor();
    }


    @Bean
    public ItemWriter<Product> fileItemWriter() {
        return new JpaItemWriterBuilder<Product>()
                .entityManagerFactory(entityManagerFactory) // 엔티티 매니저를 만들기 위해 의존성 주입
                .usePersist(true) // 기본값(true) 처리
                .build();
    }


}
