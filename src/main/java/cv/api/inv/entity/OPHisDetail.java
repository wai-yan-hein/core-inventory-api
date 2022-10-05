package cv.api.inv.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@ToString
@Entity
@Table(name = "op_his_detail")
public class OPHisDetail implements java.io.Serializable {
    @Id
    @Column(name = "op_code")
    private String opCode;
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
    @Column(name = "amount")
    private Float amount;
    @ManyToOne
    @JoinColumn(name = "loc_code")
    private Location location;
    @ManyToOne
    @JoinColumnsOrFormulas(value = {
            @JoinColumnOrFormula(formula = @JoinFormula(value = "comp_code")),
            @JoinColumnOrFormula(formula = @JoinFormula(value = "dept_id")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "unit"))
    })
    private StockUnit stockUnit;
    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "comp_code", insertable = false, updatable = false)
    private String compCode;
}
