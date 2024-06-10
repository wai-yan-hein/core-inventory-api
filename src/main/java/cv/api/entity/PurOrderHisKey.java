package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PurOrderHisKey {
    private String vouNo;
    private String compCode;
}
