package com.nsoz.model;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.nsoz.effect.Effect;
import com.nsoz.mob.Mob;
import com.nsoz.skill.Skill;
import com.nsoz.ability.AbilityFromEquip;
import com.nsoz.fashion.FashionFromEquip;
import com.nsoz.item.Mount;
import com.nsoz.item.Equip;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Vector;

import com.nsoz.db.jdbc.DbManager;
import com.nsoz.db.mongodb.MongoDbConnection;
import com.nsoz.effect.EffectManager;
import com.nsoz.event.eventpoint.EventPoint;
import com.nsoz.item.Item;
import com.nsoz.network.NoService;
import com.nsoz.network.Controller;
import com.nsoz.network.Service;
import com.nsoz.map.zones.Zone;
import com.nsoz.util.Log;
import com.nsoz.util.NinjaUtils;
import com.nsoz.lib.ParseData;
import com.nsoz.map.world.World;
import com.nsoz.server.GameData;
import java.sql.SQLException;
import org.bson.Document;

public class CloneChar extends Char {

    public Char human;
    public int damePercent;
    public short selectSkillId;
    public boolean isUpdate;

    public CloneChar(Char _char, int damePercent) {
        super(-(10000000 + _char.id));
        this.isHuman = false;
        this.isNhanBan = true;
        this.human = _char;
        this.damePercent = damePercent;
    }

    @Override
    public EventPoint getEventPoint() {
        return human.getEventPoint();
    }

    public World findWorld(byte type) {
        if (isNhanBan) {
            return human.findWorld(type);
        }
        return super.findWorld(type);

    }

    @Override
    public boolean isMeCanAttackOtherPlayer(Char cAtt) {
        if (isNhanBan) {
            return human.isMeCanAttackOtherPlayer(cAtt);
        }
        return super.isMeCanAttackOtherPlayer(cAtt);
    }

    @Override
    public boolean isMeCanAttackNpc(Mob cAtt) {
        if (isNhanBan) {
            return human.isMeCanAttackNpc(cAtt);
        }
        return super.isMeCanAttackNpc(cAtt);
    }

