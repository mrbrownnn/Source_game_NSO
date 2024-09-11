package com.nsoz.map;

import com.nsoz.clan.Clan;
import com.nsoz.constants.ItemName;
import com.nsoz.event.Event;
import com.nsoz.event.KoroKing;
import com.nsoz.item.Item;
import com.nsoz.item.ItemFactory;
import com.nsoz.model.Char;
import com.nsoz.model.WarMember;
import com.nsoz.option.ItemOption;
import com.nsoz.server.GlobalService;
import com.nsoz.util.Log;
import com.nsoz.util.NinjaUtils;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WarClan {

    public int coinTotal;
    public String whiteName;
    public String blackName;
    public ArrayList<String> mandatoryWhiteMemberNames;
    public ArrayList<String> mandatoryBlackMemberNames;
    public ArrayList<Char> whiteMembers;
    public ArrayList<Char> blackMembers;
    public int whitePoint;
    public int blackPoint;
    public int whiteTurretKill;
    public int blackTurretKill;
    public int numberJoinedWhite;
    public int numberJoinedBlack;
    public ArrayList<WarMember> members;
    public Clan clanWhite;
    public Clan clanBlack;
    public long time;
    public int status;

    public ReadWriteLock lock = new ReentrantReadWriteLock();
    public void notify(String text) {
        GlobalService.getInstance().chat("Hệ Thống", text);
    }
    public WarClan(){
        this.blackMembers = new ArrayList<>();
        this.whiteMembers = new ArrayList<>();
        this.members = new ArrayList<>();
        this.numberJoinedWhite = 0;
        this.numberJoinedBlack = 0;
        this.time = System.currentTimeMillis();
        this.whiteTurretKill = 0;
        this.blackTurretKill = 0;
        this.whitePoint = 0;
        this.blackPoint = 0;
        this.whiteName ="";
        this.blackName = "";
    }
    public WarClan(Clan clanWhite, Clan clanBlack){
        this.blackMembers = new ArrayList<>();
        this.whiteMembers = new ArrayList<>();
        this.members = new ArrayList<>();
        this.numberJoinedWhite = 0;
        this.numberJoinedBlack = 0;
        this.time = System.currentTimeMillis();
        this.whiteTurretKill = 0;
        this.blackTurretKill = 0;
        this.whitePoint = 0;
        this.blackPoint = 0;
        this.whiteName =clanWhite.name;
        this.blackName = clanBlack.name;
        this.notify("Gia tộc "+whiteName +" và gia tộc "+ blackName +" đang giao chiến với nhau");
    }

    public void initMap() {
        for (Map map : MapManager.getInstance().getMaps()) {
            if (map.id >= 117 && map.id <= 124) {
                map.setWarClan(this);
                map.initZone();
            }
        }
    }

    public void register() {
        this.status = 0;
    }

    public void viewTop(Char _char) {
        String info = "";
        int whitePointAdd = this.whiteTurretKill * 500;
        int blackPointAdd = this.blackTurretKill * 500;
        int whitePoint = this.whitePoint;
        if (whitePoint < 0) {
            whitePoint = 0;
        }
        int blackPoint = this.blackPoint;
        if (blackPoint < 0) {
            blackPoint = 0;
        }
        boolean checkWin = whitePoint > blackPoint;
        info += "Bạch giả: " + whitePoint + " (" + (checkWin ? "Thắng" : "Thua") + ")";
//        info += "\nTiêu diệt: " + this.whiteTurretKill + " Hắc Long Trụ";
        // info += "\nĐiểm toàn đội: +" + whitePointAdd + " và -" + blackPointAdd;
        info += "\n";
        info += "\nHắc giả: " + blackPoint + " (" + (!checkWin ? "Thắng" : "Thua") + ")";
//        info += "\nTiêu diệt: " + this.blackTurretKill + " Bạch Long Trụ";
        // info += "\nĐiểm toàn đội: +" + blackPointAdd + " và -" + whitePointAdd;
        boolean reward = false;

        info += "\n--------------------------";
        if (_char.faction != -1 && _char.time == this.time && _char.member != null) {
            int pointCT = _char.member.point;
            if (_char.faction == 0) {
                pointCT += whitePointAdd - blackPointAdd;
            }
            if (_char.faction == 1) {
                pointCT += blackPointAdd - whitePointAdd;
            }
            info += "\nĐiểm của bạn: " + pointCT;
            info += "\nK/D: " + _char.nKill + "/" + _char.nDead;
            if (this.status == 2 && _char.faction != -1 && this.time == _char.time && pointCT > 200 && _char.member.point > 200 && !_char.isRewarded) {
                reward = true;
            }
        }
        ArrayList<WarMember> list = new ArrayList<>();
        for (WarMember mem : this.members) {
            WarMember clone = mem.clone();
            if (clone.faction == 0) {
                clone.point += whitePointAdd - blackPointAdd;
            }
            if (clone.faction == 1) {
                clone.point += blackPointAdd - whitePointAdd;
            }
            if (clone.point < 0) {
                clone.point = 0;
            }
            list.add(clone);
        }
        list.sort((m1, m2) -> (Integer.compare(m2.point, m1.point)));
        int size = list.size();
        if (size > 10) {
            size = 10;
        }
        for (int i = 0; i < size; i++) {
            WarMember mem = list.get(i);
            info += "\n" + (i + 1) + ". " + mem.name + ": " + mem.point + " (" + (mem.faction == 0 ? "Bạch" : "Hắc") + ")";
            info += "\nDanh hiệu: " + mem.getRank();
        }
        _char.getService().reviewCT(info,  reward);
    }

    public void finish(final byte type) {
        if (this.status == 2 ) {
            final int coin = this.coinTotal * 2 * 9 / 10;
            if (type == -1) {
                clanWhite.addCoin(coin / 2);
                clanBlack.addCoin(coin / 2);
                for (final WarMember member : members) {
                    Char player = Char.findCharByName(member.name);
                    if (player != null) {
                        player.serverMessage("Hai gia tộc hoà nhau và nhận lại " + coin + " xu gia tộc.");
                    }
                }
            } else if (type == 0) {
                clanWhite.addCoin(coin / 2);
                for (final WarMember member : members) {
                    Char player = Char.findCharByName(member.name);
                    if (player != null) {
                        player.serverMessage("Gia tộc " + clanWhite.name + " giành chiến thắng và nhận được " + coin + " xu gia tộc.");
                    }
                }
            } else if (type == 1) {
                clanBlack.addCoin(coin);
                for (final WarMember member : members) {
                    Char player = Char.findCharByName(member.name);
                    if (player != null) {
                        player.serverMessage("Gia tộc " + clanBlack.name + " giành chiến thắng và nhận được " + coin + " xu gia tộc.");
                    }
                }

            }
        }
    }

    public void end() {
        this.status = 2;
        lock.writeLock().lock();
        if (whitePoint == blackPoint) {
            this.finish((byte) (-1));
        } else if (whitePoint > blackPoint) {
            this.finish((byte) (0));
        } else {
            this.finish((byte) (1));
        }
        try {
            for (Char _char : whiteMembers) {
                try {
                    _char.member.save();
                    short[] xy = NinjaUtils.getXY(_char.mapBeforeEnterPB);
                    _char.setXY(xy);
                    _char.changeMap(_char.mapBeforeEnterPB);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (Char _char : blackMembers) {
                try {
                    _char.member.save();
                    short[] xy = NinjaUtils.getXY(_char.mapBeforeEnterPB);
                    _char.setXY(xy);
                    _char.changeMap(_char.mapBeforeEnterPB);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }


    }
    public void addMember(Char _char) {
        lock.writeLock().lock();
        try {
            if (_char.faction == 0) {
                if (!this.whiteMembers.contains(_char)) {
                    this.whiteMembers.add(_char);
                }
            }
            if (_char.faction == 1) {
                if (!this.blackMembers.contains(_char)) {
                    this.blackMembers.add(_char);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    public void addTurretPoint(int faction) {
        if (faction == 0) {
            this.whiteTurretKill += 1;
        }
        if (faction == 1) {
            this.blackTurretKill += 1;
        }
    }
    public void addMember(WarMember mem) {
        lock.writeLock().lock();
        try {
            this.members.add(mem);
        } finally {
            lock.writeLock().unlock();
        }
    }
    public void removeMember(Char _char) {
        lock.writeLock().lock();
        try {
            if (_char.faction == 0) {
                this.whiteMembers.remove(_char);
            }
            if (_char.faction == 1) {
                this.blackMembers.remove(_char);
            }
        } finally {
            lock.writeLock().unlock();  
        }
    }

    public void start() {
        this.status = 1;
    }
    public static void initWarClan(Clan clanWhite, Clan clanBlack){
        Runnable runnable = new Runnable() {
            private volatile boolean isWarEnded = false;
            public void run() {
                try {
                    WarClan war = MapManager.getInstance().warClan = new WarClan(clanWhite, clanBlack);
                    war.initMap();
                    war.register();
                    Thread.sleep(300000);
                    war.start();
                    Thread.sleep(3600000);
                    war.end();
                    isWarEnded = true;
                } catch (InterruptedException ex) {
                    Logger.getLogger(War.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
