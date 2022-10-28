package cv.api.inv.entity;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class ProcessHisKey implements Serializable {
    private String compCode;
    private Integer deptId;
    private String vouNo;
    private String stockCode;
}
