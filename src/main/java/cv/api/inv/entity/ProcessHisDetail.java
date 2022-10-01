package cv.api.inv.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "process_his_detail")
public class ProcessHisDetail {
    @Id
    @Column(name = "pd_code")
    private String pdCode;
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "stock_code", referencedColumnName = "stock_code"),
            @JoinColumn(name = "comp_code", referencedColumnName = "comp_code")
    })
    private Stock stock;
    @ManyToOne
    @JoinColumn(name = "loc_code")
    private Location location;
    @Column(name = "qty")
    private Float qty;
    @Column(name = "price")
    private Float price;
    @Column(name = "unit")
    private StockUnit unit;
    @Column(name = "amount")
    private Float amount;
    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "unique_id")
    private Integer uniqueId;
}
