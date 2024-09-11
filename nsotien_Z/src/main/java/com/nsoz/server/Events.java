package com.nsoz.server;

import com.nsoz.map.MapManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Comparator;
import java.util.Vector;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.nsoz.item.ItemManager;
import com.nsoz.model.Char;
import com.nsoz.item.Item;
import com.nsoz.constants.ItemName;
import com.nsoz.map.Map;
import com.nsoz.mob.Mob;
import com.nsoz.model.RandomItem;
import com.nsoz.map.zones.Zone;
import com.nsoz.mob.MobTemplate;
import com.nsoz.constants.NpcName;
import com.nsoz.db.jdbc.DbManager;
import com.nsoz.event.eventpoint.EventPoint;
import com.nsoz.item.ItemFactory;
import com.nsoz.util.Log;
import com.nsoz.util.NinjaUtils;
import com.nsoz.lib.RandomCollection;
import com.nsoz.mob.MobManager;
import java.util.Calendar;
import java.util.List;

public class Events {

    public static final byte OFF = 0;
    public static final byte TRUNG_THU = 1;
    public static final byte NOEL = 3;
    public static final byte TET = 4;
    public static final byte WOMAN_DAY = 5;
    public static final byte HUNG_KING = 6;
    public static final byte SEA_GAME = 7;
    public static final byte SUMMER = 8;

    public static final byte TOP_LONG_DEN = 0;

    public static final byte TOP_QTT = 0;
    public static final byte TOP_TUAN_LOC = 1;

    public static final byte TOP_BMM = 0;
    public static final byte TOP_CHUOT = 1;

    public static final byte TOP_XAY_THANH = 0;

    public static final byte TOP_FISHING = 0;
    public static final byte TOP_ICE_CREAM = 1;

    private static Vector<ListLeaderBoard> listLeaderBoard = new Vector<>();
    public static RandomCollection<Integer> ITEM_EVENT;
    public static int event = OFF;

    static {
        switch (event) {
            case TRUNG_THU:
                ITEM_EVENT = RandomItem.TRUNG_THU;
                break;

            case NOEL:
                ITEM_EVENT = RandomItem.NOEL;
                break;

            case TET:
                ITEM_EVENT = RandomItem.TET;
                break;

            case WOMAN_DAY:
                ITEM_EVENT = RandomItem.WOMAN_DAY;
                break;

            case HUNG_KING:
                ITEM_EVENT = RandomItem.HUNG_KING;
                break;

            case SEA_GAME:
                ITEM_EVENT = RandomItem.SEA_GAME;
                break;

            case SUMMER:
                ITEM_EVENT = RandomItem.SUMMER;
                break;
        }
    }

    public static void loadListLeaderBoard() {
        try {
            Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
            for (int i = 0; i < 3; i++) {
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT `name`, CAST(JSON_EXTRACT(event_point, \"$[" + i
                        + "]\") AS INT) AS `eventPoint` FROM players ORDER BY `eventPoint` DESC LIMIT 20;");
                ResultSet res = stmt.executeQuery();

                ListLeaderBoard newList = new ListLeaderBoard(i);
                listLeaderBoard.add(newList);

                while (res.next()) {
                    String name = res.getString("name");
                    int point = res.getInt("eventPoint");
                    newList.leaders.add(new LeaderBoard(name, point));
                }
                res.close();
                stmt.close();

                newList.sortAndGetLowestScore();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateLeaderBoard(Char _char, int type) {
        ListLeaderBoard list = listLeaderBoard.get(type);
        if (list != null) {
            list.updateLeaderBoard(_char);
        }
    }

    public static void showLeaderBoard(Char _char, int type, String format) {
        ListLeaderBoard list = listLeaderBoard.get(type);
        if (list != null) {
            list.showLeaderBoard(_char, format);
        }
    }

    public static void boHoa(Char p, int number, int itemID) {
        if (number < 1) {
            p.getService().npcChat(NpcName.TIEN_NU, "Số lượng tối thiểu là 1 cái.");
            return;
        }
        if (number > 1000) {
            p.getService().npcChat(NpcName.TIEN_NU, "Chỉ có thể làm tối đa 1.000 cái.");
            return;
        }
        String nameHoaHong = new String[]{"Hoa hồng đỏ", "Hoa hồng vàng", "Hoa hồng xanh"}[itemID - 386];
        int giayMau = 2 * number;
        int ruyBang = 1 * number;
        int hoaHong = 8 * number;
        int indexGiayMau = p.getIndexItemByIdInBag(ItemName.GIAY_MAU);
        int indexRuyBang = p.getIndexItemByIdInBag(ItemName.RUY_BANG);
        int indexHoaHong = p.getIndexItemByIdInBag(itemID);
        if (indexGiayMau == -1 || p.bag[indexGiayMau] == null || p.bag[indexGiayMau].getQuantity() < giayMau) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Giấy màu");
            return;
        }
        if (indexRuyBang == -1 || p.bag[indexRuyBang] == null || p.bag[indexRuyBang].getQuantity() < ruyBang) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Ruy băng");
            return;
        }
        if (indexHoaHong == -1 || p.bag[indexHoaHong] == null || p.bag[indexHoaHong].getQuantity() < hoaHong) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ " + nameHoaHong);
            return;
        }
        if (p.getSlotNull() == 0) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return;
        }
        p.removeItem(indexGiayMau, giayMau, true);
        p.removeItem(indexRuyBang, ruyBang, true);
        p.removeItem(indexHoaHong, hoaHong, true);
        Item item = ItemFactory.getInstance().newItem(itemID + 3);
        item.isLock = false;
        item.setQuantity(number);
        p.addItemToBag(item);
    }

