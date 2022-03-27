package cv.api.inv.entity;

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
@Entity
@Table(name = "pattern_detail")
public class PatternDetail implements java.io.Serializable {
    @Id
    @Column(name = "pt_code")
    private String ptCode;
    @Column(name = "code")
    private String patternCode;
    @ManyToOne
    @JoinColumn(name = "loc_code")
    private Location location;
    @ManyToOne
    @JoinColumn(name = "stock_code")
    private Stock stock;
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
    @Transient
    private Float costPrice;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PatternDetail that = (PatternDetail) o;
        return ptCode != null && Objects.equals(ptCode, that.ptCode);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
