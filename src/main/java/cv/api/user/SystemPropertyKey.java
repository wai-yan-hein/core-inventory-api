package cv.api.user;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SystemPropertyKey {
    private String propKey;
    private String compCode;


}
