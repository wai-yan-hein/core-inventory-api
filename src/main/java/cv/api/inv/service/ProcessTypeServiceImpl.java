package cv.api.inv.service;

import cv.api.inv.dao.ProcessTypeDao;
import cv.api.inv.entity.ProcessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class ProcessTypeServiceImpl implements ProcessTypeService {
    @Autowired
    private ProcessTypeDao dao;

    @Autowired
    private SeqTableService seqService;

    @Override
    public ProcessType save(ProcessType type) {
        if (Objects.isNull(type.getKey().getProCode())) {
            String compCode = type.getKey().getCompCode();
            type.getKey().setProCode(getPCode(compCode));
        }
        return dao.save(type);
    }

    @Override
    public List<ProcessType> getProcessType(String compCode) {
        return dao.getProcessType(compCode);
    }

    private String getPCode(String compCode) {
        int seqNo = seqService.getSequence(0, "ProcessType", "-", compCode);
        return String.format("%0" + 3 + "d", seqNo);
    }
}
