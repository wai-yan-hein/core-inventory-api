package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "op_his_detail")
public class OPHisDetail implements java.io.Serializable {
    @EmbeddedId
    private OPHisDetailKey key;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "qty")
    private double qty;
    @Column(name = "price")
    private double price;
    @Column(name = "amount")
    private double amount;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "unit")
    private String unitCode;
    @Column(name = "weight")
    private double weight;
    @Column(name = "weight_unit")
    private String weightUnit;
    @Column(name = "total_weight")
    private double totalWeight;
    @Column(name = "wet")
    private Double wet;
    @Column(name = "rice")
    private Double rice;
    @Column(name = "bag")
    private Double bag;
    @Transient
    private String userCode;
    @Transient
    private String stockName;
    @Transient
    private String groupName;
    @Transient
    private String brandName;
    @Transient
    private String catName;
    @Transient
    private String relName;
}
