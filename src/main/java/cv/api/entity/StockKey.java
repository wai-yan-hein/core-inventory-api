package cv.api.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Embeddable
public class StockKey implements Serializable {
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "comp_code")
    private String compCode;

    public StockKey() {
    }

    public StockKey(String stockCode, String compCode) {
        this.stockCode = stockCode;
        this.compCode = compCode;
    }
}
