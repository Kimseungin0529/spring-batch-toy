package project.springbatchtoy.service;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import project.springbatchtoy.batch.domain.ApiInfo;
import project.springbatchtoy.batch.domain.ApiResponse;

@Service
public class ApiService3 extends AbstractApiService {

    @Override
    protected ApiResponse doApiService(RestTemplate restTemplate, ApiInfo apiInfo) {
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8083/product/3", apiInfo, String.class);
        HttpStatusCode statusCode = response.getStatusCode();

        return ApiResponse.builder()
                .status(statusCode.value())
                .msg(response.getBody())
                .build();
    }
}
