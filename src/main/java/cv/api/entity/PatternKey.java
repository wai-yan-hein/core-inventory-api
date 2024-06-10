package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatternKey {
    private String stockCode;
    private String compCode;
    private Integer uniqueId;
    private String mapStockCode;
}
