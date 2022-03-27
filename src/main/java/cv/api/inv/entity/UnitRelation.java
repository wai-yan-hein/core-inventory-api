package cv.api.inv.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "unit_relation")
public class UnitRelation implements java.io.Serializable {
    @Id
    @Column(name = "rel_code")
    private String relCode;
    @Column(name = "rel_name")
    private String relName;
    @Transient
    private List<UnitRelationDetail> detailList;
}
