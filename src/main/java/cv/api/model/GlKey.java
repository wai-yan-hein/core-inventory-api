package cv.api.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class GlKey {
    private String glCode;
    private String compCode;
    private Integer deptId;
}
