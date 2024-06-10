package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MillingUsageKey {
    private String vouNo;
    private String compCode;
    private Integer uniqueId;
}
