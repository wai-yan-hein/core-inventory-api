package cv.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class LanguageKey implements Serializable {
    @Column(name = "lan_type")
    private String lanType;
    @Column(name = "lan_key")
    private String lanKey;
}
