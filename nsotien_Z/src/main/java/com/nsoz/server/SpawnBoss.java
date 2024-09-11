
package com.nsoz.server;

import com.nsoz.lib.RandomCollection;
import com.nsoz.map.Map;
import com.nsoz.map.zones.Zone;
import com.nsoz.mob.Mob;
import com.nsoz.mob.MobManager;
import com.nsoz.mob.MobTemplate;
import com.nsoz.util.Log;
import com.nsoz.util.NinjaUtils;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SpawnBoss {

    private int id;
    private Map map;
    private final RandomCollection<Integer> mobs = new RandomCollection<>();
    private Mob currMonster;
    private short x, y;

    public SpawnBoss(int id, Map map, short x, short y) {
        this.id = id;
        this.map = map;
        this.x = x;
        this.y = y;
    }

    public void add(int rate, int mobID) {
        mobs.add(rate, mobID);
    }

    public void spawn() {
        if (currMonster != null) {
            currMonster.die();
            currMonster = null;
        }
        int zoneId = NinjaUtils.nextInt(map.getZones().size());
        Zone z = map.getZoneById(zoneId);
        int mobID = mobs.next();
        MobTemplate mobTemplate = MobManager.getInstance().find(mobID);
        Mob mob = z.getMobFactory().createBoss((short) mobTemplate.getId(), mobTemplate.getHp(), mobTemplate.getLevel(), x, y);
        z.addMob(mob);
        currMonster = mob;
        String text = mob.template.name + " đã xuất hiện ở " + z.tilemap.name + "khu "+ z.id;
        GlobalService.getInstance().chat("Hệ thống", text);
        Log.debug(text + " khu " + z.id);
        Log.info(text+ " khu "+ z.id);
    }
}
