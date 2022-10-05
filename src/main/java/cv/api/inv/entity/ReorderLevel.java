package cv.api.inv.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "reorder_level")
public class ReorderLevel implements java.io.Serializable {
    @Id
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "min_qty")
    private Float minQty;
    @Column(name = "min_unit")
    private String minUnitCode;
    @Column(name = "max_qty")
    private Float maxQty;
    @Column(name = "max_unit")
    private String maxUnitCode;
    @Transient
    private Float orderQty;
    @Transient
    private String orderUnit;
    @Transient
    private float minSmallQty;
    @Transient
    private float maxSmallQty;
    @Transient
    private float balSmallQty;
    @Transient
    private String balUnit;
}
