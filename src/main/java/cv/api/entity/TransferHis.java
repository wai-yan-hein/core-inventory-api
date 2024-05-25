package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class TransferHis {
    private TransferHisKey key;
    private Integer deptId;
    private String createdBy;
    private LocalDateTime createdDate;
    private Boolean deleted;
    private LocalDateTime vouDate;
    private String refNo;
    private String remark;
    private LocalDateTime updatedDate;
    private String updatedBy;
    private String locCodeFrom;
    private String locCodeTo;
    private Integer macId;
    private String intgUpdStatus;
    private Boolean vouLock;
    private String traderCode;
    private String labourGroupCode;
    private String jobCode;
    private Integer printCount;
    private Boolean skipInv;
    private List<TransferHisDetail> listTD;
    private List<THDetailKey> delList;
    private List<String> location;
    private ZonedDateTime vouDateTime;
}
