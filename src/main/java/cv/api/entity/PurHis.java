/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "pur_his")
public class PurHis {

    @EmbeddedId
    private PurHisKey key;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "trader_code")
    private String traderCode;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "vou_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime vouDate;
    @Temporal(TemporalType.DATE)
    @Column(name = "due_date")
    private Date dueDate;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "vou_total")
    private Double vouTotal;
    @Column(name = "paid")
    private Double paid;
    @Column(name = "discount")
    private Double discount;
    @Column(name = "balance")
    private Double balance;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "remark")
    private String remark;
    @Column(name = "session_id")
    private Integer session;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "disc_p")
    private Double discP;
    @Column(name = "tax_p")
    private Double taxP;
    @Column(name = "tax_amt")
    private Double taxAmt;
    @Column(name = "reference")
    private String reference;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "vou_lock")
    private boolean vouLock;
    @Column(name = "batch_no")
    private String batchNo;
    @Column(name = "comm_p")
    private Double commP;
    @Column(name = "comm_amt")
    private Double commAmt;
    @Column(name = "expense")
    private Double expense;
    @Column(name = "project_no")
    private String projectNo;
    @Column(name = "car_no")
    private String carNo;
    @Column(name = "labour_group_code")
    private String labourGroupCode;
    @Column(name = "land_vou_no")
    private String landVouNo;
    @Column(name = "print_count")
    private Integer printCount;
    @Column(name = "weight_vou_no")
    private String weightVouNo;
    @Column(name = "payable_acc")
    private String payableAcc;
    @Column(name = "cash_acc")
    private String cashAcc;
    @Column(name = "purchase_acc")
    private String purchaseAcc;
    @Column(name = "dept_code")
    private String deptCode;
    @Column(name = "grand_total")
    private Double grandTotal;
    @Transient
    private String status = "STATUS";
    @Transient
    private List<PurHisDetail> listPD;
    @Transient
    private List<PurDetailKey> listDel;
    @Transient
    private List<String> location;
    @Transient
    private List<PurExpense> listExpense;
    @Transient
    private ZonedDateTime vouDateTime;

}
