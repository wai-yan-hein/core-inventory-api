package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RelationKey{
    private String relCode;
    private String compCode;
}
