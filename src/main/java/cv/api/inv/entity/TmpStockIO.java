package cv.api.inv.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "tmp_stock_io_column")
public class TmpStockIO {
    @EmbeddedId
    private TmpStockIOKey key;
    @Column(name = "op_qty")
    private Float opQty;
    @Column(name = "pur_qty")
    private Float purQty;
    @Column(name = "in_qty")
    private Float inQty;
    @Column(name = "sale_qty")
    private Float saleQty;
    @Column(name = "out_qty")
    private Float outQty;
}
