/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.Order;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wai yan
 */
@Repository
public class OrderDaoImpl extends AbstractDao<String, Order> implements OrderDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Order save(Order order) {
        persist(order);
        return order;
    }

    @Override
    public int delete(String id) {
        return 0;
    }

    @Override
    public List<Order> findActiveOrder(String fromDate, String toDate, String cusId, String orderCode) {
        String hsql = "select o from Order o ";
        String strFilter = "";
        if (!fromDate.equals("-") && !toDate.equals("-")) {
            strFilter = "date(o.orderDate) between '" + fromDate
                    + "' and '" + toDate + "'";
        } else if (!fromDate.endsWith("-")) {
            strFilter = "date(o.orderDate) >= '" + fromDate + "'";
        } else if (!toDate.equals("-")) {
            strFilter = "date(o.orderDate) <= '" + toDate + "'";
        }
        if (!cusId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.trader= '" + cusId + "'";
            } else {
                strFilter = strFilter + " and o.trader= '" + cusId + "'";
            }
        }
        if (!orderCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.orderCode = '" + orderCode + "'";
            } else {
                strFilter = strFilter + " and o.orderCode= '" + orderCode + "'";
            }
        }
        if (!strFilter.isEmpty()) {
            hsql = hsql + " where " + strFilter + " and o.isOrder = true";
        }
        Query<Order> query = sessionFactory.getCurrentSession().createQuery(hsql, Order.class);
        return query.list();

    }

}
