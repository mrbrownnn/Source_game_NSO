
package com.nsoz.clan;

import com.nsoz.api.Dao;
import com.nsoz.db.jdbc.DbManager;
import com.nsoz.item.Item;
import com.nsoz.model.ThanThu;
import com.nsoz.option.ItemOption;
import com.nsoz.server.Config;
import com.nsoz.util.Log;
import com.nsoz.util.NinjaUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.time.DateUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author Admin
 */
public class ClanDAO implements Dao<Clan> {

    private List<Clan> clans = new ArrayList<>();

    public boolean checkExist(String name) {
        synchronized (clans) {
            for (Clan clan : clans) {
                if (clan.name.equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Optional<Clan> get(long id) {
        return clans.stream().filter(clan -> clan.id == id).findFirst();
    }

    @Override
    public List<Clan> getAll() {
        return this.clans;
    }

    @Override
    public void save(Clan clan) {
        try {
            Connection conn = DbManager.getInstance().getConnection(DbManager.GAME);
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO `clan` (`name`, `main_name`, `log`, `box`, `alert`, `than_thu`, `server_id`) VALUES (?, ?, ?, ?, ?, '[]', ?)", Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = null;
            try {
                ps.setString(1, clan.name);
                ps.setString(2, clan.main_name);
                ps.setString(3, clan.getLog());
                ps.setString(4, "[]");
                ps.setString(5, "");
                ps.setInt(6, Config.getInstance().getServerID());
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    clan.id = rs.getInt(1);
                }
            } finally {
                ps.close();
                if (rs != null) {
                    rs.close();
                }
            }
            clans.add(clan);
        } catch (SQLException ex) {
            Log.error("save err: " + ex.getMessage(), ex);
        }
    }

    public void load() {
        try {
            Log.info("Loading clan data");
            Connection conn = DbManager.getInstance().getConnection(DbManager.GAME);
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `clan` where `server_id` = ?");
            stmt.setInt(1, Config.getInstance().getServerID());
            Date now = new Date();
            try {
                ResultSet res = stmt.executeQuery();
                while (res.next()) {
                    Clan clan = new Clan();
                    clan.id = res.getInt("id");
                    clan.name = res.getString("name");
                    clan.assist_name = res.getString("assist_name");
                    clan.main_name = res.getString("main_name");
                    clan.alert = res.getString("alert");
                    clan.level = res.getByte("level");
                    clan.coin = res.getInt("coin");
                    clan.itemLevel = res.getByte("item_level");
                    clan.exp = res.getInt("exp");
                    clan.openDun = res.getByte("open_dun");
                    clan.use_card = res.getByte("use_card");
                    clan.reg_date = res.getDate("reg_date");
                    clan.log = res.getString("log");
                    clan.thanThus = new ArrayList<>();
                    if (res.getObject("than_thu") != null) {
                        JSONArray arr = (JSONArray) JSONValue.parse(res.getString("than_thu"));
                        for (int i = 0; i < arr.size(); i++) {
                            ThanThu thanThu = new ThanThu();
                            thanThu.load((JSONObject) arr.get(i));
                            clan.thanThus.add(thanThu);
                        }
                    }
                    Date updated_at = res.getDate("updated_at");
                    clan.loadItem((JSONArray) JSONValue.parse(res.getString("box")));

                    if (!DateUtils.isSameDay(now, updated_at)) {
                        clan.openDun = 1;
                        clan.use_card = 1;
                        PreparedStatement stmt3 = conn.prepareStatement(
                                "UPDATE `clan` SET `open_dun` = 1,`use_card` = 1, `updated_at` = ? WHERE `id` = ? LIMIT 1;");
                        stmt3.setString(1, NinjaUtils.dateToString(now, "yyyy-MM-dd"));
                        stmt3.setInt(2, clan.id);
                        stmt3.executeUpdate();
                        stmt3.close();
                    }
                    clan.memberDAO.load();
                    clans.add(clan);
                }
                res.close();
            } finally {
                stmt.close();
            }
            Log.info("Load clan data successfully");
        } catch (SQLException ex) {
            Log.error("load fail", ex);
        }
    }

    @Override
    public void update(Clan clan) {
        if (!clan.isSaving()) {
            clan.setSaving(true);
            try {
                JSONArray jThanThu = new JSONArray();
                for (ThanThu thanThu : clan.thanThus) {
                    jThanThu.add(thanThu.toJSONObject());
                }
                Connection conn = DbManager.getInstance().getConnection(DbManager.GAME);
                PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE `clan` SET `coin` = ?, `level` = ?, `exp` = ?, `item_level` = ?, `open_dun` = ?, `use_card` = ?, `box` = ?, `log` = ?, `than_thu` = ? WHERE `id` = ? LIMIT 1;");
                try {
                    stmt.setInt(1, clan.coin);
                    stmt.setInt(2, clan.level);
                    stmt.setInt(3, clan.exp);
                    stmt.setInt(4, clan.itemLevel);
                    stmt.setInt(5, clan.openDun);
                    stmt.setInt(6, clan.use_card);
                    JSONArray boxs = new JSONArray();
                    for (Item itm : clan.items) {
                        if (itm == null) {
                            continue;
                        }
                        JSONObject item = new JSONObject();
                        item.put("id", itm.id);
                        item.put("expire", itm.expire);
                        item.put("sys", itm.sys);
                        item.put("isLock", itm.isLock);
                        item.put("yen", itm.yen);
                        if (itm.template.isTypeBody() || itm.template.isTypeMount() || itm.template.isTypeNgocKham()) {
                            item.put("upgrade", itm.upgrade);
                            JSONArray abilitys = new JSONArray();
                            if (itm.options != null) {
                                for (ItemOption option : itm.options) {
                                    JSONArray ability = new JSONArray();
                                    ability.add(option.optionTemplate.id);
                                    ability.add(option.param);
                                    abilitys.add(ability);
                                }
                            }
                            item.put("options", abilitys);
                        }
                        item.put("quantity", itm.getQuantity());
                        boxs.add(item);
                    }
                    stmt.setString(7, boxs.toJSONString());
                    stmt.setString(8, clan.log);
                    stmt.setString(9, jThanThu.toJSONString());
                    stmt.setInt(10, clan.id);
                    stmt.executeUpdate();
                } finally {
                    stmt.close();
                }
                List<Member> members = clan.memberDAO.getAll();
                synchronized (members) {
                    for (Member member : members) {
                        clan.memberDAO.update(member);
                    }
                }
            } catch (SQLException ex) {
                Log.error("update clan fail", ex);
            } finally {
                clan.setSaving(false);
            }
        }
    }

    @Override
    public void delete(Clan clan) {
        try {
            Connection conn = DbManager.getInstance().getConnection(DbManager.GAME);
            PreparedStatement ps = conn.prepareStatement("DELETE FROM `clan` WHERE `id` = ?;");
            try {
                ps.setInt(1, clan.id);
                ps.executeUpdate();
            } finally {
                ps.close();
            }
            get(clan.id).ifPresent(exist -> clans.remove(exist));
        } catch (SQLException ex) {
            Log.error("delete clan err", ex);
        }
    }

}
