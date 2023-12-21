package cv.api.service;

import cv.api.dao.LabourGroupDao;
import cv.api.entity.LabourGroup;
import cv.api.entity.LabourGroupKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class LabourGroupServiceImpl implements LabourGroupService{

    @Autowired
    private LabourGroupDao dao;
    @Autowired
    private SeqTableService seqService;
    @Override
    public LabourGroup save(LabourGroup status) {
        if (Objects.isNull(status.getKey().getCode())) {
            String compCode = status.getKey().getCompCode();
            status.getKey().setCode(getCode(compCode));
        }
        return dao.save(status);
    }

    @Override
    public List<LabourGroup> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public int delete(LabourGroupKey key) {
        return dao.delete(key);
    }

    @Override
    public LabourGroup findById(LabourGroupKey key) {
        return dao.findById(key);
    }
    @Override
    public Date getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public List<LabourGroup> getLabourGroup(LocalDateTime updatedDate) {
        return dao.getLabourGroup(updatedDate);
    }

    private String getCode(String compCode) {
        int seqNo = seqService.getSequence(0, "LabourGroup", "-", compCode);
        return String.format("%0" + 3 + "d", seqNo);
    }
}