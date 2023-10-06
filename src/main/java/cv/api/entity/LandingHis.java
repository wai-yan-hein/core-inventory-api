package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "landing_his")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LandingHis {

    @EmbeddedId
    private LandingHisKey key;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "vou_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime vouDate;
    @Column(name = "trader_code")
    private String traderCode;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "remark")
    private String remark;
    @Column(name = "loc_code")
    private String locCode;
    @Transient
    private List<LandingHisDetail> listDetail;
    @Transient
    private List<LandingHisDetailKey> listDel;
    @Transient
    private String traderName;
    @Transient
    private String traderUserCode;
    @Transient
    private ZonedDateTime vouDateTime;

    public LandingHis() {
    }
}