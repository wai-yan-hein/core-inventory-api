/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class PurHis {

    private PurHisKey key;
    private Integer deptId;
    private String traderCode;
    private LocalDateTime vouDate;
    private LocalDate dueDate;
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
    private Double taxP;
    private Double taxAmt;
    private String reference;
    private String intgUpdStatus;
    private Integer macId;
    private Boolean vouLock;
    private String batchNo;
    private Double commP;
    private Double commAmt;
    private Double expense;
    private String projectNo;
    private String carNo;
    private String labourGroupCode;
    private String landVouNo;
    private Integer printCount;
    private String weightVouNo;
    private String payableAcc;
    private String commAcc;
    private String disAcc;
    private String taxAcc;
    private String cashAcc;
    private String purchaseAcc;
    private String deptCode;
    private Double grandTotal;
    private Boolean sRec;
    private Integer tranSource;
    private Double outstanding;
    private String grnVouNo;
    private String refNo;
    private List<PurHisDetail> listPD;
    private List<PurExpense> listExpense;
    private ZonedDateTime vouDateTime;

}
