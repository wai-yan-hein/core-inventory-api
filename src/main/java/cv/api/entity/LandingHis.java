package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "landing_his")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LandingHis {

    @EmbeddedId
    private LandingHisKey key;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "vou_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime vouDate;
    @Column(name = "trader_code")
    private String traderCode;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "remark")
    private String remark;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "amount")
    private Double amount;
    @Column(name = "pur_amt")
    private Double purAmt;
    @Column(name = "pur_price")
    private Double purPrice;
    @Column(name = "price")
    private Double price;
    @Column(name = "criteria_amt")
    private Double criteriaAmt;
    @Column(name = "cargo")
    private String cargo;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "gross_qty")
    private Double grossQty;
    @Transient
    private List<LandingHisPrice> listPrice;
    @Transient
    private List<LandingHisPriceKey> listDelPrice;
    @Transient
    private List<LandingHisQty> listQty;
    @Transient
    private List<LandingHisGrade> listGrade;
    @Transient
    private List<LandingHisQtyKey> listDelQty;
    @Transient
    private String traderName;
    @Transient
    private String locName;
    @Transient
    private String stockName;
    @Transient
    private String traderUserCode;
    @Transient
    private ZonedDateTime vouDateTime;

    public LandingHis() {
    }
}