package cv.api.entity;

import lombok.Data;


@Data
public class PHDetailKey {
    private String compCode;
    private Integer deptId;
    private String vouNo;
    private int uniqueId;
    private String stockCode;
}
