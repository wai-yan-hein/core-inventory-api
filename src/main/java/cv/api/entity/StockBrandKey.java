package cv.api.entity;

import lombok.Builder;
import lombok.Data;

import jakarta.persistence.*;

import java.io.Serializable;

@Data
@Builder
public class StockBrandKey {
    private String brandCode;
    private String compCode;
}
