package com.nsoz.event;

import com.nsoz.bot.Bot;
import com.nsoz.bot.Principal;
import com.nsoz.bot.move.PrincipalMove;
import com.nsoz.constants.ItemOptionName;
import com.nsoz.constants.CMDInputDialog;
import com.nsoz.constants.CMDMenu;
import com.nsoz.constants.ConstTime;
import com.nsoz.constants.ItemName;
import com.nsoz.constants.MapName;
import com.nsoz.constants.MobName;
import com.nsoz.constants.NpcName;
import com.nsoz.event.eventpoint.EventPoint;
import com.nsoz.item.Item;
import com.nsoz.item.ItemFactory;
import com.nsoz.lib.RandomCollection;
import com.nsoz.map.Map;
import com.nsoz.map.MapManager;
import com.nsoz.map.zones.Zone;
import com.nsoz.mob.Mob;
import com.nsoz.model.Char;
import com.nsoz.model.InputDialog;
import com.nsoz.model.Menu;
import com.nsoz.npc.Npc;
import com.nsoz.npc.NpcFactory;
import com.nsoz.option.ItemOption;
import com.nsoz.server.GlobalService;
import com.nsoz.store.ItemStore;
import com.nsoz.store.StoreManager;
import com.nsoz.util.NinjaUtils;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * Được share bởi Youtube : nsotien tv
 */
public class LunarNewYear extends Event {

    public static final String TOP_LUCKY_CHARM = "lucky_charm";
    public static final String TOP_MAKE_CHUNG_CAKE = "chung_cake";
    public static final String MYSTERY_BOX_LEFT = "mystery_box";
    public static final String ENVELOPE = "envelope";
    private static final int MAKE_CHUNG_CAKE = 0;
    private static final int MAKE_TET_CAKE = 1;
    private static final int MAKE_FIREWORK = 2;
    public RandomCollection<Integer> vipItems = new RandomCollection<>();
    private ZonedDateTime start, end;

    public LunarNewYear() {
        setId(Event.LUNAR_NEW_YEAR);
        endTime.set(2024, 1, 18, 23, 59, 59);
        itemsThrownFromMonsters.add(5, ItemName.NEP);
        itemsThrownFromMonsters.add(3, ItemName.LA_DONG);
        itemsThrownFromMonsters.add(3, ItemName.DAU_XANH2);
        itemsThrownFromMonsters.add(2, ItemName.LAT_TRE);

        keyEventPoint.add(EventPoint.DIEM_TIEU_XAI);
        keyEventPoint.add(TOP_LUCKY_CHARM);
        keyEventPoint.add(TOP_MAKE_CHUNG_CAKE);
        keyEventPoint.add(MYSTERY_BOX_LEFT);
        keyEventPoint.add(ENVELOPE);

        itemsRecFromGoldItem.add(0.1, ItemName.HOA_KY_LAN);
        itemsRecFromGoldItem.add(1, ItemName.SHIRAIJI);
        itemsRecFromGoldItem.add(1, ItemName.HAJIRO);
        itemsRecFromGoldItem.add(2, ItemName.BACH_HO);
        itemsRecFromGoldItem.add(2, ItemName.LAN_SU_VU);
        itemsRecFromGoldItem.add(1, ItemName.PET_UNG_LONG);
        itemsRecFromGoldItem.add(2, ItemName.GAY_TRAI_TIM);
        itemsRecFromGoldItem.add(2, ItemName.GAY_MAT_TRANG);
        itemsRecFromGoldItem.add(15, ItemName.DA_DANH_VONG_CAP_1);
        itemsRecFromGoldItem.add(12, ItemName.DA_DANH_VONG_CAP_2);
        itemsRecFromGoldItem.add(9, ItemName.DA_DANH_VONG_CAP_3);
        itemsRecFromGoldItem.add(7, ItemName.DA_DANH_VONG_CAP_4);
        itemsRecFromGoldItem.add(5, ItemName.DA_DANH_VONG_CAP_5);
        itemsRecFromGoldItem.add(15, ItemName.VIEN_LINH_HON_CAP_1);
        itemsRecFromGoldItem.add(12, ItemName.VIEN_LINH_HON_CAP_2);
        itemsRecFromGoldItem.add(9, ItemName.VIEN_LINH_HON_CAP_3);
        itemsRecFromGoldItem.add(7, ItemName.VIEN_LINH_HON_CAP_4);
        itemsRecFromGoldItem.add(5, ItemName.VIEN_LINH_HON_CAP_5);

        itemsRecFromGold2Item.add(0.1, ItemName.HOA_KY_LAN);
        itemsRecFromGold2Item.add(1, ItemName.SHIRAIJI);
        itemsRecFromGold2Item.add(1, ItemName.HAJIRO);
        itemsRecFromGold2Item.add(2, ItemName.BACH_HO);
        itemsRecFromGold2Item.add(2, ItemName.LAN_SU_VU);
        itemsRecFromGold2Item.add(1, ItemName.PET_UNG_LONG);
        itemsRecFromGold2Item.add(2, ItemName.GAY_TRAI_TIM);
        itemsRecFromGold2Item.add(2, ItemName.GAY_MAT_TRANG);

        vipItems.add(0.4, ItemName.HOA_KY_LAN);
        vipItems.add(2, ItemName.BACH_HO);
        vipItems.add(2, ItemName.PET_UNG_LONG);
        vipItems.add(2, ItemName.HAKAIRO_YOROI);
        vipItems.add(2, ItemName.SHIRAIJI);
        vipItems.add(2, ItemName.HAJIRO);
        vipItems.add(4, ItemName.GAY_TRAI_TIM);
        vipItems.add(3, ItemName.GAY_MAT_TRANG);
        timerSpawnPrincipal();
    }

