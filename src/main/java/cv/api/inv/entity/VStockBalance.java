/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wai yan
 */
@Data
public class VStockBalance implements Serializable {

    private String stockCode;
    private String stockName;
    private String locCode;
    private String locationName;
    private Float totalQty;
    private Float weight;
    private String unitName;
    private Float smallestQty;

}
