package cv.api.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
public class ProcessHis {
    private ProcessHisKey key;
    private Integer deptId;
    private String stockCode;
    private LocalDateTime vouDate;
    private String locCode;
    private LocalDateTime endDate;
    private String ptCode;
    private String remark;
    private String processNo;
    private Double qty;
    private String unit;
    private Double price;
    private Boolean finished;
    private Boolean deleted;
    private String createdBy;
    private String updatedBy;
    private Integer macId;
    private List<ProcessHisDetail> listDetail;
    private String stockUsrCode;
    private String stockName;
    private String ptName;
    private String locName;
    private Double avgQty;
    private Double avgPrice;
    private ZonedDateTime vouDateTime;

}
