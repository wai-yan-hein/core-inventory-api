package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationKey {
    private String locCode;
    private String compCode;
}
