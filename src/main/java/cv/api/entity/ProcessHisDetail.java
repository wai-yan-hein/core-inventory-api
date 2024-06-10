package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ProcessHisDetail {
    private ProcessHisDetailKey key;
    private String stockCode;
    private String locCode;
    private Integer deptId;
    private LocalDateTime vouDate;
    private Double qty;
    private String unit;
    private Double price;
    private String locName;
    private String stockName;
    private String stockUsrCode;
}
