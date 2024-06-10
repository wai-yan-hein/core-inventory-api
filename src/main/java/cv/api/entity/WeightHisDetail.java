package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeightHisDetail {
    private WeightHisDetailKey key;
    private Double weight;
}
