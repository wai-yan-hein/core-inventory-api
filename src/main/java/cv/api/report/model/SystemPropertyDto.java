package cv.api.report.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class SystemPropertyDto {
    private SystemPropertyKey key;
    private String propValue;
    private String remark;
    private LocalDateTime updatedDate;

    }
