
package com.nsoz.map.item;

import com.nsoz.constants.ItemName;
import com.nsoz.item.Item;
import com.nsoz.item.ItemFactory;

/**
 *
 * Được share bởi Youtube : nsotien tv
 */
public class MapItem extends ItemMap {

    public MapItem(short id) {
        super(id);
        this.ownerID = -1;
        Item item = ItemFactory.getInstance().newItem(ItemName.DIA_DO);
        item.isLock = true;
        item.setQuantity(1);
        item.yen = 10000;
        setItem(item);
    }

    @Override
    public boolean isExpired() {
        return false;
    }

}
