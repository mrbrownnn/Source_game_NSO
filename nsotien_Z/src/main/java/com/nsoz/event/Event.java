package com.nsoz.event;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nsoz.constants.ConstTime;
import com.nsoz.item.ItemManager;
import com.nsoz.item.Item;
import com.nsoz.constants.ItemName;
import com.nsoz.constants.ItemOptionName;
import com.nsoz.model.Char;
import com.nsoz.option.ItemOption;
import com.nsoz.constants.NpcName;
import com.nsoz.constants.SQLStatement;
import com.nsoz.db.jdbc.DbManager;
import com.nsoz.event.eventpoint.EventPoint;
import com.nsoz.event.eventpoint.Point;
import com.nsoz.item.ItemFactory;
import com.nsoz.server.GlobalService;
import com.nsoz.util.NinjaUtils;
import com.nsoz.lib.RandomCollection;
import com.nsoz.map.zones.Zone;
import com.nsoz.server.Config;
import com.nsoz.util.Log;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

public abstract class Event {

    public static final int NGAY_PHU_NU_VIET_NAM = 0;
    public static final int KOROKING = 1;
    public static final int TRUNG_THU = 2;
    public static final int HALLOWEEN = 3;
    public static final int NOEL = 4;
    public static final int LUNAR_NEW_YEAR = 5;
    public static final int WOMENS_DAY = 6;
    public static final byte SEA_GAME = 7;
    public static final int SUMMER = 8;

    public static final byte DOI_BANG_LUONG = 0;
    public static final byte DOI_BANG_XU = 1;

    public static final long EXPIRE_7_DAY = 604800000L;
    public static final long EXPIRE_30_DAY = 2592000000L;

    public static final RandomCollection<Integer> DOI_PHAN_TU = new RandomCollection<>();

    static {
        DOI_PHAN_TU.add(1, ItemName.LONG_DEN_BUOM_BUOM_THOI_TRANG);
        DOI_PHAN_TU.add(1, ItemName.LONG_DEN_HOA_SEN_THOI_TRANG);
        DOI_PHAN_TU.add(1, ItemName.LONG_DEN_KEO_QUAN_THOI_TRANG);
        DOI_PHAN_TU.add(1, ItemName.LONG_DEN_MAT_TRANG_THOI_TRANG);
        DOI_PHAN_TU.add(1, ItemName.LONG_DEN_MAT_TROI_THOI_TRANG);
        DOI_PHAN_TU.add(1, ItemName.LONG_DEN_NGOI_SAO_THOI_TRANG);
        DOI_PHAN_TU.add(1, ItemName.LONG_DEN_TRAI_TIM_THOI_TRANG);
        DOI_PHAN_TU.add(1, ItemName.LONG_DEN_TRON_THOI_TRANG);
    }

    private static Event instance;

    public static void init() {
        if (Config.getInstance().getEvent() != null) {
            try {
                instance = (Event) Class.forName(Config.getInstance().getEvent()).newInstance();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                Log.error(ex.getMessage(), ex);
            }
        }
    }

    public static Event getEvent() {
        return instance;
    }

    public static boolean isEvent() {
        return (instance != null && !instance.isEnded());
    }

    public static boolean isTrungThu() {
        return isEvent() && instance instanceof TrungThu;//2  lỗi
    }
    public static boolean isSummer() {
        return isEvent() && instance instanceof SumMer;
    }

    public static boolean isKoroKing() {
        return isEvent() && instance instanceof KoroKing;//1  lỗi
    }

    public static boolean isVietnameseWomensDay() {
        return isEvent() && instance instanceof VietnameseWomensDay;//0 
    }

    public static boolean isInternationalWomensDay() {
        return isEvent() && instance instanceof InternationalWomensDay;//6  
    }

    public static boolean isHalloween() {
        return isEvent() && instance instanceof Halloween;//3 
    }

    public static boolean isNoel() {
        return isEvent() && instance instanceof Noel;//4 được  
    }

    public static boolean isLunarNewYear() {
        return isEvent() && instance instanceof LunarNewYear;//5 
    }

    public static LunarNewYear getLunarNewYear() {
        if (instance instanceof LunarNewYear) {
            return (LunarNewYear) instance;
        }
        return null;
    }

