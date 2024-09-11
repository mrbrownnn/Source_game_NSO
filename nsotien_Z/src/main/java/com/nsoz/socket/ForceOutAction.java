
package com.nsoz.socket;

import com.nsoz.model.User;
import com.nsoz.server.ServerManager;
import com.nsoz.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import com.nsoz.network.Service;
import com.nsoz.server.Config;

/**
 *
 * @author PC
 */
public class ForceOutAction implements IAction {

    @Override
    public void call(JSONObject json) {
        try {
            int userId = json.getInt("user_id");
            if (json.has("current_server")) {
                int currentServer = json.getInt("current_server");
                if (currentServer == Config.getInstance().getServerID()) { // ignore current server
                    return;
                }
            }
            User user = ServerManager.findUserByUserID(userId);
            if (user != null && user.sltChar != null) {
                if (!user.isCleaned) {
                    ((Service) user.session.getService()).serverDialog("Có người đăng nhập vào tài khoản của bạn.");
                    user.session.disconnect();
                }
            }
        } catch (JSONException ex) {
            Log.error("Error get socket", ex);
        }
    }

}