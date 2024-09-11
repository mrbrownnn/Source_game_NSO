
package com.nsoz.map;

import com.nsoz.map.item.ItemMap;
import com.nsoz.map.zones.Zone;
import com.nsoz.clan.Clan;
import com.nsoz.constants.CMD;
import com.nsoz.model.Figurehead;
import com.nsoz.model.Char;
import com.nsoz.effect.Effect;
import com.nsoz.item.Mount;
import com.nsoz.mob.Mob;
import com.nsoz.network.AbsService;
import com.nsoz.network.Message;
import com.nsoz.npc.Npc;
import com.nsoz.option.ItemOption;
import com.nsoz.util.Log;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Admin
 */
public class MapService extends AbsService {

    private Zone zone;

    public MapService(Zone zone) {
        this.zone = zone;
    }

    public void playerMove(Char p) {
        try {
            Message mss = new Message(CMD.PLAYER_MOVE);
            DataOutputStream ds = mss.writer();
            ds.writeInt(p.id);
            ds.writeShort(p.x);
            ds.writeShort(p.y);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            Log.error("player move err: " + ex.getMessage(), ex);
        }
    }

    public void acceptInviteClan(Char p) {
        Clan clan = p.clan;
        if (clan != null) {
            try {
                Message ms = messageSubCommand(CMD.CLAN_ACCEPT_INVITE);
                DataOutputStream ds = ms.writer();
                ds.writeInt(p.id);
                ds.writeUTF(clan.getName());
                ds.writeByte(clan.getMemberByName(p.name).getType());
                ds.flush();
                sendMessage(ms);
                ms.cleanup();
            } catch (Exception ex) {
                Log.error("acceptInviteClan err: " + ex.getMessage(), ex);
            }
        }
    }

    public void moveOutClan(Char p) {
        try {
            Message ms = messageNotMap(CMD.CLAN_MOVEOUT_MEM);
            DataOutputStream ds = ms.writer();
            ds.writeInt(p.id);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Log.error("moveOutClan err: " + ex.getMessage(), ex);
        }
    }

    public void removeItem(short itemID) {
        try {
            Message ms = new Message(CMD.ITEMMAP_REMOVE);
            DataOutputStream ds = ms.writer();
            ds.writeShort(itemID);
            ds.flush();
            sendMessage(ms);
        } catch (Exception ex) {
            Log.error("removeitem err: " + ex.getMessage(), ex);
        }
    }

    public void sendMessage(Message ms) {
        List<Char> chars = zone.getChars();
        for (Char pl : chars) {
            pl.getService().sendMessage(ms);
        }
    }

    public void recoveryMonster(Mob mob) {
        try {
            int levelBoss = mob.levelBoss;
            double maxHp = mob.maxHP;
            if (levelBoss == 1 || levelBoss == 2) {
                levelBoss = 0;
                maxHp = mob.template.hp;
            }
            Message m = new Message(CMD.NPC_LIVE);
            DataOutputStream ds = m.writer();
            ds.writeByte(mob.id);
            ds.writeByte(mob.sys);
            ds.writeByte(levelBoss);
            ds.writeInt((int)maxHp);
            ds.flush();
            sendMessage(m);
            m.cleanup();
        } catch (IOException ex) {
            Log.error("recovery monster err: " + ex.getMessage(), ex);
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
        } catch (Exception ex) {
            Log.error("sendTimeInMap err: " + ex.getMessage(), ex);
        }
    }

