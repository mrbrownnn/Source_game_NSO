
package com.nsoz.admin;

import com.nsoz.constants.CMDInputDialog;
import com.nsoz.constants.CMDMenu;
import com.nsoz.constants.ItemName;
import com.nsoz.convert.Converter;
import com.nsoz.event.Event;
import com.nsoz.event.LunarNewYear;
import com.nsoz.item.Item;
import com.nsoz.item.ItemFactory;
import com.nsoz.map.Map;
import com.nsoz.map.MapManager;
import com.nsoz.mob.Mob;
import com.nsoz.mob.MobManager;
import com.nsoz.mob.MobTemplate;
import com.nsoz.model.Char;
import com.nsoz.model.InputDialog;
import com.nsoz.model.Menu;
import com.nsoz.model.WarMember;
import com.nsoz.option.ItemOption;
import com.nsoz.server.GameData;
import com.nsoz.server.Server;
import com.nsoz.server.ServerManager;
import com.nsoz.skill.Skill;
import com.nsoz.store.ItemStore;
import com.nsoz.store.StoreManager;
import com.nsoz.util.NinjaUtils;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * Được share bởi Youtube : nsotien tv
 */
public class AdminService {

    private static final AdminService instance = new AdminService();

    public static AdminService getInstance() {
        return instance;
    }

