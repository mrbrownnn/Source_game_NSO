package com.nsoz.network;

import com.nsoz.admin.AdminService;
import com.nsoz.constants.CMD;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.nsoz.model.User;
import com.nsoz.server.Config;
import com.nsoz.server.GameData;
import com.nsoz.server.Language;
import com.nsoz.server.NinjaSchool;
import com.nsoz.server.Server;
import com.nsoz.server.ServerManager;
import com.nsoz.util.Log;
import com.nsoz.util.NinjaUtils;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 *
 * @author ASD
 */
public class Session implements ISession {

    private byte[] key;
    public Socket sc;
    public DataInputStream dis;
    public DataOutputStream dos;
    public int id;
    public User user;
    private IMessageHandler controller;
    private Service service;
    public boolean connected;
    public boolean isLoginSuccess;
    private byte curR, curW;
    private final Sender sender;
    private Thread collectorThread;
    protected Thread sendThread;
    protected String plastfrom;
    public String version;
    protected byte clientType;
    public boolean clientOK;
    public byte zoomLevel;
    protected boolean isGPS;
    protected int width;
    protected int height;
    protected boolean isQwert;
    protected boolean isTouch;
    protected byte languageId;
    public Language language;
    protected int provider;
    protected String agent;
    public String IPAddress;
    public boolean sendKeyComplete;
    public boolean isLogin;
    public boolean isSetClientType;
    public static HashMap<String, Lock> lockLogins = new HashMap<>();
    public boolean isClosed;
    private int versionInt;
    
    public Session(Socket sc, int id) throws IOException {
        this.sc = sc;
        this.id = id;
        this.sc.setKeepAlive(true);
        // this.sc.setTcpNoDelay(true);
        // this.sc.setSoTimeout(300000);//ko hoat dong -> close
        connected = true;
        this.dis = new DataInputStream(sc.getInputStream());
        this.dos = new DataOutputStream(sc.getOutputStream());
        setHandler(new Controller(this));
        setService(new Service(this));
        sendThread = new Thread(sender = new Sender());
        String remoteSocketAddress = sc.getRemoteSocketAddress().toString();
        sendThread.setName("sender: " + remoteSocketAddress);
        collectorThread = new Thread(new MessageCollector());
        collectorThread.setName("reader: " + remoteSocketAddress);
        collectorThread.start();
    }

