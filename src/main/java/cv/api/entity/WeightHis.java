package cv.api.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
public class WeightHis {
    private WeightHisKey key;
    private LocalDateTime vouDate;
    private String description;
    private Integer deptId;
    private String traderCode;
    private String stockCode;
    private Double weight;
    private Double totalQty;
    private Double totalWeight;
    private Double totalBag;
    private String remark;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Boolean deleted;
    private Integer macId;
    private String tranSource;
    private Boolean draft;
    private Boolean post;
    private List<WeightHisDetail> listDetail;
    private ZonedDateTime vouDateTime;
    private String stockUserCode;
    private String stockName;
    private String traderUserCode;
    private String traderName;
}
