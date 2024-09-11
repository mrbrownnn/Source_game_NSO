package com.nsoz.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.nsoz.map.TileMap;
import com.nsoz.map.zones.Zone;
import com.nsoz.map.MapManager;
import com.nsoz.server.Server;

import com.nsoz.map.Map;
import com.nsoz.server.Config;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.time4j.PlainDate;
import net.time4j.calendar.ChineseCalendar;

/**
 *
 * @author pika
 */
public class NinjaUtils {

    private static final Random rand = new Random();
    private static final NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi"));
    private static final DateFormat dateFormatWeek = new SimpleDateFormat("yyyy-MM-ww");

    public static int nextInt(int max) {
        return rand.nextInt(max);
    }

    public static Object randomObject(Object... array) {
        return array[nextInt(array.length)];
    }

    public static int nextInt(int[] list) {
        return list[nextInt(list.length)];
    }

    public static boolean isLunarCalendar(int day, int month) {
        PlainDate gregorian = PlainDate.nowInSystemTime();
        ChineseCalendar cc = gregorian.transform(ChineseCalendar.axis());
        return (cc.getDayOfMonth() == day && cc.getMonth().getNumber() == month);
    }

    public static boolean isTrungThu() {
        return isLunarCalendar(15, 8);
    }

    public static String getCurrency(long number) {
        return numberFormat.format(number);
    }

    public static String replace(String text, String regex, String replacement) {
        return text.replace(regex, replacement);
    }

    public static boolean checkExist(ArrayList<Integer> array, int number) {
        for (int i : array) {
            if (i == number) {
                return true;
            }
        }
        return false;
    }

    public static boolean inArray(Object key, Object ...keys) {
        return Arrays.asList(keys).contains(key);
    }

    public static int randomWithRate(int[] percent, int max) {
        int next = nextInt(max), i = 0;

            for (i = 0; i < percent.length; i++) {
                if (next < percent[i]) {
                    return i;
                }
                next -= percent[i];
            }
            return i;

    }

    public static int randomWithRate(int[] percent) {
        int sum = (int) sum(percent);
        int next = nextInt(sum), i;
        for (i = 0; i < percent.length; i++) {
            if (next < percent[i]) {
                return i;
            }
            next -= percent[i];
        }
        return i;
    }

    public static boolean nextBoolean() {
        return nextInt(2) == 0;
    }

    public static byte randomZoneId(int mapId) {
        Map map = MapManager.getInstance().find(mapId);
        List<Zone> zones = map.getZones();
        if (zones.isEmpty()) {
            return -1;
        }
        byte zoneId = 0;
        for (Zone zone : zones) {
            if (zone.getNumberChar() >= 24) {
                zoneId++;
            } else {
                break;
            }
        }
        int size = zones.size();
        if (zoneId < 0 || zoneId >= size) {
            zoneId = (byte) NinjaUtils.nextInt(size);
        }
        return zoneId;
    }

    public static <E, K> java.util.Map<K, List<E>> groupBy(List<E> list, Function<E, K> keyFunction) {
        return Optional.ofNullable(list)
                .orElseGet(ArrayList::new)
                .stream().filter((t) -> keyFunction.apply(t) != null)
                .collect(Collectors.groupingBy(keyFunction));
    }

    public static short[] getXY(int map) {
        short[] xy = new short[2];
        if (map == 22) {
            xy[0] = 228;
            xy[1] = 192;
        } else if (map == 10) {
            xy[0] = 109;
            xy[1] = 264;
        } else if (map == 17) {
            xy[0] = 1480;
            xy[1] = 264;
        } else if (map == 32) {
            xy[0] = 2502;
            xy[1] = 384;
        } else if (map == 38) {
            xy[0] = 397;
            xy[1] = 336;
        } else if (map == 43) {
            xy[0] = 2529;
            xy[1] = 240;
        } else if (map == 48) {
            xy[0] = 140;
            xy[1] = 432;
        } else if (map == 1) {
            xy[0] = 193;
            xy[1] = 384;
        } else if (map == 27) {
            xy[0] = 647;
            xy[1] = 408;
        } else if (map == 72) {
            xy[0] = 1611;
            xy[1] = 672;
        } else {
            xy[0] = 35;
            xy[1] = 100;
        }
        return xy;
    }

