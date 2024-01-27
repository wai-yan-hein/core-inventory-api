package cv.api.service;


import cv.api.entity.OrderStatus;
import cv.api.entity.OrderStatusKey;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface OrderStatusService {
    OrderStatus save(OrderStatus status);
    List<OrderStatus> findAll(String compCode);
    int delete(OrderStatusKey key);
    OrderStatus findById(OrderStatusKey key);
    List<OrderStatus> search(String description);
    List<OrderStatus> unUpload();
    List<OrderStatus> getOrderStatus(LocalDateTime updatedDate);
}
