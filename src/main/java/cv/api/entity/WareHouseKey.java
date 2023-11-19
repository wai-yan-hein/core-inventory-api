package cv.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import lombok.Data;

import java.io.Serializable;
@Data
@Embeddable
public class WareHouseKey implements Serializable {
    private String code;
    private String compCode;
}
