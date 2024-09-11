
package com.nsoz.map;

import com.nsoz.constants.MapName;
import com.nsoz.mob.MobPosition;
import com.nsoz.npc.Npc;
import com.nsoz.server.GameData;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Vector;

import java.awt.image.BufferedImage;
import java.io.File;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author pika
 */
public class TileMap {

    public static final int T_EMPTY = 0;
    public static final int T_TOP = 2;
    public static final int T_LEFT = 2 << 1;
    public static final int T_RIGHT = 2 << 2;
    public static final int T_TREE = 2 << 3;
    public static final int T_WATERFALL = 2 << 4;
    public static final int T_WATERFLOW = 2 << 5;
    public static final int T_TOPFALL = 2 << 6;
    public static final int T_OUTSIDE = 2 << 7;
    public static final int T_DOWN1PIXEL = 2 << 8;
    public static final int T_BRIDGE = 2 << 9;
    public static final int T_UNDERWATER = 2 << 10;
    public static final int T_SOLIDGROUND = 2 << 11;
    public static final int T_BOTTOM = 2 << 12;
    public static final int T_DIE = 2 << 13;
    public static final int T_HEBI = 2 << 14;
    public static final int T_BANG = 2 << 15;
    public static final int T_JUM8 = 2 << 16;
    public static final int T_NT0 = 2 << 17;
    public static final int T_NT1 = 2 << 18;
    public static final int T_RIVERFLOW = 2 << 19;

    public static final byte MAP_NORMAL = 0;
    public static final byte MAP_DAUTRUONG = 1;
    public static final byte MAP_PB = 2;
    public static final byte MAP_CHIENTRUONG = 3;
    public static final byte MAP_LDGT = 4;

    public int id;
    public String name;
    public byte type;
    public byte tileId;
    public byte bgId;
    public List<Npc> npcs;
    public List<Waypoint> waypoints;
    public List<MobPosition> monsterCoordinates;
    public short tmw, tmh;
    public int[] maps;
    public int[] types;
    public short pxh, pxw;
    public List<int[]> locationStand;
    public HashMap<Integer, String> stands;
    public List<PosWater> totalWater;
    public int zoneNumber;
    public List<Integer> items;
    public List<ItemTree> vItemTreeBehind;
    public List<ItemTree> vItemTreeBetwen;
    public List<ItemTree> vItemTreeFront;
    private final int size = 24;

