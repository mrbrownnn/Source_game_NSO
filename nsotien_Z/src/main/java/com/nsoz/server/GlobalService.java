
package com.nsoz.server;

import com.nsoz.constants.CMD;
import com.nsoz.model.Char;
import com.nsoz.network.AbsService;
import com.nsoz.network.Message;
import com.nsoz.util.Log;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Admin
 */
public class GlobalService extends AbsService {

    private static final GlobalService instance = new GlobalService();

    public static GlobalService getInstance() {
        return instance;
    }

    @Override
    public void chat(String name, String text) {
        try {
            Message ms = new Message(CMD.CHAT_SERVER);
            DataOutputStream ds = ms.writer();
            ds.writeUTF(name);
            ds.writeUTF(text);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Log.error("chat global err: " + ex.getMessage(), ex);
        }
    }

    public void chatPrivate(String name, String text) {
        try {
            Message mss = new Message(CMD.CHAT_PRIVATE);
            DataOutputStream ds = mss.writer();
            ds.writeUTF(name);
            ds.writeUTF(text);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (Exception ex) {
            Log.error("chatPrivate err: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void sendMessage(Message ms) {
        List<Char> chars = ServerManager.getChars();
        for (Char _char : chars) {
            _char.getService().sendMessage(ms);
        }
    }

}
