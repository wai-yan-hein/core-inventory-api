package cv.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "grade_detail_criteria")
public class GradeDetailCriteria {
    @EmbeddedId
    private GradeDetailCriteriaKey key;
    @Column(name = "criteria_code")
    private String criteriaCode;
    @Column(name = "percent")
    private double percent;
    @Column(name = "price")
    private double price;
    private transient String criteriaUserCode;
    private transient String criteriaName;
}
