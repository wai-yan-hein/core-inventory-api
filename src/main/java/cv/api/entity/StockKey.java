package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockKey {
    private String stockCode;
    private String compCode;

}
