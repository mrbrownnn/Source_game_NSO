package com.nsoz.network;

import java.io.*;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.nsoz.model.Figurehead;
import com.nsoz.constants.CMD;
import com.nsoz.model.Char;
import com.nsoz.clan.Clan;
import com.nsoz.map.world.Dungeon;
import com.nsoz.effect.Effect;
import com.nsoz.effect.EffectAutoData;
import com.nsoz.item.Equip;
import com.nsoz.model.Friend;
import com.nsoz.item.Item;
import com.nsoz.map.item.ItemMap;
import com.nsoz.store.ItemStore;
import com.nsoz.map.ItemTree;
import com.nsoz.map.Map;
import com.nsoz.map.MapManager;
import com.nsoz.mob.Mob;
import com.nsoz.npc.Npc;
import com.nsoz.npc.NpcManager;
import com.nsoz.skill.Skill;
import com.nsoz.task.Task;
import com.nsoz.task.TaskOrder;
import com.nsoz.map.TileMap;
import com.nsoz.map.Waypoint;
import com.nsoz.map.zones.Zone;
import com.nsoz.clan.Member;
import com.nsoz.constants.ItemName;
import com.nsoz.constants.MobName;
import com.nsoz.map.world.Arena;
import com.nsoz.model.ConfirmPopup;
import com.nsoz.model.InputDialog;
import com.nsoz.model.Menu;
import com.nsoz.model.ThanThu;
import com.nsoz.option.ItemOption;
import com.nsoz.server.Config;
import com.nsoz.server.Server;
import com.nsoz.mob.MobTemplate;
import com.nsoz.model.Trader;
import com.nsoz.constants.NpcName;
import com.nsoz.npc.NpcTemplate;
import com.nsoz.party.Group;
import com.nsoz.party.MemberGroup;
import com.nsoz.constants.TaskName;
import com.nsoz.effect.EffectAutoDataManager;
import com.nsoz.effect.EffectData;
import com.nsoz.effect.EffectDataManager;
import com.nsoz.event.Event;
import com.nsoz.item.ItemManager;
import com.nsoz.item.ItemTemplate;
import com.nsoz.map.world.World;
import com.nsoz.mob.MobManager;
import com.nsoz.model.Card;
import com.nsoz.server.GameData;
import com.nsoz.task.TaskTemplate;
import com.nsoz.thiendia.Ranking;
import com.nsoz.util.Log;
import java.util.ArrayList;
import java.util.HashMap;

public class Service extends AbsService {

    private Session session;
    private Char player;

    public Service(Session session) {
        this.session = session;
    }

    public void setChar(Char pl) {
        this.player = pl;
    }

