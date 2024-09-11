package com.nsoz.event;

import com.nsoz.constants.CMDInputDialog;
import com.nsoz.constants.CMDMenu;
import com.nsoz.item.Item;
import com.nsoz.constants.ItemName;
import com.nsoz.model.Char;
import com.nsoz.model.InputDialog;
import com.nsoz.model.Menu;
import com.nsoz.mob.Mob;
import com.nsoz.constants.NpcName;
import com.nsoz.event.eventpoint.EventPoint;
import com.nsoz.item.ItemFactory;
import com.nsoz.model.RandomItem;
import com.nsoz.npc.Npc;
import com.nsoz.mob.MobTemplate;
import com.nsoz.util.NinjaUtils;
import com.nsoz.lib.RandomCollection;
import com.nsoz.map.zones.Zone;
import com.nsoz.mob.MobManager;

public class KoroKing extends Event {

    public static final int TOP_LUCKY_START = 0;

    public KoroKing() {
        setId(Event.KOROKING);
        itemsThrownFromMonsters.add(3, ItemName.TO_DIEP);
        itemsThrownFromMonsters.add(3, ItemName.NGU_TINH_THAO);
        itemsThrownFromMonsters.add(1, ItemName.CAY_KEO_MUT);
        itemsThrownFromMonsters.add(1, ItemName.HOP_BANH_NGOT);
        itemsThrownFromMonsters.add(1, ItemName.QUA_BONG_BONG);
    }

    @Override
    public void useItem(Char _char, Item item) {
        if (item.id == ItemName.TINH_DAU_TO_DIEP || item.id == ItemName.TINH_DAU_NGU_THAO
                || item.id == ItemName.NGOI_SAO_NHO || item.id == ItemName.NGOI_SAO_MAY_MAN) {
            if (_char.getSlotNull() == 0) {
                _char.warningBagFull();
                return;
            }

            if (item.id > ItemName.TINH_DAU_TO_DIEP) {
                Npc npc = _char.zone.getNpc(NpcName.KIRIKO);

                if (npc == null) {
                    _char.serverMessage("Vui tìm Kiriko để giao vật phẩm này.");
                    return;
                }

                int distance = NinjaUtils.getDistance(npc.cx, npc.cy, _char.x, _char.y);
                if (distance > 100) {
                    _char.serverMessage("Vui tìm Kiriko để giao vật phẩm này.");
                    return;
                }
            }

            RandomCollection<Integer> rc;
            if (item.id == ItemName.NGOI_SAO_MAY_MAN) {
                rc = itemsRecFromGold2Item;
            } else if (item.id == ItemName.TINH_DAU_TO_DIEP) {
                rc = itemsRecFromGoldItem;
            } else {
                rc = itemsRecFromCoinItem;
            }

            boolean isDone = useEventItem(_char, item.id, rc);
        }
    }

