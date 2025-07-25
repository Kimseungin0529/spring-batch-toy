package project.springbatchtoy.batch.domain;

import lombok.*;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVO {
    private Long id;
    private String name;
    private int price;
    private String type;


}
