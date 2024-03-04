package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaleNote {
    private String vouNo;
    private String compCode;
    private Integer uniqueId;
    private String description;
    private Double saleQty;
    private Double qty;
    private String unitName;
}
