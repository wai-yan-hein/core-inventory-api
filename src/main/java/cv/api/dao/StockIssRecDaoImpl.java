package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.StockIssueReceive;
import cv.api.entity.StockIssueReceiveKey;
import cv.api.model.VStockIO;
import cv.api.model.VStockIssueReceive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
public class StockIssRecDaoImpl extends AbstractDao<StockIssueReceiveKey, StockIssueReceive> implements StockIssRecDao {
    @Override
    public StockIssueReceive save(StockIssueReceive obj) {
        obj.setUpdatedDate(LocalDateTime.now());
        saveOrUpdate(obj, obj.getKey());
        return obj;
    }

    @Override
    public StockIssueReceive findById(StockIssueReceiveKey key) {
        return getByKey(key);
    }

    @Override
    public boolean delete(StockIssueReceiveKey key) {
        StockIssueReceive his = findById(key);
        if (his != null) {
            his.setDeleted(true);
            his.setUpdatedDate(LocalDateTime.now());
            update(his);
        }
        return true;
    }

    @Override
    public boolean restore(StockIssueReceiveKey key) {
        StockIssueReceive his = findById(key);
        if (his != null) {
            his.setDeleted(false);
            his.setUpdatedDate(LocalDateTime.now());
            update(his);
        }
        return true;
    }
}
