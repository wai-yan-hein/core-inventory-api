package cv.api.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class StockPaymentDetail {
    private String vouNo;
    private String compCode;
    private Integer uniqueId;
    private Integer deptId;
    private LocalDate saleVouDate;
    private String saleVouNo;
    private Double payQty;
    private String remark;
    private String reference;
    private boolean fullPaid;
}
