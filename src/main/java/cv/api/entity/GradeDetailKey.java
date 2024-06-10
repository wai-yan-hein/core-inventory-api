package cv.api.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class GradeDetailKey implements Serializable {
    private String formulaCode;
    private String compCode;
    private String criteriaCode;
    private int uniqueId;
}
