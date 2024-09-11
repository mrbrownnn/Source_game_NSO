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
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;


public class SumMer extends Event {

    public static final String TOP_LUCKY_CHARM = "lucky_charm";
    public static final String TOP_MAKE_VAI_CAKE = "chung_cake";
    public static final String MYSTERY_BOX_LEFT = "mystery_box";
    public static final String ENVELOPE = "envelope";
    private static final int MAKE_VAI_CAKE = 0;
    private static final int MAKE_GIAY_CAKE = 1;
    private static final int MAKE_KEM_CAKE = 2;
    private static final int MAKE_FIREWORK = 3;
    public RandomCollection<Integer> vipItems = new RandomCollection<>();
    private ZonedDateTime start, end;
    protected ReadWriteLock lock = new ReentrantReadWriteLock();
    protected ArrayList<Char> members = new ArrayList();

    public SumMer() {
        setId(Event.SUMMER);
        endTime.set(2025, Calendar.JANUARY, 15, 23, 59, 59);
        itemsThrownFromMonsters.add(5, ItemName.TRE);
        itemsThrownFromMonsters.add(5, ItemName.DAY);
        itemsThrownFromMonsters.add(2, ItemName.GIAY2);
        itemsThrownFromMonsters.add(2, ItemName.VAI);
        keyEventPoint.add(EventPoint.DIEM_TIEU_XAI);
        keyEventPoint.add(TOP_LUCKY_CHARM);
        keyEventPoint.add(TOP_MAKE_VAI_CAKE);
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

        itemsRecFromGold2Item.add(0.2, ItemName.HOA_KY_LAN);
        itemsRecFromGold2Item.add(1, ItemName.SHIRAIJI);
        itemsRecFromGold2Item.add(1, ItemName.HAJIRO);
        itemsRecFromGold2Item.add(2, ItemName.BACH_HO);
        itemsRecFromGold2Item.add(2, ItemName.LAN_SU_VU);
        itemsRecFromGold2Item.add(1, ItemName.PET_UNG_LONG);
        itemsRecFromGold2Item.add(2, ItemName.GAY_TRAI_TIM);
        itemsRecFromGold2Item.add(2, ItemName.GAY_MAT_TRANG);

        vipItems.add(0.2, ItemName.HOA_KY_LAN);
        vipItems.add(2, ItemName.BACH_HO);
        vipItems.add(2, ItemName.PET_UNG_LONG);
        vipItems.add(2, ItemName.HAKAIRO_YOROI);
        vipItems.add(2, ItemName.SHIRAIJI);
        vipItems.add(2, ItemName.HAJIRO);
        vipItems.add(4, ItemName.GAY_TRAI_TIM);
        vipItems.add(4, ItemName.GAY_MAT_TRANG);
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
            Npc npc = z.getNpc(NpcName.EM_BE);
            if (npc != null) {
                Bot bot = info.toBot(npc);
                GlobalService.getInstance().chat(bot.name,
                        "Help các con vợ!!!");
                z.join(bot);
            }
        }
    }
//item goso
    @Override
    public void initStore() {
        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(432)
                .itemID(ItemName.MAU_VE_THO_SO)
                .coin(20000)
                .expire(ConstTime.FOREVER)
                .build());
        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(433)
                .itemID(ItemName.MAU_VE_CAO_CAP)
                .gold(20)
                .expire(ConstTime.FOREVER)
                .build());
