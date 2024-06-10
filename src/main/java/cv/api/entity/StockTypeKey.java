package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockTypeKey {
    private String stockTypeCode;
    private String compCode;
}
