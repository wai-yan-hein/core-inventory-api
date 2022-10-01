package cv.api.inv.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "pattern")
@Data
public class Pattern implements java.io.Serializable {
    @Id
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "stock_code", referencedColumnName = "stock_code"),
            @JoinColumn(name = "comp_code", referencedColumnName = "comp_code")
    })
    private Stock stock;
    @Column(name = "qty")
    private Float qty;
    @Column(name = "price")
    private Float price;
    @ManyToOne
    @JoinColumn(name = "unit")
    private StockUnit unit;
    @ManyToOne
    @JoinColumn(name = "loc_code")
    private Location location;
    @Column(name = "f_stock_code")
    private String stockCode;
    @Column(name = "unique_id")
    private Integer uniqueId;
}
