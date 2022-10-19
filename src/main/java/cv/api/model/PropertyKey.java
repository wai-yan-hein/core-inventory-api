package cv.api.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
public class PropertyKey  {
    private String propKey;
    private String compCode;
}
