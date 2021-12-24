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
public class UsrCompRoleKey implements Serializable {

    private String userCode;
    private String compCode;
    private String roleCode;

    @Column(name = "user_code")
    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    @Column(name = "comp_code")
    public String getCompCode() {
        return compCode;
    }

    public void setCompCode(String compCode) {
        this.compCode = compCode;
    }

    @Column(name = "role_code")
    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.userCode);
        hash = 67 * hash + Objects.hashCode(this.compCode);
        hash = 67 * hash + Objects.hashCode(this.roleCode);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UsrCompRoleKey other = (UsrCompRoleKey) obj;
        if (!Objects.equals(this.userCode, other.userCode)) {
            return false;
        }
        if (!Objects.equals(this.compCode, other.compCode)) {
            return false;
        }
        return Objects.equals(this.roleCode, other.roleCode);
    }

}
