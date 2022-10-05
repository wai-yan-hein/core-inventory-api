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
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "stock_code"),
            @JoinColumn(name = "comp_code"),
            @JoinColumn(name = "dept_id")
    })
    private Stock stock;
    @Column(name = "min_qty")
    private Float minQty;
    @ManyToOne
    @JoinColumnsOrFormulas(value = {
            @JoinColumnOrFormula(formula = @JoinFormula(value = "comp_code")),
            @JoinColumnOrFormula(formula = @JoinFormula(value = "dept_id")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "min_unit"))
    })
    private StockUnit minUnit;
    @Column(name = "max_qty")
    private Float maxQty;
    @ManyToOne
    @JoinColumnsOrFormulas(value = {
            @JoinColumnOrFormula(formula = @JoinFormula(value = "comp_code")),
            @JoinColumnOrFormula(formula = @JoinFormula(value = "dept_id")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "max_unit"))
    })
    private StockUnit maxUnit;
    @Transient
    private Float orderQty;
    @Transient
    private StockUnit orderUnit;
    @Transient
    private float minSmallQty;
    @Transient
    private float maxSmallQty;
    @Transient
    private float balSmallQty;
    @Transient
    private String balUnit;
}
