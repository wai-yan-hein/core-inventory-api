package cv.api.model;

import lombok.Data;

@Data
public class StockBalance {
    private StockBalanceKey key;
    private String stockName;
    private String balance;
    private String locName;
    private String userCode;
}
