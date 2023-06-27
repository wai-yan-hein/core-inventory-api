package cv.api.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Embeddable
public class VouStatusKey implements Serializable {
    @Column(name = "code")
    private String code;
    @Column(name = "comp_code")
    private String compCode;
}
