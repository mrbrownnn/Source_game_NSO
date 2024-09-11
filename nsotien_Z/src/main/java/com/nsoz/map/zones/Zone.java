package com.nsoz.map.zones;

import com.mongodb.lang.Nullable;
import com.nsoz.bot.Bot;
import com.nsoz.bot.SantaClaus;
import com.nsoz.effect.Effect;
import com.nsoz.constants.MapName;
import com.nsoz.map.item.ItemMap;
import com.nsoz.map.Map;
import com.nsoz.map.MapManager;
import com.nsoz.map.MapService;
import com.nsoz.map.TileMap;
import com.nsoz.map.Waypoint;
import com.nsoz.mob.Mob;
import com.nsoz.model.Figurehead;
import com.nsoz.model.Char;
import com.nsoz.npc.Npc;
import com.nsoz.npc.NpcFactory;
import com.nsoz.party.Group;
import com.nsoz.task.Task;
import com.nsoz.constants.TaskName;
import com.nsoz.effect.EffectManager;
import com.nsoz.event.Event;
import com.nsoz.map.Tree;
import com.nsoz.map.item.IceCrystal;
import com.nsoz.map.item.Mushroom;
import com.nsoz.map.item.Ore;
import com.nsoz.mob.MobFactory;
import com.nsoz.mob.MobPosition;
import com.nsoz.mob.TerritoryMobFactory;
import com.nsoz.mob.WarMobFactory;
import com.nsoz.server.GameData;
import com.nsoz.task.MobInfo;
import com.nsoz.task.TaskFactory;
import com.nsoz.util.Log;
import com.nsoz.util.NinjaUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class Zone {

    @Getter
    private boolean isClosed;
    private List<Char> players;
    private List<ItemMap> itemMaps;
    @Getter
    public List<Mob> monsters;
    private List<Tree> trees;
    private List<Figurehead> figureheads;
    private List<ItemMap> listRemoveItem;
    @Getter
    private List<Npc> npcs;
    public short numberDropItem;
    public int id;
    public byte numberElitez, numberChief;
    public TileMap tilemap;
    public Map map;
    public List<Mob> waitingListRecoveries;
    public List<Mob> waitingListDelete;
    public List<Mob> listRecoveries;
    public long lastUpdateEverySecond;
    public long lastUpdateEveryHalfSecond;
    public long lastUpdateEveryFiveSecond;
    private ReadWriteLock lockMob;
    private ReadWriteLock lockChar;
    private ReadWriteLock lockItem;
    public boolean isOpened;
    public boolean isLastBossWasBorn;

    public final int LIMIT_SPEED = 17;
    @Getter
    private MapService service;

    @Getter
    private MobFactory mobFactory;

    @Builder
    public Zone(int id, TileMap tilemap, Map map) {
        this.id = id;
        this.tilemap = tilemap;
        this.map = map;
        if (tilemap.isChienTruong()) {
            this.mobFactory = new WarMobFactory(this);
        } else if (tilemap.isDungeoClan()) {
            this.mobFactory = new TerritoryMobFactory(this);
        } else {
            this.mobFactory = new MobFactory(this);
        }
        figureheads = new ArrayList<>();
        players = new ArrayList<>();
        monsters = new ArrayList<>();
        itemMaps = new ArrayList<>();
        npcs = new ArrayList<>();
        trees = new ArrayList<>();
        listRemoveItem = new ArrayList<>();
        waitingListRecoveries = new ArrayList<>();
        waitingListDelete = new ArrayList<>();
        listRecoveries = new ArrayList<>();
        lockMob = new ReentrantReadWriteLock();
        lockChar = new ReentrantReadWriteLock();
        lockItem = new ReentrantReadWriteLock();
        map.addZone(this);
        init();
    }

    public void addNpc(Npc npc) {
        this.npcs.add(npc);
    }

    public void addTree(Tree tree) {
        this.trees.add(tree);
    }

    public void init() {
        this.service = new MapService(this);
        this.numberChief = 0;
        this.numberElitez = 0;
        createMonster();
        for (Npc npc : tilemap.npcs) {
            Npc newNpc = NpcFactory.getInstance().newNpc(npc.id, npc.template.npcTemplateId, npc.cx, npc.cy,
                    npc.status);
            newNpc.setService(service);
            addNpc(newNpc);
        }

        if (Event.isEvent()) {
            Event.getEvent().initMap(this);
        }
    }

    public int getNumberChar() {
        if (!isClosed) {
            return players.size();
        }
        return 0;
    }

    public List<Mob> searchMonster(int x, int y, int range) {
        List<Mob> mobs = getLivingMonsters();
        List<Mob> list = new ArrayList<>();
        for (Mob mob : mobs) {
            if (range > 0) {
                int d = NinjaUtils.getDistance(mob.x, mob.y, x, y);
                if (d > range) {
                    continue;
                }
            }
            list.add(mob);
        }
        return list;
    }

    public void requestChangeMap(@NotNull Char p) {
        Waypoint wp = tilemap.findWaypoint(p.x, p.y);
        if (wp == null) {
            return;
        }
        int nextID = wp.next;
        if (nextID == -1) {
            int[] list = new int[]{MapName.NUI_DORAGON, MapName.RUNG_MAJO, MapName.VUC_YUNIKOON, MapName.DONG_KINGU};
            nextID = NinjaUtils.nextInt(list);
            short x = 0;
            short y = 0;
            switch (nextID) {
                case MapName.DONG_KINGU:
                    x = 45;
                    y = 264;
                    break;

                case MapName.VUC_YUNIKOON:
                    x = 1011;
                    y = 1128;
                    break;

                case MapName.RUNG_MAJO:
                    x = 2595;
                    y = 240;
                    break;

                case MapName.NUI_DORAGON:
                    x = 1011;
                    y = 720;
                    break;
            }
            p.setXY(x, y);
            p.changeMap(nextID);
            return;
        }
        if (p.isCanEnterMap(nextID)) {
            p.setXY(wp.x, wp.y);
            p.changeMap(nextID);
        } else {
            p.returnToPreviousPostion(() -> {
                p.serverDialog("Bạn chưa thể đến khu vực này. Hày hoàn thành nhiệm vụ trước.");
            });
        }
    }

    public void returnTownFromDead(@NotNull Char p) {
        short[] xy = NinjaUtils.getXY(p.saveCoordinate);
        p.setXY(xy[0], xy[1]);
        p.changeMap(p.saveCoordinate);
    }

    public void startDie(@NotNull Char p) {

    }

    public boolean isCanMove(@Nullable Char _char, short x, short y) {
        return true;
    }

    public void move(@NotNull Char p, short x, short y) {
        try {
            if (p.isDontMove()) {
                return;
            }
            if (!isCanMove(p, x, y)) {
                p.getService().resetPoint();
                return;
            }
            short preX = p.x;
            short preY = p.y;
            if (p.x != x || p.y != y) {
                if (!p.isAutoPlay() && !(p instanceof Bot)) {
                    if (Math.abs(x - preX) > p.speed * LIMIT_SPEED) {
                        p.getService().resetPoint();
                        return;
                    }
                }
                p.preX = preX;
                p.preY = preY;
                p.setXY(x, y);
                getService().playerMove(p);
                if (p.isAutoPlay()) {
                    getService().teleport(p);
                }
                if (p.clone != null && p.clone.isNhanBan && !p.clone.isDead) {
                    p.clone.move((short) (NinjaUtils.nextInt(-50, 50) + x), y);
                }
                Bot escortedEvent = p.getEscortedEvent();
                if (escortedEvent != null) {
                    escortedEvent.setXY((short) (NinjaUtils.nextInt(-50, 50) + x), y);
                    getService().playerMove(escortedEvent);
                }
                if (GameData.ANTICROSS_MAP) {
                    p.y = tilemap.collisionY(p.x, p.y);
                }
                if (p.isCrossMap(p.x, p.y)) {
                    p.x = preX;
                    p.y = preY;
                    p.startDie();
                }
                if (p.classId == 2) {
                    if (p.isHide) {
                        EffectManager em = p.getEm();
                        Effect eff = em.findByType((byte) 12);
                        if (eff != null) {
                            p.getService().removeEffect(eff);
                            getService().playerRemoveEffect(p, eff);
                            em.removeEffect(eff);
                        }

                    }

                }
            } else {
                p.getService().resetPoint();
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }

    }

    public void addMobForWatingListRespawn(Mob mob) {
        synchronized (waitingListRecoveries) {
            if (!mob.isCantRespawn) {
                waitingListRecoveries.add(mob);
            }
        }
    }

    public Npc getNpc(int id) {
        for (Npc npc : npcs) {
            if (npc.template.npcTemplateId == id) {
                return npc;
            }
        }
        return null;
    }

    public ItemMap findItemMapById(short id) {
        lockItem.readLock().lock();
        try {
            for (ItemMap item : itemMaps) {
                if (item.getId() == id) {
                    return item;
                }
            }
        } finally {
            lockItem.readLock().unlock();
        }
        return null;
    }

    public void addItemMap(ItemMap item) {
        if (itemMaps != null) {
            lockItem.writeLock().lock();
            try {
                this.itemMaps.add(item);
            } finally {
                lockItem.writeLock().unlock();
            }
        }
    }

    public Mob findMoLivebByID(int id) {
        List<Mob> mobs = monsters;
        int l = 0, r = mobs.size() - 1;
        while (l <= r) {
            int m = l + (r - l) / 2;
            Mob mob = mobs.get(m);
            if (mob.id == id) {
                if (mob.isDead) {
                    return null;
                }
                return mob;
            }
            if (mob.id < id) {
                l = m + 1;
            } else {
                r = m - 1;
            }
        }
        return null;
    }

    public int getNumberGroup() {
        List<Char> chars = getChars();
        java.util.Map<Group, List<Char>> map = NinjaUtils.groupBy(chars, Char::getGroup);
        return map.size();
        // HashMap<String, Group> groups = new HashMap<>();
        // List<Char> chars = getChars();
        // for (Char _char : chars) {
        // Group group = _char.getGroup();
        // if (group != null) {
        // groups.put(group.memberGroups.get(0).name, group);
        // }
        // }
        // return groups.size();
    }

    public List<Mob> getLivingMonsters() {
        ArrayList<Mob> mobs = new ArrayList<>();
        for (Mob mob : monsters) {
            if (!mob.isDead) {
                mobs.add(mob);
            }
        }
        return mobs;
    }

    public Char findCharById(int id) {
        lockChar.readLock().lock();
        try {
            for (Char c : players) {
                if (c != null && c.id == id) {
                    return c;
                }
            }
        } finally {
            lockChar.readLock().unlock();
        }
        return null;
    }

    public Char findCharName(String name) {
        lockChar.readLock().lock();
        try {
            for (Char c : players) {
                if (c.user != null && c.user.session != null && c.name.equals(name)) {
                    return c;
                }
            }
        } finally {
            lockChar.readLock().unlock();
        }
        return null;
    }

    public void addChar(Char _char) {
        if (players != null) {
            lockChar.writeLock().lock();
            try {
                this.players.add(_char);
            } finally {
                lockChar.writeLock().unlock();
            }
        }
    }

    public void add(Figurehead buNhin) {
        if (figureheads != null) {
            this.figureheads.add(buNhin);
        }
    }

    public void join(Char p) {
        Zone preZone = p.zone;
        p.mapId = (short) this.tilemap.id;
        p.zone = this;
        if (preZone != null) {
            if (preZone.tilemap.isWorld() && !tilemap.isWorld()) {
                p.removeMemberFromWorld(preZone, this);
            }
        }
        // thêm player vào khu cho người chơi khác
        getService().playerAdd(p);
        if (p.coat > 0) {
            getService().loadCoat(p);
        }
        if (p.glove > 0) {
            getService().loadGlove(p);
        }
        if (p.mount[4] != null) {
            getService().loadMount(p);
        }
        if (p.mobMe != null) {
            getService().loadPet(p);
        }
        if (p.fashion[10] != null && p.honor > 0) {
            getService().loadHonor(p);
        }
        if (p.mobBijuu != null) {
            getService().onChangeBijuu(p.id, p.mobBijuu);
        }
        addChar(p);
        p.getService().sendZone();
        p.getService().sendItemMap();
        loadMap(p);// load người trong map
        for (Tree tree : trees) {
            p.getService().addEffectAuto((byte) tree.getId(), tree.getX(), tree.getY(), (byte) -1, (short) 0);
        }

        // =============Phân thân vào map cùng chủ thân (đi
        // theo)===========================
        if (p.clone != null && p.clone.isNhanBan && !p.clone.isDead) {
            p.clone.setXY(p.x, p.y);
            join(p.clone);
        }
        Bot escortedEvent = p.getEscortedEvent();
        if (escortedEvent != null) {
            escortedEvent.setXY(p.x, p.y);
            join(escortedEvent);
        }

        // =========Xóa member khi rời khỏi chiến trường============
        if (!tilemap.isChienTruong()) {
            if (preZone != null && preZone.tilemap.isChienTruong()) {
                if (p.war != null && p.war.status != 2) {
                    p.war.removeMember(p);
                }
            }
        }

        /* =========Xóa member khi rời khỏi đấu trường============ */
        if (!tilemap.isDauTruong()) {
            if (preZone != null && preZone.tilemap.isDauTruong()) {
                MapManager.getInstance().talentShow.removePlayer(p);
            }
        }

    }

    public void out(Char p) {
        if (p.mob != null) {
            p.mob = null;
        }
        removeChar(p);
        getService().playerRemove(p.id);
        if (p.clone != null && p.clone.isNhanBan && !p.clone.isDead) {
            out(p.clone);
        }
        Bot escortedEvent = p.getEscortedEvent();
        if (escortedEvent != null) {
            out(escortedEvent);
        }
    }

    public List<Char> getChars() {
        ArrayList<Char> chars = new ArrayList<>();
        lockChar.readLock().lock();
        try {
            if (players != null) {
                for (Char c : players) {
                    if (!(c instanceof Bot)) {
                        if (c == null || c.isCleaned) {
                            continue;
                        }
                    }
                    chars.add(c);
                }
            }
        } finally {
            lockChar.readLock().unlock();
        }
        return chars;
    }

    public List<ItemMap> getItemMaps(Task... tasks) {
        List<ItemMap> list = new ArrayList<>();
        boolean mushroomPickingTask = false;
        boolean iceCrystalPickingTask = false;
        boolean mineralsPickingTask = false;
        for (Task task : tasks) {
            if (task == null) {
                continue;
            }
            if (task.taskId == TaskName.NV_HAI_NAM && task.index == 1) {
                mushroomPickingTask = true;
            } else if (task.taskId == TaskName.NV_THU_THAP_NGUYEN_LIEU && task.index == 1) {
                mineralsPickingTask = true;
            } else if (task.taskId == TaskName.NV_THU_THAP_TINH_THE_BANG && task.index == 1) {
                iceCrystalPickingTask = true;
            }
        }
        lockItem.readLock().lock();
        try {
            for (ItemMap itemMap : itemMaps) {
                if (!mushroomPickingTask && itemMap instanceof Mushroom) {
                    continue;
                }
                if (!mineralsPickingTask && itemMap instanceof Ore) {
                    continue;
                }
                if (!iceCrystalPickingTask && itemMap instanceof IceCrystal) {
                    continue;
                }
                list.add(itemMap);
            }
        } finally {
            lockItem.readLock().unlock();
        }
        return list;
    }

    public Figurehead[] getBuNhins() {
        return this.figureheads.toArray(new Figurehead[this.figureheads.size()]);
    }

    public void removeItem(ItemMap item) {
        if (itemMaps != null) {
            lockItem.writeLock().lock();
            try {
                itemMaps.remove(item);
            } finally {
                lockItem.writeLock().unlock();
            }
        }
    }

    public void removeChar(Char _char) {
        if (players != null) {
            lockChar.writeLock().lock();
            try {
                players.remove(_char);
            } finally {
                lockChar.writeLock().unlock();
            }
        }

    }

    public void removeMonster(Mob mob) {
        if (monsters != null) {
            lockMob.writeLock().lock();
            try {
                monsters.remove(mob);
            } finally {
                lockMob.writeLock().unlock();
            }
        }
    }

    public void removeItem() {
        for (ItemMap item : listRemoveItem) {
            try {
                removeItem(item);
                getService().removeItem(item.getId());
            } catch (Exception ex) {
                Logger.getLogger(Zone.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        listRemoveItem.clear();
    }

    public void recoveryMonster() {
        synchronized (this.listRecoveries) {
            for (Mob mob : this.listRecoveries) {
                getService().recoveryMonster(mob);
                this.waitingListRecoveries.remove(mob);

            }
            this.listRecoveries.clear();
        }
    }

    public void addMob(Mob mob) {
        try {
            if (monsters == null) {
                return;
            }
            lockMob.writeLock().lock();
            try {
                monsters.add(mob);
            } finally {
                lockMob.writeLock().unlock();
            }
            getService().addMob(mob);
        } catch (Exception ex) {
            Logger.getLogger(Zone.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createMonster() {
        int id = 0;
        boolean isMapTask = this.id == 0 && tilemap.isNormal() && !tilemap.isThatThuAi() && !tilemap.isDauTruong()
                && !tilemap.isCandyBattlefield() && !tilemap.isDungeo() && !tilemap.isDungeoClan()
                && !tilemap.isLangTruyenThuyet() && !tilemap.isLangCo() && !tilemap.isNotSave()
                && !tilemap.isNotChangeZone() && !tilemap.isFujukaSanctuary() && !tilemap.isNotTeleport();
        for (MobPosition mobPosition : tilemap.monsterCoordinates) {
            Mob mob = mobFactory.createMonster(id++, mobPosition);
            if (mob != null) {
                addMob(mob);

                if (isMapTask && !mob.isBoss) {
                    MobInfo mobInfo = MobInfo.builder().mapID(map.id).mobID(mob.template.id).level(mob.level).build();
                    if (mob.isBeast()) {
                        TaskFactory.getInstance().addMobInfoTaskBoss(mobInfo);
                    } else {
                        TaskFactory.getInstance().addMobInfoTaskDay(mobInfo);
                    }
                }
                if (id >= 127) {
                    return;
                }
            }
        }

    }

    public void mobDead(Mob mob, Char killer) {

    }

    public void setHPAllMonsters(double hp) {
        List<Mob> mobs = getLivingMonsters();
        for (Mob mob : mobs) {
            mob.hp = 100;
            mob.maxHP = 100;
        }
    }

    public void recoveryAllMonsters(int level) {
        lockMob.writeLock().lock();
        try {
            monsters.clear();
        } finally {
            lockMob.writeLock().unlock();
        }
        int id = 0;
        for (MobPosition mob : tilemap.monsterCoordinates) {
            Mob monster = mobFactory.createMonster(id++, mob, level);
            addMob(monster);
        }
    }

    public void killAllMonsters() {
        List<Mob> monsters = getLivingMonsters();
        for (Mob mob : monsters) {
            double hp = mob.hp;
            mob.die();
            getService().attackMonster(hp, false, mob);
        }
        lockMob.writeLock().lock();
        try {
            this.monsters.clear();
        } finally {
            lockMob.writeLock().unlock();
        }
    }

    public void addMobForRespawnList(Mob mob) {
        synchronized (listRecoveries) {
            listRecoveries.add(mob);
        }
    }

    public void updateRecovery() {
        synchronized (waitingListRecoveries) {
            if (this.waitingListRecoveries.size() > 0) {
                for (Mob mob : this.waitingListRecoveries) {
                    mob.recoveryTimeCount--;
                    if (mob.recoveryTimeCount <= 0) {
                        mob.recovery();
                        addMobForRespawnList(mob);
                    }
                }
                if (listRecoveries.size() > 0) {
                    recoveryMonster();
                }
            }
        }
    }

    public void updateChar() {
        try {
            if (this.players.size() > 0) {
                long now = System.currentTimeMillis();
                boolean isUpdateEverySecond = ((now - this.lastUpdateEverySecond) >= 1000);
                if (isUpdateEverySecond) {
                    this.lastUpdateEverySecond = now;
                }
                boolean isUpdateEveryHalfSecond = ((now - this.lastUpdateEveryHalfSecond) >= 500);
                if (isUpdateEveryHalfSecond) {
                    this.lastUpdateEveryHalfSecond = now;
                }
                boolean isUpdateEveryFiveSecond = ((now - this.lastUpdateEveryFiveSecond) >= 5000);
                if (isUpdateEveryFiveSecond) {
                    this.lastUpdateEveryFiveSecond = now;
                }
                if (isUpdateEveryHalfSecond || isUpdateEverySecond || isUpdateEveryFiveSecond) {
                    List<Char> mChars = getChars();
                    for (Char _char : mChars) {
                        try {
                            if (isUpdateEveryHalfSecond) {
                                _char.updateEveryHalfSecond();
                            }
                            if (isUpdateEverySecond) {
                                _char.updateEverySecond();
                            }
                            if (isUpdateEveryFiveSecond) {
                                _char.updateEveryFiveSecond();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateMob() {
        try {
            if (players.size() > 0 && monsters.size() > 0) {
                lockMob.readLock().lock();
                try {
                    for (Mob mob : monsters) {
                        try {
                            mob.update();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } finally {
                    lockMob.readLock().unlock();
                }
                try {
                    if (waitingListDelete.size() > 0) {
                        for (Mob mob : waitingListDelete) {
                            removeMonster(mob);
                        }
                        waitingListDelete.clear();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateBuNhin() {
        try {
            if (figureheads.size() > 0) {
                ArrayList<Integer> list = new ArrayList<>();
                for (int i = figureheads.size() - 1; i >= 0; i--) {
                    Figurehead b = figureheads.get(i);
                    if (b == null || b.countDown <= 0) {
                        list.add(i);
                        continue;
                    }
                    b.countDown--;
                }
                if (list.size() > 0) {
                    for (int index : list) {
                        try {
                            getService().removeBuNhin(index);
                            figureheads.remove(index);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getNumberItem() {
        return itemMaps.size();
    }

    public void updateItemMap() {
        try {
            if (this.itemMaps.size() > 0) {
                lockItem.readLock().lock();
                try {
                    for (ItemMap item : itemMaps) {
                        try {
                            if (item.isExpired()) {
                                listRemoveItem.add(item);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } finally {
                    lockItem.readLock().unlock();
                }
                if (listRemoveItem.size() > 0) {
                    removeItem();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setFire(Mob mob, boolean isFire) {
        mob.isFire = isFire;
        getService().setFire(mob);
    }

    public void setIce(Mob mob, boolean isIce) {
        mob.isIce = isIce;
        getService().setIce(mob);
    }

    public void setWind(Mob mob, boolean isWind) {
        mob.isWind = isWind;
        getService().setWind(mob);
    }

    public void setMove(Mob mob, boolean isDontMove) {
        mob.isDontMove = isDontMove;
        getService().setMove(mob);
    }

    public void setDisable(Mob mob, boolean isDisable) {
        mob.isDisable = isDisable;
        getService().setDisable(mob);
    }

    public void loadMap(Char _char) {
        List<Char> chars = getChars();
        for (Char _char2 : chars) {
            try {
                if (_char != _char2) {
                    _char.getService().playerAdd(_char2);
                }
                if (_char2.mount[4] != null) {
                    _char.getService().loadMount(_char2);
                }
                if (_char2.coat > 0) {
                    _char.getService().loadCoat(_char2);
                }
                if (_char2.glove > 0) {
                    _char.getService().loadGlove(_char2);
                }
                if (_char2.mobMe != null) {
                    _char.getService().loadPet(_char2);
                }
                if (_char2.fashion[10] != null && _char2.honor > 0) {
                    _char.getService().loadHonor(_char2);
                }
                if (_char2.mobBijuu != null) {
                    _char.getService().onChangeBijuu(_char2.id, _char2.mobBijuu);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        if (!isClosed) {
            updateBuNhin();
            updateItemMap();
            updateRecovery();
            updateMob();
        }
    }

    public void close() {
        if (!isClosed) {
            this.isClosed = true;
            this.map = null;
            this.players = null;
            this.figureheads = null;
            this.itemMaps = null;
            this.monsters = null;
            this.listRecoveries = null;
            this.listRemoveItem = null;
            this.waitingListRecoveries = null;
        }
    }
}
