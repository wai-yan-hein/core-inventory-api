package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SaleManKey {
    private String saleManCode;
    private String compCode;
}
