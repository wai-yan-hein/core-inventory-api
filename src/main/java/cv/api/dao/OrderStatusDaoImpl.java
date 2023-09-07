package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.OrderStatus;
import cv.api.entity.OrderStatusKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@Repository
public class OrderStatusDaoImpl extends AbstractDao<OrderStatusKey, OrderStatus> implements OrderStatusDao{
    @Override
    public OrderStatus save(OrderStatus orderStatus) {
        saveOrUpdate(orderStatus, orderStatus.getKey());
        return orderStatus;
    }

    @Override
    public List<OrderStatus> findAll(String compCode) {
        String hsql = "select o from OrderStatus o where o.key.compCode = '" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public int delete(OrderStatusKey key) {
        remove(key);
        return 1;
    }

    @Override
    public OrderStatus findById(OrderStatusKey id) {
        return getByKey(id);
    }

    @Override
    public List<OrderStatus> search(String des) {
        String strSql = "";

        if (!des.equals("-")) {
            strSql = "o.statusDesp like '%" + des + "%'";
        }

        if (strSql.isEmpty()) {
            strSql = "select o from OrderStatus o";
        } else {
            strSql = "select o from OrderStatus o where " + strSql;
        }

        return findHSQL(strSql);
    }

    @Override
    public List<OrderStatus> unUpload() {
        String hsql = "select o from OrderStatus o where o.intgUpdStatus is null";
        return findHSQL(hsql);
    }

    @Override
    public Date getMaxDate() {
        String sql = "select max(updated_date) date from order_status";
        ResultSet rs = getResult(sql);
        try {
            if (rs.next()) {
                Date date = rs.getTimestamp("date");
                if (date != null) {
                    return date;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Util1.getOldDate();
    }

    @Override
    public List<OrderStatus> getOrderStatus(LocalDateTime updatedDate) {
        String hsql = "select o from OrderStatus o where o.updatedDate > :updatedDate";
        return createQuery(hsql).setParameter("updatedDate", updatedDate).getResultList();
    }
}
