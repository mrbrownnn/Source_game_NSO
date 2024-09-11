package com.nsoz.mob;

import com.nsoz.constants.MobName;
import com.nsoz.constants.ItemName;
import com.nsoz.item.Item;
import com.nsoz.map.zones.Zone;

import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.nsoz.event.Event;
import com.nsoz.event.KoroKing;
import com.nsoz.model.Figurehead;
import com.nsoz.model.Char;
import com.nsoz.effect.Effect;
import com.nsoz.task.GloryTask;
import com.nsoz.map.item.ItemMap;
import com.nsoz.constants.MapName;
import com.nsoz.store.ItemStore;
import com.nsoz.model.RandomItem;
import com.nsoz.task.TaskOrder;
import com.nsoz.map.world.Territory;
import com.nsoz.constants.TaskName;
import com.nsoz.convert.Converter;
import com.nsoz.event.Halloween;
import com.nsoz.event.Noel;
import com.nsoz.item.ItemFactory;
import com.nsoz.util.NinjaUtils;
import com.nsoz.lib.RandomCollection;
import com.nsoz.map.item.ItemMapFactory;
import com.nsoz.party.Group;
import com.nsoz.store.StoreManager;
import com.nsoz.util.Log;

import java.util.Vector;

import lombok.Getter;
import lombok.Setter;

public class Mob {

    public static final byte YEN = 0;
    public static final byte ITEM = 1;
    public static final byte ITEM_TASK = 2;
    public static final byte SUSHI = 3;
    public static final byte BOSS = 4;
    public static final byte EQUIP = 5;
    public static final byte LANG_CO = 6;
    public static final byte VDMQ = 8;
    public static final byte EVENT = 7;
    public static final byte LANG_TRUYEN_THUYET = 9;
    public static final byte CHIEN_TRUONG = 10;
    public static final byte CHIA_KHOA_CO_QUAN = 11;
    public static final byte LAM_THAO_DUOC = 12;
    public static final byte BOSS_LDGT = 13;
    public static final byte LANH_DIA_GIA_TOC = 14;
    public static final byte BI_MA = 15;
    public static final byte UPYEN = 16;
    public static final byte vt = 17;

    public int id;
    public boolean isDisable;
    public boolean isDontMove;
    public boolean isFire;
    public boolean isIce;
    public boolean isWind;
    public byte sys;
    public double hp;
    public double maxHP;
    public double originalHp;
    public short level;
    public short x;
    public short y;
    public MobTemplate template;
    public byte status;
    public byte levelBoss;
    public boolean isBoss;
    public long lastTimeAttack;
    public long attackDelay = 3000;
    public int recoveryTimeCount;
    public Vector<Integer> chars;
    public boolean isDead;
    public int damageOnPlayer, damageOnPlayer2;
    public int damageOnMob, damageOnMob2;
    @Setter
    public Zone zone;
    public ItemMap itemMap;
    public boolean isBusyAttackSokeOne;
    public boolean isCantRespawn;
    @Setter
    @Getter
    private boolean isBeast;
    public Lock lock = new ReentrantLock();

    public Hashtable<Byte, Effect> effects = new Hashtable<>();

    public Mob(short templateId, boolean isBoss) {
        this.id = -1;
        this.template = MobManager.getInstance().find(templateId);
        this.isBoss = isBoss;
        this.hp = 0;
        this.maxHP = 0;
        this.isDisable = false;
        this.isDontMove = false;
        this.isFire = false;
        this.isIce = false;
        this.isWind = false;
        this.sys = 1;
        this.status = 4;
        this.levelBoss = 0;
        this.zone = null;
    }

    public Mob(int id, short templateId, double hp, short level, short x, short y, boolean isBeast, boolean isBoss) {
        this.id = id;
        this.template = MobManager.getInstance().find(templateId);
        this.originalHp = hp;
        this.level = level;
        this.x = x;
        this.y = y;
        this.isDisable = false;
        this.isDontMove = false;
        this.status = 5;
        this.isBoss = isBoss;
        this.levelBoss = 0;
        this.isFire = this.isIce = this.isWind = false;
        this.isDead = false;
        this.chars = new Vector<>();
        setBeast(isBeast);
        setClass();
        if (templateId == MobName.HEO_RUNG || templateId == MobName.HEO_MOI || zone.tilemap.isThatThuAi()) {
            this.isCantRespawn = true;
        }
        setLevelBoss();
        setHP();
        setDamage();
    }

    public Mob(int id, short templateId, double hp, short level, short x, short y, boolean isBeast, boolean isBoss,
               Zone zone) {
        this.id = id;
        this.template = MobManager.getInstance().find(templateId);
        this.originalHp = hp;
        this.level = level;
        this.x = x;
        this.y = y;
        this.isDisable = false;
        this.isDontMove = false;
        this.status = 5;
        this.isBoss = isBoss;
        this.levelBoss = 0;
        this.isFire = this.isIce = this.isWind = false;
        this.isDead = false;
        this.chars = new Vector<>();
        setZone(zone);
        setBeast(isBeast);
        setClass();
        if (templateId == MobName.HEO_RUNG || templateId == MobName.HEO_MOI || zone.tilemap.isThatThuAi()) {
            this.isCantRespawn = true;
        }
        setLevelBoss();
        setHP();
        setDamage();
    }

    public void setClass() {
        this.sys = (byte) (int)NinjaUtils.nextInt(1, 3);
    }

