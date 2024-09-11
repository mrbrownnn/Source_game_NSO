
package com.nsoz.model;

import com.nsoz.item.Item;
import com.nsoz.skill.Skill;
import java.util.ArrayList;
import java.util.List;

public class Bijuu {

    private List<Skill> skills;
    private short[] potential;
    private Item[] equips;
    private int ppoint, spoint;

    public Bijuu() {
        this.skills = new ArrayList<>();
        this.potential = new short[5];
    }

}
