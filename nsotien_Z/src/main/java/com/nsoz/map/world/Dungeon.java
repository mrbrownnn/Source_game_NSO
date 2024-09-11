package com.nsoz.map.world;

import com.nsoz.map.zones.Zone;
import java.util.List;

import com.nsoz.map.Map;
import com.nsoz.map.TileMap;
import com.nsoz.map.zones.Cave;
import com.nsoz.model.Char;
import com.nsoz.mob.Mob;
import com.nsoz.constants.MobName;
import com.nsoz.map.MapManager;
import com.nsoz.constants.TaskName;
import com.nsoz.util.NinjaUtils;
import java.util.ArrayList;

public class Dungeon extends World {

    public static final int[][] MAP_DUNGEON = { { 91, 92, 93 }, { 94, 95, 96, 97 }, { 105, -1, 109 }, { 114, 115, 116 },
            { 125, 126, 127, 128 }, { -2 } };
    public static final int[] MAP_DUNGEON_5X = { 106, 107, 108 };
    public static final int[] MAP_DUNGEON_9X = { 157, 158, 159 };
    public static final int[][] INFO = { { 91, 35, 264, 0, 35 }, { 94, 35, 408, 1, 45 }, { 105, 35, 360, 2, 55 },
            { 114, 35, 576, 3, 65 }, { 125, 35, 552, 4, 75 }, { 157, 60, 264, 5, 95 } };
    public static final int[] REWARD = { 272, 272, 282, 282, 282, 647 };
    public static final int[] POINT = { 10, 10, 33, 33, 33, 27 };
    public static List<Dungeon> dungeons = new ArrayList<>();

    public static void addDungeon(Dungeon dungeon) {
        synchronized (dungeons) {
            dungeons.add(dungeon);
        }
    }

    public static Dungeon findDungeonById(int id) {
        synchronized (dungeons) {
            for (Dungeon dun : dungeons) {
                if (dun.id == id) {
                    return dun;
                }
            }
        }
        return null;
    }

    public ArrayList<Integer> listCharId;
    public int timeFinish = 0;
    public int level;
    public long timeCreate;
    public int index = 0;
    public int levelMonster = 0;
    public int time;
    public boolean bossAppeared = false;
    private boolean finished;

    public Dungeon(int level, int time) {
        setType(World.DUNGEON);
        this.name = "Dungeon";
        generateId();
        this.listCharId = new ArrayList<>();
        this.countDown = time;
        this.time = time;
        this.level = level;
        open();
        this.timeCreate = System.currentTimeMillis();
        initFinished = true;
    }

    @Override
    public void update() {
        if (countDown <= 0) {
            close();
            return;
        }
        if (level == 5) {
            boolean isMonsterLive = false;
            for (Zone zone : zones) {
                // tối ưu lại hang 9x
                List<Mob> monsters = zone.getLivingMonsters();
                int numberLiving = monsters.size();
                if (numberLiving > 0) {
                    boolean isBossLive = false;
                    for (Mob mob : monsters) {
                        if (mob.isBoss) {
                            isBossLive = true;
                            isMonsterLive = true;
                            break;
                        }
                    }
                    if (!isBossLive) {
                        zone.killAllMonsters();
                        addPointPB(numberLiving);
                    }
                }
            }
            if (!isMonsterLive) {
                if (!finished) {
                    int rand = NinjaUtils.nextInt(2);
                    if (rand == 0) {
                        finish();
                    } else {
                        int index = NinjaUtils.nextInt(MAP_DUNGEON_9X.length);
                        Zone zone = zones.get(index);
                        zone.recoveryAllMonsters(0);
                    }
                }
            }
        } else {
            Zone zone = zones.get(index);
            if (zone != null) {
                List<Mob> mobs = zone.getLivingMonsters();
                if (mobs.isEmpty()) {
                    if ((index == MAP_DUNGEON[level].length - 1) || (level == 2 && index == 4)) {
                        if (level == 4 && !bossAppeared) {
                            int size = zone.getMonsters().size();
                            Mob mob = new Mob(size, (short) 138, 120000000, (short) 75, (short) 756,
                                    (short) 672, false, true, zone);
                            size++;
                            Mob mob2 = new Mob(size, (short) 138, 120000000, (short) 75, (short) 708,
                                    (short) 672, false, true, zone);
                            zone.addMob(mob);
                            zone.addMob(mob2);
                            bossAppeared = true;
                        } else {
                            finish();
                        }
                    } else if (level == 2 && index == 1) {
                        boolean isAllMonsterLive = false;
                        for (int i = 2; i <= MAP_DUNGEON_5X.length; i++) {
                            Zone z = zones.get(i);
                            if (z.getLivingMonsters().size() > 0) {
                                isAllMonsterLive = true;
                                break;
                            }
                        }
                        if (!isAllMonsterLive) {
                            index = 2;
                            open();
                            index = 4;
                        }
                    } else {
                        index++;
                        open();
                    }
                } else {
                    if (zone.tilemap.id == 114 || zone.tilemap.id == 115) {
                        if (mobs.get(0).hp > 100) {
                            boolean mobLive = false;
                            for (Mob mob : mobs) {
                                if (mob.template.id == MobName.TRUNG_TAM_SAC
                                        || mob.template.id == MobName.LAM_THACH_THAO) {
                                    mobLive = true;
                                    break;
                                }
                            }
                            if (!mobLive) {
                                zone.setHPAllMonsters(100);
                            }
                        }
                    }
                }
            }
        }
        countDown--;
    }

