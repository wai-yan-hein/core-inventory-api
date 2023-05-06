package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Data
@Embeddable
public class OPHisDetailKey implements java.io.Serializable {
    @Column(name = "op_code")
    private String opCode;
    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "dept_id")
    private Integer deptId;

}
