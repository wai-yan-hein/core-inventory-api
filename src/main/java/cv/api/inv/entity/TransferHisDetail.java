package cv.api.inv.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "transfer_his_detail")
public class TransferHisDetail {
    @Id
    @Column(name = "td_code")
    private String tdCode;
    @Column(name = "vou_no")
    private String vouNo;
    @ManyToOne
    @JoinColumns({@JoinColumn(name = "stock_code"), @JoinColumn(name = "comp_code"), @JoinColumn(name = "dept_id")})
    private Stock stock;
    @Column(name = "qty")
    private Float qty;
    @ManyToOne
    @JoinColumnsOrFormulas(value = {@JoinColumnOrFormula(formula = @JoinFormula(value = "comp_code")), @JoinColumnOrFormula(formula = @JoinFormula(value = "dept_id")), @JoinColumnOrFormula(column = @JoinColumn(name = "unit"))})
    private StockUnit unit;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "comp_code", insertable = false, updatable = false)
    private String compCode;
}
