package cv.api.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
public class PaymentHis {
    private String vouNo;
    private String compCode;
    private Integer deptId;
    private LocalDateTime vouDate;
    private String traderCode;
    private String remark;
    private Boolean deleted;
    private Double amount;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String createdBy;
    private String updatedBy;
    private Integer macId;
    private String account;
    private String debtorAcc;
    private String projectNo;
    private String curCode;
    private String intgUpdStatus;
    private String tranOption;
    private String deptCode;
    private List<PaymentHisDetail> listDetail;
    private String traderName;
    private ZonedDateTime vouDateTime;
}
