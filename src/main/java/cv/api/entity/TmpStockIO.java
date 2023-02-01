package cv.api.entity;

import lombok.Data;

@Data
public class TmpStockIO {
    private TmpStockIOKey key;
    private Float opQty;
    private Float purQty;
    private Float inQty;
    private Float saleQty;
    private Float outQty;
}