    public void setLevelBoss() {
        if (template.id == MobName.HEO_RUNG || template.id == MobName.HEO_MOI) {
            this.levelBoss = 2;
        } else if (template.id == MobName.MOC_NHAN) {
            this.levelBoss = 0;
        } else if (zone.tilemap.isDungeo()) {
            this.levelBoss = 0;
            if (zone.tilemap.id == 116) {
                if (this.id == 82 || this.id == 85) {
                    this.levelBoss = 1;
                }
            } else {
                if (template.id == MobName.NINJA_HAC_AM || template.id == MobName.THIEN_VUONG
                        || template.id == MobName.NGAN_LANG_VUONG) {
                    this.levelBoss = 2;
                }
            }
        } else if (zone.tilemap.isDungeoClan()) {
            this.levelBoss = 0;
            if (this.template.id == MobName.BAO_QUAN) {
                this.levelBoss = 2;
            }
        } else if (zone.tilemap.id == MapName.DIA_DAO_CHIKATOYA || zone.tilemap.id == MapName.THAT_THU_AI) {
            this.levelBoss = 0;
        } else {
            if (this.levelBoss == 3) {
                return;
            }
            if (isBeast) {
                this.levelBoss = 3;
            } else if (zone.numberChief < 1 && (int)NinjaUtils.nextInt(100) == 1 && this.level >= 10 && !this.isBoss) {
                this.levelBoss = 2;
                zone.numberChief++;
            } else if (zone.numberElitez < 2 && (int)NinjaUtils.nextInt(50) == 1 && this.level >= 10 && !this.isBoss) {
                this.levelBoss = 1;
                zone.numberElitez++;
            } else {
                this.levelBoss = 0;
            }
        }
    }

    public void setDamage() {
        this.damageOnPlayer = (int) (this.level + (Math.pow(this.level, 2) / 5));
        if (this.isBoss) {
            this.damageOnPlayer *= 20;
        } else if (this.levelBoss == 1) {
            this.damageOnPlayer *= 2;
        } else if (this.levelBoss == 2) {
            this.damageOnPlayer *= 3;
        }
        this.damageOnPlayer2 = this.damageOnPlayer - this.damageOnPlayer / 10;
    }

    public void setHP() {
        if (this.levelBoss == 1) {
            this.hp = this.maxHP = this.originalHp * 10;
        } else if (this.levelBoss == 2) {
            this.hp = this.maxHP = this.originalHp * 100;
        } else if (this.levelBoss == 3) {
            this.hp = this.maxHP = this.originalHp * 200;
        } else {
            this.hp = this.maxHP = this.originalHp;
        }
        if (template.id == MobName.HEO_RUNG || template.id == MobName.HEO_MOI) {
            this.hp = this.maxHP = this.originalHp;
        }if (zone.tilemap.id == 181) {
                    this.hp = 1000000;
                    this.maxHP = 1000000;
            }if (zone.tilemap.id == 182) {
                    this.hp = 1500000;
                    this.maxHP = 1500000;
            }if (zone.tilemap.id == 183) {
                    this.hp = 2000000;
                    this.maxHP = 2000000;
            }if (zone.tilemap.id == 184) {
                    this.hp = 2500000;
                    this.maxHP = 2500000;
            }
        if (zone.tilemap.id == 185) {
                    this.hp = 3000000;
                    this.maxHP = 3000000;
            }
        if (this.maxHP < 0) {
            this.hp = this.maxHP = Integer.MAX_VALUE;
        }
    }

    public void recovery() {
        this.itemMap = null;
        this.isDead = false;
        setClass();
        setLevelBoss();
        setHP();
        setDamage();
        this.status = 5;
        this.isFire = false;
        this.isIce = false;
        this.isWind = false;
        this.isDontMove = false;
        this.isDisable = false;
        this.effects.clear();
    }

    public void die() {
        switch (this.levelBoss) {
            case 3:
                break;
            case 2:
                zone.numberChief--;
                break;
            case 1:
                zone.numberElitez--;
                break;
            default:
                break;
        }
        if (zone.numberChief < 0) {
            zone.numberChief = 0;
        }
        if (zone.numberElitez < 0) {
            zone.numberElitez = 0;
        }
        this.hp = 0;
        this.status = 0;
        this.recoveryTimeCount = 12;
        if (isBeast) {
            this.recoveryTimeCount = 300;
        }
        if (this.zone.tilemap.isChienTruong()) {
            this.recoveryTimeCount = 300;
            if (this.template.id == MobName.BACH_LONG_TRU || this.template.id == MobName.HAC_LONG_TRU) {
                this.recoveryTimeCount += 300;
            }
        } else if (this.template.id == MobName.HOP_BI_AN) {
            this.recoveryTimeCount = 65;
        } else if (this.template.id == MobName.NGUOI_TUYET || this.template.id == MobName.CHUOT_CANH_TY) {
            this.recoveryTimeCount = 900;
        }
        this.isDead = true;
        this.chars.clear();
    }

