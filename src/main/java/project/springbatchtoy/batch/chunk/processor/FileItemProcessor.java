package project.springbatchtoy.batch.chunk.processor;

import org.springframework.batch.item.ItemProcessor;
import project.springbatchtoy.batch.domain.Product;
import project.springbatchtoy.batch.domain.ProductVO;

public class FileItemProcessor implements ItemProcessor<ProductVO, Product> {

    @Override
    public Product process(ProductVO item) throws Exception {

        return Product.toEntity(item);
    }
}