    public static void gioHoa(Char p, int number) {
        if (number < 1) {
            p.getService().npcChat(NpcName.TIEN_NU, "Số lượng tối thiểu là 1 cái.");
            return;
        }
        if (number > 1000) {
            p.getService().npcChat(NpcName.TIEN_NU, "Chỉ có thể làm tối đa 1.000 cái.");
            return;
        }
        int hoaHongDo = 8 * number;
        int hoaHongVang = 8 * number;
        int hoaHongXanh = 8 * number;
        int giayMau = 1 * number;
        int ruyBang = 1 * number;
        int khungTre = 1 * number;
        int indexHoaHongDo = p.getIndexItemByIdInBag(386);
        int indexHoaHongVang = p.getIndexItemByIdInBag(387);
        int indexHoaHongXanh = p.getIndexItemByIdInBag(388);
        int indexGiayMau = p.getIndexItemByIdInBag(393);
        int indexRuyBang = p.getIndexItemByIdInBag(394);
        int indexKhungTre = p.getIndexItemByIdInBag(395);
        if (indexHoaHongDo == -1 || p.bag[indexHoaHongDo] == null
                || p.bag[indexHoaHongDo].getQuantity() < hoaHongDo) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Hoa hồng đỏ");
            return;
        }
        if (indexHoaHongVang == -1 || p.bag[indexHoaHongVang] == null
                || p.bag[indexHoaHongVang].getQuantity() < hoaHongVang) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Hoa hồng vàng");
            return;
        }
        if (indexGiayMau == -1 || p.bag[indexGiayMau] == null || p.bag[indexGiayMau].getQuantity() < giayMau) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Giấy màu");
            return;
        }
        if (indexHoaHongXanh == -1 || p.bag[indexHoaHongXanh] == null
                || p.bag[indexHoaHongXanh].getQuantity() < hoaHongXanh) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Hoa hồng xanh");
            return;
        }
        if (indexRuyBang == -1 || p.bag[indexRuyBang] == null || p.bag[indexRuyBang].getQuantity() < ruyBang) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Ruy băng");
            return;
        }
        if (indexKhungTre == -1 || p.bag[indexKhungTre] == null || p.bag[indexKhungTre].getQuantity() < khungTre) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Khung tre");
            return;
        }
        if (p.getSlotNull() == 0) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return;
        }
        p.removeItem(indexHoaHongDo, hoaHongDo, true);
        p.removeItem(indexHoaHongVang, hoaHongVang, true);
        p.removeItem(indexGiayMau, giayMau, true);
        p.removeItem(indexHoaHongXanh, hoaHongXanh, true);
        p.removeItem(indexRuyBang, ruyBang, true);
        p.removeItem(indexKhungTre, khungTre, true);
        Item item = ItemFactory.getInstance().newItem(ItemName.GIO_HOA_8_3);
        item.isLock = false;
        item.setQuantity(number);
        p.addItemToBag(item);
    }

    public static void tangHoa(Char _char, String name, int itemID) {
        String[] arrName = {"Bó hoa hồng đỏ", "Bó hoa hồng vàng", "Bó hoa hồng xanh", "Giỏ hoa"};
        String itemName = arrName[itemID - 389];
        int index = _char.getIndexItemByIdInBag(itemID);
        if (index != -1) {
            Char _c = ServerManager.findCharByName(name);

            if (_char == _c) {
                _char.serverMessage("Bạn không thể lì xì cho chính bạn!");
                return;
            }

            if (_c == null) {
                _char.getService().npcChat(NpcName.TIEN_NU, "Người này không online");
                return;
            }

            if (_char.level < 20) {
                _char.getService().npcChat(NpcName.TIEN_NU, "Bạn cần đạt cấp 20");
                return;
            }

            if (!_c.notReceivedExp) {
                _c.addExp(5000000);
            }
            if (!_char.notReceivedExp) {
                _char.addExp(5000000);
            }
            if (!_c.duatop) {
                _c.addExp(5000000);
            }
            if (!_char.duatop) {
                _char.addExp(5000000);
            }
            _char.removeItem(index, 1, true);
            if ((itemID == ItemName.GIO_HOA_8_3 && NinjaUtils.nextBoolean())
                    || (itemID == ItemName.BO_HOA_HONG_XANH && (int)NinjaUtils.nextInt(10) == 0)) {
                RandomCollection<Integer> rc = RandomItem.BANH_KHUC_CAY_CHOCOLATE;
                int itemId = rc.next();
                Item itm = ItemFactory.getInstance().newItem(itemId);
                itm.initExpire();
                _char.addItemToBag(itm);
            }
        } else {
            _char.getService().npcChat(NpcName.TIEN_NU, "Không đủ " + itemName);
        }
    }

    public static void chocolate(Char p, int number) {
        if (number < 1) {
            p.getService().npcChat(NpcName.TIEN_NU, "Số lượng tối thiểu là 1 cái.");
            return;
        }
        if (number > 1000) {
            p.getService().npcChat(NpcName.TIEN_NU, "Chỉ có thể làm tối đa 1.000 cái.");
            return;
        }
        int bo = 5 * number;
        int kem = 5 * number;
        int chocolate = 1 * number;
        int duongBot = 5 * number;
        int indexBo = p.getIndexItemByIdInBag(666);
        int indexKem = p.getIndexItemByIdInBag(667);
        int indexChocolate = p.getIndexItemByIdInBag(669);
        int indexDuongBot = p.getIndexItemByIdInBag(668);
        if (indexBo == -1 || p.bag[indexBo] == null || p.bag[indexBo].getQuantity() < bo) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Bơ");
            return;
        }
        if (indexKem == -1 || p.bag[indexKem] == null || p.bag[indexKem].getQuantity() < kem) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Kem");
            return;
        }
        if (indexDuongBot == -1 || p.bag[indexDuongBot] == null || p.bag[indexDuongBot].getQuantity() < duongBot) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Đường bột");
            return;
        }
        if (indexChocolate == -1 || p.bag[indexChocolate] == null
                || p.bag[indexChocolate].getQuantity() < chocolate) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Chocolate");
            return;
        }
        if (p.getSlotNull() == 0) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return;
        }
        p.removeItem(indexBo, bo, true);
        p.removeItem(indexKem, kem, true);
        p.removeItem(indexDuongBot, duongBot, true);
        p.removeItem(indexChocolate, chocolate, true);
        Item item = ItemFactory.getInstance().newItem(671);
        item.isLock = false;
        item.setQuantity(number);
        p.addItemToBag(item);
    }

    public static void dauTay(Char p, int number) {
        if (number < 1) {
            p.getService().npcChat(NpcName.TIEN_NU, "Số lượng tối thiểu là 1 cái.");
            return;
        }
        if (number > 1000) {
            p.getService().npcChat(NpcName.TIEN_NU, "Chỉ có thể làm tối đa 1.000 cái.");
            return;
        }
        int bo = 5 * number;
        int kem = 5 * number;
        int dauTay = 2 * number;
        int duongBot = 5 * number;
        int indexBo = p.getIndexItemByIdInBag(666);
        int indexKem = p.getIndexItemByIdInBag(667);
        int indexDauTay = p.getIndexItemByIdInBag(670);
        int indexDuongBot = p.getIndexItemByIdInBag(668);
        if (indexBo == -1 || p.bag[indexBo] == null || p.bag[indexBo].getQuantity() < bo) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Bơ");
            return;
        }
        if (indexKem == -1 || p.bag[indexKem] == null || p.bag[indexKem].getQuantity() < kem) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Kem");
            return;
        }
        if (indexDuongBot == -1 || p.bag[indexDuongBot] == null || p.bag[indexDuongBot].getQuantity() < duongBot) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Đường bột");
            return;
        }
        if (indexDauTay == -1 || p.bag[indexDauTay] == null || p.bag[indexDauTay].getQuantity() < dauTay) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Dâu Tây");
            return;
        }
        if (p.getSlotNull() == 0) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return;
        }
        p.removeItem(indexBo, bo, true);
        p.removeItem(indexKem, kem, true);
        p.removeItem(indexDuongBot, duongBot, true);
        p.removeItem(indexDauTay, dauTay, true);
        Item item = ItemFactory.getInstance().newItem(672);
        item.isLock = false;
        item.setQuantity(number);
        p.addItemToBag(item);
    }

    public static void banhChung(Char p, int number) {
        if (number < 1) {
            p.getService().npcChat(NpcName.TIEN_NU, "Số lượng tối thiểu là 1 cái.");
            return;
        }
        if (number > 1000) {
            p.getService().npcChat(NpcName.TIEN_NU, "Chỉ có thể làm tối đa 1.000 cái.");
            return;
        }
        int nep = 5 * number;
        int laDong = 3 * number;
        int dauXanh = 3 * number;
        int latTre = 2 * number;
        int thitHeo = 1 * number;
        int indexNep = p.getIndexItemByIdInBag(ItemName.NEP);
        int indexLaDong = p.getIndexItemByIdInBag(ItemName.LA_DONG);
        int indexDauXanh = p.getIndexItemByIdInBag(ItemName.DAU_XANH2);
        int indexLatTre = p.getIndexItemByIdInBag(ItemName.LAT_TRE);
        int indexThitHeo = p.getIndexItemByIdInBag(ItemName.THIT_HEO);
        
        if (indexNep == -1 || p.bag[indexNep] == null || p.bag[indexNep].getQuantity() < nep) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Nếp");
            return;
        }
        if (indexLaDong == -1 || p.bag[indexLaDong] == null || p.bag[indexLaDong].getQuantity() < laDong) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Lá Dong");
            return;
        }
        if (indexDauXanh == -1 || p.bag[indexDauXanh] == null || p.bag[indexDauXanh].getQuantity() < dauXanh) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Đậu Xanh");
            return;
        }
        if (indexLatTre == -1 || p.bag[indexLatTre] == null || p.bag[indexLatTre].getQuantity() < latTre) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Đậu Xanh");
            return;
        }
        if (indexThitHeo == -1 || p.bag[indexThitHeo] == null || p.bag[indexThitHeo].getQuantity() < thitHeo) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Thịt Heo");
            return;
        }
        if (p.getSlotNull() == 0) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return;
        }
        p.removeItem(indexNep, nep, true);
        p.removeItem(indexLaDong, laDong, true);
        p.removeItem(indexDauXanh, dauXanh, true);
        p.removeItem(indexLatTre, latTre, true);
        p.removeItem(indexThitHeo, thitHeo, true);
        Item item = ItemFactory.getInstance().newItem(ItemName.BANH_CHUNG);
        item.isLock = false;
        item.setQuantity(number);
        p.addItemToBag(item);
    }

    public static void banhTet(Char p, int number) {
        if (number < 1) {
            p.getService().npcChat(NpcName.TIEN_NU, "Số lượng tối thiểu là 1 cái.");
            return;
        }
        if (number > 1000) {
            p.getService().npcChat(NpcName.TIEN_NU, "Chỉ có thể làm tối đa 1.000 cái.");
            return;
        }
        int nep = 4 * number;
        int laDong = 2 * number;
        int dauXanh = 2 * number;
        int latTre = 4 * number;
        int coin = 100000 * number;
        int indexNep = p.getIndexItemByIdInBag(ItemName.NEP);
        int indexLaDong = p.getIndexItemByIdInBag(ItemName.LA_DONG);
        int indexDauXanh = p.getIndexItemByIdInBag(ItemName.DAU_XANH2);
        int indexLatTre = p.getIndexItemByIdInBag(ItemName.LAT_TRE);
        if (indexNep == -1 || p.bag[indexNep] == null || p.bag[indexNep].getQuantity() < nep) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Nếp");
            return;
        }
        if (indexLaDong == -1 || p.bag[indexLaDong] == null || p.bag[indexLaDong].getQuantity() < laDong) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Lá Dong");
            return;
        }
        if (indexDauXanh == -1 || p.bag[indexDauXanh] == null || p.bag[indexDauXanh].getQuantity() < dauXanh) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Đậu Xanh");
            return;
        }
        if (indexLatTre == -1 || p.bag[indexLatTre] == null || p.bag[indexLatTre].getQuantity() < latTre) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Đậu Xanh");
            return;
        }
        if (p.coin < coin) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ xu");
            return;
        }
        if (p.getSlotNull() == 0) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return;
        }
        p.removeItem(indexNep, nep, true);
        p.removeItem(indexLaDong, laDong, true);
        p.removeItem(indexDauXanh, dauXanh, true);
        p.removeItem(indexLatTre, latTre, true);
        p.addCoin(-coin);
        Item item = ItemFactory.getInstance().newItem(ItemName.BANH_TET);
        item.isLock = false;
        item.setQuantity(number);
        p.addItemToBag(item);
    }

    public static void dayPhao(Char p, int number) {
        if (number < 1) {
            p.getService().npcChat(NpcName.TIEN_NU, "Số lượng tối thiểu là 1 cái.");
            return;
        }
        if (number > 1000) {
            p.getService().npcChat(NpcName.TIEN_NU, "Chỉ có thể làm tối đa 1.000 cái.");
            return;
        }
        int manhPhaoHoa = 10 * number;
        int indexManhPhao = p.getIndexItemByIdInBag(ItemName.MANH_PHAO_HOA);

        if (indexManhPhao == -1 || p.bag[indexManhPhao] == null
                || p.bag[indexManhPhao].getQuantity() < manhPhaoHoa) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ mảnh pháo hoa");
            return;
        }
        if (p.getSlotNull() == 0) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return;
        }
        p.removeItem(indexManhPhao, manhPhaoHoa, true);
        Item item = ItemFactory.getInstance().newItem(ItemName.DAY_PHAO_HOA);
        item.isLock = false;
        item.setQuantity(number);
        p.addItemToBag(item);
    }

    public static void liXi(Char _char, String content) {

        if (_char.level < 20) {
            _char.getService().npcChat(NpcName.TIEN_NU, "Bạn cần đạt cấp 20");
            return;
        }

        if (content.equals("")) {
            _char.getService().npcChat(NpcName.TIEN_NU, "Người này không online hoặc không tồn tại!");
            return;
        }

        Char receiver = Char.findCharByName(content);

        if (receiver == null) {
            _char.serverMessage("Người này không online hoặc không tồn tại!");
            return;
        }

        if (_char == receiver) {
            _char.serverMessage("Bạn không thể lì xì cho chính bạn!");
            return;
        }

        if (_char.user.gold < 25) {
            _char.serverMessage("Bạn cần tối thiểu 25 lượng");
            return;
        }

        _char.addGold(-25);

        if (_char.getSlotNull() == 0) {
            _char.warningBagFull();
            return;
        }
        int[] exps = {8000000, 16000000, 25000000};
        int exp = exps[NinjaUtils.randomWithRate(new int[]{50, 30, 20})];
        if(!_char.duatop){
            _char.addExp(exp);
        }

        if (NinjaUtils.nextBoolean()) {
            RandomCollection<Integer> rc = RandomItem.BANH_CHUNG;
            int itemId = rc.next();
            Item itm = ItemFactory.getInstance().newItem(itemId);
            itm.initExpire();

            if (itm.id == ItemName.THONG_LINH_THAO) {
                itm.setQuantity((int)(int)NinjaUtils.nextInt(5, 10));
            }

            if (itemId == ItemName.BAT_BAO || itemId == ItemName.RUONG_BACH_NGAN || itemId == ItemName.RUONG_HUYEN_BI) {
                GlobalService.getInstance().chat("Hệ thống", _char.name + " lì xì nhận được " + itm.template.name);
            }

            _char.addItemToBag(itm);
        }

        int yen = (int)NinjaUtils.nextInt(50000, 200000);
        receiver.addYen(yen);
        receiver.serverMessage("Bạn được " + _char.name + " lì xì " + NinjaUtils.getCurrency(yen) + " yên");
    }

    public static void matNaHo(Char p) {
        int indexBanhTet = p.getIndexItemByIdInBag(ItemName.BANH_TET);
        if (indexBanhTet == -1 || p.bag[indexBanhTet] == null || p.bag[indexBanhTet].getQuantity() < 1) {
            p.getService().npcChat(NpcName.TIEN_NU, "Ngươi cần có đủ 20 chiếc bánh tét ");
            return;
        }
        if (p.getSlotNull() == 0) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return;
        }
        if (p.user.gold < 500) {
            p.getService().npcChat(NpcName.TIEN_NU, "Ngươi phải có đủ 500 lượng.");
            return;
        }

        p.removeItem(indexBanhTet, 20, true);
        p.addGold(-500);
        Item item = ItemFactory.getInstance().newItem(ItemName.MAT_NA_HO);
        item.isLock = false;
        item.expire = System.currentTimeMillis() + 1296000000L;

        item.randomOptionTigerMask();

        p.addItemToBag(item);
    }

    public static void hopQua(Char p, int number) {
        if (number < 1) {
            p.getService().npcChat(NpcName.TIEN_NU, "Số lượng tối thiểu là 1 cái.");
            return;
        }
        if (number > 1000) {
            p.getService().npcChat(NpcName.TIEN_NU, "Chỉ có thể làm tối đa 1.000 cái.");
            return;
        }
        int gold = 10 * number;
        int traiChau = 3 * number;
        int duyBang = 3 * number;
        int chuongVang = 3 * number;
        int indexTraiChau = p.getIndexItemByIdInBag(481);
        int indexDuyBang = p.getIndexItemByIdInBag(482);
        int indexChuongVang = p.getIndexItemByIdInBag(831);
        if (indexTraiChau == -1 || p.bag[indexTraiChau] == null || p.bag[indexTraiChau].getQuantity() < traiChau) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Trái châu");
            return;
        }
        if (indexDuyBang == -1 || p.bag[indexDuyBang] == null || p.bag[indexDuyBang].getQuantity() < duyBang) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Duy băng");
            return;
        }
        if (indexChuongVang == -1 || p.bag[indexChuongVang] == null
                || p.bag[indexChuongVang].getQuantity() < chuongVang) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Chuông vàng");
            return;
        }
        if (gold > p.user.gold) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ lượng.");
            return;
        }
        if (p.getSlotNull() == 0) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return;
        }
        p.removeItem(indexChuongVang, chuongVang, true);
        p.removeItem(indexDuyBang, duyBang, true);
        p.removeItem(indexTraiChau, traiChau, true);
        p.addGold(-gold);
        Item item = ItemFactory.getInstance().newItem(832);
        item.isLock = false;
        item.setQuantity(number);
        p.addItemToBag(item);
    }

    public static void banhThapCam(Char p, int number) {
        if (number < 1) {
            p.getService().npcChat(NpcName.TIEN_NU, "Số lượng tối thiểu là 1 cái.");
            return;
        }
        if (number > 1000) {
            p.getService().npcChat(NpcName.TIEN_NU, "Chỉ có thể làm tối đa 1.000 cái.");
            return;
        }
        int yen = 10000 * number;
        int botMi = 20 * number;
        int trung = 10 * number;
        int hatSen = 10 * number;
        int duong = 10 * number;
        int mut = 10 * number;
        int indexBotMi = p.getIndexItemByIdInBag(292);
        int indexTrung = p.getIndexItemByIdInBag(293);
        int indexHatSen = p.getIndexItemByIdInBag(295);
        int indexDuong = p.getIndexItemByIdInBag(294);
        int indexMut = p.getIndexItemByIdInBag(297);
        if (p.yen < yen) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ yên");
            return;
        }
        if (indexBotMi == -1 || p.bag[indexBotMi] == null || p.bag[indexBotMi].getQuantity() < botMi) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Bột mì");
            return;
        }
        if (indexTrung == -1 || p.bag[indexTrung] == null || p.bag[indexTrung].getQuantity() < trung) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Trứng");
            return;
        }
        if (indexDuong == -1 || p.bag[indexDuong] == null || p.bag[indexDuong].getQuantity() < duong) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Đường");
            return;
        }
        if (indexHatSen == -1 || p.bag[indexHatSen] == null || p.bag[indexHatSen].getQuantity() < hatSen) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Hạt sen");
            return;
        }
        if (indexMut == -1 || p.bag[indexMut] == null || p.bag[indexMut].getQuantity() < mut) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Mứt");
            return;
        }
        if (p.getSlotNull() == 0) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return;
        }
        p.removeItem(indexBotMi, botMi, true);
        p.removeItem(indexTrung, trung, true);
        p.removeItem(indexDuong, duong, true);
        p.removeItem(indexHatSen, hatSen, true);
        p.removeItem(indexMut, mut, true);
        p.addYen(-yen);
        Item item = ItemFactory.getInstance().newItem(298);
        item.isLock = false;
        item.setQuantity(number);
        p.addItemToBag(item);
    }

    public static void banhDeo(Char p, int number) {
        if (number < 1) {
            p.getService().npcChat(NpcName.TIEN_NU, "Số lượng tối thiểu là 1 cái.");
            return;
        }
        if (number > 1000) {
            p.getService().npcChat(NpcName.TIEN_NU, "Chỉ có thể làm tối đa 1.000 cái.");
            return;
        }
        int yen = 10000 * number;
        int botMi = 20 * number;
        int hatSen = 10 * number;
        int duong = 10 * number;
        int mut = 10 * number;
        int indexBotMi = p.getIndexItemByIdInBag(292);
        int indexHatSen = p.getIndexItemByIdInBag(295);
        int indexDuong = p.getIndexItemByIdInBag(294);
        int indexMut = p.getIndexItemByIdInBag(297);
        if (p.yen < yen) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ yên");
            return;
        }
        if (indexBotMi == -1 || p.bag[indexBotMi] == null || p.bag[indexBotMi].getQuantity() < botMi) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Bột mì");
            return;
        }
        if (indexDuong == -1 || p.bag[indexDuong] == null || p.bag[indexDuong].getQuantity() < duong) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Đường");
            return;
        }
        if (indexHatSen == -1 || p.bag[indexHatSen] == null || p.bag[indexHatSen].getQuantity() < hatSen) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Hạt sen");
            return;
        }
        if (indexMut == -1 || p.bag[indexMut] == null || p.bag[indexMut].getQuantity() < mut) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Mứt");
            return;
        }
        if (p.getSlotNull() == 0) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return;
        }
        p.removeItem(indexBotMi, botMi, true);
        p.removeItem(indexDuong, duong, true);
        p.removeItem(indexHatSen, hatSen, true);
        p.removeItem(indexMut, mut, true);
        p.addYen(-yen);
        Item item = ItemFactory.getInstance().newItem(299);
        item.isLock = false;
        item.setQuantity(number);
        p.addItemToBag(item);
    }

    public static void banhDauXanh(Char p, int number) {
        if (number < 1) {
            p.getService().npcChat(NpcName.TIEN_NU, "Số lượng tối thiểu là 1 cái.");
            return;
        }
        if (number > 1000) {
            p.getService().npcChat(NpcName.TIEN_NU, "Chỉ có thể làm tối đa 1.000 cái.");
            return;
        }
        int yen = 10000 * number;
        int botMi = 20 * number;
        int trung = 10 * number;
        int duong = 10 * number;
        int dauXanh = 10 * number;
        int indexBotMi = p.getIndexItemByIdInBag(292);
        int indexTrung = p.getIndexItemByIdInBag(293);
        int indexDuong = p.getIndexItemByIdInBag(294);
        int indexDauXanh = p.getIndexItemByIdInBag(296);
        if (p.yen < yen) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ yên");
            return;
        }
        if (indexBotMi == -1 || p.bag[indexBotMi] == null || p.bag[indexBotMi].getQuantity() < botMi) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Bột mì");
            return;
        }
        if (indexTrung == -1 || p.bag[indexTrung] == null || p.bag[indexTrung].getQuantity() < trung) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Trứng");
            return;
        }
        if (indexDuong == -1 || p.bag[indexDuong] == null || p.bag[indexDuong].getQuantity() < duong) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Đường");
            return;
        }
        if (indexDauXanh == -1 || p.bag[indexDauXanh] == null || p.bag[indexDauXanh].getQuantity() < dauXanh) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Đậu xanh");
            return;
        }
        if (p.getSlotNull() == 0) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return;
        }
        p.removeItem(indexBotMi, botMi, true);
        p.removeItem(indexTrung, trung, true);
        p.removeItem(indexDuong, duong, true);
        p.removeItem(indexDauXanh, dauXanh, true);
        p.addYen(-yen);
        Item item = ItemFactory.getInstance().newItem(300);
        item.isLock = false;
        item.setQuantity(number);
        p.addItemToBag(item);
    }

    public static void banhPia(Char p, int number) {
        if (number < 1) {
            p.getService().npcChat(NpcName.TIEN_NU, "Số lượng tối thiểu là 1 cái.");
            return;
        }
        if (number > 1000) {
            p.getService().npcChat(NpcName.TIEN_NU, "Chỉ có thể làm tối đa 1.000 cái.");
            return;
        }
        int yen = 10000 * number;
        int botMi = 20 * number;
        int trung = 10 * number;
        int duong = 10 * number;
        int dauXanh = 10 * number;
        int indexBotMi = p.getIndexItemByIdInBag(292);
        int indexTrung = p.getIndexItemByIdInBag(293);
        int indexDuong = p.getIndexItemByIdInBag(294);
        int indexDauXanh = p.getIndexItemByIdInBag(296);
        if (p.yen < yen) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ yên");
            return;
        }
        if (indexBotMi == -1 || p.bag[indexBotMi] == null || p.bag[indexBotMi].getQuantity() < botMi) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Bột mì");
            return;
        }
        if (indexTrung == -1 || p.bag[indexTrung] == null || p.bag[indexTrung].getQuantity() < trung) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Trứng");
            return;
        }
        if (indexDuong == -1 || p.bag[indexDuong] == null || p.bag[indexDuong].getQuantity() < duong) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Đường");
            return;
        }
        if (indexDauXanh == -1 || p.bag[indexDauXanh] == null || p.bag[indexDauXanh].getQuantity() < dauXanh) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Đậu xanh");
            return;
        }
        if (p.getSlotNull() == 0) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return;
        }
        p.removeItem(indexBotMi, botMi, true);
        p.removeItem(indexTrung, trung, true);
        p.removeItem(indexDuong, duong, true);
        p.removeItem(indexDauXanh, dauXanh, true);
        p.addYen(-yen);
        Item item = ItemFactory.getInstance().newItem(301);
        item.isLock = false;
        item.setQuantity(number);
        p.addItemToBag(item);
    }

    public static void hopBanhThuong(Char p, int number) {
        if (number < 1) {
            p.getService().npcChat(NpcName.TIEN_NU, "Số lượng tối thiểu là 1 cái.");
            return;
        }
        if (number > 1000) {
            p.getService().npcChat(NpcName.TIEN_NU, "Chỉ có thể làm tối đa 1.000 cái.");
            return;
        }
        int banhThapCam = 1 * number;
        int banhDeo = 1 * number;
        int banhDauXanh = 1 * number;
        int banhPia = 1 * number;
        int giayGoiThuong = 1 * number;
        int indexBanhThapCam = p.getIndexItemByIdInBag(298);
        int indexBanhDeo = p.getIndexItemByIdInBag(299);
        int indexBanhDauXanh = p.getIndexItemByIdInBag(300);
        int indexBanhPia = p.getIndexItemByIdInBag(301);
        int indexGiayGoiThuong = p.getIndexItemByIdInBag(304);
        if (indexBanhThapCam == -1 || p.bag[indexBanhThapCam] == null
                || p.bag[indexBanhThapCam].getQuantity() < banhThapCam) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Bánh thập cẩm");
            return;
        }
        if (indexBanhDeo == -1 || p.bag[indexBanhDeo] == null || p.bag[indexBanhDeo].getQuantity() < banhDeo) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Bánh dẻo");
            return;
        }
        if (indexBanhPia == -1 || p.bag[indexBanhPia] == null || p.bag[indexBanhPia].getQuantity() < banhPia) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Bánh Pia");
            return;
        }
        if (indexBanhDauXanh == -1 || p.bag[indexBanhDauXanh] == null
                || p.bag[indexBanhDauXanh].getQuantity() < banhDauXanh) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Bánh đậu xanh");
            return;
        }
        if (indexGiayGoiThuong == -1 || p.bag[indexGiayGoiThuong] == null
                || p.bag[indexGiayGoiThuong].getQuantity() < giayGoiThuong) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Giấy gói thường");
            return;
        }
        if (p.getSlotNull() == 0) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return;
        }
        p.removeItem(indexBanhThapCam, banhThapCam, true);
        p.removeItem(indexBanhDeo, banhDeo, true);
        p.removeItem(indexBanhPia, banhPia, true);
        p.removeItem(indexBanhDauXanh, banhDauXanh, true);
        p.removeItem(indexGiayGoiThuong, giayGoiThuong, true);
        Item item = ItemFactory.getInstance().newItem(302);
        item.isLock = false;
        item.setQuantity(number);
        p.addItemToBag(item);
    }

    public static void hopBanhThuongHang(Char p, int number) {
        if (number < 1) {
            p.getService().npcChat(NpcName.TIEN_NU, "Số lượng tối thiểu là 1 cái.");
            return;
        }
        if (number > 1000) {
            p.getService().npcChat(NpcName.TIEN_NU, "Chỉ có thể làm tối đa 1.000 cái.");
            return;
        }
        int banhThapCam = 1 * number;
        int banhDeo = 1 * number;
        int banhDauXanh = 1 * number;
        int banhPia = 1 * number;
        int giayGoiCaoCap = 1 * number;
        int indexBanhThapCam = p.getIndexItemByIdInBag(298);
        int indexBanhDeo = p.getIndexItemByIdInBag(299);
        int indexBanhDauXanh = p.getIndexItemByIdInBag(300);
        int indexBanhPia = p.getIndexItemByIdInBag(301);
        int indexGiayGoiCaoCap = p.getIndexItemByIdInBag(305);
        if (indexBanhThapCam == -1 || p.bag[indexBanhThapCam] == null
                || p.bag[indexBanhThapCam].getQuantity() < banhThapCam) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Bánh thập cẩm");
            return;
        }
        if (indexBanhDeo == -1 || p.bag[indexBanhDeo] == null || p.bag[indexBanhDeo].getQuantity() < banhDeo) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Bánh dẻo");
            return;
        }
        if (indexBanhPia == -1 || p.bag[indexBanhPia] == null || p.bag[indexBanhPia].getQuantity() < banhPia) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Bánh Pia");
            return;
        }
        if (indexBanhDauXanh == -1 || p.bag[indexBanhDauXanh] == null
                || p.bag[indexBanhDauXanh].getQuantity() < banhDauXanh) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Bánh đậu xanh");
            return;
        }
        if (indexGiayGoiCaoCap == -1 || p.bag[indexGiayGoiCaoCap] == null
                || p.bag[indexGiayGoiCaoCap].getQuantity() < giayGoiCaoCap) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Giấy gói cao cấp");
            return;
        }
        if (p.getSlotNull() == 0) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return;
        }
        p.removeItem(indexBanhThapCam, banhThapCam, true);
        p.removeItem(indexBanhDeo, banhDeo, true);
        p.removeItem(indexBanhPia, banhPia, true);
        p.removeItem(indexBanhDauXanh, banhDauXanh, true);
        p.removeItem(indexGiayGoiCaoCap, giayGoiCaoCap, true);
        Item item = ItemFactory.getInstance().newItem(303);
        item.isLock = false;
        item.setQuantity(number);
        p.addItemToBag(item);
    }

    public static void treTramDot(Char p, int itemId, int number) {
        if (number < 1) {
            p.getService().npcChat(NpcName.TIEN_NU, "Số lượng tối thiểu là 1 cái.");
            return;
        }
        if (number > 1000) {
            p.getService().npcChat(NpcName.TIEN_NU, "Chỉ có thể làm tối đa 1.000 cái.");
            return;
        }
        int amount = 100 * number;
        int indexTre = p.getIndexItemByIdInBag(itemId);
        if (indexTre == -1 || p.bag[indexTre] == null || p.bag[indexTre].getQuantity() < amount) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ đốt tre");
            return;
        } else if (itemId == ItemName.DOT_TRE_XANH && p.coin < number * 100000) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ xu");
            return;
        } else if (itemId == ItemName.DOT_TRE_VANG && p.user.gold < number * 20) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ lượng");
            return;
        } else if (p.getSlotNull() == 0) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return;
        }

        if (ItemName.DOT_TRE_XANH == itemId) {
            p.addCoin(-number * 100000);
        } else if (ItemName.DOT_TRE_VANG == itemId) {
            p.addGold(-number * 20);
        }

        p.removeItem(indexTre, amount, true);
        Item item = ItemFactory.getInstance().newItem(itemId + 2);
        item.isLock = false;
        item.setQuantity(number);
        p.addItemToBag(item);
    }

    public static void tiepTeLuongThuc(Char p, int itemId, int number) {
        if (number < 1) {
            p.getService().npcChat(NpcName.TIEN_NU, "Số lượng tối thiểu là 1 cái.");
            return;
        }
        if (number > 1000) {
            p.getService().npcChat(NpcName.TIEN_NU, "Chỉ có thể làm tối đa 1.000 cái.");
            return;
        }
        int amount = 5 * number;
        int indexCaChua = p.getIndexItemByIdInBag(ItemName.CA_CHUA);
        int indexBapNgo = p.getIndexItemByIdInBag(ItemName.BAP_NGO);
        int indexTangThit = p.getIndexItemByIdInBag(ItemName.TANG_THIT);
        int indexKhucCa = p.getIndexItemByIdInBag(ItemName.KHUC_CA);
        if (itemId == ItemName.THAU_RAU_CU && (indexCaChua == -1 || p.bag[indexCaChua] == null || p.bag[indexCaChua].getQuantity() < amount)) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ cà chua");
            return;
        } else if (itemId == ItemName.THAU_RAU_CU && (indexBapNgo == -1 || p.bag[indexBapNgo] == null || p.bag[indexBapNgo].getQuantity() < amount)) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ bắp ngô");
            return;
        } else if (itemId == ItemName.THAU_THIT_CA && (indexTangThit == -1 || p.bag[indexTangThit] == null || p.bag[indexTangThit].getQuantity() < amount)) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ tảng thịt");
            return;
        } else if (itemId == ItemName.THAU_THIT_CA && (indexKhucCa == -1 || p.bag[indexKhucCa] == null || p.bag[indexKhucCa].getQuantity() < amount)) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ tảng thịt");
            return;
        } else if (itemId == ItemName.THAU_RAU_CU && p.coin < number * 100000) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ xu");
            return;
        } else if (itemId == ItemName.THAU_THIT_CA && p.user.gold < number * 20) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ lượng");
            return;
        } else if (p.getSlotNull() == 0) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return;
        }

        if (ItemName.THAU_RAU_CU == itemId) {
            p.addCoin(-number * 100000);
            p.removeItem(indexCaChua, amount, true);
            p.removeItem(indexBapNgo, amount, true);
        } else if (ItemName.THAU_THIT_CA == itemId) {
            p.addGold(-number * 20);
            p.removeItem(indexTangThit, amount, true);
            p.removeItem(indexKhucCa, amount, true);
        }

        Item item = ItemFactory.getInstance().newItem(itemId);
        item.isLock = false;
        item.setQuantity(number);
        p.addItemToBag(item);
    }

    public static void xayThanh(Char p, int number) {
        if (number < 1) {
            p.getService().npcChat(NpcName.TIEN_NU, "Số lượng tối thiểu là 1 cái.");
            return;
        }
        if (number > 1000) {
            p.getService().npcChat(NpcName.TIEN_NU, "Chỉ có thể góp tối đa 1.000 cái.");
            return;
        }

        int amount = 10 * number;
        int index1 = p.getIndexItemByIdInBag(ItemName.GO_LIM);
        if (index1 == -1 || p.bag[index1] == null || p.bag[index1].getQuantity() < amount) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ gỗ lim");
            return;
        } else if (p.user.gold < number * 20) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ lượng");
            return;
        } else if (p.getSlotNull() < number) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return;
        }

        p.addGold(-number * 20);
        p.removeItem(index1, amount, true);

        for (int i = 0; i < number; i++) {
            RandomCollection<Integer> rc = RandomItem.BUA_MAY_MAN;
            int itemId = rc.next();
            Item itm = ItemFactory.getInstance().newItem(itemId);
            itm.initExpire();

            if (itm.id == ItemName.THONG_LINH_THAO) {
                itm.setQuantity((int)NinjaUtils.nextInt(5, 10));
            }

            p.addItemToBag(itm);
        }

        //p.addEventPoint(amount, Events.TOP_XAY_THANH);
    }

    public static void doiThanhVat(Char p) {
        int point = 100000;
        if (p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI) < point) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ điểm tiêu xài.");
            return;
        }

        if (p.getSlotNull() == 0) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return;
        }

        Item item = ItemFactory.getInstance().newItem((int)NinjaUtils.nextInt(859, 863));
        p.addItemToBag(item);
        p.getEventPoint().subPoint(EventPoint.DIEM_TIEU_XAI, point);
    }

    public static boolean useEventItem(Char _char, int number, int[][] itemRequire, int gold, RandomCollection<Integer> rc) {
        return makeEventItem(_char, number, itemRequire, gold, rc, -1);
    }

    public static boolean makeEventItem(Char _char, int number, int[][] itemRequire, int gold, int itemIdReceive) {
        return makeEventItem(_char, number, itemRequire, gold, null, itemIdReceive);
    }

    public static boolean makeEventItem(Char p, int number, int[][] itemRequire, int gold, RandomCollection<Integer> rc, int itemIdReceive) {
        if (number < 1) {
            p.getService().npcChat(NpcName.TIEN_NU, "Số lượng tối thiểu là 1.");
            return false;
        }

        if (number > 1000) {
            p.getService().npcChat(NpcName.TIEN_NU, "Số lượng tối đa là 1.000.");
            return false;
        }

        int priceGold = number * gold;

        for (int i = 0; i < itemRequire.length; i++) {
            int itemId = itemRequire[i][0];
            int amount = itemRequire[i][1] * number;
            int index = p.getIndexItemByIdInBag(itemId);
            if (index == -1 || p.bag[index] == null || p.bag[index].getQuantity() < amount) {
                p.getService().npcChat(NpcName.TIEN_NU, "Không đủ " + ItemManager.getInstance().getItemName(itemId));
                return false;
            }
        }

        if (p.user.gold < priceGold) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ lượng");
            return false;
        } else if (rc != null && p.getSlotNull() < number) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return false;
        } else if (itemIdReceive != -1 && p.getSlotNull() < 1) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return false;
        }

        if (priceGold > 0) {
            p.addGold(-priceGold);
        }

        for (int i = 0; i < itemRequire.length; i++) {
            int itemId = itemRequire[i][0];
            int amount = itemRequire[i][1] * number;
            int index = p.getIndexItemByIdInBag(itemId);
            p.removeItem(index, amount, true);
        }

        if (rc != null) {
            for (int i = 0; i < number; i++) {
                if(!p.duatop){
                    p.addExp(5000000);
                }

                int itemId = rc.next();
                Item itm = ItemFactory.getInstance().newItem(itemId);
                itm.initExpire();
                if (itm.id == ItemName.THONG_LINH_THAO) {
                    itm.setQuantity((int)NinjaUtils.nextInt(5, 10));
                }
                p.addItemToBag(itm);
            }

            if (priceGold > 0) {
                p.getEventPoint().addPoint(EventPoint.DIEM_TIEU_XAI, number);
            }
        } else if (itemIdReceive != -1) {
            Item itm = ItemFactory.getInstance().newItem(itemIdReceive);
            itm.setQuantity(number);
            p.addItemToBag(itm);
            if (itemIdReceive == ItemName.DIEU_VAI || itemIdReceive == ItemName.HU_KEM_DAM) {
                p.getEventPoint().addPoint(EventPoint.DIEM_TIEU_XAI, number);
            }
        }
        return true;
    }

    public static void sellFish(Char _char, int itemId, int number) {
        int[][] itemRequires = new int[][]{{itemId, 1}};
        RandomCollection<Integer> rc = RandomItem.BUA_MAY_MAN;

        if (itemId == ItemName.XUONG_CA) {
            rc = RandomItem.TRE_XANH_TRAM_DOT;
        }
        // else if(itemId == ItemName.SAO_BIEN_XANH || itemId == ItemName.CUA_HOANG_DE){
        //     rc = RandomItem.BUA_MAY_MAN;
        // }
        Events.useEventItem(_char, number, itemRequires, 0, rc);
    }

    public static void useVipEventItem(Char _char, RandomCollection<Integer> rc, int type) {
        int itemId = rc.next();
        Item itm = ItemFactory.getInstance().newItem(itemId);
        itm.isLock = false;
        itm.expire = System.currentTimeMillis();

        long month = (int)NinjaUtils.nextInt(1, type == 2 ? 3 : 2);
        long expire = 86400000; // 1 day
        expire *= 30; // 30 day
        expire *= month;
        itm.expire += expire;

        if (type == 2 && (int)NinjaUtils.nextInt(1, 10) == 10) {
            itm.expire = -1;
        }

        if (itm.id == ItemName.MAT_NA_HO) {
            itm.randomOptionTigerMask();
        }

        _char.addItemToBag(itm);
    }

    public static void doiCup(Char p, int type) {
        int point = type == 1 ? 5000 : 20000;
        if (p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI) < point) {
            p.getService().npcChat(NpcName.TIEN_NU, "Ngươi cần tối thiểu " + NinjaUtils.getCurrency(point) + " điểm thi đấu mới có thể đổi được chiếc cúp này.");
            return;
        }

        if (p.getSlotNull() == 0) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return;
        }

        Item item = ItemFactory.getInstance().newItem(type == 1 ? ItemName.CUP_BAC : ItemName.CUP_VANG);
        p.addItemToBag(item);
        p.getEventPoint().subPoint(EventPoint.DIEM_TIEU_XAI, point);
    }

    public static void doiLinhVat(Char p, int type) {
        int point = type == 1 ? 5000 : 20000;
        if (p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI) < point) {
            p.getService().npcChat(NpcName.TIEN_NU, "Ngươi cần tối thiểu " + NinjaUtils.getCurrency(point) + " điểm sự kiện mới có thể đổi được vật này.");
            return;
        }

        if (p.getSlotNull() == 0) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return;
        }

        Item item = ItemFactory.getInstance().newItem(type == 1 ? ItemName.DE_NGOC : ItemName.BO_VANG);
        p.addItemToBag(item);
        p.getEventPoint().subPoint(EventPoint.DIEM_TIEU_XAI, point);
    }

    public static void doiVuKhiThoiTrang(Char p, int hsd) {
        int quantity = 20;
        int banhTrungThu = 0;
        int point = 0;
        if (hsd == 7) {
            banhTrungThu = 308;
            point = 200;
        } else if (hsd == 30) {
            banhTrungThu = 309;
            point = 500;
        }
        if (p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI) < point) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ điểm tiêu xài.");
            return;
        }

        if (p.getQuantityItemById(banhTrungThu) < quantity) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ " + ItemManager.getInstance().getItemName(banhTrungThu));
            return;
        }
        if (p.getSlotNull() == 0) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return;
        }
        for (int i = 0; i < quantity; i++) {
            int indexItem = p.getIndexItemByIdInBag(banhTrungThu);
            p.removeItem(indexItem, 1, true);
        }
        Item item = null;
        if (p.gender == 0) {
            item = ItemFactory.getInstance().newItem(800);
        } else {
            item = ItemFactory.getInstance().newItem(799);
        }
        long expire = System.currentTimeMillis() + (hsd * (long) 86400000);
        item.expire = expire;
        p.addItemToBag(item);
        p.getEventPoint().subPoint(EventPoint.DIEM_TIEU_XAI, point);
    }

    public static Mob mouseMob = null;
   public static void bornMouseBoss() {
    if (event == SUMMER) {
        Map map = MapManager.getInstance().find(169);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Calendar currentTime = Calendar.getInstance();
                        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
                        int currentMinute = currentTime.get(Calendar.MINUTE);
                        if ((currentHour == 12 && currentMinute >= 30) || (currentHour > 12 && currentHour < 2) || (currentHour == 2 && currentMinute <= 30)) {
                            if (mouseMob != null) {
                                if (mouseMob.isDead || mouseMob.hp <= 0) {
                                    mouseMob = null;
                                    Thread.sleep(10000);
                                }
                            } else {
                                List<Zone> zones = map.getZones();
                                int random = (int)NinjaUtils.nextInt(10, 20);
                                Zone z = zones.get(random);
                                MobTemplate template = MobManager.getInstance().find(237);
                                Mob monster = new Mob(z.getMonsters().size(), (short) template.id, template.hp, template.level, (short) 771, (short) 240, false, template.isBoss(), z);
                                z.addMob(monster);
                                mouseMob = monster;

                                String text = template.name + " đã xuất hiện ở " + z.tilemap.name;
                                GlobalService.getInstance().chat("Hệ thống", text);

                                Log.debug(template.name + " đã xuất hiện");
                            }
                        }
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        });
        t.start();
    }
}



}