    public void doiLongDen(Char p, byte type, int index) {
        List<Item> list = p.getListItemByID(ItemName.LONG_DEN_TRON, ItemName.LONG_DEN_CA_CHEP,
                ItemName.LONG_DEN_MAT_TRANG, ItemName.LONG_DEN_NGOI_SAO);
        if (index < 0 || index >= list.size()) {
            return;
        }
        if (!isEvent()) {
            p.getService().npcChat(NpcName.TIEN_NU, "Sự kiện đã kết thúc!");
            return;
        }
        if (type == DOI_BANG_LUONG) {
            if (p.user.gold < 100) {
                p.getService().npcChat(NpcName.TIEN_NU, "Không đủ lượng!");
                return;
            }
            p.addGold(-100);
        }
        if (type == DOI_BANG_XU) {
            if (p.coin < 2000000) {
                p.getService().npcChat(NpcName.TIEN_NU, "Không đủ xu!");
                return;
            }
            p.addCoin(-2000000);
        }
        Item item = list.get(index);
        p.removeItem(item.index, 1, true);
        int itemID = DOI_PHAN_TU.next();
        Item itm = ItemFactory.getInstance().newItem(itemID);
        for (ItemOption o : item.options) {
            itm.options.add(o);
        }

        if ((int)NinjaUtils.nextInt(200) == 0) {
            itm.options.add(new ItemOption(ItemOptionName.MIEN_GIAM_SAT_THUONG_POINT_PERCENT_TYPE_8,
                    (int)NinjaUtils.nextInt(1, 30)));
        }

        if (type == DOI_BANG_LUONG) {
            itm.randomOptionLongDen();
        }

        itm.expire = System.currentTimeMillis() + EXPIRE_30_DAY;
        p.addItemToBag(itm);
        p.getService().endDlg(true);
    }

    public static void useVipEventItem(Char _char, int type, RandomCollection<Integer> rc) {
        int itemId = rc.next();
        Item itm = ItemFactory.getInstance().newItem(itemId);
        itm.isLock = false;
        itm.expire = System.currentTimeMillis();

        long month = (int)NinjaUtils.nextInt(1, type == 2 ? 3 : 2);
        long expire = month * ConstTime.MONTH;
        itm.expire += expire;

        if (type == 2 && (int)NinjaUtils.nextInt(1, 35) == 1) {
            itm.expire = -1;
        }

        if (itm.id == ItemName.MAT_NA_HO) {
            itm.randomOptionTigerMask();
        }

        _char.addItemToBag(itm);
    }

    @Getter
    @Setter
    protected int id;
    protected List<EventPoint> eventPoints;
    @Getter
    protected RandomCollection<Integer> itemsThrownFromMonsters;
    @Getter
    protected RandomCollection<Integer> itemsRecFromCoinItem;
    @Getter
    protected RandomCollection<Integer> itemsRecFromGoldItem;
    @Getter
    protected RandomCollection<Integer> itemsRecFromGold2Item;
    protected Set<String> keyEventPoint;
    protected Calendar endTime = Calendar.getInstance();

    public Event() {
        itemsThrownFromMonsters = new RandomCollection<>();
        itemsRecFromCoinItem = new RandomCollection<>();
        itemsRecFromGoldItem = new RandomCollection<>();
        itemsRecFromGold2Item = new RandomCollection<>();
        eventPoints = new ArrayList<>();
        keyEventPoint = new TreeSet<>();
        initRandomItem();
    }

    public abstract void initStore();

    public int randomItemID() {
        return itemsThrownFromMonsters.next();
    }

    public abstract void action(Char p, int type, int amount);

    public abstract void menu(Char p);

    public void useItem(Char _char, Item item) {
    }

    public EventPoint createEventPoint() {
        EventPoint eventPoint = new EventPoint();
        keyEventPoint.forEach((key) -> {
            eventPoint.add(new Point(key, 0, 0));
        });
        return eventPoint;
    }

