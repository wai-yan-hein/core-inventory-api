/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @author wai yan
 */
@Data
@Builder
public class RetInKey {

    private Integer uniqueId;
    private String compCode;
    private String vouNo;


}
