package cv.api.inv.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "process_his")
public class ProcessHis {
    @EmbeddedId
    private ProcessHisKey key;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "vou_date")
    private Date vouDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    private Date endDate;
    @Column(name = "pt_code")
    private String ptCode;
    @Column(name = "remark")
    private String remark;
    @Column(name = "process_no")
    private String processNo;
    @Column(name = "qty")
    private Float qty;
    @Column(name = "unit")
    private String unit;
    @Column(name = "price")
    private Float price;
    @Column(name = "finished")
    private boolean finished;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "mac_id")
    private Integer macId;
    @Transient
    private String stockUsrCode;
    @Transient
    private String stockName;
    @Transient
    private String ptName;

}
