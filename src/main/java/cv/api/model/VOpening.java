package cv.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class VOpening {
    private String vouNo;
    private String vouDate;
    private String remark;
    private String locationName;
    private String stockCode;
    private String stockUserCode;
    private String stockName;
    private String unit;
    private Float qty;
    private Float price;
    private Float amount;
    private String stockTypeName;
    private String createdBy;
    private boolean deleted;
    private Integer deptId;
}
