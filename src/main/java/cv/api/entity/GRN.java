package cv.api.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "grn")
public class GRN {
    @EmbeddedId
    private GRNKey key;
    @Column(name = "batch_no")
    private String batchNo;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "vou_date")
    private Date vouDate;
    @Column(name = "trader_code")
    private String traderCode;
    @Column(name = "closed")
    private boolean closed;
    @Column(name = "deleted")
    private boolean deleted;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "remark")
    private String remark;
    @Column(name = "loc_code")
    private String locCode;
    @Transient
    private List<GRNDetail> listDetail;
    @Transient
    private List<GRNDetailKey> listDel;
    @Transient
    private String traderName;
    @Transient
    private String traderUserCode;

}
