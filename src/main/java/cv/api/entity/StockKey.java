package cv.api.entity;

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
    @Column(name = "dept_id")
    private Integer deptId;

    public StockKey() {
    }

    public StockKey(String stockCode, String compCode, Integer deptId) {
        this.stockCode = stockCode;
        this.compCode = compCode;
        this.deptId = deptId;
    }
}
