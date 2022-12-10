package cv.api.inv.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@EqualsAndHashCode(callSuper = true)
@Data
@Embeddable
public class BKSaleDetailKey extends SaleDetailKey {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tran_id")
    private Long tranId;
}
