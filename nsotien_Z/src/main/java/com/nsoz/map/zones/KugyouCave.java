package com.nsoz.map.zones;

import java.util.ArrayList;

import com.nsoz.map.item.ItemMap;
import com.nsoz.map.Map;
import com.nsoz.map.TileMap;
import com.nsoz.map.item.ItemMapFactory;

public class KugyouCave extends Zone {

    private ArrayList<ItemMap> iceCrystal;
    private long lastUpdate;

    public KugyouCave(int id, TileMap tilemap, Map map) {
        super(id, tilemap, map);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void init() {
        super.init();
        iceCrystal = new ArrayList<>();
        short[][] xys = {{420, 648}, {60, 336}, {490, 216}};
        for (short[] xy : xys) {
            ItemMap itemMap = ItemMapFactory.getInstance().builder()
                    .id(numberDropItem++)
                    .type(ItemMapFactory.ICE_CRYSTAL)
                    .x(xy[0])
                    .y(xy[1])
                    .build();
            iceCrystal.add(itemMap);
        }
    }

    @Override
    public void update() {
        super.update();
        long now = System.currentTimeMillis();
        if (now - lastUpdate > 30000) {
            lastUpdate = now;
            for (ItemMap itemMap : iceCrystal) {
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
