package cv.api.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GradeDetail {
    private GradeDetailKey key;
    private String gradeStockCode;
    private double minPercent;
    private double maxPercent;
    private LocalDateTime updatedDate;
    private String stockName;
}
