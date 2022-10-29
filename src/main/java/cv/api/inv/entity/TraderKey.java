package cv.api.inv.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class TraderKey implements Serializable {
    @Column(name = "code")
    private String code;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "dept_id")
    private Integer deptId;

}
