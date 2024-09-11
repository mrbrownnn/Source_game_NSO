
package com.nsoz.map.zones;

import com.nsoz.map.Map;
import com.nsoz.map.TileMap;
import com.nsoz.model.Char;

/**
 *
 * @author Admin
 */
public class AreaWithCountdownTime extends Zone {

    protected int countDown;

    public AreaWithCountdownTime(int id, TileMap tilemap, Map map) {
        super(id, tilemap, map);
    }

    @Override
    public void join(Char p) {
        super.join(p);
        p.getService().sendTimeInMap(countDown);
    }

    public void setTimeMap(int t) {
        countDown = t;
        getService().sendTimeInMap(countDown);
    }

    @Override
    public void update() {
        super.update();
        if (countDown > 0) {
            countDown--;
            if (countDown == 0) {
                close();
            }
        }
    }

}
