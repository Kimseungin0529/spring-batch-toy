package project.springbatchtoy.batch.job.api;


import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import project.springbatchtoy.batch.domain.ApiRequestVO;

public class ApiItemWriter3 implements ItemWriter<ApiRequestVO> {

    @Override
    public void write(Chunk<? extends ApiRequestVO> chunk) throws Exception {

    }
}


