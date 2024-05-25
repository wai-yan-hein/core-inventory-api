package cv.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class MillingUsageKey {
    private String vouNo;
    private String compCode;
    private Integer uniqueId;
}
