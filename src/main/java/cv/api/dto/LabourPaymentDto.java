package cv.api.dto;

import cv.api.r2dbc.LabourPayment;
import cv.api.r2dbc.LabourPaymentDetail;
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
    private int macId;
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

    public LabourPayment toEntity() {
        return LabourPayment.builder()
                .vouNo(getVouNo())
                .compCode(getCompCode())
                .deptId(getDeptId())
                .vouDate(getVouDate())
                .labourGroupCode(getLabourGroupCode())
                .curCode(getCurCode())
                .remark(getRemark())
                .createdDate(getCreatedDate())
                .updatedDate(getUpdatedDate())
                .createdBy(getCreatedBy())
                .updatedBy(getUpdatedBy())
                .deleted(getDeleted())
                .macId(getMacId())
                .memberCount(getMemberCount())
                .payTotal(getPayTotal())
                .fromDate(getFromDate())
                .toDate(getToDate())
                .sourceAcc(getSourceAcc())
                .expenseAcc(getExpenseAcc())
                .deptCode(getDeptCode())
                .post(getPost())
                .build();
    }


}
