package com.nsoz.effect;

import java.sql.ResultSet;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.nsoz.db.jdbc.DbManager;
import com.nsoz.model.Frame;
import com.nsoz.model.ImageInfo;
import com.nsoz.lib.ParseData;
import java.sql.Connection;
import lombok.Getter;
import lombok.Setter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

@Getter
@Setter
public class EffectAutoData {

    private short id;
    private ImageInfo[] imgInfo;
    private Frame[] frameEffAuto;
    private short[] frameRunning;
    private byte[] data;

    public void setData() {
        try {
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bas);
            dos.writeByte(imgInfo.length);
            for (ImageInfo img : imgInfo) {
                dos.writeByte(img.id);
                dos.writeByte(img.x0);
                dos.writeByte(img.y0);
                dos.writeByte(img.w);
                dos.writeByte(img.h);
            }
            dos.writeShort(frameEffAuto.length);
            for (Frame frame : frameEffAuto) {
                dos.writeByte(frame.idImg.length);
                for (int j = 0; j < frame.idImg.length; j++) {
                    dos.writeShort(frame.dx[j]);
                    dos.writeShort(frame.dy[j]);
                    dos.writeByte(frame.idImg[j]);
                }
            }
            dos.writeShort(frameRunning.length);
            for (short index : frameRunning) {
                dos.writeShort(index);
            }
            dos.flush();
            data = bas.toByteArray();
            dos.close();
            bas.close();
        } catch (Exception ex) {
            Logger.getLogger(EffectAutoData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
