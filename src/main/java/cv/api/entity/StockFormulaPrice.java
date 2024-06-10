package cv.api.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class StockFormulaPrice {
    private StockFormulaPriceKey key;
    private String criteriaCode;
    private Double percent;
    private Double price;
    private double percentAllow;
    private LocalDateTime updatedDate;
    private transient String criteriaName;
    private transient String userCode;
    private transient List<GradeDetail> listGrade;

}
