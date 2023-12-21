package cv.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "price_option")
public class PriceOption {
    @EmbeddedId
    private PriceOptionKey key;
    @Column(name = "desp")
    private String description;
    @Column(name = "unique_id")
    private int uniqueId;
    @Column(name = "tran_option")
    private String tranOption;
    @Column(name = "updated_date",columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
}
