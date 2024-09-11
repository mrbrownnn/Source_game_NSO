
package com.nsoz.event;

import com.nsoz.constants.CMDInputDialog;
import com.nsoz.constants.CMDMenu;
import com.nsoz.constants.ConstTime;
import com.nsoz.constants.ItemName;
import com.nsoz.constants.ItemOptionName;
import com.nsoz.constants.MapName;
import com.nsoz.constants.MobName;
import com.nsoz.constants.NpcName;
import com.nsoz.effect.Effect;
import com.nsoz.effect.EffectAutoDataManager;
import com.nsoz.event.eventpoint.EventPoint;
import com.nsoz.item.Item;
import com.nsoz.item.ItemFactory;
import com.nsoz.lib.RandomCollection;
import com.nsoz.map.Map;
import com.nsoz.map.Tree;
import com.nsoz.map.zones.Zone;
import com.nsoz.mob.Mob;
import com.nsoz.model.Char;
import com.nsoz.model.InputDialog;
import com.nsoz.model.Menu;
import com.nsoz.option.ItemOption;
import com.nsoz.store.ItemStore;
import com.nsoz.store.StoreManager;
import com.nsoz.util.NinjaUtils;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 *
 * Được share bởi Youtube : nsotien tv
 */
public class Halloween extends Event {

    private static final int HOP_MA_QUY = 0;
    private static final int KEO_TAO = 1;
    private static final int CHIA_KHOA = 2;
    private static final int MA_VAT = 3;
    private static final int THOI_TRANG = 4;

    public static final String TOP_DEVIL_BOX = "devil_box";
    public static final String INVITATION_NUMBER = "invitation_number";

    private RandomCollection<Integer> rd = new RandomCollection<>();
    private RandomCollection<Integer> vipItems = new RandomCollection<>();
    protected RandomCollection<Integer> itemsThrownFromMonsters2;
    private ZonedDateTime start, end;
    protected ReadWriteLock lock = new ReentrantReadWriteLock();
    protected ArrayList<Char> members = new ArrayList();

    public Halloween() {

        setId(Event.HALLOWEEN);

        endTime.set(2023, 11, 18, 23, 59, 59);

        keyEventPoint.add(EventPoint.DIEM_TIEU_XAI);
        keyEventPoint.add(TOP_DEVIL_BOX);
        keyEventPoint.add(INVITATION_NUMBER);

        itemsThrownFromMonsters.add(1, ItemName.QUA_TAO);
        itemsThrownFromMonsters.add(3, ItemName.MAT_ONG);
        itemsThrownFromMonsters.add(2, ItemName.TAN_LINH);
        itemsThrownFromMonsters.add(5, ItemName.XUONG_THU);
        itemsThrownFromMonsters.add(10, -1);// ko rơi

        itemsThrownFromMonsters2 = new RandomCollection<>();
        itemsThrownFromMonsters2.add(1, ItemName.H);
        itemsThrownFromMonsters2.add(1, ItemName.A);
        itemsThrownFromMonsters2.add(2, ItemName.L);
        itemsThrownFromMonsters2.add(1, ItemName.O);
        itemsThrownFromMonsters2.add(1, ItemName.W);
        itemsThrownFromMonsters2.add(2, ItemName.E);
        itemsThrownFromMonsters2.add(1, ItemName.N);

        // rd.add(1, ItemName.MAT_NA_SHIN_AH);
        // rd.add(1, ItemName.MAT_NA_ONI);
        // rd.add(1, ItemName.MAT_NA_VO_DIEN);
        // rd.add(1, ItemName.MAT_NA_KUMA);
        // rd.add(1, ItemName.MAT_NA_INU);
        rd.add(1, ItemName.SHIRAIJI);
        rd.add(1, ItemName.HAJIRO);

        itemsRecFromGoldItem.add(5, ItemName.TA_LINH_MA);
        itemsRecFromGoldItem.add(5, ItemName.PHONG_THUONG_MA);
        itemsRecFromGoldItem.add(5, ItemName.XICH_TU_MA);
        itemsRecFromGoldItem.add(2, ItemName.MAT_NA_SHIN_AH);
        itemsRecFromGoldItem.add(2, ItemName.MAT_NA_VO_DIEN);
        itemsRecFromGoldItem.add(2, ItemName.MAT_NA_ONI);
        itemsRecFromGoldItem.add(2, ItemName.MAT_NA_KUMA);
        itemsRecFromGoldItem.add(2, ItemName.MAT_NA_INU);

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

        itemsRecFromGold2Item.add(5, ItemName.TA_LINH_MA);
        itemsRecFromGold2Item.add(5, ItemName.PHONG_THUONG_MA);
        itemsRecFromGold2Item.add(5, ItemName.XICH_TU_MA);
        itemsRecFromGold2Item.add(2, ItemName.MAT_NA_SHIN_AH);
        itemsRecFromGold2Item.add(2, ItemName.MAT_NA_VO_DIEN);
        itemsRecFromGold2Item.add(2, ItemName.MAT_NA_ONI);
        itemsRecFromGold2Item.add(2, ItemName.MAT_NA_KUMA);
        itemsRecFromGold2Item.add(2, ItemName.MAT_NA_INU);

        vipItems.add(1, ItemName.TA_LINH_MA);
        vipItems.add(1, ItemName.PHONG_THUONG_MA);
        vipItems.add(1, ItemName.XICH_TU_MA);
        vipItems.add(2, ItemName.MAT_NA_SHIN_AH);
        vipItems.add(2, ItemName.MAT_NA_VO_DIEN);
        vipItems.add(2, ItemName.MAT_NA_ONI);
        vipItems.add(2, ItemName.MAT_NA_KUMA);
        vipItems.add(2, ItemName.MAT_NA_INU);
        vipItems.add(2, ItemName.HAKAIRO_YOROI);
        vipItems.add(2, ItemName.LAN_SU_VU);
    }

