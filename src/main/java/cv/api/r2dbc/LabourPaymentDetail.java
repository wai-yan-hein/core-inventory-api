package cv.api.r2dbc;

import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
public class LabourPaymentDetail {
    private String vouNo;
    private String compCode;
    private Integer uniqueId;
    private Integer deptId;
    private String tranOption;
    private String description;
    private Double qty;
    private Double price;
    private Double amount;
    private String account;
    private String deptCode;

}
