package cv.api.report.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class StockValueDto {
    private String id;
    private String compCode;
    private LocalDate tranDate;
    private String groupName;
    private String catName;
    private String stockCode;
    private String stockName;
    private Double qty;
    private Double bag;
    private LocalDateTime updatedDate;
}
