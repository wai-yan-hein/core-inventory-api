package cv.api.common;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
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
    private String warehouseCode;
    private int reportType;
    private boolean summary;
    private String remark;
    private String userCode;
    private String tranSource;
    private String description;
    private String vouStatus;
    private String jobNo;
    private String tranOption;
    private String saleVouNo;
    private String account;
    private String orderNo;
    private String orderName;
    private String reference;
    private boolean nullBatch;
    private boolean finished;
    private boolean close;
    private boolean orderByBatch;
    private String processNo;
    private String orderStatus;
    private String refNo;
    private boolean draft;




}