class ListLeaderBoard {

    public ListLeaderBoard(int type) {
        this.type = type;
    }

    Vector<LeaderBoard> leaders = new Vector<>();
    public ReadWriteLock lock = new ReentrantReadWriteLock();
    private int lowestScore = 0;
    private int type = 0;

    public void sort() {
        lock.writeLock().lock();
        try {
            leaders.sort(new PointSorter());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void updateLeaderBoard(Char _char) {
//        int charEventPoint = _char.eventPoints[type];
//        if (charEventPoint > 0) {
//            int index = getIndexByChar(_char);
//            try {
//                lock.writeLock().lock();
//                if (index == -1) {
//                    if (leaders.size() < 20) {
//                        leaders.add(new LeaderBoard(_char.name, charEventPoint));
//                    } else {
//                        if (charEventPoint > lowestScore) {
//                            leaders.remove(19);
//                            leaders.add(new LeaderBoard(_char.name, charEventPoint));
//                        }
//                    }
//                } else {
//                    leaders.get(index).point = charEventPoint;
//                }
//            } finally {
//                lock.writeLock().unlock();
//            }
//            sortAndGetLowestScore();
//        }
    }

    public void sortAndGetLowestScore() {
        if (leaders.size() > 0) {
            sort();
            lowestScore = leaders.get(leaders.size() - 1).point;
        }

    }

    public int getIndexByChar(Char _char) {
        lock.readLock().lock();
        try {
            int i = 0;
            for (LeaderBoard leader : leaders) {
                if (leader.name.equals(_char.name)) {
                    return i;
                }
                i++;
            }
            return -1;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void showLeaderBoard(Char _char, String format) {
        lock.readLock().lock();
        try {
            String text = "";
            int n = 1;
            for (LeaderBoard leader : leaders) {
                text += n + ". " + leader.name + " " + String.format(format, leader.point) + "\n";
                n++;
            }
            _char.getService().showAlert("TOP SỰ KIỆN", text);
        } finally {
            lock.readLock().unlock();
        }
    }
}

class LeaderBoard {

    public LeaderBoard(String name, int point) {
        this.name = name;
        this.point = point;
    }

    public String name;
    public Integer point;
}

class PointSorter implements Comparator<LeaderBoard> {

    @Override
    public int compare(LeaderBoard o1, LeaderBoard o2) {
        return o2.point.compareTo(o1.point);
    }
}
