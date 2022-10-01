package cv.api.inv.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@RequiredArgsConstructor
@Embeddable
public class UnitRelationDetailKey implements java.io.Serializable {
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "rel_code")
    private String relCode;

    public UnitRelationDetailKey(Integer uniqueId, String relCode) {
        this.uniqueId = uniqueId;
        this.relCode = relCode;
    }
}
