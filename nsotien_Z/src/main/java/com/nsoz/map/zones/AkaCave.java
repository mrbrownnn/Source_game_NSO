
package com.nsoz.map.zones;

import com.nsoz.map.item.ItemMap;
import com.nsoz.map.Map;
import com.nsoz.map.TileMap;
import com.nsoz.map.item.ItemMapFactory;
import java.util.ArrayList;

/**
 *
 * @author Admin
 */
public class AkaCave extends Zone {

    private ArrayList<ItemMap> ores;
    private long lastUpdate;

    public AkaCave(int id, TileMap tilemap, Map map) {
        super(id, tilemap, map);
    }



    @Override
    public void init() {
        super.init();
        ores = new ArrayList<>();
        short[][] xys = {{530, 624}, {900, 384}, {1093, 192}};
        for (short[] xy : xys) {
            ItemMap itemMap = ItemMapFactory.getInstance().builder()
                    .id(numberDropItem++)
                    .type(ItemMapFactory.ORE)
                    .x(xy[0])
                    .y(xy[1])
                    .build();
            ores.add(itemMap);
        }
    }

    @Override
    public void update() {
        super.update();
        long now = System.currentTimeMillis();
        if (now - lastUpdate > 30000) {
            lastUpdate = now;
            for (ItemMap itemMap : ores) {
                if (itemMap.isPickedUp()) {
                    itemMap.setId(numberDropItem++);
                    itemMap.setPickedUp(false);
                    getService().addItemMap(itemMap);
                    addItemMap(itemMap);
                }
            }
        }
    }

}
