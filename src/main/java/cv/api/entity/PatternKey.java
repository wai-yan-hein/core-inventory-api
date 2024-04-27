package cv.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class PatternKey {
    private String stockCode;
    private String compCode;
    private Integer uniqueId;
    private String mapStockCode;
}
