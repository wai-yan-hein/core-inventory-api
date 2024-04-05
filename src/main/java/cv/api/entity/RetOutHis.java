/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author WSwe
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class RetOutHis {

    private RetOutHisKey key;
    private Integer deptId;
    private String traderCode;
    private LocalDateTime vouDate;
    private String locCode;
    private Boolean deleted;
    private Double vouTotal;
    private Double paid;
    private Double discount;
    private Double balance;
    private String createdBy;
    private LocalDateTime createdDate;
    private String updatedBy;
    private LocalDateTime updatedDate;
    private String remark;
    private Integer sessionId;
    private String refNo;
    private String curCode;
    private Double discP;
    private String intgUpdStatus;
    private Integer macId;
    private String status;
    private Boolean vouLock;
    private String projectNo;
    private Integer printCount;
    private Double grandTotal;
    private String deptCode;
    private String srcAcc;
    private String cashAcc;
    private String payableAcc;
    private String disAcc;
    private String taxAcc;
    private Double taxAmt;
    private Double taxP;
    private List<RetOutHisDetail> listRD;
    private ZonedDateTime vouDateTime;
    private String traderName;
}