    @Override
    public void useItem(Char _char, Item item) {
        if (item.id == ItemName.THU_MOI_LE_HOI) {
            _char.getEventPoint().addPoint(INVITATION_NUMBER, 1);
            _char.serverMessage(
                    "Số lượt tham gia lễ hội hoá trang: " + _char.getEventPoint().find(INVITATION_NUMBER).getPoint());
            _char.removeItem(item.index, 1, true);
        } else if (item.id == ItemName.BI_MA) {
            int time = 8 * 60 * 60 * 1000;
            short param = 2;
            byte templateID = 43;
            Effect eff = _char.getEm().findByID(templateID);
            if (eff != null) {
                eff.addTime(time);
                _char.getEm().setEffect(eff);
            } else {
                Effect effect = new Effect(templateID, time, param);
                effect.param2 = item.id;
                _char.getEm().setEffect(effect);
            }
            _char.removeItem(item.index, 1, true);
        } else if (item.id == ItemName.KEO_TAO) {
            if (_char.getSlotNull() == 0) {
                _char.warningBagFull();
                return;
            }else{
            if((int)NinjaUtils.nextInt(100) > 40){
             useEventItem(_char, item.id, itemsRecFromCoinItem);
            }else{
                long exp = (int)NinjaUtils.nextInt(1, 1000) * 1000;
            _char.addExp(exp);
            _char.serverMessage("Bạn Nhận Được "+exp+" exp");
            _char.removeItem(item.index, 1, true);
            }
            }
        } else if (item.id == ItemName.HOP_MA_QUY) {
            int indexItem = _char.getIndexItemByIdInBag(ItemName.CHIA_KHOA);
            if (indexItem == -1) {
                _char.serverMessage("Cần có chìa khoá mới có thể mở hộp ma quỷ");
                return;
            }
            if (_char.getSlotNull() == 0) {
                _char.warningBagFull();
                return;
            }else{
            if((int)NinjaUtils.nextInt(100) < 40){
                long exp = (int)NinjaUtils.nextInt(1, 1000) * 10000;
            _char.addExp(exp);
            _char.serverMessage("Bạn Nhận Được "+exp+" exp");
            _char.removeItem(item.index, 1, true);
            }else{
            int[][] itemRequires = new int[][]{{ItemName.HOP_MA_QUY, 1}, {ItemName.CHIA_KHOA, 1}};
            boolean isDone = useEventItem(_char, 1, itemRequires, 0, 0, 0, itemsRecFromGoldItem);
            _char.getEventPoint().addPoint(TOP_DEVIL_BOX, 1);}
            }
        } else if (item.id == ItemName.GAY_PHEP || item.id == ItemName.CHOI_BAY) {
            if (_char.getSlotNull() == 0) {
                _char.warningBagFull();
                return;
            }
            Event.useVipEventItem(_char, item.id == ItemName.GAY_PHEP ? 1 : 2, vipItems);
            _char.removeItem(item.index, 1, true);
        }
    }

