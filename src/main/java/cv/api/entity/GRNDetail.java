package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import jakarta.persistence.*;

@Data
@Entity
@Table(name = "grn_detail")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GRNDetail {
    @EmbeddedId
    private GRNDetailKey key;
    @Column(name = "dept_id")
    private Integer deptId;
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
    @Column(name = "total_weight")
    private Float totalWeight;
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
