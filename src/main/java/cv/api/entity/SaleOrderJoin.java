package cv.api.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "sale_order_join")
public class SaleOrderJoin {
    @EmbeddedId
    private SaleOrderJoinKey key;
}