    public BufferedImage createImage(int zoomLevel, int x, int y, int w, int h) {
        int size = this.size * zoomLevel;
        int iw = tmw * size;
        int ih = tmh * size;
        BufferedImage img = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_ARGB);
        BufferedImage tile = loadTile(zoomLevel);
        for (int i = 0; i < tmw; i++) {
            for (int j = 0; j < tmh; j++) {
                int id = (maps[j * tmw + i]) - 1;
                if (id >= 0) {
                    BufferedImage dest = tile.getSubimage(0, id * size, size, size);
                    img.createGraphics().drawImage(dest, i * size, j * size, null);
                }
            }
        }
        return img.getSubimage(x * zoomLevel, y * zoomLevel, w * zoomLevel, h * zoomLevel);
    }

    public BufferedImage loadTile(int zoomLevel) {
        try {
            return ImageIO.read(new File("Data/Img/Map/" + zoomLevel + "/tile/" + tileId + ".png"));
        } catch (IOException ex) {
            Logger.getLogger(TileMap.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Waypoint findWaypoint(short x, short y) {
        for (Waypoint way : waypoints) {
            if (x >= (way.minX - 100) && x <= (way.maxX + 100) && y >= (way.minY - 100) && y <= (way.maxY + 100)) {
                return way;
            }
        }
        return null;
    }

    public boolean isNotChangeZone() {
        return isDungeo() || isDungeoClan() || isLoiDai() || isFujukaSanctuary() || isCandyBattlefield() || id == MapName.HANG_NYMOZ;
    }

    public boolean isWorld() {
        return isDungeo() || isDungeoClan() || isThatThuAi() || isCandyBattlefield();
    }

    public boolean isNotRivival() {
        return isLoiDai() || isDauTruong();
    }

    public boolean isNotPk() {
        return isLoiDai() || isChienTruong() || isDauTruong() || isFujukaSanctuary() || isCandyBattlefield();
    }

    public boolean isNotAllowPkPoint() {
        return this.id == 138 || this.id == 137 || this.id == 136 || this.id == 135 || this.id == 134
                || this.id == 162 || this.id == 163 || this.id == 164 || this.id == 165;
    }

    public boolean isFujukaSanctuary() {
        return id == MapName.THANH_DIA_FUJUKA;
    }

    public boolean isTalentShow() {
        return id == MapName.DAU_TRUONG;
    }

    public boolean isLangTruyenThuyet() {
        return id == MapName.LANG_SHIIBA || id == MapName.HANG_KISO || id == MapName.DAO_BAY_NOKKI
                || id == MapName.DEN_HASHI;
    }

    public boolean isLangCo() {
        return id == MapName.LANG_FEARRI || id == MapName.NUI_DORAGON || id == MapName.RUNG_MAJO
                || id == MapName.VUC_YUNIKOON || id == MapName.DONG_KINGU;
    }

    public boolean isupyen() {
        return id == MapName.UPYEN || id == MapName.UPYEN1 || id == MapName.UPYEN2
                || id == MapName.UPYEN3 || id == MapName.UPYEN4 || id == MapName.UPYEN5 || id == MapName.UPYEN6
                || id == MapName.UPYEN7 || id == MapName.UPYEN8 || id == MapName.UPYEN9;
    }
    
    public boolean isLoiDai() {
        return id == MapName.LOI_DAI || id == MapName.LOI_DAI_2 || id == MapName.LOI_DAI_3 || id == MapName.LOI_DAI_4;
    }

    public boolean isNhaThiDau() {
        return id == MapName.NHA_THI_DAU_HARUNA || id == MapName.NHA_THI_DAU_HIROSAKI
                || id == MapName.NHA_THI_DAU_OOKAZA;
    }

    public boolean isCandyBattlefield() {
        return id == MapName.KEO_CHIEN || id == MapName.KEO_DEN || id == MapName.KEO_TRANG || id == MapName.PHONG_CHO;
    }

    public boolean isNotReturnTown() {
        return isLoiDai();
    }

    public boolean isNotChangeMap() {
        return isLoiDai() || isNhaThiDau();
    }

    public boolean isNotTeleport() {
        return isLoiDai() || isDauTruong() || isChienTruong() || isDungeo() || isDungeoClan() || isNhaThiDau()
                || isThatThuAi() || isFujukaSanctuary() || isCandyBattlefield();
    }

    public boolean isNotInvite() {
        return isLoiDai();
    }

    public boolean isNotTrade() {
        return isLoiDai();
    }

    public boolean isNotSave() {
        return isDungeo() || isDungeoClan() || isChienTruong() || isDauTruong() || isLoiDai() || isFujukaSanctuary()
                || isNhaThiDau() || isThatThuAi() || id == MapName.KHU_BAO_DANH || id == MapName.HANG_INOSHISHI
                || id == MapName.DIA_DAO_CHIKATOYA || isDauTruong() || isCandyBattlefield() || id == MapName.HANG_NYMOZ;
    }

    public boolean isDungeo() {
        return type == MAP_PB;
    }

    public boolean isNormal() {
        return type == MAP_NORMAL;
    }

    public boolean isThatThuAi() {
        return id == MapName.KHU_VUC_CHO || id == MapName.THAT_THU_AI;
    }

    public boolean isNotSummon() {
        return isDauTruong() || isChienTruong() || isDungeo() || isDungeoClan() || isLoiDai() || isCandyBattlefield();
    }

    public boolean isDauTruong() {
        return id == MapName.DAU_TRUONG; // MAP_DAUTRUONG
    }

    public boolean isChienTruong() {
        return type == MAP_CHIENTRUONG;
    }

    public boolean isDungeoClan() {
        return type == MAP_LDGT;
    }

    public short collisionY(short x, short y) {
        // if (!(y % 24 != 0 && tileTypeAt(x, y, TileMap.T_TOP) && tileTypeAt(x, y,
        // TileMap.T_SOLIDGROUND))) {
        y = (short) (y / 24 * 24);
        while (y < this.pxh) {
            if (tileTypeAt(x, y, TileMap.T_TOP) || tileTypeAt(x, y, TileMap.T_BRIDGE)
                    || tileTypeAt(x, y, TileMap.T_WATERFLOW)) {
                return y;
            }
            y += 24;
        }
        // }
        return (short) (this.pxh - 1);
    }

    public short collisionX(short x, short toX, short y) {
        byte dir = (byte) (x > toX ? -1 : 1);
        int block = Math.abs(toX - x) / 24;
        short preX = -1;
        for (int i = 0; i < block; i++) {
            if (dir == -1) {
                if (tileTypeAt(toX, y, TileMap.T_LEFT)) {
                    return preX;
                }
                preX = toX;
                toX += 24;
            } else {
                if (tileTypeAt(x, y, TileMap.T_RIGHT)) {
                    return preX;
                }
                preX = x;
                x += 24;
            }
        }
        return -1;
    }

    public int tileAt(int x, int y) {
        int result;
        try {
            result = this.maps[y * this.tmw + x];
        } catch (Exception ex) {
            result = 1000;
        }
        return result;
    }

    public boolean tileTypeAt(int px, int py, int t) {
        boolean result;
        try {
            result = ((types[py / 24 * tmw + px / 24] & t) == t);
        } catch (Exception ex) {
            result = false;
        }
        return result;
    }

    public void killTileTypeAt(int px, int py, int t) {
        types[py / 24 * tmw + px / 24] &= ~t;
    }

    public int tileTypeAt(int x, int y) {
        int result;
        try {
            result = types[y * tmw + x];
        } catch (Exception ex) {
            result = 1000;
        }
        return result;
    }

    public int tileTypeAtPixel(int px, int py) {
        int result;
        try {
            result = types[py / 24 * tmw + px / 24];
        } catch (Exception ex) {
            result = 1000;
        }
        return result;
    }

    public int tileId(int px, int py) {
        int result;
        try {
            result = maps[py / 24 * tmw + px / 24];
        } catch (Exception ex) {
            result = 1000;
        }
        return result;
    }

    public int tileXofPixel(int px) {
        return px / 24 * 24;
    }

    public int tileYofPixel(int py) {
        return py / 24 * 24;
    }

    public void setPosWater() {
        totalWater = new Vector<>();
        for (int i = 0; i < this.tmw; i++) {
            for (int j = 0; j < this.tmh; j++) {
                // int num = (int) (this.maps[j * this.tmw + i] - '\u0001');
                if ((tileTypeAt(i, j) & TileMap.T_OUTSIDE) != TileMap.T_OUTSIDE) {
                    if ((tileTypeAt(i, j) & TileMap.T_WATERFALL) == TileMap.T_WATERFALL) {
                        putPosIntoVector(i * 24, j * 24);
                    }
                }
            }
        }
    }

    public boolean isWood(int x, int y) {
        int[] arr = {61, 61, 51, -1};
        int tileAt = tileId(x, y);
        if (arr[tileId - 1] == tileAt) {
            return true;
        }
        return false;
    }

    public boolean isRock(int x, int y) {
        int[] arr = {-1, 127, 111, -1};
        int tileAt = tileId(x, y);
        if (arr[tileId - 1] == tileAt) {
            return true;
        }
        return false;
    }

    public boolean isInWaypoint(int cx, int cy) {
        int num = waypoints.size();
        byte b = 0;
        while ((int) b < num) {
            Waypoint waypoint = waypoints.get((int) b);
            if (cx >= (int) waypoint.minX && cx <= (int) waypoint.maxX && cy >= (int) waypoint.minY
                    && cy <= (int) waypoint.maxY) {
                return true;
            }
            b += 1;
        }
        return false;
    }

    public void putPosIntoVector(int x, int y) {
        PosWater posWater = new PosWater();
        posWater.x = x;
        posWater.y = y;
        totalWater.add(posWater);
    }

    public void loadMap() {
        this.types = new int[this.tmw * this.tmh];
        pxh = (short) (this.tmh * 24);
        pxw = (short) (this.tmw * 24);
        try {
            int length = this.tmh * this.tmw;
            for (int i = 0; i < length; ++i) {
                if (isStand(i)) {
                    types[i] |= TileMap.T_TOP;
                }
                if (tileId == 4) {
                    if (maps[i] == 1 || maps[i] == 2 || maps[i] == 3 || maps[i] == 4 || maps[i] == 5 || maps[i] == 6
                            || maps[i] == 9 || maps[i] == 10 || maps[i] == 79 || maps[i] == 80 || maps[i] == 13
                            || maps[i] == 14 || maps[i] == 43 || maps[i] == 44 || maps[i] == 45 || maps[i] == 50) {
                        types[i] |= T_TOP;
                    }
                    if (maps[i] == 9 || maps[i] == 11) {
                        types[i] |= T_LEFT;
                    }
                    if (maps[i] == 10 || maps[i] == 12) {
                        types[i] |= T_RIGHT;
                    }
                    if (maps[i] == 13 || maps[i] == 14) {
                        types[i] |= T_BRIDGE;
                    }
                    if (maps[i] == 76 || maps[i] == 77) {
                        types[i] |= T_WATERFLOW;
                        if (maps[i] == 78) {
                            types[i] |= T_SOLIDGROUND;
                        }
                    }

                }

                if (tileId == 1) {
                    if (maps[i] == 1 || maps[i] == 2 || maps[i] == 3 || maps[i] == 4 || maps[i] == 5 || maps[i] == 6
                            || maps[i] == 7 || maps[i] == 36 || maps[i] == 37 || maps[i] == 54 || maps[i] == 91
                            || maps[i] == 92 || maps[i] == 93 || maps[i] == 94 || maps[i] == 73 || maps[i] == 74
                            || maps[i] == 97 || maps[i] == 98 || maps[i] == 116 || maps[i] == 117 || maps[i] == 118
                            || maps[i] == 120 || maps[i] == 61) {
                        types[i] |= T_TOP;
                    }
                    if (maps[i] == 2 || maps[i] == 3 || maps[i] == 4 || maps[i] == 5 || maps[i] == 6 || maps[i] == 20
                            || maps[i] == 21 || maps[i] == 22 || maps[i] == 23 || maps[i] == 36 || maps[i] == 37
                            || maps[i] == 38 || maps[i] == 39 || maps[i] == 61) {
                        types[i] |= T_SOLIDGROUND;
                    }
                    if (maps[i] == 8 || maps[i] == 9 || maps[i] == 10 || maps[i] == 12 || maps[i] == 13 || maps[i] == 14
                            || maps[i] == 30) {
                        types[i] |= T_TREE;
                    }
                    if (maps[i] == 17) {
                        types[i] |= T_WATERFALL;
                    }
                    if (maps[i] == 18) {
                        types[i] |= T_TOPFALL;
                    }
                    if (maps[i] == 37 || maps[i] == 38 || maps[i] == 61) {
                        types[i] |= T_LEFT;
                    }
                    if (maps[i] == 36 || maps[i] == 39 || maps[i] == 61) {
                        types[i] |= T_RIGHT;
                    }
                    if (maps[i] == 19) {
                        types[i] |= T_WATERFLOW;
                        if ((types[i - tmw] & T_SOLIDGROUND) == T_SOLIDGROUND) {
                            types[i] |= T_SOLIDGROUND;
                        }
                    }
                    if (maps[i] == 35) {
                        types[i] |= T_UNDERWATER;
                    }
                    if (maps[i] == 7) {
                        types[i] |= T_BRIDGE;
                    }
                    if (maps[i] == 32 || maps[i] == 33 || maps[i] == 34) {
                        types[i] |= T_OUTSIDE;
                    }

                }
                if (tileId == 2) {
                    if (maps[i] == 1 || maps[i] == 2 || maps[i] == 3
                            || maps[i] == 4 || maps[i] == 5 || maps[i] == 6
                            || maps[i] == 7 || maps[i] == 36 || maps[i] == 37
                            || maps[i] == 54 || maps[i] == 61 || maps[i] == 73
                            || maps[i] == 76 || maps[i] == 77
                            || maps[i] == 78 || maps[i] == 79 || maps[i] == 82
                            || maps[i] == 83 || maps[i] == 98 || maps[i] == 99
                            || maps[i] == 100 || maps[i] == 102 || maps[i] == 103
                            || maps[i] == 108 || maps[i] == 109 || maps[i] == 110
                            || maps[i] == 112 || maps[i] == 113 || maps[i] == 116
                            || maps[i] == 117 || maps[i] == 125 || maps[i] == 126
                            || maps[i] == 127 || maps[i] == 129 || maps[i] == 130) {
                        types[i] |= T_TOP;

                    }
                    if (maps[i] == 1 || maps[i] == 3 || maps[i] == 4 || maps[i] == 5 || maps[i] == 6 || maps[i] == 20
                            || maps[i] == 21 || maps[i] == 22 || maps[i] == 23 || maps[i] == 36 || maps[i] == 37
                            || maps[i] == 38 || maps[i] == 39 || maps[i] == 55 || maps[i] == 109 || maps[i] == 111
                            || maps[i] == 112 || maps[i] == 113 || maps[i] == 114 || maps[i] == 115 || maps[i] == 116
                            || maps[i] == 127 || maps[i] == 129 || maps[i] == 130) {
                        types[i] |= T_SOLIDGROUND;
                    }
                    if (maps[i] == 8 || maps[i] == 9 || maps[i] == 10 || maps[i] == 12
                            || maps[i] == 13 || maps[i] == 14 || maps[i] == 30 || maps[i] == 135) {
                        types[i] |= T_TREE;
                    }
                    if (maps[i] == 17) {
                        types[i] |= T_WATERFALL;
                    }
                    if (maps[i] == 18) {
                        types[i] |= T_TOPFALL;
                    }
                    if (maps[i] == 61 || maps[i] == 37 || maps[i] == 38 || maps[i] == 127 || maps[i] == 130
                            || maps[i] == 131) {
                        types[i] |= T_LEFT;
                    }
                    if (maps[i] == 61 || maps[i] == 36 || maps[i] == 39 || maps[i] == 127 || maps[i] == 129
                            || maps[i] == 132) {
                        types[i] |= T_RIGHT;
                    }
                    if (maps[i] == 19) {
                        types[i] |= T_WATERFLOW;
                        if ((types[i - tmw] & T_SOLIDGROUND) == T_SOLIDGROUND) {
                            types[i] |= T_SOLIDGROUND;
                        }
                    }
                    if (maps[i] == 134) {
                        types[i] |= T_WATERFLOW;
                        if ((types[i - tmw] & T_SOLIDGROUND) == T_SOLIDGROUND) {
                            types[i] |= T_SOLIDGROUND;
                        }
                    }
                    if (maps[i] == 35) {
                        types[i] |= T_UNDERWATER;
                    }
                    if (maps[i] == 7) {
                        types[i] |= T_BRIDGE;
                    }
                    if (maps[i] == 32 || maps[i] == 33 || maps[i] == 34) {
                        types[i] |= T_OUTSIDE;
                    }
                    if (maps[i] == 61 || maps[i] == 127) {
                        types[i] |= T_BOTTOM;
                    }
                }
                if (tileId == 3) {
                    if (maps[i] == 1 || maps[i] == 2 || maps[i] == 3
                            || maps[i] == 4 || maps[i] == 5 || maps[i] == 6
                            || maps[i] == 7 || maps[i] == 11 || maps[i] == 14
                            || maps[i] == 17 || maps[i] == 43 || maps[i] == 51
                            || maps[i] == 63 || maps[i] == 65
                            || maps[i] == 67 || maps[i] == 68 || maps[i] == 71
                            || maps[i] == 72 || maps[i] == 83 || maps[i] == 84 || maps[i] == 85 || maps[i] == 87
                            || maps[i] == 91
                            || maps[i] == 94 || maps[i] == 97 || maps[i] == 98
                            || maps[i] == 106 || maps[i] == 107 || maps[i] == 111
                            || maps[i] == 113 || maps[i] == 117 || maps[i] == 118
                            || maps[i] == 119 || maps[i] == 125 || maps[i] == 126 || maps[i] == 129 || maps[i] == 130
                            || maps[i] == 131 || maps[i] == 133 || maps[i] == 136 || maps[i] == 138 || maps[i] == 139
                            || maps[i] == 142) {
                        types[i] |= T_TOP;
                    }
                    if (maps[i] == 124 || maps[i] == 116 || maps[i] == 123 || maps[i] == 44 || maps[i] == 12
                            || maps[i] == 15 || maps[i] == 16 || maps[i] == 45 || maps[i] == 10 || maps[i] == 9) {
                        types[i] |= T_SOLIDGROUND;
                    }
                    if (maps[i] == 23) {
                        types[i] |= T_WATERFALL;
                    }
                    if (maps[i] == 24) {
                        types[i] |= T_TOPFALL;
                    }
                    if (maps[i] == 6 || maps[i] == 15 || maps[i] == 51 || maps[i] == 95 || maps[i] == 97
                            || maps[i] == 106 || maps[i] == 111 || maps[i] == 123 || maps[i] == 125 || maps[i] == 138
                            || maps[i] == 140) {
                        types[i] |= T_LEFT;
                    }
                    if (maps[i] == 7 || maps[i] == 16 || maps[i] == 51 || maps[i] == 96 || maps[i] == 98
                            || maps[i] == 107 || maps[i] == 111 || maps[i] == 124 || maps[i] == 126 || maps[i] == 139
                            || maps[i] == 141) {
                        types[i] |= T_RIGHT;
                    }
                    if (maps[i] == 25) {
                        types[i] |= T_WATERFLOW;
                        if ((types[i - tmw] & T_SOLIDGROUND) == T_SOLIDGROUND) {
                            types[i] |= T_SOLIDGROUND;
                        }
                    }
                    if (maps[i] == 34) {
                        types[i] |= T_UNDERWATER;
                    }
                    if (maps[i] == 17) {
                        types[i] |= T_BRIDGE;
                    }
                    if (maps[i] == 33 || maps[i] == 103 || maps[i] == 104 || maps[i] == 105 || maps[i] == 26
                            || maps[i] == 33) {
                        types[i] |= T_OUTSIDE;
                    }
                    if (maps[i] == 51 || maps[i] == 111 || maps[i] == 68) {
                        types[i] |= T_BOTTOM;
                    }
                    if (maps[i] == 82 || maps[i] == 110 || maps[i] == 143) {
                        types[i] |= T_DIE;
                    }
                    if (maps[i] == 113) {
                        types[i] |= T_BANG;
                    }
                    if (maps[i] == 142) {
                        types[i] |= T_HEBI;
                    }
                    if (maps[i] == 40 || maps[i] == 41) {
                        types[i] |= T_JUM8;
                    }
                    if (maps[i] == 110) {
                        types[i] |= T_NT0;
                    }
                    if (maps[i] == 143) {
                        types[i] |= T_NT1;
                    }
                }
            }
            setPosWater();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loadMapFromResource() {
        try {
            byte[] ab = GameData.getInstance().loadFile("Data/Map/" + (this.id));
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(ab));
            byte key = 0;
            if (ab[0] + ab[1] == 0) {
                dis.read();
                dis.read();
                key = ab[0];
            }
            this.tmw = (short) (dis.read() - key);
            this.tmh = (short) (dis.read() - key);
            int size = this.tmw * this.tmh;
            this.maps = new int[size];
            for (int i = 0; i < size; i++) {
                this.maps[i] = (dis.readUnsignedByte() - key);
            }
            int num2 = locationStand.size();
            this.stands = new HashMap<>();
            for (int m = 0; m < num2; m++) {
                int num3 = this.locationStand.get(m)[0];
                int num4 = this.locationStand.get(m)[0];
                int k2 = (num4 * tmw + num3);
                this.stands.put(k2, "location");
            }
            loadMap();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public boolean isStand(int index) {
        try {
            return this.stands.get(index) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isVDMQ() {
        return tileId == 4;
    }

    public class PosWater {

        public int x = -1;
        public int y = -1;
    }
}
