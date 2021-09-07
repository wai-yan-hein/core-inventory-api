/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author WSwe
 */
@Slf4j
public class Util1 {

    public static String getEngChar(int i) {
        String[] engChar = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"};

        if ((i + 1) < 0 || (i + 1) > engChar.length) {
            return null;
        } else {
            return engChar[i + 1];
        }
    }

    public static boolean isNumber(String number) {
        boolean status = false;

        try {
            if (number != null && !number.isEmpty()) {
                double tmp = Double.parseDouble(number);
            }
            status = true;
        } catch (NumberFormatException ex) {
            log.error("NumberUtil.isNumber : " + ex.getMessage());
        }
        return status;
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

    public static Date toJavaDate(Object objDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
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

    public static boolean isDate(String str) {

        return str.length() == 10;
    }

    public static boolean isSameDate(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(d1).equals(sdf.format(d2));
    }

    public static String toDateStrMYSQL(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = null;

        try {
            date = formatter.format(toDate(strDate));
        } catch (Exception ex) {
            log.info("toDateTimeStrMYSQL : " + ex.getMessage());
        }

        return date;
    }

    public static String toDateTimeStrMYSQL(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = null;

        try {
            date = formatter.format(toDate(strDate, "dd/MM/yyyy"));
        } catch (Exception ex) {
            log.info("toDateTimeStrMYSQL : " + ex.getMessage());
        }

        return date;
    }

    public static String toDateStr(String strDate, String inFormat, String outFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(outFormat);
        String date = null;

        try {
            /*if (strDate.contains("-")) {
                date = strDate;
            } else {*/
            date = formatter.format(toDate(strDate, inFormat));
            //}
        } catch (Exception ex) {
            try {
                date = formatter.format(toDate(strDate, outFormat));
            } catch (Exception ex1) {
                log.info("toDateStr : " + ex1.getMessage());
            }
        }

        return date;
    }

    public static String toDateStrMYSQLEnd(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = null;

        try {
            date = formatter.format(toDate(strDate, "dd/MM/yyyy")) + " 23:59:59";
        } catch (Exception ex) {
            log.info("toDateStrMYSQL Error : " + ex.getMessage());
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

    public static String getFileExtension(String content) {
        String extension = "";

        if (content.contains("jpeg")) {
            extension = "jpg";
        } else if (content.contains("gif")) {
            extension = "gif";
        } else if (content.contains("tif")) {
            extension = "tif";
        } else if (content.contains("png")) {
            extension = "png";
        } else if (content.contains("bmp")) {
            extension = "bmp";
        }

        return extension;
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

    public static String toDateStrMYSQL(String strDate, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = null;

        try {
            date = formatter.format(toDate(strDate, format));
        } catch (Exception ex) {
            log.info("toDateTimeStrMYSQL : " + ex.getMessage());
        }

        return date;
    }

    public static String addDateTo(String date, int ttlDay) {
        String output = null;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();

        try {
            c.setTime(toDate(date, "dd/MM/yyyy")); // Now use today date.
            c.add(Calendar.DATE, ttlDay);
            output = formatter.format(c.getTime());
        } catch (Exception ex) {
            log.info("addDateTo : " + ex.getMessage());
        }

        return output;
    }

    public static Date addDateTo(Date date, int ttlDay) {
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        Date tmp = null;

        try {
            //c.setTime(toDate(date, "yyyy-MM-dd")); // Now use today date.
            c.setTime(date);
            c.add(Calendar.DATE, ttlDay);
            tmp = c.getTime();
        } catch (Exception ex) {
            log.info("addDateTo : " + ex.getMessage());
        }

        return tmp;
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

    public static String isNullObj(Object obj, String value) {
        return value;
    }

    public static Date getLastDayOfMonth(String strDate, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(toDate(strDate, format));

        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);

        return calendar.getTime();
    }

    public static int isNullZero(Integer value) {
        return Objects.requireNonNullElse(value, 0);
    }

    public static double nullZero(String value) {
        if (value == null) {
            return 0;
        }

        if (value.isEmpty()) {
            return 0;
        }

        return Double.parseDouble(value);
    }

    public static String getPeriod(String strDate, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMyyyy");
        String strPeriod = null;
        Date date = toDate(strDate, format);

        if (date != null) {
            strPeriod = formatter.format(date);
        }

        return strPeriod;
    }

    public static String getPeriod(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMyyyy");
        String strPeriod = null;

        if (date != null) {
            strPeriod = formatter.format(date);
        }

        return strPeriod;
    }

    public static Double getDouble(Object number) {
        double value = 0.0;
        if (number != null) {
            if (!number.toString().isEmpty()) {
                value = Double.parseDouble(number.toString());
            }
        }
        return value;
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

    public static Long getLong(Object number) {
        long value = 0;
        if (number != null) {
            value = Long.parseLong(number.toString());
        }
        return value;
    }

    public static Integer getInteger(Object obj) {
        return obj != null ? Integer.parseInt(obj.toString()) : 0;
    }

    public static String getString(String str) {
        String value = "";
        if (str != null) {
            value = str;
        }
        return value;
    }

    public static String getString(Object obj) {
        String value = "";
        if (obj != null) {
            value = obj.toString();
        }
        return value;
    }

    public static String getStringValue(Object obj) {
        String value = "";
        if (obj != null) {
            value = obj.toString();
        }
        return value;
    }

    public static boolean getBoolean(Boolean obj) {
        if (obj == null) {
            obj = false;
        }
        return obj;

    }

    public static int getCurrentMonth() {
        LocalDate currentdate = LocalDate.now();
        return currentdate.getMonth().getValue();

    }

    public static boolean isValidDateFormat(Object dateStr, String dateFromat) {
        boolean status = true;
        DateFormat formatter = new SimpleDateFormat(dateFromat);
        if (isDate(dateStr.toString())) {
            try {
                formatter.parse(dateStr.toString());
            } catch (ParseException ex) {
                log.info("isValidDateFormat Error : " + ex.getMessage());
                status = false;

            }

        } else {
            status = false;
        }
        return status;
    }

    public static String toFormatDate(String obj) {
        String[] arr;
        arr = obj.split("(?<=\\G.{2})");

        return arr[0] + "/" + arr[1] + "/" + arr[2] + arr[3];

    }

    public static JDialog getLoading(JDialog owner, ImageIcon icon) {
        JDialog dialog = new JDialog(owner, false);
        dialog.getContentPane().setBackground(Color.white);
        dialog.setSize(70, 70);
        dialog.getContentPane().setLayout(new BorderLayout());
        JLabel lblImg = new JLabel(icon);
        lblImg.setLocation(70, 0);
        dialog.add(lblImg);
        dialog.getContentPane().add(lblImg, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(null);
        dialog.setUndecorated(true);
        dialog.validate();
        return dialog;
    }

    public static JDialog getLoading(JFrame owner, ImageIcon icon) {
        JDialog dialog = new JDialog(owner, false);
        dialog.getContentPane().setBackground(Color.white);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(70, 70);
        dialog.getContentPane().setLayout(new BorderLayout());
        JLabel lblImg = new JLabel(icon);
        lblImg.setLocation(70, 0);
        dialog.add(lblImg);
        dialog.getContentPane().add(lblImg, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(null);
        dialog.setUndecorated(true);
        dialog.validate();
        return dialog;
    }

    public static Dimension getScreenSize() {
        //Calculate dialog position to centre.
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        return toolkit.getScreenSize();
    }

    public static String getComputerName() {
        String computerName = "";

        try {
            computerName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.info("getComputerName : " + e);
        }

        return computerName;
    }

    public static String getIPAddress() {
        String iPAddress = "";

        try {
            iPAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.info("getIPAddress : " + e);
        }

        return iPAddress;
    }

    public static String toFormatPattern(Double value) {
        final String pattern = "#,##0.###;(#,##0.###)";
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
        df.applyPattern(pattern);
        return df.format(value);

    }

    public static void writeJsonFile(Object data, String exportPath) throws IOException {
        try ( Writer writer = new FileWriter(exportPath)) {
            Gson gson = new GsonBuilder().serializeNulls().create();
            gson.toJson(data, writer);
        }
    }
}
