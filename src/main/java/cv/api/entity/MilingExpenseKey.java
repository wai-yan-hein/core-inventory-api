/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cv.api.entity;

import jakarta.persistence.Column;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MilingExpenseKey implements Serializable {

    @Column(name = "expense_code")
    private String expenseCode;
    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "unique_id")
    private Integer uniqueId;
}
