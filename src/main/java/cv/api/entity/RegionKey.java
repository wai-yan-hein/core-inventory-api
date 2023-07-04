package cv.api.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Embeddable
public class RegionKey implements Serializable {
    @Column(name = "reg_code")
    private String regCode;
    @Column(name = "comp_code")
    private String compCode;
}
