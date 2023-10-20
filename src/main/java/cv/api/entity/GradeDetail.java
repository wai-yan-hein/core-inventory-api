package cv.api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "grade_detail")
public class GradeDetail {
    @EmbeddedId
    private GradeDetailKey key;
    @Column(name = "grade_stock_code")
    private String gradeStockCode;
    @Column(name = "min_percent")
    private double minPercent;
    @Column(name = "max_percent")
    private double maxPercent;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Transient
    private String stockName;
}
