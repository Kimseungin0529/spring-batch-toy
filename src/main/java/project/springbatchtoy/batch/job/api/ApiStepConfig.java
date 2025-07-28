package project.springbatchtoy.batch.job.api;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.support.ClassifierCompositeItemProcessor;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import project.springbatchtoy.batch.chunk.processor.ApiItemProcessor1;
import project.springbatchtoy.batch.chunk.processor.ApiItemProcessor2;
import project.springbatchtoy.batch.chunk.processor.ApiItemProcessor3;
import project.springbatchtoy.batch.chunk.writer.ApiItemWriter1;
import project.springbatchtoy.batch.chunk.writer.ApiItemWriter2;
import project.springbatchtoy.batch.classfier.ProcessorClassifier;
import project.springbatchtoy.batch.classfier.WriterClassifier;
import project.springbatchtoy.batch.domain.ApiRequestVO;
import project.springbatchtoy.batch.domain.ProductVO;
import project.springbatchtoy.batch.partition.ProductPartitioner;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ApiStepConfig {

    private final DataSource dataSource;
    private final static int CHUNK_SIZE = 10;


    @Bean
    public Step apiMasterStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
        return new StepBuilder("apiMasterStep", jobRepository)
                .partitioner(apiSlaveStep(jobRepository, transactionManager).getName(), partitioner())
                .step(apiSlaveStep(jobRepository, transactionManager))
                .gridSize(3)
                .taskExecutor(taskExecutor())
                .build();

    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(3);
        taskExecutor.setMaxPoolSize(6);
        taskExecutor.setThreadNamePrefix("api-thread-");
        return taskExecutor;

    }

    @Bean
    public Step apiSlaveStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
        return new StepBuilder("apiSlaveStep", jobRepository)
                .<ProductVO, ProductVO>chunk(CHUNK_SIZE, transactionManager)

                .reader(itemReader(null))
                .processor(processor())
                .writer(ItemWriter())
                .build();
    }

    @Bean
    public ItemWriter ItemWriter() {
        ClassifierCompositeItemWriter<ApiRequestVO> itemWriter
                = new ClassifierCompositeItemWriter<>();

        Map<String, ItemWriter<ApiRequestVO>> writerMap = new HashMap<>();
        writerMap.put("1", new ApiItemWriter1());
        writerMap.put("2", new ApiItemWriter2());
        writerMap.put("3", new ApiItemWriter3());

        WriterClassifier<ApiRequestVO, ItemWriter<? super ApiRequestVO>> classifier = new WriterClassifier<>();
        classifier.setWriterMap(writerMap);

        itemWriter.setClassifier(classifier);

        return itemWriter;
    }

    @Bean
    public ItemProcessor processor() {
        ClassifierCompositeItemProcessor<ProductVO, ApiRequestVO> itemProcessor
                = new ClassifierCompositeItemProcessor<>();
        Map<String, ItemProcessor<ProductVO, ApiRequestVO>> processorMap = new HashMap<>();
        processorMap.put("1", new ApiItemProcessor1());
        processorMap.put("2", new ApiItemProcessor2());
        processorMap.put("3", new ApiItemProcessor3());

        ProcessorClassifier<ProductVO, ItemProcessor<?, ? extends ApiRequestVO>> classifier = new ProcessorClassifier<>();
        classifier.setProcessorMap(processorMap);

        itemProcessor.setClassifier(classifier);


        return itemProcessor;
    }


    @Bean
    public ProductPartitioner partitioner() {
        return new ProductPartitioner(dataSource);
    }

    // TODO : ItemReader, processor, writer, partitioner 구체 구현 이해 불가 -> 학습 필요

    /**
     * ItemReader 를 사용하면서 jdbc 를 통해 사용하곤 한다. jpa, jdbc 그리고 복잡한 쿼리 혹은 직접 접근을 위해
     * rowMapper 또는 sql 을 사용하는데 각 기술에 대한 경험 부족으로 무엇을 사용하는 게 나은지 모르겠다.
     * -> rowMapper 는 sql 로 조회한 데이터를 객체로 변환시키는 역할
     */
    @Bean
    @StepScope
    public ItemReader<ProductVO> itemReader(@Value("#{stepExecutionContext['product']}") ProductVO productVO) throws Exception {

        // Jdbc 페이징 배치 처리 설정, builder 형식도 가능하다.
        JdbcPagingItemReader<ProductVO> reader = new JdbcPagingItemReader<>();
        // dataSource 설정
        reader.setDataSource(dataSource);
        reader.setPageSize(CHUNK_SIZE);
        // 조회한 값을 I 객체로 변환
        reader.setRowMapper(new BeanPropertyRowMapper<>(ProductVO.class));
        // db url 을 보고 맞는 DB 구현체 적용
        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        // 가져올 쿼리문 설정
        queryProvider.setSelectClause("id, name, price, type");
        queryProvider.setFromClause("from product");
        queryProvider.setWhereClause("where type = :type");
        // order by 쿼리문 지정, 정렬 기준 설정 가능
        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("id", Order.DESCENDING);
        queryProvider.setSortKeys(sortKeys);

        reader.setParameterValues(QueryGenerator.getParameterForQuery("type", productVO.getType()));
        //QueryProvider 설정
        reader.setQueryProvider(queryProvider);
        reader.afterPropertiesSet(); // TODO : 무슨 옵션인지 확인하기, partition 으로 추정

        return reader;
    }
}
