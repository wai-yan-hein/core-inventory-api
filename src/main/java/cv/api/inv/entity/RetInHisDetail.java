/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author wai yan
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "ret_in_his_detail")
public class RetInHisDetail implements java.io.Serializable {

    @EmbeddedId
    private RetInKey riKey;
    @ManyToOne
    @JoinColumn(name = "stock_code", nullable = false)
    private Stock stock;
    @Column(name = "qty", nullable = false)
    private Float qty;
    @ManyToOne
    @JoinColumn(name = "unit", nullable = false)
    private StockUnit unit;
    @Column(name = "cost_price")
    private Float costPrice;
    @Column(name = "price", nullable = false)
    private Float price;
    @Column(name = "amt", nullable = false)
    private Float amount;
    @ManyToOne
    @JoinColumn(name = "loc_code")
    private Location location;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "wt")
    private Float wt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RetInHisDetail that = (RetInHisDetail) o;
        return riKey != null && Objects.equals(riKey, that.riKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(riKey);
    }
}
