package cv.api.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
public class LabourOutput {
    private String vouNo;
    private String compCode;
    private Integer deptId;
    private LocalDateTime vouDate;
    private String remark;
    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime updatedDate;
    private String updatedBy;
    private Integer macId;
    private Boolean deleted;
    private Double outputQty;
    private Double rejectQty;
    private Double amount;
    private ZonedDateTime vouDateTime;
    private List<LabourOutputDetail> listDetail;
}
