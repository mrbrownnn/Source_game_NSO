package com.nsoz.server;

import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.ImageIcon;

import com.nsoz.model.Char;
import com.nsoz.stall.StallManager;
import com.nsoz.clan.Clan;
import com.nsoz.db.jdbc.DbManager;
import com.nsoz.util.Log;
import com.nsoz.util.NinjaUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ASD
 */
public class NinjaSchool extends WindowAdapter implements ActionListener {

    private Frame frame;

    public static boolean isStop = false;

    public NinjaSchool() {
        try {
            frame = new Frame("Manger");
            InputStream is = getClass().getClassLoader().getResourceAsStream("icon.png");
            byte[] data = new byte[is.available()];
            is.read(data);
            ImageIcon img = new ImageIcon(data);
            frame.setIconImage(img.getImage());
            frame.setSize(200, 360);
            frame.setBackground(Color.DARK_GRAY);
            frame.setResizable(false);
            frame.addWindowListener(this);
            //Button b = new Button("Bảo trì");
            //b.setBounds(30, 60, 140, 30);
            //b.setActionCommand("stop");
            //b.addActionListener(this);
            //frame.add(b);
            Button b2 = new Button("Lưu Shinwa");
            b2.setBounds(30, 100, 140, 30);
            b2.setActionCommand("shinwa");
            b2.addActionListener(this);
            frame.add(b2);
            Button b3 = new Button("Lưu dữ liệu gia tộc");
            b3.setBounds(30, 140, 140, 30);
            b3.setActionCommand("clan");
            b3.addActionListener(this);
            frame.add(b3);
            Button b4 = new Button("Lưu dữ liệu người chơi");
            b4.setBounds(30, 180, 140, 30);
            b4.setActionCommand("player");
            b4.addActionListener(this);
            frame.add(b4);
            Button b5 = new Button("Làm mới TOP");
            b5.setBounds(30, 220, 140, 30);
            b5.setActionCommand("rank");
            b5.addActionListener(this);
            frame.add(b5);
            Button b6 = new Button("Restart DB");
            b6.setBounds(30, 260, 140, 30);
            b6.setActionCommand("restartDB");
            b6.addActionListener(this);
            frame.add(b6);
            Button b7 = new Button("Gửi Đồ");
            b7.setBounds(30, 300, 140, 30);
            b7.setActionCommand("sendItem");
            b7.addActionListener(this);
            frame.add(b7);
            frame.setLocationRelativeTo(null);
            frame.setLayout(null);
            frame.setVisible(true);
        } catch (IOException ex) {
            Logger.getLogger(NinjaSchool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String args[]) {
        if (Config.getInstance().load()) {
            if (!DbManager.getInstance().start()) {
                return;
            }
            if (NinjaUtils.availablePort(Config.getInstance().getPort())) {
                new NinjaSchool();
                if (!Server.init()) {
                    Log.error("Khoi tao that bai!");
                    return;
                }
                Server.start();
            } else {
                Log.error("Port " + Config.getInstance().getPort() + " da duoc su dung!");
            }
        } else {
            Log.error("Vui long kiem tra lai cau hinh!");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("shinwa")) {
            if (Server.start) {
                Log.info("Lưu Shinwa");
                StallManager.getInstance().save();
                Log.info("Lưu xong");
            } else {
                Log.info("Mãy chủ chưa bật");
            }
        }
        /*if (e.getActionCommand().equals("stop")) {
            if (Server.start) {
                if (!isStop) {
                    (new Thread(new Runnable() {
                        public void run() {
                            try {
                                Server.maintance();
                                System.exit(0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    })).start();
                }

            } else {
                Log.info("Máy chủ chưa bật.");
            }
        }*/
        if (e.getActionCommand().equals("clan")) {
            Log.info("Lưu dữ liệu gia tộc.");
            List<Clan> clans = Clan.getClanDAO().getAll();
            synchronized (clans) {
                for (Clan clan : clans) {
                    Clan.getClanDAO().update(clan);
                }
            }
            Log.info("Lưu xong");
        }
        if (e.getActionCommand().equals("rank")) {
            List<Char> chars = ServerManager.getChars();
            for (Char _char : chars) {
                _char.saveData();
            }
            Log.info("Làm mới bảng xếp hạng");
            Ranked.refresh();
        }
        if (e.getActionCommand().equals("player")) {
            Log.info("Lưu dữ liệu người chơi");
            List<Char> chars = ServerManager.getChars();
            for (Char _char : chars) {
                try {
                    if (_char != null && !_char.isCleaned) {
                        _char.saveData();
                        if (_char.clone != null && !_char.clone.isCleaned) {
                            _char.clone.saveData();
                        }
                        if (_char.user != null && !_char.user.isCleaned) {
                            if (_char.user != null) {
                                _char.user.saveData();
                            }

                        }

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            Log.info("Lưu xong");
        }
        if (e.getActionCommand().equals("restartDB")) {
            Log.info("Bắt đầu khởi động lại!");
            DbManager.getInstance().shutdown();
            DbManager.getInstance().start();
            Log.info("Khởi động xong!");
        }
        if (e.getActionCommand().equals("sendItem")) {
            JFrameSendItem.run();
        }
    }

    public void windowClosing(WindowEvent e) {
        frame.dispose();
        if (Server.start) {
            Log.info("Đóng máy chủ.");
            Server.stop();
            System.exit(0);
        }
    }

    // public static void generateSkill() {
    // int skillId = 84;
    // int imgID = 3735;
    // HashMap<Integer, EffectCharPaint> effects = new HashMap<>();
    // HashMap<Integer, Integer> img = new HashMap<>();
    // SkillPaint skill = null;
    // for (SkillPaint sk : Server.sks) {
    // if (sk.id == skillId) {
    // skill = sk.clone();
    // }
    // }
    // int effID = Server.efs.size() + 1;
    // if (skill.effId > 0) {
    // EffectCharPaint eff = Server.efs.get(skill.effId - 1);
    // effects.put(eff.idEf, eff);
    // }
    // for (SkillInfoPaint skillInfo : skill.skillStand) {
    // if (skillInfo.effS0Id > 0) {
    // EffectCharPaint eff = Server.efs.get(skillInfo.effS0Id - 1);
    // effects.put(eff.idEf, eff);
    // }
    // if (skillInfo.effS1Id > 0) {
    // EffectCharPaint eff = Server.efs.get(skillInfo.effS1Id - 1);
    // effects.put(eff.idEf, eff);
    // }
    // if (skillInfo.effS2Id > 0) {
    // EffectCharPaint eff = Server.efs.get(skillInfo.effS2Id - 1);
    // effects.put(eff.idEf, eff);
    // }
    // }
    // for (SkillInfoPaint skillInfo : skill.skillfly) {
    // if (skillInfo.effS0Id > 0) {
    // EffectCharPaint eff = Server.efs.get(skillInfo.effS0Id - 1);
    // effects.put(eff.idEf, eff);
    // }
    // if (skillInfo.effS1Id > 0) {
    // EffectCharPaint eff = Server.efs.get(skillInfo.effS1Id - 1);
    // effects.put(eff.idEf, eff);
    // }
    // if (skillInfo.effS2Id > 0) {
    // EffectCharPaint eff = Server.efs.get(skillInfo.effS2Id - 1);
    // effects.put(eff.idEf, eff);
    // }
    // }
    // for (EffectCharPaint eff : effects.values()) {
    // for (EffectInfoPaint effInfo : eff.arrEfInfo) {
    // if (!img.containsKey(effInfo.idImg)) {
    // for (int i = 1; i <= 4; i++) {
    // byte[] data = NinjaUtils.getFile("Data/Img/Small/" + i + "/Small" +
    // effInfo.idImg + ".png");
    // NinjaUtils.saveFile("Data/Img/Small/" + i + "/Small" + imgID + ".png", data);
    // }
    // img.put(effInfo.idImg, imgID);
    // imgID++;
    // }
    // }
    // for (EffectInfoPaint effInfo : eff.arrEfInfo) {
    // for (Map.Entry<Integer, Integer> entry : img.entrySet()) {
    // int before = entry.getKey();
    // int after = entry.getValue();
    // if (effInfo.idImg == before) {
    // effInfo.idImg = after;
    // break;
    // }
    // }
    // }
    // eff.idEf = effID;
    // effID++;
    // JSONArray arr = new JSONArray();
    // for (EffectInfoPaint effPaint : eff.arrEfInfo) {
    // JSONObject obj = new JSONObject();
    // obj.put("id", effPaint.idImg);
    // obj.put("dx", effPaint.dx);
    // obj.put("dy", effPaint.dy);
    // arr.add(obj);
    // }
    // NinjaUtils.saveFile("eff_" + eff.idEf, arr.toJSONString().getBytes());
    // }
    // JSONArray skillStand = new JSONArray();
    // for (SkillInfoPaint skillInfo : skill.skillStand) {
    // for (Map.Entry<Integer, EffectCharPaint> entry : effects.entrySet()) {
    // int key = entry.getKey();
    // if (skillInfo.effS0Id == key) {
    // skillInfo.effS0Id = entry.getValue().idEf;
    // }
    // if (skillInfo.effS1Id == key) {
    // skillInfo.effS1Id = entry.getValue().idEf;
    // }
    // if (skillInfo.effS2Id == key) {
    // skillInfo.effS2Id = entry.getValue().idEf;
    // }
    //
    // }
    // JSONObject obj = new JSONObject();
    // obj.put("effS0Id", skillInfo.effS0Id);
    // obj.put("effS1Id", skillInfo.effS1Id);
    // obj.put("effS2Id", skillInfo.effS2Id);
    // obj.put("status", skillInfo.status);
    // obj.put("arrowId", skillInfo.arrowId);
    // obj.put("adx", skillInfo.adx);
    // obj.put("ady", skillInfo.ady);
    // obj.put("e0dx", skillInfo.e0dx);
    // obj.put("e0dy", skillInfo.e0dy);
    // obj.put("e1dx", skillInfo.e1dx);
    // obj.put("e1dy", skillInfo.e1dy);
    // obj.put("e2dx", skillInfo.e2dx);
    // obj.put("e2dy", skillInfo.e2dy);
    // skillStand.add(obj);
    // }
    // JSONArray skillfly = new JSONArray();
    // for (SkillInfoPaint skillInfo : skill.skillfly) {
    // for (Map.Entry<Integer, EffectCharPaint> entry : effects.entrySet()) {
    // int key = entry.getKey();
    // if (skillInfo.effS0Id == key) {
    // skillInfo.effS0Id = entry.getValue().idEf;
    // }
    // if (skillInfo.effS1Id == key) {
    // skillInfo.effS1Id = entry.getValue().idEf;
    // }
    // if (skillInfo.effS2Id == key) {
    // skillInfo.effS2Id = entry.getValue().idEf;
    // }
    //
    // }
    // JSONObject obj = new JSONObject();
    // obj.put("effS0Id", skillInfo.effS0Id);
    // obj.put("effS1Id", skillInfo.effS1Id);
    // obj.put("effS2Id", skillInfo.effS2Id);
    // obj.put("status", skillInfo.status);
    // obj.put("arrowId", skillInfo.arrowId);
    // obj.put("adx", skillInfo.adx);
    // obj.put("ady", skillInfo.ady);
    // obj.put("e0dx", skillInfo.e0dx);
    // obj.put("e0dy", skillInfo.e0dy);
    // obj.put("e1dx", skillInfo.e1dx);
    // obj.put("e1dy", skillInfo.e1dy);
    // obj.put("e2dx", skillInfo.e2dx);
    // obj.put("e2dy", skillInfo.e2dy);
    // skillfly.add(obj);
    // }
    // NinjaUtils.saveFile("skill_stand", skillStand.toJSONString().getBytes());
    // NinjaUtils.saveFile("skill_fly", skillfly.toJSONString().getBytes());
    // for (Map.Entry<Integer, EffectCharPaint> entry : effects.entrySet()) {
    // int key = entry.getKey();
    // if (key == skill.effId) {
    // skill.effId = entry.getValue().idEf;
    // }
    // }
    // Log.debug("effId: " + (skill.effId));
    // }
}
