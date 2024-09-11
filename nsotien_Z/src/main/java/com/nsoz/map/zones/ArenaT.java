
package com.nsoz.map.zones;

import com.nsoz.bot.Bot;
import com.nsoz.map.Map;
import com.nsoz.map.TileMap;
import com.nsoz.model.Char;
import com.nsoz.thiendia.Ranking;
import com.nsoz.thiendia.ThienDiaData;
import com.nsoz.util.NinjaUtils;
import lombok.Setter;

/**
 *
 * @author Admin
 */
public class ArenaT extends Zone {

    @Setter
    private Bot bot;
    @Setter
    private Char player;
    @Setter
    private Ranking ranking1, ranking2;
    @Setter
    private ThienDiaData thienDiaData;

    public ArenaT(int id, TileMap tilemap, Map map) {
        super(id, tilemap, map);
    }

    public void setWin(boolean win) {
        if (ranking1 != null && ranking2 != null && thienDiaData != null) {
            ranking1.setFighting(false);
            ranking2.setFighting(false);
            if (win) {
                if (ranking1.getRanked() < ranking2.getRanked()) {
                    int temp = ranking1.getRanked();
                    ranking1.setRanked(ranking2.getRanked());
                    ranking2.setRanked(temp);
                }
                thienDiaData.sort();
            }
        }
        if (bot != null) {
            bot.setArenaT(null);
            bot.outZone();
            bot = null;
        }
        if (player != null) {
            player.setArenaT(null);
            short[] xy = NinjaUtils.getXY(player.mapBeforeEnterPB);
            player.setXY(xy[0], xy[1]);
            player.changeMap(player.mapBeforeEnterPB);
            if (win) {
                player.getService().serverMessage(String.format("Bạn đã thắng và được thăng lên hạng %d", ranking2.getRanked()));
            } else {
                player.countArenaT = 0;
                player.getService().serverMessage("Bạn đã thua");
            }
            player = null;
        }

        close();
    }

}