//        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
//                .id(428)
//                .itemID(ItemName.TRE)
//                .coin(10)
//                .expire(ConstTime.FOREVER)
//                .build());
//        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
//                .id(429)
//                .itemID(ItemName.DAY)
//                .coin(20)
//                .expire(ConstTime.FOREVER)
//                .build());
//        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
//                .id(430)
//                .itemID(ItemName.GIAY2)
//                .coin(20)
//                .expire(ConstTime.FOREVER)
//                .build());
//        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
//                .id(431)
//                .itemID(ItemName.VAI)
//                .coin(20)
//                .expire(ConstTime.FOREVER)
//                .build());
//        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
//                .id(902)
//                .itemID(ItemName.KEM_OC_QUE)
//                .coin(10)
//                .expire(ConstTime.FOREVER)
//                .build());
//        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
//                .id(903)
//                .itemID(ItemName.KEM_CHOCOLATE)
//                .coin(20)
//                .expire(ConstTime.FOREVER)
//                .build());
//        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
//                .id(904)
//                .itemID(ItemName.KEM_DAU)
//                .coin(20)
//                .expire(ConstTime.FOREVER)
//                .build());
//        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
//                .id(905)
//                .itemID(ItemName.KEM_SUA)
//                .coin(20)
//                .expire(ConstTime.FOREVER)
//                .build());
    }
    @Override
    public void action(Char p, int type, int amount) {
        if (isEnded()) {
            p.serverMessage("Sự kiện đã kết thúc");
            return;
        }
        switch (type) {
            case MAKE_VAI_CAKE:
                makeVaiCake(p, amount);
                break;
            case MAKE_GIAY_CAKE:
                makeGiayCake(p, amount);
                break;
            case MAKE_KEM_CAKE:
                makeKemCake(p, amount);
                break;
           /* case MAKE_FIREWORK:
                makeFirework(p, amount);
                break;*/
        }
    }

    private void makeVaiCake(Char p, int amount) {
        int[][] itemRequires = new int[][] { { ItemName.TRE, 3 }, { ItemName.DAY, 3 }, { ItemName.VAI, 3 },{ItemName.MAU_VE_CAO_CAP,1}};
        int itemIdReceive = ItemName.DIEU_VAI;
        boolean isDone = makeEventItem(p, amount, itemRequires, 0, 0, 0, itemIdReceive);
        if (isDone) {
            p.getEventPoint().addPoint(SumMer.TOP_MAKE_VAI_CAKE, amount);
            p.getEventPoint().addPoint(EventPoint.DIEM_TIEU_XAI, amount);
           
         
        }
    }

    private void makeGiayCake(Char p, int amount) {
        int[][] itemRequires = new int[][] { { ItemName.TRE, 3 }, { ItemName.DAY, 3 },{ItemName.GIAY2, 3},{ItemName.MAU_VE_THO_SO, 1}};
        int itemIdReceive = ItemName.DIEU_GIAY;
        makeEventItem(p, amount, itemRequires, 0, 0, 0, itemIdReceive);
    }
    
    private void makeKemCake(Char p, int amount) {
        int[][] itemRequires = new int[][] { { ItemName.KEM_OC_QUE, 3 }, { ItemName.KEM_CHOCOLATE, 3 }, { ItemName.KEM_DAU, 3 },{ ItemName.KEM_SUA, 3 } };
        int itemIdReceive = ItemName.HU_KEM_DAM;
        makeEventItem(p, amount, itemRequires, 0, 500000, 0, itemIdReceive);
    }
   

    @Override
    public void menu(Char p) {
      //  p.menus.clear();
         // p.menus.add(new Menu(CMDMenu.EXECUTE, "Làm Diều", () -> {
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Diều vải ", () -> {
                p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Diều vải", () -> {
                    InputDialog input = p.getInput();
                    try {
                        int number = input.intValue();
                        action(p, MAKE_VAI_CAKE, number);
                    } catch (NumberFormatException e) {
                        if (!input.isEmpty()) {
                            p.inputInvalid();
                        }
                    }
                }));
                p.getService().showInputDialog();
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Diều giấy", () -> {
                p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Diều giấy", () -> {
                    InputDialog input = p.getInput();
                    try {
                        int number = input.intValue();
                        action(p, MAKE_GIAY_CAKE, number);
                    } catch (NumberFormatException e) {
                        if (!input.isEmpty()) {
                            p.inputInvalid();
                        }
                    }
                }));
                p.getService().showInputDialog();
          //  }));
          //  p.getService().openUIMenu();
        }));
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Làm Hũ Kem Dầm", () -> {
            p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Làm hũ kem", () -> {
                InputDialog input = p.getInput();
                try {
                    int number = input.intValue();
                    action(p, MAKE_KEM_CAKE, number);
                } catch (NumberFormatException e) {
                    if (!input.isEmpty()) {
                        p.inputInvalid();
                    }
                }
            }));
            p.getService().showInputDialog();
        }));

        p.menus.add(new Menu(CMDMenu.EXECUTE, "Đua TOP", () -> {
            p.menus.clear();
            /*p.menus.add(new Menu(CMDMenu.EXECUTE, "Cho bé ăn", () -> {
                p.menus.clear();
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Bảng xếp hạng", () -> {
                    viewTop(p, TOP_LUCKY_CHARM, "Cho bé ăn", "%d. %s đã cho ăn %s lần");
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
            }));*/
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Thợ làm Diều", () -> {
                p.menus.clear();
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Bảng xếp hạng", () -> {
                    viewTop(p, TOP_MAKE_VAI_CAKE, "Thợ làm Diều", "%d. %s đã làm %s chiếc Diều");
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
                    int ranking = getRanking(p, TOP_MAKE_VAI_CAKE);
                    if (ranking <= 10 && p.getEventPoint().getRewarded(TOP_MAKE_VAI_CAKE) == 0) {
                        p.menus.add(new Menu(CMDMenu.EXECUTE, String.format("Nhận Thưởng TOP %d", ranking), () -> {
                            receiveReward(p, TOP_MAKE_VAI_CAKE);
                        }));
                    }
                }
                p.getService().openUIMenu();
            }));

            p.getService().openUIMenu();
        }));
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Hướng dẫn", () -> {
            StringBuilder sb = new StringBuilder();
            /*sb.append("- Số lần cho bé ăn: ")
                    .append(NinjaUtils.getCurrency(p.getEventPoint().getPoint(TOP_LUCKY_CHARM))).append("\n");
           */
              sb.append("- Số diều đã làm: ")
                    .append(NinjaUtils.getCurrency(p.getEventPoint().getPoint(TOP_MAKE_VAI_CAKE))).append("\n");
            sb.append("===CÔNG THỨC===").append("\n");
            sb.append("- Diều Vải: 3 Tre + 3 Dây + 3 Vải + 1 Màu vẽ cao cấp = 1 Diều Vải.").append("\n");
            sb.append("- Diều Giấy: 3 Tre + 3 Dây + 3 Giấy +  Màu vẽ thô sơ = 1 Diều Giấy.").append("\n");
            sb.append("- Hũ Kem Dầm: 3 Kem ốc quế + 3 Kem chocolate + 3 Kem dâu + 3 Kem sữa + 100k xu = 1 Hũ kem dầm.").append("\n");
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
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.EM_BE, 1426, 552, 0));
                break;
             case MapName.TRUONG_HARUNA:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.EM_BE, 502, 408, 0));
                break;
            case MapName.TRUONG_HIROSAKI:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.EM_BE, 1207, 168, 0));
                break;

           /* case MapName.LANG_TONE:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.EM_BE, 1427, 264, 0));
                break;

            case MapName.LANG_KOJIN:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.EM_BE, 621, 288, 0));
                break;

            case MapName.LANG_CHAI:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.EM_BE, 1804, 384, 0));
                break;

            case MapName.LANG_SANZU:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.EM_BE, 320, 288, 0));
                break;

            case MapName.LANG_CHAKUMI:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.EM_BE, 626, 312, 0));
                break;

            case MapName.LANG_ECHIGO:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.EM_BE, 360, 360, 0));
                break;

            case MapName.LANG_OSHIN:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.EM_BE, 921, 408, 0));
                break;

            case MapName.LANG_SHIIBA:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.EM_BE, 583, 408, 0));
                break;

            case MapName.LANG_FEARRI:
                zone.addNpc(NpcFactory.getInstance().newNpc(99, NpcName.EM_BE, 611, 312, 0));
                break;
*/
            case MapName.CANH_DONG_FUKI:
                if (zone != null){
                int time = 3 * 60 * 60 * 1000;
                if (zone.id == (int)NinjaUtils.nextInt(5, 29)) {
                Mob monster = new Mob(zone.getMonsters().size(), (short) MobName.TU_LOI_DIEU_THIEN_LONG, 1000000000, (byte) 10,
                        (short) 3355, (short) 240, false, true, zone);
                zone.addMob(monster);
                }}
                break;
            case MapName.RUNG_DAO_SAKURA:
                if (zone != null){
                int time1 = 3 * 60 * 60 * 1000;
                if (zone.id == (int)NinjaUtils.nextInt(5, 29)) {
                 Mob monster = new Mob(zone.getMonsters().size(), (short) MobName.TU_LOI_DIEU_THIEN_LONG, 1000000000, (byte) 100,
                            (short) 1928, (short) 240, false, true, zone);
                    zone.addMob(monster);
                }}
                break;
                
                case MapName.HANG_KARASUMORI_92:
                Calendar calendar = Calendar.getInstance();
