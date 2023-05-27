package cv.api.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "receive_his")
public class ReceiveHis {
    @EmbeddedId
    private ReceiveHisKey key;
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
    @Column(name = "dis_amt")
    private Float disAmt;
    @Column(name = "dis_p")
    private Float disPercent;
    @Column(name = "created_date")
    private Date createdDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "mac_id")
    private Integer macId;
    @Transient
    private List<ReceiveHisDetail> listDetail;
    @Transient
    private List<ReceiveHisDetailKey> listDelete;
}
