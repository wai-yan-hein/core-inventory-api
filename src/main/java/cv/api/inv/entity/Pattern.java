package cv.api.inv.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "pattern")
@Data
public class Pattern implements java.io.Serializable {
    @Id
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "stock_code"),
            @JoinColumn(name = "comp_code"),
            @JoinColumn(name = "dept_id")
    })
    private Stock stock;
    @Column(name = "qty")
    private Float qty;
    @Column(name = "price")
    private Float price;
    @ManyToOne
    @JoinColumnsOrFormulas(value = {
            @JoinColumnOrFormula(formula = @JoinFormula(value = "comp_code")),
            @JoinColumnOrFormula(formula = @JoinFormula(value = "dept_id")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "unit"))
    })
    private StockUnit unit;
    @ManyToOne
    @JoinColumn(name = "loc_code")
    private Location location;
    @Column(name = "f_stock_code")
    private String stockCode;
    @Column(name = "unique_id")
    private Integer uniqueId;
}
