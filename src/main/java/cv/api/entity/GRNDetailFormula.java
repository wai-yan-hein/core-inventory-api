package cv.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "grn_detail_formula")
public class GRNDetailFormula {
    @EmbeddedId
    private GRNDetailFormulaKey key;
    @Column(name = "description")
    private String description;
    @Column(name = "percent")
    private double percent;
    @Column(name = "price")
    private double price;
}
