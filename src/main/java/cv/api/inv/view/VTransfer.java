package cv.api.inv.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VTransfer {
    private String vouNo;
    private String vouDate;
    private String remark;
    private String fromLocationName;
    private String toLocationName;
    private String refNo;
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
