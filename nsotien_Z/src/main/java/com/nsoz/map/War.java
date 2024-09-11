package com.nsoz.map;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.nsoz.constants.ItemName;
import com.nsoz.db.mongodb.MongoDbConnection;
import com.nsoz.event.KoroKing;
import com.nsoz.event.Event;
import com.nsoz.item.Item;
import com.nsoz.item.ItemFactory;
import com.nsoz.model.Char;
import com.nsoz.model.WarMember;
import com.nsoz.option.ItemOption;
import com.nsoz.server.Config;
import com.nsoz.server.GlobalService;
import com.nsoz.util.Log;
import com.nsoz.util.NinjaUtils;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 * F
 *
 * @author PC
 */
public class War {

    public static final int TYPE_LEVEL_30_TO_50 = 0;
    public static final int TYPE_LEVEL_70_TO_90 = 1;
    public static final int TYPE_ALL_LEVEL = 2;
    public static final int TYPE_CUSTOM_LEVEL = 3;

    public static final int TOP_MONTH = 0;
    public static final int TOP_WEEK = 1;

    public String whiteName;
    public String blackName;
    public int whitePoint;
    public int blackPoint;
    public ArrayList<String> mandatoryWhiteMemberNames;
    public ArrayList<String> mandatoryBlackMemberNames;
    public ArrayList<Char> whiteMembers;
    public ArrayList<Char> blackMembers;
    public int whiteTurretKill;
    public int blackTurretKill;
    public int numberJoinedWhite;
    public int numberJoinedBlack;
    public ArrayList<WarMember> members;
    public int type;
    public int status;
    public long time;
    public ReadWriteLock lock = new ReentrantReadWriteLock();

    public War(int type) {
        this.blackMembers = new ArrayList<>();
        this.whiteMembers = new ArrayList<>();
        this.members = new ArrayList<>();
        this.whitePoint = 0;
        this.blackPoint = 0;
        this.numberJoinedWhite = 0;
        this.numberJoinedBlack = 0;
        this.type = type;
        this.time = System.currentTimeMillis();
        this.whiteTurretKill = 0;
        this.blackTurretKill = 0;
        if (this.type == TYPE_CUSTOM_LEVEL) {
            this.notify("Ninja tài năng đã mở cửa điểm danh, các đội thi đấu có 2 phút để gặp mặt NPC Kanata và tiến hành tham gia phòng chờ");
        } else {
            this.notify("Chiến trường đã mở cửa điểm danh");
        }
    }

