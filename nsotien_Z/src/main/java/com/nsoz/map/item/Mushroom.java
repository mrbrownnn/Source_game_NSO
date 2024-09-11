
package com.nsoz.map.item;

import com.nsoz.constants.ItemName;
import com.nsoz.item.Item;
import com.nsoz.item.ItemFactory;

/**
 *
 * Được share bởi Youtube : nsotien tv
 */
public class Mushroom extends ItemMap {

    public Mushroom(short id) {
        super(id);
        Item item = ItemFactory.getInstance().newItem(ItemName.CAY_NAM);
        item.setQuantity(1);
        item.isLock = true;
        setItem(item);
        this.ownerID = -1;
        this.pickedUp = true;
    }

    @Override
    public boolean isExpired() {
        return false;
    }

}
