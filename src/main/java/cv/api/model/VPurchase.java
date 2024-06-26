package cv.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import cv.api.entity.LandingHisPrice;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class VPurchase {
    private String pdCode;
    private Double balance;
    private String createdBy;
    private String createdDate;
    private Boolean deleted;
    private Double discount;
    private String dueDate;
    private Double total;
    private Double paid;
    private String vouDate;
    private ZonedDateTime vouDateTime;
    private String remark;
    private String sessionId;
    private String updatedBy;
    private String updatedDate;
    private Double vouTotal;
    private String curCode;
    private String traderCode;
    private Double discountPrice;
    private Double taxPrice;
    private Double taxAmount;
    private String intgUpdStatus;
    private Integer macId;
    private String compCode;
    private String vouNo;
    private String stockCode;
    private String expDate;
    private Double qty;
    private Double qtyPercent;
    private Double stdWt;
    private Double avgPrice;
    private String purUnit;
    private String purUnitName;
    private Double avgWt;
    private Double purPrice;
    private Double purAmount;
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
    private Double weight;
    private String weightUnit;
    private String weightUnitName;
    private Double totalQty;
    private Double totalWeight;
    private String batchNo;
    private String projectNo;
    private String address;
    private String labourGroupName;
    private String landVouNo;
    private String phoneNo;
    private String regionName;
    private String reference;
    private Double bag;
    private Double wet;
    private Double totalWet;
    private Double rice;
    private Double totalRice;
    private Double grandTotal;
    private Double avgWet;
    private Double avgRice;
    private List<LandingHisPrice> listPrice;
}
