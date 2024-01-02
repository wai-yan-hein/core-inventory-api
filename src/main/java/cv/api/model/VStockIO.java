package cv.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VStockIO {
    private String vouNo;
    private String vouDate;
    private String remark;
    private String description;
    private String vouTypeUserCode;
    private String vouTypeName;
    private String stockUsrCode;
    private String stockName;
    private String locName;
    private String curCode;
    private Double inQty;
    private String inUnit;
    private Double outQty;
    private String outUnit;
    private Double costPrice;
    private Double inAmt;
    private Double outAmt;
    private String createdBy;
    private boolean deleted;
    private String unit;
    private Double price;
    private Double qty;
    private String stockCode;
    private String processNo;
    private Integer deptId;
    private String relName;
    private Double smallPrice;
    private ZonedDateTime vouDateTime;
    private String jobName;
    private String labourGroupName;
    private String traderName;
    private String receivedName;
    private String receivedPhone;
    private String carNo;
    private String phoneNo;
    private String regionName;
    private String inUnitName;
    private String outUnitName;
    private String weightUnitName;
    private Double weight;
    private Double inBag;
    private Double outBag;
}
