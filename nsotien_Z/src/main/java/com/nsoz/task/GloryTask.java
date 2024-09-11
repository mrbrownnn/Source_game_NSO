package com.nsoz.task;

import java.util.ArrayList;

import com.nsoz.item.ItemManager;
import com.nsoz.model.Char;
import com.nsoz.util.NinjaUtils;

public class GloryTask {

    public static final byte TIEU_DIET_TINH_ANH = 0;
    public static final byte TIEU_DIET_THU_LINH = 1;
    public static final byte CHIEN_THANG_LOI_DAI = 2;
    public static final byte CUU_SAT_NGUOI_KHAC = 3;
    public static final byte NHAT_YEN = 4;
    public static final byte NANG_CAP = 5;

    public static final String TITLE_NANG_CAP = "Nâng cấp vật phẩm";
    public static final String TITLE_TIEU_DIET_THU_LINH = "Tiêu diệt thủ lĩnh";
    public static final String TITLE_TIEU_DIET_TINH_ANH = "Tiêu diệt tinh anh";
    public static final String TITLE_CHIEN_THANG_LOI_DAI = "Chiến thắng lôi đài";
    public static final String TITLE_CUU_SAT_NGUOI_KHAC = "Cừu sát người khác";
    public static final String TITLE_NHAT_YEN = "Nông dân chăm chỉ";

    public static final String TASK_NANG_CAP = "- Nâng cấp %d/%d món lên cấp 8";
    public static final String TASK_TIEU_DIET_TINH_ANH = "- Tiêu diệt %d/%d quái tinh anh không chênh lệch quá 10 cấp độ";
    public static final String TASK_TIEU_DIET_THU_LINH = "- Tiêu diệt %d/%d quái thủ lĩnh không chênh lệch quá 10 cấp độ";
    public static final String TASK_CHIEN_THANG_LOI_DAI = "- Chiến thắng %d/%d trận lôi đài với người không chênh lệch quá 10 cấp độ";
    public static final String TASK_CUU_SAT_NGUOI_KHAC = "- Cừu sát %d/%d người khác nhau không chênh lệch quá 10 cấp độ";
    public static final String TASK_NHAT_YEN = "- Kiếm %d/%d yên từ quái vật";

    public static final int[][] TASKS = {{TIEU_DIET_THU_LINH, 5}, {TIEU_DIET_TINH_ANH, 10},
    {CHIEN_THANG_LOI_DAI, 5}, {CUU_SAT_NGUOI_KHAC, 10}, {NHAT_YEN, 1000000}, {NANG_CAP, 1}};

    public Char _char;
    public int requireUseEquip;
    public int type;
    public int quantity;
    public int progress;
    public ArrayList<Integer> characterIds;

    public GloryTask(Char _char) {
        this._char = _char;
        int rand = NinjaUtils.nextInt(TASKS.length);
        this.type = rand;
        this.progress = 0;
        this.quantity = 0;
        for (int[] task : TASKS) {
            if (task[0] == type) {
                this.quantity = task[1];
            }
        }
        this.requireUseEquip = ItemManager.getInstance().randomItemGloryTask(_char);
        if (this.type == CUU_SAT_NGUOI_KHAC) {
            this.characterIds = new ArrayList<>();
        }
    }

    public GloryTask(Char _char, int type, int quantity, int progress, int requireUseEquip) {
        this._char = _char;
        this.type = type;
        this.quantity = quantity;
        this.progress = progress;
        this.requireUseEquip = requireUseEquip;
        if (this.type == CUU_SAT_NGUOI_KHAC) {
            this.characterIds = new ArrayList<>();
        }
    }

    public boolean isComplete() {
        return progress >= quantity;
    }

    public String getTaskProgress() {
        String task1 = "- Sử dụng " + ItemManager.getInstance().getItemName(requireUseEquip);
        String task2 = "";
        switch (this.type) {
            case NANG_CAP:
                task2 = String.format(TASK_NANG_CAP, this.progress, quantity);
                break;

            case TIEU_DIET_THU_LINH:
                task2 = String.format(TASK_TIEU_DIET_THU_LINH, this.progress, quantity);
                break;

            case TIEU_DIET_TINH_ANH:
                task2 = String.format(TASK_TIEU_DIET_TINH_ANH, this.progress, quantity);
                break;

            case CHIEN_THANG_LOI_DAI:
                task2 = String.format(TASK_CHIEN_THANG_LOI_DAI, this.progress, quantity);
                break;

            case CUU_SAT_NGUOI_KHAC:
                task2 = String.format(TASK_CUU_SAT_NGUOI_KHAC, this.progress, quantity);
                break;

            case NHAT_YEN:
                task2 = String.format(TASK_NHAT_YEN, this.progress, quantity);
                break;
        }
        return task1 + "\n" + task2;
    }

    public String getTaskTitle() {
        String title = "";
        switch (this.type) {
            case NANG_CAP:
                title = TITLE_NANG_CAP;
                break;

            case TIEU_DIET_THU_LINH:
                title = TITLE_TIEU_DIET_THU_LINH;
                break;

            case TIEU_DIET_TINH_ANH:
                title = TITLE_TIEU_DIET_TINH_ANH;
                break;

            case CHIEN_THANG_LOI_DAI:
                title = TITLE_CHIEN_THANG_LOI_DAI;
                break;

            case CUU_SAT_NGUOI_KHAC:
                title = TITLE_CUU_SAT_NGUOI_KHAC;
                break;

            case NHAT_YEN:
                title = TITLE_NHAT_YEN;
                break;
        }
        return title;
    }

    public String getTask() {
        String title = getTaskTitle() + "\n";
        String taskProgress = getTaskProgress() + "\n";
        String str = "";
        if (isComplete()) {
            str = "- Hoàn thành nhiệm vụ. Hãy gặp Ameji để trả nhiệm vụ\n";
        }
        return title + taskProgress + str;
    }

    public void updateProgress(int update) {
        int equipType = getEquipType();
        if (_char.equipment[equipType] != null && _char.equipment[equipType].id == requireUseEquip) {
            this.progress += update;
            if (isComplete()) {
                this.progress = this.quantity;
                _char.serverMessage("Hoàn thành nhiệm vụ. Hãy gặp Ameji để trả nhiệm vụ.");
            } else {
                _char.serverMessage(getTaskProgress());
            }
        }
    }

    public int getEquipType() {
        return ItemManager.getInstance().getItemTemplate(this.requireUseEquip).type;
    }

    public boolean isExistCharacterId(int characterId) {
        if (characterIds.contains(characterId)) {
            return true;
        }
        return false;
    }

    public void addCharacterId(int characterId) {
        if (!isExistCharacterId(characterId)) {
            characterIds.add(characterId);
        }
    }

}
