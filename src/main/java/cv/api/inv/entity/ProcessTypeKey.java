package cv.api.inv.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Data
public class ProcessTypeKey implements java.io.Serializable {
    @Column(name = "pt_code")
    private String proCode;
    @Column(name = "comp_code")
    private String compCode;
}
