package cv.api.entity;

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
