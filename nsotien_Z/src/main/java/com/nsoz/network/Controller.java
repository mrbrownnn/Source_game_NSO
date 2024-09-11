package com.nsoz.network;

import com.nsoz.constants.CMD;
import com.nsoz.model.Char;
import com.nsoz.model.User;
import com.nsoz.util.Log;

/**
 * @author ASD
 */
public class Controller implements IMessageHandler {

    private final Session client;
    private User user;
    private Char _char;
    private Service service;

    public Controller(Session client) {
        this.client = client;
    }

    public void setUser(User us) {
        this.user = us;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public void setChar(Char _char) {
        this._char = _char;
    }

    @Override
    public void onMessage(Message mss) {
        if (mss != null) {
            try {
                int command = mss.getCommand();
//                System.out.println("COMMAND = "+command);
                if (command != CMD.NOT_LOGIN && command != CMD.NOT_MAP && command != CMD.SUB_COMMAND) {
                    if (user == null || _char == null || user.isCleaned || _char.isCleaned) {
                        return;
                    }
                }
                switch (command) {
                    case CMD.NEW_MESSAGE:
                        newMessage(mss);
                        break;

                    case CMD.REMOVE_VI_THU:
                        _char.actionBijuu(mss);
                        break;

                    case CMD.NOT_MAP:
                        messageNotMap(mss);
                        break;

                    case CMD.NOT_LOGIN:
                        messageNotLogin(mss);
                        break;

                    case CMD.SUB_COMMAND:
                        messageSubCommand(mss);
                        break;

                    case CMD.GET_EFFECT:
                        byte type = mss.reader().readByte();
                        if (type == 1) {
                            service.sendImgEffect(mss);
                        }
                        if (type == 2) {
                            service.sendEffectData(mss);
                        }
                        break;

                    case CMD.RANKED_MATCH:
                        _char.requestRanked(mss);
                        break;

                    case CMD.NGOCKHAM:
                        _char.ngocKham(mss);
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;

                    case CMD.TEST_DUN_INVITE:
                        _char.acceptInviteTestDun(mss);
                        break;
                    case CMD.TEST_GT_INVITE:
                        _char.acceptInviteWarClan(mss);
                        break;


                    case CMD.TEST_DUN_LIST:
                        _char.requestMatchInfo(mss);
                        break;

                    case CMD.OPEN_UI_CONFIRM_ID:
                        _char.confirmID(mss);
                        break;

                    case CMD.SEND_ITEM_TO_AUCTION:
                        _char.sendToSaleItem(mss);
                        break;

                    case CMD.VIEW_ITEM_AUCTION:
                        _char.requestViewDetails(mss);
                        break;

                    case CMD.BUY_ITEM_AUCTION:
                        _char.buyItemAuction(mss);
                        break;

                    case CMD.DOI_OPTION:
                        _char.dichChuyen(mss);
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;

                    case CMD.TINH_LUYEN:
                        _char.tinhLuyen(mss);
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;

                    case CMD.LUYEN_THACH:
                        _char.luyenThach(mss);
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;

                    case CMD.CHAT_MAP:
                        if (user != null && _char != null) {
                            _char.chatPublic(mss);
                        }
                        break;

                    case CMD.CHAT_PRIVATE:
                        _char.chatPrivate(mss);
                        break;

                    case CMD.CHAT_SERVER:
                        _char.chatGlobal(mss);
                        break;

                    case CMD.CHAT_CLAN:
                        _char.chatClan(mss);
                        break;

                    case CMD.CHAT_PARTY:
                        _char.chatParty(mss);
                        break;

                    case CMD.PLEASE_INPUT_PARTY:
                        _char.pleaseInputParty(mss);
                        break;

                    case CMD.ACCEPT_PLEASE_PARTY:
                        _char.acceptPleaseParty(mss);
                        break;

                    case CMD.MAP_CHANGE:
                        _char.requestChangeMap();
                        break;

                    case CMD.ME_THROW:
                        _char.throwItem(mss);
                        break;

                    case CMD.ITEMMAP_MYPICK:
                        _char.pickItem(mss);
                        break;

                    case CMD.ME_BACK:
                        _char.returnTownFromDead(mss);
                        break;

                    case CMD.ME_LIVE:
                        _char.wakeUpFromDead(mss);
                        break;

                    case CMD.PLAYER_MOVE:
                        _char.move(mss);
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;

                    case CMD.REQUEST_PLAYERS:
                        _char.requestCharInfo(mss);
                        break;

                    case CMD.ITEM_USE:
                        _char.useItem(mss);
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;

                    case CMD.ITEM_USE_CHANGEMAP:
                        _char.useItemChangeMap(mss);
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;

                    case CMD.ITEM_BUY:
                        _char.buyItem(mss);
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;

                    case CMD.ITEM_SALE:
                        _char.saleItem(mss);
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;

                    case CMD.ITEM_BODY_TO_BAG:
                        _char.itemBodyToBag(mss);
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;

                    case CMD.ITEM_BOX_TO_BAG:
                        _char.itemBoxToBag(mss);
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;

                    case CMD.ITEM_BAG_TO_BOX:
                        _char.itemBagToBox(mss);
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;

                    case CMD.UPGRADE:
                        _char.upgradeItem(mss);
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;

                    case CMD.SPLIT:
                        _char.splitItem(mss);
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;

                    case CMD.UPPEARL:
                        _char.upPearl(mss, true);
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;

                    case CMD.UPPEARL_LOCK:
                        _char.upPearl(mss, false);
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;

                    case CMD.ZONE_CHANGE:
                        _char.changeZone(mss);
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;

                    case CMD.MENU:
                        _char.menu(mss);
                        break;

                    case CMD.OPEN_UI_ZONE:
                        service.openUIZone();
                        break;

                    case CMD.OPEN_UI_MENU:
                        _char.openMenu(mss);
                        break;

                    case CMD.SKILL_SELECT:
                        _char.selectSkill(mss);
                        break;

                    case CMD.REQUEST_ITEM_INFO:
                        _char.requestItemInfo(mss);
                        break;

                    case CMD.TRADE_INVITE:
                        _char.tradeInvite(mss);
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;

                    case CMD.TRADE_INVITE_ACCEPT:
                        _char.acceptInviteTrade(mss);
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;

                    case CMD.TRADE_LOCK_ITEM:
                        _char.tradeItemLock(mss);
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;

                    case CMD.TRADE_ACCEPT:
                        _char.tradeAccept();
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;

                    case CMD.TASK_GET:
                        _char.getTask(mss);
                        break;

                    case CMD.TRADE_CANCEL:
                        _char.tradeClose();
                        break;

                    case CMD.FRIEND_INVITE:
                        _char.addFriend(mss);
                        break;

                    case CMD.PLAYER_ATTACK_NPC:
                        _char.attackMonster(mss);
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;

                    case CMD.PLAYER_ATTACK_PLAYER:
                        _char.attackCharacter(mss);
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;
                    case CMD.TEST_INVITE:
                        if (!_char.isTest) {
                            _char.testInvite(mss);
                        }
                        break;
                    case CMD.TEST_INVITE_ACCEPT:
                        _char.testAccept(mss);
                        break;

                    case CMD.ADD_CUU_SAT:
                        _char.addCuuSat(mss);
                        break;

                    case CMD.PLAYER_ATTACK_P_N:
                        _char.attackAllType(mss, 2);
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;

                    case CMD.PLAYER_ATTACK_N_P:
                        _char.attackAllType(mss, 1);
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;

                    case CMD.OPEN_TEXT_BOX_ID:
                        _char.input(mss);
                        break;

                    case CMD.VIEW_INFO:
                        _char.viewInfo(mss);
                        break;

                    case CMD.REQUEST_ITEM_PLAYER:
                        _char.requestItemChar(mss);
                        break;

                    case CMD.ITEM_MON_TO_BAG:
                        _char.itemMountToBag(mss);
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;

                    case CMD.PARTY_INVITE:
                        _char.addParty(mss);
                        break;

                    case CMD.PARTY_ACCEPT:
                        _char.addPartyAccept(mss);
                        break;

                    case CMD.PARTY_CANCEL:
                        _char.addPartyCancel(mss);
                        break;

                    case CMD.PARTY_OUT:
                        _char.outParty();
                        break;

                    case CMD.USE_SKILL_MY_BUFF:
                        _char.useSkillBuff(mss);
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;

                    default:
                        Log.debug("CMD: " + mss.getCommand());
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void newMessage(Message mss) {
        if (mss != null) {
            try {
                if (user == null || _char == null) {
                    return;
                }
                byte command = mss.reader().readByte();
                switch (command) {
                    case 0:
                        _char.cancelClan();
                        break;

                    default:
                        Log.debug(String.format("Client %d: newMessage: %d", client.id, command));
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void messageNotLogin(Message mss) {
        if (mss != null) {
            try {
                if (user != null) {
                    return;
                }
                byte command = mss.reader().readByte();
                switch (command) {
                    case CMD.LOGIN:
                        client.login(mss);
                        break;

                    case CMD.CLIENT_INFO:
                        client.setClientType(mss);
                        break;

                    default:
                        Log.debug(String.format("Client %d: messageNotLogin: %d", client.id, command));
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void messageNotMap(Message mss) {
        if (mss != null) {
            try {
                if (user == null || user.isCleaned) {
                    return;
                }
                byte command = mss.reader().readByte();
                switch (command) {
                    case CMD.SELECT_PLAYER:
                        if (_char == null) {
                            user.selectChar(mss);
                        }
                        break;

                    case CMD.SERVER_ADD_MOB:
                        byte type = mss.reader().readByte();
                        if (type == 0) {
                            service.sendImgEffectAuto(mss);
                        } else if (type == 1) {
                            service.sendEffectAutoData(mss);
                        }
                        break;

                    case CMD.CREATE_PLAYER:
                        if (_char == null) {
                            user.createCharacter(mss);
                        }
                        break;

                    case CMD.UPDATE_DATA:
                        service.updateData();
                        break;

                    case CMD.UPDATE_MAP:
                        service.updateMap();
                        break;

                    case CMD.UPDATE_SKILL:
                        service.updateSkill();
                        break;

                    case CMD.UPDATE_ITEM:
                        service.updateItem();
                        break;

                    case CMD.REQUEST_ICON:
                        service.requestIcon(mss);
                        break;

                    case CMD.REQUEST_CLAN_LOG:
                        if (_char != null) {
                            service.writeLog();
                        }
                        break;
                    case CMD.REQUEST_CLAN_INFO:
                        if (_char != null) {
                            service.requestClanInfo();
                        }
                        break;

                    case CMD.REQUEST_CLAN_MEMBER:
                        if (_char != null) {
                            service.requestClanMember();
                        }
                        break;

                    case CMD.REQUEST_CLAN_ITEM:
                        if (_char != null) {
                            service.requestClanItem();
                        }
                        break;

                    case CMD.REWARD_PB:
                        if (_char != null) {
                            _char.rewardDungeon();
                        }
                        break;

                    case CMD.REWARD_CT:
                        if (_char != null) {
                            _char.rewardCT();
                        }
                        break;

                    case CMD.REQUEST_MAPTEMPLATE:
                        if (_char != null) {
                            service.requestMapTemplate(mss);
                        }
                        break;

                    case CMD.REQUEST_NPCTEMPLATE:
                        if (_char != null) {
                            service.requestMobTemplate(mss);
                        }
                        break;

                    case CMD.CLIENT_OK:
                        this.client.clientOk();
                        break;

                    case CMD.CLAN_CHANGE_ALERT:
                        if (_char != null) {
                            _char.changeClanAlert(mss);
                        }
                        break;

                    case CMD.CLAN_CHANGE_TYPE:
                        if (_char != null) {
                            _char.changeClanType(mss);
                        }
                        break;

                    case CMD.CLAN_MOVEOUT_MEM:
                        if (_char != null) {
                            _char.moveOutClan(mss);
                        }
                        break;

                    case CMD.CLAN_OUT:
                        if (_char != null) {
                            _char.outClan();
                        }
                        break;

                    case CMD.CLAN_UP_LEVEL:
                        if (_char != null) {
                            _char.clanUpLevel();
                        }
                        break;

                    case CMD.INPUT_COIN_CLAN:
                        if (_char != null) {
                            _char.inputCoinClan(mss);
                        }
                        break;

                    case CMD.OUTPUT_COIN_CLAN:
                        if (_char != null) {
                            _char.outputCoinClan(mss);
                        }
                        break;

                    case CMD.INVITE_CLANDUN: //
                        _char.inviteTerritory(mss);
                        break;
                    case CMD.CLAN_USE_ITEM:
                        _char.clanUseItem(mss);
                        break;

                    case CMD.CONVERT_UPGRADE:
                        if (_char != null) {
                            _char.convertUpgrade(mss);
                        }
                        break;

                    case CMD.ITEM_SPLIT:
                        if (_char != null) {
                            _char.inputNumberSplit(mss);
                        }
                        break;

                    case CMD.LAT_HINH:
                        if (_char != null) {
                            _char.selectCard(mss);
                        }
                        break;

                    case CMD.OPEN_CLAN_ITEM:
                        if (_char != null) {
                            _char.unlockClanItem();
                        }
                        break;

                    case CMD.CLAN_SEND_ITEM:
                        if (_char != null) {
                            _char.sendClanItem(mss);
                        }
                        break;

                    default:
                        Log.debug(String.format("Client %d: messageNotMap: %d", client.id, command));
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void messageSubCommand(Message mss) {
        if (mss != null) {
            try {
                if (user == null || _char == null || user.isCleaned || _char.isCleaned) {
                    return;
                }

                byte command = mss.reader().readByte();
                switch (command) {

                    case CMD.CLAN_INVITE:
                        _char.clanInvite(mss);
                        break;

                    case CMD.BUFF_LIVE:
                        _char.hoiSinh(mss);
                        // _char.lastMessageSentAt = System.currentTimeMillis();
                        break;

                    case CMD.CLAN_ACCEPT_INVITE:
                        _char.acceptInviteClan(mss);
                        break;

                    case CMD.LOAD_RMS:
                        _char.loadSkillShortcut(mss);
                        break;

                    case CMD.SAVE_RMS:
                        _char.saveRms(mss);
                        break;

                    case CMD.CHANGE_TYPE_PK:
                        _char.changeTypePk(mss);
                        break;

                    case CMD.FRIEND_REMOVE:
                        _char.removeFriend(mss);
                        break;

                    case CMD.ENEMIES_REMOVE:
                        _char.removeEnemy(mss);
                        break;

                    case CMD.REQUEST_FRIEND:
                        service.requestFriend();
                        break;

                    case CMD.REQUEST_ENEMIES:
                        service.requestEnemy();
                        break;

                    case CMD.POTENTIAL_UP:
                        _char.upPotential(mss);
                        break;

                    case CMD.SKILL_UP:
                        _char.upSkill(mss);
                        break;

                    case CMD.BAG_SORT:
                        _char.bagSort();
                        break;

                    case CMD.BOX_SORT:
                        _char.boxSort();
                        break;

                    case CMD.BOX_COIN_IN:
                        _char.boxCoinIn(mss);
                        break;

                    case CMD.BOX_COIN_OUT:
                        _char.boxCoinOut(mss);
                        break;

                    case CMD.REQUEST_ITEM:
                        _char.requestItem(mss);
                        break;

                    case CMD.CREATE_PARTY:
                        _char.createGroup();
                        break;

                    case CMD.LOCK_PARTY:
                        _char.lockParty(mss);
                        break;

                    case CMD.FIND_PARTY:
                        _char.openFindParty();
                        break;

                    case CMD.MOVE_MEMBER:
                        _char.moveMember(mss);
                        break;

                    case CMD.CHANGE_TEAMLEADER:
                        _char.changeTeamLeader(mss);
                        break;

                    default:
                        Log.debug(String.format("Client %d: messageSubCommand: %d", client.id, command));
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionFail() {
        Log.debug(String.format("Client %d: Kết nối thất bại!", client.id));
    }

    @Override
    public void onDisconnected() {
        Log.debug(String.format("Client %d: Mất kết nối!", client.id));
    }

    @Override
    public void onConnectOK() {
        Log.debug(String.format("Client %d: Kết nối thành công!", client.id));
    }

}
