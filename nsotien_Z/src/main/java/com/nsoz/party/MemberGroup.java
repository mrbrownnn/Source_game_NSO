
package com.nsoz.party;

import com.nsoz.map.world.World;
import com.nsoz.map.zones.NymozCave;
import com.nsoz.model.Char;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Admin
 */
public class MemberGroup {

    public int charId;
    public byte classId;
    public String name;
    @Getter
    @Setter
    private NymozCave nymozCave;
    private Char p;
    private final List<World> worlds = new ArrayList<>();

    public void setWorld(World world) {
        synchronized (worlds) {
            for (World w : worlds) {
                if (w.getType() == world.getType()) {
                    remove(w);
                    break;
                }
            }
            add(world);
        }
    }

    public void add(World world) {
        synchronized (worlds) {
            worlds.add(world);
        }
    }

    public void remove(World world) {
        synchronized (worlds) {
            worlds.remove(world);
        }
    }

    public World find(byte type) {
        synchronized (worlds) {
            for (World world : worlds) {
                if (world.getType() == type) {
                    return world;
                }
            }
        }
        return null;
    }

    public void setChar(Char p) {
        this.p = p;
    }

    public Char getChar() {
        return this.p;
    }
}
