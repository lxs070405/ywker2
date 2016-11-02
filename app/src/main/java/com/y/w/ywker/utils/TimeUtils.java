package com.y.w.ywker.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lxs on 16/3/11.
 * 时间工具类
 */

public class TimeUtils {

    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat DATE_FORMAT_DATE    = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//yyyy/ :ss

    public static final SimpleDateFormat DATE_FORMAT_DATE_MINUTE    = new SimpleDateFormat("yyyy/MM/dd HH:mm");


    private TimeUtils() {
        throw new AssertionError();
    }

    /**
     * long time to string
     *
     * @param timeInMillis
     * @param dateFormat
     * @return
     */
    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    /**
     * long time to string, format is {@link #DEFAULT_DATE_FORMAT}
     * @param timeInMillis
     * @return
     */
    public static String getTime(long timeInMillis) {
        return getTime(timeInMillis, DATE_FORMAT_DATE);
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

    /**
     * get current time in milliseconds, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @return
     */
    public static String getCurrentTimeInString() {
        return getTime(getCurrentTimeInLong());
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }

    /**
     * 给定一个字符串来判断当前时间
     * @param date
     * @return
     */
    public static String FormatDatestr(String date) {
        if (date.equals("null") || date.equals("")) {
            return "";
        } else {
            Date dNow = new Date(); // 当前时间
            Date dBefore = new Date();
            Calendar calendar = Calendar.getInstance(); // 得到日历
            calendar.setTime(dNow);// 把当前时间赋给日历
            calendar.add(Calendar.DAY_OF_MONTH, -1); // 设置为前一天
            dBefore = calendar.getTime(); // 得到前一天的时间
            SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
            Date d = null;
            try {

                d = formatDate.parse(date);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "";
            }
            if (formatDate.format(dNow).equals(formatDate.format(d))) {
                return "今天";
            } else if (formatDate.format(dBefore).equals(formatDate.format(d))) {
                return "昨天";
            } else {
                return formatDate.format(d);
            }
        }
    }

    public static String setTime(String lastTime){
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(lastTime);
        String _time = "";
        while (matcher.find()){
            _time = matcher.group();
        }
        try {
            long mm = Long.parseLong(_time);
           _time = TimeUtils.daysBetween(mm);
        }catch (Exception e){
            _time = "";
        }

        return  _time;
    }

    public static String frmatTime(String string,String framt){
        String str;
        if(framt.equals("1")){
            str = "yyyy/M/dd HH:mm";//:ss
        }else {
            str = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(str);

        try {
            long millionSeconds = sdf.parse(string).getTime();//毫秒
            if(dayIsToday(millionSeconds)){
                string = "今天"+string.substring(string.indexOf(" "),string.lastIndexOf(":"));
            }else {
                string = daysBetween(millionSeconds);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "未知时间";
        }

        return string;
    }
    //相差几天
    public static String daysBetween(long lastTime){
        long interval=(System.currentTimeMillis() - lastTime)/1000;//秒
        long day = interval/(24*3600);//天
        long hour = interval%(24*3600)/3600;//小时
        long minute = interval%3600/60;//分钟
        long second = interval%60;//秒
        if (day > 0){
            if (day < 7){
                return day + "天前";
            }
            if (day <= 13)
                return "1周前";
            if (day <= 20)
                return "2周前";
            if (day <= 30)
                return "3周前";
            return getTime(lastTime);
        }
        if (hour > 0){
            return hour + "小时前";
        }
        if (minute > 0){
            return minute + "分钟前";
        }
        if (second >= 0){
            return "刚刚";
        }
        return getTime(lastTime);
    }
    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate(Long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");//HH:mm:ss
        String dateString = formatter.format(date);
        return dateString;
    }
    public static boolean dayIsToday(long lastTime){
        long interval=(System.currentTimeMillis() - lastTime)/1000;//秒
        long day = interval/(24*3600);//天
        return day <= 0;
    }

    public static String formatDuring(long mss) {
        long hours = (mss % (1000 * 60 * 60)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        String totaltime = "";
        if (hours > 0){
            totaltime += hours + "小时";
        }

        if (minutes > 0){
            totaltime += minutes + "分";
        }

        if (seconds > 0){
            totaltime += seconds + "秒";
        }
        return totaltime;
    }

    public static List<Integer> getNumArrayIndex(String text){
        Pattern p= Pattern.compile("\\d+");
        Matcher m=p.matcher(text);

        List<Integer> list = new ArrayList<Integer>();
        while(m.find()) {
            list.add(m.start());
            list.add(m.end());
        }
        return list;
    }

    /**
     * 针对易维客的特殊时间串进行处理
     * @param time
     * @return
     */
    public static String formatYwkerDate(String time){
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(time);
        String _time = "";
        while (matcher.find()){
            _time = matcher.group();
        }
        try {
            long mm = Long.parseLong(_time);
            _time = TimeUtils.getTime(mm);
        }catch (Exception e){
            _time = "";
        }
        return _time;
    }

    /**
     * 根据标准时间串转为毫秒数
     * @param time
     * @return
     */
    public static long getTime(String time){
        try {
            return DATE_FORMAT_DATE.parse(time).getTime();
        } catch (ParseException e) {

        }
        return 0;
    }

    public static String getPublicOrderTitleTime(long time){
        return getTime(time,DATE_FORMAT_DATE_MINUTE);
    }
}
