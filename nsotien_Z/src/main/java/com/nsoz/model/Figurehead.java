
package com.nsoz.model;

/**
 *
 * @author Administrator
 */
public class Figurehead {

    public Figurehead(String name, short x, short y, int countDown) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.countDown = countDown;
    }

    public String name;
    public short x;
    public short y;
    public int countDown;
}
