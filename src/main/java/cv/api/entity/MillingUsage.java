package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MillingUsage {
    private MillingUsageKey key;
    private String stockCode;
    private Double qty;
    private String unit;
    private String locCode;
    private String userCode;
    private String stockName;
    private String locName;
}
