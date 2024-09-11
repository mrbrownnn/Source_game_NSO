/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nsoz.lib;

import com.nsoz.map.Map;
import com.nsoz.map.MapManager;
import com.nsoz.util.Log;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import lombok.Builder;
import lombok.Getter;

/**
 *
 * @author Kitak
 */
@Getter
public class ImageMap {

    private int mapID;
    private int x;
    private int y;
    private int w;
    private int h;
    private byte zoomLevel;
    private byte[] data;

    @Builder
    public ImageMap(int mapID, int x, int y, int w, int h, byte zoomLevel) {
        this.mapID = mapID;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.zoomLevel = zoomLevel;
        create();
    }

    private void create() {
        try {
            Map map = MapManager.getInstance().find(mapID);
            if (map == null) {
                Log.error("Khong tim thay map");
            }
            BufferedImage img = map.tilemap.createImage(zoomLevel, x, y, w, h);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            data = baos.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(ImageMap.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
