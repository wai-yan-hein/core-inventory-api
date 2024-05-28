package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LandingHisPriceKey {
    private String vouNo;
    private String compCode;
    private Integer uniqueId;
}