    public void doShowRankedListUI(List<Ranking> list) {
        try {
            Message ms = new Message(CMD.RANKED_MATCH);
            DataOutputStream ds = ms.writer();
            ds.writeByte(list.size());
            for (Ranking rank : list) {
                ds.writeUTF(rank.getName());
                ds.writeInt(rank.getRanked());
                ds.writeUTF(rank.getStt());
            }
            ds.flush();
            sendMessage(ms);
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void reviewCT(String text, boolean reward) {
        try {

            Message ms = messageNotMap(CMD.REVIEW_CT);
            DataOutputStream ds = ms.writer();
            ds.writeUTF(text);
            ds.writeBoolean(reward);
            ds.flush();
            sendMessage(ms);
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void selectChar(Vector<Char> list) {
        try {
            Message ms = messageNotMap(CMD.SELECT_PLAYER);
            DataOutputStream ds = ms.writer();
            ds.writeByte(list.size());
            for (Char _char : list) {
                ds.writeByte(_char.gender); // gender
                ds.writeUTF(_char.name); // name
                ds.writeUTF(_char.school); // phai
                ds.writeByte(_char.level);// Level
                ds.writeShort(_char.head); // Head
                ds.writeShort(_char.weapon); // Weapon
                ds.writeShort(_char.body); // body
                ds.writeShort(_char.leg); // leg
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void selectCard(Card[] results) {
        try {
            Message mss = messageNotMap(CMD.LAT_HINH);
            DataOutputStream ds = mss.writer();
            for (Card result : results) {
                ds.writeShort(result.getId());
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadClass() {
        try {
            Message ms = messageSubCommand(CMD.ME_LOAD_CLASS);
            DataOutputStream ds = ms.writer();
            ds.writeByte(player.speed);
            ds.writeInt((int)player.maxHP);
            ds.writeInt((int)player.maxMP);
            ds.writeShort(player.potential[0]);
            ds.writeShort(player.potential[1]);
            ds.writeInt(player.potential[2]);
            ds.writeInt(player.potential[3]);
            ds.writeByte(player.classId);
            ds.writeShort((int)player.skillPoint);
            ds.writeShort((int)player.potentialPoint);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendItemMap() {
        try {
            TileMap map = player.zone.tilemap;
            if (map != null) {
                Message ms = new Message(CMD.MAP_ITEM);
                DataOutputStream ds = ms.writer();
                ds.writeByte(map.items.size());
                for (int id : map.items) {
                    byte[] ab = GameData.getInstance().loadFile("Data/Img/Object/" + session.zoomLevel + "/" + id + ".png");
                    ds.writeShort(id);
                    ds.writeInt(ab.length);
                    ds.write(ab);
                }
                ds.writeByte(map.vItemTreeBehind.size());
                for (ItemTree item : map.vItemTreeBehind) {
                    ds.writeByte(item.idTree);
                    ds.writeByte(item.xTree);
                    ds.writeByte(item.yTree);
                }
                ds.writeByte(map.vItemTreeBetwen.size());
                for (ItemTree item : map.vItemTreeBetwen) {
                    ds.writeByte(item.idTree);
                    ds.writeByte(item.xTree);
                    ds.writeByte(item.yTree);
                }
                ds.writeByte(map.vItemTreeFront.size());
                for (ItemTree item : map.vItemTreeFront) {
                    ds.writeByte(item.idTree);
                    ds.writeByte(item.xTree);
                    ds.writeByte(item.yTree);
                }
                ds.flush();
                sendMessage(ms);
                ms.cleanup();
            }
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addYen(int add) {
        try {
            Message ms = new Message(CMD.ME_UP_COIN_LOCK);
            DataOutputStream ds = ms.writer();
            ds.writeInt(add);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addGold(int add) {
        try {
            Message ms = messageSubCommand(CMD.ME_UP_GOLD);
            DataOutputStream ds = ms.writer();
            ds.writeInt(add);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadGold() {
        try {
            Message ms = messageSubCommand(CMD.ME_LOAD_GOLD);
            DataOutputStream ds = ms.writer();
            ds.writeInt(player.user.gold);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void exchangeYenForXu(int xu) {
        try {
            Message ms = new Message(CMD.ME_CHANGE_COIN);
            DataOutputStream ds = ms.writer();
            ds.writeInt(xu);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addXu(int xu) {
        try {
            Message ms = new Message(CMD.ME_UP_COIN_BAG);
            DataOutputStream ds = ms.writer();
            ds.writeInt(xu);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void buy() {
        try {
            Message ms = new Message(CMD.ITEM_BUY);
            DataOutputStream ds = ms.writer();
            ds.writeInt(player.getCoinInt());
            ds.writeInt(player.getYenInt());
            ds.writeInt(player.user.gold);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void convertUpgrade(Item... item) {
        try {
            Message ms = messageNotMap(CMD.CONVERT_UPGRADE);
            DataOutputStream ds = ms.writer();
            ds.writeByte(item[0].index);
            ds.writeByte(item[0].upgrade);
            ds.writeByte(item[1].index);
            ds.writeByte(item[1].upgrade);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addEffect(Effect effect) {
        try {
            Message ms = messageSubCommand(CMD.ME_ADD_EFFECT);
            DataOutputStream ds = ms.writer();
            ds.writeByte(effect.template.id);// templateId
            ds.writeInt(effect.getTimeStart());// timeStart
            ds.writeInt(effect.getTimeLength());// tiemLenght
            ds.writeShort(effect.param);// param
            if (effect.template.type == 14 || effect.template.type == 2 || effect.template.type == 3) {
                ds.writeShort(player.x);
                ds.writeShort(player.y);
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void turnOnAuto() {
        try {
            int range = player.getRange();
            if (range <= 0) {
                range = 1000;
            }
            Message mss = new Message(CMD.AUTO_ATTACK_MOVE);
            DataOutputStream ds = mss.writer();
            ds.writeByte(-1);
            ds.writeInt(range);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            Log.error("turnOnAuto err: " + ex.getMessage(), ex);
        }
    }

    public void turnOffAuto() {
        try {
            Message mss = new Message(CMD.AUTO_ATTACK_MOVE);
            DataOutputStream ds = mss.writer();
            ds.writeByte(1);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            Log.error("turnOffAuto err: " + ex.getMessage(), ex);
        }
    }

    public void removeEffect(Effect effect) {
        try {
            Message ms = messageSubCommand(CMD.ME_REMOVE_EFFECT);
            DataOutputStream ds = ms.writer();
            ds.writeByte(effect.template.id);// templateId
            if (effect.template.type == 0 || effect.template.type == 12) {
                ds.writeInt((int)player.hp);
                ds.writeInt((int)player.mp);
            } else if (effect.template.type == 4 || effect.template.type == 13 || effect.template.type == 17) {
                ds.writeInt((int)player.hp);
            } else if (effect.template.type == 23) {
                ds.writeInt((int)player.hp);
                ds.writeInt((int)player.maxHP);
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void showWait(String title) {
        try {
            Message ms = messageSubCommand(CMD.SHOW_WAIT);
            DataOutputStream ds = ms.writer();
            ds.writeUTF(title);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void editEffect(Effect effect) {
        try {
            Message ms = messageSubCommand(CMD.ME_EDIT_EFFECT);
            DataOutputStream ds = ms.writer();
            ds.writeByte(effect.template.id);// templateId
            ds.writeInt(effect.getTimeStart());
            ds.writeInt(effect.getTimeLength());
            ds.writeShort(effect.param);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateHp() {
        try {
            Message ms = messageSubCommand(CMD.ME_LOAD_HP);
            DataOutputStream ds = ms.writer();
            ds.writeInt((int)player.hp);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateMp() {
        try {
            Message ms = messageSubCommand(CMD.ME_LOAD_MP);
            DataOutputStream ds = ms.writer();
            ds.writeInt((int)player.mp);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendTaskOrder(TaskOrder task) {
        try {
            Message ms = new Message(CMD.GET_TASK_ORDER);
            DataOutputStream ds = ms.writer();
            ds.writeByte(task.taskId);
            ds.writeInt(task.count);
            ds.writeInt(task.maxCount);
            ds.writeUTF(task.name);
            ds.writeUTF(task.description);
            ds.writeByte(task.killId);
            ds.writeByte(task.mapId);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateTaskOrder(TaskOrder task) {
        try {
            Message ms = new Message(CMD.GET_TASK_UPDATE);
            DataOutputStream ds = ms.writer();
            ds.writeByte(task.taskId);
            ds.writeInt(task.count);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearTaskOrder(TaskOrder task) {
        try {
            Message ms = new Message(CMD.CLEAR_TASK_ORDER);
            DataOutputStream ds = ms.writer();
            ds.writeByte(task.taskId);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playerLoadAll(Char pl) {
        try {
            if (pl.isCleaned) {
                return;
            }
            Message ms = messageSubCommand(CMD.PLAYER_LOAD_ALL);
            DataOutputStream ds = ms.writer();
            ds.writeInt(pl.id);
            charInfo(ms, pl);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void callEffectBall() {
        Message ms = messageSubCommand(CMD.CALL_EFFECT_BALL);
        sendMessage(ms);
        ms.cleanup();
    }

    public void sendSkillShortcut(String key, byte[] data, byte type) {
        try {
            Message ms = messageSubCommand(CMD.LOAD_RMS);
            DataOutputStream ds = ms.writer();
            ds.writeUTF(key);
            ds.writeInt(data.length);
            ds.write(data);
            ds.writeByte(type);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void boxCoinIn(int xu) {
        try {
            Message ms = messageSubCommand(CMD.BOX_COIN_IN);
            DataOutputStream ds = ms.writer();
            ds.writeInt(xu);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void boxCoinOut(int xu) {
        try {
            Message ms = messageSubCommand(CMD.BOX_COIN_OUT);
            DataOutputStream ds = ms.writer();
            ds.writeInt(xu);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void useBookSkill(byte itemIndex, short skillId) {
        try {
            Message ms = messageSubCommand(CMD.USE_BOOK_SKILL);
            DataOutputStream ds = ms.writer();
            ds.writeByte(itemIndex);
            ds.writeShort(skillId);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendDataBox() {
        try {
            Message ms = new Message(CMD.OPEN_UI_BOX);
            DataOutputStream ds = ms.writer();
            ds.writeInt(player.getCoinInBoxInt());
            ds.writeByte(player.numberCellBox);
            for (Item item : player.box) {
                if (item != null) {
                    ds.writeShort(item.id);
                    ds.writeBoolean(item.isLock);
                    if (item.template.isTypeBody() || item.template.isTypeNgocKham()) {
                        ds.writeByte(item.upgrade);
                    }
                    ds.writeBoolean(item.hasExpire());
                    ds.writeShort(item.getQuantityDisplay());
                } else {
                    ds.writeShort(-1);
                }
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openUIBox() {
        try {
            openUI((byte) 4);
            sendDataBox();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openUICollectionBox() {
        try {
            openUI((byte) 4, "Bộ sưu tập", "Sử dụng");
            Message ms = new Message(CMD.OPEN_UI_BOX);
            DataOutputStream ds = ms.writer();
            ds.writeInt(player.getCoinInBoxInt());
            List<Item> collectionBox = player.getCollectionBox();
            ds.writeByte(collectionBox.size());
            for (Item item : collectionBox) {
                if (item != null) {
                    ds.writeShort(item.id);
                    ds.writeBoolean(item.isLock);
                    if (item.template.isTypeBody() || item.template.isTypeNgocKham()) {
                        ds.writeByte(item.upgrade);
                    }
                    ds.writeBoolean(item.hasExpire());
                    ds.writeShort(item.getQuantityDisplay());
                } else {
                    ds.writeShort(-1);
                }
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openUIShopTrungThu(List<Item> items, String title, String caption) {
        try {
            openUI((byte) 4, title, caption);
            Message ms = new Message(CMD.OPEN_UI_BOX);
            DataOutputStream ds = ms.writer();
            ds.writeInt(player.getCoinInBoxInt());
            ds.writeByte(items.size());
            for (Item item : items) {
                if (item != null) {
                    ds.writeShort(item.id);
                    ds.writeBoolean(item.isLock);
                    if (item.template.isTypeBody() || item.template.isTypeNgocKham()) {
                        ds.writeByte(item.upgrade);
                    }
                    ds.writeBoolean(item.hasExpire());
                    ds.writeShort(item.getQuantityDisplay());
                } else {
                    ds.writeShort(-1);
                }
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
public void openThangNguong() { // thăng ấn
        try {
            openUI((byte) 4, "Thăng Ngưỡng", "Thăng Ngưỡng");
            Message ms = new Message(CMD.OPEN_UI_BOX);
            DataOutputStream ds = ms.writer();
            ds.writeInt(player.getCoinInBoxInt());
            Equip[] maskBox = player.equipment; //lay do dang mac
            ds.writeByte(maskBox.length);
            for (Item item : maskBox) {
                if (item != null && item.template.type < 10) {
                    ds.writeShort(item.id);
                    ds.writeBoolean(item.isLock);
                    if (item.template.isTypeBody() || item.template.isTypeNgocKham()) {
                        ds.writeByte(item.upgrade);
                    }
                    ds.writeBoolean(item.hasExpire());
                    ds.writeShort(item.getQuantityDisplay());
                } else {
                    ds.writeShort(-1);
                }
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public void openThangCap() { // thăng ấn
        try {
            openUI((byte) 4, "Thăng Cấp", "Thăng Cấp");
            Message ms = new Message(CMD.OPEN_UI_BOX);
            DataOutputStream ds = ms.writer();
            ds.writeInt(player.getCoinInBoxInt());
            Equip[] maskBox = player.equipment; //lay do dang mac
            ds.writeByte(maskBox.length);
            for (Item item : maskBox) {
                if (item != null && item.template.type < 10) {
                    ds.writeShort(item.id);
                    ds.writeBoolean(item.isLock);
                    if (item.template.isTypeBody() || item.template.isTypeNgocKham()) {
                        ds.writeByte(item.upgrade);
                    }
                    ds.writeBoolean(item.hasExpire());
                    ds.writeShort(item.getQuantityDisplay());
                } else {
                    ds.writeShort(-1);
                }
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openUIMaskBox() {
        try {
            openUI((byte) 4, "Cải trang", "Sử dụng");
            Message ms = new Message(CMD.OPEN_UI_BOX);
            DataOutputStream ds = ms.writer();
            ds.writeInt(player.getCoinInBoxInt());
            List<Item> maskBox = player.getMaskBox();
            ds.writeByte(maskBox.size());
            for (Item item : maskBox) {
                if (item != null) {
                    ds.writeShort(item.id);
                    ds.writeBoolean(item.isLock);
                    if (item.template.isTypeBody() || item.template.isTypeNgocKham()) {
                        ds.writeByte(item.upgrade);
                    }
                    ds.writeBoolean(item.hasExpire());
                    ds.writeShort(item.getQuantityDisplay());
                } else {
                    ds.writeShort(-1);
                }
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void resetPoint() {
        try {
            Message mss = new Message(CMD.RESET_POINT);
            DataOutputStream ds = mss.writer();
            ds.writeShort(player.x);
            ds.writeShort(player.y);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void npcAttackMe(Mob mob, double dameHp, double dameMp) {
        try {
            Message ms = new Message(CMD.NPC_ATTACK_ME);
            DataOutputStream ds = ms.writer();
            ds.writeByte(mob.id);
            ds.writeInt((int)dameHp);
            ds.writeInt((int)dameMp);
            ds.writeShort(-1);
            ds.writeByte(0);
            ds.writeByte(0);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reviewDungeon() {
        try {
            Dungeon dungeon = (Dungeon) player.findWorld(World.DUNGEON);
            Message mss = messageNotMap(CMD.REVIEW_PB);
            DataOutputStream ds = mss.writer();
            if (dungeon != null) {
                int team = dungeon.listCharId.size();
                int reward = player.pointPB / Dungeon.POINT[dungeon.level];
                if (dungeon.timeFinish > 0) {
                    reward += team;
                }
                ds.writeShort(player.pointPB);
                ds.writeShort(dungeon.timeFinish);
                ds.writeByte(team);
                ds.writeShort(reward);
            } else {
                ds.writeShort(0);
                ds.writeShort(0);
                ds.writeByte(0);
                ds.writeShort(0);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendZone() {
        try {
            Zone zone = player.zone;
            TileMap tilemap = zone.tilemap;
            Message ms = new Message(CMD.MAP_INFO);
            DataOutputStream ds = ms.writer();
            ds.writeByte(player.mapId);
            ds.writeByte(tilemap.tileId);
            ds.writeByte(tilemap.bgId);
            ds.writeByte(tilemap.type);
            ds.writeUTF(tilemap.name);
            ds.writeByte(zone.id);
            ds.writeShort(player.x);
            ds.writeShort(player.y);
            int len = tilemap.waypoints.size();
            ds.writeByte(len);// mob
            for (Waypoint w : tilemap.waypoints) {
                ds.writeShort(w.minX);
                ds.writeShort(w.minY);
                ds.writeShort(w.maxX);
                ds.writeShort(w.maxY);
            }
            List<Mob> monsters = zone.getMonsters();
            len = monsters.size();
            ds.writeByte(len);// mob
            for (Mob mob : monsters) {
                int levelBoss = mob.levelBoss;
                double hp = mob.hp;
                double maxHp = mob.maxHP;
                if ((levelBoss == 1 || levelBoss == 2) && !tilemap.isDungeo() && !tilemap.isDungeoClan()) {
                    if (!mob.checkExist(player.id)) {
                        levelBoss = 0;
                        maxHp = mob.template.hp;
                        hp = hp > maxHp ? maxHp : hp;
                    }
                }
                ds.writeBoolean(mob.isDisable);
                ds.writeBoolean(mob.isDontMove);
                ds.writeBoolean(mob.isFire);
                ds.writeBoolean(mob.isIce);
                ds.writeBoolean(mob.isWind);
                if (player.isVersionAbove(211)) {
                    ds.writeShort(mob.template.id);
                } else {
                    ds.writeByte(mob.template.id);
                }
                ds.writeByte(mob.sys);
                ds.writeInt((int)hp);
                ds.writeByte(mob.level);
                ds.writeInt((int)maxHp);
                ds.writeShort(mob.x);
                ds.writeShort(mob.y);
                ds.writeByte(mob.status);
                ds.writeByte(levelBoss);
                ds.writeBoolean(mob.isBoss);
            }
            Figurehead[] buNhins = zone.getBuNhins();
            int num = buNhins.length;
            ds.writeByte(num);
            for (Figurehead buNhin : buNhins) {
                ds.writeUTF(buNhin.name);
                ds.writeShort(buNhin.x);
                ds.writeShort(buNhin.y);
            }
            List<Npc> npcs = zone.getNpcs();
            num = npcs.size();
            ds.writeByte(num);
            for (Npc npc : npcs) {
                byte status = (byte) npc.status;
                if (npc.template.npcTemplateId == NpcName.JAIAN) {
                    Task task = player.taskMain;
                    if (task != null && task.taskId == TaskName.NV_DUA_JAIAN_TRO_VE && task.index == 1) {
                        status = 1;
                    }
                } else if (npc.template.npcTemplateId == NpcName.LONG_DEN_2) {
                    if (Event.isTrungThu() && player.level >= 20 && !player.isLeading()) {
                        status = 1;
                    }
                }
                ds.writeByte(status);
                ds.writeShort(npc.cx);
                ds.writeShort(npc.cy);
                ds.writeByte(npc.template.npcTemplateId);
            }
            List<ItemMap> items = zone.getItemMaps(player.taskMain);
            num = items.size();
            ds.writeByte(num);
            for (ItemMap item : items) {
                ds.writeShort(item.getId());
                ds.writeShort(item.getItemID());
                ds.writeShort(item.getX());
                ds.writeShort(item.getY());
            }
            ds.writeUTF(tilemap.name);
            int size = tilemap.locationStand.size();
            ds.writeByte(size);
            for (int[] stand : tilemap.locationStand) {
                ds.writeByte(stand[0]);
                ds.writeByte(stand[1]);
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void tradeInvite(int id) {
        try {
            Message mss = new Message(CMD.TRADE_INVITE);
            DataOutputStream ds = mss.writer();
            ds.writeInt(id);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void meLive() {
        sendMessage(new Message(CMD.ME_LIVE));
    }

    public void testWarClanInvite(int id) {
        try {
            Message ms = new Message(CMD.TEST_GT_INVITE);
            DataOutputStream ds = ms.writer();
            ds.writeInt(id);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void testDungeonInvite(int id) {
        try {
            Message ms = new Message(CMD.TEST_DUN_INVITE);
            DataOutputStream ds = ms.writer();
            ds.writeInt(id);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



    public void testDunageonList() {
        try {
            Message ms = new Message(CMD.TEST_DUN_LIST);
            DataOutputStream ds = ms.writer();
            ds.writeByte(Arena.arenas.size());
            for (Arena arena : Arena.arenas) {
                ds.writeByte(arena.getId());
                ds.writeUTF(arena.leaderTeamOneName);
                ds.writeUTF(arena.leaderTeamTwoName);
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addMonster(Mob mob) {
        Message mss = new Message(CMD.SERVER_ADD_MOB);
        DataOutputStream ds = mss.writer();
        try {
            ds.writeByte(0);
            ds.writeByte(1);
            ds.writeByte(127);
            ds.writeBoolean(mob.isDisable);
            ds.writeBoolean(mob.isDontMove);
            ds.writeBoolean(mob.isFire);
            ds.writeBoolean(mob.isIce);
            ds.writeBoolean(mob.isWind);
            if (player.isVersionAbove(211)) {
                ds.writeShort(mob.template.id);
            } else {
                ds.writeByte(mob.template.id);
            }
            ds.writeByte(mob.sys);
            ds.writeInt((int)mob.hp);
            ds.writeByte(mob.level);
            ds.writeInt((int)mob.maxHP);
            ds.writeShort(mob.x);
            ds.writeShort(mob.y);
            ds.writeByte(mob.status);
            ds.writeByte(0);
            ds.writeBoolean(mob.isBoss);
            ds.flush();
            sendMessage(mss);
        } catch (IOException e) {
            Log.error("err: " + e.getMessage(), e);
        }
    }

    public void saleItem(int indexUI, int quantity) {
        try {
            Message ms = new Message(CMD.ITEM_SALE);
            DataOutputStream ds = ms.writer();
            ds.writeByte(indexUI);
            ds.writeInt(player.getYenInt());
            ds.writeShort(quantity);
            ds.flush();
            sendMessage(ms);
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void requestPlayers(ArrayList<Char> chars) {
        try {
            Message mss = new Message(CMD.REQUEST_PLAYERS);
            DataOutputStream ds = mss.writer();
            ds.writeByte(chars.size());
            for (Char _char : chars) {
                ds.writeInt(_char.id);
                ds.writeShort(_char.x);
                ds.writeShort(_char.y);
                ds.writeInt((int)_char.hp);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addExpDown(long exp) {
        try {
            Message ms = new Message(CMD.PLAYER_UP_EXPDOWN);
            DataOutputStream ds = ms.writer();
            ds.writeLong(exp);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addExp(long exp) {
        try {
            Message ms = new Message(CMD.PLAYER_UP_EXP);
            DataOutputStream ds = ms.writer();
            ds.writeLong(exp);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void requestViewDetails(int id, Item item) {
        try {
            Message mss = new Message(CMD.VIEW_ITEM_AUCTION);
            DataOutputStream ds = mss.writer();
            ds.writeInt(id);
            ds.writeInt(item.yen);
            if (item.template.isTypeBody()) {
                ds.writeByte(item.upgrade);
                ds.writeByte(item.sys);
                ArrayList<ItemOption> options = item.getDisplayOptions();
                for (ItemOption option : options) {
                    ds.writeByte(option.optionTemplate.id);
                    ds.writeInt(option.param);
                }
            }
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendItemToAuction(int index) {
        try {
            Message mss = new Message(CMD.SEND_ITEM_TO_AUCTION);
            DataOutputStream ds = mss.writer();
            ds.writeByte(index);
            ds.writeInt(player.getCoinInt());
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addFriend(String name, int type) {
        try {
            Message m = new Message(CMD.FRIEND_ADD);
            DataOutputStream ds = m.writer();
            ds.writeUTF(name);
            ds.writeByte(type);
            ds.flush();
            sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void npcUpdate(int index, int status) {
        try {
            Message ms = messageSubCommand(CMD.NPC_PLAYER_UPDATE);
            DataOutputStream ds = ms.writer();
            ds.writeByte(index);
            ds.writeByte(status);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Log.error("npc update err: " + ex.getMessage(), ex);
        }
    }

    public void pickItem(ItemMap item) {
        try {
            Message ms = new Message(CMD.ITEMMAP_MYPICK);
            DataOutputStream ds = ms.writer();
            ds.writeShort(item.getId());
            ds.writeShort(item.getItem().getQuantityDisplay());
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Zone.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void playerPickItem(Char _char, ItemMap item) {
        try {
            Message ms = new Message(CMD.ITEMMAP_PLAYERPICK);
            DataOutputStream ds = ms.writer();
            ds.writeShort(item.getId());
            ds.writeInt(_char.id);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Zone.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void throwItem(byte index, ItemMap item) {
        try {
            Message ms = new Message(CMD.ME_THROW);
            DataOutputStream ds = ms.writer();
            ds.writeByte(index);
            ds.writeShort(item.getId());
            ds.writeShort(item.getX());
            ds.writeShort(item.getY());
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void playerThrowItem(Char _char, byte index, ItemMap item) {
        try {
            Message mss = new Message(CMD.PLAYER_THROW);
            DataOutputStream dss = mss.writer();
            dss.writeInt(_char.id);
            dss.writeShort(item.getId());
            dss.writeShort(item.getItemID());
            dss.writeShort(item.getX());
            dss.writeShort(item.getY());
            dss.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void warInfo() {
        try {
            Message m = messageNotMap(CMD.CHIENTRUONG_INFO);
            DataOutputStream ds = m.writer();
            ds.writeShort(player.warPoint);
            ds.flush();
            sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void inviteFriend(String name) {
        try {
            Message m = new Message(CMD.FRIEND_INVITE);
            DataOutputStream ds = m.writer();
            ds.writeUTF(name);
            ds.flush();
            sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadPet(Char _char) {
        try {
            Message ms = messageSubCommand(CMD.PLAYER_LOAD_THU_NUOI);
            DataOutputStream ds = ms.writer();
            ds.writeInt(_char.id);
            int templateID = 0;
            int boss = 0;
            if (_char.mobMe != null) {
                templateID = _char.mobMe.template.id;
                boss = (_char.mobMe.isBoss ? 1 : 0);
            }
            if (player.isVersionAbove(200)) {
                ds.writeShort(templateID);
            } else {
                ds.writeByte(templateID);
            }
            ds.writeByte(boss);
            ds.flush();
            sendMessage(ms);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void itemMountToBag(int index1, int index2) {
        try {
            Message mss = new Message(CMD.ITEM_MON_TO_BAG);
            DataOutputStream ds = mss.writer();
            ds.writeByte(player.speed);
            ds.writeInt((int)player.maxHP);
            ds.writeInt((int)player.maxMP);
            ds.writeShort((short)player.eff5buffhp);
            ds.writeShort((short)player.eff5buffmp);
            ds.writeByte(index1);
            ds.writeByte(index2);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createClan() {
        try {
            if (player.clan == null) {
                return;
            }
            Message ms = messageNotMap(CMD.CREATE_CLAN);
            DataOutputStream ds = ms.writer();
            ds.writeUTF(player.clan.getName());
            ds.writeInt(player.user.gold);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void changeClanAlert(String alert) {
        try {
            Message ms = messageNotMap(CMD.CLAN_CHANGE_ALERT);
            DataOutputStream ds = ms.writer();
            ds.writeUTF(alert);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openUIZone() {
        try {
            TileMap tile = player.zone.tilemap;
            if (tile.isNotChangeZone()) {
                serverDialog("Không thể chuyển khu");
                return;
            }
            boolean isNear = false;

            for (Npc npc : player.zone.getNpcs()) {
                if (npc != null && npc.template.npcTemplateId == 13 && Math.abs(player.x - npc.cx) < 50
                        && Math.abs(player.y - npc.cy) < 50) {
                    isNear = true;
                }
            }
            if (!isNear && player.getQuantityItemById(35) == 0 && player.getQuantityItemById(37) == 0) {
                player.x = 120;
                player.y = 0;
                player.startDie();
            } else {
                Message ms = new Message(CMD.OPEN_UI_ZONE);
                DataOutputStream ds = ms.writer();
                Map m = player.zone.map;
                if (m == null) {
                    ds.flush();
                    ms.cleanup();
                    return;
                }
                List<Zone> zones = m.getZones();
                ds.writeByte(zones.size());// so khu
                for (Zone z : zones) {
                    ds.writeByte(z.getNumberChar());// so nguoi
                    ds.writeByte(z.getNumberGroup());// so nhom
                }
                ds.flush();
                sendMessage(ms);
                ms.cleanup();
            }
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updatePointPB() {
        try {
            Message ms = messageNotMap(CMD.POINT_PB);
            DataOutputStream ds = ms.writer();
            ds.writeShort(player.pointPB);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception e) {

        }
    }

    public void sendTaskInfo() {
        try {
            Message ms = new Message(CMD.TASK_GET);
            DataOutputStream ds = ms.writer();
            Task task = player.taskMain;
            ds.writeShort(task.taskId);
            ds.writeByte(task.index);
            TaskTemplate template = task.template;
            if (template != null) {
                ds.writeUTF(template.getName());
                ds.writeUTF(template.getDetail());
                String[] subnames = template.getSubNames();
                int num = subnames.length;
                ds.writeByte(num);
                for (int i = 0; i < num; i++) {
                    ds.writeUTF(subnames[i]);
                }
                ds.writeShort(task.count);
                short[] counts = template.getCounts();
                for (int i = 0; i < num; i++) {
                    ds.writeShort(counts[i]);
                }
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadAll() {
        try {
            Message ms = messageSubCommand(CMD.ME_LOAD_ALL);
            DataOutputStream ds = ms.writer();
            ds.writeInt(player.id);
            if (player.clan == null) {
                ds.writeUTF("");
            } else {
                String name = player.clan.getName();
                int typeClan = player.clan.getMemberByName(player.name).getType();
                ds.writeUTF(name);
                ds.writeByte(typeClan);
            }
            ds.writeByte(player.taskId);// taskId
            ds.writeByte(player.gender);
            ds.writeShort(player.head);
            ds.writeByte(player.speed);
            ds.writeUTF(player.name);
            ds.writeByte(player.hieuChien);// pk
            ds.writeByte(player.typePk);// type pk
            ds.writeInt((int)player.maxHP); // maxHP
            ds.writeInt((int)player.hp); // hp
            ds.writeInt((int)player.maxMP); // maxMP
            ds.writeInt((int)player.mp); // mp
            ds.writeLong(player.exp); // exp
            ds.writeLong(player.expDown); // exp dowm
            ds.writeShort((short)player.eff5buffhp); // eff5buff
            ds.writeShort((short)player.eff5buffmp); // eff5buff
            ds.writeByte(player.classId);// nclass 1 kiem, 2 tieu, 3 kunai, 4 cung, 5 dao, 6 quat
            ds.writeShort((short)player.potentialPoint); // point
            ds.writeShort(player.potential[0]);
            ds.writeShort(player.potential[1]);
            ds.writeInt(player.potential[2]);
            ds.writeInt(player.potential[3]);
            ds.writeShort((short)player.skillPoint);
            ds.writeByte(player.vSkill.size());// skill
            for (Skill my : player.vSkill) {
                ds.writeShort(my.id);// skill id
            }
            ds.writeInt(player.getCoinInt());// xu
            ds.writeInt(player.getYenInt());// yen
            ds.writeInt(player.user.gold);// luong
            ds.writeByte(player.numberCellBag);// bag
            for (int i = 0; i < player.numberCellBag; i++) {
                Item item = player.bag[i];
                if (item != null) {
                    ds.writeShort(item.id);
                    ds.writeBoolean(item.isLock);
                    if (item.template.isTypeBody() || item.template.isTypeNgocKham() || item.template.isTypeMount()) {
                        ds.writeByte(item.upgrade);
                    }
                    ds.writeBoolean(item.hasExpire());
                    ds.writeShort(item.getQuantityDisplay());
                } else {
                    ds.writeShort(-1);
                }
            }

            // trang bi
            for (int i = 0; i < 16; i++) {
                if (player.equipment[i] != null) {
                    ds.writeShort(player.equipment[i].id);
                    ds.writeByte(player.equipment[i].upgrade);
                    ds.writeByte(player.equipment[i].sys);
                } else {
                    ds.writeShort(-1);
                }
            }
            ds.writeBoolean(player.isHuman);
            ds.writeBoolean(player.isNhanBan);
            ds.writeShort(player.head);
            ds.writeShort(player.weapon);
            ds.writeShort(player.body);
            ds.writeShort(player.leg);
            short[] thoiTrang = player.getFashion();
            for (int i = 0; i < 10; i++) {
                ds.writeShort(thoiTrang[i]);
            }
            for (int i = 0; i < 16; i++) {
                if (player.fashion[i] != null) {
                    ds.writeShort(player.fashion[i].id);
                    ds.writeByte(player.fashion[i].upgrade);
                    ds.writeByte(player.fashion[i].sys);
                } else {
                    ds.writeShort(-1);
                }
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadMobMe() {
        try {
            // Log.debug("load mob me");
            Mob mob = player.mobMe;
            int templateId = 0;
            int boss = 0;
            if (mob != null) {
                templateId = mob.template.id;
                boss = mob.isBoss ? 1 : 0;
            }
            Message m = messageSubCommand(CMD.ME_LOAD_THU_NUOI);
            if (player.isVersionAbove(200)) {
                m.writer().writeShort(templateId);
            } else {
                m.writer().writeByte(templateId);
            }
            m.writer().writeByte(boss);
            m.writer().flush();
            sendMessage(m);
            m.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateInfoChar(Char pl) {
        try {
            if (pl.isCleaned) {
                return;
            }
            Message ms = new Message(CMD.UPDATE_INFO_CHAR);
            DataOutputStream ds = ms.writer();
            ds.writeInt(pl.id);
            charInfo(ms, pl);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateInfoMe() {
        try {
            Message ms = messageSubCommand(CMD.UPDATE_INFO_ME);
            DataOutputStream ds = ms.writer();
            ds.writeInt(player.id);
            if (player.clan == null) {
                ds.writeUTF("");
            } else {
                String name = player.clan.getName();
                int typeClan = player.clan.getMemberByName(player.name).getType();
                ds.writeUTF(name);
                ds.writeByte(typeClan);
            }
            ds.writeByte(player.taskId);// taskId
            ds.writeByte(player.gender);
            ds.writeShort(player.head);
            ds.writeByte(player.speed);
            ds.writeUTF(player.name);
            ds.writeByte(player.hieuChien);// pk
            ds.writeByte(player.typePk);// type pk
            ds.writeInt((int)player.maxHP); // maxHP
            ds.writeInt((int)player.hp); // hp
            ds.writeInt((int)player.maxMP); // maxMP
            ds.writeInt((int)player.mp); // mp
            ds.writeLong(player.exp); // exp
            ds.writeLong(player.expDown); // exp dowm
            ds.writeShort((short)player.eff5buffhp); // eff5buff
            ds.writeShort((short)player.eff5buffmp); // eff5buff
            ds.writeByte(player.classId);// nclass 1 kiem, 2 tieu, 3 kunai, 4 cung, 5 dao, 6 quat
            ds.writeShort((int)player.potentialPoint); // point
            ds.writeShort(player.potential[0]);
            ds.writeShort(player.potential[1]);
            ds.writeInt(player.potential[2]);
            ds.writeInt(player.potential[3]);
            ds.writeShort((short)player.skillPoint);
            ds.writeByte(player.vSkill.size());// skill
            for (Skill my : player.vSkill) {
                ds.writeShort(my.id);// skill id
            }
            ds.writeInt(player.getCoinInt());// yen
            ds.writeInt(player.getYenInt());// xu
            ds.writeInt(player.user.gold);// luong
            ds.writeByte(player.numberCellBag);// bag
            for (int i = 0; i < player.numberCellBag; i++) {
                Item item = player.bag[i];
                if (item != null) {
                    ds.writeShort(item.id);
                    ds.writeBoolean(item.isLock);
                    if (item.template.isTypeBody() || item.template.isTypeNgocKham() || item.template.isTypeMount()) {
                        ds.writeByte(item.upgrade);
                    }
                    ds.writeBoolean(item.hasExpire());
                    ds.writeShort(item.getQuantityDisplay());
                } else {
                    ds.writeShort(-1);
                }
            }

            // trang bi
            for (int i = 0; i < 16; i++) {
                if (player.equipment[i] != null) {
                    ds.writeShort(player.equipment[i].id);
                    ds.writeByte(player.equipment[i].upgrade);
                    ds.writeByte(player.equipment[i].sys);
                } else {
                    ds.writeShort(-1);
                }
            }
            ds.writeBoolean(player.isHuman);
            ds.writeBoolean(player.isNhanBan);
            ds.writeShort(player.head);
            ds.writeShort(player.weapon);
            ds.writeShort(player.body);
            ds.writeShort(player.leg);
            short[] thoiTrang = player.getFashion();
            for (int i = 0; i < 10; i++) {
                ds.writeShort(thoiTrang[i]);
            }
            for (int i = 0; i < 16; i++) {
                if (player.fashion[i] != null) {
                    ds.writeShort(player.fashion[i].id);
                    ds.writeByte(player.fashion[i].upgrade);
                    ds.writeByte(player.fashion[i].sys);
                } else {
                    ds.writeShort(-1);
                }
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openUIConfirmID() {
        try {
            ConfirmPopup confirmPopup = player.getConfirmPopup();
            if (confirmPopup == null) {
                return;
            }
            Message ms = new Message(CMD.OPEN_UI_CONFIRM_ID);
            DataOutputStream ds = ms.writer();
            ds.writeByte(confirmPopup.getId());
            ds.writeUTF(confirmPopup.getTitle());
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadInfo() {
        try {
            Message ms = messageSubCommand(CMD.ME_LOAD_INFO);
            DataOutputStream ds = ms.writer();
            ds.writeInt(player.getCoinInt());
            ds.writeInt(player.getYenInt());
            ds.writeInt(player.user.gold);
            ds.writeInt((int)player.hp);
            ds.writeInt((int)player.mp);
            ds.writeByte(player.captcha);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Log.error("load info err: " + ex.getMessage(), ex);
        }
    }

    public void sendImgEffect(Message msg) {
        try {
            short id = msg.reader().readShort();
            byte[] ab = GameData.getInstance().loadFile("Data/Img/Effect/" + session.zoomLevel + "/" + id + ".png");
            Message ms = new Message(CMD.GET_EFFECT);
            DataOutputStream ds = ms.writer();
            ds.writeByte(1);
            ds.writeByte(id);
            ds.writeInt(ab.length);
            ds.write(ab);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendEffectData(Message msg) {
        try {
            short id = msg.reader().readShort();
            EffectData eff = EffectDataManager.getInstance().find(id);
            if (eff != null) {
                byte[] data = eff.getData();
                Message ms = new Message(CMD.GET_EFFECT);
                DataOutputStream ds = ms.writer();
                ds.writeByte(2);
                ds.writeByte(id);
                ds.writeShort(data.length);
                ds.write(data);
                ds.flush();
                sendMessage(ms);
                ms.cleanup();
            }
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendImgEffectAuto(Message msg) {
        try {
            int id = msg.reader().readUnsignedByte();
            byte[] ab = GameData.getInstance().loadFile("Data/Img/EffectAuto/" + session.zoomLevel + "/" + id + ".png");
            Message ms = new Message(CMD.SERVER_ADD_MOB);
            DataOutputStream ds = ms.writer();
            ds.writeByte(2);
            ds.writeByte(id);
            ds.writeInt(ab.length);
            ds.write(ab);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendEffectAutoData(Message msg) {
        try {
            int id = msg.reader().readUnsignedByte();
            EffectAutoData eff = EffectAutoDataManager.getInstance().find(id);
            if (eff != null) {
                byte[] data = eff.getData();
                Message ms = new Message(CMD.SERVER_ADD_MOB);
                DataOutputStream ds = ms.writer();
                ds.writeByte(3);
                ds.writeByte(id);
                ds.writeShort(data.length);
                ds.write(data);
                ds.flush();
                sendMessage(ms);
                ms.cleanup();
            }
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void playerLoadInfo(Char pl) {
        try {
            Message ms = messageSubCommand(CMD.PLAYER_LOAD_INFO);
            DataOutputStream ds = ms.writer();
            ds.writeInt(pl.id);
            ds.writeInt((int)pl.hp);
            ds.writeInt((int)pl.maxHP);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openUI(byte typeUI, String... array) {
        try {
            Message ms = new Message(CMD.OPEN_UI);
            DataOutputStream ds = ms.writer();
            ds.writeByte(typeUI);
            if (array.length == 2) {
                ds.writeUTF(array[0]);
                ds.writeUTF(array[1]);
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openUI(byte typeUI, String title, String action) {
        try {
            Message ms = new Message(CMD.OPEN_UI);
            DataOutputStream ds = ms.writer();
            ds.writeByte(typeUI);
            ds.writeUTF(title);
            ds.writeUTF(action);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void showInputDialog() {
        try {
            InputDialog input = player.getInput();
            if (input != null) {
                Message ms = new Message(CMD.OPEN_TEXT_BOX_ID);
                DataOutputStream ds = ms.writer();
                ds.writeUTF(input.getTitle());
                ds.writeShort(input.getId());
                ds.flush();
                sendMessage(ms);
                ms.cleanup();
            }
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void endDlg(boolean isResetButton) {
        try {
            Message ms = new Message(CMD.GIAODO);
            DataOutputStream ds = ms.writer();
            ds.writeByte(isResetButton ? 0 : 1);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void requestMapTemplate(Message ms) {
        try {
            int templateId = ms.reader().readUnsignedByte();
            TileMap tilemap = player.zone.tilemap;
            ms = messageNotMap(CMD.REQUEST_MAPTEMPLATE);
            DataOutputStream ds = ms.writer();
            ds.writeByte(tilemap.tmw);
            ds.writeByte(tilemap.tmh);
            int size = tilemap.tmw * tilemap.tmh;
            for (int i = 0; i < size; i++) {
                ds.writeByte(tilemap.maps[i]);
            }
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void clanInvite(Char c) {
        try {
            Message ms = messageSubCommand(CMD.CLAN_INVITE);
            DataOutputStream ds = ms.writer();
            ds.writeInt(c.id);
            ds.writeUTF(c.clan.getName());
            ds.flush();
            sendMessage(ms);
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void chat(String name, String text) {
        try {
            Message mss = new Message(CMD.CHAT_PRIVATE);
            DataOutputStream ds = mss.writer();
            ds.writeUTF(name);
            ds.writeUTF(text);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            Log.error("chatPrivate err: " + ex.getMessage(), ex);
        }
    }

    public void chatGlobal(String name, String text) {
        try {
            Message mss = new Message(CMD.CHAT_SERVER);
            DataOutputStream ds = mss.writer();
            ds.writeUTF(name);
            ds.writeUTF(text);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            Log.error("chatPrivate err: " + ex.getMessage(), ex);
        }
    }

    public void requestMobTemplate(Message ms) {
        try {
            int mobTemplateID = 0;
            if (player.isVersionAbove(200)) {
                mobTemplateID = ms.reader().readUnsignedShort();
            } else {
                mobTemplateID = ms.reader().readUnsignedByte();
            }
            int zoomLevel = session.zoomLevel;
            Message mss = messageNotMap(CMD.REQUEST_NPCTEMPLATE);
            DataOutputStream ds = mss.writer();
            MobTemplate mob = MobManager.getInstance().find(mobTemplateID);
            ds.writeShort(mobTemplateID);
            ds.writeByte(mob.typeFly);
            ds.writeByte(mob.numberImage);
            if (mobTemplateID == MobName.BACH_LONG_TRU || mobTemplateID == MobName.HAC_LONG_TRU) {
                byte[] ab = GameData.getInstance().loadFile("Data/Img/Mob/" + zoomLevel + "/" + mobTemplateID + ".png");
                ds.writeInt(ab.length);
                ds.write(ab);
            } else {
                for (int i = 0; i < mob.numberImage; i++) {
                    byte[] ab = GameData.getInstance().loadFile("Data/Img/Mob/" + zoomLevel + "/" + mobTemplateID + "_" + i + ".png");
                    ds.writeInt(ab.length);
                    ds.write(ab);
                }
            }
            if (mob.isBoss()) {
                ds.writeBoolean(true);
                ds.writeByte(mob.frameBossMove.length);
                for (byte move : mob.frameBossMove) {
                    ds.writeByte(move);
                }
                ds.writeByte(mob.frameBossAttack.length);
                for (byte[] attack : mob.frameBossAttack) {
                    ds.writeByte(attack.length);
                    for (byte att : attack) {
                        ds.writeByte(att);
                    }
                }
            } else {
                ds.writeBoolean(false);
            }
            if (mob.isBoss()) {
                ds.writeInt(1);
                if (mob.id < 236) {
                    GameData.getInstance().writeDataMobOld(ds, mob);
                } else {
                    GameData.getInstance().writeDataMobNew(ds, mob);
                }
            } else {
                ds.writeInt(0);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (Exception ex) {
            // Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
            Log.info(ex);
        }
    }

    public void requestClanInfo() {
        try {
            Clan clan = player.clan;
            if (clan == null) {
                return;
            }
            Message ms = messageNotMap(CMD.REQUEST_CLAN_INFO);
            DataOutputStream ds = ms.writer();
            ds.writeUTF(clan.getName());
            ds.writeUTF(clan.getMainName());
            ds.writeUTF(clan.getAssistName());
            ds.writeShort(clan.getNumberMember());
            ds.writeByte(clan.getOpenDun());
            ds.writeByte(clan.getLevel());
            ds.writeInt(clan.getExp());
            ds.writeInt(clan.getExpNext());
            ds.writeInt(clan.getCoin());
            ds.writeInt(clan.getFreeCoin());
            ds.writeInt(clan.getCoinUp());
            ds.writeUTF(clan.getRegDate().toString());
            ds.writeUTF(clan.getAlert());
            ds.writeInt(clan.getUseCard());
            ds.writeByte(clan.getItemLevel());
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void requestClanMember() {
        try {
            Clan clan = player.clan;
            if (clan == null) {
                return;
            }
            List<Member> members = clan.memberDAO.getAll();
            Message mss = messageNotMap(CMD.REQUEST_CLAN_MEMBER);
            DataOutputStream ds = mss.writer();
            synchronized (members) {
                ds.writeShort(members.size());
                for (Member mem : members) {
                    ds.writeByte(mem.getClassId());
                    ds.writeByte(mem.getLevel());
                    ds.writeByte(mem.getType());
                    ds.writeUTF(mem.getName());
                    ds.writeInt(mem.getPointClan());
                    ds.writeBoolean(mem.isOnline());
                }
                for (Member mem : members) {
                    ds.writeInt(mem.getPointClanWeek());
                }
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void requestClanItem() {
        try {
            Clan clan = player.clan;
            if (clan == null) {
                return;
            }
            Item[] items = clan.getItems();
            Message mss = messageNotMap(CMD.REQUEST_CLAN_ITEM);
            DataOutputStream ds = mss.writer();
            ds.writeByte(items.length);
            for (Item item : items) {
                ds.writeShort(item.getQuantityDisplay());
                ds.writeShort(item.id);

            }
            ds.writeByte(clan.thanThus.size()); // thần thú
            for (ThanThu thanThu : clan.thanThus) {
                if (thanThu.getEggHatchingTime() == -1) {
                    ds.writeUTF(String.format("%s cấp %d", thanThu.getName(), thanThu.getLevel()));
                } else {
                    ds.writeUTF(thanThu.getName());
                }
                ds.writeShort(thanThu.getIcon());
                ds.writeShort(thanThu.getId());
                ds.writeInt(thanThu.getEggHatchingTime());
                ArrayList<ItemOption> options = thanThu.getOptions();
                ds.writeByte(options.size());
                if (thanThu.getEggHatchingTime() >= 0) {
                    ds.writeUTF("Thời gian nở: ");
                } else {
                    for (ItemOption option : options) {
                        if (option.optionTemplate.id == ThanThu.ST_QUAI_ID) {
                            ds.writeUTF("Sát thương quái: " + option.param);
                        } else if (option.optionTemplate.id == ThanThu.ST_NGUOI_ID) {
                            ds.writeUTF("Sát thương người: " + option.param);
                        } else {
                            ds.writeUTF(option.getOptionString());
                        }
                    }
                    ds.writeInt(thanThu.getCurrentExp());
                    ds.writeInt(thanThu.getMaxExp());
                }
                ds.writeByte(thanThu.getStars());
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeLog() {
        try {
            Clan clan = player.clan;
            if (clan == null) {
                return;
            }
            Message mss = messageNotMap(CMD.REQUEST_CLAN_LOG);
            DataOutputStream ds = mss.writer();
            ds.writeUTF(clan.getLog());
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openUITrade(String name) {
        try {
            Message ms = new Message(CMD.OPEN_UI_TRADE);
            DataOutputStream ds = ms.writer();
            ds.writeUTF(name);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException e) {
            Log.error("open ui trade err: " + e.getMessage(), e);
        }
    }

    public void tradeItemLock(Trader trader) {
        try {
            Message ms = new Message(CMD.TRADE_LOCK_ITEM);
            DataOutputStream ds = ms.writer();
            ds.writeInt(trader.coinTradeOrder);
            ds.writeByte(trader.itemTradeOrder.size());
            for (Item item : trader.itemTradeOrder) {
                ds.writeShort(item.id);
                if (item.template.isTypeBody() || item.template.isTypeNgocKham()) {
                    ds.writeByte(item.upgrade);
                }
                ds.writeBoolean(item.hasExpire());
                ds.writeShort(item.getQuantityDisplay());
            }
            ds.flush();
            sendMessage(ms);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void viewItemInfo(Trader trader, byte type, byte index) {
        try {
            Item item = trader.itemTradeOrder.get(index);
            Message mss = new Message(CMD.REQUEST_ITEM_INFO);
            DataOutputStream ds = mss.writer();
            ds.writeByte(type);
            ds.writeByte(index);
            ds.writeLong(item.expire);
            ds.writeInt(item.yen);
            if (item.template.isTypeBody() || item.template.isTypeMount() || item.template.isTypeNgocKham()) {
                ds.writeByte(item.sys);
                ArrayList<ItemOption> options = item.getDisplayOptions();
                for (ItemOption ability : options) {
                    ds.writeByte(ability.optionTemplate.id);
                    ds.writeInt(ability.param);
                }
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tradeOk() {
        try {
            Message ms = new Message(CMD.TRADE_OK);
            DataOutputStream ds = ms.writer();
            ds.writeInt(player.getCoinInt());
            ds.flush();
            sendMessage(ms);
        } catch (IOException ex) {
            Log.error("trade ok err: " + ex.getMessage(), ex);
        }
    }

    public void tradeCancel() {
        sendMessage(new Message(CMD.TRADE_CANCEL));
    }

    public void taskFinish() {
        sendMessage(new Message(CMD.TASK_FINISH));
    }

    public void pleaseInputParty(String name) {
        try {
            Message ms = new Message(CMD.PLEASE_INPUT_PARTY);
            DataOutputStream ds = ms.writer();
            ds.writeUTF(name);
            ds.flush();
            sendMessage(ms);
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void npcChat(int npcId, String text) {
        try {
            Message ms = new Message(CMD.OPEN_UI_SAY);
            ms.writer().writeShort(npcId);
            ms.writer().writeUTF(text);
            ms.writer().flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Char.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void upPearl(boolean isCoin, byte type, Item item) {
        try {
            Message mss = new Message(isCoin ? CMD.UPPEARL : CMD.UPPEARL_LOCK);
            DataOutputStream ds = mss.writer();
            ds.writeByte(type);
            ds.writeByte(item.index);
            ds.writeShort(item.id);
            ds.writeBoolean(item.isLock);
            ds.writeBoolean(item.hasExpire());
            if (!isCoin) {
                ds.writeInt(player.getYenInt());
            }
            ds.writeInt(player.getCoinInt());
            ds.flush();
            sendMessage(mss);
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addCoinClan(int coin) {
        try {
            Message mss = messageNotMap(CMD.INPUT_COIN_CLAN);
            DataOutputStream ds = mss.writer();
            ds.writeInt(coin);
            ds.flush();
            sendMessage(mss);
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void meDieExpDown() {
        try {
            Message m = new Message(CMD.ME_DIE_EXP_DOWN);
            DataOutputStream ds = m.writer();
            ds.writeByte(player.typePk);
            ds.writeShort(player.x);
            ds.writeShort(player.y);
            ds.writeLong(player.expDown);
            ds.flush();
            sendMessage(m);
            m.cleanup();
        } catch (IOException ex) {
            Log.error("meDieExpDown err: " + ex.getMessage(), ex);
        }
    }

    public void meDie() {
        try {
            Message m = new Message(CMD.ME_DIE);
            DataOutputStream ds = m.writer();
            ds.writeByte(player.typePk);
            ds.writeShort(player.x);
            ds.writeShort(player.y);
            ds.writeLong(player.exp);
            ds.flush();
            sendMessage(m);
            m.cleanup();
        } catch (IOException ex) {
            Log.error("meDie err: " + ex.getMessage(), ex);
        }
    }

    public void openFindParty(HashMap<String, Group> groups) {
        try {
            Message ms = messageSubCommand(CMD.FIND_PARTY);
            DataOutputStream ds = ms.writer();
            for (Group g : groups.values()) {
                MemberGroup party = g.memberGroups.get(0);
                ds.writeByte(party.classId);
                ds.writeByte(party.getChar().level);
                ds.writeUTF(party.name);
                ds.writeByte(g.memberGroups.size());
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Char.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void splitItem(List<Item> list) {
        try {
            Message m = new Message(CMD.SPLIT);
            DataOutputStream ds = m.writer();
            ds.writeByte(list.size());
            for (Item it : list) {
                ds.writeByte(it.index);
                ds.writeShort(it.id);
            }
            ds.flush();
            sendMessage(m);
            m.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Char.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void partyInvite(int id, String name) {
        try {
            Message mss = new Message(CMD.PARTY_INVITE);
            DataOutputStream ds = mss.writer();
            ds.writeInt(id);
            ds.writeUTF(name);
            ds.flush();
            sendMessage(mss);
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void outParty() {
        sendMessage(new Message(CMD.PARTY_OUT));

    }

    public void openUIShop(byte type, List<ItemStore> items) {
        try {
            Message ms = new Message(CMD.OPEN_UI_SHOP);
            DataOutputStream ds = ms.writer();
            ds.writeByte(type);
            ds.writeByte(items.size());
            int index = 0;
            for (ItemStore item : items) {
                ds.writeByte(index++);
                ds.writeShort(item.getItemID());
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void updateTaskCount(int count) {
        try {
            Message ms = new Message(CMD.TASK_UPDATE);
            DataOutputStream ds = ms.writer();
            ds.writeShort(count);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void taskNext() {
        sendMessage(new Message(CMD.TASK_NEXT));
    }

    public void clearTask() {
        sendMessage(messageNotMap(CMD.CLEAR_TASK));
    }

    public void requestIcon(Message ms) {
        try {
            int icon = ms.reader().readInt();
            byte[] ab = GameData.getInstance().loadFile("Data/Img/Small/" + session.zoomLevel + "/Small" + icon + ".png");
            Message mss = messageNotMap(CMD.REQUEST_ICON);
            DataOutputStream ds = mss.writer();
            ds.writeInt(icon);
            ds.writeInt(ab.length);
            ds.write(ab);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void removeFriend(String name) {
        try {
            Message m = messageSubCommand(CMD.FRIEND_REMOVE);
            DataOutputStream ds = m.writer();
            ds.writeUTF(name);
            ds.flush();
            sendMessage(m);
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void requestFriend() {
        try {
            player.isViewListFriend = true;
            Message m = messageSubCommand(CMD.REQUEST_FRIEND);
            DataOutputStream ds = m.writer();
            Friend[] friends = player.getFriends();
            for (Friend friend : friends) {
                ds.writeUTF(friend.name);
                if (friend.type == 1 && Char.findCharByName(friend.name) != null) {
                    ds.writeByte(3);
                } else {
                    ds.writeByte(friend.type);
                }
            }
            ds.flush();
            sendMessage(m);
            m.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void requestEnemy() {
        try {
            player.isViewListFriend = false;
            Message m = messageSubCommand(CMD.REQUEST_ENEMIES);
            DataOutputStream ds = m.writer();
            Friend[] enemies = player.getEnemies();
            for (Friend e : enemies) {
                ds.writeUTF(e.name);
                if (Char.findCharByName(e.name) != null) {
                    ds.writeByte(3);
                } else {
                    ds.writeByte(e.type);
                }
            }
            ds.flush();
            sendMessage(m);
            m.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void removeEnemy(String name) {
        try {
            Message m = messageSubCommand(CMD.ENEMIES_REMOVE);
            DataOutputStream ds = m.writer();
            ds.writeUTF(name);
            ds.flush();
            sendMessage(m);
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void requestItemChar(Equip equip, int index) {
        try {
            if (equip == null) {
                return;
            }
            Message mss = new Message(CMD.REQUEST_ITEM_PLAYER);
            DataOutputStream ds = mss.writer();
            ds.writeByte(index);
            ds.writeLong(equip.expire);
            ds.writeInt(equip.yen);
            ds.writeByte(equip.sys);
            ArrayList<ItemOption> options = equip.getDisplayOptions();
            for (ItemOption ab : options) {
                ds.writeByte(ab.optionTemplate.id);
                ds.writeInt(ab.param);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void itemBoxToBag(int index1, int index2) {
        try {
            Message mss = new Message(CMD.ITEM_BOX_TO_BAG);
            DataOutputStream ds = mss.writer();
            ds.writeByte(index1);
            ds.writeByte(index2);
            ds.flush();
            sendMessage(mss);
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void itemBagToBox(int index1, int index2) {
        try {
            Message mss = new Message(CMD.ITEM_BAG_TO_BOX);
            DataOutputStream ds = mss.writer();
            ds.writeByte(index1);
            ds.writeByte(index2);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void useItem(int index) {
        try {
            Message mss = new Message(CMD.ITEM_USE);
            DataOutputStream ds = mss.writer();
            ds.writeByte(index);
            ds.writeByte(player.speed);
            ds.writeInt((int)player.maxHP);
            ds.writeInt((int)player.maxMP);
            ds.writeShort((short)player.eff5buffhp);
            ds.writeShort((short)player.eff5buffmp);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void itemBodyToBag(int equipType, int index) {
        try {
            Message mss = new Message(CMD.ITEM_BODY_TO_BAG);
            DataOutputStream ds = mss.writer();
            ds.writeByte(player.speed);
            ds.writeInt((int)player.maxHP);
            ds.writeInt((int)player.maxMP);
            ds.writeShort((short)player.eff5buffhp);
            ds.writeShort((short)player.eff5buffmp);
            ds.writeByte(equipType);
            ds.writeByte(index);
            ds.writeShort(player.head);
            ds.flush();
            sendMessage(mss);
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void itemInfo(Item item, byte typeUI, byte indexUI) {
        try {
            Message mss = new Message(CMD.REQUEST_ITEM_INFO);
            DataOutputStream ds = mss.writer();
            ds.writeByte(typeUI);
            ds.writeByte(indexUI);
            ds.writeLong(item.expire);
            ds.writeInt(item.yen);
            if (item.template.isTypeBody() || item.template.isTypeMount() || item.template.isTypeNgocKham()) {
                ds.writeByte(item.sys);
                ArrayList<ItemOption> options = item.getDisplayOptions();
                for (ItemOption ability : options) {
                    ds.writeByte(ability.optionTemplate.id);
                    ds.writeInt(ability.param);
                }
            } else if (item.id == ItemName.DIA_DO2 || item.id == ItemName.DIA_DO3 || item.id == ItemName.DIA_DO4) {
                byte[] ab = Server.IMAGE_MAP_ARR[item.id - ItemName.DIA_DO2][session.zoomLevel - 1].getData();
                ds.writeInt(ab.length);
                ds.write(ab);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void itemStoreInfo(ItemStore item, byte typeUI, byte indexUI) {
        try {
            int xu = item.getCoin();
            int yen = item.getYen();
            int luong = item.getGold();
            Message mss = new Message(CMD.REQUEST_ITEM_INFO);
            DataOutputStream ds = mss.writer();
            ds.writeByte(typeUI);
            ds.writeByte(indexUI);
            ds.writeLong(item.getExpire());
            ds.writeInt(xu);
            ds.writeInt(yen);
            ds.writeInt(luong);
            ItemTemplate template = item.getTemplate();
            if (template.isTypeBody() || template.isTypeMount() || template.isTypeNgocKham()) {
                ds.writeByte(item.getSys());
                List<ItemOption> options = item.getMaxOptions();
                for (ItemOption o : options) {
                    ds.writeByte(o.optionTemplate.id);
                    ds.writeInt(o.param);
                }
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void equipmentInfo(Equip equip, byte typeUI, byte indexUI) {
        try {
            Message mss = new Message(CMD.REQUEST_ITEM_INFO);
            DataOutputStream ds = mss.writer();
            ds.writeByte(typeUI);
            ds.writeByte(indexUI);
            ds.writeLong(equip.expire);
            ds.writeInt(equip.yen);
            ds.writeByte(equip.sys);
            ArrayList<ItemOption> options = equip.getDisplayOptions();
            for (ItemOption ability : options) {
                ds.writeByte(ability.optionTemplate.id);
                ds.writeInt(ability.param);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openMenu(String text) {
        try {
            ArrayList<Menu> menus = player.getMenus();
            Message ms = new Message(CMD.OPEN_MENU_ID);
            DataOutputStream ds = ms.writer();
            ds.writeUTF(text);
            ds.writeByte(menus.size());
            for (Menu menu : menus) {
                ds.writeUTF(menu.getName());
                ds.writeShort(menu.getId());
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Log.error("open menu err", ex);
        }
    }

    public void ngocKham(byte type, Item item) {
        try {
            Message mss = new Message(CMD.NGOCKHAM);
            DataOutputStream ds = mss.writer();
            ds.writeByte(type);
            ds.writeInt(player.user.gold);
            ds.writeInt(player.getCoinInt());
            ds.writeInt(player.getYenInt());
            if (type == 1) {
                ds.writeByte(item.upgrade);
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            Log.error("ngocKham err", ex);
        }
    }

    public void upgrade(byte type, Item item) {
        try {
            Message mss = new Message(CMD.UPGRADE);
            DataOutputStream ds = mss.writer();
            ds.writeByte(type);
            ds.writeInt(player.user.gold);
            ds.writeInt(player.getCoinInt());
            ds.writeInt(player.getYenInt());
            ds.writeByte(item.upgrade);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            Log.error("upgrade err", ex);
        }
    }

    public void openUIMenu() {
        try {
            ArrayList<Menu> menus = player.getMenus();
            Message ms = new Message(CMD.OPEN_UI_NEWMENU);
            DataOutputStream ds = ms.writer();
            for (Menu menu : menus) {
                ds.writeUTF(menu.getName());
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Log.error("open menu err", ex);
        }
    }

    public void updatePotential() {
        try {
            Message ms = messageSubCommand(CMD.POTENTIAL_UP);
            DataOutputStream ds = ms.writer();
            ds.writeByte(player.speed);
            ds.writeInt((int)player.maxHP);
            ds.writeInt((int)player.maxMP);
            ds.writeShort((short)player.potentialPoint);
            ds.writeShort(player.potential[0]);
            ds.writeShort(player.potential[1]);
            ds.writeInt(player.potential[2]);
            ds.writeInt(player.potential[3]);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void levelUp() {
        try {
            Message ms = messageSubCommand(CMD.ME_LOAD_LEVEL);
            DataOutputStream ds = ms.writer();
            ds.writeByte(player.speed);
            ds.writeInt((int)player.maxHP);
            ds.writeInt((int)player.maxMP);
            ds.writeLong(player.exp);
            ds.writeShort((short)player.skillPoint);
            ds.writeShort((short)player.potentialPoint);
            ds.writeShort(player.potential[0]);
            ds.writeShort(player.potential[1]);
            ds.writeInt(player.potential[2]);
            ds.writeInt(player.potential[3]);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void expandBag(Item item) {
        try {
            Message ms = messageSubCommand(CMD.UPDATE_BAG_COUNT);
            DataOutputStream ds = ms.writer();
            ds.writeByte(player.numberCellBag);
            ds.writeByte(item.index);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadSkill() {
        try {
            Message ms = messageSubCommand(CMD.ME_LOAD_SKILL);
            DataOutputStream ds = ms.writer();
            ds.writeByte(player.speed);
            ds.writeInt((int)player.maxHP);
            ds.writeInt((int)player.maxMP);
            ds.writeShort((short)player.skillPoint);
            ds.writeByte(player.vSkill.size());
            for (Skill my : player.vSkill) {
                ds.writeShort(my.id);
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void bagSort() {
        sendMessage(messageSubCommand(CMD.BAG_SORT));
    }

    public void boxSort() {
        sendMessage(messageSubCommand(CMD.BOX_SORT));
    }

    public void openUIConfirm(int npcId, String title) {
        try {
            Message mss = new Message(CMD.OPEN_UI_CONFIRM);
            DataOutputStream ds = mss.writer();
            ds.writeShort(npcId);
            ds.writeUTF(title);
            ArrayList<Menu> menus = player.getMenus();
            ds.writeByte(menus.size());
            for (Menu m : menus) {
                ds.writeUTF(m.getName());
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void menu(String... menu) {
        try {
            Message ms = new Message(CMD.OPEN_UI_MENU);
            DataOutputStream ds = ms.writer();
            for (byte i = 0; i < menu.length; i++) {
                ms.writer().writeUTF(menu[i]);// menu
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException e) {
        }
    }

    public void updateVersion() {
        try {
            Message ms = messageNotMap(CMD.UPDATE_VERSION);
            DataOutputStream ds = ms.writer();
            ds.write(Server.version);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateMap() {
        try {
            List<TileMap> list = MapManager.getInstance().getTileMaps();
            Message ms = messageNotMap(CMD.UPDATE_MAP);
            DataOutputStream ds = ms.writer();
            ds.writeByte(Config.getInstance().getMapVersion());
            ds.writeByte(list.size());
            for (TileMap map : list) {
                ds.writeUTF(map.name);
            }
            List<NpcTemplate> npcs = NpcManager.getInstance().getNpcTemplates();
            ds.writeByte(npcs.size());
            for (NpcTemplate npc : npcs) {
                ds.writeUTF(npc.name);
                ds.writeShort(npc.headId);
                ds.writeShort(npc.bodyId);
                ds.writeShort(npc.legId);
                String[][] menu = npc.menu;
                ds.writeByte(menu.length);
                for (String[] m : menu) {
                    ds.writeByte(m.length);
                    for (String s : m) {
                        ds.writeUTF(s);
                    }
                }
            }
            List<MobTemplate> mobTemplates = MobManager.getInstance().getMobs();
            if (session.isVersionAbove(200)) {
                ds.writeShort(mobTemplates.size());
                for (MobTemplate mob : mobTemplates) {
                    ds.writeByte(mob.type);
                    ds.writeUTF(mob.name);
                    ds.writeInt(mob.hp);
                    ds.writeByte(mob.rangeMove);
                    ds.writeByte(mob.speed);
                }
            } else {
                List<MobTemplate> subList = mobTemplates.subList(0, 255);
                ds.writeByte(subList.size());
                for (MobTemplate mob : subList) {
                    ds.writeByte(mob.type);
                    ds.writeUTF(mob.name);
                    ds.writeInt(mob.hp);
                    ds.writeByte(mob.rangeMove);
                    ds.writeByte(mob.speed);
                }
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateData() {
        try {
            Message ms = messageNotMap(CMD.UPDATE_DATA);
            DataOutputStream ds = ms.writer();
            ds.write(Server.data);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateSkill() {
        try {
            Message ms = messageNotMap(CMD.UPDATE_SKILL);
            DataOutputStream ds = ms.writer();
            ds.write(Server.skill);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateItem() {
        try {
            Message ms = messageNotMap(CMD.UPDATE_ITEM);
            DataOutputStream ds = ms.writer();
            ds.write(ItemManager.getInstance().getData());
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateItem(Item item) {
        try {
            Message ms = new Message(CMD.ITEM_BAG_REFRESH);
            DataOutputStream ds = ms.writer();
            ds.writeByte(item.index);
            ds.writeShort(item.getQuantityDisplay());
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void useItemUpToUp(int index, int quantity) {
        try {
            Message ms = new Message(CMD.ITEM_USE_UPTOUP);
            DataOutputStream ds = ms.writer();
            ds.writeByte(index);
            ds.writeShort(quantity);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void removeItem(int index) {
        try {
            Message ms = new Message(CMD.ITEM_BAG_CLEAR);
            DataOutputStream ds = ms.writer();
            ds.writeByte(index);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openWeb(String btnRight, String btnLeft, String url, String alert) {
        try {
            Message ms = new Message(CMD.ALERT_OPEN_WEB);
            DataOutputStream ds = ms.writer();
            ds.writeUTF(btnRight);
            ds.writeUTF(btnLeft);
            ds.writeUTF(url);
            ds.writeUTF(alert);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void viewInfo(Char pl) {
        try {
            Message ms = new Message(CMD.VIEW_INFO);
            DataOutputStream ds = ms.writer();
            ds.writeInt(pl.id);
            ds.writeUTF(pl.name);
            ds.writeShort(pl.head);
            ds.writeByte(pl.gender);
            ds.writeByte(pl.classId);
            ds.writeByte(pl.hieuChien);
            ds.writeInt((int)pl.hp);
            ds.writeInt((int)pl.maxHP);
            ds.writeInt((int)pl.mp);
            ds.writeInt((int)pl.maxMP);
            ds.writeByte(pl.speed);
            ds.writeShort((short)pl.resFire);// hoả chống
            ds.writeShort((short)pl.resIce);// băng chống
            ds.writeShort((short)pl.resWind);// phong chống
            ds.writeInt((int)pl.damage);// dame
            ds.writeInt((int)pl.dameDown);// dame down
            ds.writeShort((short)pl.exactly);// chinh xac
            ds.writeShort((short)pl.miss);// khả năng né đòn
            ds.writeShort((short)pl.fatal);// chí mạng
            ds.writeShort((short)pl.reactDame);// phản đòn cận chiến
            ds.writeShort((short)pl.sysUp);// cường khắc
            ds.writeShort((short)pl.sysDown);// hạ khắc
            ds.writeByte(pl.level);
            ds.writeShort(321);// diem hoat dong
            if (pl.clan == null) {
                ds.writeUTF("");
            } else {
                String name = pl.clan.getName();
                int typeClan = pl.clan.getMemberByName(pl.name).getType();
                ds.writeUTF(name);
                ds.writeByte(typeClan);
            }
            ds.writeShort(pl.pointUyDanh);// diem uy danh
            ds.writeShort(pl.pointNon);// diem non
            ds.writeShort(pl.pointAo);// diem ao
            ds.writeShort(pl.pointGangTay);// diem gang tay
            ds.writeShort(pl.pointQuan);// diem quan
            ds.writeShort(pl.pointGiay);// diem giay
            ds.writeShort(pl.pointVuKhi);// diem vu khi
            ds.writeShort(pl.pointLien);// diem lien
            ds.writeShort(pl.pointNhan);// diem nhan
            ds.writeShort(pl.pointNgocBoi);// diem ngoc boi
            ds.writeShort(pl.pointPhu);// diem phu
            ds.writeByte(pl.countFinishDay);// nhiem vu hang ngay
            ds.writeByte(pl.countLoopBoss);// truy bắt tà thú
            ds.writeByte(pl.countPB);// vào hang động
            ds.writeByte(pl.limitTiemNangSo);// học sách tiềm năng
            ds.writeByte(pl.limitKyNangSo);// hoc sách kỹ năng
            if (!player.isVersionAbove(180)) {
                for (int i = 0; i < 16; i++) {
                    if (pl.equipment[i] != null) {
                        ds.writeShort(pl.equipment[i].id);
                        ds.writeByte(pl.equipment[i].upgrade);
                        ds.writeByte(pl.equipment[i].sys);
                    }
                }
            } else {
                for (int i = 0; i < 16; i++) {
                    if (pl.equipment[i] != null) {
                        ds.writeShort(pl.equipment[i].id);
                        ds.writeByte(pl.equipment[i].upgrade);
                        ds.writeByte(pl.equipment[i].sys);
                    } else {
                        ds.writeShort(-1);
                    }
                }
                for (int i = 0; i < 16; i++) {
                    if (pl.fashion[i] != null) {
                        ds.writeShort(pl.fashion[i].id);
                        ds.writeByte(pl.fashion[i].upgrade);
                        ds.writeByte(pl.fashion[i].sys);
                    } else {
                        ds.writeShort(-1);
                    }
                }
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();

            ms = new Message(CMD.VIEW_INFO1);
            ds = ms.writer();
            ds.writeInt(0);// tinh tu
            ds.writeByte(pl.limitPhongLoi);
            ds.writeByte(pl.limitBangHoa);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
            if (pl != player) {
                updateInfoChar(pl);
                pl.serverMessage(player.name + " đang xem thông tin của bạn!");
            } else {
                updateInfoMe();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteItemBody(int index) {
        try {
            Message ms = new Message(CMD.ITEM_BODY_CLEAR);
            DataOutputStream ds = ms.writer();
            ds.writeByte(index);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addItem(Item item) {
        try {
            Message ms = new Message(CMD.ITEM_BAG_ADD);
            DataOutputStream ds = ms.writer();
            ds.writeByte(item.index);
            ds.writeShort(item.id);
            ds.writeBoolean(item.isLock);
            if (item.template.isTypeBody() || item.template.isTypeNgocKham()) {
                ds.writeByte(item.upgrade);
            }
            ds.writeBoolean(item.hasExpire());
            ds.writeShort(item.getQuantityDisplay());
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addItemQuantity(int index, int quantity) {
        try {
            Message ms = new Message(CMD.ITEM_BAG_ADD_QUANTITY);
            DataOutputStream ds = ms.writer();
            ds.writeByte(index);
            ds.writeShort(quantity);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void tradeAccept() {
        sendMessage(new Message(CMD.TRADE_ACCEPT));
    }

    public void testInvite(int charId) {
        Message msg = null;
        try {
            msg = new Message(CMD.TEST_INVITE);
            msg.writer().writeInt(charId);
            sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void addCuuSat(int charId) {
        Message msg = null;
        try {
            msg = new Message(CMD.ADD_CUU_SAT);
            msg.writer().writeInt(charId);
            sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void meCuuSat(int charId) {
        Message msg = null;
        try {
            msg = new Message(CMD.ME_CUU_SAT);
            msg.writer().writeInt(charId);
            sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void clearCuuSat(int charId) {
        Message msg = null;
        try {
            msg = new Message(CMD.CLEAR_CUU_SAT);
            if (player.id != charId) {
                msg.writer().writeInt(charId);
            }
            sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void clearMap() {
        Message msg = null;
        try {
            msg = new Message(CMD.MAP_CLEAR);
            sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void endWait(String text) {
        try {
            Message ms = messageSubCommand(CMD.END_WAIT);
            DataOutputStream ds = ms.writer();
            ds.writeUTF(text);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void endWait() {
        try {
            sendMessage(messageSubCommand(CMD.END_WAIT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(Message ms) {
        if (this.session != null) {
            this.session.sendMessage(ms);
        }
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
}
