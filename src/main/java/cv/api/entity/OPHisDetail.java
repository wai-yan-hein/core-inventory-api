package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class OPHisDetail {
    private OPHisDetailKey key;
    private Integer deptId;
    private String stockCode;
    private Double qty;
    private Double price;
    private Double amount;
    private String locCode;
    private String unitCode;
    private Double weight;
    private String weightUnit;
    private Double totalWeight;
    private Double wet;
    private Double rice;
    private Double bag;
    private String userCode;
    private String stockName;
    private String groupName;
    private String brandName;
    private String catName;
    private String relName;
    private String relCode;
}
