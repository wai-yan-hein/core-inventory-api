package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LabourOutputDetail {
    private String vouNo;
    private String compCode;
    private Integer uniqueId;
    private String jobNo;
    private String labourCode;
    private String description;
    private String orderVouNo;
    private String refNo;
    private String remark;
    private String vouStatusCode;
    private Double outputQty;
    private Double rejectQty;
    private Double price;
    private Double amount;
}
