package project.springbatchtoy.batch.job.api;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ApiJobChildConfig {

    /**
     * 실제 API 호출 및 처리를 위한 Job
     * API 호출은 멀티스레딩 환경에서 적용
     */

    private final Step apiMasterStep;
    private final JobLauncher jobLauncher; // master step 을 사용하기 위한 job 호출 도구

    @Bean
    public Step jobStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("jobStep", jobRepository)
                .job(childJob(jobRepository, transactionManager)) // ApiJob 에서 호출할 step 요소 구현
                .launcher(jobLauncher) // partition 에 사용할 jobLancher
                .build();
    }

    @Bean
    public Job childJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("childJob", jobRepository)
                .start(apiMasterStep) //apiMasterStep 를 통해 API 호출 처리
                .build();
    }

}
