package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class Pattern {
    private PatternKey key;
    private Integer deptId;
    private Double qty;
    private Double price;
    private String unitCode;
    private String locCode;
    private String priceTypeCode;
    private Boolean explode;
    private String intgUpdStatus;
    private LocalDateTime updatedDate;
    private String priceTypeName;
    private String userCode;
    private String stockName;
    private String groupName;
    private String brandName;
    private String catName;
    private String relName;
    private String locName;
    private Double amount;

}
