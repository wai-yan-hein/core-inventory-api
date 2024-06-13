package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PurchaseIOJoin {
    private String purVouNo;
    private String ioVouNo;
    private String compCode;
}
