
package com.nsoz.map.item;

import com.nsoz.item.Item;
import com.nsoz.util.TimeUtils;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Administrator
 */
@Getter
@Setter
public class ItemMap {

    protected short id;
    protected Item item;
    protected int ownerID;
    protected short x;
    protected short y;
    protected boolean pickedUp;
    protected int requireItemID = -1;
    protected long createdAt;
    protected long expired = 30000;

    public Lock lock = new ReentrantLock();

    public ItemMap(short id) {
        this.id = id;
        this.createdAt = System.currentTimeMillis();
        return;
    }

    public boolean isExpired() {
        return TimeUtils.canDoWithTime(createdAt, expired);
    }

    public boolean isCanPickup() {
        return TimeUtils.canDoWithTime(createdAt, 20000);
    }
    
    public int getItemID() {
        return item.id;
    }

}
