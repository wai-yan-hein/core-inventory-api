package cv.api.inv.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "grn_detail")
public class GRNDetail {
    @EmbeddedId
    private GRNDetailKey key;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "qty")
    private Float qty;
    @Column(name = "unit")
    private String unit;
    @Column(name = "loc_code")
    private String locCode;
    @Transient
    private String userCode;
    @Transient
    private String stockName;
    @Transient
    private String relName;
    @Transient
    private String locName;
}
