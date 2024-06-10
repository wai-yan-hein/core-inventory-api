package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class WeightLossHisDetail {
    private WeightLossHisDetailKey key;
    private Integer deptId;
    private String stockCode;
    private String locCode;
    private Double qty;
    private String unit;
    private Double price;
    private Double lossQty;
    private String lossUnit;
    private Double lossPrice;
    private String stockUserCode;
    private String stockName;
    private String locName;
    private String relName;
}