    public int randomItemID() {
        int itemID = RandomItem.ITEM.next();
        if (!isBoss && itemID == ItemName.DA_CAP_1) {
            itemID = this.level / 15;
            itemID = itemID > ItemName.DA_CAP_5 ? ItemName.DA_CAP_5 : itemID;
        } else if (itemID == ItemName.BINH_HP_CUC_TIEU) {
            if (this.level < 10) {
                itemID = ItemName.BINH_HP_CUC_TIEU;
            } else if (this.level < 30) {
                itemID = ItemName.BINH_HP_TIEU;
            } else if (this.level < 40) {
                itemID = ItemName.BINH_HP_VUA;
            } else if (this.level < 70) {
                itemID = ItemName.BINH_HP_LON;
            } else if (this.level < 90) {
                itemID = ItemName.BINH_HP_CUC_LON;
            } else {
                itemID = ItemName.BINH_HP_CAO_CAP;
            }
        } else if (itemID == ItemName.BINH_MP_CUC_TIEU) {
            if (this.level < 10) {
                itemID = ItemName.BINH_MP_CUC_TIEU;
            } else if (this.level < 30) {
                itemID = ItemName.BINH_MP_TIEU;
            } else if (this.level < 40) {
                itemID = ItemName.BINH_MP_VUA;
            } else if (this.level < 70) {
                itemID = ItemName.BINH_MP_LON;
            } else if (this.level < 90) {
                itemID = ItemName.BINH_MP_CUC_LON;
            } else {
                itemID = ItemName.BINH_MP_CAO_CAP;
            }
        }
        return itemID;
    }

