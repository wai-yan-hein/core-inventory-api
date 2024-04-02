package cv.api.report.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class TopPurchase {
    private String id;
    private LocalDate tranDate;
    private String compCode;
    private String catName;
    private String stockCode;
    private String stockName;
    private Double wet;
    private Double rice;
    private Double price;
    private Double qty;
    private Double qtyPercent;
    private Double amount;
    private LocalDateTime updatedDate;
}
