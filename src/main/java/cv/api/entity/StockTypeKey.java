package cv.api.entity;

import lombok.Builder;
import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Builder
public class StockTypeKey {
    private String stockTypeCode;
    private String compCode;
}
