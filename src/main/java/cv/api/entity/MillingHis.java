/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import jakarta.persistence.Transient;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author wai yan
 */
@Data
@Builder
public class MillingHis {
    private MillingHisKey key;
    private Integer deptId;
    private String traderCode;
    private LocalDateTime vouDate;
    private String curCode;
    private String locCode;
    private String remark;
    private String reference;
    private Boolean deleted;
    private LocalDateTime createdDate;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime updatedDate;
    private Integer macId;
    private String intgUpdStatus;
    private Boolean vouLock;
    private String projectNo;
    private String carNo;
    private String vouStatusId;
    private Double loadQty;
    private Double loadWeight;
    private Double loadAmount;
    private Double loadExpense;
    private Double loadCost;
    private Double outputQty;
    private Double outputWeight;
    private Double outputAmount;
    private Double diffWeight;
    private Double diffQty;
    private Double percentWeight;
    private Double percentQty;
    private String jobNo;
    private Integer printCount;
    private String localVouNo;
    private String status;
    private List<MillingRawDetail> listRaw;
    private List<MillingOutDetail> listOutput;
    private List<MillingExpense> listExpense;
    private List<MillingUsage> listUsage;
    private Boolean local;
    private String traderName;
    private String processType;
    private String vouDateStr;
    private ZonedDateTime vouDateTime;

}
