package cv.api.entity;

import jakarta.persistence.*;
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
    @Transient
    private String stockName;
}
