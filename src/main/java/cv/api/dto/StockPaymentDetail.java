package cv.api.dto;

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
    private LocalDate refDate;
    private String stockCode;
    private String refNo;
    private Double qty;
    private String remark;
    private String reference;
}