    private void timerSpawnPrincipal() {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        start = zonedNow.withMonth(1).withDayOfMonth(22).withHour(0).withMinute(0).withSecond(0);
        end = zonedNow.withMonth(1).withDayOfMonth(24).withHour(23).withMinute(59).withSecond(59);
        if (zonedNow.isAfter(start) && zonedNow.isBefore(end)) {
            start = zonedNow.plusMinutes(5);// thời gian khởi động server
        }
        if (zonedNow.compareTo(start) <= 0) {
            Duration duration = Duration.between(zonedNow, start);
            long initalDelay = duration.getSeconds();
            Runnable runnable = new Runnable() {
                public void run() {
                    spawnPrincipal();
                }
            };
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(runnable, initalDelay, 24 * 60 * 60, TimeUnit.SECONDS);

        }
    }

    public void spawnPrincipal() {
        List<BotInfo> botInfoList = new ArrayList<>();
        botInfoList.add(new BotInfo(MapName.TRUONG_HIROSAKI, "Cô Toyotomi", 44, 45, 46));
        botInfoList.add(new BotInfo(MapName.TRUONG_OOKAZA, "Thầy Ookamesama", 53, 54, 55));
        botInfoList.add(new BotInfo(MapName.TRUONG_HARUNA, "Thầy Kazeto", 65, 66, 67));

        for (BotInfo info : botInfoList) {
            Map map = MapManager.getInstance().find(info.mapId);
            Zone z = map.rand();
            System.out.println(z.id);
            Npc npc = z.getNpc(NpcName.HOA_MAI);
            if (npc != null) {
                Bot bot = info.toBot(npc);
                GlobalService.getInstance().chat(bot.name,
                        "Chúc mừng năm mới, các con hãy tới gốc cây mai tại các trường để nhận quá nhé");
                z.join(bot);
            }
        }
    }

