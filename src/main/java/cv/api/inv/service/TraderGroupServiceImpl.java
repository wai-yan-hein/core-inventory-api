package cv.api.inv.service;

import cv.api.common.Util1;
import cv.api.inv.dao.TraderGroupDao;
import cv.api.inv.entity.TraderGroup;
import cv.api.inv.entity.TraderGroupKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TraderGroupServiceImpl implements TraderGroupService {
    @Autowired
    private TraderGroupDao dao;
    @Autowired
    private SeqTableService seqService;

    @Override
    public TraderGroup save(TraderGroup group) {
        if (Util1.isNullOrEmpty(group.getKey().getGroupCode())) {
            group.getKey().setGroupCode(getGroupCode(group.getKey().getCompCode()));
        }
        group.setIntgUpdStatus(null);
        return dao.save(group);
    }

    @Override
    public List<TraderGroup> getTraderGroup(String compCode, Integer deptId) {
        return dao.getTraderGroup(compCode, deptId);
    }

    @Override
    public List<TraderGroup> unUpload() {
        return dao.unUpload();
    }

    @Override
    public TraderGroup findById(TraderGroupKey key) {
        return dao.findById(key);
    }

    private String getGroupCode(String compCode) {
        int seqNo = seqService.getSequence(0, "TraderGroup", "-", compCode);
        return String.format("%0" + 5 + "d", seqNo);
    }
}
