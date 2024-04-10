package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import jakarta.persistence.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "reorder_level")
public class ReorderLevel implements java.io.Serializable {
    @EmbeddedId
    private ReorderKey key;
    @Column(name = "min_qty")
    private Double minQty;
    @Column(name = "min_unit")
    private String minUnitCode;
    @Column(name = "max_qty")
    private Double maxQty;
    @Column(name = "max_unit")
    private String maxUnitCode;
    @Transient
    private Double orderQty;
    @Transient
    private String orderUnit;
    @Transient
    private Double minSmallQty;
    @Transient
    private Double maxSmallQty;
    @Transient
    private Double balSmallQty;
    @Transient
    private String balUnit;
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
    @Transient
    private Integer position;

}
