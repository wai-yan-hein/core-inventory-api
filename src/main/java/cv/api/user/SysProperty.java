package cv.api.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Builder
public class SysProperty {

    private PropertyKey key;
    private String propValue;
    private String remark;
    private LocalDateTime updatedDate;
}