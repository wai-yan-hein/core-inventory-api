package cv.api.inv.entity;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;

@Data
@Embeddable
public class PHDetailKey {
    private String compCode;
    private Integer deptId;
    private String vouNo;
    private Integer uniqueId;
    private String stockCode;}