    public void dropItem(Char owner, byte type) {
        try {
            if (zone.getNumberItem() > 100) {
                return;
            }
            Item itm = null;
            int itemId = 0;
            if (type == ITEM) {
                itemId = randomItemID();
            } else if (type == LANG_CO) {
                itemId = RandomItem.LANG_CO.next();
                if (this.levelBoss == 1 && (int)NinjaUtils.nextInt(1000) == 0) {
                    itemId = ItemName.HARLEY_DAVIDSON;
                }
            }else if (type == UPYEN) {
                itemId = RandomItem.UPYEN.next();
            }
            
            else if (zone.map.id == 169) {
                    itemId = RandomItem.vt.next();
                }
            
            else if (type == LANG_TRUYEN_THUYET) {
                itemId = RandomItem.LANG_TRUYEN_THUYET.next();
                if (this.levelBoss == 1 && (int)NinjaUtils.nextInt(1000) == 0) {
                    itemId = ItemName.HARLEY_DAVIDSON;
                }
            } else if (type == VDMQ) {
                itemId = RandomItem.VDMQ.next();
                if (itemId == ItemName.PHAN_THAN_LENH && this.level < 90) {
                    return;
                }
            } else if (type == LANH_DIA_GIA_TOC) {
                itemId = RandomItem.LANH_DIA_GIA_TOC.next();
            } else if (type == BOSS_LDGT) {
                itemId = RandomItem.BOSS_LDGT.next();
            } else if (type == CHIEN_TRUONG) {
                itemId = 846;// chìa khóa
            } else if (type == CHIA_KHOA_CO_QUAN) {
                itemId = ItemName.CHIA_KHOA_LANH_DIA_GIA_TOC;// chìa khóa
            } else if (type == LAM_THAO_DUOC) {
                itemId = ItemName.LAM_THAO_DUOC;// lam thảo dược
            } else if (type == YEN) {
                itemId = ItemName.YEN;
            } else if (type == ITEM_TASK) {
                itemId = owner.getIdItemTask(template.id);
                if (itemId == -1) {
                    return;
                }
            } else if (type == EVENT) {
                itemId = Event.getEvent().randomItemID();
                if (itemId == -1) {
                    return;
                }
            } else if (type == BI_MA) {
                Halloween halloween = (Halloween) Event.getEvent();
                itemId = halloween.randomItemID2();
            } else if (type == SUSHI) {
                itemId = ItemName.SUSHI;
            } else if (type == BOSS) {
                if (zone.map.id == 167) {
                    itemId = RandomItem.BOSS_LDGT.next();
                } else if (zone.map.id >= 162) {
                    itemId = RandomItem.BOSS_LANG_TRUYEN_THUYET.next();
                } else if (template.id == MobName.HOA_KY_LAN || template.id == MobName.TU_HA_MA_THAN
                        || template.id == MobName.CHUOT_CANH_TY || template.id == 227
                        || template.id == MobName.KING_HEO || template.id == MobName.MY_HAU_TUONG
                        || template.id == MobName.HOA_KY_LAN_2) {
                    itemId = RandomItem.BOSS_EVENT.next();
                } else {
                    if (this.level >= 90) {
                        itemId = RandomItem.BOSS_VDMQ.next();
                    } else {
                        itemId = RandomItem.BOSS.next();
                    }
                }
            } else if (type == EQUIP) {
                int levelMin = this.level / 10 * 10;
                int levelMax = levelMin + 9;
                List<ItemStore> list = StoreManager.getInstance().getListEquipmentWithLevelRange(levelMin, levelMax);
                if (list.isEmpty()) {
                    return;
                }
                int rd = (int)NinjaUtils.nextInt(list.size());
                ItemStore itemStore = list.get(rd);
                if (itemStore == null) {
                    return;
                }
                itm = Converter.getInstance().toItem(itemStore, Converter.RANDOM_OPTION);
                int n = (int)NinjaUtils.nextInt(itm.options.size() - 1);
                for (int i = 0; i < n; i++) {
                    int index = (int)NinjaUtils.nextInt(itm.options.size());
                    itm.options.remove(index);
                }
                if (n > 0) {
                    itm.yen = 5;
                }
            }
            Item item = null;
            if (type == EQUIP) {
                item = itm;
            } else {
                item = ItemFactory.getInstance().newItem(itemId);
            }
            if (item.id < 12 && !this.isBoss && this.levelBoss == 0) {
                item.isLock = true;
            }
            if (item.template.type == 25) {
                item.isLock = true;
            }
            if (item.id == ItemName.SUSHI) {
                if (owner.selectedSkill != null) {
                    item.setQuantity(owner.selectedSkill.options[0].param);
                }
            } else if (item.id == ItemName.YEN) {
                if (type == LANG_CO) {
                    item.setQuantity(10000);
                } else {
                    // yen roi
                    boolean isExceed = owner.level > this.level + 20 && owner.level < this.level - 20;
                    if (owner.level < 10) {
                        if (isExceed) {
                            item.setQuantity((int) ((int)NinjaUtils.nextInt(400, 600) * 0.10));
                        } else {
                            item.setQuantity((int)NinjaUtils.nextInt(400, 600));
                        }
                    } else if (owner.level < 20) {
                        if (isExceed) {
                            item.setQuantity((int) ((int)NinjaUtils.nextInt(600, 800) * 0.10));
                        } else {
                            item.setQuantity((int)(int)NinjaUtils.nextInt(600, 800));
                        }
                    } else if (owner.level < 30) {
                        if (isExceed) {
                            item.setQuantity((int) ((int)NinjaUtils.nextInt(800, 900) * 0.10));
                        } else {
                            item.setQuantity((int)NinjaUtils.nextInt(800, 900));
                        }
                    } else if (owner.level < 40) {
                        if (isExceed) {
                            item.setQuantity((int) ((int)NinjaUtils.nextInt(900, 1200) * 0.20));
                        } else {
                            item.setQuantity((int)NinjaUtils.nextInt(900, 1200));
                        }
                    } else if (owner.level < 50) {
                        if (isExceed) {
                            item.setQuantity((int) ((int)NinjaUtils.nextInt(1200, 1400) * 0.20));
                        } else {
                            item.setQuantity((int)NinjaUtils.nextInt(1200, 1400));
                        }
                    } else if (owner.level < 60) {
                        if (isExceed) {
                            item.setQuantity((int) ((int)NinjaUtils.nextInt(1400, 1600) * 0.20));
                        } else {
                            item.setQuantity((int)NinjaUtils.nextInt(1400, 1600));
                        }
                    } else if (owner.level < 70) {
                        if (isExceed) {
                            item.setQuantity((int) ((int)NinjaUtils.nextInt(1600, 2000) * 0.20));
                        } else {
                            item.setQuantity((int)NinjaUtils.nextInt(1600, 2000));
                        }
                    } else if (owner.level < 80) {
                        if (isExceed) {
                            item.setQuantity((int) ((int)NinjaUtils.nextInt(2000, 2500) * 0.20));
                        } else {
                            item.setQuantity((int)NinjaUtils.nextInt(2000, 2500));
                        }
                    } else if (owner.level < 90) {
                        if (isExceed) {
                            item.setQuantity((int) ((int)NinjaUtils.nextInt(2700, 3200) * 0.30));
                        } else {
                            item.setQuantity((int)NinjaUtils.nextInt(2700, 3200));
                        }
                    } else {
                        if (isExceed) {
                            item.setQuantity((int) ((int)NinjaUtils.nextInt(this.level * 50, this.level * 70) * 0.30));
                        } else {
                            item.setQuantity((int)NinjaUtils.nextInt(this.level * 50, (this.level + 10) * 70));
                        }
                    }
                }
                if (this.isBoss) {
                    item.setQuantity(50000);
                } else if (this.levelBoss == 1) {
                    item.setQuantity(item.getQuantity() * 2);
                } else if (this.levelBoss == 2) {
                    item.setQuantity(item.getQuantity() * 3);
                }
                if (!this.isBoss) {
                    if (owner != null) {
                        owner.addYen(item.getQuantity());
                        owner.serverMessage("Bạn nhận được " + item.getQuantity() + " yên.");

                        if (owner.gloryTask != null && owner.gloryTask.type == GloryTask.NHAT_YEN) {
                            owner.gloryTask.updateProgress(item.getQuantity());
                        }

                        return;
                    }
                }
            } else {
                item.setQuantity(1);
            }
            if (type == EVENT || type == BI_MA) {
                if (owner != null) {
                    if (owner.getSlotNull() > 0) {
                        owner.addItemToBag(item);
                        return;
                    }
                }
            }

            item.expire = -1;

            if (type == CHIA_KHOA_CO_QUAN || type == LAM_THAO_DUOC) {
                item.expire = System.currentTimeMillis() + (600000 * 3); //
            }
            short x = this.x;
            short y = this.y;
            if (type != SUSHI) {
                x = (short) (int)NinjaUtils.nextInt(this.x - 20, this.x + 20);
            }
            int temp = 0;
            if (x < 50) {
                x = 50;
            } else if (x > (temp = (zone.tilemap.tmw * 24))) {
                x = (short) (temp - 50);
            }
            y = zone.tilemap.collisionY(x, (short) (this.y / 24 * 24));

            ItemMap itemMap = ItemMapFactory.getInstance()
                    .builder()
                    .id(zone.numberDropItem++)
                    .x(x)
                    .y(y)
                    .build();
            itemMap.setOwnerID(owner.id);
            if (type == BOSS_LDGT) {
                itemMap.setOwnerID(-1);
            }
            if (item != null) {
                itemMap.setItem(item);
                zone.addItemMap(itemMap);
                if (type == SUSHI || type == ITEM_TASK) {
                    this.itemMap = itemMap;
                } else {
                    zone.getService().addItemMap(itemMap);
                }
            }
        } catch (Exception e) {
            Log.error("mob drop item err", e);
        }
    }

