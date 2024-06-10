package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ReorderLevel {
    private ReorderKey key;
    private Integer deptId;
    private Double minQty;
    private String minUnitCode;
    private Double maxQty;
    private String maxUnitCode;
    private Double orderQty;
    private String orderUnit;
    private Double minSmallQty;
    private Double maxSmallQty;
    private Double balSmallQty;
    private String balUnit;
    private String userCode;
    private String stockName;
    private String groupName;
    private String brandName;
    private String catName;
    private String relName;
    private String locName;
    private Integer position;

}
