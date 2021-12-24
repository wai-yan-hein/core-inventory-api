package com.cv.inv.api.view;

import lombok.Data;

import java.io.Serializable;

@Data
public class VPurchase implements Serializable {
    private String pdCode;
    private Float balance;
    private String createdBy;
    private String createdDate;
    private boolean deleted;
    private Float discount;
    private String dueDate;
    private Float purExpTotal;
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
    private String stockName;
}
