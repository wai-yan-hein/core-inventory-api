package cv.api.inv.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class ReorderKey implements Serializable {
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "loc_code")
    private String locCode;
}
