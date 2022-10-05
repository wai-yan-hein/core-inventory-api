package cv.api.inv.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "pattern")
@Data
public class Pattern implements java.io.Serializable {
    @Id
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "qty")
    private Float qty;
    @Column(name = "price")
    private Float price;
    @Column(name = "unit")
    private String unitCode;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "f_stock_code")
    private String mapStockCode;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "dept_id")
    private Integer deptId;

}
