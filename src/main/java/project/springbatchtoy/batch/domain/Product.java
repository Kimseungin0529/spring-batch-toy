package project.springbatchtoy.batch.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Product {
    @Id
    private Long id;
    private String name;
    private int price;
    private String type;
}
