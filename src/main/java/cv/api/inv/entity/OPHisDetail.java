package cv.api.inv.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "op_his_detail")
public class OPHisDetail implements java.io.Serializable {
    @Id
    @Column(name = "op_code")
    private String opCode;
    @ManyToOne
    @JoinColumn(name = "stock_code")
    private Stock stock;
    @Column(name = "qty")
    private Float qty;
    @Column(name = "std_wt")
    private Float stdWt;
    @Column(name = "price")
    private Float price;
    @Column(name = "amount")
    private Float amount;
    @ManyToOne
    @JoinColumn(name = "loc_code")
    private Location location;
    @ManyToOne
    @JoinColumn(name = "unit")
    private StockUnit stockUnit;
    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "unique_id")
    private Integer uniqueId;
}
