package com.nsoz.map.world;

import com.nsoz.map.Map;
import com.nsoz.constants.MapName;
import com.nsoz.map.zones.BattleZone;
import com.nsoz.map.zones.AttendanceArea;
import com.nsoz.map.zones.Zone;
import com.nsoz.model.Char;
import com.nsoz.task.GloryTask;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.nsoz.server.GlobalService;
import com.nsoz.map.MapManager;
import com.nsoz.util.NinjaUtils;

public class Arena extends World {

    public static ReadWriteLock lockArena = new ReentrantReadWriteLock();
    public static ArrayList<Arena> arenas = new ArrayList<>();
    public static ArrayList<String> results = new ArrayList<>();
    public static byte number = 0;

    public static Arena getArenaByID(int id) {
        lockArena.readLock().lock();
        try {
            for (Arena arena : arenas) {
                if (arena.id == id) {
                    return arena;
                }
            }
        } finally {
            lockArena.readLock().unlock();
        }
        return null;
    }

    public static void addArena(Arena arena) {
        arenas.add(arena);
    }

    public static void removeArena(Arena arena) {
        arenas.remove(arena);
    }

    public ArrayList<Char> teamOne;
    public ArrayList<Char> teamTwo;
    public ArrayList<Char> viewers;
    private int moneyTeamOne, moneyTeamTwo;
    private Zone zone;
    public boolean isOpened;
    private boolean isLeaderTeamOneOut;
    private boolean isLeaderTeamTwoOut;
    public String leaderTeamOneName, leaderTeamTwoName;
    public boolean isTwoTeamsEtered;
    private boolean finished;

    public Arena() {
        setType(World.ARENA);
        this.name = "Arena";
        this.id = number++;
        this.teamOne = new ArrayList<>();
        this.teamTwo = new ArrayList<>();
        this.viewers = new ArrayList<>();
        this.countDown = 300;
        Map map = MapManager.getInstance().find(MapName.KHU_BAO_DANH);
        AttendanceArea z = new AttendanceArea(0, map.tilemap, map);
        z.setWorld(this);
        this.zone = z;
        initFinished = true;
    }

    public void join(int team, Char _char) {
        lock.writeLock().lock();
        try {
            if (team == 1) {
                if (teamOne.isEmpty()) {
                    leaderTeamOneName = _char.name;
                }
                this.teamOne.add(_char);
                _char.setXY((short) 155, (short) 264);
            }
            if (team == 2) {
                if (teamTwo.isEmpty()) {
                    leaderTeamTwoName = _char.name;
                }
                this.teamTwo.add(_char);
                _char.setXY((short) 394, (short) 264);
            }
            if (team == 3) {
                this.viewers.add(_char);
                _char.setTypePk(Char.PK_NORMAL);
                _char.zone.getService().changePk(_char);
            }
        } finally {
            lock.writeLock().unlock();
        }
        if (!isOpened) {
            addMember(_char);
        }
        zone.join(_char);
    }

