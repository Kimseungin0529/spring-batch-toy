package project.springbatchtoy.service;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import project.springbatchtoy.batch.domain.ApiInfo;
import project.springbatchtoy.batch.domain.ApiResponseVO;

@Service
public class ApiService2 extends AbstractApiService {

    @Override
    protected ApiResponseVO doApiService(RestTemplate restTemplate, ApiInfo apiInfo) {
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8082/api/product/2", apiInfo, String.class);
        HttpStatusCode statusCode = response.getStatusCode();

        return ApiResponseVO.builder()
                .status(statusCode.value())
                .msg(response.getBody())
                .build();
    }
}
