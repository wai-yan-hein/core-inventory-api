/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class RetInHis {

    private RetInHisKey key;
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
    private Integer session;
    private String curCode;
    private Double discP;
    private String intgUpdStatus;
    private Integer macId;
    private Boolean vouLock;
    private String projectNo;
    private Integer printCount;
    private Double taxAmt;
    private Double taxP;
    private String refNo;
    private Integer sessionId;
    private String status;
    private Double grandTotal;
    private String deptCode;
    private String srcAcc;
    private String cashAcc;
    private String debtorAcc;
    private String disAcc;
    private String taxAcc;
    private List<RetInHisDetail> listRD;
    private List<String> location;
    private ZonedDateTime vouDateTime;
    private String traderName;
    private Boolean sRec;

}
