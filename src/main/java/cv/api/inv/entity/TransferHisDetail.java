package cv.api.inv.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "transfer_his_detail")
public class TransferHisDetail {
    @Id
    @Column(name = "td_code")
    private String tdCode;
    @Column(name = "vou_no")
    private String vouNo;
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "stock_code", referencedColumnName = "stock_code"),
            @JoinColumn(name = "comp_code", referencedColumnName = "comp_code")
    })
    private Stock stock;
    @Column(name = "qty")
    private Float qty;
    @Column(name = "wt")
    private Float wt;
    @ManyToOne
    @JoinColumn(name = "unit")
    private StockUnit unit;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "comp_code", insertable = false, updatable = false)
    private String compCode;
}
