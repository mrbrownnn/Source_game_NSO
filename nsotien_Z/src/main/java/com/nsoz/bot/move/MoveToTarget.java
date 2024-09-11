
package com.nsoz.bot.move;

import com.nsoz.map.TileMap;
import com.nsoz.map.zones.Zone;
import com.nsoz.model.Char;
import com.nsoz.util.NinjaUtils;
import lombok.Setter;
import com.nsoz.bot.IMove;

/**
 *
 * @author Admin
 */
public class MoveToTarget implements IMove {

    @Setter
    protected Char target;

    public MoveToTarget(Char target) {
        this.target = target;
    }

    @Override
    public void move(Char owner) {
        if (target == null) {
            return;
        }
        if (owner.isDead || target.isDead) {
            return;
        }
        if (owner.isDontMove()) {
            return;
        }
        int d = NinjaUtils.getDistance(owner.x, owner.y, target.x, target.y);
        if (d < 50) {
            return;
        }
        Zone zone = owner.zone;
        owner.preX = owner.x;
        owner.preY = owner.y;
        int dir = target.x > owner.x ? 1 : -1;
        int x =(int) NinjaUtils.nextInt(50, 90) * dir;
        owner.x += x;
        if (owner.x < 24) {
            owner.x = 24;
        }
        if (owner.x > owner.zone.tilemap.pxw - 24) {
            owner.x = (short) (owner.zone.tilemap.pxw - 24);
        }
        if (owner.isCrossMap(owner.x, owner.y)) {
            owner.x -= x * 2;
        }
        short tempX = (short) (owner.x + 11);
        short tempY = (short) (owner.y - 16);
        if (zone.tilemap.isInWaypoint(tempX, tempY)) {
            owner.x = owner.preX;
            owner.y = owner.preY;
        }
        owner.y -= 24;
        owner.y = owner.zone.tilemap.collisionY(owner.x, owner.y);
        if (Math.abs(owner.x - target.x) < 100) {
            if (target.y < owner.y) {
                owner.y -= 72;
            } else if (target.y > owner.y) {
                owner.y = target.y;
            }
        }
        if (!owner.zone.tilemap.tileTypeAt(tempX, tempY, TileMap.T_TOP)) {
            owner.y = owner.zone.tilemap.collisionY(owner.x, owner.y);
        }
        if (owner.isCrossMap(owner.x, owner.y)) {
            owner.x = owner.preX;
            owner.y = owner.preY;
        }
        if (owner.clone != null && owner.clone.isNhanBan && !owner.clone.isDead) {
            owner.clone.move((short) (NinjaUtils.nextInt(-50, 50) + owner.x), owner.y);
        }
        owner.zone.getService().playerMove(owner);
    }

}
