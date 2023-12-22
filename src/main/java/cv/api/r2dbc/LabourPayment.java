package cv.api.r2dbc;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
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


}
