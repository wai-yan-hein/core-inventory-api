package cv.api.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class WareHouse {
    private WareHouseKey key;
    private String description;
    private LocalDateTime updatedDate;
    private String updatedBy;
    private LocalDateTime createdDate;
    private String createdBy;
    private String userCode;
    private Boolean active;
    private Boolean deleted;
}
