package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "op_his")
public class OPHis {
    @EmbeddedId
    private OPHisKey key;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "op_date")
    @Temporal(TemporalType.DATE)
    private Date vouDate;
    @Column(name = "remark")
    private String remark;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "op_amt")
    private float opAmt;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "trader_code")
    private String traderCode;
    @Column(name = "tran_source")
    private int tranSource;
    @Transient
    private List<OPHisDetail> detailList;
    @Transient
    private List<OPHisDetailKey> listDel;
    @Transient
    private String status = "STATUS";
    @Transient
    private List<LocationKey> keys;
    @Transient
    private String locName;
    @Transient
    private String vouDateStr;
}
