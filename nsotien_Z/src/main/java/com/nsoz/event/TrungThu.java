
package com.nsoz.event;

import com.nsoz.constants.*;
import com.nsoz.effect.EffectAutoDataManager;
import com.nsoz.event.eventpoint.EventPoint;
import com.nsoz.item.Item;
import com.nsoz.item.ItemFactory;
import com.nsoz.item.ItemManager;
import com.nsoz.lib.RandomCollection;
import com.nsoz.map.Map;
import com.nsoz.map.Tree;
import com.nsoz.map.zones.Zone;
import com.nsoz.model.Char;
import com.nsoz.model.InputDialog;
import com.nsoz.model.Menu;
import com.nsoz.model.RandomItem;
import com.nsoz.option.ItemOption;
import com.nsoz.store.ItemStore;
import com.nsoz.store.StoreManager;
import com.nsoz.util.NinjaUtils;

import java.util.Calendar;
import java.util.List;

/**
 *
 * Được share bởi Youtube : nsotien tv
 */
public class TrungThu extends Event {

    private static final int DOI_BACH_HO = 0;
    private static final int VU_KHI_THOI_TRANG_7_NGAY = 1;
    private static final int VU_KHI_THOI_TRANG_30_NGAY = 2;
    private static final int QUA_DAC_BIET = 9;
    private static final int BANH_THAP_CAM = 3;
    private static final int BANH_DEO = 4;
    private static final int BANH_DAU_XANH = 5;
    private static final int BANH_PIA = 6;
    private static final int HOP_BANH_THUONG = 7;
    private static final int HOP_BANH_THUONG_HANG = 8;
    public static final int HOA_PHUC_SINH = 9;

    public static final String TOP_LONG_DEN = "release_lanterns";
    public static final String TOP_BANH_TRUNG_THU = "use_moon_cake";

    public static final long EXPIRE_7_DAY = 604800000L;
    public static final long EXPIRE_30_DAY = 2592000000L;

    public TrungThu() {
        setId(Event.TRUNG_THU);
        endTime.set(2023, 10, 1, 1, 37, 59);
        itemsThrownFromMonsters.add(1, ItemName.TRUNG);
        itemsThrownFromMonsters.add(2, ItemName.BOT_MI);
        itemsThrownFromMonsters.add(1, ItemName.HAT_SEN);
        itemsThrownFromMonsters.add(1, ItemName.DUONG);
        itemsThrownFromMonsters.add(1, ItemName.DAU_XANH);
        itemsThrownFromMonsters.add(1, ItemName.MUT);
       keyEventPoint.add(EventPoint.DIEM_TIEU_XAI);
        keyEventPoint.add(TOP_LONG_DEN);
        keyEventPoint.add(TOP_BANH_TRUNG_THU);
    }

    @Override
    public void useItem(Char _char, Item item) {
        if (item.id == ItemName.HOP_BANH_THUONG || item.id == ItemName.HOP_BANH_THUONG_HANG) {
            if (_char.getSlotNull() == 0) {
                _char.warningBagFull();
                return;
            }
            RandomCollection<Integer> rc = item.id == ItemName.HOP_BANH_THUONG ? itemsRecFromCoinItem : itemsRecFromGoldItem;
            boolean isDone = useEventItem(_char, item.id, rc);
        } else if (item.id == ItemName.LONG_DEN) {
            if (_char.getSlotNull() == 0) {
                _char.warningBagFull();
                return;
            }
            boolean isDone = useEventItem(_char, item.id, itemsRecFromGold2Item);
            if (isDone) {
                _char.getEventPoint().addPoint(TOP_LONG_DEN, 1);
            }
            _char.zone.getService().addEffectAuto((byte) 7, (short) _char.x, _char.y, (byte) 0, (short) 1);
        }
    }

    @Override
    public void action(Char p, int type, int amount) {
        switch (type) {
            case BANH_THAP_CAM:
                banhThapCam(p, amount);
                break;
            case BANH_DAU_XANH:
                banhDauXanh(p, amount);
                break;
            case BANH_DEO:
                banhDeo(p, amount);
                break;
            case BANH_PIA:
                banhPia(p, amount);
                break;
            case HOP_BANH_THUONG:
                hopBanhThuong(p, amount);
                break;
            case HOP_BANH_THUONG_HANG:
                hopBanhThuongHang(p, amount);
                break;
            case HOA_PHUC_SINH:
                hoaPhucSinh(p, amount);
                break;
            case DOI_BACH_HO:
                doiBachHo(p);
                break;
            case VU_KHI_THOI_TRANG_7_NGAY:
                doiVuKhiThoiTrang(p, ItemName.BANH_TRUNG_THU_PHONG_LOI, 10, EXPIRE_7_DAY);
                break;
            case VU_KHI_THOI_TRANG_30_NGAY:
                doiVuKhiThoiTrang(p, ItemName.BANH_TRUNG_THU_BANG_HOA, 20, EXPIRE_30_DAY);
                break;
        }
    }

