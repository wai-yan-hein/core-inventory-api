package cv.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "weight_his_detail")
public class WeightHisDetail  {
    @EmbeddedId
    private WeightHisDetailKey key;
    @Column(name = "weight")
    private double weight;
}
