/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import lombok.Builder;
import lombok.Data;

/**
 *
 * @author wai yan
 */
@Data
@Builder
public class MillingOutDetail {

    private MillingOutDetailKey key;
    private String stockCode;
    private Integer deptId;
    private Double qty;
    private String unitCode;
    private Double price;
    private Double amount;
    private String locCode;
    private Double weight;
    private String weightUnit;
    private Double percent;
    private Double totalWeight;
    private Double percentQty;
    private Integer sortId;
    private String userCode;
    private String stockName;
    private String groupName;
    private String brandName;
    private String catName;
    private String relName;
    private String locName;
    private String traderName;
    private Stock stock;
    private String unitName;
    private String weightUnitName;
}
