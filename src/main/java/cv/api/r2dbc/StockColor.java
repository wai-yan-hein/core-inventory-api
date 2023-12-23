package cv.api.r2dbc;

import lombok.Builder;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Data
@Builder
public class StockColor {
    private String colorId;
    private String colorName;
    private String compCode;
    private LocalDateTime updatedDate;
}
