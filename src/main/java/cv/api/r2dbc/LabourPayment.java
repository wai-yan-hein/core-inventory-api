package cv.api.r2dbc;

import cv.api.common.Util1;
import cv.api.dto.LabourPaymentDto;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@Slf4j
public class LabourPayment {
    @Id
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
    private boolean deleted;
    private int macId;
    private Integer memberCount;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String sourceAcc;
    private String expenseAcc;
    private double payTotal;
    private String deptCode;
    private boolean post;


    public LabourPaymentDto buildDto() {
        return LabourPaymentDto.builder()
                .vouNo(getVouNo())
                .compCode(getCompCode())
                .deptId(getDeptId())
                .vouDate(getVouDate())
                .vouDateTime(Util1.toZonedDateTime(getVouDate()))
                .labourGroupCode(getLabourGroupCode())
                .curCode(getCurCode())
                .remark(getRemark())
                .createdBy(getCreatedBy())
                .updatedBy(getUpdatedBy())
                .deleted(isDeleted())
                .macId(getMacId())
                .fromDate(getFromDate())
                .toDate(getToDate())
                .sourceAcc(getSourceAcc())
                .expenseAcc(getExpenseAcc())
                .payTotal(getPayTotal())
                .deptCode(getDeptCode())
                .post(isPost())
                .build();
    }

}
