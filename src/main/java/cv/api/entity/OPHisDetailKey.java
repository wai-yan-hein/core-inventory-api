package cv.api.entity;

import lombok.Builder;
import lombok.Data;

import jakarta.persistence.*;

@Data
@Builder
public class OPHisDetailKey {
    private String vouNo;
    private Integer uniqueId;
    private String compCode;

}
