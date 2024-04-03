package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class OPHis {
    private OPHisKey key;
    private Integer deptId;
    private LocalDate vouDate;
    private String remark;
    private String createdBy;
    private LocalDateTime createdDate;
    private String updatedBy;
    private String curCode;
    private String locCode;
    private Double opAmt;
    private LocalDateTime updatedDate;
    private Boolean deleted;
    private Integer macId;
    private String intgUpdStatus;
    private String traderCode;
    private Integer tranSource;
    private List<OPHisDetail> detailList;
    private String status;
    private String locName;
    private String vouDateStr;
    private Double qty;
    private Double bag;
    public static final int STOCK_OP = 1;
    public static final int PAYABLE = 2;
    public static final int PADDY = 3;
    public static final int CONSIGN = 4;



}
