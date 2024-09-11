
package com.nsoz.thiendia;

import com.nsoz.db.jdbc.DbManager;
import com.nsoz.util.Log;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Admin
 */
public class ThienDiaManager {

    private static final ThienDiaManager instance = new ThienDiaManager();

    public static ThienDiaManager getInstance() {
        return instance;
    }

    @Getter
    private final List<ThienDiaData> list;

    public ThienDiaManager() {
        this.list = new ArrayList<>();
    }

    public void init() {
        try {
            Log.info("Load TDB");
            list.clear();
            Connection conn = DbManager.getInstance().getConnection(DbManager.GAME);
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM `match_list`");
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String requires = rs.getString("require");
                    JSONObject requireObj = new JSONObject(requires);
                    JSONObject levelObj = requireObj.getJSONObject("level");
                    int levelMin = levelObj.getInt("min");
                    int levelMax = levelObj.getInt("max");
                    JSONArray clazzs = requireObj.getJSONArray("class");
                    byte[] clazzArr = new byte[clazzs.length()];
                    for (int i = 0; i < clazzArr.length; i++) {
                        clazzArr[i] = (byte) clazzs.getInt(i);
                    }
                    ThienDiaData thienDiaData = ThienDiaData.builder()
                            .id(id)
                            .name(name)
                            .levelMax(levelMax)
                            .levelMin(levelMin)
                            .clazzs(clazzArr)
                            .build();
                    list.add(thienDiaData);
                    Log.info("load finish:" + thienDiaData.getName());
                }
            } finally {
                rs.close();
                ps.close();
            }
            Log.info("Load TDB successfully");

        } catch (SQLException | JSONException ex) {
            Log.error("load err: " + ex.getMessage(), ex);
        }
    }

    public ThienDiaData getThienDiaData(int level, byte clazz) {
        synchronized (list) {
            for (ThienDiaData thienDiaData : list) {
                if (thienDiaData.getLevelMin() != -1 && thienDiaData.getLevelMin() > level) {
                    continue;
                }
                if (thienDiaData.getLevelMax() != -1 && thienDiaData.getLevelMax() < level) {
                    continue;
                }
                byte[] clazzArr = thienDiaData.getClazzs();
                for (byte clazzID : clazzArr) {
                    if (clazzID == clazz) {
                        return thienDiaData;
                    }
                }
            }
        }
        return null;
    }

    public void update() {
        synchronized (list) {
            for (ThienDiaData thienDiaData : list) {
                thienDiaData.updateData();
            }
        }
    }

    public void removeRankingByID(int playerId) {
        try {
            Connection conn = DbManager.getInstance().getConnection(DbManager.GAME);
            synchronized (list) {
                for (ThienDiaData thienDiaData : list) {
                    boolean r = thienDiaData.removeRankingByID(playerId);
                    if (r) {
                        thienDiaData.sort();

                        PreparedStatement ps = conn.prepareStatement("DELETE FROM `ranking_list` WHERE `ranking_list`.`match_id` = ? AND `ranking_list`.`player_id` = ?");
                        ps.setInt(1, thienDiaData.getId());
                        ps.setInt(2, playerId);
                        ps.executeUpdate();
                        ps.close();
                        return;

                    }
                }
            }
        } catch (SQLException ex) {
            Log.error("removeRankingByID err: " + ex.getMessage(), ex);
        }
    }
}
