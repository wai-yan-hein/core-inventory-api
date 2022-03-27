/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author wai yan
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "stock_in_out_detail")
public class StockInOutDetail implements Serializable {

    @EmbeddedId
    private StockInOutKey ioKey;
    @ManyToOne
    @JoinColumn(name = "stock_code")
    private Stock stock;
    @ManyToOne
    @JoinColumn(name = "loc_code")
    private Location location;
    @Column(name = "in_qty")
    private Float inQty;
    @Column(name = "in_wt")
    private Float inWt;
    @ManyToOne
    @JoinColumn(name = "in_unit")
    private StockUnit inUnit;
    @Column(name = "out_qty")
    private Float outQty;
    @Column(name = "out_wt")
    private Float outWt;
    @ManyToOne
    @JoinColumn(name = "out_unit")
    private StockUnit outUnit;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "cost_price")
    private Float costPrice;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StockInOutDetail that = (StockInOutDetail) o;
        return ioKey != null && Objects.equals(ioKey, that.ioKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ioKey);
    }
}
