
package com.nsoz.map.item;

import com.nsoz.util.TimeUtils;

/**
 *
 * Được share bởi Youtube : nsotien tv
 */
public class MagicItem extends ItemMap {
    
    public MagicItem(short id) {
        super(id);
    }
    
    @Override
    public boolean isExpired() {
        return TimeUtils.canDoWithTime(createdAt, 900000);
    }
    
}
