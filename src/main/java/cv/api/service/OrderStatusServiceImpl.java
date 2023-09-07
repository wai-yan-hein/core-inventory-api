package cv.api.service;

import cv.api.dao.OrderStatusDao;
import cv.api.entity.OrderStatus;
import cv.api.entity.OrderStatusKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class OrderStatusServiceImpl implements OrderStatusService{

    @Autowired
    OrderStatusDao dao;
    @Autowired
    private SeqTableService seqService;
    @Override
    public OrderStatus save(OrderStatus status) {
        if (Objects.isNull(status.getKey().getCode())) {
            Integer macId = status.getMacId();
            String compCode = status.getKey().getCompCode();
            status.getKey().setCode(getOrderStatusCode(macId, compCode));
        }
        return dao.save(status);
    }

    @Override
    public List<OrderStatus> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public int delete(OrderStatusKey key) {
        return dao.delete(key);
    }

    @Override
    public OrderStatus findById(OrderStatusKey key) {
        return dao.findById(key);
    }

    @Override
    public List<OrderStatus> search(String description) {
        return dao.search(description);
    }

    @Override
    public List<OrderStatus> unUpload() {
        return dao.unUpload();
    }

    @Override
    public Date getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public List<OrderStatus> getOrderStatus(LocalDateTime updatedDate) {
        return dao.getOrderStatus(updatedDate);
    }

    private String getOrderStatusCode(Integer macId, String compCode) {
        int seqNo = seqService.getSequence(macId, "OrderStatus", "-", compCode);
        return String.format("%0" + 3 + "d", macId) + "-" + String.format("%0" + 4 + "d", seqNo);
    }
}