    public void menu(Char _char) {
        _char.menus.add(new Menu(CMDMenu.EXECUTE, "Tinh dầu", () -> {
            _char.menus.clear();
            _char.menus.add(new Menu(CMDMenu.EXECUTE, "Ngư thảo", () -> {
                InputDialog input = new InputDialog(CMDInputDialog.NGU_THAO, "Tinh dầu ngư thảo");
                _char.setInput(input);
                _char.getService().showInputDialog();
            }));
            _char.menus.add(new Menu(CMDMenu.EXECUTE, "Tô diệp", () -> {
                InputDialog input = new InputDialog(CMDInputDialog.TO_DIEP, "Tinh dầu tô diệp");
                _char.setInput(input);
                _char.getService().showInputDialog();
            }));
            _char.getService().openUIMenu();
        }));

        _char.menus.add(new Menu(CMDMenu.EXECUTE, "Ngôi sao may mắn", () -> {
            _char.menus.clear();
            _char.menus.add(new Menu(CMDMenu.EXECUTE, "Ngôi sao nhỏ", () -> {
                InputDialog input = new InputDialog(CMDInputDialog.NGOI_SAO_NHO, "Ngôi sao nhỏ");
                _char.setInput(input);
                _char.getService().showInputDialog();
            }));
            _char.menus.add(new Menu(CMDMenu.EXECUTE, "Ngôi sao may mắn", () -> {
                InputDialog input = new InputDialog(CMDInputDialog.NGOI_SAO_MAY_MAN, "Ngôi sao may mắn");
                _char.setInput(input);
                _char.getService().showInputDialog();
            }));
            _char.getService().openUIMenu();
        }));

        _char.menus.add(new Menu(CMDMenu.EXECUTE, "Đua Top", () -> {
            _char.menus.clear();
            _char.menus.add(new Menu(CMDMenu.EXECUTE, "Ngôi sao may mắn", () -> {
                Ranking.showLeaderBoard(_char, TOP_LUCKY_START, "đã sử dụng %d ngôi sao may mắn");
            }));
            _char.getService().openUIMenu();
        }));

        _char.menus.add(new Menu(CMDMenu.EXECUTE, "Hoa phục sinh", () -> {
            _char.menus.clear();
            _char.menus.add(new Menu(CMDMenu.EXECUTE, "Hoa thiên diệu", () -> {
                doiHoaPhucSinh(_char, 1);
            }));
            _char.menus.add(new Menu(CMDMenu.EXECUTE, "Hoa dạ yến", () -> {
                doiHoaPhucSinh(_char, 2);
            }));
            _char.menus.add(new Menu(CMDMenu.EXECUTE, "Điểm sự kiện", () -> {
                _char.getService().showAlert("Hướng dẫn", "- Điểm sự kiện: "
                        + NinjaUtils.getCurrency(_char.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI))
                        + "\n\nBạn có thể quy đổi điểm sự kiện như sau\n- Hoa thiên diệu: 5.000 điểm\n- Hoa dạ yến: 20.000 điểm\n");
            }));
            _char.getService().openUIMenu();
        }));
    }

    public void action(Char _char, int type, int amount) {
        switch (type) {
            case 1:
                nguThao(_char, amount);
                break;
            case 2:
                toDiep(_char, amount);
                break;
            case 3:
                ngoiSaoNho(_char, amount);
                break;
            case 4:
                ngoiSaoMayMan(_char, amount);
                break;
            case 5:
                hoaPhucSinh(_char, amount);
                break;
        }
    }

    public void nguThao(Char _char, int amount) {
        int[][] itemRequires = new int[][]{{ItemName.NGU_TINH_THAO, 3}};
        int itemIdReceive = ItemName.TINH_DAU_NGU_THAO;
        makeEventItem(_char, amount, itemRequires, 0, 100000, 0, itemIdReceive);
    }

    public void toDiep(Char _char, int amount) {
        int[][] itemRequires = new int[][]{{ItemName.TO_DIEP, 3}};
        int itemIdReceive = ItemName.TINH_DAU_TO_DIEP;
        makeEventItem(_char, amount, itemRequires, 20, 0, 0, itemIdReceive);
    }

    public void ngoiSaoNho(Char _char, int amount) {
        int[][] itemRequires = new int[][]{{ItemName.HUY_HIEU, 1}, {ItemName.CAY_KEO_MUT, 1},
        {ItemName.HOP_BANH_NGOT, 1}, {ItemName.QUA_BONG_BONG, 1}};
        int itemIdReceive = ItemName.NGOI_SAO_NHO;
        makeEventItem(_char, amount, itemRequires, 0, 100000, 0, itemIdReceive);
    }

    public void ngoiSaoMayMan(Char _char, int amount) {
        int[][] itemRequires = new int[][]{{ItemName.HUY_HIEU, 1}, {ItemName.CAY_KEO_MUT, 1},
        {ItemName.HOP_BANH_NGOT, 1}, {ItemName.QUA_BONG_BONG, 1}};
        int itemIdReceive = ItemName.NGOI_SAO_MAY_MAN;
        makeEventItem(_char, amount, itemRequires, 20, 0, 0, itemIdReceive);
    }

    public void hoaPhucSinh(Char _char, int itemId) {
        if (_char.getSlotNull() == 0) {
            _char.warningBagFull();
            return;
        }

        int itemIndex = _char.getIndexItemByIdInBag(itemId);

        if (itemIndex != -1) {
            RandomCollection<Integer> rc = RandomItem.LINH_VAT;
            Event.useVipEventItem(_char, 2, rc);
            _char.removeItem(itemIndex, 1, true);
        } else {
            _char.getService().npcChat((short) NpcName.KIRIKO, "Hãy tìm đúng loài hoa rồi đến gặp ta");
        }
    }

    public void doiHoaPhucSinh(Char p, int type) {
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

        Item item = ItemFactory.getInstance().newItem(type == 1 ? ItemName.HOA_THIEN_DIEU : ItemName.HOA_DA_YEN);
        p.addItemToBag(item);
        p.getEventPoint().subPoint(EventPoint.DIEM_TIEU_XAI, point);
    }

    public static void addTrophy(Char _char) {
        addTrophy(_char, 1);
    }

    public static void addTrophy(Char _char, int amount) {
        Item item = ItemFactory.getInstance().newItem(ItemName.HUY_HIEU);
        item.setQuantity(amount);
        _char.addItemToBag(item);
    }

    public void bornKoroKing(Mob mob) {
        int incrementId = mob.zone.getMonsters().size();
        MobTemplate template = MobManager.getInstance().find(232);
        Mob monster = new Mob(incrementId++, (short) template.id, mob.maxHP, mob.level, mob.x, mob.y, false,
                template.isBoss(), mob.zone);
        mob.zone.addMob(monster);
    }

    public void infection(Char _char) {
        if (_char.fashion[11] == null || _char.fashion[11].id != ItemName.KHAU_TRANG) {
            _char.infection();
            _char.zone.getService().chat(_char.id, "Khụ khụ !!!");
            _char.serverMessage("Bạn đã bị dính Virus");
        }
    }


    @Override
    public void initStore() {

    }

    @Override
    public void initMap(Zone zone) {

    }

}