    public static short[] getFirstPosition(short map) {
        short x = 120;
        short y = 0;
        TileMap tile = MapManager.getInstance().getTileMap(map);
        y = (short) (y / 24 * 24);
        if (y == 0) {
            while (tile.tileTypeAt(x, y, TileMap.T_TOP)) {
                y += 24;
                if (y > tile.pxh) {
                    break;
                }
            }
        }
        while (!tile.tileTypeAt(x, y, TileMap.T_TOP)) {
            y += 24;
            if (y > tile.pxh) {
                break;
            }
        }
        return new short[]{x, y};
    }

    public static long sum(int... numbers) {
        long sum = 0;
        for (long number : numbers) {
            sum += number;
        }
        return sum;
    }
    
    public static long sum(long... numbers) {
        long sum = 0;
        for (long number : numbers) {
            sum += number;
        }
        return sum;
    }
    
    public static double sum(double... numbers) {
        double sum = 0;
        for (double number : numbers) {
            sum += number;
        }
        return sum;
    }

    public static int nextInt(int min, int max) {
        if (min >= max) {
            return max;
        }
        return rand.nextInt((int) (max + 1 - min)) + min;
    }

    public static void setOption(int[][] option, int up1, int up2) {
        int num = up2 - up1;
        for (int i = 0; i < option.length; i++) {
            if (option[i][0] == 6 || option[i][0] == 7) {
                option[i][1] += 15 * num;
            } else if (option[i][0] == 8 || option[i][0] == 9 || option[i][0] == 19) {
                option[i][1] += 10 * num;
            } else if (option[i][0] == 10 || option[i][0] == 11 || option[i][0] == 12 || option[i][0] == 13
                    || option[i][0] == 14 || option[i][0] == 15 || option[i][0] == 17 || option[i][0] == 18
                    || option[i][0] == 20) {
                option[i][1] += 5 * num;
            } else if (option[i][0] == 21 || option[i][0] == 22 || option[i][0] == 23 || option[i][0] == 24
                    || option[i][0] == 25 || option[i][0] == 26) {
                option[i][1] += 150 * num;
            } else if (option[i][0] == 16) {
                option[i][1] += 3 * num;
            }
        }
    }

    public static int[][] getOptionShop(int[][] option) {
        int[][] result = new int[option.length][2];
        for (int i = 0; i < option.length; i++) {
            if (option[i][0] == 0 || option[i][0] == 1 || option[i][0] == 21 || option[i][0] == 22 || option[i][0] == 23
                    || option[i][0] == 24 || option[i][0] == 25 || option[i][0] == 26) {
                result[i][1] = option[i][1] - 49;
            } else if (option[i][0] == 6 || option[i][0] == 7 || option[i][0] == 8 || option[i][0] == 9
                    || option[i][0] == 19) {
                result[i][1] = option[i][1] - 9;
            } else if (option[i][0] == 2 || option[i][0] == 3 || option[i][0] == 4 || option[i][0] == 5
                    || option[i][0] == 10 || option[i][0] == 11 || option[i][0] == 12 || option[i][0] == 13
                    || option[i][0] == 14 || option[i][0] == 15 || option[i][0] == 17 || option[i][0] == 18) {
                result[i][1] = option[i][1] - 4;
            } else if (option[i][0] == 16) {
                result[i][1] = option[i][1] - 2;
            } else {
                result[i][1] = option[i][1];
            }
        }
        return result;
    }

    public static String dateToString(Date date, String dateFormat) {
        SimpleDateFormat DateFor = new SimpleDateFormat(dateFormat);
        return DateFor.format(date);
    }

    public static String getColor(int type) {
        if (type > 0 && type < 9) {
            return "\nc" + type;
        }
        return "\nc0";
    }

    public static String getColor(String color) {
        if (color.equals("tahoma_7_white")) {
            return "c0";
        }
        if (color.equals("tahoma_7b_yellow")) {
            return "c1";
        }
        if (color.equals("tahoma_7b_white")) {
            return "c2";
        }
        if (color.equals("tahoma_7_yellow")) {
            return "c3";
        }
        if (color.equals("tahoma_7b_red")) {
            return "c4";
        }
        if (color.equals("tahoma_7_red")) {
            return "c5";
        }
        if (color.equals("tahoma_7_grey")) {
            return "c6";
        }
        if (color.equals("tahoma_7b_blue")) {
            return "c7";
        }
        if (color.equals("tahoma_7_blue")) {
            return "c8";
        }
        if (color.equals("tahoma_7_green")) {
            return "c9";
        }
        return "c0";
    }

