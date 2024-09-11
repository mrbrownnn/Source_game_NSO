package com.nsoz.map.world;

import com.nsoz.map.zones.Zone;
import com.nsoz.constants.MobName;
import com.nsoz.mob.Mob;
import com.nsoz.event.KoroKing;
import com.nsoz.event.Event;
import com.nsoz.map.Map;
import com.nsoz.map.TileMap;
import java.util.HashMap;
import java.util.List;

import com.nsoz.map.zones.ClanTerritory;
import com.nsoz.mob.MobTemplate;
import com.nsoz.model.Char;
import com.nsoz.map.MapManager;
import com.nsoz.mob.MobPosition;
import com.nsoz.mob.MobManager;
import com.nsoz.util.Log;
import com.nsoz.util.NinjaUtils;
import java.util.ArrayList;

public class Territory extends World {

    public static HashMap<Integer, Territory> territories = new HashMap<Integer, Territory>();
    public static final int[] MAPS = {80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 167};

    public ArrayList<Integer> listCharId;
    public ArrayList<Integer> listGuestId;
    public boolean started = false;
    private int countOpen = 0;
    private int nextMapId = -1;

    public Territory(int clanId) {
        setType(World.TERRITORY);
        this.name = "Territory";
        this.id = clanId;
        this.listCharId = new ArrayList<>();;
        this.listGuestId = new ArrayList<>();;
        this.countDown = 600; // 10 minutes
        initZone();
        initFinished = true;
    }

    public void initZone() {
        for (int mapId : MAPS) {
            Map map = MapManager.getInstance().find(mapId);
            ClanTerritory clanTerritory = new ClanTerritory(0, map.tilemap, map);
            clanTerritory.setWorld(this);
            addZone(clanTerritory);
        }
    }

    @Override
    public void update() {
        countDown--;
        if (countDown <= 0) {
            if (nextMapId != -1) {
                nextTurn();
            } else {
                close();
                return;
            }
        }
    }