    private void attack() {
        Figurehead[] buNhins = zone.getBuNhins();
        for (int j = 0; j < buNhins.length; j++) {
            Figurehead buNhin = buNhins[j];
            int distance = NinjaUtils.getDistance(this.x, this.y, buNhin.x, buNhin.y);
            if ((this.isBoss && distance > 300) || (!this.isBoss && distance > 300)) {
                continue;
            }
            zone.getService().npcAttackBuNhin(this, j);
            return;
        }
        Vector<Char> list = new Vector<Char>();
        Vector<Char> chars = getChars();
        for (Char _char : chars) {
            if (_char.isCleaned) {
                continue;
            }
            if (_char.isInvisible()) {
                continue;
            }
            if (_char.isNhanBan) {
                continue;
            }
            if (_char.isModeCreate) {
                continue;
            }
            int distance = NinjaUtils.getDistance(this.x, this.y, _char.x, _char.y);
            if ((this.isBoss && distance > 600) || (!this.isBoss && distance > 300)) {
                continue;
            }
            list.add(_char);
        }
        if (list.isEmpty()) {
            return;
        }
        int rand = (int)NinjaUtils.nextInt(list.size());
        Char pl = list.get(rand);
        attack(pl, null);
    }

    public void attack(Char pl, Char owner) {
        if (pl != null && !pl.isDead) {
            pl.lock.lock();
            try {
                boolean isMobMe = (owner != null);
                double dameHp = (int)NinjaUtils.nextInt(this.damageOnPlayer2, this.damageOnPlayer);
                if (!isMobMe) {
                    if (zone.tilemap.isDungeo()) {
                        dameHp *= 2;
                    } else if (zone.tilemap.isDungeoClan()) {
                        dameHp = pl.hp * 80 / 100;
                        if (this.isBoss) {
                            dameHp *= 20;
                        } else if (this.levelBoss == 1) {
                            dameHp *= 2;
                        } else if (this.levelBoss == 2) {
                            dameHp *= 3;
                        }
                    } else if (zone.tilemap.isDauTruong() || zone.tilemap.isLoiDai()) {
                        dameHp = dameHp * 10 / 100;
                    }
                }
                dameHp -= pl.dameDown;
                if (pl.isReductionDame) {
                    dameHp -= dameHp * pl.options[136] / 100;
                }
                switch (sys) {
                    case 1:
                        dameHp -= dameHp * pl.options[127] / 100;
                        dameHp -= pl.resFire;
                        dameHp -= pl.options[48];
                        break;

                    case 2:
                        dameHp -= dameHp * pl.options[130] / 100;
                        dameHp -= pl.resIce;
                        dameHp -= pl.options[49];
                        break;

                    case 3:
                        dameHp -= dameHp * pl.options[131] / 100;
                        dameHp -= pl.resWind;
                        dameHp -= pl.options[50];
                        break;
                }

                Effect eff2 = pl.getEm().findByID((byte) 37);
                if (eff2 != null) {
                    dameHp -= dameHp * eff2.param / 100;
                }
                if (pl.isFire) {
                    dameHp += dameHp;
                }
                int level = this.level;
                // level = this.level > 0 ? this.level : 1;
                double exactly = (int)NinjaUtils.nextInt((level * 10) + 100);
                double miss = NinjaUtils.nextInt((int) (pl.miss + 100));
                boolean isMiss = exactly < miss;
                if (!isMobMe) {
                    if (zone.tilemap.isDungeoClan()) {
                        int effectId = -1;
                        int downTimeEffectId = -1;
                        int randEffectId = (int)NinjaUtils.nextInt(3);
                        if (zone.tilemap.id == 84
                                || ((zone.tilemap.id == 90 || zone.tilemap.id == 167) && randEffectId == 1)) {
                            effectId = 5;
                            downTimeEffectId = 40;
                        } else if (zone.tilemap.id == 85
                                || ((zone.tilemap.id == 90 || zone.tilemap.id == 167) && randEffectId == 2)) {
                            effectId = 7;
                            downTimeEffectId = 42;
                        } else if (zone.tilemap.id == 86
                                || ((zone.tilemap.id == 90 || zone.tilemap.id == 167) && randEffectId == 3)) {
                            effectId = 6;
                            downTimeEffectId = 41;
                        }

                        int randEffect = (int)NinjaUtils.nextInt(100);
                        if (effectId != -1 && downTimeEffectId != -1
                                && (randEffect < 10 || (randEffect < 50 && zone.tilemap.id == 90)
                                || (randEffect < 20 && zone.tilemap.id == 167))) {
                            Effect eff = new Effect(effectId, 3000, 0);
                            eff.addTime(-pl.options[downTimeEffectId] * 1000);
                            pl.getEm().setEffect(eff);
                        }
                    }
                }

                if (pl.isMiss) {
                    isMiss = true;
                }
                if (isMiss) {
                    dameHp = -1;
                } else {
                    if (dameHp <= 0) {
                        dameHp = 1;
                    }
                }
                double dameMp = 0;
                if (pl.isShieldMana) {
                    Effect eff = pl.getEm().findByType((byte) 6);
                    if (eff != null) {
                        if ((pl.mp * 100 / pl.maxMP) >= 10) {
                            dameMp = dameHp * eff.param / 100;
                            dameHp -= dameMp;
                            pl.addMp(-dameMp);
                        }
                    }
                }
                if ((pl.taskId == TaskName.NV_LAY_NUOC_HANG_SAU || pl.taskId == TaskName.NV_LAY_NUOC_HANG_SAU
                        || pl.taskId == TaskName.NV_HAI_NAM) && pl.isCatchItem) {
                    pl.isFailure = true;
                }
                if (isMobMe) {
                    if (dameHp > 0) {
                        owner.zone.getService().mobMeAttack(owner, pl);
                        owner.zone.getService().attackCharacter(dameHp, dameMp, pl);
                    }
                } else {
                    attack(pl, dameHp, dameMp);
                }
                dameHp = dameHp - dameHp * 80 / 100;
                if (dameHp > 0) {
                    pl.addHp(-dameHp);
                    if (pl.hp <= 0) {
                        pl.startDie();
                    }
                }

            } finally {
                pl.lock.unlock();
            }
        }
    }

