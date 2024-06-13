package cv.api.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StockUnitPrice {
    private StockUnitPriceKey key;
    private Double salePriceN;
    private Double salePriceA;
    private Double salePriceB;
    private Double salePriceC;
    private Double salePriceD;
    private Double salePriceE;
    private Integer uniqueId;
    private LocalDateTime updatedDate;


}