    public void setClientType(Message mss) throws IOException {
        if (!isSetClientType) {
            this.clientType = mss.reader().readByte();
            this.zoomLevel = mss.reader().readByte();
            if (this.zoomLevel < 1 || this.zoomLevel > 4) {
                this.zoomLevel = 1;
            }
            this.isGPS = mss.reader().readBoolean();
            this.width = mss.reader().readInt();
            this.height = mss.reader().readInt();
            this.isQwert = mss.reader().readBoolean();
            this.isTouch = mss.reader().readBoolean();
            this.plastfrom = mss.reader().readUTF();
            mss.reader().readInt();
            mss.reader().readByte();
            this.languageId = mss.reader().readByte();
            this.provider = mss.reader().readInt();
            this.agent = mss.reader().readUTF();
            this.language = GameData.getInstance().getLanguage(languageId);
            this.isSetClientType = true;
        }
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public void setHandler(IMessageHandler messageHandler) {
        this.controller = messageHandler;
    }

    public IMessageHandler getMessageHandler() {
        return this.controller;
    }

    @Override
    public void setService(Service service) {
        this.service = service;
    }

    public Service getService() {
        return this.service;
    }

    @Override
    public void sendMessage(Message message) {
        if (connected) {
            sender.addMessage(message);
        }
    }

    private void doSendMessage(Message m) {
        try {
            byte[] data = m.getData();
            byte value = m.getCommand();
            int num = data.length;
            value = num > Short.MAX_VALUE ? CMD.FULL_SIZE : value;
            byte b = value;
            if (sendKeyComplete) {
                b = writeKey(value);
            }
            dos.writeByte(b);
            if (value == CMD.FULL_SIZE) {
                if (sendKeyComplete) {
                    dos.writeByte(writeKey(m.getCommand()));
                } else {
                    dos.writeByte(m.getCommand());
                }
                int byte2 = writeKey((byte) (num >> 24));
                dos.writeByte(byte2);
                int byte3 = writeKey((byte) (num >> 16));
                dos.writeByte(byte3);
                int byte4 = writeKey((byte) (num >> 8));
                dos.writeByte(byte4);
                int byte5 = writeKey((byte) (num & 255));
                dos.writeByte(byte5);
            } else if (sendKeyComplete) {
                int byte6 = writeKey((byte) (num >> 8));
                dos.writeByte(byte6);
                int byte7 = writeKey((byte) (num & 255));
                dos.writeByte(byte7);
            } else {
                dos.writeByte(num & 0xFF00);
                dos.writeByte(num & 0xFF);
            }
            if (sendKeyComplete) {
                for (int i = 0; i < num; i++) {
                    data[i] = writeKey(data[i]);
                }
            }
            dos.write(data);
            dos.flush();
        } catch (Exception e) {

        }
    }

    public byte readKey(byte b) {
        byte b2 = this.curR;
        this.curR = (byte) (b2 + 1);
        byte result = (byte) ((key[b2] & 255) ^ (b & 255));
        if (this.curR >= key.length) {
            this.curR %= key.length;
        }
        return result;
    }

    public byte writeKey(byte b) {
        byte b2 = this.curW;
        this.curW = (byte) (b2 + 1);
        byte result = (byte) ((key[b2] & 255) ^ (b & 255));
        if (this.curW >= key.length) {
            this.curW %= key.length;
        }
        return result;
    }

    public void generateKey() {
        this.key = ("nsoz_" + (Config.getInstance().getServerID() * NinjaUtils.nextInt(10000))).getBytes();
    }

    @Override
    public void close() {
        cleanNetwork();
    }

    private void cleanNetwork() {
        try {
            if (user != null) {
                if (user.sltChar != null) {
                    if (user.sltChar.clone != null) {
                        if (!user.sltChar.clone.isCleaned) {
                            user.sltChar.clone.cleanUp();
                        }
                    }
                    if (!user.sltChar.isCleaned) {
                        user.sltChar.cleanUp();
                    }
                }
                if (!user.isCleaned) {
                    user.cleanUp();
                }
            }
            curR = 0;
            curW = 0;
            connected = false;
            isLoginSuccess = false;
            if (sc != null) {
                sc.close();
                sc = null;
            }
            if (dos != null) {
                dos.close();
                dos = null;
            }
            if (dis != null) {
                dis.close();
                dis = null;
            }
            if (sendThread != null) {
                sendThread.interrupt();
                sendThread = null;
            }
            if (collectorThread != null) {
                collectorThread.interrupt();
                collectorThread = null;
            }
            controller = null;
            language = null;
            service = null;
            //System.gc();
        } catch (IOException e) {
            Log.error("cleanNetwork err", e);
        }
    }

    @Override
    public String toString() {
        if (this.user != null) {
            return this.user.toString();
        }
        return "Client " + this.id;
    }

    public void sendKey() throws Exception {
        if (!sendKeyComplete) {
            generateKey();
            Message ms = new Message(CMD.GET_SESSION_ID);
            DataOutputStream ds = ms.writer();
            ds.writeByte(key.length);
            ds.writeByte(key[0]);
            for (int i = 1; i < key.length; i++) {
                ds.writeByte(key[i] ^ key[i - 1]);
            }
            ds.flush();
            doSendMessage(ms);
            sendKeyComplete = true;
            sendThread.start();
        }
    }

   public void login(Message ms) {
    try {
        String username = ms.reader().readUTF().trim();
        String password = ms.reader().readUTF().trim();
        String version = ms.reader().readUTF().trim();
        ms.reader().readUTF();
        ms.reader().readUTF();
        String random = ms.reader().readUTF().trim();
        byte server = ms.reader().readByte();
        Log.debug(String.format("Client id: %d - username: %s - version: %s - random: %s - server: %d", id, username, version, random, server));
        this.version = version;
        String ver = version.replaceAll("\\.", "");
        try {
            versionInt = Integer.parseInt(ver);
        } catch (NumberFormatException e) {
        }
        if (!connected || NinjaSchool.isStop || !isSetClientType || !sendKeyComplete) {
            disconnect();
            return;
        }
        if (this.isLoginSuccess) {
            return;
        }
        if (isLogin) {
            return;
        }
        isLogin = true;

        User us = new User(this, username, password, random);
        us.login();
        if (us.isLoadFinish) {
            this.isLoginSuccess = true;
            isLogin = false;
            // ServerManager.addUser(us);
            this.user = us;
            Controller controller = (Controller) getMessageHandler();
            controller.setUser(us);
            controller.setService((Service) service);
            service.updateVersion();
        } else {
            this.isLoginSuccess = false;
            isLogin = false;
            Log.debug("Client " + this.id + ": Đăng nhập thất bại.");
        }
    } catch (IOException ex) {
        Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
    }
}

    public void clientOk() {
        if (!clientOK) {
            clientOK = true;
            user.initCharacterList();
            if (user.chars != null) {
                service.selectChar(user.chars);
                Log.debug("Client " + this.id + ": đăng nhập thành công");
            } else {
                disconnect();
            }
        }
    }

    public boolean isVersion200() {
        String ver = version.replaceAll("\\.", "");
        try {
            int versionInt = Integer.parseInt(ver);
            return versionInt >= 200;
        } catch (Exception e) {
        }
        return false;
    }

    public void disconnect() {
        try {
            if (sc != null) {
                sc.close();
            }
        } catch (Exception e) {
            Log.error("disconnect err", e);
        }
    }

    public void closeMessage() {
        try {
            if (isClosed) {
                return;
            }
            isClosed = true;
            try {
                ServerManager.remove(this.IPAddress);
            } catch (Exception e) {
                Log.error("remove ipv4 from list", e);
            }
            try {
                if (user != null) {
                    try {
                        user.saveData();
                    } catch (Exception e) {
                        Log.error("save user: " + user.username + " - err: " + e.getMessage(), e);
                    } finally {
                        ServerManager.removeUser(user);
                    }
                    if (user.sltChar != null) {
                        try {
                            user.sltChar.close();
                            user.sltChar.saveData();
                        } catch (Exception e) {
                            Log.error("save player: " + user.sltChar.name + " - err: " + e.getMessage(), e);
                        } finally {
                            user.sltChar.outZone();
                            ServerManager.removeChar(user.sltChar);
                        }

                    }
                }
            } finally {
                if (controller != null) {
                    controller.onDisconnected();
                }
                close();
            }
        } catch (Exception e) {
            Log.error("closeMessage err: " + e.getMessage(), e);
        }
    }

    public void addAttendance() {
        String ip = this.IPAddress;
        int countAttendanceByIp = getCountAttendance();
        ServerManager.countAttendanceByIp.put(ip, countAttendanceByIp + 1);
    }

    public int getCountAttendance() {
        if (!ServerManager.countAttendanceByIp.containsKey(this.IPAddress)) {
            ServerManager.countAttendanceByIp.put(this.IPAddress, 0);
        }
        return ServerManager.countAttendanceByIp.get(this.IPAddress);
    }

    public void addUseGiftCode() {
        String ip = this.IPAddress;
        int countUseGiftCodeByIp = getCountUseGiftCode();
        ServerManager.countUseGiftCodeByIp.put(ip, countUseGiftCodeByIp + 1);
    }

    public int getCountUseGiftCode() {
        String ip = this.IPAddress;
        if (!ServerManager.countUseGiftCodeByIp.containsKey(ip)) {
            ServerManager.countUseGiftCodeByIp.put(ip, 0);
        }
        return ServerManager.countUseGiftCodeByIp.get(this.IPAddress);
    }

    public void setName(String name) {
        if (collectorThread != null) {
            collectorThread.setName(name);
        }
        if (sendThread != null) {
            sendThread.setName(name);
        }
    }

    private void processMessage(Message ms) {
        if (!isClosed && !Server.isStop) {
            controller.onMessage(ms);
        }
    }

    public int version() {
        return versionInt;
    }

    public boolean isVersionAbove(int version) {
        return version() >= version;
    }

    private class Sender implements Runnable {

        private final ArrayList<Message> sendingMessage;

        public Sender() {
            sendingMessage = new ArrayList<>();
        }

        public void addMessage(Message message) {
            sendingMessage.add(message);
        }

        @Override
        public void run() {
            while (connected) {
                if (sendKeyComplete) {
                    while (sendingMessage != null && sendingMessage.size() > 0) {
                        try {
                            Message m = sendingMessage.get(0);
                            if (m != null) {
                                doSendMessage(m);
                            }
                            sendingMessage.remove(0);
                        } catch (Exception e) {
                            disconnect();
                        }
                    }
                }
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException e) {
                }
            }

        }
    }

