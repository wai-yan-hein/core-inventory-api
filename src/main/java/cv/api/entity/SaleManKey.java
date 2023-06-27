package cv.api.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Embeddable
public class SaleManKey implements Serializable {
    @Column(name = "saleman_code")
    private String saleManCode;
    @Column(name = "comp_code")
    private String compCode;
}
