package cv.api.model;

import lombok.Data;

@Data
public class Department {
    private Integer deptId;
    private String userCode;
    private String deptName;
    private String inventoryQ;
    private String accountQ;

}