    public void doiBachHo(Char p) {
        int amount = 10;
        List<Item> list = p.getListItemByID(ItemName.BANH_TRUNG_THU_PHONG_LOI);
        if (list.size() < amount) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ Bánh trung thu phong lôi");
            return;
        }

        for (Item item : list.subList(0, amount)) {
            p.removeItem(item.index, 1, true);
        }
        Item item = ItemFactory.getInstance().newItem(ItemName.BACH_HO);
        item.setQuantity(1);
        item.isLock = false;
        item.expire = System.currentTimeMillis() + EXPIRE_30_DAY;
        p.addItemToBag(item);
    }

    public void doiVuKhiThoiTrang(Char p, int itemID, int amount, long expire) {
        List<Item> list = p.getListItemByID(itemID);
        if (list.size() < amount) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ " + ItemManager.getInstance().getItemName(itemID));
            return;
        }

        for (Item item : list.subList(0, amount)) {
            p.removeItem(item.index, 1, true);
        }

        if (p.gender == 1) {
            itemID = ItemName.GAY_MAT_TRANG;
        } else {
            itemID = ItemName.GAY_TRAI_TIM;
        }
        Item item = ItemFactory.getInstance().newItem(itemID);
        item.setQuantity(1);
        item.isLock = false;
        if (expire == -1) {
            item.expire = -1;
        } else {
            item.expire = System.currentTimeMillis() + expire;
        }
        p.addItemToBag(item);
    }

    public void banhThapCam(Char p, int amount) {
        int[][] itemRequires = new int[][]{{ItemName.BOT_MI, 10}, {ItemName.TRUNG, 5}, {ItemName.HAT_SEN, 5}, {ItemName.DUONG, 5}, {ItemName.MUT, 5}};
        int itemIdReceive = ItemName.BANH_THAP_CAM;
        makeEventItem(p, amount, itemRequires, 0, 0, 15000, itemIdReceive);
    }

    public void banhDeo(Char p, int amount) {
        int[][] itemRequires = new int[][]{{ItemName.BOT_MI, 10}, {ItemName.DUONG, 5}, {ItemName.HAT_SEN, 5}, {ItemName.MUT, 5}};
        int itemIdReceive = ItemName.BANH_DEO;
        makeEventItem(p, amount, itemRequires, 0, 0, 15000, itemIdReceive);
    }

    public void banhDauXanh(Char p, int amount) {
        int[][] itemRequires = new int[][]{{ItemName.BOT_MI, 10}, {ItemName.TRUNG, 5}, {ItemName.DUONG, 5}, {ItemName.DAU_XANH, 5}};
        int itemIdReceive = ItemName.BANH_DAU_XANH;
        makeEventItem(p, amount, itemRequires, 0, 0, 15000, itemIdReceive);
    }

    public void banhPia(Char p, int amount) {
        int[][] itemRequires = new int[][]{{ItemName.BOT_MI, 10}, {ItemName.TRUNG, 5}, {ItemName.DUONG, 5}, {ItemName.DAU_XANH, 5}};
        int itemIdReceive = ItemName.BANH_PIA;
        makeEventItem(p, amount, itemRequires, 0, 0, 15000, itemIdReceive);
    }

    public void hopBanhThuong(Char p, int amount) {
        int[][] itemRequires = new int[][]{{ItemName.GIAY_GOI_THUONG, 1}, {ItemName.BANH_THAP_CAM, 1}, {ItemName.BANH_DEO, 1}, {ItemName.BANH_DAU_XANH, 1}, {ItemName.BANH_PIA, 1}};
        int itemIdReceive = ItemName.HOP_BANH_THUONG;
        makeEventItem(p, amount, itemRequires, 0, 0, 0, itemIdReceive);
    }

    public void hopBanhThuongHang(Char p, int amount) {
        int[][] itemRequires = new int[][]{{ItemName.GIAY_GOI_CAO_CAP, 1}, {ItemName.BANH_THAP_CAM, 1}, {ItemName.BANH_DEO, 1}, {ItemName.BANH_DAU_XANH, 1}, {ItemName.BANH_PIA, 1}};
        int itemIdReceive = ItemName.HOP_BANH_THUONG_HANG;
        makeEventItem(p, amount, itemRequires, 0, 0, 0, itemIdReceive);
          p.getEventPoint().addPoint(TOP_BANH_TRUNG_THU, amount);
    }

    public void doiHoaPhucSinh(Char p, int type) {
        int point = type == 1 ? 5000 : 20000;
        if (p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI) < point) {
            p.getService().npcChat(NpcName.TIEN_NU,
                    "Ngươi cần tối thiểu " + NinjaUtils.getCurrency(point) + " điểm sự kiện mới có thể đổi được vật này.");
            return;
        }

        if (p.getSlotNull() == 0) {
            p.getService().npcChat(NpcName.TIEN_NU, p.language.getString("BAG_FULL"));
            return;
        }

        Item item = ItemFactory.getInstance().newItem(type == 1 ? ItemName.HOA_THIEN_DIEU : ItemName.HOA_DA_YEN);
        p.addItemToBag(item);
        p.getEventPoint().subPoint(EventPoint.DIEM_TIEU_XAI, point);
    }

    public void hoaPhucSinh(Char _char, int itemId) {
        if (_char.getSlotNull() == 0) {
            _char.warningBagFull();
            return;
        }

        int itemIndex = _char.getIndexItemByIdInBag(itemId);

        if (itemIndex != -1) {
            RandomCollection<Integer> rc = RandomItem.LINH_VAT;
            useVipEventItem(_char, itemId == ItemName.HOA_THIEN_DIEU ? 1 : 2, rc);
            _char.removeItem(itemIndex, 1, true);
        } else {
            _char.getService().npcChat((short) NpcName.KIRIKO, "Hãy tìm đúng loài hoa rồi đến gặp ta");
        }
    }

    public void escortFinish(Char p) {
        RandomCollection<Integer> rc = itemsRecFromGold2Item;
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

    @Override
    public void menu(Char p) {
        p.menus.clear();
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Làm bánh", () -> {
            p.menus.clear();
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Bánh Thập Cẩm", () -> {
                p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Bánh Thập Cẩm", () -> {
                    InputDialog input = p.getInput();
                    try {
                        int number = input.intValue();
                        action(p, BANH_THAP_CAM, number);
                    } catch (Exception e) {
                        if (!input.isEmpty()) {
                            p.inputInvalid();
                        }
                    }
                }));
                p.getService().showInputDialog();
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Bánh Dẻo", () -> {
                p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Bánh Dẻo", () -> {
                    InputDialog input = p.getInput();
                    try {
                        int number = input.intValue();
                        action(p, BANH_DEO, number);
                    } catch (Exception e) {
                        if (!input.isEmpty()) {
                            p.inputInvalid();
                        }
                    }
                }));
                p.getService().showInputDialog();
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Bánh Đậu xanh", () -> {
                p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Bánh Đậu xanh", () -> {
                    InputDialog input = p.getInput();
                    try {
                        int number = input.intValue();
                        action(p, BANH_DAU_XANH, number);
                    } catch (Exception e) {
                        if (!input.isEmpty()) {
                            p.inputInvalid();
                        }
                    }
                }));
                p.getService().showInputDialog();

            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Bánh Pía", () -> {
                p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Bánh Pía", () -> {
                    InputDialog input = p.getInput();
                    try {
                        int number = input.intValue();
                        action(p, BANH_PIA, number);
                    } catch (Exception e) {
                        if (!input.isEmpty()) {
                            p.inputInvalid();
                        }
                    }
                }));
                p.getService().showInputDialog();
            }));
            p.getService().openUIMenu();
        }));
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Làm hộp bánh", () -> {
            p.menus.clear();
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Hộp bánh thường", () -> {
                p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Hộp bánh thường", () -> {
                    InputDialog input = p.getInput();
                    try {
                        int number = input.intValue();
                        action(p, HOP_BANH_THUONG, number);
                    } catch (Exception e) {
                        if (!input.isEmpty()) {
                            p.inputInvalid();
                        }
                    }
                }));
                p.getService().showInputDialog();
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Hộp bánh thượng hạng", () -> {
                p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Hộp bánh thượng hạng", () -> {
                    InputDialog input = p.getInput();
                    try {
                        int number = input.intValue();
                        action(p, HOP_BANH_THUONG_HANG, number);
                    } catch (Exception e) {
                        if (!input.isEmpty()) {
                            p.inputInvalid();
                        }
                    }
                }));
                p.getService().showInputDialog();
            }));
            p.getService().openUIMenu();
        }));
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Đổi quà", () -> {
            p.menus.clear();
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Bạch hổ 30 ngày", () -> {
                action(p, DOI_BACH_HO, 1);
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Vũ khí thời trang 7 ngày", () -> {
                action(p, VU_KHI_THOI_TRANG_7_NGAY, 1);
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Vũ khí thời trang 30 ngày", () -> {
                action(p, VU_KHI_THOI_TRANG_30_NGAY, 1);
            }));
            p.getService().openUIMenu();
        }));
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Đổi lồng đèn", () -> {
            p.menus.clear();
            p.menus.add(new Menu(CMDMenu.EXECUTE, "5tr xu", () -> {
                p.setCommandBox(Char.DOI_LONG_DEN_XU);
                List<Item> list = p.getListItemByID(ItemName.LONG_DEN_TRON, ItemName.LONG_DEN_CA_CHEP, ItemName.LONG_DEN_MAT_TRANG, ItemName.LONG_DEN_NGOI_SAO);
                p.getService().openUIShopTrungThu(list, "Đổi lồng đèn 5tr xu", "Đổi (5tr xu)");
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "10 lượng", () -> {
                p.setCommandBox(Char.DOI_LONG_DEN_LUONG);
                List<Item> list = p.getListItemByID(ItemName.LONG_DEN_TRON, ItemName.LONG_DEN_CA_CHEP, ItemName.LONG_DEN_MAT_TRANG, ItemName.LONG_DEN_NGOI_SAO);
                p.getService().openUIShopTrungThu(list, "Đổi lồng đèn 10 lượng", "Đổi (10l)");
            }));
            p.getService().openUIMenu();
        }));

        p.menus.add(new Menu(CMDMenu.EXECUTE, "Hoa phục sinh", () -> {
            p.menus.clear();
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Hoa thiên diệu", () -> {
                doiHoaPhucSinh(p, 1);
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Hoa dạ yến", () -> {
                doiHoaPhucSinh(p, 2);
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Điểm sự kiện", () -> {
                p.getService().showAlert("Hướng dẫn", "- Điểm sự kiện: " + p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI)
                        + "\n\nBạn có thể quy đổi điểm sự kiện như sau\n- Hoa thiên diệu: 5.000 điểm\n- Hoa dạ yến: 20.000 điểm\n");
            }));
            p.getService().openUIMenu();
        }));

        p.menus.add(new Menu(CMDMenu.EXECUTE, "Đua Top", () -> {
            p.menus.clear();
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Thả Lồng Đèn", () -> {
             p.menus.clear();
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Bảng xếp hạng", () -> {
                     viewTop(p, TOP_LONG_DEN, "Thả Lồng Đèn", "%d. %s đã thả %s lồng đèn");
            }));
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Phần thưởng", () -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Top 1:").append("\n");
                    sb.append("- Hoả Kỳ Lân v.v MCS\n");
                    sb.append("- Áo dài v.v MCS\n");
                    sb.append("- 3 Rương huyền bí\n");
                    sb.append("- 10 Hoa Dạ Yến\n\n");
                    sb.append("Top 2:").append("\n");
                    sb.append("- Hoả Kỳ Lân v.v\n");
                    sb.append("- Áo dài v.v\n");
                    sb.append("- 1 Rương huyền bí\n");
                    sb.append("- 5 Hoa Dạ Yến\n\n");
                    sb.append("Top 3 - 5:").append("\n");
                    sb.append("- Hoả Kỳ Lân 3 tháng\n");
                    sb.append("- Áo dài 3 tháng\n");
                    sb.append("- 2 Rương bạch ngân\n");
                    sb.append("- 3 Hoa Dạ Yến\n\n");
                    sb.append("Top 6 - 10:").append("\n");
                    sb.append("- Hoả Kỳ Lân 1 tháng\n");
                    sb.append("- 1 rương bạch ngân\n");
                    p.getService().showAlert("Phần thưởng", sb.toString());
                }));
                if (isEnded()) {
                    int ranking = getRanking(p, TOP_LONG_DEN);
                    if (ranking <= 10 && p.getEventPoint().getRewarded(TOP_LONG_DEN) == 0) {
                        p.menus.add(new Menu(CMDMenu.EXECUTE, String.format("Nhận Thưởng TOP %d", ranking), () -> {
                            receiveReward(p, TOP_LONG_DEN);
                        }));
                    }
                }
                p.getService().openUIMenu();
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Bánh Trung Thu", () -> {
            p.menus.clear();
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Bảng xếp hạng", () -> {
                  viewTop(p, TOP_BANH_TRUNG_THU, "Bánh Trung Thu", "%d. %s đã sử dụng %s bánh trung thu");
            }));
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Phần thưởng", () -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Top 1:").append("\n");
                    sb.append("- Pet ứng long v.v MCS\n");
                    sb.append("- Gậy thời trang v.v\n");
                    sb.append("- 3 rương huyền bí\n");
                    sb.append("- 10 Hoa Dạ Yến\n\n");
                    sb.append("Top 2:").append("\n");
                    sb.append("- Pet ứng long v.v\n");
                    sb.append("- Gậy thời trang v.v\n");
                    sb.append("- 1 rương huyền bí\n");
                    sb.append("- 5 Hoa Dạ Yến\n\n");
                    sb.append("Top 3 - 5:").append("\n");
                    sb.append("- Pet ứng long 3 tháng\n");
                    sb.append("- Gậy thời trang 3 tháng\n");
                    sb.append("- 2 rương bạch ngân\n");
                    sb.append("- 3 Hoa Dạ Yến\n\n");
                    sb.append("Top 6 - 10:").append("\n");
                    sb.append("- Pet ứng long 1 tháng\n");
                    sb.append("- 1 rương bạch ngân\n");
                    p.getService().showAlert("Phần thưởng", sb.toString());
                }));
                if (isEnded()) {
                    int ranking = getRanking(p, TOP_BANH_TRUNG_THU);
                    if (ranking <= 10 && p.getEventPoint().getRewarded(TOP_BANH_TRUNG_THU) == 0) {
                        p.menus.add(new Menu(CMDMenu.EXECUTE, String.format("Nhận Thưởng TOP %d", ranking), () -> {
                            receiveReward(p, TOP_BANH_TRUNG_THU);
                        }));
                    }
                }
                p.getService().openUIMenu();
            }));
                p.getService().openUIMenu();
        }));
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Hướng dẫn", () -> {
            StringBuilder sb = new StringBuilder();
           
            sb.append("- Trong quá trình diễn ra sự kiện các ninja có level từ 30 trở lên đánh quái +- 7 level sẽ có tỉ lệ nhận được các nguyên liệu khóa sau:").append("\n");
            sb.append("+ Bột mì").append("\n");
            sb.append("+ Trứng").append("\n");
            sb.append("+ Hạt sen").append("\n");
            sb.append("+ Đường").append("\n");
            sb.append("+ Đậu xanh").append("\n");
            sb.append("+ Mứt").append("\n");

            sb.append("- Dùng Thiên nhãn phù hay Khai nhãn phù có thể tăng tỉ lệ rơi nguyên liệu").append("\n");
            sb.append("- Khi đã có đủ nguyên liệu các bạn có thể đến các làng gặp NPC Tiên Nữ để làm ra những chiếc bánh trung thu thơm ngon với công thức như sau:").append("\n");
            sb.append("* Bánh Thập Cẩm = 10 Bột + 5 Trứng + 5 Hạt sen + 5 Đường + 5 Mứt + yên").append("\n");
            sb.append("* Bánh Dẻo = 10 Bột + 5 Hạt sen + 5 Đường + 5 Mứt + yên").append("\n");
            sb.append("* Bánh Đậu xanh = 10 Bột + 5 Trứng + 5 Đường + 5 Đậu xanh + yên").append("\n");
            sb.append("* Bánh Pía = 10 Bột + 5 Trứng + 5 Đường + 5 Đậu xanh + yên").append("\n");

            sb.append("- Bánh trung thu khóa").append("\n");
            sb.append("- Tôi sẽ thu mỗi bạn một ít Yên cho tiền công làm bánh.").append("\n");

            sb.append("* Hộp bánh thường = 4 loại bánh + 1 giấy gói thường.").append("\n");
            sb.append("* Hộp bánh thượng hạng = 4 loại bánh + 1 giấy gói cao cấp. Có thể giao dịch").append("\n");

            sb.append("1 giấy gói thường bán ở Tabemono . Có thể giao dịch").append("\n");
            sb.append("1 giấy gói cao cấp bán ở Goosho").append("\n");
             
             sb.append("- Sự Kiện Diễn Ra Đến 0h 15/10/2023").append("\n");
            p.getService().showAlert("Hướng dẫn", sb.toString());
        }));
    }

    @Override
    public void initStore() {
        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(1999)
                .itemID(ItemName.GIAY_GOI_CAO_CAP)
                .gold(5)
                .expire(ConstTime.WEEK)
                .build());
        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_FOOD, ItemStore.builder()
                .id(1017)
                .itemID(ItemName.GIAY_GOI_THUONG)
                .coin(3000)
                .expire(ConstTime.WEEK)
                .build());
    }

    @Override
    public void initMap(Zone zone) {
        boolean isTrungThu = NinjaUtils.isTrungThu();
        Map map = zone.map;
        int mapID = map.id;
        switch (mapID) {
            
            case MapName.TRUONG_OOKAZA:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_TRUNG_THU).x((short) 1426).y((short) 552).build());
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_TRUNG_THU_2).x((short) 784).y((short) 648).build());
                if (isTrungThu) {
                    zone.addTree(Tree.builder().id(EffectAutoDataManager.THA_LONG_DEN).x((short) 1426).y((short) 552).build());
                    zone.addTree(Tree.builder().id(EffectAutoDataManager.THA_LONG_DEN).x((short) 784).y((short) 648).build());
                }
                break;
            case MapName.TRUONG_HARUNA:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_TRUNG_THU).x((short) 502).y((short) 408).build());
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_TRUNG_THU).x((short) 1863).y((short) 360).build());
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_TRUNG_THU).x((short) 2048).y((short) 360).build());
                if (isTrungThu) {
                    zone.addTree(Tree.builder().id(EffectAutoDataManager.THA_LONG_DEN).x((short) 502).y((short) 408).build());
                    zone.addTree(Tree.builder().id(EffectAutoDataManager.THA_LONG_DEN).x((short) 1863).y((short) 360).build());
                    zone.addTree(Tree.builder().id(EffectAutoDataManager.THA_LONG_DEN).x((short) 2048).y((short) 360).build());
                }
                break;
            case MapName.TRUONG_HIROSAKI:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_TRUNG_THU).x((short) 1207).y((short) 168).build());
                if (isTrungThu) {
                    zone.addTree(Tree.builder().id(EffectAutoDataManager.THA_LONG_DEN).x((short) 1207).y((short) 168).build());
                }
                break;
            case MapName.LANG_TONE:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_TRUNG_THU).x((short) 1427).y((short) 264).build());
                break;

            case MapName.LANG_KOJIN:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_TRUNG_THU).x((short) 621).y((short) 288).build());
                break;

            case MapName.LANG_CHAI:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_TRUNG_THU).x((short) 1804).y((short) 384).build());
                break;

            case MapName.LANG_SANZU:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_TRUNG_THU).x((short) 320).y((short) 288).build());
                break;

            case MapName.LANG_CHAKUMI:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_TRUNG_THU).x((short) 626).y((short) 312).build());
                break;

            case MapName.LANG_ECHIGO:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_TRUNG_THU).x((short) 360).y((short) 360).build());
                break;

            case MapName.LANG_OSHIN:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_TRUNG_THU).x((short) 921).y((short) 408).build());
                break;

            case MapName.LANG_SHIIBA:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_TRUNG_THU).x((short) 583).y((short) 408).build());
                break;

            case MapName.LANG_FEARRI:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.CAY_TRUNG_THU).x((short) 611).y((short) 312).build());
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

        if (key == TOP_LONG_DEN) {
            toplongden(ranking, p);
        } else if (key == TOP_BANH_TRUNG_THU) {
            topbanhtrungthu(ranking, p);
        }
        p.getEventPoint().setRewarded(key, 1);
    }
    public void toplongden(int ranking, Char p) {
        Item mount = ItemFactory.getInstance().newItem(ItemName.HOA_KY_LAN);
        int dressId = p.gender == 1 ? ItemName.AO_NGU_THAN : ItemName.AO_TAN_THOI;
        Item aoDai = ItemFactory.getInstance().newItem(dressId);
        Item tree = ItemFactory.getInstance().newItem(ItemName.HOA_DA_YEN);
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
    public void topbanhtrungthu(int ranking, Char p) {
        Item pet = ItemFactory.getInstance().newItem(ItemName.PET_UNG_LONG);
        int tickId = p.gender == 1 ? ItemName.GAY_MAT_TRANG : ItemName.GAY_TRAI_TIM;
        Item fashionStick = ItemFactory.getInstance().newItem(tickId);
        Item tree = ItemFactory.getInstance().newItem(ItemName.HOA_DA_YEN);
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

}
