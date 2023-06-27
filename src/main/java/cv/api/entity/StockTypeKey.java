package cv.api.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Embeddable
public class StockTypeKey implements Serializable {
    @Column(name = "stock_type_code")
    private String stockTypeCode;
    @Column(name = "comp_code")
    private String compCode;
}
