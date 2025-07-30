package project.springbatchtoy.batch.rowmapper;


import org.springframework.jdbc.core.RowMapper;
import project.springbatchtoy.batch.domain.ProductVO;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductRowMapper implements RowMapper<ProductVO> {
    @Override
    public ProductVO mapRow(ResultSet rs, int i) throws SQLException {
        return ProductVO.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .price(rs.getInt("price"))
                .type(rs.getString("type"))
                .build();
    }
}