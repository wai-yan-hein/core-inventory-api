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
    private String openBagRel;
    private Double purBag;
    private String purBagRel;
    private Double inBag;
    private String inBagRel;
    private Double outBag;
    private String outBagRel;
    private Double saleBag;
    private String saleBagRel;
    private Double balBag;
    private String balBagRel;

    private Double openWet;
    private String openWetRel;
    private Double purWet;
    private String purWetRel;
    private Double inWet;
    private String inWetRel;
    private Double outWet;
    private String outWetRel;
    private Double saleWet;
    private String saleWetRel;
    private Double balWet;
    private String balWetRel;

    private Double openRice;
    private String openRiceRel;
    private Double purRice;
    private String purRiceRel;
    private Double inRice;
    private String inRiceRel;
    private Double outRice;
    private String outRiceRel;
    private Double saleRice;
    private String saleRiceRel;
    private Double balRice;
    private String balRiceRel;

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
//    private Double wet;
//    private Double rice;
    private Double bag;
    private Double price;
    private String warehouse;
    private String tranOption;
}
