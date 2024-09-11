
package com.nsoz.bot.move;

import com.nsoz.model.Char;
import lombok.Builder;

/**
 *
 * @author Admin
 */
public class MoveWithinCustom extends MoveToTarget {

    private int minX, maxX;
    private int minY, maxY;

    @Builder
    public MoveWithinCustom(Char p, int minX, int minY, int maxX, int maxY) {
        super(p);
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    @Override
    public void move(Char owner) {
        if (target == null) {
            return;
        }
        if (target.x < minX || target.x > maxX || target.y < minY || target.y > maxY) {
            return;
        }
        super.move(owner);
    }

}
