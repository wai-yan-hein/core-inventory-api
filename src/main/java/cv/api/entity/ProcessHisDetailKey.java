package cv.api.entity;

import lombok.Builder;
import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Builder
public class ProcessHisDetailKey implements Serializable {
    private String vouNo;
    private String stockCode;
    private String locCode;
    private String compCode;
    private Integer deptId;
    private int uniqueId;
}
