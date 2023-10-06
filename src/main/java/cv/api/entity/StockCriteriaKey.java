package cv.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
@Data
@Embeddable
public class StockCriteriaKey implements Serializable {
    @Column(name = "criteria_code")
    private String criteriaCode;
    @Column(name = "comp_code")
    private String compCode;
    public StockCriteriaKey() {}
    public StockCriteriaKey(String criteriaCode, String compCode) {
        this.criteriaCode = criteriaCode;
        this.compCode = compCode;
    }
}
