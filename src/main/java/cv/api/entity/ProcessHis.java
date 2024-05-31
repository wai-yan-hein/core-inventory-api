package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
public class  ProcessHis {
    private ProcessHisKey key;
    private LocalDateTime vouDate;
    private LocalDateTime endDate;
    private String ptCode;
    private String remark;
    private String processNo;
    private Double qty;
    private Double avgQty;
    private String unit;
    private Double price;
    private Double avgPrice;
    private boolean finished;
    private boolean deleted;
    private String createdBy;
    private String updatedBy;
    private Integer macId;
    @Transient
    private List<ProcessHisDetail> listDetail;
    @Transient
    private String stockUsrCode;
    @Transient
    private String stockName;
    @Transient
    private String ptName;
    @Transient
    private String locName;
    @Transient
    private ZonedDateTime vouDateTime;

}
