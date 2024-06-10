package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockUnitKey {
    private String unitCode;
    private String compCode;
}
