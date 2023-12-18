package cv.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class FontKey implements java.io.Serializable {
    @Column(name = "winkeycode")
    private Integer winKeyCode;
    @Column(name = "zawgyikeycode")
    private Integer zwKeyCode;
}
