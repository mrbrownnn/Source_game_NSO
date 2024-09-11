
package com.nsoz.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author PC
 */
public class Invite {

    public static final byte GIAO_DICH = 0;
    public static final byte NHOM = 1;
    public static final byte XIN_VAO_NHOM = 2;
    public static final byte TY_THI = 4;
    public static final byte GIA_TOC = 5;
    public static final byte CHANGE_ZONE = 6;
    public static final byte PK = 7;
    public static final byte GIA_TOC_CHIEN = 8;

    private HashMap<Byte, List<PlayerInvite>> list = new HashMap<>();

    public Invite() {
        list.put(GIAO_DICH, new ArrayList<>());
        list.put(NHOM, new ArrayList<>());
        list.put(XIN_VAO_NHOM, new ArrayList<>());
        list.put(TY_THI, new ArrayList<>());
        list.put(GIA_TOC, new ArrayList<>());
        list.put(GIA_TOC_CHIEN, new ArrayList<>());
        list.put(CHANGE_ZONE, new ArrayList<>());
        list.put(PK, new ArrayList<>());
    }

    public void addCharInvite(byte type, int charId, int time) {
        List<PlayerInvite> v = list.get(type);
        v.add(new PlayerInvite(charId, time));
    }

    public PlayerInvite findCharInvite(byte type, int charId) {
        List<PlayerInvite> v = list.get(type);
        for (PlayerInvite in : v) {
            if (in.charId == charId) {
                return in;
            }
        }
        return null;
    }

    public void update() {
        Collection<List<PlayerInvite>> collection = list.values();
        for (List<PlayerInvite> v : collection) {
            int size = v.size();
            List<PlayerInvite> removes = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                PlayerInvite p = v.get(i);
                p.time--;
                if (p.time < 0) {
                    removes.add(p);
                }
            }
            v.removeAll(removes);
        }
    }

    public class PlayerInvite {

        public PlayerInvite(int charId, int time) {
            this.charId = charId;
            this.time = time;
        }

        private int charId;
        public int time;
    }
}
