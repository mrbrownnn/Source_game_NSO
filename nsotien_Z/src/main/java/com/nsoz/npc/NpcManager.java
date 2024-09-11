package com.nsoz.npc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import com.nsoz.constants.SQLStatement;
import com.nsoz.db.jdbc.DbManager;
import com.nsoz.util.Log;
import com.nsoz.util.ProgressBar;
import java.sql.SQLException;

import lombok.Getter;
import org.json.JSONException;

public class NpcManager {

    private static final NpcManager instance = new NpcManager();

    public static NpcManager getInstance() {
        return instance;
    }

    @Getter
    private List<NpcTemplate> npcTemplates = new ArrayList<>();

    ;

    public boolean load() {
        try {
            Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
            PreparedStatement stmt = conn.prepareStatement(SQLStatement.GET_ALL_NPC, ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = stmt.executeQuery();
            resultSet.last();
            ProgressBar pb = new ProgressBar("Loading Npc", resultSet.getRow());
            resultSet.beforeFirst();
            while (resultSet.next()) {
                try {
                    NpcTemplate npc = new NpcTemplate();
                    npc.npcTemplateId = resultSet.getInt("id");
                    npc.name = resultSet.getString("name");
                    npc.headId = resultSet.getShort("head");
                    npc.bodyId = resultSet.getShort("body");
                    npc.legId = resultSet.getShort("leg");
                    JSONArray jArr = new JSONArray(resultSet.getString("menu"));
                    int size = jArr.length();
                    npc.menu = new String[size][];
                    for (int i = 0; i < size; i++) {
                        JSONArray jArr2 = (JSONArray) jArr.get(i);
                        int size2 = jArr2.length();
                        npc.menu[i] = new String[size2];
                        for (int a = 0; a < size2; a++) {
                            npc.menu[i][a] = jArr2.getString(a);
                        }
                    }
                    add(npc);
                    pb.setExtraMessage(npc.name + " finished!");
                    pb.step();
                } catch (SQLException | JSONException e) {
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
            Log.error("load npc err", e);
            return false;
        }
        return true;
    }

    public void add(NpcTemplate npc) {
        npcTemplates.add(npc);
    }

    public void remove(NpcTemplate npc) {
        npcTemplates.remove(npc);
    }

    public NpcTemplate find(int npcTemplateId) {
        for (NpcTemplate npc : npcTemplates) {
            if (npc.npcTemplateId == npcTemplateId) {
                return npc;
            }
        }
        return null;
    }
}
