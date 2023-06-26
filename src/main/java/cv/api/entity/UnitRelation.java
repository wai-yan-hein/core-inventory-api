package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "unit_relation")
public class UnitRelation {
    @EmbeddedId
    private RelationKey key;
    @Column(name = "rel_name")
    private String relName;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "dept_id")
    private Integer deptId;
    @Transient
    private List<UnitRelationDetail> detailList;
}
