package cv.api.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

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
    @Column(name = "tran_type")
    private String tranOption;
}
