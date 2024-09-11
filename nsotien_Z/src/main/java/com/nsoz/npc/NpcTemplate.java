
package com.nsoz.npc;

/**
 *
 * @author pika
 */
public class NpcTemplate {

    public int npcTemplateId;
    public String name;
    public int headId;
    public int bodyId;
    public int legId;
    public String[][] menu;

    @Override
    public String toString() {
        return String.format("ID: %d name: %s head: %d body: %d leg: %d", this.npcTemplateId, this.name, this.headId, this.bodyId, this.legId);
    }
}
