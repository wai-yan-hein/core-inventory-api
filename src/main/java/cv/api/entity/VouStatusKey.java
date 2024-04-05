package cv.api.entity;

import lombok.Builder;
import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Builder
public class VouStatusKey {
    private String code;
    private String compCode;
}