    public void initMap() {
        for (Map map : MapManager.getInstance().getMaps()) {
            if (map.id >= 98 && map.id <= 104) {
                map.setWar(this);
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
        info += "\nTiêu diệt: " + this.whiteTurretKill + " Hắc Long Trụ";
        // info += "\nĐiểm toàn đội: +" + whitePointAdd + " và -" + blackPointAdd;
        info += "\n";
        info += "\nHắc giả: " + blackPoint + " (" + (!checkWin ? "Thắng" : "Thua") + ")";
        info += "\nTiêu diệt: " + this.blackTurretKill + " Bạch Long Trụ";
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
        list.sort((m1, m2) -> (new Integer(m2.point).compareTo((new Integer(m1.point)))));
        int size = list.size();
        if (size > 10) {
            size = 10;
        }
        for (int i = 0; i < size; i++) {
            WarMember mem = list.get(i);
            info += "\n" + (i + 1) + ". " + mem.name + ": " + mem.point + " (" + (mem.faction == 0 ? "Bạch" : "Hắc") + ")";
            info += "\nDanh hiệu: " + mem.getRank();
        }
        _char.getService().reviewCT(info, type == TYPE_CUSTOM_LEVEL ? false : reward);
    }

    public void reward(Char _char) {
        if (this.status == 2 && _char.faction != -1 && this.time == _char.time && !_char.isRewarded) {
            int whitePointAdd = this.whiteTurretKill * 500;
            int blackPointAdd = this.blackTurretKill * 500;
            int pointCT = _char.member.point;
            if (_char.faction == 0) {
                pointCT += whitePointAdd - blackPointAdd;
            }
            if (_char.faction == 1) {
                pointCT += blackPointAdd - whitePointAdd;
            }
            if (pointCT < 200 || _char.member.point < 200) {
                return;
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
            list.sort((m1, m2) -> (new Integer(m2.point).compareTo((new Integer(m1.point)))));
            _char.isRewarded = true;
            int quantity = pointCT / 200;
            int quantityPhao = pointCT / 10;

            if (Event.isKoroKing()) {
                KoroKing.addTrophy(_char, 20);
            }

            if (pointCT >= 1000) {
                int whitePoint = this.whitePoint;
                int blackPoint = this.blackPoint;
                int checkWin = whitePoint > blackPoint ? 0 : 1;
                if (checkWin == _char.faction) {
                    quantity += 5;
                    quantityPhao += 100;
                }
            }
            if (quantity > 0) {
                Item item = ItemFactory.getInstance().newItem(ItemName.RUONG_CHIEN_TRUONG);
                item.setQuantity(quantity);
                item.isLock = false;
                _char.addItemToBag(item);
            }
            int size = this.members.size();
            if (size > 3) {
                size = 3;
            }
            for (int i = 0; i < size; i++) {
                WarMember mem = list.get(i);
                if (mem != null && mem.id == _char.id) {
                    int itemId = ItemName.SON_TINH;
                    if (NinjaUtils.nextBoolean()) {
                        itemId = ItemName.THUY_TINH;
                    }
                    Item item = ItemFactory.getInstance().newItem(itemId);
                    item.isLock = true;
                    item.setQuantity(1);
                    ArrayList<ItemOption> itemOptions = new ArrayList<>();
                    itemOptions.add(new ItemOption(87, 5000));
                    itemOptions.add(new ItemOption(82, 5000));
                    item.options = itemOptions;
                    item.expire = System.currentTimeMillis() + 259200000L;
                    _char.addItemToBag(item);
                    _char.addGold(i == 0 ? 5000 : (i == 1 ? 3000 : 2000));
                    return;
                }
            }
        }
    }

    public void start() {
        this.status = 1;
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

    public void addTurretPoint(int faction) {
        if (faction == 0) {
            this.whiteTurretKill += 1;
        }
        if (faction == 1) {
            this.blackTurretKill += 1;
        }
    }

    public byte getFactionInMandatory(Char _char) {
        if (mandatoryWhiteMemberNames.contains(_char.name)) {
            return 0;
        } else if (mandatoryBlackMemberNames.contains(_char.name)) {
            return 1;
        } else {
            return -1;
        }
    }

    public void end() {
        this.status = 2;
        lock.writeLock().lock();
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

    public byte getWinner() {
        int whitePoint = this.whitePoint + this.whiteTurretKill * 500;
        int blackPoint = this.blackPoint + this.blackTurretKill * 500;
        if (whitePoint > blackPoint) {
            return 0;
        }
        return 1;
    }

    public void notify(String text) {
        GlobalService.getInstance().chat("Hệ Thống", text);
    }

    public static void timer(int hours, int minutes, int seconds, int t) {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        ZonedDateTime zonedNext5 = zonedNow.withHour(hours).withMinute(minutes).withSecond(seconds);
        if (zonedNow.compareTo(zonedNext5) > 0) {
            zonedNext5 = zonedNext5.plusDays(1);
        }

        Duration duration = Duration.between(zonedNow, zonedNext5);
        long initalDelay = duration.getSeconds();
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    War war = MapManager.getInstance().normalWar = new War(t);
                    war.initMap();
                    war.register();
                    Thread.sleep(1800000);
                    war.start();
                    Thread.sleep(3600000);
                    war.end();
                } catch (InterruptedException ex) {
                    Logger.getLogger(War.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(runnable, initalDelay, 1 * 24 * 60 * 60, TimeUnit.SECONDS);
        Log.debug("Chien truong type: " + t + " " + hours + "h" + minutes);
    }

    public static void viewTop(Char p, int type, int typeTop) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        String title = null;
        MongoCollection collection = MongoDbConnection.getCollection("top_war");
        StringBuilder sb = new StringBuilder();
        int serverID = Config.getInstance().getServerID();
        if (typeTop == TOP_MONTH) {
            title = "Top Tháng";
            BasicDBObject match = new BasicDBObject("$match", new BasicDBObject("month", month).append("year", year).append("server_id", serverID).append("type", type));
            BasicDBObject group = new BasicDBObject("$group", new BasicDBObject("_id", "$player_id").append("name", new BasicDBObject("$first", "$name")).append("total_point", new BasicDBObject("$sum", "$point")));
            BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("total_point", -1));
            BasicDBObject limit = new BasicDBObject("$limit", 10);
            List<BasicDBObject> pipeline = Arrays.asList(match, group, sort, limit);
            AggregateIterable<Document> documents = collection.aggregate(pipeline);
            int i = 1;
            for (Document document : documents) {
                sb.append(String.format("%d. %s: %s điểm", i, document.get("name"), document.get("total_point"))).append("\n");
            }
        }
        if (typeTop == TOP_WEEK) {
            title = "Top Tuần";
            Bson filterType = Filters.eq("type", type);
            Bson filterServerID = Filters.eq("server_id", serverID);
            Bson filterWeek = Filters.eq("week", weekOfYear);
            Bson filterMonth = Filters.eq("month", month);
            Bson filterYear = Filters.eq("year", year);
            Bson filter = Filters.and(filterType, filterServerID, filterWeek, filterMonth, filterYear);
            FindIterable<Document> documents = collection.find(filter).sort(new Document("point", -1)).limit(10);
            int i = 1;
            for (Document document : documents) {
                sb.append(String.format("%d. %s: %s điểm", i, document.get("name"), document.get("point"))).append("\n");
            }
        }

        p.getService().showAlert(title, sb.toString());
    }

}
