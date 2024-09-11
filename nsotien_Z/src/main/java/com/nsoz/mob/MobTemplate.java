
package com.nsoz.mob;

import com.nsoz.model.Frame;
import com.nsoz.model.ImageInfo;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author pika
 */
@Getter
@Setter
public class MobTemplate {

    public int id;
    public String name;
    public byte type;
    public int hp;
    public short level;
    public byte rangeMove;
    public byte speed;
    public ImageInfo[] imgInfo;
    public Frame[] frameBoss;
    public byte[] frameBossMove;
    public byte[][] frameBossAttack;
    public byte numberImage;
    public byte typeFly;
    public byte[][] frameChar = new byte[4][];
    public short[] sequence;
    public byte[] indexSplash = new byte[4];
    private boolean isBoss;
}
