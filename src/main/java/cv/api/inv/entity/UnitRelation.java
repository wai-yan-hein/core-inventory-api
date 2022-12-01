package cv.api.inv.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "unit_relation")
public class UnitRelation implements java.io.Serializable {
    @EmbeddedId
    private RelationKey key;
    @Column(name = "rel_name")
    private String relName;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "updated_date")
    private Date updatedDate;
    @Transient
    private List<UnitRelationDetail> detailList;

    public UnitRelation() {
    }

    public UnitRelation(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
