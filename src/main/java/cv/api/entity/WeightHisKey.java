package cv.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
@Builder
public class WeightHisKey  {
    private String vouNo;
    private String compCode;
}
