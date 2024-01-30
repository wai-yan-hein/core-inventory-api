package cv.api.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PropertyKey{
    private String propKey;
    private String compCode;
}
