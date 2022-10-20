package cv.api.inv.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class PatternKey implements Serializable {
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "dept_id")
    private Integer deptId;
}
