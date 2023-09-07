package cv.api.dao;

import cv.api.entity.OrderStatus;
import cv.api.entity.OrderStatusKey;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface OrderStatusDao {
    OrderStatus save(OrderStatus OrderStatus);

    List<OrderStatus> findAll(String compCode);

    int delete(OrderStatusKey key);

    OrderStatus findById(OrderStatusKey id);

    List<OrderStatus> search(String des);

    List<OrderStatus> unUpload();

    Date getMaxDate();

    List<OrderStatus> getOrderStatus(LocalDateTime updatedDate);
}
