package cv.api.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "price_option")
public class PriceOption {
    @EmbeddedId
    private PriceOptionKey key;
    @Column(name = "desp")
    private String description;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "tran_option")
    private String tranOption;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;
}
