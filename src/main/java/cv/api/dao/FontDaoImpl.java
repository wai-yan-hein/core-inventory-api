package cv.api.dao;

import cv.api.entity.CFont;
import cv.api.entity.FontKey;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FontDaoImpl extends AbstractDao<FontKey, CFont> implements FontDao {
    @Override
    public List<CFont> getFont() {
        String hsql = "select o from CFont o";
        return findHSQL(hsql);
    }
}
