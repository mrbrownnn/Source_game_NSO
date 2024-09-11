package com.nsoz.clan;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.nsoz.item.Item;
import com.nsoz.model.ThanThu;
import com.nsoz.option.ItemOption;
import com.nsoz.util.Log;
import com.nsoz.util.NinjaUtils;
import com.nsoz.lib.ParseData;
import com.nsoz.model.Char;
import lombok.Getter;
import lombok.Setter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Clan {

    public static final int TYPE_NORMAl = 0;
    public static final int TYPE_TOCPHO = 3;
    public static final int TYPE_TOCTRUONG = 4;
    public static final int TYPE_TRUONGLAO = 2;
    public static final int TYPE_UUTU = 1;
    public static final int UP_LEVEL = 5;
    public static final int MOVE_INPUT_MONEY = 2;
    public static final int MOVE_OUT_MEM = 1;
    public static final int CREATE_CLAN = 0;
    public static final int MOVE_OUT_MONEY = 3;
    public static final int FREE_MONEY = 4;

    private static final ClanDAO clanDAO = new ClanDAO();
    public static Map<String, Clan> mapClan = new HashMap<>();
    public static boolean running;

    public static ClanDAO getClanDAO() {
        return clanDAO;
    }

    public static void start() {
        running = true;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    long l1 = System.currentTimeMillis();
                    updateClan();
                    long l2 = System.currentTimeMillis();
                    if (l2 - l1 < 1000) {
                        try {
                            Thread.sleep(1000 - (l2 - l1));
                        } catch (InterruptedException ex) {
                            Log.error("clan update err");
                        }
                    }
                }
            }
        }
        );
        thread.start();

    }

    public static void updateClan() {
        List<Clan> clans = clanDAO.getAll();
        mapClan.clear();
        mapClan = clans.stream().collect(Collectors.toMap(Clan::getName, Function.identity()));
        synchronized (clans) {
            for (Clan clan : clans) {
                try {
                    clan.update();
                } catch (Exception e) {
                    Log.error("update err 1");
                }
            }
        }
    }

    public int id;
    public String name;
    public String main_name;
    public String assist_name;
    public byte openDun;
    public byte level;
    public int exp;
    public int coin;
    public String alert;
    public int use_card;
    public byte itemLevel;
    public Date reg_date;
    public String log;
    public Item[] items;
    public ArrayList<ThanThu> thanThus;
    public MemberDAO memberDAO;
    @Setter
    @Getter
    private boolean saving;

    @Getter
    private ClanService clanService;

    public Clan() {
        this.items = new Item[30];
        this.log = "";
        this.clanService = new ClanService(this);
        this.thanThus = new ArrayList<ThanThu>();
        this.memberDAO = new MemberDAO(this);
    }

    public Item[] getItems() {
        Vector<Item> items = new Vector<>();
        for (Item item : this.items) {
            if (item != null) {
                items.add(item);
            }
        }
        return items.toArray(new Item[items.size()]);
    }

    public void update() {
        synchronized (thanThus) {
            for (ThanThu thanThu : thanThus) {
                int eggHatchingTime = thanThu.getEggHatchingTime();
                if (eggHatchingTime > 0) {
                    eggHatchingTime -= 1000;
                    thanThu.setEggHatchingTime(eggHatchingTime);
                    if (eggHatchingTime <= 0) {
                        thanThu.setEggHatchingTime(-1);
                        thanThu.hatchedEgg();
                        Clan.getClanDAO().update(this);
                    }
                }
            }
        }
    }

    public int getIndexItem(Item item) {
        int index = -1;
        Item[] items = getItems();
        for (int i = 0; i < items.length; i++) {
            if (items[i].id == item.id) {
                index = i;
                break;
            }
        }
        return index;
    }

    public int getIndexItem(int itemID) {
        int index = -1;
        Item[] items = getItems();
        for (int i = 0; i < items.length; i++) {
            if (items[i].id == itemID) {
                index = i;
                break;
            }
        }
        return index;
    }

    private boolean isExist(Item item) {
        boolean isExist = false;
        Item[] items = getItems();
        for (Item i : items) {
            if (i.id == item.id) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }

    public void addItem(Item item) {
        int index = getIndexItem(item);
        if (index > -1) {
            this.items[index].add(item.getQuantity());
        } else {
            for (int i = 0; i < this.items.length; i++) {
                if (this.items[i] == null) {
                    this.items[i] = item;
                    break;
                }
            }
        }
    }

    public void removeItem(Item item, int quantity) {
        for (int i = 0; i < this.items.length; i++) {
            if (this.items[i] == item) {
                item.reduce(quantity);
                if (!item.has()) {
                    this.items[i] = null;
                    break;
                }
            }
        }
    }

    public void removeItem(int index, int quantity) {
        if (items[index] != null) {
            items[index].reduce(quantity);
            if (!items[index].has()) {
                items[index] = null;
            }

        }
    }

    public ThanThu getThanThu(int type) {
        synchronized (thanThus) {
            for (ThanThu thanThu : thanThus) {
                if (thanThu.getType() == type) {
                    return thanThu;
                }
            }
        }
        return null;
    }

    public void addExp(int exp) {
        this.exp += exp;
        //Clan.getClanDAO().update(this);
    }

    public void loadItem(JSONArray jArr) {
        for (int i = 0; i < jArr.size(); i++) {
            JSONObject obj = (JSONObject) jArr.get(i);
            ParseData parse = new ParseData(obj);
            Item item = new Item(parse.getInt("id"));
            item.loadHeader(parse);
            item.isLock = parse.getBoolean("isLock");
            item.sys = parse.getByte("sys");
            item.yen = parse.getInt("yen");
            if (item.template.isTypeBody() || item.template.isTypeMount() || item.template.isTypeNgocKham()) {
                item.upgrade = parse.getByte("upgrade");
                JSONArray ability = parse.getJSONArray("options");
                int size2 = ability.size();
                item.options = new ArrayList<>();
                for (int c = 0; c < size2; c++) {
                    JSONArray jAbility = (JSONArray) ability.get(c);
                    int templateId = Integer.parseInt(jAbility.get(0).toString());
                    int param = Integer.parseInt(jAbility.get(1).toString());
                    item.options.add(new ItemOption(templateId, param));
                }
            } else {
                item.upgrade = 0;
            }
            item.setQuantity(parse.getInt("quantity"));
            if (item.hasExpire()) {
                int remaining = (int) (item.getExpire() / 1000 / 60 / 60 / 24 / 30);
                if (remaining > 1) {
                    item.expire = (7 * 24 * 60 * 60 * 1000);
                }
            }
            this.items[i] = item;
        }
    }

    public String getLog() {
        return log;
    }

    public void writeLog(String name, int num, int number) {
        String[] array = log.split("\n");
        log = name + "," + num + "," + number + ","
                + NinjaUtils.dateToString(Date.from(Instant.now()), "yyyy/MM/dd hh:mm:ss") + "\n";
        for (int i = 0; i < array.length; i++) {
            if (i == 10) {
                break;
            }
            log += array[i] + "\n";
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainName() {
        return main_name;
    }

    public void setMainName(String main_name) {
        this.main_name = main_name;
    }

    public String getAssistName() {
        return assist_name;
    }

    public void setAssistName(String assist_name) {
        this.assist_name = assist_name;
    }

    public byte getOpenDun() {
        return openDun;
    }

    public void setOpenDun(byte openDun) {
        this.openDun = openDun;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getExpNext() {
        int expNext = 2000;
        for (int i = 1; i < level; i++) {
            if (i == 1) {
                expNext = 3720;
            } else {
                if (i < 10) {
                    expNext = ((expNext / i) + 310) * (i + 1);
                } else if (i < 20) {
                    expNext = ((expNext / i) + 620) * (i + 1);
                } else {
                    expNext = ((expNext / i) + 930) * (i + 1);
                }
            }
        }
        return expNext;
    }

    public void addCoin(int coin) {
        this.coin += coin;
        //Clan.getClanDAO().update(this);
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public int getFreeCoin() {
        return (30 + getNumberMember() * 5) * 1000;
    }

    public int getCoinUp() {
        return ((this.level - 1) / 10 + 1) * 100000 + 500000;
    }

    public String getAlert() {
        return "Thông báo: " + alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public int getUseCard() {
        return use_card;
    }

    public void setUseCard(int use_card) {
        this.use_card = use_card;
    }

    public byte getItemLevel() {
        return itemLevel;
    }

    public void setItemLevel(byte itemLevel) {
        this.itemLevel = itemLevel;
    }

    public Date getRegDate() {
        return reg_date;
    }

    public void setRegDate(Date reg_date) {
        this.reg_date = reg_date;
    }

    public Member getMemberByName(String name) {
        List<Member> members = memberDAO.getAll();
        synchronized (members) {
            for (Member mem : members) {
                if (mem.getName().equals(name)) {
                    return mem;
                }
            }
        }
        return null;
    }

    public int getNumberMember() {
        return memberDAO.getAll().size();
    }

    public int getMemberMax() {
        return this.level * 5 + 45;
    }

    public int getNumberSameType(int type) {
        int number = 0;
        List<Member> members = memberDAO.getAll();
        synchronized (members) {
            for (Member mem : members) {
                if (mem != null && mem.getType() == type) {
                    number++;
                }
            }
        }
        return number;
    }

    public List<Char> getOnlineMembers() {
        List<Char> chars = new ArrayList<>();
        List<Member> members = memberDAO.getAll();
        synchronized (members) {
            for (Member mem : members) {
                if (mem != null && mem.isOnline() && mem.getChar() != null) {
                    chars.add(mem.getChar());
                }
            }
        }
        return chars;
    }
}
