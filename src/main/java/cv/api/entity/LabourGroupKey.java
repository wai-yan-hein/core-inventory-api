package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LabourGroupKey {
    private String code;
    private String compCode;
}
