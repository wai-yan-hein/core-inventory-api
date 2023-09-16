package cv.api.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;
@Embeddable
@Data
public class PaymentHisDetailKey implements Serializable {
    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "unique_id")
    private int uniqueId;
    @Column(name = "dept_id")
    private Integer deptId;
}
