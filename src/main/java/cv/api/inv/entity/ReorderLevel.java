package cv.api.inv.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "reorder_level")
public class ReorderLevel implements java.io.Serializable {
    @Id
    @ManyToOne
    @JoinColumn(name = "stock_code")
    private Stock stock;
    @Column(name = "min_qty")
    private Float minQty;
    @ManyToOne
    @JoinColumn(name = "min_unit")
    private StockUnit minUnit;
    @Column(name = "max_qty")
    private Float maxQty;
    @ManyToOne
    @JoinColumn(name = "max_unit")
    private StockUnit maxUnit;
    @Column(name = "bal_qty")
    private Float balQty;
    @ManyToOne
    @JoinColumn(name = "bal_unit")
    private StockUnit balUnit;
    @Column(name = "comp_code")
    private String compCode;
    @Transient
    private Float orderQty;
    @Transient
    private StockUnit orderUnit;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ReorderLevel that = (ReorderLevel) o;
        return stock != null && Objects.equals(stock, that.stock);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
