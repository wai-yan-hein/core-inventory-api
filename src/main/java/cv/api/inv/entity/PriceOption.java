package cv.api.inv.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "price_option")
public class PriceOption implements java.io.Serializable {
    @Id
    @Column(name = "type")
    private String priceType;
    @Column(name = "desp")
    private String description;
    @Column(name = "comp_code")
    private String compCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PriceOption that = (PriceOption) o;
        return priceType != null && Objects.equals(priceType, that.priceType);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
