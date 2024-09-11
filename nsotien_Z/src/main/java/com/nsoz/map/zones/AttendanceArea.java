
package com.nsoz.map.zones;

import com.nsoz.map.Map;
import com.nsoz.map.TileMap;
import com.nsoz.model.Char;

/**
 *
 * @author Admin
 */
public class AttendanceArea extends ZWorld {

    public AttendanceArea(int id, TileMap tilemap, Map map) {
        super(id, tilemap, map);
    }

    public void join(Char p) {
        super.join(p);
        p.hp = p.maxHP;
        p.isDead = false;
        p.getService().loadInfo();
        p.setTypePk(Char.PK_NORMAL);
    }

    public void out(Char p) {
        super.out(p);
        p.hp = p.maxHP;
        p.isDead = false;
        p.getService().loadInfo();
        p.setTypePk(Char.PK_NORMAL);
    }

}
