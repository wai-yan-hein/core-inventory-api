/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Lenovo
 */
@Data
@Entity
@Table(name = "order_his")
public class Order implements Serializable {

    @Id
    @Column(name = "order_code", unique = true, nullable = false)
    private String orderCode;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "order_date")
    private Date orderDate;
    @Column(name = "description")
    private String description;
    @ManyToOne
    @JoinColumn(name = "cus_code")
    private Trader trader;
    @Column(name = "order_status")
    private Boolean isOrder;
    @Column(name = "order_total")
    private Float orderTotal;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;
    @Column(name = "order_address")
    private String orderAddress;

}
