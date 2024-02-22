/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author wai yan
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "sale_his")
public class SaleHis {

    @EmbeddedId
    private SaleHisKey key;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "trader_code")
    private String traderCode;
    @Column(name = "saleman_code")
    private String saleManCode;
    @Column(name = "vou_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime vouDate;
    @Column(name = "credit_term", columnDefinition = "TIMESTAMP")
    private LocalDateTime creditTerm;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "remark")
    private String remark;
    @Column(name = "reference")
    private String reference;
    @Column(name = "vou_total")
    private Double vouTotal;
    @Column(name = "grand_total")
    private Double grandTotal;
    @Column(name = "discount")
    private Double discount;
    @Column(name = "disc_p")
    private Double discP;
    @Column(name = "tax_amt")
    private Double taxAmt;
    @Column(name = "tax_p")
    private Double taxPercent;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "paid")
    private Double paid;
    @Column(name = "vou_balance")
    private Double balance;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "session_id")
    private Integer session;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "address")
    private String address;
    @Column(name = "order_code")
    private String orderCode;
    @Column(name = "reg_code")
    private String regionCode;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "vou_lock")
    private boolean vouLock;
    @Column(name = "order_no")
    private String orderNo;
    @Column(name = "project_no")
    private String projectNo;
    @Column(name = "car_no")
    private String carNo;
    @Column(name = "grn_vou_no")
    private String grnVouNo;
    @Column(name = "expense")
    private Double expense;
    @Column(name = "labour_group_code")
    private String labourGroupCode;
    @Column(name = "print_count")
    private Integer printCount;
    @Column(name = "account")
    private String saleAcc;
    @Column(name = "debtor_acc")
    private String debtorAcc;
    @Column(name = "cash_acc")
    private String cashAcc;
    @Column(name = "dept_code")
    private String deptCode;
    @Column(name = "weight_vou_no")
    private String weightVouNo;
    @Column(name = "post")
    private boolean post;
    @Column(name = "s_pay")
    private boolean sPay;
    @Column(name = "tran_source")
    private int tranSource;
    @Column(name = "outstanding")
    private Double outstanding;
    @Transient
    private String status = "STATUS";
    @Transient
    private List<SaleHisDetail> listSH;
    @Transient
    private List<SaleDetailKey> listDel;
    @Transient
    private boolean backup;
    @Transient
    private List<String> location;
    @Transient
    private List<SaleExpense> listExpense;
    @Transient
    private List<SaleExpenseKey> listDelExpense;
    @Transient
    private List<VouDiscount> listVouDiscount;
    @Transient
    private List<VouDiscountKey> listDelVouDiscount;
    @Transient
    private ZonedDateTime vouDateTime;
    @Transient
    private List<String> listOrder;

}
