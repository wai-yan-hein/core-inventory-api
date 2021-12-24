package com.cv.inv.api.common;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClosingBalance {
    private String compName;
    private String compAddress;
    private String compPhone;
    private String typeUserCode;
    private String typeName;
    private String stockUsrCode;
    private String stockCode;
    private String stockName;
    private float openQty;
    private float openAmt;
    private float purQty;
    private float purAmt;
    private float inQty;
    private float inAmt;
    private float outQty;
    private float outAmt;
    private float saleQty;
    private float saleAmt;
    private float balQty;
    private float avgCost;
}
