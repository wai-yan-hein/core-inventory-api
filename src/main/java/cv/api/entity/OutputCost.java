package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Table(name = "output_cost")
public class OutputCost {
    @EmbeddedId
    private OutputCostKey key;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "name")
    private String name;
    @Column(name = "price")
    private double price;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "active")
    private boolean active;
}
