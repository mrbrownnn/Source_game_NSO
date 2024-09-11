
package com.nsoz.map.item;

import com.nsoz.constants.ItemName;
import com.nsoz.item.Item;
import com.nsoz.item.ItemFactory;

/**
 *
 * Được share bởi Youtube : nsotien tv
 */
public class Ore extends ItemMap {

    public Ore(short id) {
        super(id);
        Item item = ItemFactory.getInstance().newItem(ItemName.KHOANG_THACH);
        item.setQuantity(1);
        item.isLock = true;
        setItem(item);
        this.pickedUp = true;
        this.ownerID = -1;
    }

    @Override
    public boolean isExpired() {
        return false;
    }

}