    public void addEquipment(Char p, String[] args) {
        try {
            int level = -1;
            int upgrade = 0;
            int sys = p.getSys();
            int gender = p.gender;
            boolean max = true;
            int tl = 0;
            for (int i = 1; i < args.length; i++) {
                boolean hP = i + 1 <= args.length;
                if (hP) {
                    if (args[i].equals("lv")) {
                        level = Integer.parseInt(args[++i]);
                    } else if (args[i].equals("up")) {
                        upgrade = Integer.parseInt(args[++i]);
                    } else if (args[i].equals("he")) {
                        sys = Integer.parseInt(args[++i]);
                    } else if (args[i].equals("max")) {
                        max = Integer.parseInt(args[++i]) == 1;
                    } else if (args[i].equals("nv")) {
                        gender = Integer.parseInt(args[++i]);
                    } else if (args[i].equals("tl")) {
                        tl = Integer.parseInt(args[++i]);
                    }
                }
            }
            if (level <= 10) {
                p.serverMessage("Hãy nhập level lớn hơn 10.");
                return;
            }
            if (upgrade < 0) {
                p.serverMessage("Hãy nhập upgrade lớn hơn 0.");
                return;
            }
            if (gender != 0 && gender != 1) {
                p.serverMessage("Giới tính là 0 hoặc 1");
                return;
            }
            if (tl < 0 || tl > 9) {
                p.serverMessage("Hãy nhập tinh luyện từ 0 đến 9");
                return;
            }
            if (sys < 0 || sys > 3) {
                p.serverMessage("Hệ không hợp lệ");
                return;
            }
            int sys2 = sys;
            if (level % 10 == 0) {
                sys2 = p.classId;
            }
            Item item = null;
            if (level >= 90 && level < 100) {
                int itemID = -1;
                int i = level % 10;
                switch (i) {
                    case 0:
                        switch (p.classId) {
                            case 1:
                                itemID = ItemName.THAI_DUONG_VO_CUC_KIEM;
                                break;
                            case 2:
                                itemID = ItemName.THAI_DUONG_THIEN_HOA_TIEU;
                                break;
                            case 3:
                                itemID = ItemName.THAI_DUONG_TANG_HON_DAO;
                                break;
                            case 4:
                                itemID = ItemName.THAI_DUONG_BANG_THAN_CUNG;
                                break;
                            case 5:
                                itemID = ItemName.THAI_DUONG_CHIEN_LUC_DAO;
                                break;
                            case 6:
                                itemID = ItemName.THAI_DUONG_HOANG_PHONG_PHIEN;
                                break;
                            default:
                                break;
                        }
                        break;

                    case 1:
                        if (gender == 1) {
                            itemID = ItemName.THAI_DUONG_COT_NGOC_NGOA;
                        } else {
                            itemID = ItemName.THAI_DUONG_COT_NGOC_NGOA_NU;
                        }
                        break;

                    case 2:
                        itemID = ItemName.THAI_DUONG_COT_NGOC_PHU;
                        break;

                    case 3:
                        if (gender == 1) {
                            itemID = ItemName.THAI_DUONG_COT_NGOC_HA_GIAP;
                        } else {
                            itemID = ItemName.THAI_DUONG_COT_NGOC_HA_GIAP_NU;
                        }
                        break;

                    case 4:
                        itemID = ItemName.THAI_DUONG_COT_NGOC_BOI;
                        break;

                    case 5:
                        if (gender == 1) {
                            itemID = ItemName.THAI_DUONG_COT_NGOC_THU;
                        } else {
                            itemID = ItemName.THAI_DUONG_COT_NGOC_THU_NU;
                        }
                        break;

                    case 6:
                        itemID = ItemName.THAI_DUONG_COT_NGOC_GIOI;
                        break;

                    case 7:
                        if (gender == 1) {
                            itemID = ItemName.THAI_DUONG_COT_NGOC_GIAP;
                        } else {
                            itemID = ItemName.THAI_DUONG_COT_NGOC_GIAP_NU;
                        }
                        break;

                    case 8:
                        itemID = ItemName.THAI_DUONG_COT_NGOC_LIEN;
                        break;

                    case 9:
                        if (gender == 1) {
                            itemID = ItemName.THAI_DUONG_COT_NGOC_TUYEN;
                        } else {
                            itemID = ItemName.THAI_DUONG_COT_NGOC_TRAM;
                        }
                        break;

                }
                if (itemID != -1) {
                    item = ItemFactory.getInstance().newItem9X(itemID, max);
                }

            } else {
                ItemStore itemStore = StoreManager.getInstance().getEquipment(level, sys2, gender);
                if (itemStore != null) {
                    item = Converter.getInstance().toItem(itemStore,
                            max ? Converter.MAX_OPTION : Converter.RANDOM_OPTION);
                    p.addItemToBag(item);
                }
            }
            if (item != null) {
                item.next(upgrade);
                item.setLock(true);
                if (tl > 0) {
                    ItemOption option = new ItemOption(85, 0);
                    item.options.add(option);
                    switch (item.template.type) {
                        case 0: {
                            int[] optionId = { 95, 96, 97 };
                            item.options.add(new ItemOption(optionId[item.sys - 1], 5));
                            item.options.add(new ItemOption(79, 5));
                            break;
                        }
                        case 1: {
                            item.options.add(new ItemOption(87, NinjaUtils.nextInt(250, 400)));
                            int[] optionId = { 88, 89, 90 };
                            item.options.add(new ItemOption(optionId[item.sys - 1], NinjaUtils.nextInt(350, 600)));
                            break;
                        }
                        case 2:
                            item.options.add(new ItemOption(80, NinjaUtils.nextInt(24, 28)));
                            item.options.add(new ItemOption(91, NinjaUtils.nextInt(10, 14)));
                            break;
                        case 3:
                            item.options.add(new ItemOption(81, 5));
                            item.options.add(new ItemOption(79, 5));
                            break;
                        case 4:
                            item.options.add(new ItemOption(86, NinjaUtils.nextInt(76, 124)));
                            item.options.add(new ItemOption(94, NinjaUtils.nextInt(76, 124)));
                            break;
                        case 5: {
                            int[] optionId = { 95, 96, 97 };
                            item.options.add(new ItemOption(optionId[item.sys - 1], 5));
                            item.options.add(new ItemOption(92, NinjaUtils.nextInt(9, 11)));
                            break;
                        }
                        case 6:
                            item.options.add(new ItemOption(83, NinjaUtils.nextInt(250, 450)));
                            item.options.add(new ItemOption(82, NinjaUtils.nextInt(250, 450)));
                            break;
                        case 7: {
                            int[] optionId = { 95, 96, 97 };
                            item.options.add(new ItemOption(optionId[item.sys - 1], 5));
                            optionId = new int[] { 88, 89, 90 };
                            item.options.add(new ItemOption(optionId[item.sys - 1], NinjaUtils.nextInt(350, 600)));
                            break;
                        }
                        case 8:
                            item.options.add(new ItemOption(83, NinjaUtils.nextInt(250, 450)));
                            item.options.add(new ItemOption(84, NinjaUtils.nextInt(76, 124)));
                            break;
                        case 9:
                            item.options.add(new ItemOption(84, NinjaUtils.nextInt(76, 124)));
                            item.options.add(new ItemOption(82, NinjaUtils.nextInt(250, 450)));
                            break;
                        default:
                            break;
                    }
                    for (int i = option.param; i < tl; i++) {
                        for (ItemOption option1 : item.options) {
                            if (option1.optionTemplate.type != 8 || option1.optionTemplate.id == 85) {
                                continue;
                            }
                            switch (option1.optionTemplate.id) {
                                case 94: {
                                    int[] percentIncreases = new int[] { 10, 10, 10, 20, 20, 30, 40, 50, 60 };
                                    option1.param += percentIncreases[option.param];
                                    break;
                                }
                                case 86: {
                                    int[] percentIncreases = new int[] { 25, 30, 35, 40, 50, 60, 80, 115, 165 };
                                    option1.param += percentIncreases[option.param];
                                    break;
                                }
                                case 87: {
                                    int[] percentIncreases = new int[] { 50, 60, 70, 90, 130, 180, 250, 330,
                                            500 };
                                    option1.param += percentIncreases[option.param];
                                    break;
                                }
                                case 88:
                                case 89:
                                case 90: {
                                    int[] percentIncreases = new int[] { 50, 70, 100, 140, 190, 250, 320, 400,
                                            500 };
                                    option1.param += percentIncreases[option.param];
                                    break;
                                }
                                case 92: {
                                    int[] percentIncreases = new int[] { 5, 5, 5, 5, 5, 5, 10, 10, 20 };
                                    option1.param += percentIncreases[option.param];
                                    break;
                                }
                                case 95:
                                case 96:
                                case 97: {
                                    int[] percentIncreases = new int[] { 5, 5, 5, 5, 5, 5, 10, 10, 15 };
                                    option1.param += percentIncreases[option.param];
                                    break;
                                }
                                case 82:
                                case 83: {
                                    int[] percentIncreases = new int[] { 40, 60, 80, 100, 140, 220, 300, 420,
                                            590 };
                                    option1.param += percentIncreases[option.param];
                                    break;
                                }
                                case 84: {
                                    int[] percentIncreases = new int[] { 25, 30, 35, 40, 50, 60, 80, 115, 165 };
                                    option1.param += percentIncreases[option.param];
                                    break;
                                }
                                case 79: {
                                    int[] percentIncreases = new int[] { 1, 2, 2, 2, 2, 2, 3, 3, 4 };
                                    option1.param += percentIncreases[option.param];
                                    break;
                                }
                                case 81: {
                                    int[] percentIncreases = new int[] { 1, 2, 2, 2, 2, 2, 3, 3, 4 };
                                    option1.param += percentIncreases[option.param];
                                    break;
                                }
                                case 80: {
                                    int[] percentIncreases = new int[] { 5, 5, 5, 5, 10, 10, 15, 15, 20 };
                                    option1.param += percentIncreases[option.param];
                                    break;
                                }
                                case 91: {
                                    int[] percentIncreases = new int[] { 5, 5, 5, 5, 5, 5, 10, 10, 15 };
                                    option1.param += percentIncreases[option.param];
                                    break;
                                }
                                default:
                                    break;
                            }
                        }
                        option.param++;
                    }
                }
                p.addItemToBag(item);
            } else {
                p.serverMessage("Không tìm thấy vật phẩm này!");
            }
        } catch (NumberFormatException e) {
            p.serverMessage("Lệnh không hợp lệ! " + e.getMessage());
        }
    }

