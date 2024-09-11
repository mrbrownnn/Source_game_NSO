package com.nsoz.model;

import com.nsoz.lib.ParseData;

import org.json.simple.JSONObject;

public class Friend {

    public String name;
    public byte type;

    public Friend(String name, byte type) {
        this.name = name;
        this.type = type;
    }

    public Friend(JSONObject obj) {
        load(obj);
    }

    private void load(JSONObject obj) {
        ParseData parse = new ParseData(obj);
        this.type = parse.getByte("type");
        this.name = parse.getString("name");
    }

    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("name", this.name);
        obj.put("type", this.type);
        return obj;
    }
}
