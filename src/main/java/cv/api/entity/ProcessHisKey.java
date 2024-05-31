package cv.api.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class ProcessHisKey implements Serializable {
    private String vouNo;
    private String compCode;
    private Integer deptId;
    private String stockCode;
    private String locCode;
}