    public void addMob(Mob mob) {
        try {
            int levelBoss = mob.levelBoss;
            double hp = mob.hp;
            double maxHp = mob.maxHP;
            if (levelBoss == 1 || levelBoss == 2) {
                levelBoss = 0;
                maxHp = mob.template.hp;
                hp = maxHp;
            }
            Message mss = new Message(CMD.SERVER_ADD_MOB);
            DataOutputStream ds = mss.writer();
            ds.writeByte(0);
            ds.writeByte(1);
            ds.writeByte(mob.id);
            ds.writeBoolean(mob.isDisable);
            ds.writeBoolean(mob.isDontMove);
            ds.writeBoolean(mob.isFire);
            ds.writeBoolean(mob.isIce);
            ds.writeBoolean(mob.isWind);
            ds.writeByte(mob.template.id);
            ds.writeByte(mob.sys);
            ds.writeInt((int)hp);
            ds.writeByte(mob.level);
            ds.writeInt((int)maxHp);
            ds.writeShort(mob.x);
            ds.writeShort(mob.y);
            ds.writeByte(mob.status);
            ds.writeByte(levelBoss);
            ds.writeBoolean(mob.isBoss);
            ds.flush();
            sendMessage(mss);
        } catch (Exception ex) {
            Log.error("add mob err: " + ex.getMessage(), ex);
        }
    }

    public void loadPet(Char _char) {
        List<Char> chars = zone.getChars();
        for (Char pl : chars) {
            pl.getService().loadPet(_char);
        }
    }

    public void refreshHP(Char p) {
        try {
            Message ms = messageSubCommand(CMD.REFRESH_HP);
            DataOutputStream ds = ms.writer();
            ds.writeInt(p.id);
            ds.writeInt((int)p.hp);
            ds.writeInt((int)p.maxHP);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Log.error("refresh hp err: " + ex.getMessage(), ex);
        }
    }

    public void addItemMap(ItemMap itemMap) {
        try {
            Message m = new Message(CMD.ITEMMAP_ADD);
            DataOutputStream ds = m.writer();
            ds.writeShort(itemMap.getId());
            ds.writeShort(itemMap.getItemID());
            ds.writeShort(itemMap.getX());
            ds.writeShort(itemMap.getY());
            ds.flush();
            sendMessage(m);
            m.cleanup();
        } catch (IOException ex) {
            Log.error("add item err: " + ex.getMessage(), ex);
        }
    }

