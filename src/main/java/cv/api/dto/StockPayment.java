package cv.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
public class StockPayment {
    private String vouNo;
    private String compCode;
    private Integer deptId;
    private LocalDateTime vouDate;
    private String traderCode;
    private String locCode;
    private String traderName;
    private String remark;
    private Boolean deleted;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String createdBy;
    private String updatedBy;
    private Integer macId;
    private String tranOption;
    private List<StockPaymentDetail> listDetail;
    private ZonedDateTime vouDateTime;
    private String projectNo;
    private Boolean calculate;
    private Double payQty;
}
