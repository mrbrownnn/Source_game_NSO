package com.nsoz.bot;

import com.nsoz.item.Equip;
import com.nsoz.item.Item;
import com.nsoz.item.Mount;
import com.nsoz.model.Char;
import com.nsoz.network.NoService;
import com.nsoz.network.Service;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author PC
 */
public class Bot extends Char {

    @Setter
    private IAttack attack;

    @Setter
    @Getter
    private IMove move;

    public Bot(int id) {
        super(id);
    }

    @Builder
    public Bot(int id, String name, int level, byte typePk, byte classId) {
        super(id);
        this.name = name;
        this.level = level;
        this.typePk = typePk;
        this.classId = classId;
    }

    public void setDefault() {
        this.bag = new Item[0];
        this.box = new Item[0];
        this.equipment = new Equip[16];
        this.fashion = new Equip[16];
        this.mount = new Mount[5];
        this.bijuu = new Item[5];
    }

    public void recovery() {
        this.hp = this.maxHP;
        this.mp = this.maxMP;
        this.isDead = false;
    }

    public void setUp() {
        loadDisplay();
        load();
        setAbility();
        this.hp = this.maxHP;
        this.mp = this.maxMP;
        setFashion();
    }

    public Service getService() {
        return NoService.getInstance();
    }

    @Override
    public void addMp(double add) {

    }

    @Override
    public void updateEveryHalfSecond() {
        try {
            super.updateEveryHalfSecond(); //To change body of generated methods, choose Tools | Templates.
        } finally {
            if (attack != null) {
                attack.attack(this);
            }
            if (move != null) {
                move.move(this);
            }
        }
    }

}
