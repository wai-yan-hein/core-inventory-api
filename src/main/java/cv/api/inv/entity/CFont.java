package cv.api.inv.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "font")
public class CFont {
    @EmbeddedId
    private FontKey fontKey;
    @Column(name = "integrakeycode")
    private Integer intCode;
}
