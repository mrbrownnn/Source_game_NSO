
package com.nsoz.clan;

import com.nsoz.api.Dao;
import com.nsoz.db.jdbc.DbManager;
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

/**
 *
 * @author Admin
 */
public class MemberDAO implements Dao<Member> {

    private Clan clan;
    private List<Member> members = new ArrayList<>();

    public MemberDAO(Clan clan) {
        this.clan = clan;
    }

    public void load() {
        try {
            Connection conn = DbManager.getInstance().getConnection(DbManager.GAME);
            Date now = new Date();
            PreparedStatement st = conn.prepareStatement("SELECT * FROM `clan_member` WHERE `clan` = ?");
            try {
                st.setInt(1, clan.id);
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    byte classId = rs.getByte("class_id");
                    int level = rs.getInt("level");
                    String name = rs.getString("name");
                    byte type = rs.getByte("type");
                    int point_clan = rs.getInt("point_clan");
                    int point_clan_week = rs.getInt("point_clan_week");
                    Date updated_at = rs.getDate("updated_at");
                    if (!NinjaUtils.isSameWeek(now, updated_at)) {
                        point_clan_week = 0;
                        PreparedStatement stmt3 = DbManager.getInstance().getConnection(DbManager.SERVER).prepareStatement(
                                "UPDATE `clan_member` SET `point_clan_week` = 0, `updated_at` = ? WHERE `id` = ? LIMIT 1;");
                        stmt3.setString(1, NinjaUtils.dateToString(new Date(), "yyyy-MM-dd"));
                        stmt3.setInt(2, id);
                        stmt3.executeUpdate();
                        stmt3.close();
                    }
                    Member member = Member.builder()
                            .id(id)
                            .classId(classId)
                            .level(level)
                            .type(type)
                            .name(name)
                            .pointClan(point_clan)
                            .pointClanWeek(point_clan_week)
                            .build();
                    members.add(member);
                }
                rs.close();
            } finally {
                st.close();
            }
        } catch (SQLException ex) {
            Log.error("load member fail");
        }
    }

    @Override
    public Optional<Member> get(long id) {
        return members.stream().filter(mem -> mem.getId() == id).findFirst();
    }

    @Override
    public List<Member> getAll() {
        return members;
    }

    @Override
    public void save(Member member) {
        try {
            Connection conn = DbManager.getInstance().getConnection(DbManager.GAME);
            PreparedStatement st = conn.prepareStatement(
                    "INSERT INTO `clan_member` (`name`, `class_id`, `level`, `clan`, `type`) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = null;
            try {
                st.setString(1, member.getName());
                st.setInt(2, member.getClassId());
                st.setInt(3, member.getLevel());
                st.setInt(4, clan.id);
                st.setInt(5, member.getType());
                st.executeUpdate();
                rs = st.getGeneratedKeys();
                if (rs.next()) {
                    member.setId(rs.getInt(1));
                }
            } finally {
                st.close();
                if (rs != null) {
                    rs.close();
                }
            }
            PreparedStatement stmt2 = conn.prepareStatement("UPDATE `players` SET `clan` = ? WHERE `id` = ? LIMIT 1;");
            try {
                stmt2.setInt(1, clan.getId());
                stmt2.setInt(2, member.getChar().id);
                stmt2.executeUpdate();
            } finally {
                stmt2.close();
            }
            members.add(member);
        } catch (SQLException ex) {
            Log.error("save err");
        }
    }

    @Override
    public void update(Member member) {
        if (!member.isSaving()) {
            member.setSaving(true);
            try {
                Connection conn = DbManager.getInstance().getConnection(DbManager.GAME);
                PreparedStatement stmt2 = conn.prepareStatement(
                        "UPDATE `clan_member` SET `level` = ?, `point_clan` = ?, `point_clan_week` = ? WHERE `id` = ? LIMIT 1");
                stmt2.setInt(1, member.getLevel());
                stmt2.setInt(2, member.getPointClan());
                stmt2.setInt(3, member.getPointClanWeek());
                stmt2.setInt(4, member.getId());
                stmt2.executeUpdate();
                stmt2.close();
            } catch (SQLException ex) {
                Log.error("update member clan err", ex);
            } finally {
                 member.setSaving(false);
            }
        }
    }

    @Override
    public void delete(Member member) {
        try {
            Connection conn = DbManager.getInstance().getConnection(DbManager.GAME);
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM `clan_member` WHERE `id` = ?;");
            try {
                stmt.setInt(1, member.getId());
                stmt.executeUpdate();
            } finally {
                stmt.close();
            }
            PreparedStatement st = conn
                    .prepareStatement("UPDATE `players` SET `clan` = 0 WHERE `name` = ? LIMIT 1;");
            try {
                st.setString(1, member.getName());
                st.executeUpdate();
            } finally {
                st.close();
            }
            PreparedStatement ps2 = conn.prepareStatement("UPDATE `players` SET `clan` = ?, `data` = JSON_SET(`data`, '$.lastTimeOutClan', ?) WHERE `name` = ? LIMIT 1;");
            try {
                ps2.setInt(1, 0);
                ps2.setLong(2, System.currentTimeMillis());
                ps2.setString(3, member.getName());
                ps2.executeUpdate();
            } finally {
                ps2.close();
            }
            if (member.getType() == Clan.TYPE_TOCPHO) {
                PreparedStatement stmt3 = conn
                        .prepareStatement("UPDATE `clan` SET `assist_name` = ? WHERE `id` = ? LIMIT 1;");
                try {
                    stmt3.setString(1, "");
                    stmt3.setInt(2, this.clan.id);
                    stmt3.executeUpdate();
                } finally {
                    stmt3.close();
                }
            }
            get(member.getId()).ifPresent(mem -> members.remove(mem));
        } catch (SQLException ex) {
            Log.error("delete err");
        }
    }


}
