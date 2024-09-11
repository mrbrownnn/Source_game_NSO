
package com.nsoz.model;

import com.nsoz.item.Item;
import java.util.Vector;

/**
 *
 * @author Admin
 */
public class Trader {

    public boolean isLock;
    public Char player;
    public int coinTradeOrder;
    public Vector<Item> itemTradeOrder;
    public boolean accept;

    public Trader(Char p) {
        this.player = p;
    }

    public Char getChar() {
        return this.player;
    }
}
