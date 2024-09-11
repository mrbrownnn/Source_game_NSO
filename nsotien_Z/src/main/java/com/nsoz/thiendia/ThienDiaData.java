
package com.nsoz.thiendia;

import com.nsoz.db.jdbc.DbManager;
import com.nsoz.server.Config;
import com.nsoz.util.Log;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 *
 * @author Admin
 */
public class ThienDiaData {

    @Getter
    private final List<Ranking> rankings;
    @Getter
    private final int id;
    @Getter
    private final String name;
    @Getter
    private final int levelMin, levelMax;
    @Getter
    private final byte[] clazzs;

    @Builder
    public ThienDiaData(int id, String name, int levelMin, int levelMax, byte... clazzs) {
        this.id = id;
        this.name = name;
        this.levelMin = levelMin;
        this.levelMax = levelMax;
        this.clazzs = clazzs;
        this.rankings = new ArrayList<>();
        load();
        sort();
    }

    public Ranking getRankedByName(String name) {
        synchronized (rankings) {
            for (Ranking ranking : rankings) {
                if (ranking.getName().equals(name)) {
                    return ranking;
                }
            }
        }
        return null;
    }

    public void updateData() {
        try {
            synchronized (rankings) {
                PreparedStatement ps = DbManager.getInstance().getConnection(DbManager.SERVER).prepareStatement("UPDATE `ranking_list` SET `match_id`=?,`rank_at`=? WHERE `id` = ?");
                for (Ranking ranking : rankings) {
                    ps.setInt(1, getId());
                    ps.setInt(2, ranking.getRanked());
                    ps.setInt(3, ranking.getId());
                    ps.addBatch();
                }
                ps.executeBatch();
                ps.close();
            }
        } catch (SQLException ex) {
            Log.error("updateData err: " + ex.getMessage(), ex);
        }
    }

    public void load() {
        try {
            this.rankings.clear();
            PreparedStatement ps = DbManager.getInstance().getConnection(DbManager.SERVER).prepareStatement("SELECT ranking_list.id, players.id as player_id, players.name, ranking_list.rank_at FROM `ranking_list` INNER JOIN `players` ON `ranking_list`.`player_id` = `players`.`id` WHERE ranking_list.match_id = ? AND ranking_list.rank_at != -1 AND ranking_list.server_id = ? ORDER BY ranking_list.rank_at ASC LIMIT 1000;");
            ps.setInt(1, this.id);
            ps.setInt(2, Config.getInstance().getServerID());
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int playerID = rs.getInt("player_id");
                    String name = rs.getString("name");
                    int rankAt = rs.getInt("rank_at");
                    Ranking ranking = Ranking.builder()
                            .id(id)
                            .playerId(playerID)
                            .name(name)
                            .ranked(rankAt)
                            .build();
                    addRanking(ranking);
                }
            } finally {
                rs.close();
                ps.close();
            }
        } catch (SQLException ex) {
            Log.error("load err: " + ex.getMessage(), ex);
        }
    }

    public void addRanking(Ranking ranking) {
        synchronized (rankings) {
            rankings.add(ranking);
        }
    }

    public void sort() {
        synchronized (rankings) {
            rankings.sort((o1, o2) -> (new Integer(o1.getRanked()).compareTo((new Integer(o2.getRanked())))));
            int i = 1;
            for (Ranking rank : rankings) {
                rank.setRanked(i++);
            }
        }
    }

    public Ranking getRankingByPlayerID(int playerID) {
        synchronized (rankings) {
            for (Ranking ranking : rankings) {
                if (ranking.getPlayerId() == playerID) {
                    return ranking;
                }
            }
        }
        return null;
    }

    public boolean removeRankingByID(int playerId) {
        synchronized (rankings) {
            int index = 0;
            for (Ranking ranking : rankings) {
                if (ranking.getPlayerId() == playerId) {
                    rankings.remove(index);
                    return true;
                }
                index++;
            }
        }
        return false;
    }
}
