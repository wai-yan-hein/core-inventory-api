package cv.api.entity;

import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class LandingHisQty {

    private LandingHisQtyKey key;
    private String criteriaCode;
    private Double percent;
    private Double qty;
    private Double totalQty;
    private Double percentAllow;
    private String unit;
    private String criteriaUserCode;
    private String criteriaName;
}
