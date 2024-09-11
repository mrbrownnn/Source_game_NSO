
package com.nsoz.effect;

import com.nsoz.db.jdbc.DbManager;
import com.nsoz.lib.ParseData;
import com.nsoz.model.Frame;
import com.nsoz.model.ImageInfo;
import com.nsoz.util.Log;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

/**
 *
 * Được share bởi Youtube : nsotien tv
 */
public class EffectAutoDataManager {

    public static final int PHAO_HOA = 0;
    public static final int THA_LONG_DEN = 7;
    public static final int NO_PHAO = 9;
    public static final int CAY_TRUNG_THU = 10;
    public static final int CAY_TRUNG_THU_2 = 11;
    public static final int CAY_HALLOWEEN = 12;

    private static final EffectAutoDataManager instance = new EffectAutoDataManager();

    public static EffectAutoDataManager getInstance() {
        return instance;
    }

    private List<EffectAutoData> effectAutoDatas;

    public EffectAutoDataManager() {
        this.effectAutoDatas = new ArrayList<>();
    }

    public void load() {
        try {
            Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `effect_data_auto`;");
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                short id = resultSet.getShort("id");
                JSONArray infos = (JSONArray) JSONValue.parseWithException(resultSet.getString("sprites"));
                int lent1 = infos.size();
                ImageInfo[] imgInfo = new ImageInfo[lent1];
                for (int i = 0; i < lent1; i++) {
                    ParseData info = new ParseData((JSONObject) infos.get(i));
                    imgInfo[i] = new ImageInfo();
                    imgInfo[i].id = info.getInt("id");
                    imgInfo[i].x0 = info.getInt("x");
                    imgInfo[i].y0 = info.getInt("y");
                    imgInfo[i].w = info.getInt("w");
                    imgInfo[i].h = info.getInt("h");
                }
                JSONArray frames = (JSONArray) JSONValue.parseWithException(resultSet.getString("frames"));
                int lent2 = frames.size();
                Frame[] frameEffAuto = new Frame[lent2];
                for (int i = 0; i < lent2; i++) {
                    JSONArray frame = (JSONArray) frames.get(i);
                    int num = frame.size();
                    frameEffAuto[i] = new Frame();
                    frameEffAuto[i].idImg = new int[num];
                    frameEffAuto[i].dx = new int[num];
                    frameEffAuto[i].dy = new int[num];
                    for (int j = 0; j < num; j++) {
                        ParseData f = new ParseData((JSONObject) frame.get(j));
                        frameEffAuto[i].idImg[j] = f.getInt("id");
                        frameEffAuto[i].dx[j] = f.getInt("dx");
                        frameEffAuto[i].dy[j] = f.getInt("dy");
                    }
                }
                JSONArray running = (JSONArray) JSONValue.parseWithException(resultSet.getString("running"));
                int lent3 = running.size();
                short[] frameRunning = new short[lent3];
                for (int i = 0; i < lent3; i++) {
                    frameRunning[i] = Short.parseShort(running.get(i).toString());
                }
                EffectAutoData effAuto = new EffectAutoData();
                effAuto.setId(id);
                effAuto.setFrameEffAuto(frameEffAuto);
                effAuto.setImgInfo(imgInfo);
                effAuto.setFrameRunning(frameRunning);
                effAuto.setData();
                add(effAuto);
            }
            resultSet.close();
            stmt.close();
        } catch (NumberFormatException | SQLException | ParseException e) {
           Log.error("load effect auto err", e);
        }
    }

    public void add(EffectAutoData effectAutoData) {
        effectAutoDatas.add(effectAutoData);
    }

    public void remove(EffectAutoData effectAutoData) {
        effectAutoDatas.remove(effectAutoData);
    }

    public EffectAutoData find(int id) {
        for (EffectAutoData effectAutoData : effectAutoDatas) {
            if (effectAutoData.getId() == id) {
                return effectAutoData;
            }
        }
        return null;
    }
}
