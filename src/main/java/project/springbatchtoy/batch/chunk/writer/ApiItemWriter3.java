package project.springbatchtoy.batch.chunk.writer;


import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import project.springbatchtoy.batch.domain.ApiRequestVO;
import project.springbatchtoy.batch.domain.ApiResponse;
import project.springbatchtoy.service.AbstractApiService;

public class ApiItemWriter3 implements ItemWriter<ApiRequestVO> {

    private AbstractApiService apiService;

    @Override
    public void write(Chunk<? extends ApiRequestVO> chunk) throws Exception {
        ApiResponse responseVO = apiService.service(chunk.getItems());
        System.out.println("responseVO = " + responseVO);
    }

    public ApiItemWriter3(AbstractApiService apiService) {
        this.apiService = apiService;
    }

}


