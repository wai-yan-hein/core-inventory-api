package cv.api.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class StockValue {
    private String stockUserCode;
    private String stockCode;
    private String stockName;
    private String stockTypeUserCode;
    private String stockTypeName;
    private String catName;
    private String balRel;
    private Double purAvgPrice;
    private Double purAvgAmount;
    private Double inAvgPrice;
    private Double inAvgAmount;
    private Double stdPrice;
    private Double stdAmount;
    private Double recentPrice;
    private Double recentAmt;
    private Double fifoPrice;
    private Double fifoAmt;
    private Double lifoPrice;
    private Double lifoAmt;
    private Double ioRecentPrice;
    private Double ioRecentAmt;
    private Double qty;
    private String relation;
}
