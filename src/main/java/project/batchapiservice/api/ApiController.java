package project.batchapiservice.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ApiController {

    @PostMapping("/api/product/1")
    public String product1(@RequestBody ApiInfo request){
        List<ProductVO> productVOList = request.getApiRequestList()
                .stream().map(ApiRequestVO::getProductVO)
                .toList();
        System.out.println("productVOList = " + productVOList);

        return "product1 was successful";
    }

    @PostMapping("/api/product/2")
    public String product2(@RequestBody ApiInfo request){
        List<ProductVO> productVOList = request.getApiRequestList()
                .stream().map(ApiRequestVO::getProductVO)
                .toList();
        System.out.println("productVOList = " + productVOList);

        return "product2 was successful";
    }

    @PostMapping("/api/product/3")
    public String product3(@RequestBody ApiInfo request){
        List<ProductVO> productVOList = request.getApiRequestList()
                .stream().map(ApiRequestVO::getProductVO)
                .toList();
        System.out.println("productVOList = " + productVOList);

        return "product3 was successful";
    }
}
