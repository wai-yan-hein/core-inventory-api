package cv.api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "milling_usage")
public class MillingUsage {
    @EmbeddedId
    private MillingUsageKey key;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "qty")
    private Double qty;
    @Column(name = "unit")
    private String unit;
    @Column(name = "loc_code")
    private String locCode;
    @Transient
    private String userCode;
    @Transient
    private String stockName;
    @Transient
    private String locName;
}
