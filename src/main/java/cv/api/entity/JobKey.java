package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobKey {
    private String jobNo;
    private String compCode;
}
