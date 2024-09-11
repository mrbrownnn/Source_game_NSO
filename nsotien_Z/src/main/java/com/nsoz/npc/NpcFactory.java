
package com.nsoz.npc;

/**
 *
 * @author Admin
 */
public class NpcFactory {

    private static final NpcFactory instance = new NpcFactory();

    public static NpcFactory getInstance() {
        return instance;
    }

    public Npc newNpc(int id, int templateID, int x, int y, int status) {
        Npc npc = Npc.builder()
                .id(id)
                .templateId(templateID)
                .cx(x)
                .cy(y)
                .status(status)
                .build();
        return npc;
    }
}
