package cv.api.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Embeddable
public class RetOutHisKey implements Serializable {
    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "comp_code")
    private String compCode;
}
