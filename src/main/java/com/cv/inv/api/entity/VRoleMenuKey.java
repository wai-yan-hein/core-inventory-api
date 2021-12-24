/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author winswe
 */
@Data
@Embeddable
public class VRoleMenuKey implements Serializable {
    @Column(name = "role_code")
    private String roleCode;
    @Column(name = "menu_code")
    private String menuCode;
}
