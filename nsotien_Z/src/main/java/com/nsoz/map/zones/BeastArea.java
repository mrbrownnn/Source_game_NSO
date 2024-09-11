
package com.nsoz.map.zones;

import com.nsoz.map.Map;
import com.nsoz.constants.MapName;
import com.nsoz.map.TileMap;
import com.nsoz.mob.Mob;
import com.nsoz.constants.MobName;
import com.nsoz.mob.MobPosition;
import com.nsoz.mob.MobManager;
import com.nsoz.mob.MobTemplate;
import com.nsoz.model.Char;
import java.util.List;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Admin
 */
public class BeastArea extends Z7Beasts {

    @Getter
    private int previousPlayerDied;

    public BeastArea(int id, TileMap tilemap, Map map) {
        super(id, tilemap, map);
        previousPlayerDied = -1;
    }

    @Override
    public void join(@NotNull Char p) {
        super.join(p);
        previousPlayerDied = p.id;
    }

    @Override
    public void refresh() {
        this.level++;
        this.previousPlayerDied = -1;
        if (this.level < 6) {
            createMonster();
        } else {
            Mob mob = new Mob(monsters.size(), (short) MobName.MUC_ONG_DO, 20000000, (short) 68, (short) 565, (short) 384, false, false, this);
            addMob(mob);
        }
    }

    @Override
    public void update() {
        super.update();
        if (level < 6) {
            if (getLivingMonsters().isEmpty()) {
                List<Char> chars = getChars();
                Zone z = world.find(MapName.KHU_VUC_CHO);
                for (Char c : chars) {
                    if (c.isNhanBan) {
                        continue;
                    }
                    c.outZone();
                    c.setXY((short) 35, (short) 360);
                    z.join(c);
                }
                refresh();
            }
        }
    }

    @Override
    public void createMonster() {
        monsters.clear();
        int id = 0;
        for (MobPosition mob : tilemap.monsterCoordinates) {
            short templateID = (short) (mob.getId() + level);
            MobTemplate template = MobManager.getInstance().find(templateID);
            short x = mob.getX();
            short y = mob.getY();
            if (template.type == 4) {
                y -= 24;
            }
            Mob monster = new Mob(id++, templateID, 500000, (short) 68, x, y, false, false, this);
            addMob(monster);
        }
    }

}
