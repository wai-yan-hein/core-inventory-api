package cv.api.entity;

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
    private Boolean deleted;
    private Boolean active;
    private String relName;
    private String groupName;
    private String brandName;
    private String catName;
}
