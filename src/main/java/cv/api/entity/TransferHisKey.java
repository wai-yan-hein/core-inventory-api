package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TransferHisKey {
    private String vouNo;
    private String compCode;
}
