
package com.nsoz.server;

import com.nsoz.constants.ConstTime;
import com.nsoz.constants.SQLStatement;
import com.nsoz.convert.Converter;
import com.nsoz.db.jdbc.DbManager;
import com.nsoz.lib.Resource;
import com.nsoz.mob.MobTemplate;
import com.nsoz.model.Char;
import com.nsoz.model.Clazz;
import com.nsoz.model.Frame;
import com.nsoz.model.ImageInfo;
import com.nsoz.option.SkillOption;
import com.nsoz.skill.Skill;
import com.nsoz.skill.SkillOptionTemplate;
import com.nsoz.skill.SkillTemplate;
import com.nsoz.util.NinjaUtils;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GameData extends Thread {

    // Default
    public static final boolean ANTICROSS_MAP = false;
    public static final long DELAY_SAVE_DATA = ConstTime.MINUTE * 1;
    public static final int[] UP_CRYSTAL = {1, 4, 16, 64, 256, 1024, 4096, 16384, 65536, 262144, 1048576,
        4194304};
    public static final int[] UP_BI_KIP = {100, 90 ,80, 70, 60, 55, 50, 45, 30, 35, 30, 25, 20, 15, 10,
            5};
    public static final int[] UP_THU_NUOI = {100, 90 ,80, 70, 60, 55, 50, 45, 30, 35, 30, 25, 20, 15, 10,
            5};
    public static final int[] UP_CLOTHE = {4, 9, 33, 132, 177, 256, 656, 2880, 3968, 6016, 13440, 54144, 71680,
        108544, 225280, 1032192};
    public static final int[] UP_ADORN = {6, 14, 50, 256, 320, 512, 1024, 5120, 6016, 9088, 19904, 86016, 108544,
        166912, 360448, 1589248};
    public static final int[] UP_WEAPON = {18, 42, 132, 627, 864, 1360, 2816, 13824, 17792, 26880, 54016, 267264,
        315392, 489472, 1032192, 4587520};
    public static final int[] COIN_UP_CRYSTAL = {10, 40, 160, 640, 2560, 10240, 40960, 163840, 655360, 1310720,
        3932160, 11796480};
    public static final int[] COIN_UP_CLOTHE = {120, 270, 990, 3960, 5310, 7680, 19680, 86400, 119040, 180480,
        403200, 1624320, 2150400, 3256320, 6758400, 10137600};
    public static final int[] COIN_UP_ADORN = {180, 420, 1500, 7680, 9600, 15360, 30720, 153600, 180480, 272640,
        597120, 2580480, 3256320, 5007360, 10813440, 16220160};
    public static final int[] COIN_UP_WEAPON = {540, 1260, 3960, 18810, 25920, 40800, 84480, 414720, 533760,
        806400, 1620480, 8017920, 9461760, 14684160, 22026240, 33039360};
    public static final int[] GOLD_UP = {5, 15, 35, 55, 75, 100, 115, 120, 150, 200, 250, 300, 400, 500, 600, 700};
    public static final int[] MAX_PERCENT = {80, 75, 70, 65, 60, 55, 50, 45, 40, 35, 30, 25, 20, 15, 10, 6};
    public static final int[] COIN_GOT_NGOC = {0, 5000, 40000, 135000, 320000, 625000, 1080000, 1715000, 2560000,
        3645000, 5000000};
    public static final int[][] NGOC_KHAM_EXP = {{0, 0}, {200, 10}, {500, 20}, {1000, 50}, {2000, 100},
    {5000, 200}, {10000, 500}, {20000, 1000}, {50000, 2000}, {100000, 5000},
    {100000, 10000}};
    
    public static final HashMap<Integer, Long> HASH_MAP = new HashMap<>();

    private static final GameData instance = new GameData();

    public static GameData getInstance() {
        return instance;
    }

    @Getter
    private List<Clazz> clazzs = new ArrayList<>();
    @Getter
    private List<SkillOptionTemplate> optionTemplates = new ArrayList<>();

    private HashMap<String, Resource> resources = new HashMap<>();

    private HashMap<String, Language> langs = new HashMap<>();

    private boolean running = true;

    public void init() {
        loadLanguage();
        loadSkillOption();
        loadClass();
    }

    private void loadLanguage() {
        File file = new File("Data/Lang/");
        File[] files = file.listFiles();
        for (File f : files) {
            String[] splits = f.getName().split("\\.");
            Language language = new Language(splits[0], f);
            langs.put(language.getLang(), language);
        }
    }

    public Language getLanguage(int id) {
        if (id == 0) {
            return langs.get("vi");
        }
        return langs.get("en");
    }

    public byte[] loadFile(String url) {
        synchronized (resources) {
            if (resources.containsKey(url)) {
                return resources.get(url).getData();
            }
            byte[] data = NinjaUtils.getFile(url);
            if (data != null) {
                resources.put(url, new Resource(data, 60 * 60 * 1000));
            } else {
                return new byte[0];
            }
            return data;
        }
    }

    public void update() {
//        synchronized (resources) {
//            List<String> list = new ArrayList<>();
//            for (Map.Entry<String, Resource> r : resources.entrySet()) {
//                if (r.getValue().isExpired()) {
//                    list.add(r.getKey());
//                }
//            }
//            for (String key : list) {
//                resources.remove(key);
//                Log.debug("remove cache: " + key);
//            }
//        }
    }

    private void loadSkillOption() {
        try {
            PreparedStatement stmt = DbManager.getInstance().getConnection(DbManager.SERVER).prepareStatement("SELECT * FROM `skill_option`;");
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                SkillOptionTemplate template = new SkillOptionTemplate();
                template.id = resultSet.getInt("id");
                template.name = resultSet.getString("name");
                optionTemplates.add(template);
            }
            resultSet.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(GameData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadClass() {
        try {
            clazzs.clear();
            Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
            PreparedStatement ps = conn.prepareStatement(SQLStatement.LOAD_CLASS);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Clazz clazz = Clazz.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .build();
                PreparedStatement ps2 = conn.prepareStatement(SQLStatement.LOAD_SKILL_TEMPLATE);
                ps2.setInt(1, clazz.getId());
                ResultSet rs2 = ps2.executeQuery();
                while (rs2.next()) {
                    SkillTemplate skillTemplate = new SkillTemplate();
                    skillTemplate.id = rs2.getInt("id");
                    skillTemplate.name = rs2.getString("name");
                    skillTemplate.maxPoint = rs2.getByte("max_point");
                    skillTemplate.type = rs2.getByte("type");
                    skillTemplate.iconId = rs2.getShort("icon");
                    skillTemplate.description = rs2.getString("description");
                    PreparedStatement ps3 = conn.prepareStatement(SQLStatement.LOAD_SKILL);
                    ps3.setInt(1, skillTemplate.id);
                    ResultSet rs3 = ps3.executeQuery();
                    while (rs3.next()) {
                        Skill skill = new Skill();
                        skill.id = rs3.getInt("id");
                        skill.point = (byte) rs3.getInt("point");
                        skill.level = (byte) rs3.getInt("level");
                        skill.coolDown = rs3.getInt("cooldown");
                        skill.dx = (short) rs3.getInt("dx");
                        skill.dy = (short) rs3.getInt("dy");
                        skill.manaUse = (short) rs3.getInt("mana_use");
                        skill.maxFight = (byte) rs3.getInt("max_fight");
                        skill.template = skillTemplate;
                        JSONArray array = new JSONArray(rs3.getString("options"));
                        skill.options = new SkillOption[array.length()];
                        for (int i = 0; i < skill.options.length; i++) {
                            JSONObject obj = array.getJSONObject(i);
                            SkillOption skillOption = new SkillOption(obj.getInt("id"), obj.getInt("param"));
                            skill.options[i] = skillOption;
                        }
                        skillTemplate.addSkill(skill);
                    }
                    rs3.close();
                    ps3.close();
                    clazz.addSkillTemplate(skillTemplate);
                }
                clazzs.add(clazz);
                rs2.close();
                ps2.close();
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(GameData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(GameData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeDataMobNew(DataOutputStream ds, MobTemplate mob) throws IOException {
        ds.writeByte(mob.imgInfo.length);
        for (ImageInfo image : mob.imgInfo) {
            ds.writeByte(image.id);
            ds.writeByte(image.x0);
            ds.writeByte(image.y0);
            ds.writeByte(image.w);
            ds.writeByte(image.h);
        }
        ds.writeShort(mob.frameBoss.length);
        for (Frame frame : mob.frameBoss) {
            ds.writeByte(frame.idImg.length);
            for (int i = 0; i < frame.idImg.length; i++) {
                ds.writeShort(frame.dx[i]);
                ds.writeShort(frame.dy[i]);
                ds.writeByte(frame.idImg[i]);
                ds.writeByte(frame.flip[i]);
                ds.writeByte(frame.onTop[i]);
            }
        }
        ds.writeByte(mob.sequence.length);
        for (short value : mob.sequence) {
            ds.writeShort(value);
        }
        ds.writeByte(mob.frameChar.length);
        for (byte[] frameC : mob.frameChar) {
            ds.writeByte(frameC.length);
            for (byte value : frameC) {
                ds.writeByte(value);
            }
        }
        for (byte index : mob.indexSplash) {
            ds.writeByte(index);
        }
    }

    public void writeDataMobOld(DataOutputStream ds, MobTemplate mob) throws IOException {
        ds.writeByte(mob.imgInfo.length);
        for (ImageInfo image : mob.imgInfo) {
            ds.writeByte(image.id);
            ds.writeByte(image.x0);
            ds.writeByte(image.y0);
            ds.writeByte(image.w);
            ds.writeByte(image.h);
        }
        ds.writeShort(mob.frameBoss.length);
        for (Frame frame : mob.frameBoss) {
            ds.writeByte(frame.idImg.length);
            for (int i = 0; i < frame.idImg.length; i++) {
                ds.writeShort(frame.dx[i]);
                ds.writeShort(frame.dy[i]);
                ds.writeByte(frame.idImg[i]);
            }
        }
        ds.writeShort(0);
    }

    public Clazz findClass(byte classId) {
        return clazzs.get(classId);
    }

    public ArrayList<Skill> getAllSkill(byte clazz, int level) {
        ArrayList<Skill> skills = new ArrayList<>();
        Clazz nClass = clazzs.get(clazz);
        for (SkillTemplate skillTemplate : nClass.getSkillTemplates()) {
            int levelRequire = 0;
            Skill skilla = null;
            for (Skill skill : skillTemplate.skills) {
                if (skill.level <= level && (skilla == null || skill.level > levelRequire)) {
                    levelRequire = skill.level;
                    skilla = skill;
                }
            }
            if (skilla != null) {
                skills.add(Converter.getInstance().newSkill(skilla));
            }
        }
        return skills;
    }

    public Skill getSkill(int classId, int templateId, int point) {
        SkillTemplate tem = getTemplate(classId, templateId);
        if (tem != null) {
            for (Skill skill : tem.skills) {
                if (skill.point == point) {
                    return Converter.getInstance().newSkill(skill);// khởi tạo skill
                }
            }
        }
        return null;
    }
    
    public Skill getSkillWithLevel(int classId, int level) {
        Clazz n = clazzs.get(classId);
        if (n != null) {
            for (SkillTemplate tem : n.getSkillTemplates()) {
                if (tem != null && tem.skills.get(1).level == level) {
                    return tem.skills.get(1);
                }
            }
        }
        return null;
    }

    public Skill getSkill(int id, int point) {
        for (Clazz nC : clazzs) {
            for (SkillTemplate template : nC.getSkillTemplates()) {
                if (template.id == id) {
                    for (Skill skill : template.skills) {
                        if (skill.point == point) {
                            return skill;
                        }
                    }
                }
            }

        }
        return null;
    }

    public SkillTemplate getTemplate(int classId, int templateId) {
        Clazz n = clazzs.get(classId);
        if (n != null) {
            for (SkillTemplate tem : n.getSkillTemplates()) {
                if (tem != null && tem.id == templateId) {
                    return tem;
                }
            }
        }

        return null;
    }

    public ArrayList<Skill> getSkills(Char _char) {
        ArrayList<Skill> skills = new ArrayList<Skill>();
        Clazz n = clazzs.get(_char.classId);
        if (n != null) {
            for (SkillTemplate tem : n.getSkillTemplates()) {
                for (Skill skill : tem.skills) {
                    if (skill.point == 1) {
                        skills.add(skill);
                    }
                }
            }
        }
        return skills;
    }

    @Override
    public void run() {
        while (running) {
            long l1 = System.currentTimeMillis();
            update();
            long l2 = System.currentTimeMillis();
            if (l2 - l1 < 5000) {
                try {
                    Thread.sleep(5000 - (l2 - l1));
                } catch (InterruptedException ex) {
                    Logger.getLogger(GameData.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public void close() {
        running = false;
    }
}
