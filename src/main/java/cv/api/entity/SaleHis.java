/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class SaleHis {

    private SaleHisKey key;
    private Integer deptId;
    private String traderCode;
    private String saleManCode;
    private LocalDateTime vouDate;
    private LocalDate creditTerm;
    private String curCode;
    private String remark;
    private String reference;
    private Double vouTotal;
    private Double grandTotal;
    private Double discount;
    private Double discP;
    private Double taxAmt;
    private Double taxPercent;
    private Boolean deleted;
    private Double paid;
    private Double balance;
    private LocalDateTime createdDate;
    private String createdBy;
    private Integer session;
    private String updatedBy;
    private LocalDateTime updatedDate;
    private String address;
    private String orderCode;
    private String regionCode;
    private String locCode;
    private Integer macId;
    private String intgUpdStatus;
    private Boolean vouLock;
    private String orderNo;
    private String projectNo;
    private String carNo;
    private String grnVouNo;
    private Double expense;
    private String labourGroupCode;
    private Integer printCount;
    private String saleAcc;
    private String debtorAcc;
    private String cashAcc;
    private String deptCode;
    private String weightVouNo;
    private Boolean post;
    private Boolean sPay;
    private Integer tranSource;
    private Double outstanding;
    private Double totalPayment;
    private Double opening;
    private Double totalBalance;
    private String status;
    private List<SaleHisDetail> listSH;
    private boolean backup;
    private List<String> location;
    private List<SaleExpense> listExpense;
    private List<VouDiscount> listVouDiscount;
    private ZonedDateTime vouDateTime;
    private List<String> listOrder;
    private String disAcc;
    private String taxAcc;

}
