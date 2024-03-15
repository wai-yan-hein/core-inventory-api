package cv.api.report.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SystemPropertyKey {
    private String propKey;
    private String compCode;


}