    public int randomItemID2() {
        return itemsThrownFromMonsters2.next();
    }

    @Override
    public void action(Char p, int type, int amount) {
        if (isEnded()) {
            p.serverMessage("Sự kiện đã kết thúc");
            return;
        }
        switch (type) {
            case CHIA_KHOA:
                makeKey(p, amount);
                break;

            case HOP_MA_QUY:
                makeDevilBox(p, amount);
                break;

            case KEO_TAO:
                makeAppleCandy(p, amount);
                break;

            case MA_VAT:
                makeMagicItem(p, amount);
                break;

            case THOI_TRANG:
                makeFashionItem(p);
                break;
        }
    }

    public void makeDevilBox(Char p, int amount) {
        int[][] itemRequires = new int[][]{{ItemName.XUONG_THU, 5}, {ItemName.TAN_LINH, 2},
        {ItemName.MA_VAT, 1}};
        int itemIdReceive = ItemName.HOP_MA_QUY;
        boolean isDone = makeEventItem(p, amount, itemRequires, 20, 0, 0, itemIdReceive);
        if (isDone) {
            p.getEventPoint().addPoint(EventPoint.DIEM_TIEU_XAI, amount);
        }
    }

    public void makeAppleCandy(Char p, int amount) {
        int[][] itemRequires = new int[][]{{ItemName.QUA_TAO, 1}, {ItemName.MAT_ONG, 3}};
        int itemIdReceive = ItemName.KEO_TAO;
        makeEventItem(p, amount, itemRequires, 0, 100000, 0, itemIdReceive);
    }

    public void makeKey(Char p, int amount) {
        int[][] itemRequires = new int[][]{{ItemName.H, 1}, {ItemName.A, 1}, {ItemName.L, 2}, {ItemName.O, 1},
        {ItemName.W, 1}, {ItemName.E, 2}, {ItemName.N, 1}};
        int itemIdReceive = ItemName.CHIA_KHOA;
        makeEventItem(p, amount, itemRequires, 0, 0, 0, itemIdReceive);
    }

    public void makeMagicItem(Char p, int amount) {
        int[][] itemRequires = new int[][]{{ItemName.H, 1}, {ItemName.A, 1}, {ItemName.L, 2}, {ItemName.O, 1},
        {ItemName.W, 1}, {ItemName.E, 2}, {ItemName.N, 1}};
        int itemIdReceive = ItemName.MA_VAT;
        makeEventItem(p, amount, itemRequires, 0, 0, 0, itemIdReceive);
    }

