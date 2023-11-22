package cv.api.common;

import lombok.Data;

import java.util.List;

@Data
public class ReportFilter {
    private List<String> listTrader;
    private List<String> listSaleMan;
    private String curCode;
    private String opDate;
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
    private String locCode;
    private String stockCode;
    private String brandCode;
    private String regCode;
    private String catCode;
    private String stockTypeCode;
    private String traderCode;
    private String saleManCode;
    private String vouTypeCode;
    private String batchNo;
    private boolean calSale;
    private boolean calPur;
    private boolean calRI;
    private boolean calRO;
    private boolean calMill;
    private Integer macId;
    private String compCode;
    private Integer deptId;
    private String status;
    private String projectNo;
    private boolean orderFavorite;
    private float creditAmt;

    private String fromDueDate;
    private String toDueDate;
    private boolean deleted;
    private boolean active;
    private String labourGroupCode;

    public ReportFilter(Integer macId, String compCode) {
        this.macId = macId;
        this.compCode = compCode;
    }


}
