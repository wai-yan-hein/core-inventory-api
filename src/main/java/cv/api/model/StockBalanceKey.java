package cv.api.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class StockBalanceKey implements Serializable {
    private String stockCode;
    private String locCode;
    private String compCode;

    public StockBalanceKey(String stockCode, String locCode, String compCode) {
        this.stockCode = stockCode;
        this.locCode = locCode;
        this.compCode = compCode;
    }

    public StockBalanceKey() {
    }
}
