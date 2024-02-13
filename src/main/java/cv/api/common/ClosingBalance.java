package cv.api.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class ClosingBalance {
    private String typeUserCode;
    private String typeName;
    private String catName;
    private String stockUsrCode;
    private String stockCode;
    private String stockName;
    private String vouNo;
    private String vouDate;
    private Double openQty;
    private Double openAmt;
    private String openRel;
    private Double purQty;
    private Double purAmt;
    private String purRel;
    private Double inQty;
    private Double inAmt;
    private String inRel;
    private Double outQty;
    private Double outAmt;
    private String outRel;
    private Double saleQty;
    private Double saleAmt;
    private String saleRel;
    private Double balQty;
    private Double closingAmt;
    private String balRel;

    private Double openWeight;
    private String openWeightRel;
    private Double purWeight;
    private String purWeightRel;
    private Double inWeight;
    private String inWeightRel;
    private Double outWeight;
    private String outWeightRel;
    private Double saleWeight;
    private String saleWeightRel;
    private Double balWeight;
    private String balWeightRel;

    private Double openBag;
    private Double openBagTotal;
    private Double purBag;
    private Double purBagTotal;
    private Double inBag;
    private Double inBagTotal;
    private Double outBag;
    private Double outBagTotal;
    private Double saleBag;
    private Double saleBagTotal;
    private Double balBag;
    private Double balBagTotal;

    private Double openWet;
    private Double openWetTotal;
    private Double purWet;
    private Double purWetTotal;
    private Double inWet;
    private Double inWetTotal;
    private Double outWet;
    private Double outWetTotal;
    private Double saleWet;
    private Double saleWetTotal;
    private Double balWet;
    private Double balWetTotal;

    private Double openRice;
    private Double openRiceTotal;
    private Double purRice;
    private Double purRiceTotal;
    private Double inRice;
    private Double inRiceTotal;
    private Double outRice;
    private Double outRiceTotal;
    private Double saleRice;
    private Double saleRiceTotal;
    private Double balRice;
    private Double balRiceTotal;

    private String groupName;
    private String remark;
    private String compCode;
    private Integer deptId;
    private String relCode;
    private String weightUnit;
    private String traderCode;
    private String traderUserCode;
    private String traderName;
    private String locName;
    private Double bag;
    private Double price;
    private String warehouse;
    private String tranOption;

    private Double opPrice;
    private Double opPriceTotal;
    private Double purPrice;
    private Double purPriceTotal;
    private Double inPrice;
    private Double inPriceTotal;
    private Double outPrice;
    private Double salePrice;
    private Double clPrice;

}
