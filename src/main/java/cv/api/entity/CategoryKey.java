package cv.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class CategoryKey implements Serializable {
    @Column(name = "cat_code")
    private String catCode;
    @Column(name = "comp_code")
    private String compCode;
}
