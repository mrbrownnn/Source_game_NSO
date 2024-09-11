package com.nsoz.model;

import com.nsoz.ability.AbilityFromEquip;
import com.nsoz.clan.Clan;
import com.nsoz.clan.Member;
import com.nsoz.constants.SQLStatement;
import com.nsoz.db.jdbc.DbManager;
import com.nsoz.fashion.FashionFromEquip;
import com.nsoz.item.Equip;
import com.nsoz.item.Item;
import com.nsoz.item.ItemTemplate;
import com.nsoz.item.Mount;
import com.nsoz.map.Map;
import com.nsoz.map.MapManager;
import com.nsoz.network.Controller;
import com.nsoz.network.Message;
import com.nsoz.network.Service;
import com.nsoz.network.Session;
import com.nsoz.server.Config;
import com.nsoz.server.NinjaSchool;
import com.nsoz.server.ServerManager;
import com.nsoz.socket.Action;
import com.nsoz.socket.SocketIO;
import com.nsoz.task.TaskOrder;
import com.nsoz.util.Log;
import com.nsoz.util.NinjaUtils;
import com.nsoz.util.StringUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class User {

    public static void newPlay(String rand, User us) {
        try {
            Connection conn = DbManager.getInstance().getConnection(DbManager.CREATE_CHAR);
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `users` WHERE `username` = ? LIMIT 1;",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            try {
                stmt.setString(1, rand);
                ResultSet result = stmt.executeQuery();
                if (!result.first()) {
                    PreparedStatement stmt2 = conn.prepareStatement(
                            "INSERT INTO `users`(`username`, `password`, `online`, `luong`) VALUES (?, ?, ?, ?);");
                    try {
                        stmt2.setString(1, rand);
                        stmt2.setString(2, "kitakeyos");
                        stmt2.setInt(3, 0);
                        stmt2.setInt(4, 999);
                        stmt2.executeUpdate();
                    } finally {
                        stmt2.close();
                    }
                }
                result.close();
            } finally {
                stmt.close();
            }
        } catch (SQLException ex) {
        }
    }

    public Session session;
    public Service service;
    public Vector<Char> chars;
    public int id;
    public String username;
    public String password;
    public String random;
    private byte status;
    private byte activated;
    public Timestamp banUntil;
    public int gold;
    public Char sltChar;
    public boolean receivedFirstGift;
    public long lastAttendance;
    public boolean isLoadFinish;
    public boolean isEntered;
    public boolean isCleaned;
    public boolean isDuplicate;
    public int[] levelRewards = new int[5];
    public List<Integer> roles = new ArrayList<>();
    public int role;
    public int kh;
    public int efffan;
    public int effvip;
    public int efftop;
    public int effytb;
    public int effdg;
    public int effygt;
    public int effydc;
    public int effydh;
    public int nhanmocnap;
    public ArrayList<String> IPAddress;
    private boolean saving;

    public User(Session client, String username, String password, String random) {
        this.session = client;
        this.service = client.getService();
        this.username = username;
        this.password = password;
        this.random = random;
    }

    public HashMap<String, Object> getUserMap() {
        try {
            ArrayList<HashMap<String, Object>> list;
            PreparedStatement stmt = DbManager.getInstance().getConnection(DbManager.LOGIN).prepareStatement(SQLStatement.GET_USER, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, this.username);
            ResultSet data = stmt.executeQuery();
            try {
                list = DbManager.getInstance().convertResultSetToList(data);
            } finally {
                data.close();
                stmt.close();
            }
            if (list.isEmpty()) {
                return null;
            }
            HashMap<String, Object> map = list.get(0);
            if (map != null) {
                String passwordHash = (String) map.get("password");
                if (!passwordHash.equals(password)) {
                    return null;
                }
//                if (!StringUtils.checkPassword(passwordHash, password)) {
                //                   return null;//cái này mã hóa
                //              }
            }
            return map;
        } catch (SQLException e) {
            Log.error("getUserMap() err", e);
        }
        return null;
    }


    public void login() {
        try {
            if (username.equals("-1") && password.equals("12345")) {
                service.serverDialog("Bạn hãy liên hệ admin để đăng kí");
                return;
            }
            Pattern p = Pattern.compile("^[a-zA-Z0-9]+$");
            Matcher m1 = p.matcher(username);
            if (!m1.find()) {
                service.serverDialog("Tên tài khoản có kí tự lạ.");
                return;
            }
            HashMap<String, Object> map = getUserMap();
            if (map == null) {
                if (Config.getInstance().getServerID() == 2) {
                    this.username = this.username + "sv2";
                    map = getUserMap();
                    this.isDuplicate = true;
                }
                if (map == null) {
                    service.serverDialog("Tài khoản hoặc mật khẩu không chính xác.");
                    return;
                }
            }

            this.id = (int) ((long) (map.get("id")));
            this.role = (int) map.get("role");
            this.kh = (int) map.get("kh");
            this.lastAttendance = (long) map.get("last_attendance_at");
            this.receivedFirstGift = (int) map.get("received_first_gift") == 1;
            this.gold = (int) map.get("luong");
            this.getRoles();
            this.status = (byte) ((int) map.get("status"));
            this.activated = (byte) ((int) map.get("activated"));
            Object obj = map.get("ban_until");
            this.efffan = (int) map.get("efffan");
            this.effvip = (int) map.get("effvip");
            this.efftop = (int) map.get("efftop");
            this.effytb = (int) map.get("effytb");
            this.effdg = (int) map.get("effdg");
            this.effygt = (int) map.get("effygt");
            this.effydc = (int) map.get("effydc");
            this.effydh = (int) map.get("effydh");
            this.nhanmocnap = (int) map.get("nhanmocnap");
            if (obj != null) {
                this.banUntil = (Timestamp) obj;
                long now = System.currentTimeMillis();
                long timeRemaining = banUntil.getTime() - now;
                if (timeRemaining > 0) {
                    service.serverDialog(String.format("Tài khoản bị khóa trong %s. Vui lòng liên hệ admin để biết thêm chi tiết.", NinjaUtils.timeAgo((int) (timeRemaining / 1000))));
                    return;
                }
            }

//            if (this.activated == 0) {
//                service.serverDialog("Tài khoản chưa được kích hoạt. Vui lòng truy cập nsoz.me để kích hoạt tài khoản.");
//                return;
//            } 
            else if (this.status == 0) {
                service.serverDialog("Liên hệ AD để kích hoạt acc!");
                return;
            }
            if (this.gold < 0) {
                service.serverDialog("Tài khoản bị âm lượng!");
                return;
            }

            // if (Config.getInstance().SERVER == 2) {
            JSONArray rewards = (JSONArray) JSONValue.parse(map.get("level_reward").toString());
            for (int i = 0; i < 5; i++) {
                this.levelRewards[i] = Integer.parseInt(rewards.get(i).toString());
            }
            // }

            this.IPAddress = new ArrayList<>();
            obj = map.get("ip_address");
            if (obj != null) {
                String str = obj.toString();
                if (!str.equals("")) {
                    JSONArray jArr = (JSONArray) JSONValue.parse(str);
                    int size = jArr.size();
                    for (int i = 0; i < size; i++) {
                        IPAddress.add(jArr.get(i).toString());
                    }
                }
            }
            if (!IPAddress.contains(session.IPAddress)) {
                IPAddress.add(session.IPAddress);
            }
            synchronized (ServerManager.users) {
                User u = ServerManager.findUserByUsername(this.username);
                if (u != null && !u.isCleaned) {
                    service.serverDialog("Tài khoản đã có người đăng nhập.");
                    if (u.session != null && u.session.getService() != null) {
                        u.session.getService().serverDialog("Có người đăng nhập vào tài khoản của bạn.");
                    }
                    NinjaUtils.setTimeout(() -> {
                        try {
                            if (!u.isCleaned) {
                                u.session.disconnect();
                            }
                        } catch (Exception e) {
                        } finally {
                            ServerManager.removeUser(u);
                        }
                    }, 1000);
                    return;
                }
                ServerManager.addUser(this);
            }

            boolean isOnline = ((byte) map.get("online")) == 1;
            if (isOnline) {
                service.serverDialog("Tài khoản đang có người đăng nhập (2)");
                forceOutOtherServer();
                return;
            }

            this.isLoadFinish = true;
        } catch (Exception ex) {
            Log.error("login err", ex);
        }
    }

    public void forceOutOtherServer() {
        SocketIO.emit(Action.FORCE_OUT, String.format("{\"user_id\":\"%d\", \"server_id\":\"-1\", \"current_server\":\"%d\"}", this.id, Config.getInstance().getServerID()));
    }

    public void initCharacterList() {
        try {
            PreparedStatement stmt = DbManager.getInstance().getConnection(DbManager.LOAD_CHAR).prepareStatement("SELECT `players`.`id`, `players`.`name`, `players`.`gender`, `players`.`class`, `players`.`last_logout_time`, `players`.`head`, `players`.`head2`, `players`.`body`, `players`.`weapon`, `players`.`leg`, `players`.`online`, CAST(JSON_EXTRACT(data, \"$.exp\") AS INT) AS `exp` FROM `players` WHERE `players`.`user_id` = ? AND `players`.`server_id` = ? ORDER BY `players`.`last_logout_time` DESC LIMIT 3;");
            stmt.setInt(1, this.id);
            stmt.setInt(2, Config.getInstance().getServerID());
            ResultSet data = stmt.executeQuery();
            try {
                this.chars = new Vector<>();
                while (data.next()) {
                    int id = data.getInt("id");
                    Char _char = new Char(id);
                    _char.loadDisplay(data);
                    this.chars.add(_char);
                }
            } finally {
                data.close();
                stmt.close();
            }
        } catch (Exception ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createCharacter(Message ms) {
        try {
            if (this.chars.size() >= 1) {
                service.serverDialog("Bạn chỉ được tạo tối đa 1 nhân vật.");
                return;
            }
            String name = ms.reader().readUTF();
            Pattern p = Pattern.compile("^[a-z0-9]+$");
            Matcher m1 = p.matcher(name);
            if (!m1.find()) {
                service.serverDialog("Tên nhân vật không được chứa ký tự đặc biệt!");
                return;
            }
            byte gender = ms.reader().readByte();
            byte head = ms.reader().readByte();
            byte[] h = null;
            if (gender == 0) {
                h = new byte[]{11, 26, 27, 28};
                gender = 0;
            } else {
                h = new byte[]{2, 23, 24, 25};
                gender = 1;
            }
            byte temp = h[0];
            for (byte b : h) {
                if (head == b) {
                    temp = b;
                    break;
                }
            }
            head = temp;
            if (name.length() < 6 || name.length() > 15) {
                service.serverDialog("Tên tài khoản chỉ cho phép từ 6 đến 15 ký tự!");
                return;
            }
            Connection conn = DbManager.getInstance().getConnection(DbManager.CREATE_CHAR);
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `players` WHERE `user_id` = ?;",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            try {
                stmt.setInt(1, this.id);
                ResultSet check = stmt.executeQuery();
                if (check.last()) {
                    if (check.getRow() >= 1) {
                        service.serverDialog("Bạn đã tạo tối đa số nhân vât!");
                        return;
                    }
                }
                check.close();
            } finally {
                stmt.close();
            }
            stmt = conn.prepareStatement("SELECT * FROM `players` WHERE `name` = ?;", ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            try {
                stmt.setString(1, name);
                ResultSet check = stmt.executeQuery();
                if (check.last()) {
                    if (check.getRow() > 0) {
                        service.serverDialog("Tên nhân vật đã tồn tại!");
                        return;
                    }
                }
                check.close();
            } finally {
                stmt.close();
            }
            stmt = conn.prepareStatement(
                    "INSERT INTO players(`user_id`, `server_id`, `name`, `gender`, `head`, `xu`, `yen`, `skill`, `equiped`, `bag`, `box`, `mount`, `effect`, `friends`) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            try {
                stmt.setInt(1, this.id);
                stmt.setInt(2, Config.getInstance().getServerID());
                stmt.setString(3, name);
                stmt.setByte(4, gender);
                stmt.setShort(5, head);
                stmt.setInt(6, 0);
                stmt.setInt(7, 0);
                stmt.setString(8, "[]");
                stmt.setString(9, "[]");
                stmt.setString(10, "[]");
                stmt.setString(11, "[]");
                stmt.setString(12, "[]");
                stmt.setString(13, "[]");
                stmt.setString(14, "[]");
                stmt.executeUpdate();
            } finally {
                stmt.close();
            }
            initCharacterList();
            service.selectChar(chars);
        } catch (IOException | SQLException e) {
            Log.error("create char err", e);
            service.serverDialog("Tạo nhân vật thất bại!");
        }
    }

    public Char getCharByName(String name) {
        for (Char _char : this.chars) {
            if (_char.name.equals(name)) {
                return _char;
            }
        }
        return null;
    }

    public void selectChar(Message ms) {
        try {

            if (NinjaSchool.isStop) {
                service.serverDialog("Hệ thống Máy chủ bảo trì vui lòng thoát game để tránh mất dữ liệu.");
                Thread.sleep(1000);
                if (!isCleaned) {
                    session.disconnect();
                }
                return;
            }
            if (isEntered) {
                return;
            }
            String name = ms.reader().readUTF();
            if (chars == null) {
                return;
            }
            forceOutOtherServer();
            sltChar = getCharByName(name);
            if (sltChar == null) {
                session.disconnect();
                return;
            }
            if (sltChar.online) {
                service.serverDialog("Nhân vật chưa lưu xong dữ liệu.");
                Thread.sleep(1000);
                if (!isCleaned) {
                    session.disconnect();
                }
                return;
            }
            chars = null;
            if (sltChar != null) {
                long now = System.currentTimeMillis();
                long lastTime = sltChar.lastLogoutTime + 20000;
                int num = (int) ((lastTime - now) / 1000);
                // if (num > 0) {
                // service.serverDialog("Bạn chỉ có thể vào lại game sau " + num + " giây nữa");
                // return;
                // }
                if (!sltChar.load()) {
                    session.disconnect();
                    return;
                }
                sltChar.user = this;
                if (sltChar.coin < 0 || sltChar.coinInBox < 0 || sltChar.yen < 0 || this.gold < 0) {
                    lock();
                    return;
                }
                Controller controller = (Controller) session.getMessageHandler();
                controller.setChar(sltChar);
                sltChar.setService(this.service);
                sltChar.setLanguage(session.language);
                service.setChar(this.sltChar);
                byte zoneId = 0;
                int map = sltChar.mapId;
                Map m = MapManager.getInstance().find(map);
                if (m.tilemap.isNotSave()) {
                    map = sltChar.saveCoordinate;
                }
                boolean isException = false;
                try {
                    zoneId = NinjaUtils.randomZoneId(map);
                    if (zoneId == -1) {
                        isException = true;
                    }
                } catch (Exception e) {
                    isException = true;
                }
                if (isException) {
                    map = sltChar.saveCoordinate;
                    zoneId = NinjaUtils.randomZoneId(map);
                    short[] xy = NinjaUtils.getXY(map);
                    sltChar.setXY(xy[0], xy[1]);
                }
                this.sltChar.setFashionStrategy(new FashionFromEquip());
                this.sltChar.setAbilityStrategy(new AbilityFromEquip());
                this.sltChar.setAbility();
                this.sltChar.hp = this.sltChar.maxHP;
                this.sltChar.mp = this.sltChar.maxMP;
                sltChar.setFashion();
                sltChar.invite = new Invite();
                ServerManager.addChar(sltChar);
                this.service.sendDataBox();
                this.service.loadAll();
                MapManager.getInstance().joinZone(sltChar, map, zoneId);
                service.onBijuuInfo(this.id, sltChar.bijuu);
                isEntered = true;
                sltChar.getEm().displayAllEffect(service, null, sltChar);
                for (Item item : sltChar.bag) {
                    if (item != null) {
                        if (item.template.isTypeBody() || item.template.isTypeMount()
                                || item.template.isTypeNgocKham()) {
                            service.itemInfo(item, (byte) 3, (byte) item.index);
                        }
                    }
                }
                if (sltChar.equipment[ItemTemplate.TYPE_THUNUOI] != null) {
                    sltChar.getEm().setEffectPet();
                }
                Clan clan = sltChar.clan;
                if (clan != null) {
                    Member mem = clan.getMemberByName(name);
                    if (mem != null) {
                        mem.setOnline(true);
                        mem.setChar(sltChar);
                    }
                    clan.getClanService().requestClanMember();
                }
                service.sendSkillShortcut("OSkill", sltChar.onOSkill, (byte) 0);
                service.sendSkillShortcut("KSkill", sltChar.onKSkill, (byte) 0);
                service.sendSkillShortcut("CSkill", sltChar.onCSkill, (byte) 0);
                if (sltChar.taskMain != null) {
                    sltChar.updateTaskLevelUp();
                    this.service.sendTaskInfo();
                }
                for (TaskOrder task : sltChar.taskOrders) {
                    service.sendTaskOrder(task);
                }
                if (!sltChar.message.equals("")) {
                    this.service.showAlert("Hệ thống", sltChar.message);
                    sltChar.message = "";
                } else {
                    String notification = Config.getInstance().getNotification();
                    if (notification != null) {
                        this.service.showAlert("Thông Báo", notification);
                    }
                }
                Connection conn = DbManager.getInstance().getConnection(DbManager.SAVE_DATA);
                try {
                    PreparedStatement stmt2 = conn.prepareStatement("UPDATE `users` SET `online` = ? WHERE `id` = ?");
                    try {
                        stmt2.setInt(1, 1);
                        stmt2.setInt(2, this.id);
                        stmt2.executeUpdate();
                    } finally {
                        stmt2.close();
                    }
                } catch (Exception ex) {
                    Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
                }
                sltChar.lastLoginTime = System.currentTimeMillis();
                PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE `players` SET `online` = ?, `last_login_time` = ? WHERE `id` = ? LIMIT 1;");
                try {
                    stmt.setInt(1, 1);
                    stmt.setLong(2, sltChar.lastLoginTime);
                    stmt.setInt(3, sltChar.id);
                    stmt.executeUpdate();
                } finally {
                    stmt.close();
                }
                sltChar.goldUnpaid();
                sltChar.giftcodeUnpaid();
                sltChar.checkExpireMount();
                session.setName(sltChar.name);
                if (this.isDuplicate) {
                    this.service.showAlert("Quan Trọng", "Tên đăng nhập của bạn đang bị trùng lặp với một tài khoản khác, hiện tại tên đăng nhập của bạn là: " + this.username + ".\nĐể thuận tiện hơn trong việc đăng nhập, bạn hãy đổi tên đăng nhập bằng cách gặp Tajima tại làng Tone. Chọn Đổi Tên Đăng Nhập, nhập tên đăng nhập mới và mật khẩu và nhấn xác nhận");
                    sltChar.changeUsername();
                }
                if (sltChar.isCool()) {
                    sltChar.serverMessage("Lạnh quá, sức đánh và khả năng hồi phục của bạn bị giảm đi 50%, hãy tìm gosho để mua lãnh dược!");
                }
            } else {
                session.disconnect();
            }

        } catch (Exception ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public boolean isAgency() {
        return this.roles.contains(2);
    }

    public boolean isMod() {
        return this.roles.contains(3);
    }

    public void lock() {
        this.lock("");
    }

    public void lock(String message) {
        try {
            PreparedStatement stmt = DbManager.getInstance().getConnection(DbManager.SAVE_DATA)
                    .prepareStatement("UPDATE `users` SET `status` = 0 WHERE `id` = ? LIMIT 1;");
            stmt.setInt(2, this.id);
            stmt.executeUpdate();
            session.disconnect();
        } catch (Exception e) {
        }
    }

    public void lock(int hours) {
        try {
            PreparedStatement stmt = DbManager.getInstance().getConnection(DbManager.SAVE_DATA)
                    .prepareStatement("UPDATE `users` SET `ban_until` = ? WHERE `id` = ? LIMIT 1;");
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis() + hours * 60 * 60 * 1000));
            stmt.setInt(2, this.id);
            stmt.executeUpdate();
            session.disconnect();
        } catch (Exception e) {
        }
    }

    public boolean isAdmin() {
        return this.roles.contains(1);
    }

    public void saveData() {
        try {
            if (isLoadFinish && !saving) {
                saving = true;
                try {
                    JSONArray list = new JSONArray();
                    for (String ip : IPAddress) {
                        list.add(ip);
                    }
                    JSONArray rewards = new JSONArray();
                    for (int i = 0; i < 5; i++) {
                        rewards.add(levelRewards[i]);
                    }
                    String jList = list.toJSONString();
                    String jRewards = rewards.toJSONString();
                    Connection conn = DbManager.getInstance().getConnection(DbManager.SAVE_DATA);
                    PreparedStatement stmt = conn.prepareStatement(
                            "UPDATE `users` SET `luong` = ?, `online` = ?, `received_first_gift` = ?, `last_attendance_at` = ?, `ip_address` = ?, `level_reward` = ? WHERE `id` = ? LIMIT 1;");
                    try {
                        stmt.setInt(1, this.gold);
                        stmt.setInt(2, 0);
                        stmt.setInt(3, this.receivedFirstGift ? 1 : 0);
                        stmt.setLong(4, this.lastAttendance);
                        stmt.setString(5, jList);
                        stmt.setString(6, jRewards);
                        stmt.setInt(7, this.id);
                        stmt.executeUpdate();
                    } finally {
                        stmt.close();
                    }
                } finally {
                    saving = false;
                }
            }
        } catch (Exception e) {
            Log.error("save data user: " + username);
        }
    }

    public void addGold(int gold) {
        long sum = (long) this.gold + (long) gold;
        int pre = this.gold;
        if (sum > 1500000000) {
            this.gold = 1500000000;
        } else {
            this.gold += gold;
        }
        if (this.gold < 0) {
            this.gold = 0;
        }
        gold = (this.gold - pre);// ttt
        service.addGold(gold);
    }

    public void cleanUp() {
        this.isCleaned = true;
        this.sltChar = null;
        this.chars = null;
        this.session = null;
        this.service = null;
        Log.debug("clean user " + this.username);
    }

    @Override
    public String toString() {
        JSONObject obj = new JSONObject();
        obj.put("luong", this.gold);
        obj.put("received_first_gift", this.receivedFirstGift ? 1 : 0);
        obj.put("last_attendance_at", this.lastAttendance);
        obj.put("id", this.id);
        return obj.toJSONString();
    }

    public void getRoles() {
        try {
            PreparedStatement stmt = DbManager.getInstance().getConnection(DbManager.LOAD_CHAR).prepareStatement("SELECT `role_id` FROM `model_has_roles` WHERE `model_type` = ? AND `model_id` = ?");
            stmt.setString(1, "App\\Modules\\User\\Models\\User");
            stmt.setInt(2, this.id);
            ResultSet data = stmt.executeQuery();
            try {
                this.chars = new Vector<>();
                while (data.next()) {
                    int id = data.getInt("role_id");
                    roles.add(id);
                }
            } finally {
                data.close();
                stmt.close();
            }
        } catch (Exception ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addLog(String name, String description) {
        try {
            Connection conn = DbManager.getInstance().getConnection(DbManager.GAME);
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO `user_logs`(`user_id`, `type`, `description`, `created_at`, `updated_at`) VALUES (?, ?, ?, ?, ?);");
            stmt.setInt(1, this.id);
            stmt.setInt(2, 1);
            stmt.setString(3, String.format("%s: %s", name, description));
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            Log.error("add log err", e);
        }
    }

}
