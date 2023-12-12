package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Data
@Entity(name = "language")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Slf4j
public class Language {
    @EmbeddedId
    private LanguageKey key;
    @Column(name = "lan_value")
    private String lanValue;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
}
