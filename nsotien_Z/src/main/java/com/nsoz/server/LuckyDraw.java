package com.nsoz.server;

import com.nsoz.constants.CMD;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.nsoz.db.jdbc.DbManager;
import com.nsoz.model.Char;
import com.nsoz.model.History;
import com.nsoz.util.NinjaUtils;
import com.nsoz.lib.ParseData;
import com.nsoz.lib.RandomCollection;
import com.nsoz.network.Message;
import java.io.DataOutputStream;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class LuckyDraw {

    public class Player {

        int id;
        public int xu;
        String name;
    }

    private int id;
    private String name;
    private int totalMoney;
    private int xuWin;
    private int timeCount;
    private String nameWin = "";
    private int typeColor;
    private int xuThamGia;
    private byte type;
    private List<Player> members = new ArrayList<>();
    private int xuMin, xuMax;
    private boolean stop;

    public LuckyDraw(String name, byte type) {
        this.name = name;
        this.type = type;
        this.id = 0;
        if (type == LuckyDrawManager.NORMAL) {
            xuMin = 10000;
            xuMax = 1000000;
        } else if (type == LuckyDrawManager.VIP) {
            xuMin = 1000000;
            xuMax = 50000000;
        }
        this.timeCount = LuckyDrawManager.TIME_COUNT_DOWN;
    }

    public int getNumberOfMemeber() {
        return this.members.size();
    }

    public synchronized void join(Char pl, int numb) {
        if (pl.trade != null) {
            pl.warningTrade();
            return;
        }
        if (!pl.isHuman) {
            pl.warningClone();
            return;
        }

        if (LuckyDrawManager.getInstance().isWaitStop()) {
            pl.serverMessage("Vòng xoay đang chờ dừng hoạt động, vui lòng thử lại sau!");
            return;
        }
        if (timeCount < 10) {
            pl.serverMessage("Đã hết thời gian tham gia vui lòng quay lại vào vòng sau");
            return;
        }
        if (this.members.size() >= 30) {
            pl.serverMessage("Số người tham gia tối đa là 30");
            return;
        }
        if (pl.coin < numb) {
            pl.serverMessage("Bạn không đủ xu để tham gia");
            return;
        }
        for (Player m : members) {
            if (m.id == pl.id) {
                if (m.xu + numb > xuMax) {
                    if (xuMax - (m.xu + numb) < xuMin) {
                        pl.serverMessage("Bạn không thể đặt thêm xu");
                    } else {
                        pl.serverMessage("Bạn chỉ có thể đặt thêm tối đa " + NinjaUtils.getCurrency(xuMax - m.xu) + " xu");
                    }
                    return;
                }
                if (numb < xuMin) {
                    pl.serverMessage("Bạn chỉ có thể đặt từ " + NinjaUtils.getCurrency(xuMin) + " đến "
                            + NinjaUtils.getCurrency(xuMax) + " xu!");
                    return;
                }
                totalMoney += numb;
                m.xu += numb;
                History history = new History(pl.id, History.VXMM_DAT);
                history.setBefore(pl.coin, pl.user.gold, pl.yen);
                pl.addCoin(-numb);
                history.setAfter(pl.coin, pl.user.gold, pl.yen);
                history.setTime(System.currentTimeMillis());
                history.setLuckyDraw(this.type, this.id, numb, "Đặt thêm");
                History.insert(history);
                pl.serverMessage("Bạn đã đặt thêm " + NinjaUtils.getCurrency(numb) + " xu thành công!");
                return;
            }
        }
        if (numb < xuMin || numb > xuMax) {
            pl.serverMessage("Bạn chỉ có thể đặt từ " + NinjaUtils.getCurrency(xuMin) + " đến "
                    + NinjaUtils.getCurrency(xuMax) + " xu!");
            return;
        }
        Player m = new Player();
        m.id = pl.id;
        m.xu = numb;
        totalMoney += numb;
        m.name = pl.name;
        members.add(m);
        History history = new History(pl.id, History.VXMM_DAT);
        history.setBefore(pl.coin, pl.user.gold, pl.yen);
        pl.addCoin(-numb);
        history.setAfter(pl.coin, pl.user.gold, pl.yen);
        history.setTime(System.currentTimeMillis());
        history.setLuckyDraw(this.type, this.id, numb, "Đặt mới");
        History.insert(history);
        pl.serverMessage("Bạn đã tham gia " + NinjaUtils.getCurrency(numb) + " xu thành công");
    }

    public void update() {
        if (!stop) {
            boolean isWaitStop = LuckyDrawManager.getInstance().isWaitStop();
            int numberOfMember = getNumberOfMemeber();
            if (numberOfMember >= 2) {
                timeCount--;
                if (timeCount <= 0) {
                    try {
                        randomCharWin();
                        result();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        if (isWaitStop) {
                            stop();
                        } else {
                            refresh();
                        }
                    }
                }
            } else {
                if (isWaitStop) {
                    stop();
                }
            }
        }
    }

    public void stop() {
        this.stop = true;
    }

    public void randomCharWin() {
        try {
            RandomCollection<Player> rd = new RandomCollection<>();
            for (Player m : members) {
                try {
                    rd.add(m.xu, m);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Player m = rd.next();
            int receive = totalMoney;
            if (members.size() > 10) {
                receive -= receive / 10;
            } else {
                receive -= receive * (members.size() - 1) / 100;
            }
            History history = new History(m.id, History.VXMM_THANG);
            Char pl = ServerManager.findCharById(m.id);
            if (pl != null) {
                history.setBefore(pl.coin, pl.user.gold, pl.yen);
                pl.addCoin(receive);
                history.setAfter(pl.coin, pl.user.gold, pl.yen);
            } else {
                long coin = 0;
                int gold = 0;
                int yen = 0;
                Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT `players`.`xu`, `players`.`data`, `players`.`yen`, `users`.`luong` FROM `players` INNER JOIN `users` ON `players`.`user_id` = `users`.`id` WHERE `players`.`id` = ?;",
                        ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

                try {
                    stmt.setInt(1, m.id);
                    ResultSet res = stmt.executeQuery();
                    if (res.first()) {
                        JSONObject json = (JSONObject) JSONValue.parse(res.getString("data"));
                        ParseData parse = new ParseData(json);
                        long coinMax = parse.getLong("coinMax");

                        coin = res.getLong("xu");
                        yen = res.getInt("yen");
                        gold = res.getInt("luong");
                        history.setBefore(coin, gold, yen);
                        coin += receive;
                        if (coin > coinMax) {
                            coin = coinMax;
                        }
                        history.setAfter(coin, gold, yen);
                    }
                    res.close();
                } finally {
                    stmt.close();
                }
                DbManager.getInstance().updateCoin(m.id, (int) coin);
            }
            history.setLuckyDraw(this.type, this.id, receive, "Thắng");
            history.setTime(System.currentTimeMillis());
            History.insert(history);
            nameWin = m.name;
            xuWin = receive;
            xuThamGia = m.xu;
        } catch (Exception ex) {
            Logger.getLogger(LuckyDraw.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getNumberMoney() {
        return totalMoney;
    }

    public Player find(int id) {
        synchronized (members) {
            for (Player pl : members) {
                if (pl.id == id) {
                    return pl;
                }
            }
        }
        return null;
    }

    public void refresh() {
        this.id++;
        timeCount = LuckyDrawManager.TIME_COUNT_DOWN;
        totalMoney = 0;
        members.clear();
        typeColor = NinjaUtils.nextInt(10);
    }

    public void result() {
        String name = "Admin";
        String text = "Chúc mừng " + nameWin.toUpperCase() + " đã chiến thắng " + NinjaUtils.getCurrency(xuWin) + " xu trong trò chơi Vòng xoay may mắn với " + NinjaUtils.getCurrency(xuThamGia) + " xu";
        GlobalService.getInstance().chat(name, text);
    }

    public void show(Char p) {
        try {
            Player pl = find(p.id);
            int xu = 0;
            if (pl != null) {
                xu = pl.xu;
            }
            int total = totalMoney;
            if (total == 0) {
                total = 1;
            }
            float percent = (float) xu * 100f / (float) total;
            String[] splits = String.format("%.2f", percent).replaceAll(",", ".").split("\\.");
            int p1 = Integer.parseInt(splits[0]);
            int p2 = Integer.parseInt(splits[1]);
            Message ms = new Message(CMD.ALERT_MESSAGE);
            DataOutputStream ds = ms.writer();
            ds.writeUTF("typemoi");
            ds.writeUTF(this.name);
            ds.writeShort(this.timeCount);
            ds.writeUTF(String.format("%sXu", NinjaUtils.getCurrency(this.totalMoney)));
            ds.writeShort(p1);
            if (p2 > 0 && p2 < 10) {
                ds.writeUTF(splits[1]);
            } else {
                ds.writeUTF(String.valueOf(p2));
            }
            ds.writeShort(getNumberOfMemeber());
            if (!nameWin.equals("")) {
                ds.writeUTF("Người vừa chiến thắng:" + NinjaUtils.getColor(typeColor) + nameWin
                        + "\nSố xu thắng: " + NinjaUtils.getCurrency(xuWin) + "Xu \nSố xu tham gia: "
                        + NinjaUtils.getCurrency(xuThamGia) + "Xu");
            } else {
                ds.writeUTF("Chưa có thông tin!");
            }
            ds.writeByte(type);
            ds.writeUTF(String.format("%s", NinjaUtils.getCurrency(xu)));
            ds.flush();
            p.getService().sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(LuckyDraw.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
}
