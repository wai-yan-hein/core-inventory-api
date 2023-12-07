package cv.api.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class LabourPaymentDetail {
    @EmbeddedId
    private LabourPaymentDetailKey key;
    private String payVouNo;
    private String description;
    private Double qty;
    private Double price;
    private Double amount;

}
