package cv.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "stock_formula_qty")
public class StockFormulaQty {
    @EmbeddedId
    private StockFormulaQtyKey key;
    @Column(name = "criteria_code")
    private String criteriaCode;
    @Column(name = "percent")
    private Double percent;
    @Column(name = "qty")
    private Double qty;
    @Column(name = "unit")
    private String unit;
    @Column(name = "percent_allow")
    private double percentAllow;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    private transient String criteriaName;
    private transient String userCode;
}
