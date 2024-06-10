package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VouDiscount {
    private VouDiscountKey key;
    private String description;
    private Double qty;
    private Double price;
    private Double amount;
    private String unit;
    private String unitName;

}
