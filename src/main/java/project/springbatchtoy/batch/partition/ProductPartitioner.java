package project.springbatchtoy.batch.partition;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import project.springbatchtoy.batch.domain.ProductVO;
import project.springbatchtoy.batch.job.api.QueryGenerator;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class ProductPartitioner implements Partitioner {

    private final DataSource dataSource;

    public ProductPartitioner(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        ProductVO[] productList = QueryGenerator.getProductList(dataSource);
        Map<String, ExecutionContext> map = new HashMap<>();

        for (int i = 0; i < productList.length; i++) {
            ExecutionContext executionContext = new ExecutionContext();

            executionContext.put("product", productList[i]);
            map.put("product" + i, executionContext);
        }

        return map;
    }


}
