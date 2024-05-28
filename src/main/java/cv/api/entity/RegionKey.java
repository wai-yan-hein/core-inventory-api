package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegionKey {
    private String regCode;
    private String compCode;
}
