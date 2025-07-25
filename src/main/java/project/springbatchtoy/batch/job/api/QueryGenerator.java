package project.springbatchtoy.batch.job.api;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import project.springbatchtoy.batch.domain.ProductVO;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryGenerator {

    public static ProductVO[] getProductList(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<ProductVO> productVOList = jdbcTemplate.query("select type from product group by type", new RowMapper<ProductVO>() {
            @Override
            public ProductVO mapRow(ResultSet rs, int rowNum) throws SQLException {
                return ProductVO.builder()
                        .type(rs.getString("type"))
                        .build();
            }
        });
        return productVOList.toArray(new ProductVO[]{});

    }

    public static Map<String, Object> getParameterForQuery(String parameter, String value) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(parameter, value);

        return parameters;
    }
}
