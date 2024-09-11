
package com.nsoz.event;

import com.nsoz.bot.Bot;
import com.nsoz.bot.SantaClaus;
import com.nsoz.bot.move.SantaClausMove;
import com.nsoz.constants.ItemOptionName;
import com.nsoz.constants.CMDInputDialog;
import com.nsoz.constants.CMDMenu;
import com.nsoz.constants.ConstTime;
import com.nsoz.constants.ItemName;
import com.nsoz.constants.MapName;
import com.nsoz.constants.MobName;
import com.nsoz.constants.NpcName;
import com.nsoz.effect.Effect;
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
import com.nsoz.server.ServerManager;
import com.nsoz.store.ItemStore;
import com.nsoz.store.StoreManager;
import com.nsoz.util.NinjaUtils;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Noel extends Event {

    public static final String TOP_DECORATION_GIFT_BOX = "decoration_gift_box";
    public static final String TOP_KILL_REINDEER_KING = "kill_reindeer_king";
    public static final String TOP_CHOCOLATE_CAKE = "chocholate_cake";
    public static final String TOP_KILL_SNOWMAN = "kill_snowman";
    public static final String RECEIVED_GIFT = "received_gift";
    private static final int LAM_BANH_KHUC_DAU_TAY = 0;
    private static final int LAM_BANH_KHUC_CHOCOLATE = 1;
    private static final int LAM_HOP_QUA = 2;
    private static final int DOI_DIEM_NGUOI_TUYET_XU = 3;
    private static final int DOI_DIEM_NGUOI_TUYET_LUONG = 4;
    private RandomCollection<Integer> vipItems = new RandomCollection<>();
    private ZonedDateTime start, end;

    public Noel() {
        setId(Event.NOEL);
        endTime.set(2023, 8, 18, 23, 59, 59);
        itemsThrownFromMonsters.add(5, ItemName.BO);
        itemsThrownFromMonsters.add(5, ItemName.KEM);
        itemsThrownFromMonsters.add(5, ItemName.DUONG_BOT);
        itemsThrownFromMonsters.add(3, ItemName.TRAI_CHAU);
        itemsThrownFromMonsters.add(3, ItemName.RUY_BANG);
        itemsThrownFromMonsters.add(3, ItemName.CHUONG_VANG);
        itemsThrownFromMonsters.add(0.005, ItemName.LANH_DUOC);

        keyEventPoint.add(EventPoint.DIEM_TIEU_XAI);
        keyEventPoint.add(TOP_DECORATION_GIFT_BOX);
        keyEventPoint.add(TOP_KILL_REINDEER_KING);
        keyEventPoint.add(TOP_CHOCOLATE_CAKE);
        keyEventPoint.add(TOP_KILL_SNOWMAN);
        keyEventPoint.add(RECEIVED_GIFT);

        itemsRecFromGoldItem.add(0.5, ItemName.SHIRAIJI);
        itemsRecFromGoldItem.add(0.5, ItemName.HAJIRO);
        itemsRecFromGoldItem.add(2, ItemName.PHUONG_HOANG_BANG);
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

        itemsRecFromGold2Item.add(0.5, ItemName.SHIRAIJI);
        itemsRecFromGold2Item.add(0.5, ItemName.HAJIRO);
        itemsRecFromGold2Item.add(2, ItemName.PHUONG_HOANG_BANG);
        itemsRecFromGold2Item.add(1, ItemName.PET_UNG_LONG);
        itemsRecFromGold2Item.add(2, ItemName.GAY_TRAI_TIM);
        itemsRecFromGold2Item.add(2, ItemName.GAY_MAT_TRANG);

        vipItems.add(1, ItemName.PHUONG_HOANG_BANG);
        vipItems.add(2, ItemName.PET_UNG_LONG);
        vipItems.add(2, ItemName.TUAN_LOC);
        vipItems.add(2, ItemName.HAKAIRO_YOROI);
        vipItems.add(2, ItemName.SHIRAIJI);
        vipItems.add(2, ItemName.HAJIRO);
        vipItems.add(2, ItemName.GAY_TRAI_TIM);
        vipItems.add(2, ItemName.GAY_MAT_TRANG);
        timerSpawnSantaClaus();
    }

    private void timerSpawnSantaClaus() {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        start = zonedNow.withMonth(7).withDayOfMonth(23).withHour(2).withMinute(2).withSecond(0);
        end = zonedNow.withMonth(8).withDayOfMonth(18).withHour(23).withMinute(59).withSecond(59);
        if (zonedNow.isAfter(start) && zonedNow.isBefore(end)) {
            start = zonedNow.plusMinutes(5);// thời gian khởi động server
        }
        if (zonedNow.compareTo(start) <= 0) {
            Duration duration = Duration.between(zonedNow, start);
            long initalDelay = duration.getSeconds();
            Runnable runnable = new Runnable() {
                public void run() {
                    spawnSantaClaus();
                }
            };
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(runnable, initalDelay, 24 * 60 * 60, TimeUnit.SECONDS);

        }

    }

    private void spawnSantaClaus() {
        GlobalService.getInstance().chat("Hệ thống", "Ông già Noel đã xuất hiện, hãy tới nhặt quà dưới gốc Cây thông.");
        int[] maps = {MapName.TRUONG_HIROSAKI, MapName.TRUONG_OOKAZA, MapName.TRUONG_HARUNA, MapName.LANG_CHAI, MapName.LANG_CHAKUMI,
            MapName.LANG_ECHIGO, MapName.LANG_FEARRI, MapName.LANG_KOJIN, MapName.LANG_OSHIN, MapName.LANG_SANZU, MapName.LANG_SHIIBA, MapName.LANG_TONE};

        for (int mapID : maps) {
            Map map = MapManager.getInstance().find(mapID);
            Zone z = map.rand();
            Npc npc = z.getNpc(NpcName.CAY_THONG);
            if (npc != null) {
                Bot bot = new SantaClaus(-(int)NinjaUtils.nextInt(100000, 200000));
                bot.setDefault();
                bot.recovery();
                bot.setXY((short) npc.cx, (short) npc.cy);
                bot.setMove(new SantaClausMove(npc));
                z.join(bot);
            }
        }
    }

    public void initEffectCool() {
        NinjaUtils.schedule(() -> {
            GlobalService.getInstance().chat("Người tuyết", "Màn đêm đã buông, làn gió lạnh lẽo đang thổi tới trên khắp bản đồ, các ngươi hãy cẩn thận nhé!");
            ServerManager.getChars().stream().forEach((Char _char) -> {
                _char.serverMessage("Lạnh quá, sức đánh và khả năng hồi phục của bạn bị giảm đi 50%, hãy tìm gosho để mua lãnh dược!");
            });
        }, 6, 0, 0);
        NinjaUtils.schedule(() -> {
            GlobalService.getInstance().chat("Người tuyết", "Trời sáng rồi, thật tuyệt!");
        }, 18, 0, 0);
    }

    @Override
    public void initStore() {
        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(996)
                .itemID(ItemName.CHOCOLATE)
                .gold(20)
                .expire(ConstTime.FOREVER)
                .build());
        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(997)
                .itemID(ItemName.DAU_TAY)
                .coin(100000)
                .expire(ConstTime.FOREVER)
                .build());
        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(998)
                .itemID(ItemName.TUAN_THU_LENH)
                .gold(20)
                .expire(ConstTime.FOREVER)
                .build());
        List<ItemOption> options = new ArrayList<>();
        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(999)
                .itemID(ItemName.TUAN_LOC)
                .gold(500)
                .options(options)
                .expire(ConstTime.MONTH)
                .build());
        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1000)
                .itemID(ItemName.LANH_DUOC)
                .gold(20)
                .expire(ConstTime.DAY * 7)
                .build());
        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1001)
                .itemID(ItemName.QUA_TRANG_TRI)
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
            case LAM_BANH_KHUC_DAU_TAY:
                makeStrawberryCake(p, amount);
                break;
            case LAM_BANH_KHUC_CHOCOLATE:
                makeChocolateCake(p, amount);
                break;

            case LAM_HOP_QUA:
                makeGiftBox(p, amount);
                break;

            case DOI_DIEM_NGUOI_TUYET_XU:
                snowmanKilledCoin(p, amount);
                break;

            case DOI_DIEM_NGUOI_TUYET_LUONG:
                snowmanKilledGold(p, amount);
                break;
        }
    }

    private void makeStrawberryCake(Char p, int amount) {
        int[][] itemRequires = new int[][]{{ItemName.BO, 5}, {ItemName.KEM, 5}, {ItemName.DUONG_BOT, 5}, {ItemName.DAU_TAY, 2}};
        int itemIdReceive = ItemName.BANH_KHUC_CAY_DAU_TAY;
        makeEventItem(p, amount, itemRequires, 0, 0, 0, itemIdReceive);
    }

    private void makeChocolateCake(Char p, int amount) {
        int[][] itemRequires = new int[][]{{ItemName.BO, 5}, {ItemName.KEM, 5}, {ItemName.DUONG_BOT, 5}, {ItemName.CHOCOLATE, 1}};
        int itemIdReceive = ItemName.BANH_KHUC_CAY_CHOCOLATE;
        boolean isDone = makeEventItem(p, amount, itemRequires, 0, 0, 0, itemIdReceive);
        if (isDone && p.duatop) {
            p.getEventPoint().addPoint(Noel.TOP_CHOCOLATE_CAKE, amount);
            p.getEventPoint().addPoint(EventPoint.DIEM_TIEU_XAI, amount);
        }
    }

    private void makeGiftBox(Char p, int amount) {
        int[][] itemRequires = new int[][]{{ItemName.TRAI_CHAU, 3}, {ItemName.RUY_BANG, 3}, {ItemName.CHUONG_VANG, 3}};
        int itemIdReceive = ItemName.HOP_QUA_TRANG_TRI;
        boolean isDone = makeEventItem(p, amount, itemRequires, 20, 0, 0, itemIdReceive);
        if (isDone && p.duatop) {
            p.getEventPoint().addPoint(EventPoint.DIEM_TIEU_XAI, amount);
        }
    }

    private void snowmanKilledCoin(Char p, int amount) {
        if (isCanExchangeSnowmanSkilledPoint(p, amount)) {
            useEventItem(p, amount, 0, 200000, itemsRecFromCoinItem);
            if ((int)NinjaUtils.nextInt(1000) == 0) {
                Item item = new Item(ItemName.LAM_SON_DA);
                p.addItemToBag(item);
            }
            p.getEventPoint().subPoint(TOP_KILL_SNOWMAN, 20 * amount);
        }
    }

    private void snowmanKilledGold(Char p, int amount) {
        if (isCanExchangeSnowmanSkilledPoint(p, amount)) {
            useEventItem(p, amount, 20, 0, itemsRecFromGold2Item);
            if ((int)NinjaUtils.nextInt(2000) == 0) {
                Item item = new Item(ItemName.TRUC_BACH_THIEN_LU);
                p.addItemToBag(item);
            }
            p.getEventPoint().subPoint(TOP_KILL_SNOWMAN, 20 * amount);
        }
    }

    private boolean isCanExchangeSnowmanSkilledPoint(Char p, int amount) {
        if (p.getEventPoint().getPoint(TOP_KILL_SNOWMAN) < 20 * amount) {
            p.getService().npcChat(NpcName.TIEN_NU,
                    "Ngươi cần tối thiểu " + NinjaUtils.getCurrency(20 * amount)
                    + " điểm tiêu diệt người tuyết mới có thể trao đổi.");
            return false;
        }
        return true;

    }

    @Override
    public void menu(Char p) {
        p.menus.clear();
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Làm bánh", () -> {
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Bánh khúc dâu tây", () -> {
                p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Bánh khúc dâu tây", () -> {
                    InputDialog input = p.getInput();
                    try {
                        int number = input.intValue();
                        action(p, LAM_BANH_KHUC_DAU_TAY, number);
                    } catch (NumberFormatException e) {
                        if (!input.isEmpty()) {
                            p.inputInvalid();
                        }
                    }
                }));
                p.getService().showInputDialog();
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Bánh khúc chocolate", () -> {
                p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Bánh khúc chocolate", () -> {
                    InputDialog input = p.getInput();
                    try {
                        int number = input.intValue();
                        action(p, LAM_BANH_KHUC_CHOCOLATE, number);
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
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Làm Hộp Quà", () -> {
            p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Làm Hộp Quà", () -> {
                InputDialog input = p.getInput();
                try {
                    int number = input.intValue();
                    action(p, LAM_HOP_QUA, number);
                } catch (NumberFormatException e) {
                    if (!input.isEmpty()) {
                        p.inputInvalid();
                    }
                }
            }));
            p.getService().showInputDialog();
        }));
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Đổi quà", () -> {
            p.menus.clear();
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Đổi bằng 200.000 xu", () -> {
                p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Đổi bằng 200.000 xu", () -> {
                    InputDialog input = p.getInput();
                    try {
                        int number = input.intValue();
                        action(p, DOI_DIEM_NGUOI_TUYET_XU, number);
                    } catch (NumberFormatException e) {
                        if (!input.isEmpty()) {
                            p.inputInvalid();
                        }
                    }
                }));
                p.getService().showInputDialog();
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Đổi bằng 20 lượng", () -> {
                p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Đổi bằng 20 lượng", () -> {
                    InputDialog input = p.getInput();
                    try {
                        int number = input.intValue();
                        action(p, DOI_DIEM_NGUOI_TUYET_LUONG, number);
                    } catch (NumberFormatException e) {
                        if (!input.isEmpty()) {
                            p.inputInvalid();
                        }
                    }
                }));
                p.getService().showInputDialog();
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Hướng dẫn", () -> {
                StringBuilder sb = new StringBuilder();
                sb.append("- Tiêu diệt người tuyết: ")
                        .append(NinjaUtils.getCurrency(p.getEventPoint().getPoint(TOP_KILL_SNOWMAN))).append("\n");
                sb.append("-- CÔNG THỨC --").append("\n");
                sb.append("- 1: 20 điểm tiêu diệt + 20 lượng").append("\n");
                sb.append("- 2: 20 điểm tiêu diệt + 200.000 xu.").append("\n");
                sb.append("- Công thức 1 có tỉ lệ ra Lam Sơn Dạ.").append("\n");
                sb.append("- Công thức 2 có tỉ lệ ra Trúc Bạch Thiên Lữ.").append("\n");
                p.getService().showAlert("Hướng Dẫn", sb.toString());
            }));
            p.getService().openUIMenu();

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
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Trang trí", () -> {
                p.menus.clear();
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Bảng xếp hạng", () -> {
                    viewTop(p, TOP_DECORATION_GIFT_BOX, "Trang trí cây thông Noel", "%d. %s đã trang trí %s lần");
                }));
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Phần thưởng", () -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Top 1:").append("\n");
                    sb.append("- Phượng Hoàng Băng v.v MCS\n");
                    sb.append("- Gậy thời trang v.v\n");
                    sb.append("- 3 Rương huyền bí\n");
                    sb.append("- 10 Trúc bạch thiên lữ\n\n");
                    sb.append("Top 2:").append("\n");
                    sb.append("- Phượng Hoàng Băng v.v\n");
                    sb.append("- Gậy thời trang v.v\n");
                    sb.append("- 1 Rương huyền bí\n");
                    sb.append("- 5 Trúc bạch thiên lữ\n\n");
                    sb.append("Top 3 - 5:").append("\n");
                    sb.append("- Phượng Hoàng Băng 3 tháng\n");
                    sb.append("- Gậy thời trang 3 tháng\n");
                    sb.append("- 2 Rương bạch ngân\n");
                    sb.append("- 3 Trúc bạch thiên lữ\n\n");
                    sb.append("Top 6 - 10:").append("\n");
                    sb.append("- Phượng Hoàng Băng 1 tháng\n");
                    sb.append("- 1 rương bạch ngân\n");
                    p.getService().showAlert("Phần thưởng", sb.toString());
                }));
                if (isEnded()) {
                    int ranking = getRanking(p, TOP_DECORATION_GIFT_BOX);
                    if (ranking <= 10 && p.getEventPoint().getRewarded(TOP_DECORATION_GIFT_BOX) == 0) {
                        p.menus.add(new Menu(CMDMenu.EXECUTE, String.format("Nhận Thưởng TOP %d", ranking), () -> {
                            receiveReward(p, TOP_DECORATION_GIFT_BOX);
                        }));
                    }
                }
                p.getService().openUIMenu();
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Thợ làm bánh", () -> {
                p.menus.clear();
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Bảng xếp hạng", () -> {
                    viewTop(p, TOP_CHOCOLATE_CAKE, "Thợ làm bánh", "%d. %s đã làm %s chiếc bánh");
                }));
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Phần thưởng", () -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Top 1:").append("\n");
                    sb.append("- Pet ứng long v.v MCS\n");
                    sb.append("- SHIRAIJI/HAJIRO v.v MCS\n");
                    sb.append("- 3 rương huyền bí\n");
                    sb.append("- 10 Trúc bạch thiên lữ\n\n");
                    sb.append("Top 2:").append("\n");
                    sb.append("- Pet ứng long v.v\n");
                    sb.append("- SHIRAIJI/HAJIRO v.v\n");
                    sb.append("- 1 rương huyền bí\n");
                    sb.append("- 5 Trúc bạch thiên lữ\n\n");
                    sb.append("Top 3 - 5:").append("\n");
                    sb.append("- Pet ứng long 3 tháng\n");
                    sb.append("- SHIRAIJI/HAJIRO 3 tháng\n");
                    sb.append("- 2 rương bạch ngân\n");
                    sb.append("- 3 Trúc bạch thiên lữ\n\n");
                    sb.append("Top 6 - 10:").append("\n");
                    sb.append("- Pet ứng long 1 tháng\n");
                    sb.append("- 1 rương bạch ngân\n");
                    p.getService().showAlert("Phần thưởng", sb.toString());
                }));
                if (isEnded()) {
                    int ranking = getRanking(p, TOP_CHOCOLATE_CAKE);
                    if (ranking <= 10 && p.getEventPoint().getRewarded(TOP_CHOCOLATE_CAKE) == 0) {
                        p.menus.add(new Menu(CMDMenu.EXECUTE, String.format("Nhận Thưởng TOP %d", ranking), () -> {
                            receiveReward(p, TOP_CHOCOLATE_CAKE);
                        }));
                    }
                }
                p.getService().openUIMenu();
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Vua tuần lộc", () -> {
                p.menus.clear();
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Bảng xếp hạng", () -> {
                    viewTop(p, TOP_KILL_REINDEER_KING, "Tiêu diệt vua tuần lộc",
                            "%d. %s đã tiêu diệt %s Boss Vua Tuần Lộc");
                }));
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Phần thưởng", () -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Top 1:").append("\n");
                    sb.append("- Pet Tuần Lộc v.v\n");
                    sb.append("- 3 rương huyền bí\n");
                    sb.append("- 10 Trúc bạch thiên lữ\n\n");
                    sb.append("Top 2:").append("\n");
                    sb.append("- Pet Tuần Lộc v.v\n");
                    sb.append("- 1 rương huyền bí\n");
                    sb.append("- 5 Trúc bạch thiên lữ\n\n");
                    sb.append("Top 3 - 5:").append("\n");
                    sb.append("- Pet Tuần Lộc 3 tháng\n");
                    sb.append("- 2 rương bạch ngân\n");
                    sb.append("- 3 Trúc bạch thiên lữ\n\n");
                    sb.append("Top 6 - 10:").append("\n");
                    sb.append("- Pet Tuần Lộc 1 tháng\n");
                    sb.append("- 1 rương bạch ngân\n\n");
                    sb.append("Lưu ý: - Tối thiểu trên 1,000 điểm mới được nhận thưởng\n");
                    p.getService().showAlert("Phần thưởng", sb.toString());
                }));
                if (isEnded()) {
                    int ranking = getRanking(p, TOP_KILL_REINDEER_KING);
                    int point = p.getEventPoint().getPoint(TOP_KILL_REINDEER_KING);
                    if (ranking <= 10 && p.getEventPoint().getRewarded(TOP_KILL_REINDEER_KING) == 0 && point >= 1000) {
                        p.menus.add(new Menu(CMDMenu.EXECUTE, String.format("Nhận Thưởng TOP %d", ranking), () -> {
                            receiveReward(p, TOP_KILL_REINDEER_KING);
                        }));
                    }
                }
                p.getService().openUIMenu();
            }));

            p.getService().openUIMenu();
        }));
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Hướng dẫn", () -> {
            StringBuilder sb = new StringBuilder();
            sb.append("- Số lần trang trí: ").append(NinjaUtils.getCurrency(p.getEventPoint().getPoint(TOP_DECORATION_GIFT_BOX))).append("\n");
            sb.append("- Số bánh đã làm: ").append(NinjaUtils.getCurrency(p.getEventPoint().getPoint(TOP_CHOCOLATE_CAKE))).append("\n");
            sb.append("- Tiêu diệt vua tuần lộc: ").append(NinjaUtils.getCurrency(p.getEventPoint().getPoint(TOP_KILL_REINDEER_KING))).append("\n");
            sb.append("- Tiêu diệt người tuyết: ").append(NinjaUtils.getCurrency(p.getEventPoint().getPoint(TOP_KILL_SNOWMAN))).append("\n");
            sb.append("===CÔNG THỨC===").append("\n");
            sb.append("- Bánh khúc Dâu tây: 5 Bơ + 5 Kem + 5 Đường bột + 2 Dâu tây.").append("\n");
            sb.append("- Bánh khúc Chocolate: 5 Bơ + 5 Kem + 5 Đường bột + 1 Chocolate.").append("\n");
            sb.append("- Hộp quà: 3 Trái châu + 3 Ruy băng + 3 Chuông vàng + 20 Lượng.").append("\n");
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
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.CAY_THONG, 1426, 552, 0));
                break;
            case MapName.TRUONG_HARUNA:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.CAY_THONG, 502, 408, 0));
                break;
            case MapName.TRUONG_HIROSAKI:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.CAY_THONG, 1207, 168, 0));
                break;

            case MapName.LANG_TONE:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.CAY_THONG, 1427, 264, 0));
                break;

            case MapName.LANG_KOJIN:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.CAY_THONG, 621, 288, 0));
                break;

            case MapName.LANG_CHAI:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.CAY_THONG, 1804, 384, 0));
                break;

            case MapName.LANG_SANZU:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.CAY_THONG, 320, 288, 0));
                break;

            case MapName.LANG_CHAKUMI:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.CAY_THONG, 626, 312, 0));
                break;

            case MapName.LANG_ECHIGO:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.CAY_THONG, 360, 360, 0));
                break;

            case MapName.LANG_OSHIN:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.CAY_THONG, 921, 408, 0));
                break;

            case MapName.LANG_SHIIBA:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.CAY_THONG, 583, 408, 0));
                break;

            case MapName.LANG_FEARRI:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.CAY_THONG, 611, 312, 0));
                break;

            case MapName.RUNG_DAO_SAKURA:
                Mob monster = new Mob(zone.getMonsters().size(), (short) MobName.NGUOI_TUYET, 3000, (byte) 0, (short) 1928, (short) 240, false, true, zone);
                zone.addMob(monster);
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

        if (key == TOP_DECORATION_GIFT_BOX) {
            topDecorationGiftBox(ranking, p);
        } else if (key == TOP_CHOCOLATE_CAKE) {
            topChocolateCake(ranking, p);
        } else if (key == TOP_KILL_REINDEER_KING) {
            topKillReindeerKing(ranking, p);
        }
        p.getEventPoint().setRewarded(key, 1);
    }

    public void topDecorationGiftBox(int ranking, Char p) {
        Item mount = ItemFactory.getInstance().newItem(ItemName.PHUONG_HOANG_BANG);
        int tickId = p.gender == 1 ? ItemName.GAY_MAT_TRANG : ItemName.GAY_TRAI_TIM;
        Item fashionStick = ItemFactory.getInstance().newItem(tickId);
        Item tree = ItemFactory.getInstance().newItem(ItemName.TRUC_BACH_THIEN_LU);
        if (ranking == 1) {
            mount.options.add(new ItemOption(ItemOptionName.NE_DON_ADD_POINT_TYPE_1, 200));
            mount.options.add(new ItemOption(ItemOptionName.CHINH_XAC_ADD_POINT_TYPE_1, 100));
            mount.options.add(new ItemOption(ItemOptionName.TAN_CONG_KHI_DANH_CHI_MANG_POINT_PERCENT_TYPE_1, 100));
            mount.options.add(new ItemOption(ItemOptionName.CHI_MANG_ADD_POINT_TYPE_1, 100));

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
            fashionStick.expire = System.currentTimeMillis() + ConstTime.DAY * 90L;
            tree.setQuantity(3);
            p.addItemToBag(tree);
            for (int i = 0; i < 2; i++) {
                Item blueChest = ItemFactory.getInstance().newItem(ItemName.RUONG_BACH_NGAN);
                p.addItemToBag(blueChest);
            }
        } else {
            mount.expire = System.currentTimeMillis() + ConstTime.DAY * 30L;
            fashionStick.expire = System.currentTimeMillis() + ConstTime.DAY * 30L;
            Item blueChest = ItemFactory.getInstance().newItem(ItemName.RUONG_BACH_NGAN);
            p.addItemToBag(blueChest);
        }

        p.addItemToBag(mount);
        p.addItemToBag(fashionStick);
    }

    public void topChocolateCake(int ranking, Char p) {
        Item pet = ItemFactory.getInstance().newItem(ItemName.PET_UNG_LONG);
        int maskId = p.gender == 1 ? ItemName.SHIRAIJI : ItemName.HAJIRO;
        Item mask = ItemFactory.getInstance().newItem(maskId);
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
            mask.expire = System.currentTimeMillis() + ConstTime.DAY * 90L;
            tree.setQuantity(3);
            p.addItemToBag(tree);
            for (int i = 0; i < 2; i++) {
                Item blueChest = ItemFactory.getInstance().newItem(ItemName.RUONG_BACH_NGAN);
                p.addItemToBag(blueChest);
            }
        } else {
            pet.expire = System.currentTimeMillis() + ConstTime.DAY * 30L;
            mask.expire = System.currentTimeMillis() + ConstTime.DAY * 90L;
            Item blueChest = ItemFactory.getInstance().newItem(ItemName.RUONG_BACH_NGAN);
            p.addItemToBag(blueChest);
        }

        p.addItemToBag(pet);
        p.addItemToBag(mask);
    }

    public void topKillReindeerKing(int ranking, Char p) {
        Item pet = ItemFactory.getInstance().newItem(ItemName.TUAN_LOC);
        Item tree = ItemFactory.getInstance().newItem(ItemName.TRUC_BACH_THIEN_LU);
        if (ranking == 1) {
            pet.expire = -1;
            tree.setQuantity(10);
            p.addItemToBag(tree);
            for (int i = 0; i < 3; i++) {
                Item mysteryChest = ItemFactory.getInstance().newItem(ItemName.RUONG_HUYEN_BI);
                p.addItemToBag(mysteryChest);
            }
        } else if (ranking == 2) {
            pet.expire = -1;
            tree.setQuantity(5);
            p.addItemToBag(tree);
            Item mysteryChest = ItemFactory.getInstance().newItem(ItemName.RUONG_HUYEN_BI);
            p.addItemToBag(mysteryChest);
        } else if (ranking >= 3 && ranking <= 5) {
            pet.expire = System.currentTimeMillis() + ConstTime.DAY * 90L;
            tree.setQuantity(3);
            p.addItemToBag(tree);
            for (int i = 0; i < 2; i++) {
                Item blueChest = ItemFactory.getInstance().newItem(ItemName.RUONG_BACH_NGAN);
                p.addItemToBag(blueChest);
            }
        } else {
            pet.expire = System.currentTimeMillis() + ConstTime.DAY * 30L;
            Item blueChest = ItemFactory.getInstance().newItem(ItemName.RUONG_BACH_NGAN);
            p.addItemToBag(blueChest);
        }
        p.addItemToBag(pet);
    }

    @Override
    public void useItem(Char p, Item item) {
        switch (item.id) {
            case ItemName.BANH_KHUC_CAY_CHOCOLATE:
                if (p.getSlotNull() == 0) {
                    p.warningBagFull();
                    return;
                }
                useEventItem(p, item.id, itemsRecFromGoldItem);
                break;
            case ItemName.BANH_KHUC_CAY_DAU_TAY:
                if (p.getSlotNull() == 0) {
                    p.warningBagFull();
                    return;
                }
                useEventItem(p, item.id, itemsRecFromCoinItem);
                break;
            case ItemName.LAM_SON_DA:
            case ItemName.TRUC_BACH_THIEN_LU:
                if (p.getSlotNull() == 0) {
                    p.warningBagFull();
                    return;
                }
                useVipEventItem(p, item.id == ItemName.LAM_SON_DA ? 1 : 2, vipItems);
                p.removeItem(item.index, 1, true);
                break;
            case ItemName.LANH_DUOC:
                int time = 6 * 60 * 60 * 1000;
                short param = 2;
                byte templateID = 45;
                Effect eff = p.getEm().findByID(templateID);
                if (eff != null) {
                    eff.addTime(time);
                    p.getEm().setEffect(eff);
                } else {
                    Effect effect = new Effect(templateID, time, param);
                    effect.param2 = item.id;
                    p.getEm().setEffect(effect);
                }
                p.removeItem(item.index, 1, true);
                break;
            case ItemName.HOP_QUA_TRANG_TRI:
                if (p.getSlotNull() == 0) {
                    p.warningBagFull();
                    return;
                }
                Npc npc = p.zone.getNpc(NpcName.CAY_THONG);
                if (npc == null) {
                    p.serverMessage("Hãy lại gần Cây Thông để sử dụng.");
                    return;
                }
                int distance = NinjaUtils.getDistance(npc.cx, npc.cy, p.x, p.y);
                if (distance > 100) {
                    p.serverMessage("Hãy lại gần Cây Thông để sử dụng.");
                    return;
                }
                useEventItem(p, item.id, itemsRecFromGold2Item);
                if( p.duatop){
                p.getEventPoint().addPoint(Noel.TOP_DECORATION_GIFT_BOX, 1);
                }
                break;
                
            case ItemName.HOP_QUA_NOEL:
                
                break;
        }
    }

    public boolean isCoolTime() {
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        if (hour > 18 || hour < 6) {
            return true;
        }
        return false;
    }

}
