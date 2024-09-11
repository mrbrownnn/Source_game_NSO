package com.nsoz.map.zones;

import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.mongodb.lang.Nullable;
import com.nsoz.constants.CMDMenu;
import com.nsoz.constants.MapName;
import com.nsoz.event.Event;
import com.nsoz.map.Map;
import com.nsoz.map.MapManager;
import com.nsoz.model.Char;
import com.nsoz.model.Menu;
import com.nsoz.network.AbsService;
import com.nsoz.network.Message;
import com.nsoz.server.Events;
import com.nsoz.server.GlobalService;
import com.nsoz.server.ServerManager;
import com.nsoz.util.NinjaUtils;
import java.util.List;
import lombok.Getter;

public class TalentShow extends Zone {

    public static final int WHITE = 1;
    public static final int BLACK = 2;

    protected ReadWriteLock lock;
    public boolean opened;
    private int countDown;
    public boolean invited;
    public boolean started;
    public boolean finished;
    public String whiteName;
    public String blackName;
    public List<String> whiteMemberNames;
    public List<String> blackMemberNames;
    private List<Player> players;
    public List<Group> groups;
    private List<String> results;

    public TalentShow(int id, Map map) {
        super(id, map.tilemap, map);
        this.lock = new ReentrantReadWriteLock();
        this.countDown = 1800;
        whiteMemberNames = new ArrayList<>();
        blackMemberNames = new ArrayList<>();
        this.players = new ArrayList<>();
        this.groups = new ArrayList<>();
        this.results = new ArrayList<>();
        addGroup(new Group(0, this));
    }

    public void invite() {
        invited = true;
        for (String name : whiteMemberNames) {
            addPlayer(name, TalentShow.WHITE);
        }
        for (String name : blackMemberNames) {
            addPlayer(name, TalentShow.BLACK);
        }
        setTimeMap(120);
        String message = String.format(
                "Trận thi đấu giữa %s và %s sẽ bắt đầu sau ít phút nữa",
                whiteName.toUpperCase(), blackName.toUpperCase());
        notify(message);
    }

    public void start() {
        if (players.isEmpty()) {
            reset();
            getService().serverMessage("Trận đấu đã bị hủy vì các đội thi đấu không có mặt");
            return;
        }
        started = true;
        String message = String.format(
                "Trận thi đấu giữa %s và %s chính thức bắt đầu, hãy cùng đón xem ai sẽ là đội chiến thắng",
                whiteName.toUpperCase(), blackName.toUpperCase());
        notify(message);
        globalNotify(message);
        for (Player player : players) {
            player.join();
        }
        setTimeMap(600);
        for (Char _char : this.getChars()) {
            showPlayerList(_char);
        }
    }

    public void finish() {
        finished = true;
        setTimeMap(30);
    }

    public void cancel() {
        reset();
        getService().serverMessage("Trận đấu đã bị hủy vì một số lý do nhất định");
    }

    public void outAllPlayers() {
        for (Player player : players) {
            player.out();
        }
    }

