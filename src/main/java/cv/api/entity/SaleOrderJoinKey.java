package cv.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class SaleOrderJoinKey implements Serializable {
    @Column(name = "sale_vou_no")
    private String saleVouNo;
    @Column(name = "order_vou_no")
    private String orderVouNo;
    @Column(name = "comp_code")
    private String compCode;
}
