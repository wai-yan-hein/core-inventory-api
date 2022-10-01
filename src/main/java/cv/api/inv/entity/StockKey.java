package cv.api.inv.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class StockKey implements Serializable {
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "comp_code")
    private String compCode;

    public StockKey(String stockCode, String compCode) {
        this.stockCode = stockCode;
        this.compCode = compCode;
    }

    public StockKey() {
    }
}
