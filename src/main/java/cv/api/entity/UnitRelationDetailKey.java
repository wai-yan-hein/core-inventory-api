package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@RequiredArgsConstructor
@Builder
public class UnitRelationDetailKey {
    private Integer uniqueId;
    private String relCode;
    private String compCode;

    public UnitRelationDetailKey(Integer uniqueId, String relCode, String compCode) {
        this.uniqueId = uniqueId;
        this.relCode = relCode;
        this.compCode = compCode;
    }
}
