package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "transfer_his")
public class TransferHis {
    @EmbeddedId
    private TransferHisKey key;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "vou_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime vouDate;
    @Column(name = "ref_no")
    private String refNo;
    @Column(name = "remark")
    private String remark;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "loc_code_from")
    private String locCodeFrom;
    @Column(name = "loc_code_to")
    private String locCodeTo;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "vou_lock")
    private boolean vouLock;
    @Transient
    private List<TransferHisDetail> listTD;
    @Transient
    private List<THDetailKey> delList;
    @Transient
    private String status = "STATUS";
    @Transient
    private List<String> location;

//    public TransferHis() {
//    }
//
//    public TransferHis(Date updatedDate, List<String> location) {
//        this.updatedDate = updatedDate;
//        this.location = location;
//    }
}
