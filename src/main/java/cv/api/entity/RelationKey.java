package cv.api.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Embeddable
public class RelationKey implements Serializable {
    @Column(name = "rel_code")
    private String relCode;
    @Column(name = "comp_code")
    private String compCode;
}
