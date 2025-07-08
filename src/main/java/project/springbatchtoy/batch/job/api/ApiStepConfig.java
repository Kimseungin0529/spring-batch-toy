package project.springbatchtoy.batch.job.api;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import project.springbatchtoy.batch.domain.ProductVO;
import project.springbatchtoy.batch.partition.ProductPartitioner;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class ApiStepConfig {

    private final DataSource dataSource;
    private final static int CHUNK_SIZE = 10;


    @Bean
    public Step apiMasterStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("apiMasterStep", jobRepository)
                .partitioner(apiSlaveStep(jobRepository, transactionManager).getName(), partitioner())
                .step(CHUNK_SIZE)
                .gridSize(3)
                .taskExecutor(taskExceutor())
                .build();

    }

    @Bean
    public Step apiSlaveStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("apiSlaveStep", jobRepository)
                .<ProductVO, ProductVO>chunk(CHUNK_SIZE)

                .reader(ItemReader())
                .processor(processor())
                .writer(ItemWriter())
                .build();
    }


    @Bean
    public ProductPartitioner partitioner() {
        return new ProductPartitioner(dataSource);
    }
}
