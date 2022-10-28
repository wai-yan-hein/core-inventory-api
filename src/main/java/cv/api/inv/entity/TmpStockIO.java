package cv.api.inv.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
public class TmpStockIO {
    private TmpStockIOKey key;
    private Float opQty;
    private Float purQty;
    private Float inQty;
    private Float saleQty;
    private Float outQty;
}
