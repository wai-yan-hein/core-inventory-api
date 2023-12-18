package cv.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

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
