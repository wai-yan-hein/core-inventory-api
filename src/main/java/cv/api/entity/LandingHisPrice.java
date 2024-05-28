package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LandingHisPrice {
    private LandingHisPriceKey key;
    private String criteriaCode;
    private Double percent;
    private Double percentAllow;
    private Double price;
    private Double amount;
    private String criteriaUserCode;
    private String criteriaName;
}
