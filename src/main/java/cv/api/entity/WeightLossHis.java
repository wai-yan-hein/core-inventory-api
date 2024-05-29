package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class WeightLossHis {
    private WeightLossHisKey key;
    private Integer deptId;
    private LocalDateTime vouDate;
    private String refNo;
    private String remark;
    private String createdBy;
    private String updatedBy;
    private Integer macId;
    private LocalDateTime updatedDate;
    private Boolean deleted;
    private List<WeightLossHisDetail> listDetail;
    private List<WeightLossHisDetailKey> delKeys;
    private ZonedDateTime vouDateTime;
}
