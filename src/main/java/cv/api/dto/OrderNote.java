package cv.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
public class OrderNote {
    private String vouNo;
    private String compCode;
    private Integer deptId;
    private Integer macId;
    private String traderCode;
    private String stockCode;
    private String orderName;
    private String orderCode;
    private LocalDateTime vouDate;
    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime updatedDate;
    private String updatedBy;
    private Boolean deleted;
    private ZonedDateTime vouDateTime;

    private String traderName;
    private String stockName;
    private List<OrderFileJoin> detailList;
}
