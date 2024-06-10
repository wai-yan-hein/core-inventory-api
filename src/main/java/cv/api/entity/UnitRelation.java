package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class UnitRelation {
    private RelationKey key;
    private String relName;
    private String intgUpdStatus;
    private LocalDateTime updatedDate;
    private Integer deptId;
    private List<UnitRelationDetail> detailList;
}
