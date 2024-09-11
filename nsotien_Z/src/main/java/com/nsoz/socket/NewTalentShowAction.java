
package com.nsoz.socket;

import com.nsoz.map.zones.TalentShow;
import com.nsoz.map.MapManager;
import com.nsoz.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NewTalentShowAction implements IAction {

    @Override
    public void call(JSONObject json) {
        try {
            String blackName = json.getString("blackName");
            String whiteName = json.getString("whiteName");
            JSONArray blackNames = json.getJSONArray("blackMemberNames");
            JSONArray whiteNames = json.getJSONArray("whiteMemberNames");

            try {
                TalentShow tls = MapManager.getInstance().talentShow;
                if (tls.started || tls.invited || !tls.opened) {
                    return;
                }
                if (whiteNames.length() == 1 && whiteNames.length() == 1) {
                    tls.whiteName = whiteNames.getString(0);
                    tls.blackName = blackNames.getString(0);
                } else {
                    tls.whiteName = blackName;
                    tls.blackName = whiteName;
                }

                tls.whiteMemberNames.clear();
                tls.blackMemberNames.clear();

                for (int i = 0; i < whiteNames.length(); i++) {
                    String name = whiteNames.getString(i);
                    tls.whiteMemberNames.add(name);
                }

                for (int i = 0; i < blackNames.length(); i++) {
                    String name = blackNames.getString(i);
                    tls.blackMemberNames.add(name);
                }

                tls.invite();

            } catch (JSONException e) {
                Log.error("socketio call() err", e);
            }

        } catch (Exception ex) {
            Log.error("Error get socket", ex);
        }
    }

}
