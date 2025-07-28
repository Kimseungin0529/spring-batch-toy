package project.springbatchtoy.batch.chunk.processor;

import org.springframework.batch.item.ItemProcessor;
import project.springbatchtoy.batch.domain.ApiRequestVO;
import project.springbatchtoy.batch.domain.ProductVO;

public class ApiItemProcessor2 implements ItemProcessor<ProductVO, ApiRequestVO> {
    @Override
    public ApiRequestVO process(ProductVO item) throws Exception {

        return ApiRequestVO.builder()
                .id(item.getId())
                .product(item)
                .build();
    }
}