    public void addGem(Char p, String[] args) {
        int itemID = -1;
        int upgrade = 1;
        boolean max = true;
        for (int i = 1; i < args.length; i++) {
            boolean hP = i + 1 <= args.length;
            if (hP) {
                if (args[i].equals("id")) {
                    itemID = Integer.parseInt(args[++i]);
                } else if (args[i].equals("u")) {
                    upgrade = Integer.parseInt(args[++i]);
                } else if (args[i].equals("m")) {
                    max = Integer.parseInt(args[++i]) == 1;
                }
            }
        }
        if (itemID == -1) {
            p.serverMessage("Hãy nhập mã vật phẩm!");
            return;
        }
        if (itemID != ItemName.HUYEN_TINH_NGOC && itemID != ItemName.HUYET_NGOC && itemID != ItemName.LAM_TINH_NGOC
                && itemID != ItemName.LUC_NGOC) {
            p.serverMessage("Vật phẩm này không phải ngọc!");
            return;
        }
        if (upgrade < 1 || upgrade > 10) {
            p.serverMessage("Cấp ngọc từ 1 đến 10!");
            return;
        }
        Item item = ItemFactory.getInstance().newGem(itemID, max);
        item.setLock(true);
        for (int i = item.upgrade; i < upgrade; i++) {
            item.upgrade++;
            for (ItemOption option : item.options) {
                switch (option.optionTemplate.id) {
                    case 73:
                        // tấn công
                        if (option.param > 0) {
                            int[] paramUp = { 0, 50, 100, 150, 200, 250, 300, 350, 400, 450 };
                            option.param += paramUp[i];
                        } else {
                            option.param -= 50;
                        }
                        break;
                    case 115:
                        // né đòn
                        if (option.param > 0) {
                            int[] paramUp = { 0, 10, 20, 30, 40, 50, 60, 70, 80, 90 };
                            option.param += paramUp[i];
                        } else {
                            int[] paramUp = { 0, 60, 40, 20, 20, 15, 15, 10, 10, 5 };
                            option.param -= paramUp[i];
                        }
                        break;
                    case 124:
                        // giảm trừ sát thương
                        if (option.param > 0) {
                            int[] paramUp = { 0, 10, 15, 20, 25, 30, 35, 40, 45, 50 };
                            option.param += paramUp[i];
                        } else {
                            option.param -= 20;
                        }
                        break;
                    case 114:
                        // chí mạng
                        if (option.param > 0) {
                            int[] paramUp = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
                            option.param += paramUp[i];
                        } else {
                            option.param -= 4;
                        }
                        break;
                    case 126:
                        // phản đòn
                        if (option.param > 0) {
                            int[] paramUp = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
                            option.param += paramUp[i];
                        } else {
                            option.param -= 4;
                        }
                        break;
                    case 118:
                        // kháng tất cả
                        if (option.param > 0) {
                            int[] paramUp = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
                            option.param += paramUp[i];
                        } else {
                            int[] paramUp = { 0, 20, 20, 20, 15, 15, 15, 10, 10, 5 };
                            option.param -= paramUp[i];
                        }
                        break;
                    case 102:
                        // sát thương lên quái
                        if (option.param > 0) {
                            int[] paramUp = { 0, 100, 200, 400, 600, 800, 1000, 1200, 1400, 1600 };
                            option.param += paramUp[i];
                        } else {
                            option.param -= 200;
                        }
                        break;
                    case 105:
                        // sát thương chí mạng
                        if (option.param > 0) {
                            int[] paramUp = { 0, 100, 200, 300, 400, 500, 600, 700, 900, 1200 };
                            option.param += paramUp[i];
                        } else {
                            option.param -= 200;
                        }
                        break;
                    case 103:
                        // sát thương lên người
                        if (option.param > 0) {
                            int[] paramUp = { 0, 100, 150, 160, 170, 200, 220, 250, 300, 350 };
                            option.param += paramUp[i];
                        } else {
                            option.param -= 150;
                        }
                        break;
                    case 121:
                        // kháng sát thương chí mạng
                        if (option.param > 0) {
                            int[] paramUp = { 0, 1, 2, 2, 2, 3, 3, 3, 4, 5 };
                            option.param += paramUp[i];
                        } else {
                            option.param -= 4;
                        }
                        break;
                    case 117:
                    case 125:
                        // hp, mp tối đa
                        if (option.param > 0) {
                            int[] paramUp = { 0, 100, 150, 200, 250, 300, 350, 400, 450, 500 };
                            option.param += paramUp[i];
                        } else {
                            option.param -= 150;
                        }
                        break;
                    case 116:
                        // chính xác
                        if (option.param > 0) {
                            int[] paramUp = { 0, 50, 50, 100, 150, 150, 200, 200, 250, 300 };
                            option.param += paramUp[i];
                        } else {
                            option.param -= 100;
                        }
                        break;
                    case 119:
                    case 229:
                        // hồi phục hp,
                        // mp
                        if (option.param > 0) {
                            int[] paramUp = { 0, 2, 4, 6, 8, 10, 12, 14, 16, 18 };
                            option.param += paramUp[i];
                        } else {
                            option.param -= 10;
                        }
                        break;
                    case 123:
                        int[] giaKham = { 800000, 1600000, 2400000, 3200000, 4800000, 7200000, 10800000, 15600000,
                                20100000, 28100000 };
                        option.param = giaKham[i];
                        break;
                    default:
                        break;
                }
            }
        }
        p.addItemToBag(item);
    }