    public void makeFashionItem(Char p) {
        if (p.user.gold < 500) {
            p.getService().npcChat(NpcName.TIEN_NU, "Cần 500 lượng để đổi.");
            return;
        }
        int index = p.getIndexItemByIdInBag(ItemName.KEO_TAO);
        Item itm = null;
        if (index != -1) {
            itm = p.bag[index];
        }
        if (itm == null || !itm.has(50)) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ kẹo táo.");
            return;
        }
        p.addGold(-500);
        p.removeItem(index, 50, true);
        int maskId = p.gender == 1 ? ItemName.SHIRAIJI : ItemName.HAJIRO;
        Item item = ItemFactory.getInstance().newItem(maskId);
        item.expire = System.currentTimeMillis() + (long) (86400000 * 15);
        item.isLock = true;
        p.addItemToBag(item);
    }

    public void makeMagicWeapon(Char p, int type) {
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

        Item item = ItemFactory.getInstance().newItem(type == 1 ? ItemName.GAY_PHEP : ItemName.CHOI_BAY);
        p.addItemToBag(item);
        p.getEventPoint().subPoint(EventPoint.DIEM_TIEU_XAI, point);
    }

    @Override
    public void menu(Char p) {
        if (!isEnded()) {
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Làm Hộp ma quỷ", () -> {
                p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Hộp ma quỷ", () -> {
                    InputDialog input = p.getInput();
                    try {
                        int number = input.intValue();
                        action(p, HOP_MA_QUY, number);
                    } catch (Exception e) {
                        if (!input.isEmpty()) {
                            p.inputInvalid();
                        }
                    }
                }));
                p.getService().showInputDialog();
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Làm Kẹo táo", () -> {
                p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Kẹo táo", () -> {
                    InputDialog input = p.getInput();
                    try {
                        int number = input.intValue();
                        action(p, KEO_TAO, number);
                    } catch (Exception e) {
                        if (!input.isEmpty()) {
                            p.inputInvalid();
                        }
                    }
                }));
                p.getService().showInputDialog();
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Đổi chìa khóa", () -> {
                p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Chìa khóa", () -> {
                    InputDialog input = p.getInput();
                    try {
                        int number = input.intValue();
                        action(p, CHIA_KHOA, number);
                    } catch (Exception e) {
                        if (!input.isEmpty()) {
                            p.inputInvalid();
                        }
                    }
                }));
                p.getService().showInputDialog();
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Đổi ma vật", () -> {
                p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Ma vật", () -> {
                    InputDialog input = p.getInput();
                    try {
                        int number = input.intValue();
                        action(p, MA_VAT, number);
                    } catch (Exception e) {
                        if (!input.isEmpty()) {
                            p.inputInvalid();
                        }
                    }
                }));
                p.getService().showInputDialog();
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Đổi đồ thời trang", () -> {
                action(p, THOI_TRANG, 1);
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Pháp khí", () -> {
                p.menus.clear();
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Gậy phép", () -> {
                    makeMagicWeapon(p, 1);
                }));
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Chổi bay", () -> {
                    makeMagicWeapon(p, 2);
                }));
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Điểm sự kiện", () -> {
                    p.getService().showAlert("Hướng dẫn", "- Điểm sự kiện: "
                            + NinjaUtils.getCurrency(p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI))
                            + "\n\nBạn có thể quy đổi điểm sự kiện như sau\n- Gậy phép: 5.000 điểm\n- Chổi bay: 20.000 điểm\n");
                }));
                p.getService().openUIMenu();
            }));
        }
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Đua TOP", () -> {
            p.menus.clear();
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Hộp Ma Quỷ", () -> {
                viewTop(p, TOP_DEVIL_BOX, "Hộp Ma Quỷ", "%d. %s đã mở %s hộp ma quỷ");
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Phần Thưởng", () -> {
                StringBuilder sb = new StringBuilder();
                sb.append("Top 1:").append("\n");
                sb.append("- Xích Tử Mã v.v MCS\n");
                sb.append("- SHIRAIJI/HAJIRO v.v MCS\n");
                sb.append("- 3 rương huyền bí\n");
                sb.append("- 10 pháp khí\n\n");
                sb.append("Top 2:").append("\n");
                sb.append("- Xích Tử Mã v.v\n");
                sb.append("- SHIRAIJI/HAJIRO v.v\n");
                sb.append("- 1 rương huyền bí\n");
                sb.append("- 5 pháp khí\n\n");
                sb.append("Top 3 - 5:").append("\n");
                sb.append("- Xích Tử Mã 3 tháng\n");
                sb.append("- SHIRAIJI/HAJIRO 3 tháng\n");
                sb.append("- 2 rương bạch ngân\n");
                sb.append("- 3 pháp khí\n\n");
                sb.append("Top 6 - 10:").append("\n");
                sb.append("- Xích Tử Mã 1 tháng\n");
                sb.append("- 1 rương bạch ngân\n");
                p.getService().showAlert("Phần thưởng", sb.toString());
            }));
            if (isEnded()) {
                int ranking = getRanking(p, TOP_DEVIL_BOX);
                if (ranking <= 10 && p.getEventPoint().getRewarded(TOP_DEVIL_BOX) == 0) {
                    p.menus.add(new Menu(CMDMenu.EXECUTE, String.format("Nhận Thưởng TOP %d", ranking), () -> {
                        receiveReward(p, TOP_DEVIL_BOX);
                    }));
                }
            }
            p.getService().openUIMenu();
        }));
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Hướng dẫn", () -> {
            StringBuilder sb = new StringBuilder();
            sb.append("- Điểm tiêu xài: ")
                    .append(NinjaUtils.getCurrency(p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI))).append("\n");
            sb.append("- Cách làm Hộp ma quỷ: 5 xương thú + 2 tàn linh + 1 ma vật.").append("\n");
            sb.append("- Cách làm ma vật: Đánh quái truy tìm các chữ, H, L, O , W, E ,N để đổi.").append("\n");
            sb.append("- Cách làm kẹo táo: Quả táo + 3 Mật ong + 100.000 xu.").append("\n");
            sb.append("- Cách đổi chìa khóa: 1 bộ HALLOWEEN.").append("\n");
            sb.append("- Cách đổi ma vật: 1 bộ HALLOWEEN.").append("\n");
            sb.append("- Đổi đồ thời trang: 50 kẹo táo + 500 lượng");
            p.getService().showAlert("Hướng Dẫn", sb.toString());
        }));
    }

    @Override
    public void initStore() {
        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(998)
                .itemID(ItemName.BI_MA)
                .gold(20)
                .expire(ConstTime.FOREVER)
                .build());

        List<ItemOption> options = new ArrayList<ItemOption>();
        options.add(new ItemOption(ItemOptionName.HP_TOI_DA_POINT_TYPE_1, (int)NinjaUtils.nextInt(4000, 5000)));
        options.add(new ItemOption(ItemOptionName.TAN_CONG_POINT_TYPE_1, (int)NinjaUtils.nextInt(4000, 5000)));
        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1000)
                .itemID(ItemName.BI_RE_HANH)
                .gold(500)
                .expire(ConstTime.WEEK)
                .options(options)
                .build());

        List<ItemOption> options2 = new ArrayList<ItemOption>();
        options2.add(new ItemOption(ItemOptionName.HP_TOI_DA_POINT_TYPE_1, 2000));
        options2.add(new ItemOption(ItemOptionName.CONG_THEM_TIEM_NANG_ADD_POINT_PERCENT_TYPE_0, 25));
        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1001)
                .itemID(ItemName.JACK_HOLLOW)
                .gold(500)
                .expire(ConstTime.WEEK)
                .options(options2)
                .build());

        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1002)
                .itemID(ItemName.THU_MOI_LE_HOI)
                .gold(20)
                .expire(ConstTime.FOREVER)
                .build());
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

        Item mount = ItemFactory.getInstance().newItem(ItemName.XICH_TU_MA);
        Item choiBay = ItemFactory.getInstance().newItem(ItemName.CHOI_BAY);
        int maskId = p.gender == 1 ? ItemName.SHIRAIJI : ItemName.HAJIRO;
        Item mask = ItemFactory.getInstance().newItem(maskId);

        if (ranking == 1) {
            mount.options.add(new ItemOption(ItemOptionName.NE_DON_ADD_POINT_TYPE_1, 200));
            mount.options.add(new ItemOption(ItemOptionName.CHINH_XAC_ADD_POINT_TYPE_1, 100));
            mount.options.add(new ItemOption(ItemOptionName.TAN_CONG_KHI_DANH_CHI_MANG_POINT_PERCENT_TYPE_1, 100));
            mount.options.add(new ItemOption(ItemOptionName.CHI_MANG_ADD_POINT_TYPE_1, 100));
            mount.options.add(new ItemOption(ItemOptionName.CONG_THEM_TIEM_NANG_ADD_POINT_PERCENT_TYPE_0, 20));
            mount.options.add(new ItemOption(ItemOptionName.TAN_CONG_ADD_POINT_PERCENT_TYPE_8, 20));

            mask.options.add(new ItemOption(125, 3000));
            mask.options.add(new ItemOption(117, 3000));
            mask.options.add(new ItemOption(94, 10));
            mask.options.add(new ItemOption(136, 20));
            mask.options.add(new ItemOption(127, 10));
            mask.options.add(new ItemOption(130, 10));
            mask.options.add(new ItemOption(131, 10));

            choiBay.setQuantity(10);
            p.addItemToBag(choiBay);
            for (int i = 0; i < 3; i++) {
                Item mysteryChest = ItemFactory.getInstance().newItem(ItemName.RUONG_HUYEN_BI);
                p.addItemToBag(mysteryChest);
            }
        } else if (ranking == 2) {
            choiBay.setQuantity(5);
            p.addItemToBag(choiBay);
            Item mysteryChest = ItemFactory.getInstance().newItem(ItemName.RUONG_HUYEN_BI);
            p.addItemToBag(mysteryChest);
        } else if (ranking >= 3 && ranking <= 5) {
            mount.expire = System.currentTimeMillis() + 86400000 * 90;
            choiBay.setQuantity(3);
            p.addItemToBag(choiBay);
            for (int i = 0; i < 2; i++) {
                Item blueChest = ItemFactory.getInstance().newItem(ItemName.RUONG_HUYEN_BI);
                p.addItemToBag(blueChest);
            }
        } else {
            mount.expire = System.currentTimeMillis() + 86400000 * 30;
            Item blueChest = ItemFactory.getInstance().newItem(ItemName.RUONG_HUYEN_BI);
            p.addItemToBag(blueChest);
        }

        p.addItemToBag(mount);
        p.addItemToBag(mask);

        p.getEventPoint().setRewarded(key, 1);
    }

    @Override
    public void initMap(Zone zone) {
        Map map = zone.map;
        int mapID = map.id;
        switch (mapID) {
            case MapName.KHU_LUYEN_TAP:
                break;
            case MapName.TRUONG_OOKAZA:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_HALLOWEEN).x((short) 1426).y((short) 552).build());
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_HALLOWEEN).x((short) 784).y((short) 648).build());
                break;
            case MapName.TRUONG_HARUNA:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_HALLOWEEN).x((short) 502).y((short) 408).build());
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_HALLOWEEN).x((short) 1863).y((short) 360).build());
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_HALLOWEEN).x((short) 2048).y((short) 360).build());
                break;
            case MapName.TRUONG_HIROSAKI:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_HALLOWEEN).x((short) 1207).y((short) 168).build());
                break;

            case MapName.LANG_TONE:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_HALLOWEEN).x((short) 1427).y((short) 264).build());
                break;

            case MapName.LANG_KOJIN:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_HALLOWEEN).x((short) 621).y((short) 288).build());
                break;

            case MapName.LANG_CHAI:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_HALLOWEEN).x((short) 1804).y((short) 384).build());
                break;

            case MapName.LANG_SANZU:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_HALLOWEEN).x((short) 320).y((short) 288).build());
                break;

            case MapName.LANG_CHAKUMI:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_HALLOWEEN).x((short) 626).y((short) 312).build());
                break;

            case MapName.LANG_ECHIGO:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_HALLOWEEN).x((short) 360).y((short) 360).build());
                break;

            case MapName.LANG_OSHIN:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_HALLOWEEN).x((short) 921).y((short) 408).build());
                break;

            case MapName.LANG_SHIIBA:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_HALLOWEEN).x((short) 583).y((short) 408).build());
                break;

            case MapName.LANG_FEARRI:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_HALLOWEEN).x((short) 611).y((short) 312).build());
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
            closeMapViThu(); 
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
    
    public void closeMapViThu() {
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
}
