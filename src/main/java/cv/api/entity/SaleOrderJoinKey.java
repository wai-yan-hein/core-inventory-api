package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaleOrderJoinKey {
    private String saleVouNo;
    private String orderVouNo;
    private String compCode;
}
