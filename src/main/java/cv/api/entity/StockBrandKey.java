package cv.api.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Embeddable
public class StockBrandKey implements Serializable {
    @Column(name = "brand_code")
    private String brandCode;
    @Column(name = "comp_code")
    private String compCode;
}
