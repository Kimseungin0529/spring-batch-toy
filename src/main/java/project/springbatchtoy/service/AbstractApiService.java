package project.springbatchtoy.service;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import project.springbatchtoy.batch.domain.ApiInfo;
import project.springbatchtoy.batch.domain.ApiRequestVO;
import project.springbatchtoy.batch.domain.ApiResponse;

import java.io.IOException;
import java.net.URI;
import java.util.List;

public abstract class AbstractApiService {
    public ApiResponse service(List<? extends ApiRequestVO> apiRequest) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        RestTemplate restTemplate = restTemplateBuilder.errorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }

            @Override
            public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
                ResponseErrorHandler.super.handleError(url, method, response);
            }
        }).build();

        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        ApiInfo apiInfo = ApiInfo.builder()
                .apiRequestList(apiRequest)
                .build();

        return doApiService(restTemplate, apiInfo);

    }

    protected abstract ApiResponse doApiService(RestTemplate restTemplate, ApiInfo apiInfo);
}
