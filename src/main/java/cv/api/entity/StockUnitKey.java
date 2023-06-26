package cv.api.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Embeddable
public class StockUnitKey implements Serializable {
    @Column(name = "unit_code")
    private String unitCode;
    @Column(name = "comp_code")
    private String compCode;
}
