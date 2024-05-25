package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class LabourGroup {
    private LabourGroupKey key;
    private String labourName;
    private LocalDateTime updatedDate;
    private String updatedBy;
    private LocalDateTime createdDate;
    private String createdBy;
    private String userCode;
    private Boolean active;
    private Boolean deleted;
    private Integer memberCount;
    private Double qty;
    private Double price;
}
