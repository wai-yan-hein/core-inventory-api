package cv.api.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Embeddable
public class ProcessHisDetailKey implements Serializable {
    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "unique_id")
    private Integer uniqueId;
}
