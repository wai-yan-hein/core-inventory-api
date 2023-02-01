package cv.api.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@Embeddable
public class FontKey implements java.io.Serializable {
    @Column(name = "winkeycode")
    private Integer winKeyCode;
    @Column(name = "zawgyikeycode")
    private Integer zwKeyCode;
}
