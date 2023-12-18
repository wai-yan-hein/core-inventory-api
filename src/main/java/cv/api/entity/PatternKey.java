package cv.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class PatternKey implements Serializable {
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "unique_id")
    private int uniqueId;
    @Column(name = "f_stock_code")
    private String mapStockCode;
}
