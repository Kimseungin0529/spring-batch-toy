package project.springbatchtoy.batch.job.api;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;
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


    @Bean
    @StepScope
    public ItemReader<ProductVO> itemReader(@Value("#{stepExecutionContext['product']}") ProductVO productVO) throws Exception {

        JdbcPagingItemReader<ProductVO> reader = new JdbcPagingItemReader<>();

        reader.setDataSource(dataSource);
        reader.setPageSize(CHUNK_SIZE);
        reader.setRowMapper(new BeanPropertyRowMapper(ProductVO.class));

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("id, name, price, type");
        queryProvider.setFromClause("from product");
        queryProvider.setWhereClause("where type = :type");

        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("id", Order.DESCENDING);
        queryProvider.setSortKeys(sortKeys);

        reader.setParameterValues(QueryGenerator.getParameterForQuery("type", productVO.getType()));
        reader.setQueryProvider(queryProvider);
        reader.afterPropertiesSet();

        return reader;
    }
}