    public void playerAddEffect(Char _char, Effect effect) {
        try {
            Message ms = messageSubCommand(CMD.PLAYER_ADD_EFFECT);
            DataOutputStream ds = ms.writer();
            ds.writeInt(_char.id);
            ds.writeByte(effect.template.id);// templateId
            ds.writeInt(effect.getTimeStart());// timeStart
            ds.writeInt(effect.getTimeLength());// tiemLenght
            ds.writeShort(effect.param);// param
            if (effect.template.type == 14 || effect.template.type == 2 || effect.template.type == 3) {
                ds.writeShort(_char.x);
                ds.writeShort(_char.y);
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Log.error("add effect err: " + ex.getMessage(), ex);
        }
    }

    public void playerRemoveEffect(Char _char, Effect effect) {
        try {
            Message ms = messageSubCommand(CMD.PLAYER_REMOVE_EFFECT);
            DataOutputStream ds = ms.writer();
            ds.writeInt(_char.id);
            ds.writeByte(effect.template.id);
            if (effect.template.type == 11) {
                ds.writeShort(_char.x);
                ds.writeShort(_char.y);
            } else if (effect.template.type == 0 || effect.template.type == 12) {
                ds.writeInt((int)_char.hp);
                ds.writeInt((int)_char.mp);
            } else if (effect.template.type == 4 || effect.template.type == 13 || effect.template.type == 17) {
                ds.writeInt((int)_char.hp);
            } else if (effect.template.type == 23) {
                ds.writeInt((int)_char.hp);
                ds.writeInt((int)_char.maxHP);
            }
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Log.error("remove eff err: " + ex.getMessage(), ex);
        }
    }

    public void playerEditEffect(Char _char, Effect effect) {
        try {
            Message ms = messageSubCommand(CMD.PLAYER_EDIT_EFFECT);
            DataOutputStream ds = ms.writer();
            ds.writeInt(_char.id);
            ds.writeByte(effect.template.id);// templateId
            ds.writeInt(effect.getTimeStart());
            ds.writeInt(effect.getTimeLength());
            ds.writeShort(effect.param);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Log.error("edit effect err: " + ex.getMessage(), ex);
        }
    }

    public void loadLevel(Char _char) {
        try {
            Message ms = messageSubCommand(CMD.PLAYER_LOAD_LEVEL);
            DataOutputStream ds = ms.writer();
            ds.writeInt(_char.id);
            ds.writeInt((int)_char.hp);
            ds.writeInt((int)_char.maxHP);
            ds.writeByte(_char.level);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Log.error("load level err: " + ex.getMessage(), ex);
        }
    }

    public void chat(int id, String text) {
        try {
            Message mss = new Message(CMD.CHAT_MAP);
            DataOutputStream ds = mss.writer();
            ds.writeInt(id);
            ds.writeUTF(text);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            Log.error("chat err: " + ex.getMessage(), ex);
        }
    }

    public void removeBuNhin(int index) {
        try {
            Message ms = new Message(CMD.CLEAR_BUNHIN);
            DataOutputStream ds = ms.writer();
            ds.writeShort(index);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Log.error("remove bu nhin err: " + ex.getMessage(), ex);
        }
    }

    public void attackMonster(double damage, boolean flag, Mob mob) {
        try {
            if (mob.isDead) {
                Message msg = new Message(CMD.NPC_DIE);
                DataOutputStream ds = msg.writer();
                ds.writeByte(mob.id);
                ds.writeInt((int)damage);
                ds.writeBoolean(flag);
                if (mob.itemMap != null) {
                    ItemMap item = mob.itemMap;
                    ds.writeShort(item.getId());
                    ds.writeShort(item.getItemID());
                    ds.writeShort(item.getX());
                    ds.writeShort(item.getY());
                }
                ds.flush();
                sendMessage(msg);
            } else if (damage == -1) {
                Message msg = new Message(CMD.NPC_MISS);
                DataOutputStream ds = msg.writer();
                ds.writeByte(mob.id);
                ds.writeInt((int)mob.hp);
                ds.flush();
                sendMessage(msg);
            } else {
                Message mss = new Message(CMD.NPC_HP);
                DataOutputStream ds = mss.writer();
                ds.writeByte(mob.id);
                ds.writeInt((int)mob.hp);
                ds.writeInt((int)damage);
                ds.writeBoolean(flag);
                ds.writeByte(mob.levelBoss);
                ds.writeInt((int)mob.maxHP);
                ds.flush();
                sendMessage(mss);
            }
        } catch (Exception ex) {
            Log.error("attack npc err: " + ex.getMessage(), ex);
        }
    }

    public void attackCharacter(double damage, double dameMp, Char _char) {
        try {
            Message mss = new Message(CMD.HAVE_ATTACK_PLAYER);
            DataOutputStream ds = mss.writer();
            ds.writeInt(_char.id);
            ds.writeInt((int)_char.hp);
            ds.writeInt((int)damage);
            ds.writeInt((int)_char.mp);
            ds.writeInt((int)dameMp);
            ds.flush();
            sendMessage(mss);
        } catch (Exception ex) {
            Log.error("attack player err: " + ex.getMessage(), ex);
        }
    }

    public void setSkillPaint_1(ArrayList<Mob> mobs, Char _char, byte skillId) {
        try {
            Message mss = new Message(CMD.PLAYER_ATTACK_NPC);
            DataOutputStream ds = mss.writer();
            ds.writeInt(_char.id);
            ds.writeByte(skillId);
            if (mobs != null) {
                for (Mob mob : mobs) {
                    ds.writeByte(mob.id);
                }
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (Exception ex) {
            Log.error("skill paint 1 err: " + ex.getMessage(), ex);
        }
    }

    public void setSkillPaint_2(ArrayList<Char> chars, Char _char, byte skillId) {
        try {
            Message mss = new Message(CMD.PLAYER_ATTACK_PLAYER);
            DataOutputStream ds = mss.writer();
            ds.writeInt(_char.id);
            ds.writeByte(skillId);
            if (chars != null) {
                for (Char pl : chars) {
                    ds.writeInt(pl.id);
                }
            }
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (Exception ex) {
            Log.error("skill paint 2 err: " + ex.getMessage(), ex);
        }
    }

    public void setFire(Mob mob) {
        try {
            Message ms = new Message(CMD.NPC_IS_FIRE);
            DataOutputStream ds = ms.writer();
            ds.writeByte(mob.id);
            ds.writeBoolean(mob.isFire);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Log.error("set fire err: " + ex.getMessage(), ex);
        }
    }

    public void setIce(Mob mob) {
        try {
            Message ms = new Message(CMD.NPC_IS_ICE);
            DataOutputStream ds = ms.writer();
            ds.writeByte(mob.id);
            ds.writeBoolean(mob.isIce);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Log.error("set ice err: " + ex.getMessage(), ex);
        }
    }

    public void setWind(Mob mob) {
        try {
            Message ms = new Message(CMD.NPC_IS_WIND);
            DataOutputStream ds = ms.writer();
            ds.writeByte(mob.id);
            ds.writeBoolean(mob.isWind);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Log.error("set wind err: " + ex.getMessage(), ex);
        }
    }

    public void setMove(Mob mob) {
        try {
            Message ms = new Message(CMD.NPC_IS_MOVE);
            DataOutputStream ds = ms.writer();
            ds.writeByte(mob.id);
            ds.writeBoolean(mob.isDontMove);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Log.error("set don't move err: " + ex.getMessage(), ex);
        }
    }

    public void setDisable(Mob mob) {
        try {
            Message ms = new Message(CMD.NPC_IS_DISABLE);
            DataOutputStream ds = ms.writer();
            ds.writeByte(mob.id);
            ds.writeBoolean(mob.isDisable);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Log.error("set disable err: " + ex.getMessage(), ex);
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
            Log.error("load mount err: " + ex.getMessage(), ex);
        }
    }

    public void loadMask(Char _char) {
        try {
            Message ms = messageSubCommand(CMD.PLAYER_LOAD_MAT_NA);
            DataOutputStream ds = ms.writer();
            ds.writeInt(_char.id);
            ds.writeInt((int)_char.hp);
            ds.writeInt((int)_char.maxHP);
            ds.writeShort((short)_char.eff5buffhp);
            ds.writeShort((short)_char.eff5buffmp);
            ds.writeShort(_char.head);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Log.error("load mask err: " + ex.getMessage(), ex);
        }
    }

    public void loadHP(Char _char) {
        try {
            Message ms = messageSubCommand(CMD.PLAYER_LOAD_HP);
            DataOutputStream ds = ms.writer();
            ds.writeInt(_char.id);
            ds.writeInt((int)_char.hp);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Log.error("load HP err: " + ex.getMessage(), ex);
        }
    }

    public void loadPant(Char _char) {
        try {
            Message ms = messageSubCommand(CMD.PLAYER_LOAD_QUAN);
            DataOutputStream ds = ms.writer();
            ds.writeInt(_char.id);
            ds.writeInt((int)_char.hp);
            ds.writeInt((int)_char.maxHP);
            ds.writeShort((short)_char.eff5buffhp);
            ds.writeShort((short)_char.eff5buffmp);
            ds.writeShort(_char.leg);
            ds.flush();
            sendMessage(ms);
        } catch (Exception ex) {
            Log.error("load pant err: " + ex.getMessage(), ex);
        }
    }

    public void loadShirt(Char _char) {
        try {
            Message ms = messageSubCommand(CMD.PLAYER_LOAD_AO);
            DataOutputStream ds = ms.writer();
            ds.writeInt(_char.id);
            ds.writeInt((int)_char.hp);
            ds.writeInt((int)_char.maxHP);
            ds.writeShort((short)_char.eff5buffhp);
            ds.writeShort((short)_char.eff5buffmp);
            ds.writeShort(_char.body);
            ds.flush();
            sendMessage(ms);
        } catch (Exception ex) {
            Log.error("load shirt err: " + ex.getMessage(), ex);
        }
    }

    public void loadWeapon(Char _char) {
        try {
            Message ms = messageSubCommand(CMD.PLAYER_LOAD_VUKHI);
            DataOutputStream ds = ms.writer();
            ds.writeInt(_char.id);
            ds.writeInt((int)_char.hp);
            ds.writeInt((int)_char.maxHP);
            ds.writeShort((short)_char.eff5buffhp);
            ds.writeShort((short)_char.eff5buffmp);
            ds.writeShort(_char.weapon);
            ds.flush();
            sendMessage(ms);
        } catch (Exception ex) {
            Log.error("load wp err: " + ex.getMessage(), ex);
        }
    }

    public void teleport(Char p) {
        try {
            Message mss = new Message(CMD.AUTO_ATTACK_MOVE);
            DataOutputStream ds = mss.writer();
            ds.writeByte(0);
            ds.writeInt(p.id);
            ds.writeShort(p.x);
            ds.writeShort(p.y);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            Log.error("teleport err: " + ex.getMessage(), ex);
        }
    }

    public void mobMeAttack(Char _c, Object target) {
        try {
            int mobID = -1;
            int targetID = -1;
            byte type = 0;
            if (target instanceof Mob) {
                mobID = ((Mob) target).id;
                type = 0;
            } else {
                targetID = ((Char) target).id;
                type = 1;
            }
            Message ms = new Message(CMD.ThuNuoi_ATTACK);
            DataOutputStream ds = ms.writer();
            ds.writeInt(_c.id);
            ds.writeByte(mobID);
            ds.writeShort(5);
            ds.writeByte(0);
            ds.writeByte(0);
            ds.writeByte(type);
            if (type == 1) {
                ds.writeInt(targetID);
            }
            ds.flush();
            sendMessage(ms);
        } catch (Exception ex) {
            Log.error("mob me err: " + ex.getMessage(), ex);
        }
    }

    public void createBuNhin(Figurehead buNhin) {
        try {
            Message ms = new Message(CMD.CREATE_BUNHIN);
            DataOutputStream ds = ms.writer();
            ds.writeUTF(buNhin.name);
            ds.writeShort(buNhin.x);
            ds.writeShort(buNhin.y);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Log.error("create bunhin err: " + ex.getMessage(), ex);
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
            Log.error("update info char err: " + ex.getMessage(), ex);
        }
    }

    public void addEffect(Object obj, int id, int timeLive, int wait, int dy) {
        try {
            Message ms = new Message(CMD.GET_EFFECT);
            DataOutputStream ds = ms.writer();
            ds.writeByte(0);
            if (obj instanceof Mob) {
                Mob mob = (Mob) obj;
                ds.writeByte(1);
                ds.writeByte(mob.id);
            } else {
                ds.writeByte(0);
                Char _char = (Char) obj;
                ds.writeInt(_char.id);
            }
            ds.writeShort(id);
            ds.writeInt(timeLive);
            ds.writeByte(wait);
            ds.writeByte(dy);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Log.error("add eff err: " + ex.getMessage(), ex);
        }
    }

    public void testAccept(int playerId, int playerId2) {
        Message msg = null;
        try {
            msg = new Message(CMD.TEST_INVITE_ACCEPT);
            msg.writer().writeInt(playerId);
            msg.writer().writeInt(playerId2);
            sendMessage(msg);
        } catch (Exception ex) {
            Log.error("test accept err: " + ex.getMessage(), ex);
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void npcUpdate(Npc npc) {
        try {
            Message ms = messageSubCommand(CMD.NPC_PLAYER_UPDATE);
            DataOutputStream ds = ms.writer();
            ds.writeByte(npc.id);
            ds.writeByte(npc.status);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Log.error("npc update err: " + ex.getMessage(), ex);
        }
    }

    public void testEnd(int playerId, int playerId2, double hp) {
        Message msg = null;
        try {
            msg = new Message(CMD.TEST_END);
            msg.writer().writeInt(playerId);
            msg.writer().writeInt(playerId2);
            msg.writer().writeInt((int)hp);
            sendMessage(msg);
        } catch (Exception ex) {
            Log.error("test end err: " + ex.getMessage(), ex);
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void returnPointMap(Char p) {
        try {
            Message mss = new Message(CMD.RETURN_POINT_MAP);
            DataOutputStream ds = mss.writer();
            ds.writeInt(p.id);
            ds.writeShort(p.x);
            ds.writeShort(p.y);
            ds.flush();
            sendMessage(mss);
            mss.cleanup();
        } catch (IOException ex) {
            Log.error("return point map err: " + ex.getMessage(), ex);
        }
    }

    public void npcChange(Mob mob) {
        try {
            Message msg = new Message(CMD.NPC_CHANGE);
            DataOutputStream ds = msg.writer();
            ds.writeByte(mob.id);
            if (mob.itemMap != null) {
                ItemMap item = mob.itemMap;
                ds.writeShort(item.getId());
                ds.writeShort(item.getItemID());
                ds.writeShort(item.getX());
                ds.writeShort(item.getY());
            }
            ds.flush();
            sendMessage(msg);
            msg.cleanup();
        } catch (IOException ex) {
            Log.error("npc change err: " + ex.getMessage(), ex);
        }
    }

    public void waitToDie(Char p) {
        try {
            Message mss = new Message(CMD.PLAYER_DIE);
            DataOutputStream ds = mss.writer();
            ds.writeInt(p.id);
            ds.writeByte(p.typePk);
            ds.writeShort(p.x);
            ds.writeShort(p.y);
            ds.flush();
            sendMessage(mss);
        } catch (Exception ex) {
            Log.error("waitToDie err: " + ex.getMessage(), ex);
        }
    }

    public void callEffectNpc(Mob mob) {
        try {
            Message ms = messageSubCommand(CMD.CALL_EFFECT_NPC);
            DataOutputStream ds = ms.writer();
            ds.writeByte(CMD.CALL_EFFECT_NPC);
            ds.writeByte(mob.id);
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Log.error("callEffectNpc err: " + ex.getMessage(), ex);
        }
    }

    public void npcAttackPlayer(Mob mob, Char _char) {
        try {
            Message ms = new Message(CMD.NPC_ATTACK_PLAYER);
            DataOutputStream ds = ms.writer();
            ds.writeByte(mob.id);
            ds.writeInt(_char.id);
            ds.writeInt((int)_char.hp);
            ds.writeInt((int)_char.mp);
            ds.writeShort(-1);
            ds.writeByte(0);
            ds.writeByte(0);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (Exception ex) {
            Log.error("npc attack player err: " + ex.getMessage(), ex);
        }
    }

    public void npcAttackBuNhin(Mob mob, int index) {
        try {
            Message ms = new Message(CMD.NPC_ATTACK_BUNHIN);
            DataOutputStream ds = ms.writer();
            ds.writeByte(mob.id);
            ds.writeShort(index);
            ds.writeShort(-1);
            ds.writeByte(0);
            ds.writeByte(0);
            ds.flush();
            sendMessage(ms);
            ms.cleanup();
        } catch (IOException ex) {
            Log.error("npc attack bunhin err: " + ex.getMessage(), ex);
        }
    }

    public void pickItem(Char _char, ItemMap item) {
        _char.getService().pickItem(item);
        List<Char> chars = zone.getChars();
        for (Char pl : chars) {
            if (pl != null && _char != pl) {
                pl.getService().playerPickItem(_char, item);
            }
        }
    }

    public void clearCuuSat(Char _char2) {
        List<Char> chars = zone.getChars();
        for (Char _char : chars) {
            if (_char != null && _char.killCharId == _char2.id) {
                _char.clearCuuSat();
            }
        }
    }

    public void throwItem(Char _char, byte index, ItemMap item) {
        _char.getService().throwItem(index, item);
        List<Char> chars = zone.getChars();
        for (Char c : chars) {
            if (c != _char) {
                c.getService().playerThrowItem(_char, index, item);
            }
        }
    }

    @Override
    public void chat(String name, String text) {

    }
}