    public void setLevel(Char p, int level) {
        long exp = NinjaUtils.getExpFromLevel(level);
        exp -= p.exp;
        p.addExp(exp);
    }

    public void setSkillWithLevel(Char p, int level) {
        if (level > p.level) {
            p.serverMessage("Trình độ chưa đạt yêu cầu để học!");
            return;
        }
        Skill skill = GameData.getInstance().getSkillWithLevel(p.classId, level);
        if (skill != null) {
            if (!p.isHuman) {
                int skillTemplateID = skill.template.id;
                if (skillTemplateID >= 68 && skillTemplateID <= 72) {
                    p.serverMessage("Phân thân không thể học chiêu này.");
                    return;
                }
            }
            for (Skill my : p.vSkill) {
                if (my.template.id == skill.template.id) {
                    p.serverMessage("Chiêu này đã học!");
                    return;
                }
            }
            skill = Converter.getInstance().newSkill(skill);
            p.serverMessage("Học chiêu " + skill.template.name + " thành công.");
            p.vSkill.add(skill);
            if (skill.template.type == Skill.SKILL_AUTO_USE) {
                p.vSupportSkill.add(skill);
                p.setAbility();
            } else if ((skill.template.type == Skill.SKILL_CLICK_USE_ATTACK
                    || skill.template.type == Skill.SKILL_CLICK_LIVE
                    || skill.template.type == Skill.SKILL_CLICK_USE_BUFF
                    || skill.template.type == Skill.SKILL_CLICK_NPC)
                    && (skill.template.maxPoint == 0 || (skill.template.maxPoint > 0 && skill.point > 0))) {

                p.vSkillFight.add(skill);
            }
            p.getService().loadSkill();
        }
    }

