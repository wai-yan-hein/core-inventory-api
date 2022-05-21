/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.common;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author WSwe
 */
@Slf4j
public class Util1 {
    public static final String DECIMAL_FORMAT = "##0.0";

    public static boolean getBoolean(String obj) {
        boolean status = false;
        if (!Util1.isNull(obj)) {
            status = obj.equals("1") || obj.equalsIgnoreCase("true");
        }
        return status;

    }
    public static boolean isNullOrEmpty(Object obj) {
        return obj == null || obj.toString().isEmpty();
    }


    public static Date toDate(Object objDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;

        try {
            if (objDate != null) {
                date = formatter.parse(objDate.toString());
            }
        } catch (ParseException ex) {
            log.info("toDateStr Error : " + ex.getMessage());
        }

        return date;
    }

    public static Date toDate(Object objDate, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date = null;

        try {
            date = formatter.parse(objDate.toString());
        } catch (ParseException ex) {
            try {
                formatter = new SimpleDateFormat("yyyy-MM-dd");
                date = formatter.parse(objDate.toString());
            } catch (ParseException ex1) {
                log.info("toDateStr Error : " + ex1.getMessage());
            }
        }

        return date;
    }

    public static String toDateStr(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String strDate = null;

        try {
            strDate = formatter.format(date);
        } catch (Exception ex) {
            System.out.println("toDateStr Error : " + ex.getMessage());
        }

        return strDate;
    }

    public static Date getTodayDate() {
        return Calendar.getInstance().getTime();
    }

    public static String isNull(String strValue, String value) {
        if (strValue == null) {
            return value;
        } else if (strValue.isEmpty()) {
            return value;
        } else {
            return strValue;
        }
    }

    public static boolean isNull(String value) {
        boolean status = false;
        if (value == null) {
            status = true;
        } else if (value.isBlank()) {
            status = true;
        }
        return status;
    }

    public static Float getFloat(Object number) {
        float value = 0.0f;
        if (number != null) {
            if (!number.toString().isEmpty()) {
                value = Float.parseFloat(number.toString());
            }
        }
        return value;
    }

    public static Integer getInteger(Object obj) {
        return obj != null ? Integer.parseInt(obj.toString()) : 0;
    }
    public static boolean getBoolean(Boolean obj) {
        if (obj == null) {
            obj = false;
        }
        return obj;

    }

    public static void writeJsonFile(Object data, String exportPath) throws IOException {
        try (Writer writer = new FileWriter(exportPath, StandardCharsets.UTF_8)) {
            //Gson gson = new GsonBuilder().serializeNulls().create();
            Gson gson = new Gson();
            gson.toJson(data, writer);
        }
    }
}