    public void loadEventPoint() {
        try {
            eventPoints.clear();
            PreparedStatement ps = DbManager.getInstance().getConnection(DbManager.GAME)
                    .prepareStatement(SQLStatement.LOAD_EVENT_POINT);
            ps.setInt(1, this.id);
            ps.setInt(2, Config.getInstance().getServerID());
            ResultSet rs = ps.executeQuery();
            Gson g = new Gson();
            while (rs.next()) {
                EventPoint eventPoint = createEventPoint();
                int id = rs.getInt("id");
                int playerID = rs.getInt("player_id");
                String name = rs.getString("name");
                ArrayList<Point> points = g.fromJson(rs.getString("point"), new TypeToken<ArrayList<Point>>() {
                }.getType());
                eventPoint.setId(id);
                eventPoint.setPlayerID(playerID);
                eventPoint.setPlayerName(name);
                eventPoint.setPoints(points);
                eventPoint.addIfMissing(keyEventPoint);
                eventPoints.add(eventPoint);
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(Event.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addEventPoint(EventPoint eventPoint) {
        synchronized (eventPoints) {
            eventPoints.add(eventPoint);
        }
    }

    public void removeEventPoint(EventPoint eventPoint) {
        synchronized (eventPoints) {
            eventPoints.remove(eventPoint);
        }
    }

    public EventPoint findEventPointByPlayerID(int playerID) {
        synchronized (eventPoints) {
            for (EventPoint ev : eventPoints) {
                if (ev.getPlayerID() == playerID) {
                    return ev;
                }
            }
            return null;
        }
    }

    public abstract void initMap(Zone zone);

    // use only 1 item
    public boolean useEventItem(Char p, int itemId, RandomCollection<Integer> rc) {
        int[][] itemRequires = new int[][] { { itemId, 1 } };
        return useEventItem(p, 1, itemRequires, 0, 0, 0, rc);
    }

    public boolean useEventItem(Char p, int number, int[][] itemRequire, int gold, int coin, int yen,
            RandomCollection<Integer> rc) {
        return makeEventItem(p, number, itemRequire, gold, coin, yen, rc, -1);
    }

    public boolean useEventItem(Char p, int number, int gold, int coin, RandomCollection<Integer> rc) {
        return makeEventItem(p, number, new int[][] {}, gold, coin, 0, rc, -1);
    }

    public boolean makeEventItem(Char p, int number, int[][] itemRequire, int gold, int coin, int yen,
            int itemIdReceive) {
        return makeEventItem(p, number, itemRequire, gold, coin, yen, null, itemIdReceive);
    }

    public boolean makeEventItem(Char p, int number, int[][] itemRequire, int gold, int coin, int yen,
            RandomCollection<Integer> rc, int itemIdReceive) {
        if (number < 1) {
            p.getService().npcChat(NpcName.TIEN_NU, "Số lượng tối thiểu là 1.");
            return false;
        }

        if (number > 1000) {
            p.getService().npcChat(NpcName.TIEN_NU, "Số lượng tối đa là 1.000.");
            return false;
        }

        int priceGold = number * gold;
        int priceCoin = number * coin;
        int priceYen = number * yen;

        for (int i = 0; i < itemRequire.length; i++) {
            int itemId = itemRequire[i][0];
            int amount = itemRequire[i][1] * number;
            int index = p.getIndexItemByIdInBag(itemId);
            if (index == -1 || p.bag[index] == null || !p.bag[index].has(amount)) {
                p.getService().npcChat(NpcName.TIEN_NU, "Không đủ " + ItemManager.getInstance().getItemName(itemId));
                return false;
            }
        }
        if (p.yen < priceYen) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ yên");
            return false;
        } else if (p.user.gold < priceGold) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ lượng");
            return false;
        } else if (p.coin < priceCoin) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ xu");
            return false;
        } else if (rc != null && p.getSlotNull() < number) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return false;
        } else if (itemIdReceive != -1 && p.getSlotNull() < 1) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return false;
        }

        if (priceYen > 0) {
            p.addYen(-priceGold);
        }
        if (priceGold > 0) {
            p.addGold(-priceGold);
        }

        if (priceCoin > 0) {
            p.addCoin(-priceCoin);
        }

        for (int i = 0; i < itemRequire.length; i++) {
            int itemId = itemRequire[i][0];
            int amount = itemRequire[i][1] * number;
            int index = p.getIndexItemByIdInBag(itemId);
            p.removeItem(index, amount, true);
        }