    public void openUIAdmin(Char p) {
        p.menus.clear();
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Đi tới", () -> {
            p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Nhập ID MAP", () -> {
                InputDialog input = p.getInput();
                try {
                    int mapID = input.intValue();
                    Map map = MapManager.getInstance().find(mapID);
                    if (map != null) {
                        int zoneID = NinjaUtils.randomZoneId(mapID);
                        p.outZone();
                        short[] xy = NinjaUtils.getFirstPosition((short) mapID);
                        p.setXY(xy);
                        map.joinZone(p, zoneID);
                    } else {
                        p.serverDialog("Không tìm thấy map này!");
                    }
                } catch (Exception e) {
                    if (!input.isEmpty()) {
                        p.serverDialog(e.getMessage());
                    }
                }
            }));
            p.getService().showInputDialog();
        }));
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Thông tin", () -> {
            showServerInfo(p);
        }));
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Bảo trì", () -> {
            NinjaUtils.setTimeout(() -> {
                Server.maintance();
                System.exit(0);
            }, 0);
        }));
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Lưu dữ liệu", () -> {
            NinjaUtils.setTimeout(() -> {
                Server.saveAll();
                p.serverDialog("Đã lưu dữ liệu!");
            }, 0);
        }));
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Tìm người chơi", () -> {
            p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Tìm người chơi", () -> {
                String text = p.getInput().getText();
                adminActionPlayer(p, text);
            }));
            p.getService().showInputDialog();
        }));
        p.menus.add(new Menu(CMDMenu.EXECUTE, "Nhập lệnh", () -> {
            p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Nhập lệnh", () -> {
                String text = p.getInput().getText();
                String[] args = text.split(" ");
                String command = args[0];
                if (args.length == 4 && command.equals("additem")) {
                    String name = args[1];
                    String[] rangeItemID = args[2].split("\\.");
                    int startID = Integer.parseInt(rangeItemID[0]);
                    int endID = startID;
                    if (rangeItemID.length == 2) {
                        endID = Integer.parseInt(rangeItemID[1]);
                    }
                    if (startID > endID) {
                        int tmp = startID;
                        startID = endID;
                        endID = tmp;
                    }
                    int quantity = Integer.parseInt(args[3]);
                    Char p2 = ServerManager.findCharByName(name);
                    if (p2 != null) {
                        for (; startID <= endID; startID++) {
                            Item item = ItemFactory.getInstance().newItem(startID);
                            item.setQuantity(quantity);
                            p2.addItemToBag(item);

                        }
                    }
                }
            }));
            p.getService().showInputDialog();
        }));
        p.getService().openUIMenu();
    }

    public void showServerInfo(Char p) {
        long total, free, used;
        double mb = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();
        total = runtime.totalMemory();
        free = runtime.freeMemory();
        used = total - free;
        StringBuilder sb = new StringBuilder();
        sb.append("- Số người đang online: ").append(ServerManager.getNumberOnline()).append("\n");
        sb.append("- Memory usage (JVM): ")
                .append(String.format("%.1f/%.1f MB (%d%%)", used / mb, total / mb, (used * 100 / total))).append("\n");
        p.getService().showAlert("Thông tin", sb.toString());
    }

    public void adminActionPlayer(Char p, String name) {
        final Char player = ServerManager.findCharByName(name);
        if (player != null) {
            p.menus.clear();
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Thông tin", () -> {
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("- Yên: %,d", player.yen)).append("\n");
                sb.append(String.format("- Xu: %,d", player.coin)).append("\n");
                sb.append(String.format("- Lượng: %,d", player.user.gold)).append("\n");
                p.getService().showAlert("Thông tin " + player.name, sb.toString());
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Khóa tài khoản", () -> {
                p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Thời hạn (giờ), bỏ trống nếu vĩnh viễn", () -> {
                    int hours = 0;
                    try {
                        hours = Integer.parseInt(p.getInput().getText());
                    } catch (Exception e) {
                    }
                    if (hours == 0) {
                        player.user.lock();
                    } else {
                        player.user.lock(hours);
                    }
                    p.serverMessage(String.format("Đã khóa %s!", player.name));
                }));
                p.getService().showInputDialog();
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Đi tới", () -> {
                if (player.zone != p.zone) {
                    p.outZone();
                    player.zone.join(p);
                } else {
                    p.getService().endDlg(true);
                }
                p.setXY(player.x, player.y);
                p.zone.getService().teleport(p);
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Cộng yên", () -> {
                p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Nhập số yên", () -> {
                    try {
                        int number = p.getInput().intValue();
                        player.addYen(number);
                    } catch (NumberFormatException e) {
                        if (!p.getInput().isEmpty()) {
                            p.serverDialog(e.getMessage());
                        }
                    }
                }));
                p.getService().showInputDialog();
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Cộng xu", () -> {
                p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Nhập số xu", () -> {
                    try {
                        int number = p.getInput().intValue();
                        player.addCoin(number);
                    } catch (NumberFormatException e) {
                        if (!p.getInput().isEmpty()) {
                            p.serverDialog(e.getMessage());
                        }
                    }
                }));
                p.getService().showInputDialog();
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Cộng lượng", () -> {
                p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Nhập số lượng", () -> {
                    try {
                        int number = p.getInput().intValue();
                        player.addGold(number);
                    } catch (NumberFormatException e) {
                        if (!p.getInput().isEmpty()) {
                            p.serverDialog(e.getMessage());
                        }
                    }
                }));
                p.getService().showInputDialog();
            }));
            p.menus.add(new Menu(CMDMenu.EXECUTE, "Level", () -> {
                p.setInput(new InputDialog(CMDInputDialog.EXECUTE, "Nhập cấp", () -> {
                    try {
                        int number = p.getInput().intValue();
                        long exp = NinjaUtils.getExpFromLevel(number);
                        exp -= p.exp;
                        p.addExp(exp);
                    } catch (NumberFormatException e) {
                        if (!p.getInput().isEmpty()) {
                            p.serverDialog(e.getMessage());
                        }
                    }
                }));
                p.getService().showInputDialog();
            }));
            p.getService().openUIMenu();
        } else {
            p.serverDialog("Không tìm thấy người chơi này!");
        }
    }

    public boolean process(Char p, String text) {
        if (text.equals("tls") && p.user.isAdmin()) {
            MapManager.getInstance().talentShow.showMenu(p);
            return true;
        }
        if (p.isModeAdd) {
            String[] args = text.split(" ");
            if (args[0].equals("body")) {
                addEquipment(p, args);
                return true;
            }
            if (args[0].equals("gem")) {
                addGem(p, args);
                return true;
            }
            if (args.length == 2 && args[0].equals("level")) {
                int level = Integer.parseInt(args[1]);
                setLevel(p, level);
                return true;
            }
            if (args.length == 2 && args[0].equals("skill")) {
                int level = Integer.parseInt(args[1]);
                setSkillWithLevel(p, level);
                return true;
            }
        }
        if (text.equals("info")) {
            p.zone.getService().chat(p.id, "map: " + p.mapId + " x: " + p.x + " y: " + p.y);
            return true;
        }
        if (text.equals("admin") && p.user.role == 9999) {
            openUIAdmin(p);
            return true;
        }
        if (text.equals("nsozxmas")) {
            p.isModeAdd = !p.isModeAdd;
            if (p.isModeAdd) {
                p.getService().serverDialog("Đã bật chế độ sáng tạo");
            } else {
                p.getService().serverDialog("Đã tắt chế độ sáng tạo");
            }
            return true;
        }
        if ((text.equals("create") && p.user.isAdmin()) || text.equals("wogdh")) {
            p.isModeCreate = !p.isModeCreate;
            if (p.isModeCreate) {
                p.getService().serverDialog("Đã bật chế độ sáng tạo");
            } else {
                p.isModeRemove = false;
                p.getService().serverDialog("Đã tắt chế độ sáng tạo");
            }
            return true;
        }
        if (p.isModeCreate) {
            if (text.equals("remove")) {
                p.isModeRemove = !p.isModeRemove;
                if (p.isModeRemove) {
                    p.getService().serverDialog("Đã bật chế độ xóa quái");
                } else {
                    p.getService().serverDialog("Đã tắt chế độ xóa quái");
                }
            }
            if (text.startsWith("addmob")) {
                text = text.replace(" ", "").trim();
                if (text.length() > 6) {
                    int mobId = Integer.parseInt(text.substring(6));
                    if (mobId > 256) {
                        return true;
                    }
                    int id = p.zone.getMonsters().size();
                    MobTemplate te = MobManager.getInstance().find(mobId);
                    int t = 0;
                    if (te.type == 4) {
                        t = 40;
                    }
                    Mob monster = new Mob(id++, (short) mobId, te.hp, te.level, p.x, (short) (p.y - t), false,
                            te.isBoss(), p.zone);
                    p.zone.addMob(monster);
                }
                return true;
            }
            if (text.equals("save")) {
                JSONArray json = new JSONArray();
                List<Mob> mobs = p.zone.getMonsters();
                for (Mob mob : mobs) {
                    // if (mob.template.isBoss()) {
                    // continue;
                    // }
                    JSONObject obj = new JSONObject();
                    obj.put("templateId", mob.template.id);
                    obj.put("x", mob.x);
                    obj.put("y", mob.y);
                    obj.put("status", 1);
                    json.add(obj);
                }
                NinjaUtils.saveFile("mob.txt", json.toJSONString().getBytes());
                p.getService().serverDialog("Đã lưu thành công");
                return true;
            }
            if (text.equals("coin")) {
                p.addCoin(100000000);
                p.getService().serverDialog("Đã lưu thành công");
                return true;
            }
        }
        return false;
    }
}
