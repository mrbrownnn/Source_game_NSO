
package com.nsoz.convert;

import com.nsoz.item.Equip;
import com.nsoz.item.Item;
import com.nsoz.item.ItemFactory;
import com.nsoz.item.ItemTemplate;
import com.nsoz.item.Mount;
import com.nsoz.server.Config;
import com.nsoz.skill.Skill;
import com.nsoz.option.ItemOption;
import com.nsoz.store.ItemStore;
import com.nsoz.util.NinjaUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Admin
 */
public class Converter {
    public static final byte MAX_OPTION = 0;
    public static final byte MIN_OPTION = 1;
    public static final byte RANDOM_OPTION = 2;

    private static Converter instance;

    public static Converter getInstance() {
        if (instance == null) {
            synchronized (Converter.class) {
                if (instance == null) {
                    instance = new Converter();
                }
            }
        }
        return instance;
    }

    public Item toItem(Equip equip) {
        Item item = new Item(equip.id);
        item.setNew(equip.isNew());
        item.setUpdatedAt(equip.getUpdatedAt());
        item.setCreatedAt(equip.getCreatedAt());
        item.upgrade = equip.upgrade;
        item.sys = equip.sys;
        item.expire = equip.expire;
        item.yen = equip.yen;
        item.isLock = equip.isLock;
        if (item.options != null) {
            item.options.clear();
            if (equip.options != null) {
                for (ItemOption o : equip.options) {
                    item.options.add(new ItemOption(o.optionTemplate.id, o.param));
                }
            }
        }
        if (item.gems != null) {
            item.gems.clear();
            if (equip.gems != null) {
                for (Item gem : equip.gems) {
                    item.addGem(gem);
                }
            }
        }
        item.setQuantity(1);
        return item;
    }

    public Item toItem(Mount mount) {
        Item item = new Item(mount.id);
        item.setNew(mount.isNew());
        item.setUpdatedAt(mount.getUpdatedAt());
        item.setCreatedAt(mount.getCreatedAt());
        item.upgrade = mount.upgrade;
        item.sys = mount.sys;
        item.expire = mount.expire;
        item.yen = mount.yen;
        item.isLock = mount.isLock;
        if (item.options != null) {
            item.options.clear();
            if (mount.options != null) {
                for (ItemOption o : mount.options) {
                    item.options.add(new ItemOption(o.optionTemplate.id, o.param));
                }
            }
        }
        item.setQuantity(1);
        return item;
    }

    public Item newItem(Item item) {
        Item newItem = new Item(item.id);
        newItem.upgrade = item.upgrade;
        newItem.setNew(item.isNew());
        newItem.setUpdatedAt(item.getUpdatedAt());
        newItem.setCreatedAt(item.getCreatedAt());
        newItem.sys = item.sys;
        newItem.expire = item.expire;
        newItem.yen = item.yen;
        newItem.isLock = item.isLock;
        if (newItem.options != null) {
            newItem.options.clear();
            if (item.options != null) {
                for (ItemOption o : item.options) {
                    newItem.options.add(new ItemOption(o.optionTemplate.id, o.param));
                }
            }
        }
        if (newItem.gems != null) {
            newItem.gems.clear();
            if (item.gems != null) {
                for (Item gem : item.gems) {
                    newItem.addGem(gem);
                }
            }
        }
        newItem.setQuantity(item.getQuantity());
        return newItem;
    }

    public Equip toEquip(Item item) {
        Equip equip = new Equip(item.id);
        equip.setNew(item.isNew());
        equip.setUpdatedAt(item.getUpdatedAt());
        equip.setCreatedAt(item.getCreatedAt());
        equip.upgrade = item.upgrade;
        equip.sys = item.sys;
        equip.expire = item.expire;
        equip.yen = item.yen;
        equip.isLock = item.isLock;
        if (equip.options != null) {
            equip.options.clear();
            if (item.options != null) {
                for (ItemOption o : item.options) {
                    equip.options.add(new ItemOption(o.optionTemplate.id, o.param));
                }
            }
        }
        if (equip.gems != null) {
            equip.gems.clear();
            if (item.gems != null) {
                for (Item gem : item.gems) {
                    equip.addGem(gem);
                }
            }
        }
        equip.setQuantity(1);
        return equip;
    }

    public Mount toMount(Item item) {
        Mount mount = new Mount(item.id);
        mount.setNew(item.isNew());
        mount.setUpdatedAt(item.getUpdatedAt());
        mount.setCreatedAt(item.getCreatedAt());
        mount.upgrade = item.upgrade;
        mount.sys = item.sys;
        mount.expire = item.expire;
        mount.yen = item.yen;
        mount.isLock = item.isLock;
        if (mount.options != null) {
            mount.options.clear();
            if (item.options != null) {
                for (ItemOption o : item.options) {
                    mount.options.add(new ItemOption(o.optionTemplate.id, o.param));
                }
            }
        }
        mount.setQuantity(1);
        return mount;
    }

    public Skill newSkill(Skill oldSkill) {
        Skill newSkill = new Skill();
        newSkill.id = oldSkill.id;
        newSkill.coolDown = oldSkill.coolDown;
        newSkill.dx = oldSkill.dx;
        newSkill.dy = oldSkill.dy;
        newSkill.level = oldSkill.level;
        newSkill.manaUse = oldSkill.manaUse;
        newSkill.maxFight = oldSkill.maxFight;
        newSkill.point = oldSkill.point;
        newSkill.template = oldSkill.template;
        newSkill.options = oldSkill.options;
        return newSkill;
    }

    public Item toItem(ItemStore itemStore, byte type) {
        Item newItem = ItemFactory.getInstance().newItem(itemStore.getItemID());

        ItemTemplate template = itemStore.getTemplate();
//      Ngoc 10
//      bo thuoc tinh ngau nhien cua ngoc neu la test
        if(template.id >= 652 && template.id <= 655 & Config.getInstance().isTestVersion()){
            newItem.options = new ArrayList<>();
            newItem.upgrade = 10;
        }
        long expire = itemStore.getExpire();
        if (expire != -1) {
            newItem.expire = (new Date()).getTime() + expire;
        } else {
            newItem.expire = -1;
        }
        newItem.isLock = itemStore.isLock();
        if (template.isTypeBody() || template.isTypeNgocKham() || template.isTypeMount()) {
            newItem.setQuantity(1);
            newItem.sys = itemStore.getSys();
            List<ItemOption> maxOptions = itemStore.getMaxOptions();
            List<ItemOption> minOptions = itemStore.getMinOptions();
            int num = maxOptions.size();
            for (int a = 0; a < num; a++) {
                ItemOption maxOption = maxOptions.get(a);
                ItemOption minOption = minOptions.get(a);
                int templateId = maxOption.optionTemplate.id;
                int param;
                if (type == RANDOM_OPTION) {
                    param =(int) NinjaUtils.nextInt(minOption.param, maxOption.param);
                } else if (type == MAX_OPTION) {
                    param = maxOption.param;
                } else {
                    param = minOption.param;
                }
                newItem.options.add(new ItemOption(templateId, param));
            }
        }
        return newItem;
    }
}
