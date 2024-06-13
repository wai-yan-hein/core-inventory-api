package cv.api.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StockUnitPriceKey {
    private String stockCode;
    private String compCode;
    private String unit;

}