        if (rc != null) {
            for (int i = 0; i < number; i++) {
                if (!p.duatop){
                    p.addExp(8000000);
                }
                int itemId = rc.next();
                Item itm = ItemFactory.getInstance().newItem(itemId);
                Item itmUsed = ItemFactory.getInstance().newItem(itemRequire[0][0]); // item used
                itm.initExpire();
                if (itm.id == ItemName.THONG_LINH_THAO) {
                    itm.setQuantity((int)NinjaUtils.nextInt(5, 10));
                } else if (itm.id == ItemName.MAT_NA_HO) {
                    itm.randomOptionTigerMask();
                }
                p.addItemToBag(itm);
//              ktg
                if (itemId == ItemName.BAT_BAO || itemId == ItemName.RUONG_BACH_NGAN
                        || itemId == ItemName.RUONG_HUYEN_BI || itemId == ItemName.HARLEY_DAVIDSON) {

                    GlobalService.getInstance().chat("Hệ thống","Người chơi "+
                            p.name + " sử dụng " + itmUsed.template.name + " nhận được " + itm.template.name);
                }
            }

        } else if (itemIdReceive != -1) {
            Item itm = ItemFactory.getInstance().newItem(itemIdReceive);
            itm.setQuantity(number);
            p.addItemToBag(itm);
            if (priceGold > 0) {
                p.getEventPoint().addPoint(EventPoint.DIEM_TIEU_XAI, 1);
            }
        }
        return true;
    }

    public void viewTop(Char p, String key, String title, String format) {
        List<EventPoint> list = eventPoints.stream().sorted((o1, o2) -> {
            int p1 = o1.getPoint(key);
            int p2 = o2.getPoint(key);
            return p2 - p1;
        }).limit(10).filter(t -> t.getPoint(key) > 0).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        int rank = 1;
        for (EventPoint t : list) {
            sb.append(String.format(format, rank++, t.getPlayerName(), NinjaUtils.getCurrency(t.getPoint(key))))
                    .append("\n");
        }
        p.getService().showAlert(title, sb.toString());
    }

    public int getRanking(Char p, String key) {
        List<EventPoint> list = eventPoints.stream().sorted((o1, o2) -> {
            int p1 = o1.getPoint(key);
            int p2 = o2.getPoint(key);
            return p2 - p1;
        }).limit(10).filter(t -> t.getPoint(key) > 0).collect(Collectors.toList());
        int rank = 0;
        for (EventPoint t : list) {
            rank++;
            if (t.getPlayerName().equals(p.name)) {
                return rank;
            }
        }
        return 99;
    }

    public boolean isEnded() {
        return endTime.getTime().getTime() - System.currentTimeMillis() <= 0;
    }

    public void initRandomItem() {
        itemsRecFromCoinItem.add(0.01, ItemName.RUONG_BACH_NGAN);
        itemsRecFromCoinItem.add(0.1, ItemName.BAT_BAO);
        itemsRecFromCoinItem.add(2, ItemName.LONG_KHI);
        itemsRecFromCoinItem.add(10, ItemName.HOA_TUYET);
        itemsRecFromCoinItem.add(10, ItemName.HUYEN_TINH_NGOC);
        itemsRecFromCoinItem.add(10, ItemName.HUYET_NGOC);
        itemsRecFromCoinItem.add(10, ItemName.LAM_TINH_NGOC);
        itemsRecFromCoinItem.add(10, ItemName.LUC_NGOC);
        itemsRecFromCoinItem.add(10, ItemName.XE_MAY);
        itemsRecFromCoinItem.add(35, ItemName.XICH_NHAN_NGAN_LANG);
        itemsRecFromCoinItem.add(70, ItemName.LONG_DEN_TRON);
        itemsRecFromCoinItem.add(70, ItemName.LONG_DEN_CA_CHEP);
        itemsRecFromCoinItem.add(70, ItemName.LONG_DEN_NGOI_SAO);
        itemsRecFromCoinItem.add(70, ItemName.LONG_DEN_MAT_TRANG);
        itemsRecFromCoinItem.add(60, ItemName.BANH_RANG);
        itemsRecFromCoinItem.add(80, ItemName.THE_BAI_KINH_NGHIEM_GIA_TOC_SO);
        itemsRecFromCoinItem.add(40, ItemName.THE_BAI_KINH_NGHIEM_GIA_TOC_TRUNG);
        itemsRecFromCoinItem.add(80, ItemName.DA_CAP_9);
        itemsRecFromCoinItem.add(30, ItemName.DA_CAP_10);
        itemsRecFromCoinItem.add(80, ItemName.MINH_MAN_DAN);
        itemsRecFromCoinItem.add(80, ItemName.LONG_LUC_DAN);
        itemsRecFromCoinItem.add(80, ItemName.KHANG_THE_DAN);
        itemsRecFromCoinItem.add(80, ItemName.SINH_MENH_DAN);
        itemsRecFromCoinItem.add(3, ItemName.BI_KIP_CUNG);
        itemsRecFromCoinItem.add(3, ItemName.BI_KIP_DAO);
        itemsRecFromCoinItem.add(3, ItemName.BI_KIP_KIEM_THUAT);
        itemsRecFromCoinItem.add(3, ItemName.BI_KIP_KUNAI);
        itemsRecFromCoinItem.add(3, ItemName.BI_KIP_QUAT);
        itemsRecFromCoinItem.add(3, ItemName.BI_KIP_TIEU_THUAT);
        itemsRecFromCoinItem.add(20, ItemName.HOAN_LUONG_CHI_THAO);
        itemsRecFromCoinItem.add(10, ItemName.GIAY_RACH);

        // item receive from gold item
        itemsRecFromGoldItem.add(25, ItemName.HOAN_LUONG_CHI_THAO);
        itemsRecFromGoldItem.add(0.01, ItemName.RUONG_HUYEN_BI);
        itemsRecFromGoldItem.add(0.05, ItemName.RUONG_BACH_NGAN);
        itemsRecFromGoldItem.add(0.1, ItemName.BAT_BAO);
        itemsRecFromGoldItem.add(2, ItemName.LONG_KHI);
        itemsRecFromGoldItem.add(15, ItemName.HOA_TUYET);
        itemsRecFromGoldItem.add(45, ItemName.IK);
        itemsRecFromGoldItem.add(25, ItemName.HUYEN_TINH_NGOC);
        itemsRecFromGoldItem.add(25, ItemName.HUYET_NGOC);
        itemsRecFromGoldItem.add(25, ItemName.LAM_TINH_NGOC);
        itemsRecFromGoldItem.add(25, ItemName.LUC_NGOC);
        itemsRecFromGoldItem.add(25, ItemName.XE_MAY);
        itemsRecFromGoldItem.add(25, ItemName.CAI_TRANG_CHILL_2);
        itemsRecFromGoldItem.add(25, ItemName.CAI_TRANG_SUPER_BLUE);
        itemsRecFromGoldItem.add(25, ItemName.CAI_TRANG_TRAU);
        itemsRecFromGoldItem.add(25, ItemName.CAI_TRANG_YANROBI);
        itemsRecFromGoldItem.add(25, ItemName.MAT_NA_SUPER_BROLY);
        itemsRecFromGoldItem.add(25, ItemName.MAT_NA_ONNA_BUGEISHA);
        itemsRecFromGoldItem.add(50, ItemName.XICH_NHAN_NGAN_LANG);
        itemsRecFromGoldItem.add(50, ItemName.BANH_RANG);
        itemsRecFromGoldItem.add(50, ItemName.LONG_DEN_TRON);
        itemsRecFromGoldItem.add(50, ItemName.LONG_DEN_CA_CHEP);
        itemsRecFromGoldItem.add(50, ItemName.LONG_DEN_NGOI_SAO);
        itemsRecFromGoldItem.add(50, ItemName.LONG_DEN_MAT_TRANG);
        itemsRecFromGoldItem.add(45, ItemName.THE_BAI_KINH_NGHIEM_GIA_TOC_SO);
        itemsRecFromGoldItem.add(40, ItemName.THE_BAI_KINH_NGHIEM_GIA_TOC_TRUNG);
        itemsRecFromGoldItem.add(60, ItemName.DA_CAP_8);
        itemsRecFromGoldItem.add(40, ItemName.DA_CAP_9);
        itemsRecFromGoldItem.add(20, ItemName.DA_CAP_10);
        itemsRecFromGoldItem.add(55, ItemName.MINH_MAN_DAN);
        itemsRecFromGoldItem.add(55, ItemName.LONG_LUC_DAN);
        itemsRecFromGoldItem.add(55, ItemName.KHANG_THE_DAN);
        itemsRecFromGoldItem.add(55, ItemName.SINH_MENH_DAN);
        itemsRecFromGoldItem.add(20, ItemName.BI_KIP_CUNG);
        itemsRecFromGoldItem.add(15, ItemName.BI_KIP_DAO);
        itemsRecFromGoldItem.add(5, ItemName.BI_KIP_KIEM_THUAT);
        itemsRecFromGoldItem.add(8, ItemName.BI_KIP_KUNAI);
        itemsRecFromGoldItem.add(10, ItemName.BI_KIP_QUAT);
        itemsRecFromGoldItem.add(2, ItemName.BI_KIP_TIEU_THUAT);
        itemsRecFromGoldItem.add(10, ItemName.HAC_NGUU);
        itemsRecFromGoldItem.add(50, ItemName.THONG_LINH_THAO);
        itemsRecFromGoldItem.add(2, ItemName.HAKAIRO_YOROI);
        itemsRecFromGoldItem.add(15, ItemName.MANH_NON_JIRAI_);
        itemsRecFromGoldItem.add(15, ItemName.MANH_AO_JIRAI_);
        itemsRecFromGoldItem.add(15, ItemName.MANH_QUAN_JIRAI_);
        itemsRecFromGoldItem.add(15, ItemName.MANH_GANG_TAY_JIRAI_);
        itemsRecFromGoldItem.add(15, ItemName.MANH_GIAY_JIRAI_);
        itemsRecFromGoldItem.add(15, ItemName.MANH_PHU_JIRAI_);
        itemsRecFromGoldItem.add(15, ItemName.MANH_DAY_CHUYEN_JIRAI_);
        itemsRecFromGoldItem.add(15, ItemName.MANH_NGOC_BOI_JIRAI_);
        itemsRecFromGoldItem.add(15, ItemName.MANH_NHAN_JIRAI_);
        itemsRecFromGoldItem.add(15, ItemName.MANH_NON_JUMITO);
        itemsRecFromGoldItem.add(15, ItemName.MANH_AO_JUMITO);
        itemsRecFromGoldItem.add(15, ItemName.MANH_QUAN_JUMITO);
        itemsRecFromGoldItem.add(15, ItemName.MANH_GANG_TAY_JUMITO);
        itemsRecFromGoldItem.add(15, ItemName.MANH_GIAY_JUMITO);
        itemsRecFromGoldItem.add(15, ItemName.MANH_PHU_JUMITO);
        itemsRecFromGoldItem.add(15, ItemName.MANH_DAY_CHUYEN_JUMITO);
        itemsRecFromGoldItem.add(15, ItemName.MANH_NGOC_BOI_JUMITO);
        itemsRecFromGoldItem.add(15, ItemName.MANH_NHAN_JUMITO);

        // item receive from gold 2 item
        itemsRecFromGold2Item.add(0.005, ItemName.RUONG_HUYEN_BI);
        itemsRecFromGold2Item.add(0.01, ItemName.RUONG_BACH_NGAN);
        itemsRecFromGold2Item.add(0.05, ItemName.BAT_BAO);
        itemsRecFromGold2Item.add(0.1, ItemName.LONG_KHI);
        itemsRecFromGold2Item.add(0.5, ItemName.HOA_TUYET);
        itemsRecFromGold2Item.add(5, ItemName.THUOC_CAI_TIEN);
        itemsRecFromGold2Item.add(5, ItemName.XE_MAY);
        itemsRecFromGold2Item.add(5, ItemName.PET_PHU_THU_BI_NGO);
        itemsRecFromGold2Item.add(25, ItemName.XICH_NHAN_NGAN_LANG);
        itemsRecFromGold2Item.add(20, ItemName.HUYEN_TINH_NGOC);
        itemsRecFromGold2Item.add(20, ItemName.HUYET_NGOC);
        itemsRecFromGold2Item.add(20, ItemName.LAM_TINH_NGOC);
        itemsRecFromGold2Item.add(20, ItemName.LUC_NGOC);
        itemsRecFromGold2Item.add(25, ItemName.MAT_NA_SUPER_BROLY);
        itemsRecFromGold2Item.add(25, ItemName.MAT_NA_ONNA_BUGEISHA);
        itemsRecFromGold2Item.add(35, ItemName.BANH_RANG);
        itemsRecFromGold2Item.add(50, ItemName.DA_CAP_8);
        itemsRecFromGold2Item.add(40, ItemName.DA_CAP_9);
        itemsRecFromGold2Item.add(20, ItemName.DA_CAP_10);
        itemsRecFromGold2Item.add(40, ItemName.LONG_DEN_TRON);
        itemsRecFromGold2Item.add(40, ItemName.LONG_DEN_NGOI_SAO);
        itemsRecFromGold2Item.add(40, ItemName.LONG_DEN_MAT_TRANG);
        itemsRecFromGold2Item.add(40, ItemName.LONG_DEN_CA_CHEP);
        itemsRecFromGold2Item.add(40, ItemName.MINH_MAN_DAN);
        itemsRecFromGold2Item.add(40, ItemName.LONG_LUC_DAN);
        itemsRecFromGold2Item.add(40, ItemName.KHANG_THE_DAN);
        itemsRecFromGold2Item.add(40, ItemName.SINH_MENH_DAN);
        itemsRecFromGold2Item.add(20, ItemName.DA_DANH_VONG_CAP_1);
        itemsRecFromGold2Item.add(15, ItemName.DA_DANH_VONG_CAP_2);
        itemsRecFromGold2Item.add(10, ItemName.DA_DANH_VONG_CAP_3);
        itemsRecFromGold2Item.add(8, ItemName.DA_DANH_VONG_CAP_4);
        itemsRecFromGold2Item.add(5, ItemName.DA_DANH_VONG_CAP_5);
        itemsRecFromGold2Item.add(20, ItemName.VIEN_LINH_HON_CAP_1);
        itemsRecFromGold2Item.add(15, ItemName.VIEN_LINH_HON_CAP_2);
        itemsRecFromGold2Item.add(8, ItemName.VIEN_LINH_HON_CAP_3);
        itemsRecFromGold2Item.add(4, ItemName.VIEN_LINH_HON_CAP_4);
        itemsRecFromGold2Item.add(2, ItemName.VIEN_LINH_HON_CAP_5);
        itemsRecFromGold2Item.add(5, ItemName.BI_KIP_CUNG);
        itemsRecFromGold2Item.add(5, ItemName.BI_KIP_DAO);
        itemsRecFromGold2Item.add(5, ItemName.BI_KIP_KIEM_THUAT);
        itemsRecFromGold2Item.add(5, ItemName.BI_KIP_KUNAI);
        itemsRecFromGold2Item.add(5, ItemName.BI_KIP_QUAT);
        itemsRecFromGold2Item.add(5, ItemName.BI_KIP_TIEU_THUAT);
        itemsRecFromGold2Item.add(10, ItemName.GIAY_RACH);
        itemsRecFromGold2Item.add(6, ItemName.GIAY_BAC);
        itemsRecFromGold2Item.add(3, ItemName.GIAY_VANG);
        itemsRecFromGold2Item.add(3, ItemName.HAGGIS);
        itemsRecFromGold2Item.add(10, ItemName.HAC_NGUU);
        itemsRecFromGold2Item.add(5, ItemName.LAN_SU_VU);
        itemsRecFromGold2Item.add(20, ItemName.THONG_LINH_THAO);
        itemsRecFromGold2Item.add(10, ItemName.MANH_NON_JIRAI_);
        itemsRecFromGold2Item.add(10, ItemName.MANH_AO_JIRAI_);
        itemsRecFromGold2Item.add(10, ItemName.MANH_QUAN_JIRAI_);
        itemsRecFromGold2Item.add(10, ItemName.MANH_GANG_TAY_JIRAI_);
        itemsRecFromGold2Item.add(10, ItemName.MANH_GIAY_JIRAI_);
        itemsRecFromGold2Item.add(10, ItemName.MANH_PHU_JIRAI_);
        itemsRecFromGold2Item.add(10, ItemName.MANH_DAY_CHUYEN_JIRAI_);
        itemsRecFromGold2Item.add(10, ItemName.MANH_NGOC_BOI_JIRAI_);
        itemsRecFromGold2Item.add(10, ItemName.MANH_NHAN_JIRAI_);
        itemsRecFromGold2Item.add(10, ItemName.MANH_NON_JUMITO);
        itemsRecFromGold2Item.add(10, ItemName.MANH_AO_JUMITO);
        itemsRecFromGold2Item.add(10, ItemName.MANH_QUAN_JUMITO);
        itemsRecFromGold2Item.add(10, ItemName.MANH_GANG_TAY_JUMITO);
        itemsRecFromGold2Item.add(10, ItemName.MANH_GIAY_JUMITO);
        itemsRecFromGold2Item.add(10, ItemName.MANH_PHU_JUMITO);
        itemsRecFromGold2Item.add(10, ItemName.MANH_DAY_CHUYEN_JUMITO);
        itemsRecFromGold2Item.add(10, ItemName.MANH_NGOC_BOI_JUMITO);
        itemsRecFromGold2Item.add(10, ItemName.MANH_NHAN_JUMITO);
    }

    void close() {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from 
    }
}
