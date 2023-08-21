package cv.api.entity;

import lombok.Data;

import jakarta.persistence.*;

@Data
@Embeddable
public class TransferHisKey implements java.io.Serializable {
    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "comp_code")
    private String compCode;
}
