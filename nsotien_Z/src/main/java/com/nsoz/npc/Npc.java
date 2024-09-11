
package com.nsoz.npc;

import com.nsoz.map.MapService;
import lombok.Builder;
import lombok.Setter;

/**
 *
 * @author PC
 */
public class Npc {

    public int id;
    public int cx, cy;
    public NpcTemplate template;
    public boolean isFocus = true;
    public int status;
    @Setter
    private MapService service;

    @Builder
    public Npc(int id, int status, int cx, int cy, int templateId) {
        this.id = id;
        this.cx = cx;
        this.cy = cy;
        this.status = status;
        this.template = NpcManager.getInstance().find(templateId);
    }

    public void setStatus(int status) {
        this.status = status;
        service.npcUpdate(this);
    }
}
