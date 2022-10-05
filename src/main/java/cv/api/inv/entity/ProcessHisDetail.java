package cv.api.inv.entity;

import lombok.Data;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;
import org.hibernate.mapping.Join;

import javax.persistence.*;

@Data
@Entity
@Table(name = "process_his_detail")
public class ProcessHisDetail {
    @Id
    @Column(name = "pd_code")
    private String pdCode;
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "stock_code"),
            @JoinColumn(name = "comp_code"),
            @JoinColumn(name = "dept_id")
    })
    private Stock stock;
    @ManyToOne
    @JoinColumn(name = "loc_code")
    private Location location;
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
    @Column(name = "amount")
    private Float amount;
    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "unique_id")
    private Integer uniqueId;
}
