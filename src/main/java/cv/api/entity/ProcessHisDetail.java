package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "process_his_detail")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessHisDetail {
    @EmbeddedId
    private ProcessHisDetailKey key;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "vou_date")
    private Date vouDate;
    @Column(name = "qty")
    private float qty;
    @Column(name = "unit")
    private String unit;
    @Column(name = "price")
    private Float price;
    @Transient
    private String locName;
    @Transient
    private String stockName;
    @Transient
    private String stockUsrCode;
}
