package cv.api.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
public class GRN {
    private GRNKey key;
    private Integer deptId;
    private String batchNo;
    private LocalDateTime vouDate;
    private String traderCode;
    private Boolean closed;
    private Boolean deleted;
    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime updatedDate;
    private String updatedBy;
    private Integer macId;
    private String remark;
    private String locCode;
    private List<GRNDetail> listDetail;
    private List<GRNDetailKey> listDel;
    private String traderName;
    private String traderUserCode;
    private ZonedDateTime vouDateTime;

}
