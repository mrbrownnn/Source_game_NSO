
package com.nsoz.event;

import com.nsoz.constants.CMDInputDialog;
import com.nsoz.constants.CMDMenu;
import com.nsoz.constants.ConstTime;
import com.nsoz.constants.ItemName;
import com.nsoz.constants.MapName;
import com.nsoz.constants.NpcName;
import com.nsoz.effect.EffectAutoDataManager;
import com.nsoz.event.eventpoint.EventPoint;
import com.nsoz.item.Item;
import com.nsoz.item.ItemFactory;
import com.nsoz.map.Map;
import com.nsoz.map.Tree;
import com.nsoz.map.zones.Zone;
import com.nsoz.model.Char;
import com.nsoz.model.InputDialog;
import com.nsoz.model.Menu;
import com.nsoz.store.ItemStore;
import com.nsoz.store.StoreManager;

/**
 *
 * Được share bởi Youtube : nsotien tv
 */
public class VietnameseWomensDay extends Event {

    private static final int DOI_HOA_HONG_XANH = 0;
    private static final int BO_HOA_HONG_DO = 1;
    private static final int BO_HOA_HONG_VANG = 2;
    private static final int BO_HOA_HONG_XANH = 3;

    public VietnameseWomensDay() {
        setId(Event.NGAY_PHU_NU_VIET_NAM);
        endTime.set(2023, 10, 18, 23, 59, 59);
        itemsThrownFromMonsters.add(1, ItemName.HOA_HONG_DO);
        keyEventPoint.add(EventPoint.DIEM_TIEU_XAI);
    }

    @Override
    public void useItem(Char _char, Item item) {
        if (item.id == ItemName.BO_HOA_HONG_VANG) {
            if (_char.getSlotNull() == 0) {
                _char.warningBagFull();
                return;
            }
            useEventItem(_char, item.id, itemsRecFromCoinItem);
        } else if (item.id == ItemName.BO_HOA_HONG_DO || item.id == ItemName.BO_HOA_HONG_XANH) {
            if (_char.getSlotNull() == 0) {
                _char.warningBagFull();
                return;
            }
            useEventItem(_char, item.id, itemsRecFromGoldItem);
        }
    }

    @Override
    public void action(Char p, int type, int amount) {
        if (isEnded()) {
            p.serverMessage("Sự kiện đã kết thúc");
            return;
        }
        switch (type) {
            case DOI_HOA_HONG_XANH:
                doiHoaHongXanh(p, amount);
                break;

            case BO_HOA_HONG_DO:
                boHoaHongDo(p, amount);
                break;

            case BO_HOA_HONG_VANG:
                boHoaHongVang(p, amount);
                break;

            case BO_HOA_HONG_XANH:
                boHoaHongXanh(p, amount);
                break;
        }
    }

