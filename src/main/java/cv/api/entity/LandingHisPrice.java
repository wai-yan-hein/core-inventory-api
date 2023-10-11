package cv.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "landing_his_price")
public class LandingHisPrice {
    @EmbeddedId
    private LandingHisPriceKey key;
    @Column(name = "criteria_code")
    private String criteriaCode;
    @Column(name = "percent")
    private double percent;
    @Column(name = "percent_allow")
    private double percentAllow;
    @Column(name = "price")
    private double price;
    @Column(name = "amount")
    private double amount;
    private transient String criteriaUserCode;
    private transient String criteriaName;
}
