package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "stock_formula")
public class StockFormula {
    @EmbeddedId
    private StockFormulaKey key;
    @Column(name = "formula_name")
    private String formulaName;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "active")
    private boolean active;
}
