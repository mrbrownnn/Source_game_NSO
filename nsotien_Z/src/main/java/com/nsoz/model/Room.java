
package com.nsoz.model;

import com.nsoz.bot.Bot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Admin
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Room {

    private boolean havePlayer;
    private Bot bot;
    private Char player;

    private int x;
    private int y;
    private int w;
    private int h;
    private short tx;
    private short ty;
    private int minX, maxX;
    private int minY, maxY;
}
