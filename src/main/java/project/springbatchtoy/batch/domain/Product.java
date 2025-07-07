package project.springbatchtoy.batch.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    @Id
    private Long id;
    private String name;
    private int price;
    private String type;

    public static Product toEntity(ProductVO productVO) {
        return Product.builder()
                .id(productVO.getId())
                .name(productVO.getName())
                .price(productVO.getPrice())
                .type(productVO.getType())
                .build();
    }
}
