/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Lenovo
 */
@Embeddable
public class RoleDefaultKey implements Serializable {

    @Column(name = "default_key", unique = true, nullable = false)
    private String key;
    @Column(name = "role_code")
    private String roleCode;

    public RoleDefaultKey(String key, String roleCode) {
        this.key = key;
        this.roleCode = roleCode;
    }

    public RoleDefaultKey() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.key);
        hash = 47 * hash + Objects.hashCode(this.roleCode);
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
        final RoleDefaultKey other = (RoleDefaultKey) obj;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        if (!Objects.equals(this.roleCode, other.roleCode)) {
            return false;
        }
        return true;
    }

}
