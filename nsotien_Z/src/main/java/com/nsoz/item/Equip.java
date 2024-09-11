package com.nsoz.item;

import com.nsoz.option.ItemOption;
import com.nsoz.lib.ParseData;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Equip extends Item {

    public Equip(int id) {
        super(id);
    }

    public Equip(JSONObject obj) {
        super(obj);
    }

    @Override
    public void load(JSONObject obj) {
        ParseData parse = new ParseData(obj);
        loadHeader(parse);
        this.template = ItemManager.getInstance().getItemTemplate(this.id);
        this.upgrade = parse.getByte("upgrade");
        this.sys = parse.getByte("sys");
        this.yen = parse.getInt("yen");
        JSONArray ability = parse.getJSONArray("options");
        int size2 = ability.size();
        this.options = new ArrayList<>();
        for (int c = 0; c < size2; c++) {
            JSONArray jAbility = (JSONArray) ability.get(c);
            int templateId = Integer.parseInt(jAbility.get(0).toString());
            int param = Integer.parseInt(jAbility.get(1).toString());
            if (templateId == 46 && param == 800) {
                param = 55;
            }
            this.options.add(new ItemOption(templateId, param));
        }
        if (this.template.isTypeAdorn() || this.template.isTypeClothe() || this.template.isTypeWeapon()) {
            this.gems = new ArrayList<>();
            if (parse.containsKey("gems")) {
                JSONArray gems = parse.getJSONArray("gems");
                for (int i = 0; i < gems.size(); i++) {
                    Item gem = new Item((JSONObject) gems.get(i));
                    if (gem.template.isTypeNgocKham()) {
                        addGem(gem);
                    }
                }
            }
            removeOptionGems();
        }
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        saveHeader(obj);
        obj.put("sys", this.sys);
        obj.put("yen", this.yen);
        obj.put("upgrade", this.upgrade);
        JSONArray options = new JSONArray();
        for (ItemOption option : this.options) {
            JSONArray ability = new JSONArray();
            ability.add(option.optionTemplate.id);
            ability.add(option.param);
            options.add(ability);
        }
        obj.put("options", options);
        if (this.template.isTypeAdorn() || this.template.isTypeClothe() || this.template.isTypeWeapon()) {
            JSONArray gems = new JSONArray();
            for (Item gem : this.gems) {
                gems.add(gem.toJSONObject());
            }
            obj.put("gems", gems);
        }
        return obj;
    }
}
