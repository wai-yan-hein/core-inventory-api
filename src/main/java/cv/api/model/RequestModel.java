package cv.api.model;

import cv.api.inv.entity.Location;
import cv.api.inv.entity.LocationKey;
import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
public class RequestModel {
    private List<LocationKey> keys;
    private Date updatedDate;

    public RequestModel(List<LocationKey> keys, Date updatedDate) {
        this.keys = keys;
        this.updatedDate = updatedDate;
    }
}