    @Override
    public void initStore() {
        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(996)
                .itemID(ItemName.THIT_HEO)
                .gold(20)
                .expire(ConstTime.FOREVER)
                .build());
        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(997)
                .itemID(ItemName.THIEP_CHUC_TET)
                .coin(100000)
                .expire(ConstTime.FOREVER)
                .build());
        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(998)
                .itemID(ItemName.THIEP_CHUC_TET_DAC_BIET)
                .gold(20)
                .expire(ConstTime.FOREVER)
                .build());
        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(999)
                .itemID(ItemName.BUA_MAY_MAN)
                .gold(20)
                .expire(ConstTime.FOREVER)
                .build());
        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1000)
                .itemID(ItemName.VUI_XUAN)
                .gold(20)
                .expire(ConstTime.FOREVER)
                .build());
    }

    @Override
    public void action(Char p, int type, int amount) {
        if (isEnded()) {
            p.serverMessage("Sự kiện đã kết thúc");
            return;
        }
        switch (type) {
            case MAKE_CHUNG_CAKE:
                makeChungCake(p, amount);
                break;
            case MAKE_TET_CAKE:
                makeTetCake(p, amount);
                break;

            case MAKE_FIREWORK:
                makeFirework(p, amount);
                break;
        }
    }

    private void makeChungCake(Char p, int amount) {
        int[][] itemRequires = new int[][] { { ItemName.NEP, 5 }, { ItemName.LA_DONG, 3 }, { ItemName.DAU_XANH2, 3 },
                { ItemName.LAT_TRE, 2 }, { ItemName.THIT_HEO, 1 } };
        int itemIdReceive = ItemName.BANH_CHUNG;
        boolean isDone = makeEventItem(p, amount, itemRequires, 0, 0, 0, itemIdReceive);
        if (isDone) {
            p.getEventPoint().addPoint(LunarNewYear.TOP_MAKE_CHUNG_CAKE, amount);
            p.getEventPoint().addPoint(EventPoint.DIEM_TIEU_XAI, amount);
        }
    }

    private void makeTetCake(Char p, int amount) {
        int[][] itemRequires = new int[][] { { ItemName.NEP, 4 }, { ItemName.LA_DONG, 2 }, { ItemName.DAU_XANH2, 2 },
                { ItemName.LAT_TRE, 4 } };
        int itemIdReceive = ItemName.BANH_TET;
        makeEventItem(p, amount, itemRequires, 0, 120000, 0, itemIdReceive);
    }

    private void makeFirework(Char p, int amount) {
        int[][] itemRequires = new int[][] { { ItemName.MANH_PHAO_HOA, 10 } };
        int itemIdReceive = ItemName.PHAO_HOA;
        boolean isDone = makeEventItem(p, amount, itemRequires, 20, 0, 0, itemIdReceive);
        if (isDone) {
            p.getEventPoint().addPoint(EventPoint.DIEM_TIEU_XAI, amount);
        }
    }

    private void exchangeAoDai(Char p) {
        int indexTetCake = p.getIndexItemByIdInBag(ItemName.BANH_TET);
        if (indexTetCake == -1 || p.bag[indexTetCake] == null || p.bag[indexTetCake].getQuantity() < 20) {
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

        int dressId = p.gender == 1 ? ItemName.AO_NGU_THAN : ItemName.AO_TAN_THOI;

        p.removeItem(indexTetCake, 20, true);
        p.addGold(-500);
        Item item = ItemFactory.getInstance().newItem(dressId);
        item.isLock = false;
        item.expire = System.currentTimeMillis() + 1296000000L;

        item.randomOptionTigerMask();

        p.addItemToBag(item);
    }

    private void luckyMoney(Char _char, String name) {
        if (_char.level < 20) {
            _char.getService().npcChat(NpcName.TIEN_NU, "Bạn cần đạt cấp 20");
            return;
        }

        if (name.equals("")) {
            _char.getService().npcChat(NpcName.TIEN_NU, "Người này không online hoặc không tồn tại!");
            return;
        }

        Char receiver = Char.findCharByName(name);

        if (receiver == null) {
            _char.serverMessage("Người này không online hoặc không tồn tại!");
            return;
        }

        if (_char == receiver) {
            _char.serverMessage("Bạn không thể lì xì cho chính bạn!");
            return;
        }

        if (receiver.level < 20) {
            _char.serverMessage("Đối phương cần đạt level 20!");
            return;
        }

        if (_char.user.gold < 20) {
            _char.serverMessage("Bạn cần tối thiểu 20 lượng");
            return;
        }

        if (_char.getSlotNull() == 0) {
            _char.warningBagFull();
            return;
        }

        boolean isDone = useEventItem(_char, 1, 20, 0, itemsRecFromGold2Item);
        if (isDone) {
            int yen = (int)NinjaUtils.nextInt(50000, 200000);
            receiver.addYen(yen);
            receiver.serverMessage("Bạn được " + _char.name + " lì xì " + NinjaUtils.getCurrency(yen) + " yên");
        }
    }

    @Override
    public void menu(Char p) {
        p.menus.clear();
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Làm bánh", () -> {
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Bánh chưng", () -> {
                p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Bánh chưng", () -> {
                    InputDialog input = p.getInput();
                    try {
                        int number = input.intValue();
                        action(p, MAKE_CHUNG_CAKE, number);
                    } catch (NumberFormatException e) {
                        if (!input.isEmpty()) {
                            p.inputInvalid();
                        }
                    }
                }));
                p.getService().showInputDialog();
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Bánh tét", () -> {
                p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Bánh tét", () -> {
                    InputDialog input = p.getInput();
                    try {
                        int number = input.intValue();
                        action(p, MAKE_TET_CAKE, number);
                    } catch (NumberFormatException e) {
                        if (!input.isEmpty()) {
                            p.inputInvalid();
                        }
                    }
                }));
                p.getService().showInputDialog();
            }));
            p.getService().openUIMenu();
        }));
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Làm pháo hoa", () -> {
            p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Làm pháo hoa", () -> {
                InputDialog input = p.getInput();
                try {
                    int number = input.intValue();
                    action(p, MAKE_FIREWORK, number);
                } catch (NumberFormatException e) {
                    if (!input.isEmpty()) {
                        p.inputInvalid();
                    }
                }
            }));
            p.getService().showInputDialog();
        }));

        p.menus.add(new Menu(CMDMenu.EXECUTE, "Áo dài", () -> {
            exchangeAoDai(p);
        }));

        p.menus.add(new Menu(CMDMenu.EXECUTE, "Đổi lồng đèn", () -> {
            p.menus.clear();
            p.menus.add(new Menu(CMDMenu.EXECUTE, "2tr xu", () -> {
                p.setCommandBox(Char.DOI_LONG_DEN_XU);
                List<Item> list = p.getListItemByID(ItemName.LONG_DEN_TRON, ItemName.LONG_DEN_CA_CHEP,
                        ItemName.LONG_DEN_MAT_TRANG, ItemName.LONG_DEN_NGOI_SAO);
                p.getService().openUIShopTrungThu(list, "Đổi lồng đèn 2tr xu", "Đổi (2tr xu)");
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "100 lượng", () -> {
                p.setCommandBox(Char.DOI_LONG_DEN_LUONG);
                List<Item> list = p.getListItemByID(ItemName.LONG_DEN_TRON, ItemName.LONG_DEN_CA_CHEP,
                        ItemName.LONG_DEN_MAT_TRANG, ItemName.LONG_DEN_NGOI_SAO);
                p.getService().openUIShopTrungThu(list, "Đổi lồng đèn 100 lượng", "Đổi (100l)");
            }));
            p.getService().openUIMenu();
        }));

        p.menus.add(new Menu(CMDMenu.EXECUTE, "Lì xì", () -> {
            p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Tên người nhận", () -> {
                InputDialog input = p.getInput();
                luckyMoney(p, input.getText());
            }));
            p.getService().showInputDialog();
        }));

        p.menus.add(new Menu(CMDMenu.EXECUTE, "Trân hi thụ", () -> {
            p.menus.clear();
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Lam sơn dạ", () -> {
                makePreciousTree(p, 1);
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Trúc bạch thiên lữ", () -> {
                makePreciousTree(p, 2);
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Điểm sự kiện", () -> {
                p.getService().showAlert("Hướng dẫn", "- Điểm sự kiện: "
                        + NinjaUtils.getCurrency(p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI))
                        + "\n\nBạn có thể quy đổi điểm sự kiện như sau\n- Lam sơn dạ: 5.000 điểm\n- Trúc bạch thiên lữ: 20.000 điểm\n");
            }));
            p.getService().openUIMenu();
        }));

        p.menus.add(new Menu(CMDMenu.EXECUTE, "Đua TOP", () -> {
            p.menus.clear();
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Bùa may mắn", () -> {
                p.menus.clear();
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Bảng xếp hạng", () -> {
                    viewTop(p, TOP_LUCKY_CHARM, "Treo bùa may mắn", "%d. %s đã treo %s lần");
                }));
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Phần thưởng", () -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Top 1:").append("\n");
                    sb.append("- Hoả Kỳ Lân v.v MCS\n");
                    sb.append("- Áo dài v.v MCS\n");
                    sb.append("- 3 Rương huyền bí\n");
                    sb.append("- 10 Trúc bạch thiên lữ\n\n");
                    sb.append("Top 2:").append("\n");
                    sb.append("- Hoả Kỳ Lân v.v\n");
                    sb.append("- Áo dài v.v\n");
                    sb.append("- 1 Rương huyền bí\n");
                    sb.append("- 5 Trúc bạch thiên lữ\n\n");
                    sb.append("Top 3 - 5:").append("\n");
                    sb.append("- Hoả Kỳ Lân 3 tháng\n");
                    sb.append("- Áo dài 3 tháng\n");
                    sb.append("- 2 Rương bạch ngân\n");
                    sb.append("- 3 Trúc bạch thiên lữ\n\n");
                    sb.append("Top 6 - 10:").append("\n");
                    sb.append("- Hoả Kỳ Lân 1 tháng\n");
                    sb.append("- 1 rương bạch ngân\n");
                    p.getService().showAlert("Phần thưởng", sb.toString());
                }));
                if (isEnded()) {
                    int ranking = getRanking(p, TOP_LUCKY_CHARM);
                    if (ranking <= 10 && p.getEventPoint().getRewarded(TOP_LUCKY_CHARM) == 0) {
                        p.menus.add(new Menu(CMDMenu.EXECUTE, String.format("Nhận Thưởng TOP %d", ranking), () -> {
                            receiveReward(p, TOP_LUCKY_CHARM);
                        }));
                    }
                }
                p.getService().openUIMenu();
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Thợ làm bánh", () -> {
                p.menus.clear();
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Bảng xếp hạng", () -> {
                    viewTop(p, TOP_MAKE_CHUNG_CAKE, "Thợ làm bánh", "%d. %s đã làm %s chiếc bánh");
                }));
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Phần thưởng", () -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Top 1:").append("\n");
                    sb.append("- Pet ứng long v.v MCS\n");
                    sb.append("- Gậy thời trang v.v\n");
                    sb.append("- 3 rương huyền bí\n");
                    sb.append("- 10 Trúc bạch thiên lữ\n\n");
                    sb.append("Top 2:").append("\n");
                    sb.append("- Pet ứng long v.v\n");
                    sb.append("- Gậy thời trang v.v\n");
                    sb.append("- 1 rương huyền bí\n");
                    sb.append("- 5 Trúc bạch thiên lữ\n\n");
                    sb.append("Top 3 - 5:").append("\n");
                    sb.append("- Pet ứng long 3 tháng\n");
                    sb.append("- Gậy thời trang 3 tháng\n");
                    sb.append("- 2 rương bạch ngân\n");
                    sb.append("- 3 Trúc bạch thiên lữ\n\n");
                    sb.append("Top 6 - 10:").append("\n");
                    sb.append("- Pet ứng long 1 tháng\n");
                    sb.append("- 1 rương bạch ngân\n");
                    p.getService().showAlert("Phần thưởng", sb.toString());
                }));
                if (isEnded()) {
                    int ranking = getRanking(p, TOP_MAKE_CHUNG_CAKE);
                    if (ranking <= 10 && p.getEventPoint().getRewarded(TOP_MAKE_CHUNG_CAKE) == 0) {
                        p.menus.add(new Menu(CMDMenu.EXECUTE, String.format("Nhận Thưởng TOP %d", ranking), () -> {
                            receiveReward(p, TOP_MAKE_CHUNG_CAKE);
                        }));
                    }
                }
                p.getService().openUIMenu();
            }));

            p.getService().openUIMenu();
        }));
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Hướng dẫn", () -> {
            StringBuilder sb = new StringBuilder();
            sb.append("- Số lần hái lộc: ")
                    .append(NinjaUtils.getCurrency(p.getEventPoint().getPoint(TOP_LUCKY_CHARM))).append("\n");
            sb.append("- Số bánh đã làm: ")
                    .append(NinjaUtils.getCurrency(p.getEventPoint().getPoint(TOP_MAKE_CHUNG_CAKE))).append("\n");
            sb.append("===CÔNG THỨC===").append("\n");
            sb.append("- Bánh chưng: 5 nếp + 3 lá dong + 3 đậu xanh + 2 lạt tre + 1 thịt heo.").append("\n");
            sb.append("- Bánh tét: 5 nếp + 3 lá dong + 3 đậu xanh + 2 lạt tre + 120.000 xu.").append("\n");
            sb.append("- Pháo hoa: 10 mảnh pháp hoa + 20 Lượng.").append("\n");
            sb.append("- Mặt nạ hổ: 20 bánh tét + 500 Lượng.").append("\n");
            p.getService().showAlert("Hướng Dẫn", sb.toString());
        }));

    }

    public void makePreciousTree(Char p, int type) {
        int point = type == 1 ? 5000 : 20000;
        if (p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI) < point) {
            p.getService().npcChat(NpcName.TIEN_NU,
                    "Ngươi cần tối thiểu " + NinjaUtils.getCurrency(point)
                            + " điểm sự kiện mới có thể đổi được vật này.");
            return;
        }

        if (p.getSlotNull() == 0) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return;
        }

        Item item = ItemFactory.getInstance().newItem(type == 1 ? ItemName.LAM_SON_DA : ItemName.TRUC_BACH_THIEN_LU);
        p.addItemToBag(item);
        p.getEventPoint().subPoint(EventPoint.DIEM_TIEU_XAI, point);
    }

    @Override
    public void initMap(Zone zone) {
        Map map = zone.map;
        int mapID = map.id;
        switch (mapID) {
            case MapName.KHU_LUYEN_TAP:
                break;
            case MapName.TRUONG_OOKAZA:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.HOA_MAI, 1426, 552, 0));
                break;
            case MapName.TRUONG_HARUNA:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.HOA_MAI, 502, 408, 0));
                break;
            case MapName.TRUONG_HIROSAKI:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.HOA_MAI, 1207, 168, 0));
                break;

            case MapName.LANG_TONE:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.HOA_MAI, 1427, 264, 0));
                break;

            case MapName.LANG_KOJIN:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.HOA_MAI, 621, 288, 0));
                break;

            case MapName.LANG_CHAI:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.HOA_MAI, 1804, 384, 0));
                break;

            case MapName.LANG_SANZU:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.HOA_MAI, 320, 288, 0));
                break;

            case MapName.LANG_CHAKUMI:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.HOA_MAI, 626, 312, 0));
                break;

            case MapName.LANG_ECHIGO:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.HOA_MAI, 360, 360, 0));
                break;

            case MapName.LANG_OSHIN:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.HOA_MAI, 921, 408, 0));
                break;

            case MapName.LANG_SHIIBA:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.HOA_MAI, 583, 408, 0));
                break;

            case MapName.LANG_FEARRI:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.HOA_MAI, 611, 312, 0));
                break;

            case MapName.CANH_DONG_FUKI:
                Mob monster = new Mob(zone.getMonsters().size(), (short) MobName.HOP_BI_AN, 20200, (byte) 10,
                        (short) 3355, (short) 240, false, true, zone);
                zone.addMob(monster);
                break;

            case MapName.RUNG_DAO_SAKURA:
                if (zone.id == 15) {
                    monster = new Mob(zone.getMonsters().size(), (short) MobName.CHUOT_CANH_TY, 1000000000, (byte) 100,
                            (short) 1928, (short) 240, false, true, zone);
                    zone.addMob(monster);
                }
                break;
        }
    }

    public void receiveReward(Char p, String key) {
        int ranking = getRanking(p, key);
        if (ranking > 10) {
            p.getService().serverDialog("Bạn không đủ điều kiện nhận phần thưởng");
            return;
        }
        if (p.getEventPoint().getRewarded(key) == 1) {
            p.getService().serverDialog("Bạn đã nhận phần thưởng rồi");
            return;
        }
        if (p.getSlotNull() < 10) {
            p.getService().serverDialog("Bạn cần để hành trang trống tối thiểu 10 ô");
            return;
        }

        if (key == TOP_LUCKY_CHARM) {
            topDecorationGiftBox(ranking, p);
        } else if (key == TOP_MAKE_CHUNG_CAKE) {
            topMakeChungCake(ranking, p);
        }
        p.getEventPoint().setRewarded(key, 1);
    }

    public void topDecorationGiftBox(int ranking, Char p) {
        Item mount = ItemFactory.getInstance().newItem(ItemName.HOA_KY_LAN);
        int dressId = p.gender == 1 ? ItemName.AO_NGU_THAN : ItemName.AO_TAN_THOI;
        Item aoDai = ItemFactory.getInstance().newItem(dressId);
        Item tree = ItemFactory.getInstance().newItem(ItemName.TRUC_BACH_THIEN_LU);
        if (ranking == 1) {
            mount.options.add(new ItemOption(ItemOptionName.NE_DON_ADD_POINT_TYPE_1, 200));
            mount.options.add(new ItemOption(ItemOptionName.CHINH_XAC_ADD_POINT_TYPE_1, 100));
            mount.options.add(new ItemOption(ItemOptionName.TAN_CONG_KHI_DANH_CHI_MANG_POINT_PERCENT_TYPE_1, 100));
            mount.options.add(new ItemOption(ItemOptionName.CHI_MANG_ADD_POINT_TYPE_1, 100));
            mount.options.add(new ItemOption(58, 10));
            mount.options.add(new ItemOption(128, 10));
            mount.options.add(new ItemOption(127, 10));
            mount.options.add(new ItemOption(130, 10));
            mount.options.add(new ItemOption(131, 10));

            aoDai.options.add(new ItemOption(125, 3000));
            aoDai.options.add(new ItemOption(117, 3000));
            aoDai.options.add(new ItemOption(94, 10));
            aoDai.options.add(new ItemOption(136, 30));
            aoDai.options.add(new ItemOption(127, 10));
            aoDai.options.add(new ItemOption(130, 10));
            aoDai.options.add(new ItemOption(131, 10));

            tree.setQuantity(10);
            p.addItemToBag(tree);
            for (int i = 0; i < 3; i++) {
                Item mysteryChest = ItemFactory.getInstance().newItem(ItemName.RUONG_HUYEN_BI);
                p.addItemToBag(mysteryChest);
            }
        } else if (ranking == 2) {
            tree.setQuantity(5);
            p.addItemToBag(tree);
            Item mysteryChest = ItemFactory.getInstance().newItem(ItemName.RUONG_HUYEN_BI);
            p.addItemToBag(mysteryChest);
        } else if (ranking >= 3 && ranking <= 5) {
            mount.expire = System.currentTimeMillis() + ConstTime.DAY * 90L;
            aoDai.expire = System.currentTimeMillis() + ConstTime.DAY * 90L;
            tree.setQuantity(3);
            p.addItemToBag(tree);
            for (int i = 0; i < 2; i++) {
                Item blueChest = ItemFactory.getInstance().newItem(ItemName.RUONG_BACH_NGAN);
                p.addItemToBag(blueChest);
            }
        } else {
            mount.expire = System.currentTimeMillis() + ConstTime.DAY * 30L;
            aoDai.expire = System.currentTimeMillis() + ConstTime.DAY * 30L;
            Item blueChest = ItemFactory.getInstance().newItem(ItemName.RUONG_BACH_NGAN);
            p.addItemToBag(blueChest);
        }

        p.addItemToBag(mount);
        p.addItemToBag(aoDai);
    }

    public void topMakeChungCake(int ranking, Char p) {
        Item pet = ItemFactory.getInstance().newItem(ItemName.PET_UNG_LONG);
        int tickId = p.gender == 1 ? ItemName.GAY_MAT_TRANG : ItemName.GAY_TRAI_TIM;
        Item fashionStick = ItemFactory.getInstance().newItem(tickId);
        Item tree = ItemFactory.getInstance().newItem(ItemName.TRUC_BACH_THIEN_LU);
        if (ranking == 1) {
            pet.options.add(new ItemOption(ItemOptionName.HP_TOI_DA_ADD_POINT_TYPE_1, 3000));
            pet.options.add(new ItemOption(ItemOptionName.MP_TOI_DA_ADD_POINT_TYPE_1, 3000));
            pet.options.add(new ItemOption(ItemOptionName.CHI_MANG_POINT_TYPE_1, 100)); // chi mang
            pet.options.add(new ItemOption(ItemOptionName.TAN_CONG_ADD_POINT_PERCENT_TYPE_8, 10));
            pet.options.add(new ItemOption(ItemOptionName.MOI_5_GIAY_PHUC_HOI_MP_POINT_TYPE_1, 200));
            pet.options.add(new ItemOption(ItemOptionName.MOI_5_GIAY_PHUC_HOI_HP_POINT_TYPE_1, 200));
            pet.options.add(new ItemOption(ItemOptionName.KHONG_NHAN_EXP_TYPE_0, 1));

            tree.setQuantity(10);
            p.addItemToBag(tree);
            for (int i = 0; i < 3; i++) {
                Item mysteryChest = ItemFactory.getInstance().newItem(ItemName.RUONG_HUYEN_BI);
                p.addItemToBag(mysteryChest);
            }
        } else if (ranking == 2) {
            tree.setQuantity(5);
            p.addItemToBag(tree);
            Item mysteryChest = ItemFactory.getInstance().newItem(ItemName.RUONG_HUYEN_BI);
            p.addItemToBag(mysteryChest);
        } else if (ranking >= 3 && ranking <= 5) {
            pet.expire = System.currentTimeMillis() + ConstTime.DAY * 90L;
            fashionStick.expire = System.currentTimeMillis() + ConstTime.DAY * 90L;
            tree.setQuantity(3);
            p.addItemToBag(tree);
            for (int i = 0; i < 2; i++) {
                Item blueChest = ItemFactory.getInstance().newItem(ItemName.RUONG_BACH_NGAN);
                p.addItemToBag(blueChest);
            }
        } else {
            pet.expire = System.currentTimeMillis() + ConstTime.DAY * 30L;
            fashionStick.expire = System.currentTimeMillis() + ConstTime.DAY * 30L;
            Item blueChest = ItemFactory.getInstance().newItem(ItemName.RUONG_BACH_NGAN);
            p.addItemToBag(blueChest);
        }

        p.addItemToBag(pet);
        p.addItemToBag(fashionStick);
    }

    @Override
    public void useItem(Char p, Item item) {
        switch (item.id) {
            case ItemName.BANH_CHUNG:
                if (p.getSlotNull() == 0) {
                    p.warningBagFull();
                    return;
                }
                useEventItem(p, item.id, itemsRecFromGoldItem);
                break;
            case ItemName.BANH_TET:
                if (p.getSlotNull() == 0) {
                    p.warningBagFull();
                    return;
                }
                useEventItem(p, item.id, itemsRecFromCoinItem);
                break;
            case ItemName.BUA_MAY_MAN:
                if (p.getSlotNull() == 0) {
                    p.warningBagFull();
                    return;
                }
                Npc npc = p.zone.getNpc(NpcName.HOA_MAI);
                if (npc == null) {
                    p.serverMessage("Hãy lại gần Hoa Mai để sử dụng.");
                    return;
                }
                int distance = NinjaUtils.getDistance(npc.cx, npc.cy, p.x, p.y);
                if (distance > 100) {
                    p.serverMessage("Hãy lại gần Hoa Mai để sử dụng.");
                    return;
                }
                useEventItem(p, item.id, itemsRecFromGold2Item);
                p.getEventPoint().addPoint(LunarNewYear.TOP_LUCKY_CHARM, 1);
                p.getEventPoint().addPoint(EventPoint.DIEM_TIEU_XAI, 1);
                break;

            case ItemName.PHAO_HOA:
                if (p.getSlotNull() == 0) {
                    p.warningBagFull();
                    return;
                }
                useEventItem(p, item.id, itemsRecFromGold2Item);
                p.getEventPoint().addPoint(LunarNewYear.TOP_LUCKY_CHARM, 1);
                p.zone.getService().addEffectAuto((byte) 0, (short) p.x, p.y, (byte) 0, (short) 5);
                break;

            case ItemName.BAO_LI_XI_LON:
            case ItemName.HOP_QUA_NOEL:
                if (p.getSlotNull() == 0) {
                    p.warningBagFull();
                    return;
                }
                useEventItem(p, item.id, itemsRecFromGold2Item);
                break;

            case ItemName.VUI_XUAN:
                p.getEventPoint().addPoint(LunarNewYear.MYSTERY_BOX_LEFT, 1);
                p.serverMessage(
                        "Số lần vui xuân hiện tại: " + p.getEventPoint().getPoint(LunarNewYear.MYSTERY_BOX_LEFT));
                p.removeItem(item.index, 1, true);
                break;
        }
    }

    class BotInfo {

        int id;
        int mapId;
        String name;
        int head;
        int body;
        int leg;

        public BotInfo(int mapId, String name, int head, int body, int leg) {
            this.id = -(int)NinjaUtils.nextInt(100000, 200000);
            this.mapId = mapId;
            this.name = name;
            this.head = head;
            this.body = body;
            this.leg = leg;
        }

        public Bot toBot(Npc npc) {
            Bot bot = new Principal(id, name, head, body, leg);
            bot.setDefault();
            bot.recovery();
            bot.setXY((short) npc.cx, (short) npc.cy);
            bot.setMove(new PrincipalMove(npc));
            return bot;
        }

    }

}
