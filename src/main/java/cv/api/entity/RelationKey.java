package cv.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class RelationKey implements Serializable {
    @Column(name = "rel_code")
    private String relCode;
    @Column(name = "comp_code")
    private String compCode;
}
