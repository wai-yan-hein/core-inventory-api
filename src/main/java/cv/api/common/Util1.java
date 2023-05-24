/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.myanmartools.TransliterateZ2U;
import com.google.myanmartools.ZawgyiDetector;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * @author WSwe
 */
@Slf4j
public class Util1 {
    public static final String DECIMAL_FORMAT = "##0.##";
    private static final DecimalFormat df0 = new DecimalFormat("0");

    private static final Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
    public static String SYNC_DATE;
    private static final char[] password = {'c', 'o', 'r', 'e', 'v', 'a', 'l', 'u', 'e'};

    public static <T> Object cast(Object from, Class<T> to) {
        return gson.fromJson(gson.toJson(from), to);
    }

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

    public static Date toDateTime(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat f2 = new SimpleDateFormat("dd/MM/yyyy");
        LocalDateTime now = LocalDateTime.now();
        String strDate = f2.format(date) + " " + now.getHour() + ":" + now.getMinute() + ":" + now.getSecond();
        try {
            date = formatter.parse(strDate);
        } catch (ParseException ex) {
            log.error(String.format("toDateTime: %s", ex.getMessage()));
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

    public static String toDateTimeStr(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

    public static Date getTodayDate() {
        return Calendar.getInstance().getTime();
    }

    public static Date getSyncDate() {
        return Util1.toDate(SYNC_DATE);
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

    public static double getDouble(Object number) {
        double value = 0.0;
        if (number != null) {
            if (!number.toString().isEmpty()) {
                value = Double.parseDouble(number.toString());
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
            gson.toJson(data, writer);
        }
    }

    public static void extractZipToJson(byte[] zipData, String exportPath) {
        try {
            File file = new File(exportPath.concat(".zip"));
            try (FileOutputStream stream = new FileOutputStream(file)) {
                stream.write(zipData);
            }
            try (ZipFile zf = new ZipFile(exportPath.concat(".zip"))) {
                if (zf.isEncrypted()) {
                    zf.setPassword(password);
                }
                zf.extractAll("temp");
            }
        } catch (IOException ex) {
            log.error("extractZipToJson : " + ex.getMessage());
        }
    }

    public static byte[] zipJsonFile(String exportPath) throws IOException {
        String zipPath = exportPath.replace(".json", ".zip");
        File file = new File(exportPath);
        try (ZipFile fr = new ZipFile(zipPath, password)) {
            fr.addFile(file, zipParameters());
        }
        FileInputStream stream = new FileInputStream(zipPath);
        byte[] data = stream.readAllBytes();
        stream.close();
        return data;
    }

    public static ZipParameters zipParameters() {
        ZipParameters p = new ZipParameters();
        p.setEncryptFiles(true);
        p.setCompressionLevel(CompressionLevel.HIGHER);
        p.setEncryptionMethod(EncryptionMethod.AES);
        return p;
    }

    public static Date getOldDate() {
        return Util1.toDate("1998-10-07");
    }

    public static boolean isZGText(String str) {
        if (!Util1.isNullOrEmpty(str)) {
            ZawgyiDetector zd = new ZawgyiDetector();
            Double score = zd.getZawgyiProbability(str);
            return getBoolean(df0.format(score));
        }
        return false;
    }

    public static String convertToUniCode(String str) {
        if (!Util1.isNullOrEmpty(str)) {
            TransliterateZ2U z2U = new TransliterateZ2U("Zawgyi to Unicode");
            return z2U.convert(str);
        }
        return str;
    }

    public static Float toNull(float value) {
        return value == 0 ? null : value;
    }

    public static String cleanStr(String str) {
        return str;
    }

    public static String isAll(String value) {
        if (value != null) {
            if (value.equals("All")) {
                return "-";
            }
        }
        return Util1.isNull(value, "-");
    }
}
