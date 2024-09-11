
package com.nsoz.map.world;

import com.nsoz.constants.CMD;
import com.nsoz.model.Char;
import com.nsoz.network.AbsService;
import com.nsoz.network.Message;
import com.nsoz.util.Log;
import java.io.DataOutputStream;
import java.util.List;

/**
 *
 * @author Admin
 */
public class WorldService extends AbsService {

    private World world;

    public WorldService(World world) {
        this.world = world;
    }

    public void sendTimeInMap(int timeCountDown) {
        try {
            Message ms = messageSubCommand(CMD.MAP_TIME);
            DataOutputStream ds = ms.writer();
            ds.writeInt(timeCountDown);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception e) {

        }
    }

    @Override
    public void chat(String name, String text) {

    }

    @Override
    public void sendMessage(Message ms) {
        List<Char> members = world.getMembers();
        for (Char _char : members) {
            try {
                _char.getService().sendMessage(ms);
            } catch (Exception e) {
                Log.error("worldService sendMessage ex: " + e.getMessage(), e);
            }
        }
    }

}
