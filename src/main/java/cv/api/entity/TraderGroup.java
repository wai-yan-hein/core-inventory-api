package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TraderGroup {
    private TraderGroupKey key;
    private Integer deptId;
    private String userCode;
    private String groupName;
    private String account;
    private String intgUpdStatus;
}
