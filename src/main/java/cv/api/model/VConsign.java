package cv.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class VConsign {
    private String vouNo;
    private String vouDate;
    private String createdBy;
    private LocalDateTime createdDate;
    private Boolean deleted;
    private String location;
    private String remark;
    private String description;
    private String updatedBy;
    private LocalDateTime updatedDate;
    private Integer macId;
    private Integer deptId;
    private String compCode;
    private String traderName;
    private String labourGroupName;
    private String stockCode;
    private String stockName;
    private Integer uniqueId;
    private Double wet;
    private Double bag;
    private Double qty;
    private Double weight;
    private Double price;
    private Double amount;
    private ZonedDateTime vouDateTime;
}
