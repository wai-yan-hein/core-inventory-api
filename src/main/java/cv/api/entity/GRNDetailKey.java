package cv.api.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Embeddable
public class GRNDetailKey implements Serializable {
    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "dept_id")
    private Integer deptId;
}
