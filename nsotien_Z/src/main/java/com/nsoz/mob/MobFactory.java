
package com.nsoz.mob;

import com.nsoz.constants.MobName;
import com.nsoz.map.zones.Zone;

/**
 *
 * Được share bởi Youtube : nsotien tv
 */
public class MobFactory {

    protected Zone zone;

    public MobFactory(Zone zone) {
        this.zone = zone;
    }

    public Mob createMonster(int id, MobPosition mob, int level) {
        MobTemplate template = MobManager.getInstance().find(mob.getId());
        int hp = template.hp;
        hp += hp * (level * 20) / 100;
        return new Mob(id, mob.getId(), hp, template.level, mob.getX(), mob.getY(), mob.isBeast() && zone.id % 5 == 0, template.isBoss(), zone);
    }

    public Mob createMonster(int id, MobPosition mob) {
        MobTemplate template = MobManager.getInstance().find(mob.getId());
        int hp = template.hp;
        short level = template.level;
        Mob monster = new Mob(id, mob.getId(), hp, level, mob.getX(), mob.getY(), mob.isBeast() && zone.id % 5 == 0, template.isBoss(), zone);
        if (template.isBoss() && zone.tilemap.isNormal() && template.id != MobName.HOP_BI_AN) {
            monster.die();
        }
        return monster;
    }

    public Mob createBoss(short id, int hp, short level, short x, short y) {
        return new Mob(zone.getMonsters().size(), id, hp, level, x, y, false, true, zone);
    }
}
