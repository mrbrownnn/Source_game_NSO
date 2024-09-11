
package com.nsoz.effect;

import com.nsoz.model.PartFrame;
import com.nsoz.model.SmallImage;
import com.nsoz.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author PC
 */
@NoArgsConstructor
@Setter
@Getter
@Builder
@AllArgsConstructor
public class EffectData {

    public int id;
    @Builder.Default
    public byte[][] frameChar = new byte[4][];
    @Builder.Default
    public byte[] indexSplash = new byte[4];
    public byte[] sequence;
    public SmallImage[] smallImage;
    public PartFrame[][] frames;
    private byte[] data;

    public void setData() {
        try {
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bas);
            dos.writeByte(smallImage.length);
            for (SmallImage small : smallImage) {
                dos.writeByte(small.id);
                dos.writeByte(small.x);
                dos.writeByte(small.y);
                dos.writeByte(small.w);
                dos.writeByte(small.h);
            }
            dos.writeShort(frames.length);
            for (PartFrame[] partFrames : frames) {
                dos.writeByte(partFrames.length);
                for (PartFrame part : partFrames) {
                    dos.writeShort(part.dx);
                    dos.writeShort(part.dy);
                    dos.writeByte(part.idSmallImg);
                    dos.writeByte(part.flip);
                    dos.writeByte(part.onTop);
                }
            }
            dos.writeByte(sequence.length);
            for (byte sq : sequence) {
                dos.writeShort(sq);
            }
            dos.writeByte(frameChar.length);
            dos.writeByte(frameChar[0].length);
            for (byte frame : frameChar[0]) {
                dos.writeByte(frame);
            }
            dos.writeByte(frameChar[1].length);
            for (byte frame : frameChar[1]) {
                dos.writeByte(frame);
            }
            dos.writeByte(frameChar[3].length);
            for (byte frame : frameChar[3]) {
                dos.writeByte(frame);
            }
            dos.writeByte(indexSplash[0]);
            dos.writeByte(indexSplash[1]);
            dos.writeByte(indexSplash[2]);
            dos.flush();
            data = bas.toByteArray();
            dos.close();
            bas.close();
        } catch (IOException ex) {
            Log.error("set data err", ex);
        }
    }

}
