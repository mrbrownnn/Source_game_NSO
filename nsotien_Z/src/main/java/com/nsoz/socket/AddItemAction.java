
package com.nsoz.socket;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.nsoz.item.Item;
import com.nsoz.model.User;
import com.nsoz.server.ServerManager;
import com.nsoz.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class AddItemAction implements IAction {

    @Override
    public void call(JSONObject json) {
        try {
            String name = json.getString("name");
            String itemObj = json.getString("item");
            User user = ServerManager.findUserByUsername(name);
            if (user != null && user.sltChar != null) {
                Item item = new Item((org.json.simple.JSONObject) JSONValue.parseWithException(itemObj));
                user.sltChar.addItemToBag(item);
            }
        } catch (JSONException ex) {
            Log.info("Error get socket");
        } catch (ParseException ex) {
            Logger.getLogger(AddItemAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
