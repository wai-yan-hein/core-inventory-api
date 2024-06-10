package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class OrderStatus {
    private OrderStatusKey key;
    private String description;
    private LocalDateTime updatedDate;
    private String updatedBy;
    private LocalDateTime createdDate;
    private String createdBy;
    private Integer macId;
    private String userCode;
    private String intgUpdStatus;
    private Integer deptId;
    private Boolean deleted;
    private Boolean active;
    private Integer orderBy;
}
