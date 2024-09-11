
package com.nsoz.bot.move;

import com.nsoz.model.Char;
import com.nsoz.util.NinjaUtils;
import com.nsoz.bot.IMove;

/**
 *
 * @author Admin
 */
public class JaianMove implements IMove {

    private short y;
    private long lastMove;
    private Char escort;

    public JaianMove(Char escort, short y) {
        this.escort = escort;
        this.y = y;
    }

    @Override
    public void move(Char owner) {
        if (owner.isDead) {
            escort.escortFailed();
            return;
        }
        if (owner.isDontMove()) {
            return;
        }
        int d = NinjaUtils.getDistance(owner.x, owner.y, escort.x, escort.y);
        if (d >= 500) {
            escort.escortFailed();
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastMove >= 3000) {
            lastMove = now;
            owner.x -= 36;
            owner.y = owner.zone.tilemap.collisionY(owner.x, y);
            owner.zone.getService().playerMove(owner);
            if (owner.x < 0) {
                owner.outZone();
                escort.taskNext();
            }
        }
    }

}
