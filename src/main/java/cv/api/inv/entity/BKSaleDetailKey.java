package cv.api.inv.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Embeddable
public class BKSaleDetailKey extends SaleDetailKey {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tran_id")
    private Long tranId;
}
