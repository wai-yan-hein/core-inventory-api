package cv.api.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class LabourPayment {
    @EmbeddedId
    private LabourPaymentKey key;
    private LocalDateTime vouDate;
    private String labourGroupCode;
    private String curCode;
    private String remark;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String createdBy;
    private String updatedBy;
    private boolean deleted;
    private int macId;

}
