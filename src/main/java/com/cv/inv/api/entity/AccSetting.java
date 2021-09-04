/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Lenovo
 */
@Data
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
    private String sourceAccount;
    @Column(name = "bal_acc")
    private String balAccount;
}