    public void reset() {
        lock.readLock().lock();
        try {
            outAllPlayers();
            invited = false;
            started = false;
            finished = false;
            whiteName = blackName = "";
            players.clear();
            whiteMemberNames.clear();
            blackMemberNames.clear();
            setTimeMap(600);
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean isTeamAllDead(int faction) {
        lock.readLock().lock();
        try {
            for (Player player : players) {
                if (player.faction == faction && player.isDead()) {
                    return false;
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return true;
    }

    private void globalNotify(String message) {
        GlobalService.getInstance().chat("Ninja Tài Năng", message);
    }

    private void notify(String message) {
        getService().serverMessage(message);
    }

    public void setTimeMap(int t) {
        countDown = t;
        getService().sendTimeInMap(countDown);
    }

    @Override
    public boolean isCanMove(@Nullable Char _char, short x, short y) {
        if (isFightingPlayer(_char)) {
            if (started && (y > 288 || y < 191)) {
                return false;
            }
        } else {
            if (y <= 288) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void update() {
        if (countDown > 0) {
            countDown--;
        }
        if (started) {
            if (!finished) {
                boolean isTeamOneAllDead = isTeamAllDead(WHITE);
                boolean isTeamTwoAllDead = isTeamAllDead(BLACK);
                if (isTeamOneAllDead || isTeamTwoAllDead) {
                    finished = true;
                    String winName = isTeamTwoAllDead ? whiteName.toUpperCase() : blackName.toUpperCase();
                    String loseName = isTeamTwoAllDead ? blackName.toUpperCase() : whiteName.toUpperCase();
                    String message = String.format("Phe %s đã giành chiến thắng trước phe %s", winName, loseName);
                    results.add(String.format("- Phe %s thắng phe %s", winName, loseName));
                    notify(message);
                    globalNotify(message);
                    finish();
                }
            }

            if (countDown <= 0) {
                if (!finished) {
                    String message = String.format("Đã quá thời gian thi đấu, kết quả 2 đội hòa nhau");
                    notify(message);
                    finish();
                } else {
                    reset();
                }
            }
        }
        if (invited && countDown <= 0) {
            start();
        }
        super.update();
    }

    public void addPlayer(String name, int faction) {
        Char _char = ServerManager.findCharByName(name);
        if (_char != null && !isFightingPlayer(_char)) {
            Player player = new Player(_char, faction);
            this.addPlayer(player);

            int randPointX = (int)NinjaUtils.nextInt(70, 650);
            _char.setXY((short) randPointX, (short) 384);
            _char.changeMap(MapName.DAU_TRUONG);
            String text = "Xin chào " + _char.name
                    + "!\nTrận đấu của bạn sẽ bắt đầu sau ít phút nữa, hãy cố gắng hết sức để hạ gục đối thủ của bạn.\nChúc may mắn <3";
            _char.getService().showAlert("Ninja Tài Năng", text);
        }
    }

    public void addGroup(Group group) {
        synchronized (this.groups) {
            this.groups.add(group);
        }
    }

    public void addPlayerToGroup(Char p) {
        if (p.isBot() || !p.isHuman) {
            return;
        }
        int size = this.groups.size();
        Group groupJoin = null;
        for (Group group : this.groups) {
            if (!group.isMaxium()) {
                groupJoin = group;
                break;
            }
        }
        if (groupJoin == null) {
            groupJoin = new Group(size, this);
            this.addGroup(groupJoin);
        }
        if (!groupJoin.isExist(p)) {
            p.setGroupIndex(groupJoin.getIndex());
            groupJoin.addPlayer(p);
        }
    }

    private boolean isFightingPlayer(Char p) {
        lock.readLock().lock();
        try {
            return this.players.stream().filter(pp -> pp.player.id == p.id).count() > 0;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void removePlayerFromGroup(Char p) {
        if (p.isBot() || !p.isHuman) {
            return;
        }
        Group group = groups.get(p.getGroupIndex());
        group.removePlayer(p);
    }

    public void addPlayerToFirtGroup(Char p) {
        if (p.isBot() || !p.isHuman) {
            return;
        }
        Group group = groups.get(0);
        if (!group.isExist(p)) {
            p.setGroupIndex(group.getIndex());
            group.addPlayer(p);
        }
    }

    public void addPlayer(Player player) {
        lock.writeLock().lock();
        try {
            players.add(player);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Player getPlayer(String name) {
        lock.readLock().lock();
        try {
            for (Player player : players) {
                if (player.player.name.equals(name)) {
                    return player;
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Player> getPlayers() {
        lock.readLock().lock();
        try {
            return players;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void removePlayer(Char _char) {
        lock.writeLock().lock();
        try {
            for (int i = 0; i < players.size(); i++) {
                Player player = (Player) players.get(i);
                if (_char.id == player.player.id) {
                    players.remove(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int getFactionByName(String name) {
        int faction = -1;
        if (whiteMemberNames.contains(name)) {
            faction = 1;
        } else if (blackMemberNames.contains(name)) {
            faction = 2;
        }
        return faction;
    }

    public void showMenu(Char _charz) {
        ArrayList<Menu> menus = _charz.menus;
        menus.clear();
        if (_charz.user.isAdmin()) {
            if (this.invited && !this.started) {
                menus.add(new Menu(CMDMenu.EXECUTE, "Bắt đầu", () -> {
                    if (players.size() > 0) {
                        start();
                    } else {
                        _charz.getService().serverMessage("Chưa có người chơi nào tham gia thi đấu");
                    }
                }));
            }
            if (this.started || this.invited) {
                menus.add(new Menu(CMDMenu.EXECUTE, "Hủy trận", () -> {
                    if (started || invited) {
                        cancel();
                    } else {
                        _charz.getService().serverMessage("Trận đấu chưa bắt đầu");
                    }
                }));
            }
            if (this.invited && !this.started) {
                menus.add(new Menu(CMDMenu.EXECUTE, "Mời", () -> {
                    StringBuffer sb = new StringBuffer();
                    for (String name : whiteMemberNames) {
                        addPlayer(name, WHITE);
                        sb.append(name).append(", ");
                    }
                    for (String name : blackMemberNames) {
                        addPlayer(name, BLACK);
                        sb.append(name).append(", ");
                    }
                    if (sb.length() > 0) {
                        sb.delete(sb.length() - 2, sb.length());
                        _charz.getService().serverMessage("Đã mời " + sb.toString() + " vào trận đấu");
                    }
                }));
            }
        }
        menus.add(new Menu(CMDMenu.EXECUTE, "DS thi đấu", () -> {
            showPlayerList(_charz);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Kết quả", () -> {
            StringBuffer sb = new StringBuffer();
            for (int i = results.size() - 1; i >= 0; i--) {
                sb.append(results.get(i)).append("\n");
            }
            _charz.getService().showAlert("Kết Quả", sb.toString());
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Rời khỏi nơi này", () -> {
            short[] xy = NinjaUtils.getXY(_charz.mapBeforeEnterPB);
            _charz.setXY(xy[0], xy[1]);
            _charz.changeMap(_charz.mapBeforeEnterPB);
        }));
        _charz.getService().openUIMenu();
    }

    private void showPlayerList(Char _charz) {
        StringBuffer sb = new StringBuffer();
        if (this.invited) {
            addTeamInfoToSB(sb, whiteName, whiteMemberNames);
            sb.append("______________\n\n");
            addTeamInfoToSB(sb, blackName, blackMemberNames);
        } else {
            sb.append("Hiện chưa có cặp thi đấu nào");
        }

        _charz.getService().showAlert("DS Thi Đấu", sb.toString());
    }

    public void addTeamInfoToSB(StringBuffer sb, String teamName, List<String> memberNames) {
        sb.append(">>> ").append(teamName).append(" <<<\n");
        for (String name : memberNames) {
            sb.append("- ").append(name);
            Char _char = ServerManager.findCharByName(name);
            if ((_char) != null) {
                sb.append(" (").append(_char.level).append(")");
                sb.append(" - ").append(isFightingPlayer(_char) ? "Có mặt" : "Vắng mặt").append("\n");
            } else {
                sb.append(" - Không hoạt động\n");
            }
        }
    }

    @Override
    public void join(Char p) {
        Zone preZone = p.zone;
        p.mapId = (short) this.tilemap.id;
        p.zone = this;
        if (preZone != null) {
            if (preZone.tilemap.isWorld() && !tilemap.isWorld()) {
                p.removeMemberFromWorld(preZone, this);
            }
        }
        if (isFightingPlayer(p)) {
            addPlayerToFirtGroup(p);
        } else {
            addPlayerToGroup(p);
        }
        GroupService service = groups.get(p.getGroupIndex()).getService();
        // thêm player vào khu cho người chơi khác
        service.playerAdd(p);
        if (p.coat > 0) {
            service.loadCoat(p);
        }
        if (p.glove > 0) {
            service.loadGlove(p);
        }
        if (p.mount[4] != null) {
            service.loadMount(p);
        }
        if (p.mobMe != null) {
            service.loadPet(p);
        }
        if (p.fashion[10] != null && p.honor > 0) {
            service.loadHonor(p);
        }
        addChar(p);
        p.getService().sendZone();
        p.getService().sendItemMap();
        loadMap(p);// load người trong map

        // =============Phân thân vào map cùng chủ thân (đi
        // theo)===========================
        if (p.clone != null && p.clone.isNhanBan && !p.clone.isDead) {
            p.clone.setXY(p.x, p.y);
            join(p.clone);
        }
        p.setTypePk(Char.PK_NORMAL);
        p.getService().sendTimeInMap(countDown);
    }

    @Override
    public void out(Char p) {
        GroupService service = groups.get(p.getGroupIndex()).getService();
        if (p.mob != null) {
            p.mob = null;
        }
        removeChar(p);
        service.playerRemove(p.id);
        if (p.clone != null && p.clone.isNhanBan && !p.clone.isDead) {
            out(p.clone);
        }
        p.setTypePk(Char.PK_NORMAL);
        removePlayerFromGroup(p);
    }

    @Override
    public void loadMap(Char p) {
        Group group = groups.get(p.getGroupIndex());
        List<Char> players = group.getPlayers();
        if (group.getIndex() != 0) {
            for (Player player : getPlayers()) {
                players.add(player.player);
            }
        }

        synchronized (players) {
            for (Char _char2 : players) {
                try {
                    if (p != _char2) {
                        p.getService().playerAdd(_char2);
                    }
                    if (_char2.mount[4] != null) {
                        p.getService().loadMount(_char2);
                    }
                    if (_char2.coat > 0) {
                        p.getService().loadCoat(_char2);
                    }
                    if (_char2.glove > 0) {
                        p.getService().loadGlove(_char2);
                    }
                    if (_char2.mobMe != null) {
                        p.getService().loadPet(_char2);
                    }
                    if (_char2.fashion[10] != null && _char2.honor > 0) {
                        p.getService().loadHonor(_char2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Player {

        public Char player;
        public int faction;

        public Player(Char _char, int faction) {
            this.player = _char;
            this.faction = faction;
        }

        public void join() {
            if (isAttendance()) {
                int pointX = this.faction == 1 ? 260 : 510;
                player.setXY((short) pointX, (short) 288);
                player.getService().resetPoint();
                getService().teleport(player);
                player.setTypePk(faction == WHITE ? Char.PK_PHE1 : Char.PK_PHE2);
                player.zone.getService().changePk(player);
                player.setNonCombatState(60);
            }
        }

        public void out() {
            if (isAttendance()) {
                int randPointX = (int)NinjaUtils.nextInt(70, 650);
                player.setXY((short) randPointX, (short) 384);
                player.getService().resetPoint();
                getService().teleport(player);
                player.setTypePk(Char.PK_NORMAL);
            }
        }

        protected boolean isDead() {
            return !player.isDead && player.isHuman;
        }

        protected boolean isAttendance() {
            return player.zone.tilemap.isTalentShow();
        }

    }

}

@Getter
class Group {

    private int index;
    private List<Char> players;
    private GroupService service;

    public Group(int index, TalentShow z) {
        this.index = index;
        this.players = new ArrayList<>();
        this.service = new GroupService(z, this);
    }

    public void addPlayer(Char p) {
        synchronized (this.players) {
            this.players.add(p);
        }
    }

    public boolean isMaxium() {
        synchronized (this.players) {
            return this.players.size() >= 20;
        }
    }

    public void removePlayer(Char p) {
        synchronized (this.players) {
            this.players.remove(p);
        }
    }

    public boolean isExist(Char p) {
        synchronized (this.players) {
            return this.players.indexOf(p) != -1;
        }
    }

}

class GroupService extends AbsService {

    private Group group;
    private TalentShow tls;

    public GroupService(TalentShow z, Group group) {
        this.group = group;
        this.tls = z;
    }

    public void loadPet(Char _char) {
        List<Char> players = getPlayers();
        synchronized (players) {
            for (Char p : players) {
                p.getService().loadPet(_char);
            }
        }
    }

    @Override
    public void sendMessage(Message ms) {
        List<Char> players = getPlayers();
        synchronized (players) {
            for (Char p : players) {
                if (p.isBot() || p.isNhanBan) {
                    continue;
                }
                p.getService().sendMessage(ms);

            }
        }
    }

    public List<Char> getPlayers() {
        List<Char> players = new ArrayList<Char>();

        if (group.getIndex() == 0) {
            for (Group group : tls.groups) {
                players.addAll(group.getPlayers());
            }
        } else {
            players = group.getPlayers();
        }
        return players;
    }

    @Override
    public void chat(String name, String text) {

    }

}
