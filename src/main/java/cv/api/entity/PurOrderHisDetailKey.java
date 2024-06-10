package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PurOrderHisDetailKey {
    private String vouNo;
    private String compCode;
    private int uniqueId;
}
