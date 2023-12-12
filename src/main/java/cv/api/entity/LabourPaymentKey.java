package cv.api.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class LabourPaymentKey implements Serializable {
    private String vouNo;
    private String compCode;
}
