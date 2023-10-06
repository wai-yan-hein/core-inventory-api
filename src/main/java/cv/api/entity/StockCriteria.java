package cv.api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "stock_criteria")
public class StockCriteria {
    @EmbeddedId
    private StockCriteriaKey key;
    @Column(name = "criteria_name")
    private String criteriaName;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "active")
    private boolean active;
    @Transient
    private String relName;
    @Transient
    private String groupName;
    @Transient
    private String brandName;
    @Transient
    private String catName;
}
