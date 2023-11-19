package cv.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class OutputCostKey implements Serializable {
    @Column(name = "code")
    private String outputCostCode;
    @Column(name = "comp_code")
    private String compCode;

}
