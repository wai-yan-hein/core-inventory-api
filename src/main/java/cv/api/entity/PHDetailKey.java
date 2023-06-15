package cv.api.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;


@Data
@Embeddable
public class PHDetailKey {
    private String compCode;
    private Integer deptId;
    private String vouNo;
    private Integer uniqueId;
    private String stockCode;
}
