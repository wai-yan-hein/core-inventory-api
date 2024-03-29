package cv.api.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VDescription {
    private String description;
    private String compCode;
}
