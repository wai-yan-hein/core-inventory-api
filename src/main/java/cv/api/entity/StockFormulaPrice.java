package cv.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "stock_formula_price")
public class StockFormulaPrice {
    @EmbeddedId
    private StockFormulaPriceKey key;
    @Column(name = "criteria_code")
    private String criteriaCode;
    @Column(name = "percent")
    private Double percent;
    @Column(name = "price")
    private Double price;
    @Column(name = "percent_allow")
    private double percentAllow;
    private transient String criteriaName;
    private transient String userCode;
    private transient List<GradeDetail> listGrade;

}
