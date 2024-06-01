package cv.api.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StockCriteria {
    private StockCriteriaKey key;
    private String userCode;
    private String criteriaName;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private boolean deleted;
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
