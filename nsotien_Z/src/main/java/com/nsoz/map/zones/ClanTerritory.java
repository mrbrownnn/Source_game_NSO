
package com.nsoz.map.zones;

import com.nsoz.map.Map;
import com.nsoz.constants.MapName;
import com.nsoz.constants.MobName;
import com.nsoz.map.TileMap;
import com.nsoz.map.Waypoint;
import com.nsoz.map.world.Territory;
import com.nsoz.map.world.World;
import com.nsoz.mob.Mob;
import com.nsoz.mob.TerritoryMobFactory;
import com.nsoz.model.Char;
import com.nsoz.util.NinjaUtils;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Admin
 */
public class ClanTerritory extends ZWorld {

    public ClanTerritory(int id, TileMap tilemap, Map map) {
        super(id, tilemap, map);
    }

    @Override
    public void requestChangeMap(@NotNull Char p) {
        Waypoint wp = tilemap.findWaypoint(p.x, p.y);
        if (wp == null) {
            return;
        }
        int nextID = wp.next;
        Zone z = world.find(nextID);
        if (z != null) {
            if (z.isOpened) {
                p.setXY(wp.x, wp.y);
                p.outZone();
                z.join(p);
                return;
            }
        }
        p.returnToPreviousPostion(() -> {
            p.serverDialog("Cửa này vẫn chưa được mở.");
        });
    }

    @Override
    public void returnTownFromDead(@NotNull Char p) {
        p.outZone();
        if (map.id == MapName.NGHIA_DIA_QUAN) {
            int posX = (int)NinjaUtils.nextInt(230, 425);
            p.setXY((short) posX, (short) 120);
            Zone z = world.find(MapName.NGHIA_DIA_QUAN);
            z.join(p);
        } else {
            p.setXY((short) 191, (short) 144);
            Zone z = world.find(MapName.CUA_CHO);
            z.join(p);
        }
    }

    @Override
    public void mobDead(Mob mob, Char killer) {
        if (killer != null) {
            Territory territory = (Territory) world;
            TerritoryMobFactory mobFactory = (TerritoryMobFactory) getMobFactory();
            if (mob.template.id == MobName.BAO_QUAN) {
                territory.waitForNextTurn();
            } else if (mob.levelBoss == 0 && tilemap.id != MapName.NGHIA_DIA_QUAN) {
                mobFactory.bornLastBoss();
            } else if (mob.levelBoss == 1 && isLastBossWasBorn) {
                mobFactory.createMonsterLamThao();
            }
        }
    }

}