    public void out(Char _char) {
        try {
            if (!isClosed) {
                if (teamOne.size() > 0) {
                    if (teamOne.get(0) == _char) {
                        this.isLeaderTeamOneOut = true;
                    }
                }
                if (teamTwo.size() > 0) {
                    if (teamTwo.get(0) == _char) {
                        this.isLeaderTeamTwoOut = true;
                    }
                }
            }
            lock.writeLock().lock();
            try {
                teamOne.remove(_char);
                teamTwo.remove(_char);
                viewers.remove(_char);
            } finally {
                lock.writeLock().unlock();
            }
            removeMember(_char);
            Zone z = zone;
            z.out(_char);
            _char.removeWorld(World.ARENA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getTeam(Char _char) {
        int team = -1;
        if (teamOne.get(0) == _char) {
            team = 1;
        }
        if (teamTwo.get(0) == _char) {
            team = 2;
        }
        return team;
    }

    public void setMoney(int team, int money) {
        String name = "";
        if (team == 1) {
            this.moneyTeamOne = money;
            name = leaderTeamOneName;
        }
        if (team == 2) {
            this.moneyTeamTwo = money;
            name = leaderTeamTwoName;
        }
        String text = name + " thay đổi tiền dặt cược là: " + NinjaUtils.getCurrency(money) + " xu.";
        service.serverMessage(text);
        if (moneyTeamOne == moneyTeamTwo) {
            service.serverMessage("Trận đấu bắt đầu.");
            open();
        }
    }

    public boolean isTeamOneAllDead() {
        if (isLeaderTeamOneOut) {
            return true;
        }
        lock.readLock().lock();
        try {
            for (Char _char : teamOne) {
                if (!_char.isDead && _char.isHuman) {
                    return false;
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return true;
    }

    public boolean isTeamTwoAllDead() {
        if (isLeaderTeamTwoOut) {
            return true;
        }
        lock.readLock().lock();
        try {
            for (Char _char : teamTwo) {
                if (!_char.isDead && _char.isHuman) {
                    return false;
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return true;
    }

    public boolean isViewer(Char p) {
        return this.viewers.stream().filter(pp -> pp.id == p.id).count() > 0;
    }

    public void open() {
        this.isOpened = true;
        zone.close();
        Map map = MapManager.getInstance().find(MapName.LOI_DAI_4);
        BattleZone z = new BattleZone(0, map.tilemap, map);
        z.setWorld(this);
        this.zone = z;
        Char leaderTeamOne = teamOne.get(0);
        Char leaderTeamTwo = teamTwo.get(0);
        leaderTeamOne.addCoin(-moneyTeamOne);
        leaderTeamTwo.addCoin(-moneyTeamTwo);
        if (moneyTeamOne >= 1000000) {
            GlobalService.getInstance().chat("Hệ thống",
                    String.format("%s (%d) đang thách đấu với %s (%d) %s xu ở lôi đài.", leaderTeamOneName,
                            leaderTeamOne.level, leaderTeamTwoName, leaderTeamTwo.level,
                            NinjaUtils.getCurrency(moneyTeamOne)));
        }
        for (Char _char : teamOne) {
            _char.setTypePk(Char.PK_PHE1);
            if (_char.isNhanBan) {
                continue;
            }
            _char.setXY((short) 205, (short) 264);
            _char.joinZone(-1, -1, 4);
            _char.zone.getService().changePk(_char);
            _char.setNonCombatState(60);
        }
        for (Char _char : teamTwo) {
            _char.setTypePk(Char.PK_PHE2);
            if (_char.isNhanBan) {
                continue;
            }
            _char.setXY((short) 565, (short) 264);
            _char.joinZone(-1, -1, 4);
            _char.zone.getService().changePk(_char);
            _char.setNonCombatState(60);
        }
        addArena(this);
        this.countDown = 600;
        service.sendTimeInMap(countDown);

    }

    public void close() {
        try {
            if (isOpened) {
                removeArena(this);
            }
            List<Char> chars = zone.getChars();
            for (Char _char : chars) {
                if (_char.isNhanBan) {
                    _char.setTypePk(Char.PK_NORMAL);
                    continue;
                }
                short[] xy = NinjaUtils.getXY(_char.mapBeforeEnterPB);
                _char.setXY(xy[0], xy[1]);
                _char.changeMap(_char.mapBeforeEnterPB);
            }
            if (zone != null) {
                zone.close();
            }
            this.zone = null;
            this.teamOne = null;
            this.teamTwo = null;
            this.viewers = null;
            this.leaderTeamOneName = null;
            this.leaderTeamTwoName = null;
            super.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean enterWorld(Zone pre, Zone next) {
        return false;
    }

    @Override
    public boolean leaveWorld(Zone pre, Zone next) {
        return false;
    }

    @Override
    public void update() {
        if (countDown <= 0) {
            if (!finished) {
                int coin = moneyTeamOne + moneyTeamTwo;
                coin -= coin * 10 / 100;
                Char leaderTeamTwo = teamTwo.get(0);
                leaderTeamTwo.addCoin(coin / 2);
                Char leaderTeamOne = teamOne.get(0);
                leaderTeamOne.addCoin(coin / 2);
                String result = "Phe " + leaderTeamOneName + " với phe " + leaderTeamTwoName + " hòa";
                Arena.results.add(0, result);
            }
            close();
            return;
        }
        if (!finished) {
            boolean isTeamOneAllDead = isTeamOneAllDead();
            boolean isTeamTwoAllDead = isTeamTwoAllDead();
            if ((isTeamOneAllDead || isTeamTwoAllDead) && isTwoTeamsEtered) {
                finished = true;
                if (isOpened) {
                    String nameTeamWin = "";
                    int coin = moneyTeamOne + moneyTeamTwo;
                    coin -= coin / 100;
                    if (isTeamOneAllDead) {
                        Char leaderTeamTwo = teamTwo.get(0);
                        nameTeamWin = leaderTeamTwo.name;
                        leaderTeamTwo.addCoin(coin);
                        if (leaderTeamTwo.gloryTask != null) {
                            if (leaderTeamTwo.gloryTask.type == GloryTask.CHIEN_THANG_LOI_DAI) {
                                leaderTeamTwo.gloryTask.updateProgress(1);
                            }
                        }
                        String result = "Phe " + leaderTeamTwoName + " thắng phe " + leaderTeamOneName;
                        Arena.results.add(0, result);
                    } else if (isTeamTwoAllDead) {
                        Char leaderTeamOne = teamOne.get(0);
                        nameTeamWin = leaderTeamOne.name;
                        leaderTeamOne.addCoin(coin);
                        if (leaderTeamOne.gloryTask != null) {
                            if (leaderTeamOne.gloryTask.type == GloryTask.CHIEN_THANG_LOI_DAI) {
                                leaderTeamOne.gloryTask.updateProgress(1);
                            }
                        }
                        String result = "Phe " + leaderTeamOneName + " thắng phe " + leaderTeamTwoName;
                        Arena.results.add(0, result);
                    }
                    if (Arena.results.size() >= 21) {
                        Arena.results.remove(20);
                    }
                    countDown = 10;
                    service.sendTimeInMap(countDown);
                    String text = "Phe " + nameTeamWin + " đã dành chiến thắng nhận được "
                            + NinjaUtils.getCurrency(coin) + " xu.";
                    zone.getService().serverMessage(text);
                } else {
                    if (isTeamOneAllDead || isTeamTwoAllDead) {
                        getService().serverMessage(
                                "Trận đấu đã bị hủy vì phe đối phương đã khiếp sợ và bỏ chạy.");
                    }
                    close();
                    return;
                }
            }
        }
        countDown--;
    }
}
