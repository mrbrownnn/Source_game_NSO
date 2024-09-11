
package com.nsoz.item;

/**
 *
 * @author Administrator
 */
public class ItemTemplate {

    public static final int TYPE_BODY_MIN = 0;
    public static final int TYPE_BODY_MAX = 15;

    public static final int TYPE_NON = 0;
    public static final int TYPE_VUKHI = 1;
    public static final int TYPE_AO = 2;
    public static final int TYPE_LIEN = 3;
    public static final int TYPE_GANGTAY = 4;
    public static final int TYPE_NHAN = 5;
    public static final int TYPE_QUAN = 6;
    public static final int TYPE_NGOCBOI = 7;
    public static final int TYPE_GIAY = 8;
    public static final int TYPE_PHU = 9;
    public static final int TYPE_THUNUOI = 10;
    public static final int TYPE_MATNA = 11;
    public static final int TYPE_AOCHOANG = 12;
    public static final int TYPE_BAOTAY = 13;
    public static final int TYPE_MATTHAN = 14;
    public static final int TYPE_BIKIP = 15;
    public static final int TYPE_HP = 16;
    public static final int TYPE_MP = 17;
    public static final int TYPE_EAT = 18;
    public static final int TYPE_MONEY = 19;
    public static final int TYPE_TUI_TIEN = 20;
    public static final int TYPE_MEAT = 21;
    public static final int TYPE_DRAGONBALL = 22; // ngọc rồng
    public static final int TYPE_TASK_SAVE = 23; // vật phẩm nhiệm vụ có lưu
    public static final int TYPE_TASK_WAIT = 24; // vật phẩm nhiệm vụ chờ
    public static final int TYPE_TASK = 25; // vật phẩm nhiệm vụ
    public static final int TYPE_CRYSTAL = 26; // huyền tinh
    public static final int TYPE_ORDER = 27; // những thứ khác
    public static final int TYPE_PROTECT = 28; // bảo hiểm
    public static final int TYPE_MON0 = 29; // thú cưỡi
    public static final int TYPE_MON1 = 30; // thú cưỡi
    public static final int TYPE_MON2 = 31; // thú cưỡi
    public static final int TYPE_MON3 = 32; // thú cưỡi
    public static final int TYPE_MON4 = 33; // thú cưỡi
    public static final int TYPE_NGOC_KHAM = 34; // thú cưỡi

    public int id;
    public String name;
    public String description;
    public byte type;
    public byte gender;
    public short level;
    public short icon;
    public short part;
    public short fashion;
    public boolean isUpToUp;

    public boolean isKunai() {
        return this.id == 99 || this.id == 100 || this.id == 101 || this.id == 102 || this.id == 103 || this.id == 333
                || this.id == 508 || this.id == 634 || this.id == 371 || this.id == 1131|| this.id == 1185;
    }

    public boolean isKiem() {
        return this.id == 194 || this.id == 94 || this.id == 95 || this.id == 96 || this.id == 97 || this.id == 98
                || this.id == 369 || this.id == 506 || this.id == 632 || this.id == 369 || this.id == 331 || this.id == 1129|| this.id == 1185;
    }

    public boolean isDao() {
        return this.id == 104 || this.id == 105 || this.id == 106 || this.id == 107 || this.id == 108 || this.id == 373
                || this.id == 335 || this.id == 510 || this.id == 636|| this.id == 1133|| this.id == 1185;
    }

    public boolean isCung() {
        return this.id == 109 || this.id == 110 || this.id == 111 || this.id == 112 || this.id == 113 || this.id == 372
                || this.id == 334 || this.id == 509 || this.id == 635|| this.id == 1132|| this.id == 1185;
    }

    public boolean isTieu() {
        return this.id == 114 || this.id == 115 || this.id == 116 || this.id == 117 || this.id == 118 || this.id == 370
                || this.id == 332 || this.id == 507 || this.id == 633 || this.id == 1130|| this.id == 1185;
    }

    public boolean isQuat() {
        return this.id == 119 || this.id == 120 || this.id == 121 || this.id == 122 || this.id == 123 || this.id == 374
                || this.id == 336 || this.id == 511 || this.id == 637|| this.id == 1134|| this.id == 1185;
    }

    public boolean isTypeClothe() {
        if (this.type == TYPE_NON || this.type == TYPE_AO || this.type == TYPE_GANGTAY || this.type == TYPE_QUAN
                || this.type == TYPE_GIAY) {
            return true;
        }
        return false;
    }

    public boolean isTypeMartialArtBook() {
        if ((this.id >= 40 && this.id <= 93) || (this.id >= 311 && this.id <= 316) || (this.id >= 375 && this.id <= 380) || (this.id >= 547 && this.id <= 563) || (this.id >= 839 && this.id <= 844)) {
            return true;
        }
        return false;
    }

    public boolean isTypeBody() {
        if (TYPE_BODY_MIN <= this.type && this.type <= TYPE_BODY_MAX) {
            return true;
        }
        return false;
    }

    public boolean isTypeAdorn() {
        if (this.type == TYPE_LIEN || this.type == TYPE_NHAN || this.type == TYPE_NGOCBOI || this.type == TYPE_PHU) {
            return true;
        }
        return false;
    }

    public boolean isTypeStack() {
        if (this.type == TYPE_HP || this.type == TYPE_MP) {
            return true;
        }
        return false;
    }

    public boolean isTypeCrystal() {
        if (this.type == TYPE_CRYSTAL) {
            return true;
        }
        return false;
    }

    public boolean isTypeWeapon() {
        if (this.type == TYPE_VUKHI) {
            return true;
        }
        return false;
    }

    public boolean isTypeMount() {
        if (TYPE_MON0 <= this.type && this.type <= TYPE_MON4) {
            return true;
        }
        return false;
    }

    public boolean isTypeNgocKham() {
        return (type == TYPE_NGOC_KHAM);
    }

    public boolean isTypeBiKip() {
        return (type == TYPE_BIKIP);
    }

    public boolean isTypeMatThan() {
        return (type == TYPE_MATTHAN);
    }
    
    public boolean isTypeYoroi() {
        return (type == TYPE_AOCHOANG);
    }

    public int getUpMax() {
        if (level < 10) {
            return 0;
        }
        if (level >= 10 && level < 20) {
            return 4;
        }
        if (level >= 20 && level < 40) {
            return 8;
        }
        if (level >= 40 && level < 50) {
            return 12;
        }
        if (level >= 50 && level < 60) {
            return 14;
        }
        return 16;
    }

    public boolean checkSys(int sys) {
        if ((isKiem() && (sys == 1 || sys == 0)) || (isTieu() && sys == 2) || (isKunai() && sys == 3)
                || (isCung() && sys == 4) || (isDao() && sys == 5) || (isQuat() && sys == 6)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "id: " + this.id + " name: " + this.name;
    }

    public boolean isBlackListItem() {
        return this.id >= 292 && this.id <= 305;
    }

    public boolean isTypeEquipmentBijuu() {
        if (this.type == 35 || this.type == 36 || this.type == 37 || this.type == 38) {
            return true;
        }
        return false;
    }

    public boolean isTypeBijuu() {
        if (this.type == 10 && this.id >= 924 && this.id <= 1047) {
            return true;
        }
        return false;
    }
}
