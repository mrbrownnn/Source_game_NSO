
package com.nsoz.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.nsoz.clan.Clan;
import com.nsoz.db.jdbc.DbManager;
import com.nsoz.util.Log;
import com.nsoz.util.NinjaUtils;
import java.sql.SQLException;
import java.util.Optional;

/**
 *
 * @author PC
 */
public class Ranked {

    public static final String[] NAME = {"Top Đại gia", "Top Cao Thủ", "Top Gia tộc", "TOP Hang động","TOP NẠP"};

    public static final String[] RANKED_NAME = {"%d. %s có %s yên", "%d. %s trình độ cấp %d vào ngày %s",
        "%d. Gia tộc %s có trình độ cấp %d do %s làm tộc trưởng, thành viên %d/%d", "%d. %s nhận được %s rương","%d. Tài khoản %s có tổng nạp là %s VND"};

    public static final Vector[] RANKED = new Vector[5];

    public static void init() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                refresh();
            }
        };
        long delay = 12 * 60 * 60 * 1000;
        Timer timer = new Timer("Ranked");
        timer.schedule(timerTask, 0, delay);
    }

    public static void refresh() {
        initTopDaiGia();
        initTopCaoThu();
        initTopGiaToc();
        initTopHangDong();
        initTopNap();
        Log.info("Refresh ranked success.");
    }

    public static void initTopDaiGia() {
        try {
            Vector<String> ranked = new Vector<>();
            Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT `name`, `yen` FROM `players` WHERE `yen` > 0 AND `server_id` = ? ORDER BY `yen` DESC LIMIT 10;");
            stmt.setInt(1, Config.getInstance().getServerID());
            ResultSet res = stmt.executeQuery();
            int i = 1;
            while (res.next()) {
                ranked.add(String.format(RANKED_NAME[0], i, res.getString("name"),
                        NinjaUtils.getCurrency(res.getInt("yen"))));
                i++;
            }
            res.close();
            stmt.close();
            RANKED[0] = ranked;
        } catch (SQLException ex) {
            Log.error("init top dai gia err", ex);
        }
    }

    public static void initTopCaoThu() {
        try {
            Vector<String> ranked = new Vector<>();
            Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT `name`, CAST(JSON_EXTRACT(data, \"$.exp\") AS INT) AS `exp`, CAST(JSON_EXTRACT(data, \"$.levelUpTime\") AS INT) AS `levelUpTime`,CASE WHEN JSON_EXTRACT(data, \"$.duatop\") = 'true' THEN 1 ELSE 0 END AS `duatop` FROM players where `server_id` = ? ORDER BY `exp` DESC, `levelUpTime` ASC LIMIT 10;");
            stmt.setInt(1, Config.getInstance().getServerID());
            ResultSet res = stmt.executeQuery();
            ArrayList<CaoThu> list = new ArrayList<>();
            while (res.next()) {
                CaoThu rank = new CaoThu();
                rank.level = NinjaUtils.getLevel(res.getLong("exp"));
                rank.time = res.getLong("levelUpTime");
                rank.name = res.getString("name");
                boolean isDuaTop = res.getInt("duatop") == 1  ;
                if(isDuaTop)
                    list.add(rank);
            }
            order(list);
            int i = 0;
            for (CaoThu c : list) {
                int level = c.level;
                String time = NinjaUtils.milliSecondsToDateString(c.time, "yyyy/MM/dd HH:mm:ss aa");
                ranked.add(String.format(RANKED_NAME[1], i, c.name, level, time));
                i++;
            }
            res.close();
            stmt.close();
            RANKED[1] = ranked;
        } catch (SQLException ex) {
            Log.error("init top cao thu", ex);
        }
    }

    public static void initTopGiaToc() {
        try {
            Vector<String> ranked = new Vector<>();
            Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
            PreparedStatement stmt = conn
                    .prepareStatement("SELECT `id` FROM `clan` WHERE `level` > 1 AND `server_id` = ? ORDER BY `level` DESC LIMIT 10;");
            stmt.setInt(1, Config.getInstance().getServerID());
            ResultSet res = stmt.executeQuery();
            int i = 1;
            while (res.next()) {
                int id = res.getInt("id");
                Optional<Clan> g = Clan.getClanDAO().get(id);
                if (g != null && g.isPresent()) {
                    Clan clan = g.get();
                    ranked.add(String.format(RANKED_NAME[2], i, clan.getName(), clan.getLevel(), clan.getMainName(),
                            clan.getNumberMember(), clan.getMemberMax()));
                    i++;
                }
            }
            res.close();
            stmt.close();
            RANKED[2] = ranked;
        } catch (SQLException ex) {
            Log.error("init top gia toc", ex);
        }
    }

    public static void initTopHangDong() {
        try {
            Vector<String> ranked = new Vector<>();
            Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT `name`, `rewardPB` FROM `players` WHERE `rewardPB` > 0 AND `server_id` = ? ORDER BY `rewardPB` DESC LIMIT 10;");
            stmt.setInt(1, Config.getInstance().getServerID());
            ResultSet res = stmt.executeQuery();
            int i = 1;
            while (res.next()) {
                ranked.add(String.format(RANKED_NAME[3], i, res.getString("name"),
                        NinjaUtils.getCurrency(res.getInt("rewardPB"))));
                i++;
            }
            res.close();
            stmt.close();
            RANKED[3] = ranked;
        } catch (SQLException ex) {
            Log.error("init top hang dong", ex);
        }
    }
    
    public static void initTopNap() {
        try {
            Vector<String> ranked = new Vector<>();
            Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT `username`, `tongnap` FROM `users` WHERE `tongnap` > 0 AND `id` = ? ORDER BY `tongnap` DESC LIMIT 10;");
            stmt.setInt(1, Config.getInstance().getServerID());
            ResultSet res = stmt.executeQuery();
            int i = 1;
            while (res.next()) {
                ranked.add(String.format(RANKED_NAME[4], i, res.getString("username"),
                        NinjaUtils.getCurrency(res.getInt("tongnap"))));
                i++;
            }
            res.close();
            stmt.close();
            RANKED[4] = ranked;
        } catch (SQLException ex) {
            Log.error("init top nap err", ex);
        }
    }

    private static void order(List<CaoThu> ranks) {

        Collections.sort(ranks, new Comparator() {

            public int compare(Object o1, Object o2) {

                Integer level1 = ((CaoThu) o1).level;
                Integer level2 = ((CaoThu) o2).level;
                int sComp = level2.compareTo(level1);
                if (sComp != 0) {
                    return sComp;
                }
                Long x1 = ((CaoThu) o1).time;
                Long x2 = ((CaoThu) o2).time;
                return x1.compareTo(x2);
            }
        });
    }
}

class CaoThu {

    public String name;
    public long time;
    public int level;
}
