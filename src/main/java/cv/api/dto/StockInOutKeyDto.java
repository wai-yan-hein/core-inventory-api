/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wai yan
 */
@Data
@Builder
public class StockInOutKeyDto {

    private String vouNo;
    private Integer uniqueId;
    private String compCode;
}
