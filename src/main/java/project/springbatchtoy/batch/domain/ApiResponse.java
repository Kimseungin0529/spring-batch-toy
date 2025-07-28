package project.springbatchtoy.batch.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse {
    private String status;
    private String msg;
}
