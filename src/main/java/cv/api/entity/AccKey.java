package cv.api.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
@Data
@Embeddable
public class AccKey implements Serializable {
    @Column(name = "type")
    private String type;
    @Column(name = "comp_code")
    private String compCode;

    public AccKey() {
    }

    public AccKey(String type, String compCode) {
        this.type = type;
        this.compCode = compCode;
    }

}
