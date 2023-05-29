package cv.api.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "payment_his")
public class PaymentHis {
    @EmbeddedId
    private PaymentHisKey key;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "vou_date")
    private Date vouDate;
    @Column(name = "trader_code")
    private String traderCode;
    @Column(name = "remark")
    private String remark;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "amount")
    private Float amount;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;
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
    @Transient
    private List<PaymentHisDetail> listDetail;
    @Transient
    private List<PaymentHisDetailKey> listDelete;
    @Transient
    private String traderName;
}
