
package com.nsoz.mob;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.nsoz.constants.SQLStatement;
import com.nsoz.db.jdbc.DbManager;
import com.nsoz.model.Frame;
import com.nsoz.model.ImageInfo;
import com.nsoz.util.Log;
import com.nsoz.util.ProgressBar;
import java.sql.SQLException;

import lombok.Getter;

/**
 *
 * Được share bởi Youtube : nsotien tv
 */
public class MobManager {

    private static final MobManager instance = new MobManager();

    public static MobManager getInstance() {
        return instance;
    }

    @Getter
    private final List<MobTemplate> mobs = new ArrayList<>();

    public boolean load() {
        try {
            Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
            PreparedStatement stmt = conn.prepareStatement(SQLStatement.GET_ALL_MONSTER, ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = stmt.executeQuery();
            resultSet.last();
            ProgressBar pb = new ProgressBar("Loading Monster", resultSet.getRow());
            resultSet.beforeFirst();
            while (resultSet.next()) {
                try {
                    MobTemplate mob = new MobTemplate();
                    mob.setId(resultSet.getInt("id"));
                    mob.setName(resultSet.getString("name"));
                    mob.setType(resultSet.getByte("type"));
                    mob.setHp(resultSet.getInt("hp"));
                    mob.setLevel(resultSet.getShort("level"));
                    mob.setRangeMove(resultSet.getByte("range_move"));
                    mob.setSpeed(resultSet.getByte("speed"));
                    mob.setNumberImage(resultSet.getByte("n_img"));
                    mob.setTypeFly(resultSet.getByte("type_fly"));
                    mob.setBoss(resultSet.getBoolean("boss"));
                    JSONArray moves = (JSONArray) JSONValue.parse(resultSet.getString("move"));
                    int size = moves.size();
                    byte[] frameBossMove = new byte[size];
                    for (int i = 0; i < size; i++) {
                        frameBossMove[i] = ((Long) moves.get(i)).byteValue();
                    }
                    mob.setFrameBossMove(frameBossMove);
                    JSONArray attacks = (JSONArray) JSONValue.parse(resultSet.getString("attack"));
                    size = attacks.size();
                    byte[][] frameBossAttack = new byte[size][];
                    for (int i = 0; i < size; i++) {
                        JSONArray jArr = (JSONArray) attacks.get(i);
                        int size2 = jArr.size();
                        frameBossAttack[i] = new byte[size2];
                        for (int j = 0; j < size2; j++) {
                            frameBossAttack[i][j] = ((Long) jArr.get(j)).byteValue();
                        }
                    }
                    mob.setFrameBossAttack(frameBossAttack);
                    JSONArray imgInfos = (JSONArray) JSONValue.parse(resultSet.getString("sprites"));
                    size = imgInfos.size();
                    ImageInfo[] imgInfo = new ImageInfo[size];
                    for (int i = 0; i < size; i++) {
                        JSONObject job = (JSONObject) imgInfos.get(i);
                        ImageInfo img = new ImageInfo();
                        img.id = ((Long) job.get("id")).intValue();
                        img.x0 = ((Long) job.get("x")).intValue();
                        img.y0 = ((Long) job.get("y")).intValue();
                        img.w = ((Long) job.get("w")).intValue();
                        img.h = ((Long) job.get("h")).intValue();
                        imgInfo[i] = img;
                    }
                    mob.setImgInfo(imgInfo);
                    JSONArray frames = (JSONArray) JSONValue.parse(resultSet.getString("frames"));
                    size = frames.size();
                    Frame[] frameBoss = new Frame[size];
                    for (int i = 0; i < size; i++) {
                        JSONArray frame = (JSONArray) frames.get(i);
                        int size2 = frame.size();
                        Frame f = new Frame();
                        f.dx = new int[size2];
                        f.dy = new int[size2];
                        f.idImg = new int[size2];
                        f.onTop = new int[size2];
                        f.flip = new int[size2];
                        for (int j = 0; j < size2; j++) {
                            JSONObject obj = (JSONObject) frame.get(j);
                            int imgID = ((Long) obj.get("id")).intValue();
                            int dx = ((Long) obj.get("dx")).intValue();
                            int dy = ((Long) obj.get("dy")).intValue();
                            int onTop = ((Long) obj.get("onTop")).intValue();
                            int flip = ((Long) obj.get("flip")).intValue();
                            f.idImg[j] = imgID;
                            f.dx[j] = dx;
                            f.dy[j] = dy;
                            f.onTop[j] = onTop;
                            f.flip[j] = flip;
                        }
                        frameBoss[i] = f;
                    }
                    mob.setFrameBoss(frameBoss);
                    JSONArray sequences = (JSONArray) JSONValue.parse(resultSet.getString("sequence"));
                    short[] sequence = new short[sequences.size()];
                    for (int i = 0; i < sequence.length; i++) {
                        sequence[i] = ((Long) sequences.get(i)).shortValue();
                    }
                    mob.setSequence(sequence);
                    JSONArray frameChars = (JSONArray) JSONValue.parse(resultSet.getString("frame_char"));
                    byte[][] frameChar = new byte[frameChars.size()][];
                    for (int i = 0; i < frameChar.length; i++) {
                        JSONArray frame = (JSONArray) frameChars.get(i);
                        frameChar[i] = new byte[frame.size()];
                        for (int j = 0; j < frame.size(); j++) {
                            frameChar[i][j] = ((Long) frame.get(i)).byteValue();
                        }
                    }
                    mob.setFrameChar(frameChar);
                    JSONArray indexSplashs = (JSONArray) JSONValue.parse(resultSet.getString("index_splash"));
                    byte[] indexSplash = new byte[indexSplashs.size()];
                    for (int i = 0; i < indexSplash.length; i++) {
                        indexSplash[i] = ((Long) indexSplashs.get(i)).byteValue();
                    }
                    mob.setIndexSplash(indexSplash);
                    add(mob);
                    pb.setExtraMessage(mob.name + " finished!");
                    pb.step();
                } catch (Exception e) {
                    pb.setExtraMessage(e.getMessage());
                    pb.reportError();
                    e.printStackTrace();
                    return false;
                }
            }
            pb.setExtraMessage("Finished!");
            pb.reportSuccess();
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            Log.error("load mob err", e);
            return false;
        }
        return true;
    }

    public void add(MobTemplate mob) {
        mobs.add(mob);
    }

    public void remove(MobTemplate mob) {
        mobs.remove(mob);
    }

    public MobTemplate find(int id) {
        for (MobTemplate mob : mobs) {
            if (mob.id == id) {
                return mob;
            }
        }
        return null;
    }
}
