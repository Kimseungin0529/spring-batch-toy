package project.springbatchtoy.batch.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVO {
    private Long id;
    private String name;
    private int price;
    private String type;


}
