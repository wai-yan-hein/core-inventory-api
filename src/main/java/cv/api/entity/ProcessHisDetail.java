package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "process_his_detail")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessHisDetail {
    @EmbeddedId
    private ProcessHisDetailKey key;
    @Column(name = "vou_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime vouDate;
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
