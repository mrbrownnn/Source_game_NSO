
package com.nsoz.bot;

import com.nsoz.ability.AbilityCustom;
import com.nsoz.fashion.FashionCustom;
import com.nsoz.map.MapManager;
import com.nsoz.model.Char;
import lombok.Builder;

/**
 *
 * @author Admin
 */
public class BotFactory {

    private static final BotFactory instance = new BotFactory();

    public static BotFactory getInstance() {
        return instance;
    }

    
    
    @Builder
    public Bot newBot(int id, String name, int level, byte typePK, byte clazz, short head, short body, short leg, short wp, int hp, int mp, int damage, int miss, int exactly, int fatal) {
        Bot bot = Bot.builder().id(id)
                .name(name)
                .level(level)
                .typePk(typePK)
                .classId(clazz)
                .build();
        bot.setDefault();
        FashionCustom fashionCustom = FashionCustom.builder()
                .head(head)
                .body(body)
                .leg(leg)
                .weapon(wp)
                .build();
        bot.setFashionStrategy(fashionCustom);
        AbilityCustom abilityCustom = AbilityCustom.builder()
                .hp(hp)
                .mp(mp)
                .damage(damage)
                .damage2(damage - (damage / 10))
                .miss(miss)
                .exactly(exactly)
                .fatal(fatal)
                .build();
        bot.setAbilityStrategy(abilityCustom);
        return bot;
    }
}
