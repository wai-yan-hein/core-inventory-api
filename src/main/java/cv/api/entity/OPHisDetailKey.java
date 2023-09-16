package cv.api.entity;

import lombok.Data;

import jakarta.persistence.*;

@Data
@Embeddable
public class OPHisDetailKey implements java.io.Serializable {
    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "unique_id")
    private int uniqueId;
    @Column(name = "comp_code")
    private String compCode;

}
