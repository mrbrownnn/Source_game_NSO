
package com.nsoz.map.zones;

import com.nsoz.map.Map;
import com.nsoz.map.TileMap;
import com.nsoz.mob.Mob;
import com.nsoz.model.Char;
import com.nsoz.util.NinjaUtils;
import java.util.List;

/**
 *
 * @author Admin
 */
public class InoshishiCave extends AreaWithCountdownTime {

    private boolean isWin;

    public InoshishiCave(int id, TileMap tilemap, Map map) {
        super(id, tilemap, map);
        countDown = 180;
    }

    @Override
    public void update() {
        super.update();
        if (!isWin) {
            List<Mob> mobs = getLivingMonsters();
            if (mobs.isEmpty()) {
                isWin = true;
                setTimeMap(20);
                getService().serverMessage(String.format("Bạn có %s giây để nhặt lấy Cây Rìu Bạc", this.countDown));
            }
        }
    }

    @Override
    public void close() {
        if (!isClosed()) {
            List<Char> chars = getChars();
            for (Char c : chars) {
                if (c.isNhanBan) {
                    continue;
                }
                short[] xy = NinjaUtils.getXY(c.saveCoordinate);
                c.setXY(xy);
                c.changeMap(c.saveCoordinate);
            }
        }
        super.close();
    }

}
