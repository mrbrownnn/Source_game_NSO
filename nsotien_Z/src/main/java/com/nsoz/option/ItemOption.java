
package com.nsoz.option;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.nsoz.item.ItemManager;
import com.nsoz.item.ItemOptionTemplate;
import com.nsoz.util.NinjaUtils;

public class ItemOption implements Cloneable {

    public ItemOption(int optionTemplateId, int param) {
        this.param = param;
        this.optionTemplate = ItemManager.getInstance().getItemOptionTemplate(optionTemplateId);
    }

    public byte active;
    public int param;
    public ItemOptionTemplate optionTemplate;

    public String getOptionString() {
        return NinjaUtils.replace(this.optionTemplate.name, "#", this.param + "");
    }

    @Override
    public ItemOption clone() {
        try {
            return (ItemOption) super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(ItemOption.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
