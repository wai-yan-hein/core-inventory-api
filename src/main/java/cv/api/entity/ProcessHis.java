package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "process_his")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessHis {
    @EmbeddedId
    private ProcessHisKey key;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "vou_date",columnDefinition = "TIMESTAMP")
    private LocalDateTime vouDate;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "end_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime endDate;
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
    private List<ProcessHisDetail> listDetail;
    @Transient
    private String stockUsrCode;
    @Transient
    private String stockName;
    @Transient
    private String ptName;
    @Transient
    private String locName;
    @Transient
    private ZonedDateTime vouDateTime;

}
