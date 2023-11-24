package cv.api.service;

import cv.api.dao.LabourGroupDao;
import cv.api.dao.WareHouseDao;
import cv.api.entity.OutputCost;
import cv.api.entity.WareHouse;
import cv.api.entity.LabourGroupKey;
import cv.api.entity.WareHouseKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class WareHouseServiceImpl implements WareHouseService{

    private final WareHouseDao dao;
    private final SeqTableService seqService;
    @Override
    public WareHouse save(WareHouse status) {
        if (Objects.isNull(status.getKey().getCode())) {
            String compCode = status.getKey().getCompCode();
            status.getKey().setCode(getCode(compCode));
        }
        return dao.save(status);
    }

    @Override
    public List<WareHouse> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public int delete(WareHouseKey key) {
        return dao.delete(key);
    }

    @Override
    public WareHouse findById(WareHouseKey key) {
        return dao.findById(key);
    }
    @Override
    public Date getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public List<WareHouse> getWarehouse(LocalDateTime updatedDate) {
        return dao.getWareHouse(updatedDate);
    }

    private String getCode(String compCode) {
        int seqNo = seqService.getSequence(0, "WareHouse", "-", compCode);
        return String.format("%0" + 3 + "d", seqNo);
    }
}
