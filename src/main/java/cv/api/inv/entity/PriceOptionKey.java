package cv.api.inv.entity;

import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;

@Data
public class PriceOptionKey implements Serializable {
    @Column(name = "type")
    private String priceType;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "dept_id")
    private Integer deptId;
}
