
package com.nsoz.map.zones;

import com.nsoz.map.Map;
import com.nsoz.map.TileMap;
import com.nsoz.mob.Mob;
import com.nsoz.constants.MobName;

/**
 *
 * @author Admin
 */
public class WaitingArea extends Z7Beasts {

    private Mob woodenDummy;

    public WaitingArea(int id, TileMap tilemap, Map map) {
        super(id, tilemap, map);
    }

    public void initMob() {
        woodenDummy = new Mob(0, (short) MobName.MOC_NHAN, 15000000, (short) 62, (short) 221, (short) 312, false, false, this);
        addMob(woodenDummy);
    }

    @Override
    public void refresh() {
        if (this.level < 5) {
            this.level++;
            if (woodenDummy == null) {
                initMob();
            } else {
                woodenDummy.recovery();
                getService().recoveryMonster(woodenDummy);
            }
        }
    }
}