    @Override
    public boolean load() {
        try {
            PreparedStatement stmt = DbManager.getInstance().getConnection(DbManager.GAME).prepareStatement(
                    "SELECT * FROM `clone_char` WHERE `id` = ? LIMIT 1", ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            stmt.setInt(1, this.id);
            ResultSet res = stmt.executeQuery();
            try {
                if (res.first()) {
                    this.classId = res.getByte("class");
                    JSONObject json = (JSONObject) JSONValue.parse(res.getString("data"));
                    ParseData parse = new ParseData(json);
                    this.limitKyNangSo = parse.getByte("limitKyNangSo");
                    this.limitTiemNangSo = parse.getByte("limitTiemNangSo");
                    this.tayTiemNang = parse.getInt("tayTiemNang");
                    this.tayKyNang = parse.getInt("tayKyNang");
                    this.limitBangHoa = parse.getInt("limitBangHoa");
                    this.limitPhongLoi = parse.getInt("limitPhongLoi");
                    this.exp = parse.getLong("exp");
                    this.level = NinjaUtils.getLevel(this.exp);
                    this.head = this.original_head = human.original_head;
                    this.clan = null;
                    this.user = human.user;
                    this.gender = human.gender;
                    this.taskId = human.taskId;
                    this.name = human.name;
                    this.potentialPoint = res.getShort("point");
                    this.selectSkillId = res.getShort("select_skill");
                    this.skillPoint = res.getShort("spoint");
                    JSONArray jArr = (JSONArray) JSONValue.parse(res.getString("potential"));
                    int len = jArr.size();
                    this.potential = new int[4];
                    for (int i = 0; i < 4; i++) {
                        if (jArr.get(i) != null) {
                            this.potential[i] = ((Long) jArr.get(i)).intValue();
                        } else {
                            this.potential[i] = 5;
                        }
                    }
                    jArr = (JSONArray) JSONValue.parse(res.getString("skill"));
                    len = jArr.size();
                    this.vSkill = new Vector<>();
                    this.vSupportSkill = new Vector<>();
                    this.vSkillFight = new Vector<>();
                    for (int i = 0; i < len; i++) {
                        JSONObject obj = (JSONObject) jArr.get(i);
                        int skillId = Integer.parseInt(obj.get("id").toString());
                        int point = Integer.parseInt(obj.get("point").toString());
                        Skill skill = GameData.getInstance().getSkill(this.classId, skillId, point);
                        if (skill.template.type == Skill.SKILL_AUTO_USE) {
                            this.vSupportSkill.add(skill);
                        } else if ((skill.template.type == Skill.SKILL_CLICK_USE_ATTACK
                                || skill.template.type == Skill.SKILL_CLICK_LIVE
                                || skill.template.type == Skill.SKILL_CLICK_USE_BUFF
                                || skill.template.type == Skill.SKILL_CLICK_NPC)
                                && (skill.template.maxPoint == 0 || (skill.template.maxPoint > 0 && skill.point > 0))) {

                            this.vSkillFight.add(skill);
                        }
                        this.vSkill.add(skill);
                    }
                    this.mount = new Mount[5];
                    JSONArray jso = (JSONArray) JSONValue.parse(res.getString("mount"));
                    if (jso != null) {
                        int size = jso.size();
                        for (int i = 0; i < size; i++) {
                            Mount mount = new Mount((JSONObject) jso.get(i));
                            this.mount[mount.template.type - 29] = mount;
                        }
                    }
                    this.bijuu = new Item[5];
                    JSONArray jBijuu = (JSONArray) JSONValue.parse(res.getString("bijuu"));
                    if (jBijuu != null) {
                        int size = jBijuu.size();
                        for (int i = 0; i < size; i++) {
                            Item bi = new Item((JSONObject) jBijuu.get(i));
                            int index = 4;
                            if (bi.template.isTypeEquipmentBijuu()) {
                                index = bi.template.type - 35;
                            }
                            this.bijuu[index] = bi;
                        }
                    }
                    this.equipment = new Equip[16];
                    jso = (JSONArray) JSONValue.parse(res.getString("equiped"));
                    if (jso != null) {
                        int size = jso.size();
                        for (int i = 0; i < size; i++) {
                            Equip equipe = new Equip((JSONObject) jso.get(i));
                            if ((equipe.hasExpire() && System.currentTimeMillis() > equipe.expire) || equipe.isRemoveItem()) {
                                continue;
                            }
                            this.equipment[equipe.template.type] = equipe;
                        }
                    }
                    this.fashion = new Equip[16];
                    jso = (JSONArray) JSONValue.parse(res.getString("fashion"));
                    if (jso != null) {
                        int size = jso.size();
                        for (int i = 0; i < size; i++) {
                            Equip equip = new Equip((JSONObject) jso.get(i));
                            if ((equip.hasExpire() && System.currentTimeMillis() > equip.expire) || equip.isRemoveItem()) {
                                continue;
                            }
                            this.fashion[equip.template.type] = equip;
                        }
                    }
                    JSONArray j = (JSONArray) JSONValue.parse(res.getString("onOSkill"));
                    if (j != null) {
                        this.onOSkill = new byte[j.size()];
                        for (int t = 0; t < this.onOSkill.length; t++) {
                            if (t < j.size()) {
                                this.onOSkill[t] = ((Long) j.get(t)).byteValue();
                            }
                        }
                    } else {
                        this.onOSkill = new byte[0];
                    }
                    j = (JSONArray) JSONValue.parse(res.getString("onCSkill"));
                    this.onCSkill = new byte[]{-1, -1, -1, -1, -1};
                    if (j != null) {
                        for (int t = 0; t < this.onCSkill.length; t++) {
                            if (t < j.size()) {
                                this.onCSkill[t] = ((Long) j.get(t)).byteValue();
                            }
                        }
                    }
                    j = (JSONArray) JSONValue.parse(res.getString("onKSkill"));
                    this.onKSkill = new byte[]{-1, -1, -1};
                    if (j != null) {
                        for (int t = 0; t < this.onKSkill.length; t++) {
                            if (t < j.size()) {
                                this.onKSkill[t] = ((Long) j.get(t)).byteValue();
                            }
                        }
                    }
                    JSONArray effects = (JSONArray) JSONValue.parse(res.getString("effect"));
                    int size = effects.size();
                    EffectManager em = getEm();
                    for (int i = 0; i < size; i++) {
                        JSONObject obj = (JSONObject) effects.get(i);
                        int id = Integer.parseInt(obj.get("id").toString());
                        int param = Integer.parseInt(obj.get("param").toString());
                        long startAt = 0;
                        long endAt = 0;
                        if (obj.containsKey("timeStart")) {
                            int timeStart = Integer.parseInt(obj.get("timeStart").toString());
                            int timeLength = Integer.parseInt(obj.get("timeLength").toString());
                            startAt = System.currentTimeMillis();
                            endAt = startAt + ((timeLength - timeStart) * 1000);

                        } else {
                            startAt = Long.parseLong(obj.get("start_at").toString());
                            endAt = Long.parseLong(obj.get("end_at").toString());
                        }
                        Effect eff = new Effect(id, startAt, endAt, param);
                        if (!eff.isExpired()) {
                            em.effect(eff, true);
                            em.add(eff);
                        }
                    }
                    Clazz clazz = GameData.getInstance().findClass(classId);
                    this.school = clazz.getName();
                    setFashionStrategy(new FashionFromEquip());
                    setAbilityStrategy(new AbilityFromEquip());
                    setLoadFinish(true);
                    initListCanEnterMap();
                } else {
                    PreparedStatement stmt2 = DbManager.getInstance().getConnection(DbManager.SERVER)
                            .prepareStatement("INSERT INTO `clone_char`(`id`) VALUES (?);");
                    stmt2.setInt(1, this.id);
                    stmt2.executeUpdate();
                    load();
                }
            } finally {
                res.close();
                stmt.close();
            }

        } catch (NumberFormatException | SQLException e) {
            Log.error("load clone char err", e);
        }
        return true;
    }

    @Override
    public void addMp(double add) {
        if (!isNhanBan) {
            this.mp += add;
        }
    }

    @Override
    public void startDie() {
        if (!isNhanBan) {
            this.isDead = true;
            human.switchToMe();
            return;
        }
    }

    @Override
    public void saveData() {
        if (isLoadFinish() && !saving) {
            saving = true;
            try {
                Log.debug("save data clone " + this.name);
                JSONObject data = new JSONObject();
                data.put("exp", this.exp);
                data.put("limitKyNangSo", this.limitKyNangSo);
                data.put("limitTiemNangSo", this.limitTiemNangSo);
                data.put("limitBangHoa", this.limitBangHoa);
                data.put("limitPhongLoi", this.limitPhongLoi);
                data.put("tayTiemNang", this.tayTiemNang);
                data.put("tayKyNang", this.tayKyNang);

                JSONArray equipment = new JSONArray();
                for (int i = 0; i < 16; i++) {
                    try {
                        if (this.equipment[i] != null) {
                            equipment.add(this.equipment[i].toJSONObject());
                        }
                    } catch (Exception e) {
                    }
                }
                JSONArray fashion = new JSONArray();
                for (int i = 0; i < 16; i++) {
                    try {
                        if (this.fashion[i] != null) {
                            fashion.add(this.fashion[i].toJSONObject());
                        }
                    } catch (Exception e) {
                    }
                }
                JSONArray mounts = new JSONArray();
                for (int i = 0; i < 5; i++) {
                    try {
                        if (this.mount[i] != null) {
                            mounts.add(this.mount[i].toJSONObject());
                        }
                    } catch (Exception e) {
                    }
                }
                JSONArray bijuus = new JSONArray();
                for (int i = 0; i < 5; i++) {
                    try {
                        if (this.bijuu[i] != null) {
                            bijuus.add(this.bijuu[i].toJSONObject());
                        }
                    } catch (Exception e) {
                    }
                }
                JSONArray skill = new JSONArray();
                if (this.vSkill != null && this.vSkill.size() > 0) {
                    try {
                        for (Skill s : this.vSkill) {
                            skill.add(s.toJSONObject());
                        }
                    } catch (Exception e) {
                    }
                }
                String onOSkill = Arrays.toString(this.onOSkill).replace(" ", "");
                String onCSkill = Arrays.toString(this.onCSkill).replace(" ", "");
                String onKSkill = Arrays.toString(this.onKSkill).replace(" ", "");
                JSONArray potentials = new JSONArray();
                if (this.potential != null) {
                    for (int i = 0; i < 4; i++) {
                        potentials.add(this.potential[i]);
                    }
                }

                JSONArray effects = getEm().toJSONArray();
                String jEquipment = equipment.toJSONString();
                String jData = data.toJSONString();
                String jPotentials = potentials.toJSONString();
                String jFashion = fashion.toJSONString();
                String jMounts = mounts.toJSONString();
                String jEffects = effects.toJSONString();
                String jSkills = skill.toJSONString();
                String jBijuus = bijuus.toJSONString();

                try {
                    boolean flag = false;
                    Long last = GameData.HASH_MAP.get(this.id);
                    long now = System.currentTimeMillis();
                    if (last != null) {
                        if (now - last < 300000) {
                            flag = true;
                        }
                    }
                    if (!flag) {
                        MongoCollection collection = MongoDbConnection.getCollection("clone_player");
                        Document document = new Document();
                        document.put("player_id", this.id);
                        document.put("equipment", jEquipment);
                        document.put("mount", jMounts);
                        document.put("fashion", jFashion);
                        document.put("bijuu", jBijuus);
                        document.put("update_at", now);
                        collection.insertOne(document);
                        GameData.HASH_MAP.put(this.id, now);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                PreparedStatement stmt = DbManager.getInstance().getConnection(DbManager.SAVE_DATA).prepareStatement(
                        "UPDATE `clone_char` SET `data` = ?, `point` = ?, `potential` = ?, `spoint` = ?, `equiped` = ?, `fashion` = ?, `mount` = ?, `effect` = ?, `onCSkill` = ?, `onKSkill` = ?, `onOSkill` = ?, `class` = ?, `skill` = ?, `select_skill` = ?, `bijuu`= ? WHERE `id` = ? LIMIT 1;");
                stmt.setString(1, jData);
                stmt.setDouble(2, this.potentialPoint);
                stmt.setString(3, jPotentials);
                stmt.setInt(4, (int)this.skillPoint);
                stmt.setString(5, jEquipment);
                stmt.setString(6, jFashion);
                stmt.setString(7, jMounts);
                stmt.setString(8, jEffects);
                stmt.setString(9, onCSkill);
                stmt.setString(10, onKSkill);
                stmt.setString(11, onOSkill);
                stmt.setInt(12, this.classId);
                stmt.setString(13, jSkills);
                stmt.setInt(14, this.selectSkillId);
                stmt.setString(15, jBijuus);
                stmt.setInt(16, this.id);
                stmt.executeUpdate();
                stmt.close();
            } catch (Exception e) {
                Log.error("save data cloneName: " + this.name, e);
            } finally {
                saving = false;
            }
        }
    }

    @Override
    public void setAbility() {
        super.setAbility();
        if (this.isNhanBan) {
            this.damage = this.damage * damePercent / 100; // human.dame 
            this.damage2 = damage;
            this.damage2 -= damage / 10;
        }
    }

    public void move(short x, short y) {
        this.x = x;
        this.y = y;
        zone.getService().playerMove(this);
    }

    @Override
    public void selectSkill(short templateId) {
        try {
            super.selectSkill(templateId);
        } finally {
            if (selectedSkill != null) {
                if (selectedSkill.template.type == Skill.SKILL_CLICK_USE_ATTACK) {
                    selectSkillId = (short) selectedSkill.template.id;
                }
            }
        }
    }

    public void create() {
        this.isDead = false;
        setFashion();
        setAbility();
        this.hp = this.maxHP;
        this.mp = this.maxMP;
        selectSkill(this.selectSkillId);
        setXY(human.x, human.y);

    }

    @Override
    public void switchToMe() {
        this.box = human.box;
        this.bag = human.bag;
        this.coin = human.coin;
        this.coinInBox = human.coinInBox;
        this.yen = human.yen;
        this.language = human.language;
        this.friends = human.friends;
        this.enemy = human.enemy;
        this.numberCellBag = human.numberCellBag;
        this.numberCellBox = human.numberCellBox;
        this.invite = human.invite;
        this.isDead = false;
        Controller contrl = (Controller) user.session.getMessageHandler();
        contrl.setChar(this);
        Service sv = user.session.getService();
        sv.setChar(this);
        human.outParty();
        this.timeCountDown = human.timeCountDown;
        human.getEm().clearScrAllEffect(sv, zone.getService(), human);
        Zone z = human.zone;
        human.outZone();
        isNhanBan = false;
        create();
        z.join(this);
        getService().sendSkillShortcut("OSkill", onOSkill, (byte) 1);
        getService().sendSkillShortcut("KSkill", onKSkill, (byte) 1);
        getService().sendSkillShortcut("CSkill", onCSkill, (byte) 1);
        getService().updateInfoMe();
        getService().onBijuuInfo(id, bijuu);
        getEm().displayAllEffect(sv, zone.getService(), clone);
        getService().loadMount(this);
        isUpdate = true;
    }

    @Override
    public void close() {
        this.isDead = true;
    }

    @Override
    public void addCoin(long xu) {
        human.addCoin(xu);
    }

    @Override
    public void addYen(long yen) {
        human.addYen(yen);
    }

    @Override
    public Service getService() {
        if (isNhanBan) {
            return NoService.getInstance();
        } else {
            return human.getService();
        }
    }

    @Override
    public void updateEveryHalfSecond() {
        super.updateEveryHalfSecond();
        if (!isDead) {
            if (isNhanBan && classId == 6) {
                synchronized (vSkillFight) {
                    for (Skill skill : vSkillFight) {
                        if (!skill.isCooldown() && skill.template.type == Skill.SKILL_CLICK_USE_BUFF) {
                            human.useSkillBuff((byte) (human.x > this.x ? 1 : -1), skill);
                        }
                    }
                }
            }
        }
    }

}
