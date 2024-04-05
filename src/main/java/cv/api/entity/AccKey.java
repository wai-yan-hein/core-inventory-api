package cv.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class AccKey {
    private String type;
    private String compCode;

    public AccKey(String type, String compCode) {
        this.type = type;
        this.compCode = compCode;
    }
}
