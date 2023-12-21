package cv.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class LocationKey implements Serializable {
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "comp_code")
    private String compCode;

}
