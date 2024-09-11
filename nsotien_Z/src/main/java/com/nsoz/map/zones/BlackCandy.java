
package com.nsoz.map.zones;

import com.nsoz.map.Map;
import com.nsoz.map.Waypoint;
import com.nsoz.map.world.World;
import com.nsoz.model.Char;
import org.jetbrains.annotations.NotNull;

/**
 *
 * Được share bởi Youtube : nsotien tv
 */
public class BlackCandy extends ZWorld {

    public BlackCandy(Map map, World world) {
        super(0, map.tilemap, map);
        setWorld(world);
    }

    @Override
    public void returnTownFromDead(@NotNull Char p) {
        p.setXY((short) 445, (short) 264);
        out(p);
        join(p);
    }

    @Override
    public void requestChangeMap(@NotNull Char p) {
        Waypoint wp = tilemap.findWaypoint(p.x, p.y);
        if (wp == null) {
            return;
        }
        Zone z = world.find(wp.next);
        p.outZone();
        p.setXY(wp.x, wp.y);
        z.join(p);
    }

}
