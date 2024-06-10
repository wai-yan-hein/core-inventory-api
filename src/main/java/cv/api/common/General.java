package cv.api.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class General {
    private Double amount;
    private Double qty;
    private Double smallQty;
    private String stockCode;
    private String stockName;
    private String sysCode;
    private String stockTypeName;
    private String brandName;
    private String categoryName;
    private String traderCode;
    private String traderName;
    private Double totalQty;
    private String saleManName;
    private String saleManCode;
    private String qtyRel;
    private String message;
    private String address;
    private String relation;
    private Double bag;
}

