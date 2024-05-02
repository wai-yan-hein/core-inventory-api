package cv.api.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class Job {
    private JobKey key;
    private String jobName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean finished;
    private Boolean deleted;
    private LocalDateTime updatedDate;
    private String updatedBy;
    private LocalDateTime createdDate;
    private String createdBy;
    private Integer deptId;
    private Double outputCost;
    private Double outputQty;
}