    public void open() {
        int mapID = MAP_DUNGEON[this.level][index];
        if (mapID == -1) {
            for (int map5x : MAP_DUNGEON_5X) {
                Map map = MapManager.getInstance().find(map5x);
                Cave cave = new Cave(0, map.tilemap, map);
                cave.setWorld(this);
                addZone(cave);
                service.serverMessage(map.tilemap.name + " đã mở.");
            }
        } else if (mapID == -2) {
            for (int map9x : MAP_DUNGEON_9X) {
                Map map = MapManager.getInstance().find(map9x);
                Cave cave = new Cave(0, map.tilemap, map);
                cave.setWorld(this);
                addZone(cave);
                service.serverMessage(map.tilemap.name + " đã mở.");
            }
        } else {
            Map map = MapManager.getInstance().find(mapID);
            Cave cave = new Cave(0, map.tilemap, map);
            cave.setWorld(this);
            addZone(cave);
            if (index > 0) {
                service.serverMessage(map.tilemap.name + " đã mở.");
            }
        }
    }

    public void finish() {
        if (this.level == 3) {
            Zone zone = zones.get(index);
            levelMonster++;
            zone.recoveryAllMonsters(levelMonster);
        }
        if (finished) {
            return;
        }
        service.serverMessage("Hành trình khám phá hang động đã kết thúc, hãy đến Kanata để đánh giá và nhận thưởng.");
        List<Char> members = getMembers();
        members.forEach(_char -> {
            try {
                if (_char.isHuman) {
                    if (_char.taskId == TaskName.NV_HOAT_DONG_HANG_NGAY) {
                        if (_char.taskMain != null && _char.taskMain.index == 2) {
                            _char.updateTaskCount(1);
                        }
                    }
                    if (_char.clan != null) {
                        _char.addClanPoint(10);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        finished = true;
        timeFinish = time - countDown;
        if (this.level != 3) {
            countDown = 60;
            service.sendTimeInMap(countDown);
        }
    }

    public void joinZone(Char _char, int map) {
        for (Zone z : this.zones) {
            TileMap tilemap = z.tilemap;
            if (tilemap.id == map) {
                z.join(_char);
                return;
            }
        }
    }

    @Override
    public void addMember(Char _char) {
        super.addMember(_char);
        for (int id : this.listCharId) {
            if (id == _char.id) {
                return;
            }
        }
        this.listCharId.add(_char.id);
    }

    public void addPointPB(int point) {
        List<Char> members = getMembers();
        for (Char _char : members) {
            try {
                _char.updatePointPB(point);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void addExp(Char _c, long exp) {
        List<Char> members = getMembers();
        for (Char _char : members) {
            try {
                if (_char != _c) {
                    _char.addExp(exp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void close() {
        if (this.isClosed) {
            return;
        }
        List<Char> members = getMembers();
        for (Char _char : members) {
            try {
                if (_char.isCleaned) {
                    continue;
                }
                short[] xy = NinjaUtils.getXY(_char.mapBeforeEnterPB);
                _char.setXY(xy[0], xy[1]);
                _char.changeMap(_char.mapBeforeEnterPB);
                _char.serverMessage("Cửa hang động đã được khép lại.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.close();
    }

    @Override
    public boolean enterWorld(Zone pre, Zone next) {
        return !pre.tilemap.isDungeo() && next.tilemap.isDungeo();
    }

    @Override
    public boolean leaveWorld(Zone pre, Zone next) {
        return pre.tilemap.isDungeo() && !next.tilemap.isDungeo();
    }
}