    public static byte[] getFile(String url) {
        try {
            FileInputStream fis = new FileInputStream(new File(Config.getInstance().getServerDir(), url));
            byte[] ab = new byte[fis.available()];
            fis.read(ab, 0, ab.length);
            fis.close();
            return ab;
        } catch (IOException ex) {

        }
        return null;
    }

    public static void saveFile(String url, byte[] ab) {
        try {
            File f = new File(url);
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(url);
            fos.write(ab);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getDistance(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public static int getLevel(long num) {
        for (int i = 0; i < Server.exps.length; i++) {
            if (num < Server.exps[i]) {
                return i;
            }
            num -= Server.exps[i];
        }
        return 1;
    }

    public static long getExpFromLevel(int level) {
        long exp = 0;
        for (int i = 0; i < Server.exps.length; i++) {
            exp += Server.exps[i];
            if (i + 1 == level) {
                return exp + (int)(Server.exps[i] * nextInt(1, 50) / 100); //
            }
        }
        return 200;
    }

    public static Date getDate(long milliSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return calendar.getTime();
    }

    public static String milliSecondsToDateString(long milliSeconds, String dateFormat) {
        return dateToString(getDate(milliSeconds), dateFormat);
    }

    public static long getExp(int level) {
        long exp = 0;
        for (int i = 0; i < level; i++) {
            exp += Server.exps[i];
        }
        return exp;
    }

    public static boolean compareWeek(Date now, Date when) {
        try {
            Date date1 = dateFormatWeek.parse(dateFormatWeek.format(now));
            Date date2 = dateFormatWeek.parse(dateFormatWeek.format(when));
            return now != when && date1.after(date2);
        } catch (Exception p) {
            p.printStackTrace();
            return false;
        }
    }

    public static boolean isSameWeek(Date date1, Date date2) {
        Calendar c = Calendar.getInstance();
        c.setTime(date1);
        int year1 = c.get(Calendar.YEAR);
        int week1 = c.get(Calendar.WEEK_OF_YEAR);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);
        int year2 = c2.get(Calendar.YEAR);
        int week2 = c2.get(Calendar.WEEK_OF_YEAR);
        if (year1 == year2) {
            if (week1 == week2) {
                return true;
            }
        }
        return false;
    }

    public static boolean compareDay(Date now, Date when) {
        Date date1 = removeTime(now);
        Date date2 = removeTime(when);
        return date1.after(date2) && now != when;
    }

    public static Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static void setTimeout(Runnable runnable, int delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (Exception e) {
                System.err.println(e);
            }
        }).start();
    }

    public static boolean isM1Tet() {
        return isLunarCalendar(1, 1);
    }

    public static String timeAgo(int seconds) {
        int minutes = seconds / 60;
        if (minutes > 0) {
            return minutes + " phút";
        } else {
            return seconds + " giây";
        }
    }

    public static boolean availablePort(int port) {
        try (Socket ignored = new Socket("localhost", port)) {
            return false;
        } catch (IOException ignored) {
            return true;
        }
    }

    public static void schedule(Runnable runnable, int hours, int minutes, int seconds) {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        ZonedDateTime zonedNext5 = zonedNow.withHour(hours).withMinute(minutes).withSecond(seconds);
        if (zonedNow.compareTo(zonedNext5) > 0) {
            zonedNext5 = zonedNext5.plusDays(1);
        }

        Duration duration = Duration.between(zonedNow, zonedNext5);
        long initalDelay = duration.getSeconds();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(runnable, initalDelay, 1 * 24 * 60 * 60, TimeUnit.SECONDS);
    }

    public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> map, final boolean order) {
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        map.entrySet()
                .stream()
                .sorted(new Comparator<java.util.Map.Entry<String, Integer>>() {
                    @Override
                    public int compare(java.util.Map.Entry<String, Integer> o1, java.util.Map.Entry<String, Integer> o2) {
                        if (order) {
                            return o1.getValue().compareTo(o2.getValue());
                        } else {
                            return o2.getValue().compareTo(o1.getValue());
                        }
                    }
                })
                .forEach(
                        entry -> {
                            sortedMap.put(entry.getKey(), entry.getValue());
                        });
        return sortedMap;
    }

}
