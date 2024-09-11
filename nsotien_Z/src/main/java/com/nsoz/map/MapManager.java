package com.nsoz.map;

import com.nsoz.constants.NpcName;
import com.nsoz.constants.SQLStatement;
import com.nsoz.db.jdbc.DbManager;
import com.nsoz.event.Event;
import com.nsoz.lib.ParseData;
import com.nsoz.map.world.CandyBattlefield;
import com.nsoz.map.world.SevenBeasts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.nsoz.model.Char;
import com.nsoz.npc.NpcFactory;
import com.nsoz.server.Events;
import com.nsoz.map.zones.TalentShow;
import com.nsoz.map.zones.Zone;
import com.nsoz.mob.MobPosition;
import com.nsoz.util.ProgressBar;

import lombok.Getter;
import lombok.Setter;

public class MapManager {

    private static final MapManager instance = new MapManager();

    public static MapManager getInstance() {
        return instance;
    }

    @Getter
    private final List<TileMap> tileMaps = new ArrayList<>();

    @Getter
    private final ArrayList<Map> maps = new ArrayList<>();
    public War normalWar;
    public WarClan warClan;
    public War talentWar;
    public TalentShow talentShow;
    @Getter
    @Setter
    private CandyBattlefield candyBattlefield;
    private final ArrayList<SevenBeasts> sevenBeastses = new ArrayList<>();

    public void addSevenBeasts(SevenBeasts sevenBeasts) {
        synchronized (sevenBeastses) {
            sevenBeastses.add(sevenBeasts);
        }
    }

    public SevenBeasts findSevenBeasts(int id) {
        synchronized (sevenBeastses) {
            for (SevenBeasts sevenBeasts : sevenBeastses) {
                if (sevenBeasts.getId() == id) {
                    return sevenBeasts;
                }
            }
        }
        return null;
    }

    public void removeSevenBeasts(SevenBeasts sevenBeasts) {
        synchronized (sevenBeastses) {
            sevenBeastses.remove(sevenBeasts);
        }
    }

