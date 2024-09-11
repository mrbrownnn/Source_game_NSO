
package com.nsoz.effect;

import com.google.gson.Gson;
import com.nsoz.db.jdbc.DbManager;
import com.nsoz.lib.ParseData;
import com.nsoz.model.PartFrame;
import com.nsoz.model.SmallImage;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * Được share bởi Youtube : nsotien tv
 */
public class EffectDataManager {

    private static final EffectDataManager instance = new EffectDataManager();

    public static EffectDataManager getInstance() {
        return instance;
    }

    private List<EffectData> effectDatas;

    public EffectDataManager() {
        this.effectDatas = new ArrayList<>();
    }

    public void load() {
        try {
            PreparedStatement stmt = DbManager.getInstance().getConnection(DbManager.SERVER).prepareStatement("SELECT * FROM `effect_data`;");
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                EffectData effData = new EffectData();
                effData.id = resultSet.getInt("id");
                JSONArray smallImage = (JSONArray) JSONValue.parse(resultSet.getString("sprites"));
                effData.smallImage = new SmallImage[smallImage.size()];
                for (int a = 0; a < effData.smallImage.length; a++) {
                    ParseData p = new ParseData((JSONObject) smallImage.get(a));
                    effData.smallImage[a] = new SmallImage();
                    effData.smallImage[a].id = p.getShort("id");
                    effData.smallImage[a].x = p.getShort("x");
                    effData.smallImage[a].y = p.getShort("y");
                    effData.smallImage[a].w = p.getShort("w");
                    effData.smallImage[a].h = p.getShort("h");
                }
                JSONArray frames = (JSONArray) JSONValue.parse(resultSet.getString("frames"));
                int size = frames.size();
                effData.frames = new PartFrame[size][];
                for (int a = 0; a < size; a++) {
                    JSONArray frame = (JSONArray) frames.get(a);
                    int lent = frame.size();
                    effData.frames[a] = new PartFrame[lent];
                    for (int j = 0; j < lent; j++) {
                        ParseData p = new ParseData((JSONObject) frame.get(j));
                        effData.frames[a][j] = new PartFrame();
                        effData.frames[a][j].idSmallImg = p.getByte("id");
                        effData.frames[a][j].dx = p.getShort("dx");
                        effData.frames[a][j].dy = p.getShort("dy");
                        effData.frames[a][j].flip = p.getByte("flip");
                        effData.frames[a][j].onTop = p.getByte("onTop");
                    }
                }
                JSONArray sequence = (JSONArray) JSONValue.parse(resultSet.getString("running"));
                int lent = sequence.size();
                effData.sequence = new byte[lent];
                for (int a = 0; a < lent; a++) {
                    effData.sequence[a] = (byte) Short.parseShort(sequence.get(a).toString());
                }
                JSONArray frameChar = (JSONArray) JSONValue.parse(resultSet.getString("frame_char"));
                for (int a = 0; a < 4; a++) {
                    JSONArray jArr = (JSONArray) frameChar.get(a);
                    int lent2 = jArr.size();
                    effData.frameChar[a] = new byte[lent2];
                    for (int j = 0; j < lent2; j++) {
                        effData.frameChar[a][j] = Byte.parseByte(jArr.get(j).toString());
                    }
                }
                effData.indexSplash[0] = (byte) (effData.frameChar[0].length - 7);
                effData.indexSplash[1] = (byte) (effData.frameChar[1].length - 7);
                effData.indexSplash[2] = (byte) (effData.frameChar[3].length - 7);
                effData.indexSplash[3] = (byte) (effData.frameChar[3].length - 7);
                add(effData);
            }
            resultSet.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(EffectDataManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void add(EffectData effect) {
        effectDatas.add(effect);
    }

    public void remove(EffectData effect) {
        effectDatas.remove(effect);
    }

    public EffectData find(int id) {
        for (EffectData eff : effectDatas) {
            if (eff.id == id) {
                return eff;
            }
        }
        return null;
    }

    public void setData() {
        effectDatas.forEach((t) -> {
            t.setData();
        });
    }
}
