package cv.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class VPurchase implements Serializable {
    private String pdCode;
    private Float balance;
    private String createdBy;
    private String createdDate;
    private boolean deleted;
    private Float discount;
    private String dueDate;
    private Float total;
    private Float paid;
    private String vouDate;
    private String remark;
    private String sessionId;
    private String updatedBy;
    private String updatedDate;
    private Float vouTotal;
    private String curCode;
    private String traderCode;
    private Float discountPrice;
    private Float taxPrice;
    private Float taxAmount;
    private String intgUpdStatus;
    private Integer macId;
    private String compCode;
    private String vouNo;
    private String stockCode;
    private String expDate;
    private Float qty;
    private Float stdWt;
    private Float avgPrice;
    private String purUnit;
    private Float avgWt;
    private Float purPrice;
    private Float purAmount;
    private String locationCode;
    private Integer uniqueId;
    private String createdName;
    private String updatedName;
    private String curName;
    private String traderName;
    private String locationName;
    private String compName;
    private String compPhone;
    private String compAddress;
    private String stockUserCode;
    private String stockName;
    private String relName;
    private String qtyStr;
    private Integer deptId;
    private String groupName;
    private Float weight;
    private String weightUnit;
    private Float totalQty;
    private String batchNo;
    private String projectNo;
}