    class MessageCollector implements Runnable {

        @Override
        public void run() {
            Message message;
            try {
                while (connected) {
                    message = readMessage();
                    if (message != null) {
                        try {
                            if (!sendKeyComplete) {
                                sendKey();
                            } else {

                                processMessage(message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        break;
                    }
                }
            } catch (Exception ex) {
            }
            closeMessage();
        }

        private Message readMessage() throws Exception {
            try {
                byte cmd = dis.readByte();
                if (!sendKeyComplete && 0 <= cmd && cmd <= 6) {
                    sendKeyComplete = true;
                }
                
                if (sendKeyComplete) {
                    cmd = readKey(cmd);
                }
                int size;
                if (sendKeyComplete) {
                    byte b1 = dis.readByte();
                    byte b2 = dis.readByte();
                    size = (readKey(b1) & 255) << 8 | readKey(b2) & 255;
                } else {
                    size = dis.readUnsignedShort();
                }
                if (size > Config.getInstance().getMessageSizeMax()) {
                    throw new IOException("Data to big");
                }
                byte data[] = new byte[size];
                int len = 0;
                int byteRead = 0;
                while (len != -1 && byteRead < size) {
                    len = dis.read(data, byteRead, size - byteRead);
                    if (len > 0) {
                        byteRead += len;
                    }
                }
                if (sendKeyComplete) {
                    for (int i = 0; i < data.length; i++) {
                        data[i] = readKey(data[i]);
                    }
                }
                Message msg = new Message(cmd, data);
                return msg;
            } catch (EOFException e) {
                return null;
            }
        }
    }
}
