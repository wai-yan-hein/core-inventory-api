package cv.api.inv.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
@Data
@Embeddable
public class TmpStockIOKey implements java.io.Serializable {
    @Column(name = "tran_option")
    private String tranOption;
    @Column(name = "tran_date")
    @Temporal(TemporalType.DATE)
    private Date tranDate;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "mac_id")
    private Integer macId;
}
