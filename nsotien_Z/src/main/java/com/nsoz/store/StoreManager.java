
package com.nsoz.store;

import com.nsoz.constants.SQLStatement;
import com.nsoz.db.jdbc.DbManager;
import com.nsoz.item.ItemTemplate;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Builder;

public class StoreManager {

    public static final byte TYPE_WEAPON = 2;
    public static final byte TYPE_POTION = 6;
    public static final byte TYPE_POTION_LOCK = 7;
    public static final byte TYPE_FOOD = 8;
    public static final byte TYPE_FOOD_LOCK = 9;
    public static final byte TYPE_MISCELLANEOUS = 14;
    public static final byte TYPE_BOOK = 15;
    public static final byte TYPE_NECKLACE = 16;
    public static final byte TYPE_RING = 17;
    public static final byte TYPE_PEARL = 18;
    public static final byte TYPE_SPELL = 19;
    public static final byte TYPE_MEN_HAT = 20;
    public static final byte TYPE_WOMEN_HAT = 21;
    public static final byte TYPE_MEN_SHIRT = 22;
    public static final byte TYPE_WOMEN_SHIRT = 23;
    public static final byte TYPE_MEN_GLOVES = 24;
    public static final byte TYPE_WOMEN_GLOVES = 25;
    public static final byte TYPE_MEN_PANT = 26;
    public static final byte TYPE_WOMEN_PANT = 27;
    public static final byte TYPE_MEN_SHOES = 28;
    public static final byte TYPE_WOMEN_SHOES = 29;
    public static final byte TYPE_FASHION = 32;
    public static final byte TYPE_CLAN = 34;

    private static final StoreManager instance = new StoreManager();

    public static StoreManager getInstance() {
        return instance;
    }

    private final List<Store> stores = new ArrayList<>();

    public void init() {
        try {
            PreparedStatement ps = DbManager.getInstance().getConnection(DbManager.GAME).prepareStatement(SQLStatement.GET_ALL_STORE);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                if (id == TYPE_CLAN) {
                    add(new ClanStore(id, name));
                } else {
                    add(new Store(id, name));
                }
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(StoreManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean load() {
        for (Store store : stores) {
            boolean result = store.load();
            if (!result) {
                return false;
            }
        }
        return true;
    }

    public void add(Store store) {
        stores.add(store);
    }

    public void addItem(byte storeType, ItemStore item) {
        Store store = find(storeType);
        if (store != null) {
            store.add(item);
        }
    }

    public void remove(Store store) {
        stores.remove(store);
    }

    public Store find(byte type) {
        for (Store store : stores) {
            if (store.getType() == type) {
                return store;
            }
        }
        return null;
    }

    public List<ItemStore> getListEquipmentWithLevelRange(int start, int end) {
        ArrayList<ItemStore> items = new ArrayList<>();
        int[] types = {TYPE_WEAPON, TYPE_NECKLACE, TYPE_RING, TYPE_PEARL, TYPE_SPELL, TYPE_MEN_HAT, TYPE_MEN_SHIRT, TYPE_MEN_GLOVES, TYPE_MEN_PANT, TYPE_MEN_SHOES,
            TYPE_WOMEN_HAT, TYPE_WOMEN_SHIRT, TYPE_WOMEN_GLOVES, TYPE_WOMEN_PANT, TYPE_WOMEN_SHOES};
        for (int type : types) {
            Store store = find((byte) type);
            if (store != null) {
                store.stream().forEach((t) -> {
                    ItemTemplate template = t.getTemplate();
                    if (template.level >= start && template.level <= end) {
                        items.add(t);
                    }
                });
            }
        }
        return items;
    }

    public ItemStore getEquipment(int level, int sys, int gender) {
        int[] types = {TYPE_WEAPON, TYPE_NECKLACE, TYPE_RING, TYPE_PEARL, TYPE_SPELL, TYPE_MEN_HAT, TYPE_MEN_SHIRT, TYPE_MEN_GLOVES, TYPE_MEN_PANT, TYPE_MEN_SHOES,
            TYPE_WOMEN_HAT, TYPE_WOMEN_SHIRT, TYPE_WOMEN_GLOVES, TYPE_WOMEN_PANT, TYPE_WOMEN_SHOES};
        for (int type : types) {
            Store store = find((byte) type);
            if (store != null) {
                for (ItemStore t : store.getItems()) {
                    ItemTemplate template = t.getTemplate();
                    if (level % 10 == 0) {
                        if (!template.checkSys(sys)) {
                            continue;
                        }
                    } else {
                        if (t.getSys() != sys) {
                            continue;
                        }
                    }
                    int iLevel = template.level;
                    if (iLevel == 85) {
                        switch (template.type) {
                            case 0:
                                iLevel = 89;
                                break;

                            case 1:
                                iLevel = 80;
                                break;

                            case 2:
                                iLevel = 87;
                                break;

                            case 3:
                                iLevel = 88;
                                break;

                            case 4:
                                iLevel = 85;
                                break;

                            case 5:
                                iLevel = 86;
                                break;

                            case 6:
                                iLevel = 83;
                                break;

                            case 7:
                                iLevel = 84;
                                break;

                            case 8:
                                iLevel = 81;
                                break;

                            case 9:
                                iLevel = 82;
                                break;
                        }
                    }
                    if (iLevel == level && (template.gender >= 2 || template.gender == gender)) {
                        return t;
                    }
                }
            }
        }
        return null;
    }
}
