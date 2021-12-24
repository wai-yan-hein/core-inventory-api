package com.cv.inv.api.common;

import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class ReportFilter {
    private List<String> listTrader;
    private List<String> listSaleMan;
    private String curCode;
    private String fromDate;
    private String toDate;
    private String vouNo;
    private List<String> listLocation;
    private List<String> listStock;
    private List<String> listBrand;
    private List<String> listRegion;
    private List<String> listCategory;
    private List<String> listStockType;
    private String reportName;
    @NonNull
    private Integer macId;
    @NonNull
    private String compCode;

    public ReportFilter(Integer macId, String compCode) {
        this.macId = macId;
        this.compCode = compCode;
    }


}
