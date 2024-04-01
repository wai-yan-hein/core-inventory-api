package cv.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ConsignHisKey {
    private String vouNo;
    private String compCode;
}
