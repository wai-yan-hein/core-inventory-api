/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class VOrder {
    private String vouNo;
    private String traderCode;
    private String rfId;
    private String saleManCode;
    private String vouDate;
    private ZonedDateTime vouDateTime;
    private String creditTerm;
    private String curCode;
    private String remark;
    private Double vouTotal;
    private Float grandTotal;
    private Float discount;
    private Float discountPrice;
    private Float taxAmt;
    private Float taxPrice;
    private String createdDate;
    private String createdBy;
    private boolean deleted;
    private Double paid;
    private Double vouBalance;
    private String updatedBy;
    private String updatedDate;
    private String cusPhoneNo;
    private String cusAddress;
    private String orderCode;
    private String regionCode;
    private Integer macId;
    private Integer sessionId;
    private String stockUserCode;
    private String stockCode;
    private String expiredDate;
    private Float qty;
    private String saleUnit;
    private Float salePrice;
    private Float saleAmount;
    private String locCode;
    private Integer uniqueId;
    private String traderName;
    private String saleManName;
    private String stockName;
    private String stockTypeCode;
    private String brandCode;
    private String brandName;
    private String catCode;
    private String categoryName;
    private String locationName;
    private String compName;
    private String compPhone;
    private String compAddress;
    private String regionName;
    private String stockTypeName;
    private String refNo;
    private float lastBalance;
    private String compCode;
    private String relName;
    private String qtyStr;
    private Integer deptId;
    private String batchNo;
    private String reference;
    private String supplierName;
    private Float weight;
    private String weightUnit;
    private String phoneNo;
    private String address;
    private String projectNo;
    private Integer vouCount;
    private String orderStatus;
    private String orderStatusName;
    private Boolean post;
}
