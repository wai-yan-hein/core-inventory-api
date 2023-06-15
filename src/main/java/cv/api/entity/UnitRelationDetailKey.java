package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@RequiredArgsConstructor
@Embeddable
public class UnitRelationDetailKey implements java.io.Serializable {
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "rel_code")
    private String relCode;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "comp_code")
    private String compCode;

    public UnitRelationDetailKey(Integer uniqueId, String relCode, Integer deptId, String compCode) {
        this.uniqueId = uniqueId;
        this.relCode = relCode;
        this.deptId = deptId;
        this.compCode = compCode;
    }
}
