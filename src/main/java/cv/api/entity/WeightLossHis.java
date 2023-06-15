package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "weight_loss_his")
public class WeightLossHis {
    @EmbeddedId
    private WeightLossHisKey key;
    @Column(name = "vou_date",columnDefinition = "TIMESTAMP")
    private LocalDateTime vouDate;
    @Column(name = "ref_no")
    private String refNo;
    @Column(name = "remark")
    private String remark;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "mac_id")
    private Integer macId;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;
    @Column(name = "deleted")
    private boolean deleted;
    @Transient
    private List<WeightLossHisDetail> listDetail;
    @Transient
    private List<WeightLossHisDetailKey> delKeys;


}
