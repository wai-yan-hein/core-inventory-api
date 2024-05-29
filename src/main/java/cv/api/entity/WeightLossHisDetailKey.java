package cv.api.entity;

import lombok.Builder;
import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Builder
public class WeightLossHisDetailKey {
    private String vouNo;
    private String compCode;
    private Integer uniqueId;
}
