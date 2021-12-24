/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author winswe
 */
@Embeddable
public class PrivilegeKey implements Serializable {

    @Column(name = "role_code")
    private String roleCode;
    @Column(name = "menu_code")
    private String menuCode;

    public PrivilegeKey(String roleCode, String menuCode) {
        this.roleCode = roleCode;
        this.menuCode = menuCode;
    }

    public PrivilegeKey() {
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.roleCode);
        hash = 97 * hash + Objects.hashCode(this.menuCode);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PrivilegeKey other = (PrivilegeKey) obj;
        if (!Objects.equals(this.roleCode, other.roleCode)) {
            return false;
        }
        return Objects.equals(this.menuCode, other.menuCode);
    }

}
