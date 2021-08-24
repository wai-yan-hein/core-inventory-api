/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Lenovo
 */
@Entity
@Table(name = "role_setting")
public class RoleDefault implements Serializable {

    @EmbeddedId
    private RoleDefaultKey key;
    @Column(name = "default_value")
    private String value;

    public RoleDefaultKey getKey() {
        return key;
    }

    public void setKey(RoleDefaultKey key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
