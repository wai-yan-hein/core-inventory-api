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
    private LocalDate refDate;
    private String stockUserCode;
    private String stockName;
    private String stockCode;
    private String refNo;
    private Double qty;
    private Double payQty;
    private Double balQty;
    private Double bag;
    private Double payBag;
    private Double balBag;
    private String remark;
    private String reference;
    private Boolean fullPaid;
    private String projectNo;
}
