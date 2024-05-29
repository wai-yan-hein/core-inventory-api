package cv.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StockFormulaQty {
    private StockFormulaQtyKey key;
    private String criteriaCode;
    private Double percent;
    private Double qty;
    private String unit;
    private double percentAllow;
    private LocalDateTime updatedDate;
    private transient String criteriaName;
    private transient String userCode;
}
