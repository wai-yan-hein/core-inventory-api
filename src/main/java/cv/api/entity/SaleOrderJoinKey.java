package cv.api.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class SaleOrderJoinKey implements Serializable {
    private String saleVouNo;
    private String orderVouNo;
    private String compCode;
}
