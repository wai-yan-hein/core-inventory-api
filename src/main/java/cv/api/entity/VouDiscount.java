package cv.api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "vou_discount")
public class VouDiscount {
    @EmbeddedId
    private VouDiscountKey key;
    @Column(name = "description")
    private String description;
    @Column(name = "qty")
    private double qty;
    @Column(name = "price")
    private double price;
    @Column(name = "amount")
    private double amount;
    @Column(name = "unit")
    private String unit;
    @Transient
    private  String unitName;

}