    public void waitForNextTurn() {
        List<Char> members = getMembers();
        try {
            for (Char _char : members) {
                try {
                    if (_char != null && _char.clan != null) {
                        _char.addClanPoint(10);
                        _char.zone.getService().chat(_char.id, "Có gì đó không ổn ???");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.nextMapId = 167;
            this.countDown = 60;
            service.sendTimeInMap(countDown);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void nextTurn() {
        List<Char> members = getMembers();
        try {
            for (Char _char : members) {
                try {
                    if (_char != null && _char.clan != null) {
                        int posX = (int)NinjaUtils.nextInt(230, 425);
                        _char.setXY((short) posX, (short) 120);
                        _char.changeMap(this.nextMapId);
                        _char.serverMessage("Đây nơi mà các cố nhẫn giả đã từng đi mà không trở về, hãy cẩn thận với quái vật và không khí ở đây.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.nextMapId = -1;
            this.countDown = 1800;
            service.sendTimeInMap(countDown);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void finish() {
        countDown = 60;
        service.sendTimeInMap(countDown);
        service.serverMessage("Hành trình lãnh địa gia tộc đã kết thúc.");
        List<Char> members = getMembers();
        for (Char _char : members) {
            try {
                if (_char != null && _char.clan != null) {
                    _char.addClanPoint(20);
                    if (Event.isKoroKing()) {
                        KoroKing.addTrophy(_char, 20);
                    }
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
                _char.serverMessage("Lãnh địa gia tộc đã được khép lại.");
                _char.removeWorld(World.TERRITORY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        removeTerritory(this.id);
        super.close();
    }

    public void joinZone(Char _char, int map) {
        for (Zone z : this.zones) {
            TileMap tilemap = z.tilemap;
            if (tilemap.id == map) {
                try {
                    if (z != null) {
                        z.join(_char);
                        _char.getService().sendTimeInMap(countDown);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }

    public void openMap(Char _char) {
        int openMapId = -1;
        if (_char.mapId == 80) {
            for (Zone zone : this.zones) {
                if (zone.tilemap.id >= 80 && zone.tilemap.id <= 83) {
                    if (!zone.isOpened) {
                        zone.isOpened = true;
                        this.countDown = 3600;
                        this.started = true;
                        _char.getService().sendTimeInMap(countDown);
                        service.serverMessage("Cửa siêu tốc, cửa phản đòn, cửa né tránh đã mở.");
                    } else {
                        _char.serverMessage("Cửa này đã được mở");
                        return;
                    }
                }
            }
        } else if (_char.mapId >= 87 && _char.mapId <= 89) {
            this.countOpen++;
            service.serverMessage(_char.name + " đã cắm chìa khóa cơ quan " + _char.zone.tilemap.name);
            if (this.countOpen >= 3) {
                Zone mapOpen = find(90);
                if (mapOpen != null) {
                    if (!mapOpen.isOpened) {
                        mapOpen.isOpened = true;
                    } else {
                        _char.serverMessage("Cửa này đã được mở");
                    }
                }
            }
        } else {
            openMapId = _char.mapId + 3;
            Zone mapOpen = find(openMapId);
            if (mapOpen != null) {
                if (!mapOpen.isOpened) {
                    mapOpen.isOpened = true;
                    service.serverMessage(_char.name + " đã cắm chìa khóa cơ quan " + _char.zone.tilemap.name);
                } else {
                    _char.serverMessage("Cửa này đã được mở");
                }
            }
        }
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

    @Override
    public void addMember(Char _char) {
        super.addMember(_char);
        synchronized (this.listCharId) {
            for (int id : this.listCharId) {
                if (id == _char.id) {
                    return;
                }
            }
            this.listCharId.add(_char.id);
        }
    }

    public void addGuest(int charId) {
        synchronized (listGuestId) {
            this.listGuestId.add(charId);
        }
    }

    public boolean isInGuestList(int charId) {
        synchronized (listGuestId) {
            for (int id : this.listGuestId) {
                if (id == charId) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean isInTerritory(int charId) {
        synchronized (listCharId) {
            for (int id : this.listCharId) {
                if (id == charId) {
                    return true;
                }
            }
            return false;
        }
    }

    public static long now() {
        return System.currentTimeMillis();
    }

    public static void addTerritory(int clanId, Territory territory) {
        territories.put(clanId, territory);
    }

    public static Territory getTerritory(int clanId) {
        return territories.get(clanId);
    }

    public static void removeTerritory(int clanId) {
        territories.remove(clanId);
    }

    public static void createMoreMonsterInLastMap(Zone zone) {
        if (zone.isLastBossWasBorn) {
            return;
        }
        zone.isLastBossWasBorn = true;
        TileMap tilemap = zone.tilemap;
        zone.killAllMonsters();
        int incrementId = zone.getMonsters().size();
        List<MobPosition> monsters = tilemap.monsterCoordinates.subList(127, tilemap.monsterCoordinates.size());
        for (MobPosition mob : monsters) {
            MobTemplate template = MobManager.getInstance().find(mob.getId());
            int hp = template.hp;
            short level = template.level;
            hp = 2000000;
            level = 100;

            if (template.id == MobName.BAO_QUAN || template.id == MobName.TU_HA_MA_THAN) {
                hp = 2000000000;
                level = 100;
            }

            Mob monster = new Mob(incrementId++, mob.getId(), hp, level, mob.getX(), mob.getY(), false, template.isBoss(), zone);
            zone.addMob(monster);
        }
    }

    public static void checkEveryAttack(Char _c) {
        Char _char = _c.getOriginChar();
        if (_char.zone.tilemap.id == 167 && _char.zone.getLivingMonsters().isEmpty()) {
            if (_char.zone.isLastBossWasBorn) {
                Territory ter = null;
                try {
                    ter = (Territory) _char.findWorld(World.TERRITORY);
                    ter.finish();
                } catch (Exception e) {
                    Log.error(String.format("Error finish on char name %s | Is null: %b", _char.name, ter == null));
                    e.printStackTrace();
                }
            } else {
                Territory.createMoreMonsterInLastMap(_char.zone);
            }
        }
    }

    @Override
    public boolean enterWorld(Zone pre, Zone next) {
        return !pre.tilemap.isDungeoClan() && next.tilemap.isDungeoClan();
    }

    @Override
    public boolean leaveWorld(Zone pre, Zone next) {
        return pre.tilemap.isDungeoClan() && !next.tilemap.isDungeoClan();
    }
}
