
package com.nsoz.network;

import com.nsoz.constants.CMD;
import com.nsoz.effect.Effect;
import com.nsoz.item.Item;
import com.nsoz.item.Mount;
import com.nsoz.mob.Mob;
import com.nsoz.model.Char;
import com.nsoz.option.ItemOption;
import com.nsoz.server.Server;
import com.nsoz.util.Log;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public abstract class AbsService {

    public abstract void sendMessage(Message ms);

    public abstract void chat(String name, String text);

    public Message messageNotLogin(int command) {
        try {
            Message ms = new Message(CMD.NOT_LOGIN);
            ms.writer().writeByte(command);
            return ms;
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Message newMessage(int command) {
        try {
            Message ms = new Message(CMD.NEW_MESSAGE);
            ms.writer().writeByte(command);
            return ms;
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Message messageNotMap(int command) {
        try {
            Message ms = new Message(CMD.NOT_MAP);
            ms.writer().writeByte(command);
            return ms;
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Message messageSubCommand(int command) {
        try {
            Message ms = new Message(CMD.SUB_COMMAND);
            ms.writer().writeByte(command);
            return ms;
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void addEffectAuto(byte id, short x, short y, byte loop, short time) {
        try {
            Message ms = new Message(CMD.SERVER_ADD_MOB);
            DataOutputStream ds = ms.writer();
            ds.writeByte(1);
            ds.writeByte(id);
            ds.writeShort(x);
            ds.writeShort(y);
            ds.writeByte(loop);
            ds.writeShort(time);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Log.error("add eff auto err: " + ex.getMessage(), ex);
        }
    }

    public void changePk(Char p) {
        try {
            Message ms = messageSubCommand(CMD.UPDATE_TYPE_PK);
            DataOutputStream ds = ms.writer();
            ds.writeInt(p.id);
            ds.writeByte(p.typePk);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Log.error("change pk err: " + ex.getMessage(), ex);
        }
    }

    public void playerAdd(Char pl) {
        try {
            if (pl.isCleaned) {
                return;
            }
            Message ms = new Message(CMD.PLAYER_ADD);
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

    public void playerRemove(int id) {
        try {
            Message ms = new Message(CMD.PLAYER_REMOVE);
            DataOutputStream ds = ms.writer();
            ds.writeInt(id);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Log.error("player remove err: " + ex.getMessage(), ex);
        }
    }

    public void loadGlove(Char _char) {
        try {
            Message ms = messageSubCommand(CMD.PLAYER_LOAD_GIA_TOC);
            DataOutputStream ds = ms.writer();
            ds.writeInt(_char.id);
            ds.writeInt((int)_char.hp);
            ds.writeInt((int)_char.maxHP);
            ds.writeShort(_char.glove);
            ds.flush();
            sendMessage(ms);
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadHonor(Char _char) {
        try {
            Message ms = new Message(CMD.GET_EFFECT);
            DataOutputStream ds = ms.writer();
            ds.writeByte(0);
            ds.writeByte(0);
            ds.writeInt(_char.id);
            ds.writeShort(_char.honor);
            ds.writeInt(5);
            ds.writeByte(0);
            ds.writeByte(1);
            ds.flush();
            sendMessage(ms);
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadCoat(Char _char) {
        try {
            Message ms = messageSubCommand(CMD.PLAYER_LOAD_AO_CHOANG);
            DataOutputStream ds = ms.writer();
            ds.writeInt(_char.id);
            ds.writeInt((int)_char.hp);
            ds.writeInt((int)_char.maxHP);
            ds.writeShort(_char.coat);
            ds.flush();
            sendMessage(ms);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loadMount(Char _char) {
        try {
            Message ms = messageSubCommand(CMD.LOAD_THU_CUOI);
            DataOutputStream ds = ms.writer();
            ds.writeInt(_char.id);
            for (int i = 0; i < 5; i++) {
                Mount m = _char.mount[i];
                if (m != null) {
                    ds.writeShort(m.id);
                    ds.writeByte(m.upgrade);
                    ds.writeLong(m.expire);
                    ds.writeByte(m.sys);
                    ArrayList<ItemOption> options = m.getDisplayOptions();
                    ds.writeByte(options.size());
                    for (ItemOption option : options) {
                        ds.writeByte(option.optionTemplate.id);
                        ds.writeInt(option.param);
                    }
                } else {
                    ds.writeShort(-1);
                }
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void charInfo(Message ms, Char _char) {
        try {
            DataOutputStream ds = ms.writer();
            if (_char.clan == null) {
                ds.writeUTF("");
            } else {
                String name = _char.clan.getName();
                int typeClan = 0;
                try {
                    typeClan = _char.clan.getMemberByName(_char.name).getType();
                } catch (Exception e) {
                }
                ds.writeUTF(name);
                ds.writeByte(typeClan);
            }
            ds.writeBoolean(_char.isInvisible());
            ds.writeByte(_char.typePk);// pk
            ds.writeByte(_char.classId);// nclass
            ds.writeByte(_char.gender);
            ds.writeShort(_char.head);
            ds.writeUTF(_char.name);
            ds.writeInt((int)_char.hp);
            ds.writeInt((int)_char.maxHP);
            ds.writeByte(_char.level);
            ds.writeShort(_char.weapon);
            ds.writeShort(_char.body);
            ds.writeShort(_char.leg);
            ds.writeByte(-1); // mobMe
            ds.writeShort(_char.x);// X
            ds.writeShort(_char.y);// Y
            ds.writeShort((short)_char.eff5buffhp);// eff5buffhp
            ds.writeShort((short)_char.eff5buffmp);// eff5buffmp
            byte[] dataE = _char.getEm().getData();
            ds.write(dataE);
            ds.writeBoolean(_char.isHuman);
            ds.writeBoolean(_char.isNhanBan);
            ds.writeShort(_char.head);
            ds.writeShort(_char.weapon);
            ds.writeShort(_char.body);
            ds.writeShort(_char.leg);
            short[] thoiTrang = _char.getFashion();
            for (int i = 0; i < 10; i++) {
                ds.writeShort(thoiTrang[i]);
            }
        } catch (Exception e) {
            Log.error("charInfo err: " + e.getMessage(), e);
        }
    }

    public void serverMessage(String text) {
        try {
            Message ms = new Message(CMD.SERVER_MESSAGE);
            DataOutputStream ds = ms.writer();
            ds.writeUTF(text);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void serverDialog(String text) {
        try {
            Message ms = new Message(CMD.SERVER_DIALOG);
            DataOutputStream ds = ms.writer();
            ds.writeUTF(text);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void serverAlert(String text) {
        try {
            Message ms = new Message(CMD.SERVER_ALERT);
            DataOutputStream ds = ms.writer();
            ds.writeUTF(text);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void showAlert(String title, String text) {
        try {
            if (title.equals("typemoi")) {
                return;
            }
            Message ms = new Message(CMD.ALERT_MESSAGE);
            DataOutputStream ds = ms.writer();
            ds.writeUTF(title);
            ds.writeUTF(text);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void onBijuuInfo(int playerID, Item[] arrItemViThu) {
        try {
            Message ms = messageBijuu(0);
            DataOutputStream ds = ms.writer();
            ds.writeInt(playerID);
            for (Item item : arrItemViThu) {
                if (item != null) {
                    ds.writeShort(item.id);
                    ds.writeByte(item.upgrade);
                    ds.writeLong(item.expire);
                    ds.writeByte(item.sys);
                    List<ItemOption> options = item.getDisplayOptions();
                    ds.writeByte(options.size());
                    for (ItemOption o : options) {
                        ds.writeByte(o.optionTemplate.id);
                        ds.writeInt(o.param);
                    }
                } else {
                    ds.writeShort(-1);
                }
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Log.error("change bijuu err");
        }
    }

    public void onChangeBijuu(int playerID, Mob mob) {
        try {
            Message ms = messageBijuu(1);
            DataOutputStream ds = ms.writer();
            ds.writeInt(playerID);
            if (mob != null) {
                ds.writeShort(mob.template.id);
                ds.writeBoolean(mob.template.isBoss());
            } else {
                ds.writeShort(0);
                ds.writeBoolean(false);
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Log.error("change bijuu err");
        }
    }

    public Message messageBijuu(int command) {
        try {
            Message ms = new Message(CMD.MAP_ITEM);
            DataOutputStream ds = ms.writer();
            ds.writeByte(-1);
            ds.writeByte(command);
            return ms;
        } catch (Exception ex) {
            Log.error("write message sub comnand err", ex);
        }
        return null;
    }
}
