package com.nsoz.map.zones;

import java.util.ArrayList;

import com.nsoz.map.item.ItemMap;
import com.nsoz.map.Map;
import com.nsoz.map.TileMap;
import com.nsoz.map.item.ItemMapFactory;

public class AikoRedRockArea extends Zone {

    private ArrayList<ItemMap> mushrooms;
    private long lastUpdate;

    public AikoRedRockArea(int id, TileMap tilemap, Map map) {
        super(id, tilemap, map);
    }

    @Override
    public void init() {
        super.init();
        mushrooms = new ArrayList<>();
        short[][] xys = {{469, 528}, {737, 600}, {710, 480}};
        for (short[] xy : xys) {
            ItemMap itemMap = ItemMapFactory.getInstance().builder()
                    .id(numberDropItem++)
                    .type(ItemMapFactory.MUSHROOM)
                    .x(xy[0])
                    .y(xy[1])
                    .build();
            mushrooms.add(itemMap);
        }
    }

    @Override
    public void update() {
        super.update();
        long now = System.currentTimeMillis();
        if (now - lastUpdate > 30000) {
            lastUpdate = now;
            for (ItemMap itemMap : mushrooms) {
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
