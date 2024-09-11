
package com.nsoz.map.zones;

import com.nsoz.map.Map;
import com.nsoz.constants.MapName;
import com.nsoz.map.TileMap;
import com.nsoz.map.Waypoint;
import com.nsoz.map.world.Dungeon;
import com.nsoz.map.world.World;
import com.nsoz.mob.Mob;
import com.nsoz.model.Char;
import com.nsoz.util.NinjaUtils;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Admin
 */
public class Cave extends ZWorld {

    public Cave(int id, TileMap tilemap, Map map) {
        super(id, tilemap, map);
    }

    @Override
    public void requestChangeMap(@NotNull Char p) {
        Waypoint wp = tilemap.findWaypoint(p.x, p.y);
        if (wp == null) {
            return;
        }
        int nextID = wp.next;
        Zone z = this.world.find(nextID);
        if (z != null) {
            if (nextID == MapName.LONG_XA_DONG || nextID == MapName.HOANG_XA_DONG || nextID == MapName.XICH_TRUNG_DONG) {
                if (z.getNumberChar() >= 2) {
                    p.returnToPreviousPostion(() -> {
                        p.serverDialog("Cửa này chỉ chứa được tối đa 2 người.");
                    });
                    return;
                }
            }
            p.setXY(wp.x, wp.y);
            p.outZone();
            z.join(p);
        } else {
            p.returnToPreviousPostion(() -> {
                p.serverDialog("Cửa này vẫn chưa được mở.");
            });
        }
    }

    @Override
    public void returnTownFromDead(@NotNull Char p) {
        int[] info = Dungeon.INFO[((Dungeon) this.world).level];
        p.setXY((short) info[1], (short) info[2]);
        p.outZone();
        Zone z = world.find(info[0]);
        z.join(p);
    }

    @Override
    public void mobDead(Mob mob, Char killer) {
        if (killer != null) {
            Dungeon dun = (Dungeon) world;
            if (dun != null) {
                switch (mob.levelBoss) {
                    case 2:
                        dun.addPointPB((int)NinjaUtils.nextInt(5, 20));
                        break;
                    case 1:
                        dun.addPointPB(6);
                        break;
                    default:
                        dun.addPointPB(1);
                        break;
                }
            }
        }
    }
}
