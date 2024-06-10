package cv.api.entity;

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
