package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GRNDetail {
    private GRNDetailKey key;
    private Integer deptId;
    private String stockCode;
    private Double qty;
    private String unit;
    private String locCode;
    private Double weight;
    private String weightUnit;
    private Double totalWeight;
    private String userCode;
    private String stockName;
    private String relName;
    private String locName;
    private Double stdWeight;
}
