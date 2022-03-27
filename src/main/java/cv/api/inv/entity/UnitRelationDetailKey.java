package cv.api.inv.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

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
