/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Lenovo
 */
@Entity
@Table(name = "acc_setting")
public class AccSetting implements Serializable {

    @Id
    @Column(name = "type", unique = true, nullable = false)
    private String type;
    @Column(name = "dis_acc")
    private String disAccount;
    @Column(name = "pay_acc")
    private String payAccount;
    @Column(name = "tax_acc")
    private String taxAccount;
    @Column(name = "dep_code")
    private String department;
    @Column(name = "source_acc")
    private String soureAccount;
    @Column(name = "bal_acc")
    private String balAccount;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDisAccount() {
        return disAccount;
    }

    public void setDisAccount(String disAccount) {
        this.disAccount = disAccount;
    }

    public String getPayAccount() {
        return payAccount;
    }

    public void setPayAccount(String payAccount) {
        this.payAccount = payAccount;
    }

    public String getTaxAccount() {
        return taxAccount;
    }

    public void setTaxAccount(String taxAccount) {
        this.taxAccount = taxAccount;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSoureAccount() {
        return soureAccount;
    }

    public void setSoureAccount(String soureAccount) {
        this.soureAccount = soureAccount;
    }

    public String getBalAccount() {
        return balAccount;
    }

    public void setBalAccount(String balAccount) {
        this.balAccount = balAccount;
    }

}
