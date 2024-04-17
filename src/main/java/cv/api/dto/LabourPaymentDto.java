package cv.api.dto;

import cv.api.entity.LabourPaymentDetail;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
public class LabourPaymentDto {
    private String vouNo;
    private String compCode;
    private Integer deptId;
    private LocalDateTime vouDate;
    private String labourGroupCode;
    private String curCode;
    private String remark;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String createdBy;
    private String updatedBy;
    private Boolean deleted;
    private Integer macId;
    private Integer memberCount;
    private ZonedDateTime vouDateTime;
    private Double payTotal;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String sourceAcc;
    private String expenseAcc;
    private String labourName;
    private String deptCode;
    private Boolean post;
    private List<LabourPaymentDetail> listDetail;

}
