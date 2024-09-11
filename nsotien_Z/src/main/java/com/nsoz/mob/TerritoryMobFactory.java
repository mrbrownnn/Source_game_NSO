
package com.nsoz.mob;

import com.nsoz.constants.MobName;
import com.nsoz.map.TileMap;
import com.nsoz.map.zones.Zone;
import com.nsoz.util.NinjaUtils;

/**
 *
 * Được share bởi Youtube : nsotien tv
 */
public class TerritoryMobFactory extends MobFactory {

    public TerritoryMobFactory(Zone zone) {
        super(zone);
    }

    @Override
    public Mob createMonster(int id, MobPosition mob) {
        MobTemplate template = MobManager.getInstance().find(mob.getId());
        int hp = template.hp;
        short level = template.level;
        hp = 2000000;
        level = 100;
        if (template.id == MobName.BAO_QUAN || template.id == MobName.TU_HA_MA_THAN) {
            hp = 2000000000;
            level = 100;
        }
        if (mob.getId() == MobName.LAM_THAO || mob.getId() == MobName.MY_HAU_TUONG) {
            return null;
        }
        Mob monster = new Mob(id, mob.getId(), hp, level, mob.getX(), mob.getY(), mob.isBeast() && zone.id % 5 == 0, template.isBoss(), zone);
        if (template.isBoss()) {
            monster.die();
        }
        return monster;
    }

    public void bornLastBoss() {
        if (zone.isLastBossWasBorn) {
            return;
        }
        if (zone.getLivingMonsters().isEmpty()) {
            zone.isLastBossWasBorn = true;
            int rand = (int) NinjaUtils.nextInt(0, zone.tilemap.monsterCoordinates.size() - 2);
            MobPosition mob = zone.tilemap.monsterCoordinates.get(rand);
            MobTemplate template = MobManager.getInstance().find(mob.getId());
            int id = zone.getMonsters().size();
            Mob monster = new Mob(id++, mob.getId(), 2000000, (short) 100, mob.getX(), mob.getY(), false, template.isBoss(), zone);
            monster.levelBoss = 1;
            monster.setHP();
            zone.addMob(monster);
        }
    }

    public void createMonsterLamThao() {
        TileMap tilemap = zone.tilemap;
        int incrementId = zone.getMonsters().size();
        for (MobPosition mob : tilemap.monsterCoordinates) {
            MobTemplate template = MobManager.getInstance().find(mob.getId());
            int hp = template.hp;
            short level = template.level;
            if (tilemap.isDungeoClan() && mob.getId() == MobName.LAM_THAO) {
                hp = 2000000;
                level = 100;
                Mob monster = new Mob(incrementId++, mob.getId(), hp, level, mob.getX(), mob.getY(), false, template.isBoss(), zone);
                zone.addMob(monster);
            }
        }
    }

}
