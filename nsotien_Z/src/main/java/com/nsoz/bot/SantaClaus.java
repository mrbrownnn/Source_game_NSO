
package com.nsoz.bot;

import com.nsoz.ability.AbilityCustom;
import com.nsoz.fashion.FashionCustom;
import com.nsoz.map.Map;
import com.nsoz.map.item.ItemMap;
import com.nsoz.map.item.ItemMapFactory;
import com.nsoz.map.zones.Zone;
import com.nsoz.model.Char;
import com.nsoz.util.NinjaUtils;
import com.nsoz.util.TimeUtils;

/**
 *
 * Được share bởi Youtube : nsotien tv
 */
public class SantaClaus extends Bot {

    private long lastTime, lastTimeDropItem, lastTimeChat;

    public SantaClaus(int id) {
        super(id, "Santa Claus", 150, Char.PK_NORMAL, (byte) 0);
        this.lastTime = this.lastTimeDropItem = System.currentTimeMillis();
    }

    @Override
    public void setDefault() {
        super.setDefault();
        FashionCustom fashionCustom = FashionCustom.builder()
                .head((short) 267)
                .body((short) 268)
                .leg((short) 269)
                .weapon((short) -1)
                .build();
        setFashionStrategy(fashionCustom);
        AbilityCustom abilityCustom = AbilityCustom.builder()
                .hp(2000000)
                .build();
        setAbilityStrategy(abilityCustom);
        setAbility();
        setFashion();
    }

    @Override
    public void updateEveryHalfSecond() {
        super.updateEveryHalfSecond();
        long now = System.currentTimeMillis();;
        if (TimeUtils.canDoWithTime(lastTime, 60000)) {
            lastTime = now;
            zone.getService().chat(this.id, "Bye bye");
            Map map = zone.map;
            Zone z = map.rand();
            outZone();
            z.join(this);
            return;
        }
        if (TimeUtils.canDoWithTime(lastTimeDropItem, 10000)) {
            lastTimeDropItem = now;
            int q = (int)NinjaUtils.nextInt(1, 3);
            for (int i = 0; i < q; i++) {
                ItemMap item = ItemMapFactory.getInstance().builder()
                        .id(zone.numberDropItem++)
                        .type(ItemMapFactory.GIFT_BOX)
                        .x((short) (x + (i * 6 * (i % 2 == 0 ? 1 : -1))))
                        .y(y)
                        .build();
                zone.addItemMap(item);
                zone.getService().addItemMap(item);
            }
        }
        if (TimeUtils.canDoWithTime(lastTimeChat, 5000)) {
            lastTimeChat = now;
            zone.getService().chat(this.id, (String) NinjaUtils.randomObject("Giáng sinh vui vẻ!", "Hô hô hô", "Giáng sinh an lành"));
        }
    }

}