    public void doiHoaHongXanh(Char p, int amount) {
        if (amount < 1) {
            p.getService().npcChat(NpcName.TIEN_NU, "Số lượng tối thiểu là 1.");
            return;
        }

        if (amount > 1000) {
            p.getService().npcChat(NpcName.TIEN_NU, "Số lượng tối đa là 1.000.");
            return;
        }
        int requiredPoint = 10 * amount;
        int point = p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI);
        if (point < requiredPoint) {
            p.getService().npcChat(NpcName.TIEN_NU, "Không đủ điểm tiêu xài.");
            return;
        }
        p.getEventPoint().subPoint(EventPoint.DIEM_TIEU_XAI, requiredPoint);
        Item item = ItemFactory.getInstance().newItem(ItemName.HOA_HONG_XANH);
        item.setQuantity(amount);
        p.addItemToBag(item);
    }

    public void boHoaHongDo(Char p, int amount) {
        int[][] itemRequires = new int[][]{{ItemName.HOA_HONG_DO, 30}, {ItemName.RUY_BANG, 1}};
        int itemIdReceive = ItemName.BO_HOA_HONG_DO;
        makeEventItem(p, amount, itemRequires, 0, 0, 0, itemIdReceive);
    }

    public void boHoaHongVang(Char p, int amount) {
        int[][] itemRequires = new int[][]{{ItemName.HOA_HONG_DO, 50}, {ItemName.GIAY_MAU, 1}};
        int itemIdReceive = ItemName.BO_HOA_HONG_VANG;
        makeEventItem(p, amount, itemRequires, 0, 0, 0, itemIdReceive);
    }

    public void boHoaHongXanh(Char p, int amount) {
        int[][] itemRequires = new int[][]{{ItemName.HOA_HONG_XANH, 50}};
        int itemIdReceive = ItemName.BO_HOA_HONG_XANH;
        makeEventItem(p, amount, itemRequires, 0, 0, 500000, itemIdReceive);
    }

    @Override
    public void menu(Char p) {
        p.menus.clear();
        if (!isEnded()) {
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Đổi Hoa Hồng Xanh", () -> {
                p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Hoa Hồng Xanh", () -> {
                    InputDialog input = p.getInput();
                    try {
                        int number = input.intValue();
                        action(p, DOI_HOA_HONG_XANH, number);
                    } catch (Exception e) {
                        if (!input.isEmpty()) {
                            p.inputInvalid();
                        }
                    }
                }));
                p.getService().showInputDialog();
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Đổi Bó Hoa", () -> {
                p.menus.clear();
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Bó Hoa Hồng Đỏ", () -> {
                    p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Số Bó Hoa Hồng Đỏ", () -> {
                        InputDialog input = p.getInput();
                        try {
                            int number = input.intValue();
                            action(p, BO_HOA_HONG_DO, number);
                        } catch (Exception e) {
                            if (!input.isEmpty()) {
                                p.inputInvalid();
                            }
                        }
                    }));
                    p.getService().showInputDialog();
                }));
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Bó Hoa Hồng Vàng", () -> {
                    p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Số Bó Hoa Hồng Vàng", () -> {
                        InputDialog input = p.getInput();
                        try {
                            int number = input.intValue();
                            action(p, BO_HOA_HONG_VANG, number);
                        } catch (Exception e) {
                            if (!input.isEmpty()) {
                                p.inputInvalid();
                            }
                        }
                    }));
                    p.getService().showInputDialog();
                }));
                p.menus.add(new Menu(CMDMenu.EXECUTE, "Bó Hoa Hồng Xanh", () -> {
                    p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Số Bó Hoa Hồng Xanh", () -> {
                        InputDialog input = p.getInput();
                        try {
                            int number = input.intValue();
                            action(p, BO_HOA_HONG_XANH, number);
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
        }
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Hướng dẫn", () -> {
            StringBuilder sb = new StringBuilder();
            sb.append("- Điểm tiêu xài: ").append(p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI)).append("\n");
            sb.append("- 10 điểm tiêu xài = Hoa hồng xanh.").append("\n");
            sb.append("- 50 Hoa Hồng Đỏ + 1 Giấy Màu = Bó Hoa Hồng Vàng.").append("\n");
            sb.append("- 30 Hoa Hồng Đỏ + 1 Ruy Băng = Bó Hoa Hồng Đỏ.").append("\n");
            sb.append("- 50 Hoa Hồng Xanh + 500.000 yên = Bó Hoa Hồng Xanh.");
            p.getService().showAlert("Hướng Dẫn", sb.toString());
        }));
    }

    @Override
    public void initStore() {
        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(998)
                .itemID(ItemName.GIAY_MAU)
                .coin(100000)
                .expire(ConstTime.FOREVER)
                .build());
        StoreManager.getInstance().addItem((byte) StoreManager.TYPE_MISCELLANEOUS, ItemStore.builder()
                .id(999)
                .itemID(ItemName.RUY_BANG)
                .gold(20)
                .expire(ConstTime.FOREVER)
                .build());
    }

    @Override
    public void initMap(Zone zone) {
        Map map = zone.map;
        int mapID = map.id;
        switch (mapID) {
            
            case MapName.TRUONG_OOKAZA:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.PHAO_HOA).x((short) 1426).y((short) 552).build());
                zone.addTree(Tree.builder().id(EffectAutoDataManager.PHAO_HOA).x((short) 784).y((short) 648).build());
               
                break;
            case MapName.TRUONG_HARUNA:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.PHAO_HOA).x((short) 502).y((short) 408).build());
                zone.addTree(Tree.builder().id(EffectAutoDataManager.PHAO_HOA).x((short) 1863).y((short) 360).build());
                zone.addTree(Tree.builder().id(EffectAutoDataManager.PHAO_HOA).x((short) 2048).y((short) 360).build());
                
                break;
            case MapName.TRUONG_HIROSAKI:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.PHAO_HOA).x((short) 1207).y((short) 168).build());
               
                break;
            case MapName.LANG_TONE:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.PHAO_HOA).x((short) 1427).y((short) 264).build());
                break;

            case MapName.LANG_KOJIN:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.PHAO_HOA).x((short) 621).y((short) 288).build());
                break;

            case MapName.LANG_CHAI:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.PHAO_HOA).x((short) 1804).y((short) 384).build());
                break;

            case MapName.LANG_SANZU:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.PHAO_HOA).x((short) 320).y((short) 288).build());
                break;

            case MapName.LANG_CHAKUMI:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.PHAO_HOA).x((short) 626).y((short) 312).build());
                break;

            case MapName.LANG_ECHIGO:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.PHAO_HOA).x((short) 360).y((short) 360).build());
                break;

            case MapName.LANG_OSHIN:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.PHAO_HOA).x((short) 921).y((short) 408).build());
                break;

            case MapName.LANG_SHIIBA:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.PHAO_HOA).x((short) 583).y((short) 408).build());
                break;

            case MapName.LANG_FEARRI:
                zone.addTree(Tree.builder().id(EffectAutoDataManager.PHAO_HOA).x((short) 611).y((short) 312).build());
                break;
        }

    }

}
