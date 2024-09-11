
package com.nsoz.map.zones;

import com.nsoz.constants.ItemName;
import com.nsoz.map.Map;
import com.nsoz.constants.MapName;
import com.nsoz.constants.MobName;
import com.nsoz.item.Item;
import com.nsoz.item.ItemFactory;
import com.nsoz.map.TileMap;
import com.nsoz.map.Waypoint;
import com.nsoz.map.world.SevenBeasts;
import com.nsoz.map.world.World;
import com.nsoz.mob.Mob;
import com.nsoz.model.Char;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Admin
 */
public abstract class Z7Beasts extends ZWorld {

    @Getter
    protected int level;

    public Z7Beasts(int id, TileMap tilemap, Map map) {
        super(id, tilemap, map);
    }

    @Override
    public void returnTownFromDead(@NotNull Char p) {
        p.outZone();
        Zone z = world.find(MapName.KHU_VUC_CHO);
        p.setXY((short) 35, (short) 360);
        z.join(p);
        if (map.id == MapName.THAT_THU_AI) {
            world.getService().serverMessage(String.format("%s đã rời ải, xin mời thành viên tiếp theo", p.name));
        }
    }

    @Override
    public void requestChangeMap(@NotNull Char p) {
        if (world.getCountDown() > 3600) {
            p.returnToPreviousPostion(() -> {
                p.serverDialog("Cửa ải chưa được mở.");
            });
            return;
        }
        Waypoint wp = tilemap.findWaypoint(p.x, p.y);
        if (wp == null) {
            return;
        }
        int nextID = wp.next;
        Z7Beasts z = (Z7Beasts) world.find(nextID);
        if (nextID == MapName.KHU_VUC_CHO) {
            p.returnToPreviousPostion(() -> {
                p.serverDialog("Lối ra đã bị chặn. Bạn chỉ còn cách tiêu diệt hết số quái trong ải");
            });
            return;
        } else if (nextID == MapName.THAT_THU_AI) {
            BeastArea area = (BeastArea) z;
            if ((area.getNumberChar() > 0 || area.getPreviousPlayerDied() == p.id) && area.getLevel() < 6) {
                p.returnToPreviousPostion(() -> {
                    p.serverDialog("Đã có người vào ải, hoặc chưa tới lượt đánh của bạn. Vui lòng chờ ở bên ngoài.");
                });
                return;
            }
            refresh();
        }
        p.outZone();
        p.setXY(wp.x, wp.y);
        z.join(p);
    }

    @Override
    public void mobDead(Mob mob, Char killer) {
        if (killer != null) {
            if ((mob.template.id == MobName.MOC_NHAN || mob.template.id == MobName.MUC_ONG_DO)) {
                Item item = ItemFactory.getInstance().newItem(ItemName.THAT_THU_THU_BAO);
                item.setQuantity(1);
                item.isLock = true;
                if (killer.getSlotNull() > 0) {
                    killer.addItemToBag(item);
                } else {
                    killer.warningBagFull();
                }
                SevenBeasts sevenBeasts = (SevenBeasts) killer.findWorld(World.SEVEN_BEASTS);
                sevenBeasts.getService().serverMessage(String.format("%s nhận được %s rơi ra từ %s", killer.name, item.template.name, mob.template.name));
                if (mob.template.id == MobName.MUC_ONG_DO) {
                    sevenBeasts.setCountdown(15);
                    sevenBeasts.getService().serverMessage("Xin chúc mừng nhóm của bạn đã vượt qua được thất thú ải.");
                }
            }
        }
    }

    public abstract void refresh();
}
