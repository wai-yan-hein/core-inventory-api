package cv.api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "payment_his")
public class PaymentHis {
    @EmbeddedId
    private PaymentHisKey key;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "vou_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime vouDate;
    @Column(name = "trader_code")
    private String traderCode;
    @Column(name = "remark")
    private String remark;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "amount")
    private Float amount;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "account")
    private String account;
    @Column(name = "project_no")
    private String projectNo;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "tran_option")
    private String tranOption;
    @Transient
    private List<PaymentHisDetail> listDetail;
    @Transient
    private List<PaymentHisDetailKey> listDelete;
    @Transient
    private String traderName;
    @Transient
    private ZonedDateTime vouDateTime;
}
