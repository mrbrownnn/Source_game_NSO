
package com.nsoz.lib;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author Administrator1
 */
public class ParseData {

    private JSONObject obj;

    public ParseData(JSONObject json) {
        this.obj = json;
    }

    public Object getObject(String key) {
        return obj.get(key);
    }

    public byte getByte(String key) {
        return Byte.parseByte(obj.get(key).toString());
    }

    public short getShort(String key) {
        return Short.parseShort(obj.get(key).toString());
    }

    public int getInt(String key) {
        return Integer.parseInt(obj.get(key).toString());
    }

    public long getLong(String key) {
        return Long.parseLong(obj.get(key).toString());
    }

    public String getString(String key) {
        try {
            return obj.get(key).toString();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(obj.get(key).toString());
    }

    public Date getDate(String key, String dateFormat) throws ParseException {
        try {
            String content = obj.get(key).toString();
            Date date = new SimpleDateFormat(dateFormat).parse(content);
            return date;
        } catch (Exception e) {
            return null;
        }
    }

    public JSONArray getJSONArray(String key) {
        try {
            return (JSONArray) JSONValue.parseWithException(obj.get(key).toString());
        } catch (Exception e) {
            return null;
        }
    }

    public ParseData getParseData(String key) {
        try {
            Object str = obj.get(key);
            if (str == null) {
                return null;
            }
            JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(str.toString());
            return new ParseData(jsonObject);
        } catch (Exception e) {
            return null;
        }
    }

    public ParseData[] getArrayParseData(String key) {
        try {
            JSONArray jsonArray = getJSONArray(key);
            int len = jsonArray.size();
            ParseData[] arr = new ParseData[len];
            for (int i = 0; i < len; i++) {
                arr[i] = new ParseData((JSONObject) jsonArray.get(i));
            }
            return arr;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean containsKey(String key) {
        return this.obj.containsKey(key);
    }

    public boolean containsValue(String key) {
        return this.obj.containsValue(key);
    }

    public boolean isEmpty() {
        return obj.isEmpty();
    }

    public double getDouble(String key) {
        return Double.parseDouble(obj.get(key).toString());
    }
}
