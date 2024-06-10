package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeightLossHisDetailKey {
    private String vouNo;
    private String compCode;
    private Integer uniqueId;
}