    public boolean load() {
        try {
            Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
            PreparedStatement stmt = conn.prepareStatement(SQLStatement.GET_ALL_MAP, ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = stmt.executeQuery();
            resultSet.last();
            ProgressBar pb = new ProgressBar("Loading Map", resultSet.getRow());
            resultSet.beforeFirst();
            while (resultSet.next()) {
                try {
                    TileMap map = new TileMap();
                    map.id = resultSet.getInt("id");
                    map.name = resultSet.getString("name");
                    map.tileId = resultSet.getByte("tileId");
                    map.bgId = resultSet.getByte("bgId");
                    map.type = resultSet.getByte("type");
                    map.zoneNumber = resultSet.getByte("zone_number");
                    map.npcs = new ArrayList<>();
                    JSONArray jArr = (JSONArray) JSONValue.parse(resultSet.getString("npc"));
                    int len = jArr.size();
                    for (int i = 0; i < len; i++) {
                        JSONObject obj = (JSONObject) jArr.get(i);
                        int status = ((Long) obj.get("status")).byteValue();
                        int cx = ((Long) obj.get("x")).shortValue();
                        int cy = ((Long) obj.get("y")).shortValue();
                        int npcTemplateId = ((Long) obj.get("templateId")).shortValue();
                        if (Events.event != Events.TET && npcTemplateId == 41) {
                            continue;
                        }
                        if (Events.event != Events.SUMMER && npcTemplateId == 42) {
                            continue;
                        }
                        if (!Event.isTrungThu() && npcTemplateId == NpcName.LONG_DEN_2) {
                            continue;
                        }
                        map.npcs.add(NpcFactory.getInstance().newNpc(i, npcTemplateId, cx, cy, status));

                    }
                    map.monsterCoordinates = new ArrayList<>();
                    jArr = (JSONArray) JSONValue.parse(resultSet.getString("monster"));
                    len = jArr.size();
                    for (int i = 0; i < len; i++) {
                        JSONObject obj = (JSONObject) jArr.get(i);
                        short templateId = ((Long) obj.get("templateId")).shortValue();
                        short x = ((Long) obj.get("x")).shortValue();
                        short y = ((Long) obj.get("y")).shortValue();
                        boolean boss = (boolean) obj.get("boss");
                        if (Events.event != Events.TET && templateId == 225) {
                            continue;
                        }

                        map.monsterCoordinates.add(new MobPosition(templateId, x, y, boss));
                    }
                    map.waypoints = new ArrayList<>();
                    jArr = (JSONArray) JSONValue.parse(resultSet.getString("waypoint"));
                    for (int i = 0; i < jArr.size(); i++) {
                        ParseData p = new ParseData((JSONObject) jArr.get(i));
                        Waypoint waypoint = new Waypoint();
                        waypoint.next = p.getShort("next");
                        waypoint.minX = p.getShort("minX");
                        waypoint.minY = p.getShort("minY");
                        waypoint.maxX = p.getShort("maxX");
                        waypoint.maxY = p.getShort("maxY");
                        waypoint.x = p.getShort("x");
                        waypoint.y = p.getShort("y");
                        map.waypoints.add(waypoint);
                    }
                    map.locationStand = new ArrayList<>();
                    jArr = (JSONArray) JSONValue.parse(resultSet.getString("locationStand"));
                    for (int i = 0; i < jArr.size(); i++) {
                        JSONArray arr = (JSONArray) jArr.get(i);
                        int x = Integer.parseInt(arr.get(0).toString());
                        int y = Integer.parseInt(arr.get(1).toString());
                        map.locationStand.add(new int[]{x, y});
                    }
                    jArr = (JSONArray) JSONValue.parse(resultSet.getString("item"));
                    int size = jArr.size();
                    map.items = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                        int idItem = Integer.parseInt(jArr.get(i).toString());
                        map.items.add(idItem);
                    }
                    jArr = (JSONArray) JSONValue.parse(resultSet.getString("behind"));
                    map.vItemTreeBehind = new ArrayList<>();
                    for (int i = 0; i < jArr.size(); i++) {
                        JSONObject obj = (JSONObject) jArr.get(i);
                        int idItem = Integer.parseInt(obj.get("id").toString());
                        int x = Integer.parseInt(obj.get("x").toString());
                        int y = Integer.parseInt(obj.get("y").toString());
                        ItemTree iTree = new ItemTree(x, y);
                        iTree.idTree = idItem;
                        map.vItemTreeBehind.add(iTree);
                    }
                    jArr = (JSONArray) JSONValue.parse(resultSet.getString("betwen"));
                    map.vItemTreeBetwen = new ArrayList<>();
                    for (int i = 0; i < jArr.size(); i++) {
                        JSONObject obj = (JSONObject) jArr.get(i);
                        int idItem = Integer.parseInt(obj.get("id").toString());
                        int x = Integer.parseInt(obj.get("x").toString());
                        int y = Integer.parseInt(obj.get("y").toString());
                        ItemTree iTree = new ItemTree(x, y);
                        iTree.idTree = idItem;
                        map.vItemTreeBetwen.add(iTree);
                    }
                    jArr = (JSONArray) JSONValue.parse(resultSet.getString("front"));
                    map.vItemTreeFront = new ArrayList<>();
                    for (int i = 0; i < jArr.size(); i++) {
                        JSONObject obj = (JSONObject) jArr.get(i);
                        int idItem = Integer.parseInt(obj.get("id").toString());
                        int x = Integer.parseInt(obj.get("x").toString());
                        int y = Integer.parseInt(obj.get("y").toString());
                        ItemTree iTree = new ItemTree(x, y);
                        iTree.idTree = idItem;
                        map.vItemTreeFront.add(iTree);
                    }
                    map.loadMapFromResource();
                    tileMaps.add(map);
                    pb.setExtraMessage(map.name + " finished!");
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
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void add(Map map) {
        maps.add(map);
    }

    public void remove(Map map) {
        maps.remove(map);
    }

    public Map find(int id) {
        for (Map map : maps) {
            if (map.id == id) {
                return map;
            }
        }
        return null;
    }

    public TileMap getTileMap(int index) {
        return tileMaps.get(index);
    }

    public void init() {
        ProgressBar pb = new ProgressBar("Init Map", tileMaps.size());
        for (TileMap tile : tileMaps) {
            try {
                pb.setExtraMessage("Đang tạo " + tile.name);
                add(new Map((short) tile.id));
                pb.setExtraMessage("Tạo hoàn tất " + tile.name);
                pb.step();
            } catch (Exception e) {
                pb.setExtraMessage(e.getMessage());
                pb.reportError();
                return;
            }
        }
        pb.setExtraMessage("Finished!");
        pb.reportSuccess();
    }

    public void joinZone(Char pl, int mapId, int zoneId) {
        Map m = find(mapId);
        if (m != null) {
            m.joinZone(pl, zoneId);
        }
    }

    public void outZone(Char pl) {
        if (pl != null && pl.zone != null) {
            Zone z = pl.zone;
            z.out(pl);
        }
    }

    public void close() {
        Map.running = false;
        synchronized (maps) {
            for (Map map : maps) {
                map.close();
            }
        }
    }

}
