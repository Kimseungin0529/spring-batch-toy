package project.springbatchtoy.batch.job.api;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import project.springbatchtoy.batch.listener.ApiJobListener;
import project.springbatchtoy.batch.tasklet.ApiEndTasklet;
import project.springbatchtoy.batch.tasklet.ApiStartTasklet;

@Configuration
@RequiredArgsConstructor
public class ApiJobConfig {

    /**
     * API 호출 및 처리 과정을 설정하는 부모 Job
     *
     * 1. DB 에서 데이터 조회
     * 2. 조회한 데이터 기반으로 외부 API 호출 처리, 멀티스레딩 처리로 처리 속도 향상시키기
     * 3. 잡 리스너를 통해 전, 후 시간 비교하여 수행 시간 측정
     * 4. 멀티스레딩 처리를 위해 partition master/slave 사용
     */

    private final ApiStartTasklet apiStartTasklet;
    private final ApiEndTasklet apiEndTasklet;
    private final ApiJobListener apiJobListener;
    private final Step jobStep;

    @Bean
    public Job apiJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        // API 호출과 처리에 대한 전반적인 관리를 위한 Job
        return new JobBuilder("apiJob", jobRepository)
                .listener(apiJobListener)
                .start(apiStep1(jobRepository, transactionManager))
                .next(jobStep) // 실제 API 호출을 처리할 JobStep
                .next(apiStep2(jobRepository, transactionManager))
                .build();
    }


    /**
     * apiStep1, apiStep2
     * 특정 Job 수행 시간을 측정하기 위한 용도
     * JobListener 를 사용해도 되지만 임시로 Step 구현
     */
    @Bean
    public Step apiStep1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("apiStep1", jobRepository)
                .tasklet(apiStartTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step apiStep2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("apiStep2", jobRepository)
                .tasklet(apiEndTasklet, transactionManager)
                .build();
    }


}