int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
if (currentHour >= 12 && currentHour <= 13) { //giờ săn boss
            if (zone != null) {
                if (zone.id == 0) {
                    Mob monster = new Mob(zone.getMonsters().size(), (short) MobName.Juubi_Shinju, 700000000, (short) 200,
                            (short) 771, (short) 240, false, true, zone);
                    zone.addMob(monster);
                }
            }
            break;
    
} else if (currentHour > 13) { //giờ đóng map
    close(); 
}
         break;       
        }
    }
    
    public List<Char> getMembers() {
        lock.readLock().lock();
        try {
            return members.stream().distinct().collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public void close() {
        List<Char> members = getMembers();
        for (Char _char : members) {
            try {
                if (_char.isCleaned) {
                    continue;
                }
                short[] xy = NinjaUtils.getXY(_char.mapBeforeEnterPB);
                _char.setXY(xy[0], xy[1]);
                _char.changeMap(_char.mapBeforeEnterPB);
                _char.serverMessage("Cửa hang vĩ thú đã được khép lại.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //super.close();
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
        } else if (key == TOP_MAKE_VAI_CAKE) {
            topMakeVaiCake(ranking, p);
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
//top dv
    public void topMakeVaiCake(int ranking, Char p) {
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
            case ItemName.DIEU_VAI:
                if (p.getSlotNull() == 0) {
                    p.warningBagFull();
                    return;
                }
                useEventItem(p, item.id, itemsRecFromGoldItem);
                break;
            case ItemName.DIEU_GIAY:
                if (p.getSlotNull() == 0) {
                    p.warningBagFull();
                    return;
                }
                useEventItem(p, item.id, itemsRecFromCoinItem);
                break;
            case ItemName.HU_KEM_DAM:
                if (p.getSlotNull() == 0) {
                    p.warningBagFull();
                    return;
                }
                Npc npc = p.zone.getNpc(NpcName.EM_BE);
                if (npc == null) {
                    p.serverMessage("Hãy đi tìm Em Bé để sử dụng.");
                    return;
                }
                int distance = NinjaUtils.getDistance(npc.cx, npc.cy, p.x, p.y);
                if (distance > 100) {
                    p.serverMessage("Hãy đi tìm Em Bé để sử dụng.");
                    return;
                }
                useEventItem(p, item.id, itemsRecFromGold2Item);
                p.getEventPoint().addPoint(SumMer.TOP_LUCKY_CHARM, 1);
                p.getEventPoint().addPoint(EventPoint.DIEM_TIEU_XAI, 1);
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
