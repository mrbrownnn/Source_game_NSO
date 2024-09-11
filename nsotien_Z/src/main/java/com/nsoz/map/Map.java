package com.nsoz.map;

import com.nsoz.constants.MapName;
import com.nsoz.map.zones.AikoRedRockArea;
import com.nsoz.map.zones.AkaCave;
import com.nsoz.map.zones.Battlefield;
import com.nsoz.map.zones.KugyouCave;
import com.nsoz.map.zones.TalentShow;
import com.nsoz.map.zones.Zone;
import com.nsoz.model.Char;
import com.nsoz.util.Log;
import com.nsoz.util.NinjaUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.Getter;

public class Map {

    public short id;
    @Getter
    private List<Zone> zones = new ArrayList<>();
    public TileMap tilemap;
    public static boolean running = true;
    public Thread threadUpdateChar, threadUpdateOther;
    public War war;
    public WarClan warClan;
    private ReadWriteLock lock;

    public Map(short id) {
        this.id = id;
        this.tilemap = MapManager.getInstance().getTileMap(id);
        lock = new ReentrantReadWriteLock();
        if (tilemap.tileId > 0) {
            if (!(id >= 98 && id <= 104)) {
                initZone();;
            }
            if (tilemap.tileId != 0) {
                update();
            }
        }
    }

    public void initZone() {
        zones.clear();
        for (int i = 0; i < this.tilemap.zoneNumber; i++) {
            Zone z = null;
            if (id == MapName.HANG_AKA) {
                z = new AkaCave((byte) i, tilemap, this);
            } else if (id == MapName.HANG_KUGYOU) {
                z = new KugyouCave((byte) i, tilemap, this);
            } else if (id == MapName.KHU_DA_DO_AIKO) {
                z = new AikoRedRockArea((byte) i, tilemap, this);
            } else if (tilemap.isChienTruong()) {
                z = new Battlefield((byte) i, this.tilemap, this);
            } else if (id == MapName.DAU_TRUONG) {
                z = new TalentShow((byte) i, this);
                MapManager.getInstance().talentShow = (TalentShow) z;
            } else {
                z = new Zone((byte) i, this.tilemap, this);
            }
        }
    }

    public void addZone(Zone z) {
        lock.writeLock().lock();
        try {
            zones.add(z);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeZone(Zone z) {
        lock.writeLock().lock();
        try {
            zones.remove(z);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void setWar(War war) {
        this.war = war;
    }
    public void setWarClan(WarClan war) {
        this.warClan = war;
    }

    public Zone getZoneById(int index) {
        return this.zones.get(index);
    }

    public void joinZone(Char _char, int zoneId) {
        try {
            Zone z = getZoneById(zoneId);
            if (z != null) {
                z.join(_char);
            }
        } catch (Exception e) {
            Log.error(String.format("Char: %s, Map: %s, Equiped is null: %b, Cleaned: %b", _char.name, this.id, _char.equipment == null, _char.isCleaned), e);
            e.printStackTrace();
        }
    }

    public void updateChar() {
        while (running) {
            try {
                long l1 = System.currentTimeMillis();
                lock.readLock().lock();
                try {
                    for (Zone zone : zones) {
                        if (!zone.isClosed()) {
                            zone.updateChar();
                        }
                    }
                } finally {
                    lock.readLock().unlock();
                }
                long l2 = System.currentTimeMillis() - l1;
                if (l2 >= 500L) {
                    continue;
                }
                try {
                    Thread.sleep(500L - l2);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

    }

    public void updateOther() {
        while (running) {
            try {
                long l1 = System.currentTimeMillis();
                ArrayList<Zone> list = new ArrayList<>();
                lock.readLock().lock();
                try {
                    for (Zone zone : zones) {
                        if (!zone.isClosed()) {
                            zone.update();
                        } else {
                            list.add(zone);
                        }
                    }
                } finally {
                    lock.readLock().unlock();
                }
                if (list.size() > 0) {
                    lock.writeLock().lock();
                    try {
                        zones.removeAll(list);
                    } finally {
                        lock.writeLock().unlock();
                    }
                }
                long l2 = System.currentTimeMillis() - l1;
                if (l2 >= 1000L) {
                    continue;
                }
                try {
                    Thread.sleep(1000L - l2);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

    }

    public void update() {
        this.threadUpdateChar = new Thread(new Runnable() {
            @Override
            public void run() {
                updateChar();
            }

        });
        this.threadUpdateChar.start();
        this.threadUpdateOther = new Thread(new Runnable() {
            @Override
            public void run() {
                updateOther();
            }
        });
        this.threadUpdateOther.start();
    }

    public void close() {
        if (this.threadUpdateChar != null && this.threadUpdateChar.isAlive()) {
            this.threadUpdateChar.interrupt();
        }
        this.threadUpdateChar = null;
        if (this.threadUpdateOther != null && this.threadUpdateOther.isAlive()) {
            this.threadUpdateOther.interrupt();
        }
        this.threadUpdateOther = null;
    }
    
    public Zone rand() {
        return zones.get(NinjaUtils.nextInt(zones.size()));
    }
}
