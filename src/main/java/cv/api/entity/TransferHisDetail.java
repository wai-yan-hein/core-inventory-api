package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import jakarta.persistence.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "transfer_his_detail")
public class TransferHisDetail {
    @EmbeddedId
    private THDetailKey key;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "qty")
    private Float qty;
    @Column(name = "unit")
    private String unitCode;
    @Column(name = "weight")
    private Float weight;
    @Column(name = "weight_unit")
    private String weightUnit;
    @Column(name = "total_weight")
    private Float totalWeight;
    @Transient
    private String userCode;
    @Transient
    private String stockName;
    @Transient
    private String groupName;
    @Transient
    private String brandName;
    @Transient
    private String catName;
    @Transient
    private String relName;
    @Transient
    private String locName;
}
