/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class VStockBalance implements Serializable {

    private String stockCode;
    private String stockName;
    private String locCode;
    private String locationName;
    private Double totalQty;
    private Double weight;
    private String unitName;
    private Double smallestQty;
    private String userCode;

}
