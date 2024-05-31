package cv.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PriceOption {
    private PriceOptionKey key;
    private String description;
    private int uniqueId;
    private String tranOption;
    private LocalDateTime updatedDate;
}
