package cv.api.entity;

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
    @Column(name = "weight")
    private Float weight;
    @Column(name = "weight_unit")
    private String weightUnit;
    @Transient
    private String userCode;
    @Transient
    private String stockName;
    @Transient
    private String relName;
    @Transient
    private String locName;
    @Transient
    private Float stdWeight;
}
