/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cv.api.entity;

import lombok.Builder;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Builder
@Data
public class LandingHisGradeKey {

    private String vouNo;
    private String compCode;
    private Integer uniqueId;

}
