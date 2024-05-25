package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GRNDetailKey {
    private String vouNo;
    private Integer uniqueId;
    private String compCode;
}
