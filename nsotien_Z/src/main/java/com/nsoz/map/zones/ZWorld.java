
package com.nsoz.map.zones;

import com.nsoz.map.Map;
import com.nsoz.map.TileMap;
import com.nsoz.map.world.World;
import com.nsoz.model.Char;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Admin
 */
public class ZWorld extends Zone {

    @Getter
    @Setter
    protected World world;

    public ZWorld(int id, TileMap tilemap, Map map) {
        super(id, tilemap, map);
    }

    @Override
    public void join(Char p) {
        Zone preZone = p.zone;
        if (preZone != null) {
            if (!preZone.tilemap.isWorld()) {
                p.addMemberForWorld(preZone, this);
            }
        }
        super.join(p);
        p.getService().sendTimeInMap(world.getCountDown());
    }

}
