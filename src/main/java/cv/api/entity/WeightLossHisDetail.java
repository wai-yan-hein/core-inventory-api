package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import jakarta.persistence.*;

@Data
@Entity
@Table(name = "weight_loss_his_detail")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeightLossHisDetail {
    @EmbeddedId
    private WeightLossHisDetailKey key;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "qty")
    private Float qty;
    @Column(name = "unit")
    private String unit;
    @Column(name = "price")
    private Float price;
    @Column(name = "loss_qty")
    private Float lossQty;
    @Column(name = "loss_unit")
    private String lossUnit;
    @Column(name = "loss_price")
    private Float lossPrice;
    @Transient
    private String stockUserCode;
    @Transient
    private String stockName;
    @Transient
    private String locName;
    @Transient
    private String relName;
}
