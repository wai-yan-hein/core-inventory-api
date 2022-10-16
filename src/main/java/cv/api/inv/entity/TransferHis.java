package cv.api.inv.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
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
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "vou_date")
    private Date vouDate;
    @Column(name = "ref_no")
    private String refNo;
    @Column(name = "remark")
    private String remark;
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
    @Transient
    private List<TransferHisDetail> listTD;
    @Transient
    private List<String> delList;
    @Transient
    private String status;
}