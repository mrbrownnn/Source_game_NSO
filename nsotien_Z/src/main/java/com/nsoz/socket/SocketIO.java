package com.nsoz.socket;

import org.json.JSONException;
import org.json.JSONObject;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.nsoz.server.Config;
import com.nsoz.util.Log;

/**
 *
 * @author HIEU HIV
 */
public class SocketIO {

    public static Socket socket;
    public static boolean isInitialized;
    public static boolean connected;

    public static void init() {
        if (isInitialized) {
            return;
        }
        isInitialized = true;
        reconnect(1);
    }

    public static void listen() {
        on(Action.NEW_TALENT_WAR, new NewTalentShowAction());
        on(Action.FORCE_OUT, new ForceOutAction());
        on(Action.EXCHANGE, new ExchangeAction());
    }

    public static void connect() {
        if (connected) {
            return;
        }
        try {
            Config config = Config.getInstance();
            socket = IO.socket(config.getWebsocketHost() + ":" + config.getWebsocketPort());
            socket.connect();
            listen();
            connected = true;
            Log.info("Connect to socket server successfully!");
        } catch (Exception e) {
            Log.error("Can not connect to socket server", e);
            reconnect(10000);
        }
    }

    public static void reconnect(long time) {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(time);
                } catch (Exception ex) {
                }
                connect();
            }

        })).start();
    }

    public static void on(String event, IAction action) {
        socket.on(event, new Emitter.Listener() {
            public void call(Object... args) {
                JSONObject json = null;
                if (args.length > 0) {
                    try {
                        json = (JSONObject) args[0];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (json != null) {
                    try {
                        int serverId = json.getInt("server_id");
                        if (serverId == -1 || serverId == Config.getInstance().getServerID()) {
                            action.call(json);
                        }
                    } catch (JSONException e) {
                    }
                }
            }
        });
    }

    public static void on(byte event, IAction action) {
        on(String.valueOf(event), action);
    }

    public static void emit(String event, String data) {
        Object obj = null;
        try {
            obj = new JSONObject(data);
        } catch (JSONException e) {
            obj = data;
        }

        while (true) {
            try {
                JSONObject send = new JSONObject();
                send.put("data", obj);
                socket.emit(event, send);
                break;
            } catch (JSONException e) {
                e.printStackTrace();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SocketIO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static void emit(byte event, String data) {
        emit(String.valueOf(event), data);
    }

    public static void disconnect() {
        socket.disconnect();
    }

}
