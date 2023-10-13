package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import jakarta.persistence.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "pattern")
@Data
public class Pattern implements java.io.Serializable {
    @EmbeddedId
    private PatternKey key;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "qty")
    private Double qty;
    @Column(name = "price")
    private Double price;
    @Column(name = "unit")
    private String unitCode;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "price_type")
    private String priceTypeCode;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Transient
    private String priceTypeName;
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
    @Transient
    private String locName;
    @Transient
    private Double amount;

}
