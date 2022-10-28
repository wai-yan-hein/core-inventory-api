package cv.api.inv.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
@Data
public class TmpStockIOKey implements java.io.Serializable {
    private String tranOption;
    private Date tranDate;
    private String stockCode;
    private String locCode;
    private Integer macId;
    private String compCode;
    private Integer deptId;
    private String vouNo;
}
