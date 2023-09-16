package cv.api.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class General {
    private Float amount;
    private Float qty;
    private Float smallQty;
    private String stockCode;
    private String stockName;
    private String sysCode;
    private String stockTypeName;
    private String brandName;
    private String categoryName;
    private String traderCode;
    private String traderName;
    private Float totalQty;
    private String saleManName;
    private String saleManCode;
    private String qtyRel;
    private String message;
    private String address;

}

