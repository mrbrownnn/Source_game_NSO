
package com.nsoz.store;

import com.nsoz.item.ItemManager;
import com.nsoz.item.ItemTemplate;
import com.nsoz.option.ItemOption;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Administrator
 */
@Getter
@Setter
public class ItemStore {

    private int id;
    private int itemID;
    public byte sys;
    private int coin;
    private int yen;
    private int gold;
    private boolean isLock;
    private long expire;
    private List<ItemOption> maxOptions, minOptions;
    private ItemTemplate template;

    @Builder
    public ItemStore(int id, int itemID, byte sys, int coin, int yen, int gold, boolean isLock, long expire,
            List<ItemOption> options) {
        this.id = id;
        this.itemID = itemID;
        this.sys = sys;
        this.coin = coin;
        this.yen = yen;
        this.gold = gold;
        this.isLock = isLock;
        this.expire = expire;
        this.maxOptions = options;
        if (maxOptions != null) {
            setMinOptions();
        }
        this.template = ItemManager.getInstance().getItemTemplate(itemID);
    }

    public void setMinOptions() {
        this.minOptions = new ArrayList<>();
        for (ItemOption o : maxOptions) {
            int optionID = o.optionTemplate.id;
            int optionParam = o.param;
            int newParam;
            if (optionID == 0 || optionID == 1 || optionID == 21 || optionID == 22 || optionID == 23
                    || optionID == 24 || optionID == 25 || optionID == 26) {
                newParam = optionParam - 49;
            } else if (optionID == 6 || optionID == 7 || optionID == 8 || optionID == 9
                    || optionID == 19) {
                newParam = optionParam - 9;
            } else if (optionID == 2 || optionID == 3 || optionID == 4 || optionID == 5
                    || optionID == 10 || optionID == 11 || optionID == 12 || optionID == 13
                    || optionID == 14 || optionID == 15 || optionID == 17 || optionID == 18) {
                newParam = optionParam - 4;
            } else if (optionID == 16) {
                newParam = optionParam - 2;
            } else {
                newParam = optionParam;
            }
            minOptions.add(new ItemOption(optionID, newParam));
        }
    }

}