    public void attack(Mob mob) {

    }

    public void dead(Char killer) {
        if (killer != null) {
            if (zone != null) {
                zone.mobDead(this, killer);
            }
            int dLevel = Math.abs(this.level - killer.level);
            if (Event.isKoroKing() && dLevel <= 10) {
                if ((int)NinjaUtils.nextInt(2000) == -1) {
                    ((KoroKing) Event.getEvent()).bornKoroKing(this);
                }
                if ((int)NinjaUtils.nextInt(2000) == 1) {
                    ((KoroKing) Event.getEvent()).infection(killer);
                }
            }
            if (killer.taskOrders != null) {
                for (TaskOrder task : killer.taskOrders) {
                    if (task.isComplete()) {
                        continue;
                    }
                    if (task.killId == this.template.id) {
                        if (task.taskId == TaskOrder.TASK_DAY) {
                            task.updateTask(1);
                        }
                        if (task.taskId == TaskOrder.TASK_BOSS) {
                            if (this.levelBoss == 3) {
                                task.updateTask(1);
                            }
                        }
                    }

                }
            }
            if (killer.taskMain != null) {
                if (killer.taskId != TaskName.NV_BAT_KHA_THI || ((killer.taskMain.index == 1 && this.levelBoss == 1)
                        || (killer.taskMain.index == 2 && this.levelBoss == 2))) {
                    killer.updateTaskKillMonster(this);
                    Group group = killer.getGroup();
                    if (group != null) {
                        List<Char> chars = group.getCharsInZone(killer.mapId, zone.id);
                        for (Char _char : chars) {
                            if (_char != null && _char != killer && !_char.isDead) {
                                if (_char.taskMain != null) {
                                    if (_char.taskMain.taskId == killer.taskMain.taskId
                                            && _char.taskMain.index == killer.taskMain.index) {
                                        _char.updateTaskKillMonster(this);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (this.template.id == MobName.NGUOI_TUYET) {
                if (killer.clan != null) {
                    for (Char mem : killer.clan.getOnlineMembers()) {
                        mem.serverMessage(killer.name + " đã tiêu diệt người tuyết");
                        mem.getEventPoint().addPoint(Noel.TOP_KILL_SNOWMAN, 100);
                    }
                }
            } else if (this.template.id == MobName.HOP_BI_AN) {
                killer.addBossVuiXuan(this.x, this.y);
            } else if (this.template.id == MobName.QUAI_VAT) {
                killer.rewardVuiXuan();
            } else if (this.template.id == MobName.CHUOT_CANH_TY) {
                // killer.addEventPoint(1, Events.TOP_CHUOT);
            } else if (this.template.id == MobName.BOSS_TUAN_LOC) {
                killer.getEventPoint().addPoint(Noel.TOP_KILL_REINDEER_KING, 1);
                killer.addExp(8000000);
                if (killer.getSlotNull() == 0) {
                    return;
                }
                RandomCollection<Integer> rc = RandomItem.VUA_TUAN_LOC;
                int itemId = rc.next();
                Item itm = ItemFactory.getInstance().newItem(itemId);
                killer.addItemToBag(itm);
            } else if (this.template.id != MobName.BU_NHIN) {
                if (this.isBoss) {
                    if (this.template.id == MobName.KORO_KING) {
                        int itemIndex = killer.getIndexItemByIdInBag(ItemName.VIEN_THUOC_THAN_KY);
                        killer.removeItem(itemIndex, 1, true);
                        killer.addExp(5000000);
                        if (killer.getSlotNull() > 0) {
                            RandomCollection<Integer> rc = RandomItem.BUA_MAY_MAN;
                            int itemId = rc.next();
                            Item itm = ItemFactory.getInstance().newItem(itemId);
                            itm.initExpire();
                            killer.addItemToBag(itm);
                        }
                        return;
                    }
                    if (zone.tilemap.isNormal()) {
                        if (killer.mapId >= 162) {
                            for (int i = 0; i < 20; i++) {
                                dropItem(killer, Mob.BOSS);
                            }
                        } else {
                            for (int i = 0; i < 20; i++) {
                                dropItem(killer, Mob.BOSS);
                            }
                        }
                    } else if (zone.tilemap.id == 167) {
                        for (int i = 0; i < 20; i++) {
                            dropItem(killer, Mob.BOSS_LDGT);
                        }
                    }
                    for (int i = 0; i < 10; i++) {
                        dropItem(killer, Mob.YEN);
                    }
                    if (Event.isVietnameseWomensDay() || Event.isInternationalWomensDay()) {
                        for (int i = 0; i < 20; i++) {
                            dropItem(killer, Mob.EVENT);
                        }
                    }
                } else {
                    int idItem = killer.getIdItemTask(this.template.id);
                    if (idItem != -1 && ((int)NinjaUtils.nextInt(3) == 0 || this.template.id == MobName.HEO_RUNG)) {
                        dropItem(killer, Mob.ITEM_TASK);
                    }
                    if (zone.tilemap.isLangCo()) {
                        if ((int)NinjaUtils.nextInt(8) == 0) {
                            dropItem(killer, Mob.LANG_CO);
                        }
                    }
                    if (zone.tilemap.isupyen()) {
                        if ((int)NinjaUtils.nextInt(10) == 0) {
                            dropItem(killer, Mob.UPYEN);
                        }
                    }
                    if (zone.tilemap.id == 169) {
                        if ((int)NinjaUtils.nextInt(10) == 10) {
                            dropItem(killer, Mob.vt);
                        }
                    }
                    
                    else if (zone.tilemap.isLangTruyenThuyet()) {
                        if ((int)NinjaUtils.nextInt(12) == 0) {
                            this.dropItem(killer, Mob.LANG_TRUYEN_THUYET);
                        }
                    } else if (zone.tilemap.isChienTruong()) {
                        if ((int)NinjaUtils.nextInt(100) == 0) {
                            dropItem(killer, Mob.CHIEN_TRUONG);
                        }
                    } else if (zone.tilemap.isDungeoClan() && zone.isLastBossWasBorn && this.levelBoss == 1) {
                        dropItem(killer, Mob.CHIA_KHOA_CO_QUAN);
                    } else if (zone.tilemap.isDungeoClan() && this.template.id == 81) {
                        if ((int)NinjaUtils.nextInt(10) == 0) {
                            dropItem(killer, Mob.LAM_THAO_DUOC);
                        }
                    } else if (zone.tilemap.isDungeoClan()
                            && (this.template.id == MobName.BAO_QUAN || this.template.id == MobName.TU_HA_MA_THAN)) {
                        for (int i = 0; i < 20; i++) {
                            dropItem(killer, Mob.BOSS_LDGT);
                        }
                    } else if (dLevel <= 7) {
                        int[] percents = {12, 10, 2, 76};
                        byte[] types = {Mob.YEN, Mob.ITEM, Mob.EQUIP, -1};
                        int index = NinjaUtils.randomWithRate(percents, 100);
                        byte type = types[index];
                        if (type == -1) {
                            // khai nhan phu thien nhan phu
                            if (zone.tilemap.isVDMQ()) {
                                if (killer.isTNP && (int)NinjaUtils.nextInt(250) < 4) { // default = 5
                                    type = Mob.VDMQ;
                                }
                                if (killer.isKNP && (int)NinjaUtils.nextInt(250) == 2) { // default = 0
                                    type = Mob.VDMQ;
                                }
                            }
                            if (zone.tilemap.isDungeoClan()) {
                                if ((int)NinjaUtils.nextInt(100) < 20) {
                                    type = Mob.LANH_DIA_GIA_TOC;
                                }
                            }
                        }
                        if (type != -1) {
                            dropItem(killer, type);
                        }
                    }
                    if (Event.isEvent()) {
                        int distance = 7;
                        int percentage = 30; // 30% ra item event
                        if (killer.isTNP || killer.isKNP) {
                            distance = 10;
                        }
                        if (killer.isTNP) {
                            percentage += 5;
                        } else if (killer.isKNP) {
                            percentage += 3;
                        }
                        if (zone.tilemap.isLangCo() || zone.tilemap.isVDMQ()) {
                            percentage += 3;
                        }
                        if (dLevel <= distance) {
                            int r = (int)NinjaUtils.nextInt(100);
                            if (r < percentage) {
                                dropItem(killer, EVENT);
                            }
                        }
                        if (killer.isBiMa && Event.isHalloween()) {
                            if (dLevel <= 10) {
                                int r = (int)NinjaUtils.nextInt(100);
                                if (r <= 5) {
                                    dropItem(killer, BI_MA);
                                }
                            }
                        }
                    }
                    if ((this.levelBoss == 1 || this.levelBoss == 2) && template.id != MobName.HEO_RUNG
                            && template.id != MobName.HEO_MOI) {
                        this.dropItem(killer, Mob.YEN);
                    }
                }
            }
            // isHuman
            if (killer.gloryTask != null) {
                if (Math.abs(killer.level - this.level) <= 10) {
                    if (this.levelBoss == 1) {
                        if (killer.gloryTask.type == GloryTask.TIEU_DIET_TINH_ANH) {
                            killer.gloryTask.updateProgress(1);
                        }
                    } else if (this.levelBoss == 2) {
                        if (killer.gloryTask.type == GloryTask.TIEU_DIET_THU_LINH) {
                            killer.gloryTask.updateProgress(1);
                        }
                    }
                }
            }
            if (killer.isModeRemove) {
                zone.waitingListDelete.add(this);
            } else {
                if (zone.tilemap.isDungeoClan()) {
                    if (this.template.id == MobName.LAM_THAO) {
                        zone.addMobForWatingListRespawn(this);
                    }
                } else if (!zone.tilemap.isDungeo()) {
                    if (!this.isBoss || this.template.id == MobName.HOP_BI_AN) {
                        zone.addMobForWatingListRespawn(this);
                    } else {
                        if (this.template.id == MobName.BOSS_TUAN_LOC || this.template.id == MobName.QUAI_VAT) {
                            killer.mob = null;
                        } else {
                            zone.waitingListDelete.add(this);
                        }
                    }
                }
                //fix hang9x
                else if (zone.tilemap.isDungeo() && (zone.tilemap.id == 157 || zone.tilemap.id == 158 || zone.tilemap.id == 159) && !this.isBoss) {
                    zone.addMobForWatingListRespawn(this);
                }
            }
        }
    }

    public void attack(Char p, double dameHp, double dameMp) {
        try {
            p.getService().npcAttackMe(this, dameHp, dameMp);
            zone.getService().npcAttackPlayer(this, p);
        } catch (Exception e) {
            Log.error("mob attack er", e);
        }
    }

    public void addCharId(int charId) {
        if (!chars.contains(charId)) {
            chars.add(charId);
        }
    }

    public boolean checkExist(int charId) {
        for (int id : chars) {
            if (id == charId) {
                return true;
            }
        }
        return false;
    }

    public Vector<Char> getChars() {
        Vector<Char> chars = new Vector<>();
        Vector<Integer> clone = (Vector<Integer>) this.chars.clone();
        for (int id : clone) {
            Char _char = zone.findCharById(id);
            if (_char != null) {
                chars.addElement(_char);
            }
        }
        return chars;
    }

    public Char randomChar() {
        Char _char = null;
        Vector<Integer> chars = (Vector<Integer>) this.chars.clone();
        do {
            int size = chars.size();
            if (size == 0) {
                break;
            }
            int index = (int)NinjaUtils.nextInt(size);
            int id = chars.get(index);
            Char tmp = zone.findCharById(id);
            if (tmp == null) {
                chars.remove(index);
            } else {
                if (!tmp.isCleaned && !tmp.isDead && !tmp.isInvisible()) {
                    int distance = NinjaUtils.getDistance(this.x, this.y, tmp.x, tmp.y);
                    if ((this.isBoss && distance > 600) || (!this.isBoss && distance > 300)) {
                        continue;
                    }
                    _char = tmp;
                    break;
                }
            }
        } while (_char == null);
        return _char;
    }

    public void update() {
        if (!this.isDead) {
            if (template.id != MobName.BACH_LONG_TRU && template.id != MobName.HAC_LONG_TRU) {
                if (template.id != MobName.BOSS_TUAN_LOC && template.id != MobName.NGUOI_TUYET
                        && template.id != MobName.HOP_BI_AN && template.id != MobName.QUAI_VAT) {
                    List<Char> list = zone.getChars();
                    if (list.size() > 0) {
                        int add = 0;
                        if (isBoss) {
                            add = 100;
                        }
                        for (Char _char : list) {
                            if (_char.isDead) {
                                continue;
                            }
                            if ((_char.faction == 0 && zone.tilemap.id == 99)
                                    || (_char.faction == 1 && zone.tilemap.id == 103) || _char.faction == 2) {
                                continue;
                            }
                            if (template.type == 4) {
                                int range = NinjaUtils.getDistance(this.x, this.y, _char.x, _char.y);
                                if (range < template.rangeMove + 50 + add) {
                                    if (!_char.isInvisible()) {
                                        addCharId(_char.id);
                                    }
                                }
                            } else {
                                if (this.y == _char.y && Math.abs(this.x - _char.x) < template.rangeMove + 20 + add) {
                                    if (!_char.isInvisible()) {
                                        addCharId(_char.id);
                                    }
                                }
                            }
                        }
                    }
                }
                if (!isIce && !isWind && !isDisable && template.id != MobName.BU_NHIN && template.id != MobName.MOC_NHAN
                        && template.id != MobName.THAO_DUOC && chars.size() > 0) {
                    long now = System.currentTimeMillis();
                    if (now - this.lastTimeAttack > this.attackDelay) {
                        this.lastTimeAttack = now;
                        attack();
                    }
                }
            }
//          eff dot
            Effect eff5 = effects.get((byte) 5);
            if (eff5 != null) {
                callFireEffect(eff5);
            }
            Vector<Byte> removeEffect = new Vector<>();
            for (Entry<Byte, Effect> entry : this.effects.entrySet()) {
                Effect eff = entry.getValue();
                if (eff == null || eff.isExpired()) {
                    removeEffect.add(entry.getKey());
                }
            }
            for (byte b : removeEffect) {
                this.effects.remove(b);
                if (b == 1) {
                    zone.setFire(this, false);
                } else if (b == 2) {
                    zone.setIce(this, false);
                } else if (b == 3) {
                    zone.setWind(this, false);
                } else if (b == 14) {
                    zone.setMove(this, false);
                } else if (b == 0) {
                    zone.setDisable(this, false);
                }
            }
        }
    }

    public void callFireEffect(Effect eff5) {
        lock.lock();
        try {
            int charId = eff5.param2;
            int damage = eff5.param;

            zone.getService().callEffectNpc(this);
            double preHP = this.hp;
            Char p = zone.findCharById(charId);
            if (p == null) {
                return;
            }

            if (this.template.id == MobName.NGUOI_TUYET) {
                if (p.clan != null) {
                    damage = 1;
                } else {
                    damage = 0;
                }
            } else if (this.zone.tilemap.isDungeoClan() && this.hp - damage <= 0) {
                damage = 0;
            }

            if (this.template.id == MobName.BU_NHIN) {
                addHp(-(this.maxHP / 5));
            } else {
                addHp(-damage);
            }
            zone.getService().attackMonster(damage, false, this);
            double nextHP = this.hp;
            double hp = Math.abs(nextHP - preHP);
            p.addExp(this,(int) hp);

            if (this.hp <= 0) {
                this.die();
            }
            if (this.isDead) {
                Char killer = p.getOriginChar();
                this.dead(killer);
            }

            if (zone.tilemap.isDungeoClan()) {
                Territory.checkEveryAttack(p);
            }
        } finally {
            lock.unlock();
        }
    }

    public void addHp(double add) {
        this.hp += add;
    }
}
