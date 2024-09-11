package com.nsoz.model;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.nsoz.ability.AbilityCustom;
import com.nsoz.ability.AbilityFromEquip;
import com.nsoz.ability.AbilityStrategy;
import com.nsoz.admin.AdminService;
import com.nsoz.bot.attack.AttackTarget;
import com.nsoz.bot.move.JaianMove;
import com.nsoz.bot.move.MoveToTarget;
import com.nsoz.bot.Bot;
import com.nsoz.bot.BotFactory;
import com.nsoz.clan.Clan;
import com.nsoz.clan.ClanDAO;
import com.nsoz.clan.Member;
import com.nsoz.constants.CMDConfirmPopup;
import com.nsoz.constants.CMDInputDialog;
import com.nsoz.constants.CMDMenu;
import com.nsoz.constants.ConstTime;
import com.nsoz.constants.ItemName;
import com.nsoz.constants.ItemOptionName;
import com.nsoz.constants.MapName;
import com.nsoz.constants.MobName;
import com.nsoz.constants.NpcName;
import com.nsoz.constants.SQLStatement;
import com.nsoz.constants.SkillName;
import com.nsoz.constants.TaskName;
import com.nsoz.convert.Converter;
import com.nsoz.db.jdbc.DbManager;
import com.nsoz.db.mongodb.MongoDbConnection;
import com.nsoz.effect.Effect;
import com.nsoz.effect.EffectManager;
import com.nsoz.event.Event;
import com.nsoz.event.Halloween;
import com.nsoz.event.KoroKing;
import com.nsoz.event.LunarNewYear;
import com.nsoz.event.Noel;
import com.nsoz.event.TrungThu;
import com.nsoz.event.eventpoint.EventPoint;
import com.nsoz.event.eventpoint.Point;
import com.nsoz.fashion.FashionCustom;
import com.nsoz.fashion.FashionFromEquip;
import com.nsoz.fashion.FashionStrategy;
import com.nsoz.item.Equip;
import com.nsoz.item.Item;
import com.nsoz.item.ItemManager;
import com.nsoz.item.ItemFactory;
import com.nsoz.item.ItemTemplate;
import com.nsoz.item.Mount;
import com.nsoz.lib.ImageMap;
import com.nsoz.lib.ParseData;
import com.nsoz.lib.RandomCollection;
import com.nsoz.map.*;
import com.nsoz.map.item.ItemMap;
import com.nsoz.map.item.Envelope;
import com.nsoz.map.item.GiftBox;
import com.nsoz.map.item.ItemMapFactory;
import com.nsoz.map.world.Arena;
import com.nsoz.map.world.CandyBattlefield;
import com.nsoz.map.world.Dungeon;
import com.nsoz.map.world.SevenBeasts;
import com.nsoz.map.world.Territory;
import com.nsoz.map.world.World;
import com.nsoz.map.zones.ArenaT;
import com.nsoz.map.zones.ChikatoyaTunnels;
import com.nsoz.map.zones.FujukaSanctuary;
import com.nsoz.map.zones.Gymnasium;
import com.nsoz.map.zones.HarunaGymnasium;
import com.nsoz.map.zones.HirosakiGymnasium;
import com.nsoz.map.zones.InoshishiCave;
import com.nsoz.map.zones.NymozCave;
import com.nsoz.map.zones.OokazaGymnasium;
import com.nsoz.map.zones.TalentShow;
import com.nsoz.map.zones.Zone;
import com.nsoz.mob.Mob;
import com.nsoz.mob.MobManager;
import com.nsoz.mob.MobTemplate;
import com.nsoz.model.Invite.PlayerInvite;
import com.nsoz.network.Controller;
import com.nsoz.network.Message;
import com.nsoz.network.Service;
import com.nsoz.npc.Npc;
import com.nsoz.option.ItemOption;
import com.nsoz.option.SkillOption;
import com.nsoz.party.Group;
import com.nsoz.party.MemberGroup;
import com.nsoz.server.Config;
import com.nsoz.server.Events;
import com.nsoz.server.GameData;
import com.nsoz.server.GlobalService;
import com.nsoz.server.Language;
import com.nsoz.server.LuckyDraw;
import com.nsoz.server.LuckyDrawManager;
import com.nsoz.server.Ranked;
import com.nsoz.server.Server;
import com.nsoz.server.ServerManager;
import com.nsoz.skill.Skill;
import com.nsoz.stall.Stall;
import com.nsoz.stall.StallManager;
import com.nsoz.store.ItemStore;
import com.nsoz.store.Store;
import com.nsoz.store.StoreManager;
import com.nsoz.task.GloryTask;
import com.nsoz.task.Task;
import com.nsoz.task.TaskFactory;
import com.nsoz.task.TaskOrder;
import com.nsoz.task.TaskTemplate;
import com.nsoz.thiendia.Ranking;
import com.nsoz.thiendia.ThienDiaData;
import com.nsoz.thiendia.ThienDiaManager;
import com.nsoz.util.Log;
import com.nsoz.util.NinjaUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.DateUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Char {

    Config serverConfig = Config.getInstance();
    public static HashMap<String, Lock> lockGiftCodes = new HashMap<>();
    private static final long[] EXPIRE_DATE_OF_TIME = new long[]{259200000, 604800000};

    public static final byte PK_NORMAL = 0;

    public static final byte PK_NHOM = 1;

    public static final int TIME_CHANGE_ZONE_LIMIT = 2;

    public static final byte PK_PHE = 1;

    public static final byte PK_BANG = 2;

    public static final byte PK_DOSAT = 3;

    public static final byte PK_PHE1 = 4;

    public static final byte PK_PHE2 = 5;

    public static final byte PK_PHE3 = 6;

    public static final byte RUONG_DO = 0;
    public static final byte BO_SUU_TAP = 1;
    public static final byte CAI_TRANG = 2;
    public static final byte DOI_LONG_DEN_XU = 3;
    public static final byte DOI_LONG_DEN_LUONG = 4;
    public static final byte THANG_NGUONG_KHAI_HOA = 5;
    public static final byte THANG_CAP_KHAI_HOA = 6;

    public static int[] DONG_XU = {0, 0, 0, 0, 0};
    private static final String[] AUTO_PICK_ITEM = new String[]{"Không nhặt gì cả", "Nhặt tất cả",
        "Nhặt v.phẩm hữu ích", "Nhặt theo danh sách"};

    public static ExecutorService threadPool = Executors.newFixedThreadPool(500);

    public static Char findCharByName(String name) {
        Char _char = ServerManager.findCharByName(name);
        if (_char != null) {
            if (_char.clone != null && !_char.clone.isNhanBan) {
                return _char.clone;
            } else {
                return _char;
            }
        }
        return null;
    }

    public int id;
    public User user;
    public String name;
    public byte gender;
    public String school;
    public byte classId;
    public int level;
    public short head, original_head;
    public short weapon;
    public short body;
    public short leg;
    public short coat;
    public short glove;
    public long coin, coinInBox;
    public long coinMax;
    public long yen;
    public int hp, maxHP;
    public int mp, maxMP;
    public int damage, damage2, basicAttack;
    public int dameDown;
    public int exactly, miss, fatalDame, percentFatalDame;
    public int resFire, resIce, resWind, fatal, reactDame, sysUp, sysDown;
    public long exp, expDown;
    public byte hieuChien, typePk;
    public Clan clan;
    public int[] potential;
    public Vector<Skill> vSkill, vSkillFight, vSupportSkill;
    public double potentialPoint, skillPoint;
    public int pointUyDanh, pointNon, pointVuKhi, pointAo, pointLien, pointGangTay, pointNhan, pointQuan, pointNgocBoi,
            pointGiay, pointPhu, pointTinhTu, countFinishDay, countLoopBoss, limitTiemNangSo, limitKyNangSo,
            limitPhongLoi, limitBangHoa, countPB, pointPB, countUseItemGlory, countUseItemDungeo, countUseItemBeast,
            countGlory, countArenaT;
    public byte speed;
    public byte numberCellBag, numberCellBox;
    public Zone zone;
    public short mapId, x, y, preX, preY;
    public double eff5buffhp, eff5buffmp;
    public byte captcha = 0;
    public byte[] onKSkill;
    public byte[] onOSkill;
    public byte[] onCSkill;
    public Item[] bag, box, bijuu;
    public Equip[] equipment, fashion;
    public Trade trade;
    public Trader myTrade;
    public Trader partnerTrade;
    public int tayTiemNang, tayKyNang;
    public int[] options, optionsSupportSkill;
    public boolean[] haveOptions;
    public short saveCoordinate;
    public Mount[] mount;
    public Skill selectedSkill;
    public boolean isDead;
    public HashMap<String, Friend> friends, enemies;
    public byte numberUseExpanedBag;
    public byte numberUseExpanedBox;
    @Setter
    public Service service;
    public long lastLogoutTime;
    public long lastLoginTime;
    public long levelUpTime;
    public short taskId = 1;
    public Task taskMain;
    public byte nAnswer = 0;
    public long lastTimeChatGlobal;
    public long lastTimeTeleport;
    public Mob mobMe, mobBijuu;
    public Char enemy;
    @Getter
    private Group group;
    public boolean isCatchItem = false;
    public boolean isFailure = false;
    public Language language;
    public int testCharId = 0;
    public boolean isTest = false;
    public int killCharId = 0;
    public boolean isCuuSat = false;
    public boolean[] reward;
    public boolean receivedRewardPB;
    public int mapBeforeEnterPB;
    public int rewardPB;
    public String message = "";
    public long lastEffect = 0L;
    public int haoQuang = -1;
    public int honor; // vinh danh
    public GloryTask gloryTask;
    public ArrayList<TaskOrder> taskOrders;
    public War war;
    public WarClan warClan;
    public short ID_BODY = -1;
    public short ID_PP = -1;
    public short ID_HAIR = -1;
    public short ID_LEG = -1;
    public short ID_HORSE = -1;
    public short ID_NAME = -1;
    public short ID_RANK = -1;
    public short ID_MAT_NA = -1;
    public short ID_BIEN_HINH = -1;
    public short ID_WEA_PONE = -1;
    public boolean isHuman;
    public boolean isNhanBan;
    public Invite invite;
    public CloneChar clone;
    public int timeCountDown;
    public int huyHieu;
    public Lock lock = new ReentrantLock();
    public Lock lockItem = new ReentrantLock();
    private boolean isUnpaid;
    private boolean isGiftCodeUnpaid;
    public boolean isCleaned;
    public boolean isInvisible, isHide, isTNP, isKNP, isBiMa, isEffExp, isEffDameDown, isEffSkipResistance, isIce,
            isWind,
            isFire, isDontMove, isMiss, isShieldMana, isEffBong;
    public int incrExp, incrHP, incrDame, incrDame2, incrDame3, incrMiss, incrRes1, incrRes2, incrExactly;
    public Mob mob;
    public boolean online;
    // public long lastMessageSentAt = System.currentTimeMillis();
    public boolean isModeCreate;
    public boolean isModeAdd;
    public boolean isModeRemove;
    public boolean notReceivedExp;
    public boolean duatop;
    public boolean dhtp;
    public boolean dhdg;
    // chiến trường
    public int warPoint;
    public byte faction;
    public long time;
    public WarMember member;
    public boolean isRewarded;
    public boolean isViewListFriend;
    public int nKill;
    public int nDead;
    public long lastTimeReductionDame = 0L;
    public boolean isReductionDame = false;
    public int stoneItemId = -1;
    public long lastTimeOutClan = 0;
    private byte typeVXMM;
    @Setter
    private byte viewAuctionTab;
    @Getter
    @Setter
    private ConfirmPopup confirmPopup;

    @Getter
    public ArrayList<Menu> menus;
    @Getter
    @Setter
    private ArrayList<Item> maskBox;
    @Getter
    @Setter
    private ArrayList<Item> collectionBox;
    @Setter
    @Getter
    private byte commandBox;
    @Setter
    @Getter
    private Item mask;

    @Setter
    @Getter
    private int maskId;
    @Getter
    @Setter
    private boolean isLoadFinish;

    @Setter
    @Getter
    private boolean isInfected;

    @Getter
    @Setter
    private InputDialog input;

    @Getter
    public boolean isLockFire;

    @Setter
    private ArenaT arenaT;

    private int numberMissed;

    @Setter
    @Getter
    private boolean leading;

    @Setter
    @Getter
    private Bot escorted, escortedEvent;

    private List<Integer> listCanEnterMap;

    private int sevenBeastsID;
    private long typeSevenBeasts;

    @Getter
    @Setter
    private Room room;

    private List<World> worlds;// danh sách world đang tham gia

    private int countEnterFujukaSanctuary;

    @Getter
    @Setter
    private boolean isAutoPlay;

    @Getter
    @Setter
    private int range = -1;

    @Getter
    @Setter
    private int typeAutoPickItem = 0;

    @Getter
    @Setter
    private int groupIndex;
    public long nonCombatTime = 0;
    @Setter
    private EventPoint eventPoint;

    @Setter
    private FashionStrategy fashionStrategy;

    @Setter
    private AbilityStrategy abilityStrategy;
    @Getter
    @Setter
    private AbsSelectCard selectCard;
    private IChat chatGlobal;
    @Getter
    @Setter
    protected EffectManager em;
    protected boolean saving;

    public Char(int id) {
        this.id = id;
        this.options = new int[ItemManager.getInstance().getOptionSize()];
        this.isHuman = true;
        this.isNhanBan = false;
        this.menus = new ArrayList<>();
        this.listCanEnterMap = new ArrayList<>();
        this.worlds = new ArrayList<>();
        this.chatGlobal = new ChatGlobal(this);
        this.em = new EffectManager(this);
    }

    public EventPoint getEventPoint() {
        return eventPoint;
    }

    public void addWorld(World world) {
        synchronized (worlds) {
            if (worlds.stream().noneMatch(w -> w.getType() == world.getType())) {
                worlds.add(world);
                Log.debug("add worldType: " + world.getType());
            }
        }
    }

    public void removeWorld(byte type) {
        synchronized (worlds) {
            worlds.removeIf((t) -> t.getType() == type);
            Log.debug("remove worldType: " + type);
        }
    }

    public World findWorld(byte type) {
        synchronized (worlds) {
            for (World world : worlds) {
                if (world.getType() == type) {
                    return world;
                }
            }
            return null;
        }
    }

    public void initListCanEnterMap() {
        listCanEnterMap.clear();
        listCanEnterMap.add(MapName.LANG_TONE);
        if (taskId >= TaskName.NV_DIET_SEN_TRU_COC) {
            listCanEnterMap.add(MapName.DOI_FUMIMEN);
            listCanEnterMap.add(MapName.VACH_ICHIDAI);
        }
        if (taskId >= TaskName.NV_HAI_THUOC_CUU_NGUOI) {
            listCanEnterMap.add(MapName.THAC_KITAJIMA);
            listCanEnterMap.add(MapName.CHAN_THAC_KITAJIMA);
        }
        if (taskId >= TaskName.NV_KHAM_PHA_XA_LANG) {
            listCanEnterMap.add(MapName.DOI_KOKORO);
            listCanEnterMap.add(MapName.VACH_AINODAKE);
            listCanEnterMap.add(MapName.THUNG_LUNG_CHET);
            listCanEnterMap.add(MapName.KHU_LUYEN_TAP);
            listCanEnterMap.add(MapName.RUNG_GIA);
            listCanEnterMap.add(MapName.CANH_DONG_FUKI);
        }
        if (taskId >= TaskName.NV_TIM_HIEU_3_TRUONG) {
            listCanEnterMap.add(MapName.TRUONG_HARUNA);
            listCanEnterMap.add(MapName.TRUONG_HIROSAKI);
            listCanEnterMap.add(MapName.TRUONG_OOKAZA);
        }
        if (taskId >= TaskName.NV_BAI_HOC_DAU_TIEN) {
            listCanEnterMap.add(MapName.DONG_HACHI);
            listCanEnterMap.add(MapName.RUNG_DAO_SAKURA);
            listCanEnterMap.add(MapName.KY_TUC_XA_HARUNA);
            listCanEnterMap.add(MapName.CUA_HANG_AKA);
            listCanEnterMap.add(MapName.SONG_BANG_YAMATO);
            listCanEnterMap.add(MapName.HO_STUKI);
        }
        if (taskId >= TaskName.NV_THU_THAP_NGUYEN_LIEU) {
            listCanEnterMap.add(MapName.HANG_AKA);
            listCanEnterMap.add(MapName.CANH_DONG_HIYA);
            listCanEnterMap.add(MapName.RUNG_TRUC_UTRA);
        }
        if (taskId >= TaskName.NV_TRUYEN_TAI_TIN_TUC) {
            listCanEnterMap.add(MapName.RUNG_MISHIMA);
            listCanEnterMap.add(MapName.SONG_WATAMARO);
            listCanEnterMap.add(MapName.MUI_NURANURA);
            listCanEnterMap.add(MapName.SUOI_AKAGI);
            listCanEnterMap.add(MapName.BO_BIEN_OURA);
        }
        if (taskId >= TaskName.NV_REN_LUYEN_THE_LUC) {
            listCanEnterMap.add(MapName.HANG_HA);
            listCanEnterMap.add(MapName.HEM_NUI_TAKANA);
            listCanEnterMap.add(MapName.NGHIA_DIA_IZUKO);
        }
        if (taskId >= TaskName.NV_DUA_JAIAN_TRO_VE) {
            listCanEnterMap.add(MapName.LANG_CHAI);
            listCanEnterMap.add(MapName.CUA_BIEN_KAWAGUCHI);
            listCanEnterMap.add(MapName.RUNG_MOSHIO);
        }
        if (taskId >= TaskName.NV_TIM_NGUYEN_LIEU_LAM_THUOC) {
            listCanEnterMap.add(MapName.LANG_OSHIN);
            listCanEnterMap.add(MapName.RUNG_KANASHII);
            listCanEnterMap.add(MapName.LANG_KOJIN);
            listCanEnterMap.add(MapName.MIEU_KAMO);
        }
        if (taskId >= TaskName.NV_LAY_NUOC_HANG_SAU) {
            listCanEnterMap.add(MapName.DEN_AMATERASU);
        }
        if (taskId >= TaskName.NV_VUOT_QUA_THU_THACH) {
            listCanEnterMap.add(MapName.MIEU_OBOKO);
            listCanEnterMap.add(MapName.DAO_HEBI);
            listCanEnterMap.add(MapName.RUNG_TOGE);
            listCanEnterMap.add(MapName.SAN_SAU_MIEU_OBOKO);
        }
        if (taskId >= TaskName.NV_THU_THAP_CHIA_KHOA) {
            listCanEnterMap.add(MapName.HANG_MEIRO);
        }
        if (taskId >= TaskName.NV_TRUY_TIM_BAO_VAT) {
            listCanEnterMap.add(MapName.RUNG_KAPPA);
            listCanEnterMap.add(MapName.KHE_NUI_CHOROCHORO);
        }
        if (taskId >= TaskName.NV_REN_LUYEN) {
            listCanEnterMap.add(MapName.RUNG_GO_KOUJI);
        }
        if (taskId >= TaskName.NV_THU_THAP_TINH_THE_BANG) {
            listCanEnterMap.add(MapName.HANG_KUGYOU);
        }
        if (taskId >= TaskName.NV_THU_THAP_XAC_DOI_LUA) {
            listCanEnterMap.add(MapName.RUNG_AOKIGAHARA);
        }
        if (taskId >= TaskName.NV_KIEN_TRI_DIET_AC) {
            listCanEnterMap.add(MapName.LANG_CHAKUMI);
            listCanEnterMap.add(MapName.VACH_NUI_ITO);
        }
        if (taskId >= TaskName.NV_DU_TRU_LUONG_THUC) {
            listCanEnterMap.add(MapName.NUI_ANZEN);
            listCanEnterMap.add(MapName.NUI_ONTAKE);
            listCanEnterMap.add(MapName.THUNG_LUNG_TAIRA);
        }
        if (taskId >= TaskName.NV_TUAN_HOAN) { // NV_DIET_MA
            listCanEnterMap.add(MapName.LANG_SANZU);
            listCanEnterMap.add(MapName.KHU_DA_DO_AKAI);
            listCanEnterMap.add(MapName.LANG_ECHIGO);
            listCanEnterMap.add(MapName.KHU_DA_DO_AIKO);
            listCanEnterMap.add(MapName.MUI_HONE);
            listCanEnterMap.add(MapName.DINH_ICHIDAI);
            listCanEnterMap.add(MapName.DONG_KISEI);
            listCanEnterMap.add(MapName.NUI_HASHIGOTO);
            listCanEnterMap.add(MapName.HANG_CHI);
            listCanEnterMap.add(MapName.DINH_OKAMA);
            listCanEnterMap.add(MapName.HANG_NUI_KURAI);
            listCanEnterMap.add(MapName.DONG_TAMATAMO);
            listCanEnterMap.add(MapName.DEN_HARUMOTO);
            listCanEnterMap.add(MapName.PHONG_AN_OUNIO);
            listCanEnterMap.add(MapName.SAN_DEN_OROCHI);
            listCanEnterMap.add(MapName.NGOI_DEN_OROCHI);
            listCanEnterMap.add(MapName.SAN_SAU_DEN_OROCHI);
        }
    }

    public boolean isCanEnterMap(int map) {
        if (map == MapName.NHA_THI_DAU_HARUNA || map >= 72 || map == -1) {
            return true;
        }
        for (int m : listCanEnterMap) {
            if (m == map) {
                return true;
            }
        }
        return false;
    }

    public void setLanguage(Language lang) {
        this.language = lang;
    }

    public void escortEventFailed() {
        zone.out(escortedEvent);
        serverMessage(String.format("Hộ tống %s thất bại!", escortedEvent.name));
        setEscortedEvent(null);
        setLeading(false);
    }

    public void escortEventFinish() {
        zone.out(escortedEvent);
        serverMessage(String.format("Hộ tống %s thành công!", escortedEvent.name));
        setEscortedEvent(null);
        setLeading(false);
        // Item
        if (Event.isTrungThu()) {
            TrungThu trungThu = (TrungThu) Event.getEvent();
            trungThu.escortFinish(this);
        }
    }

    public void updateEverySecond() {
        if (mob != null) {
            mob.update();
        }
        if (Event.isEvent()) {
            if (escortedEvent != null) {
                if (Event.isTrungThu()) {
                    if (escortedEvent.isDead) {
                        escortEventFailed();
                    } else {
                        int x = 0;
                        int y = 0;
                        if (mapId == MapName.TRUONG_HIROSAKI) {
                            x = 828;
                            y = 312;
                        } else if (mapId == MapName.TRUONG_HARUNA) {
                            x = 1241;
                            y = 360;
                        } else if (mapId == MapName.TRUONG_OOKAZA) {
                            x = 848;
                            y = 648;
                        }
                        int d = NinjaUtils.getDistance(x, y, escortedEvent.x, escortedEvent.y);
                        if (d < 100) {
                            escortEventFinish();
                        }
                    }
                }
            }
        }
        if (!isNhanBan) {
            if (invite != null) {
                invite.update();
            }
            if (this.timeCountDown > 0) {
                this.timeCountDown--;
                if (this.timeCountDown == 0) {
                    if (isHuman) {
                        if (clone != null) {
                            if (clone.isNhanBan && !clone.isDead) {
                                clone.outZone();
                                clone.isDead = true;
                            }
                        }
                    } else {
                        this.isDead = true;
                        ((CloneChar) this).human.switchToMe();
                    }
                }
            }
        }
    }

    public void updateEveryHalfSecond() {
        if (isInfected() && (isVillage() || isSchool())) {
            cure();
        }
        //if(em != null){
        // for (byte j = 0; j < this.equipment.length; j++) {
        //   Item item = this.equipment[10];
        //    if (item != null &&  (item.options.size() < 7 ||item.id == ItemName.HOA_LONG)) {
        //        continue;
       //     }else{
        //      this.equipment[10] = null;
        //      if(item != null){
        //     this.equipment[10] = null;
        //     getService().deleteItemBody(item.id);
        //     return;
        //      }
            //serverDialog("Thu hồi item lỗi.");
        //    }
        //    return;
        //    }
        //}
        int hp = 0;
        int mp = 0;
        if (em != null) {
            em.update();
            if (!this.isDead) {
                List<Effect> effects = em.filter(e -> {
                    int id = e.template.id;
                    int type = e.template.type;
                    if (id == 36 || id == 42 || type == 0 || type == 12 || type == 13 || type == 4 || type == 17) {
                        return true;
                    }
                    return false;
                });
                if (!effects.isEmpty()) {
                    for (Effect eff : effects) {
                        if (eff.template.id == 36 || eff.template.id == 42) {
                            hp += eff.param;
                            mp += eff.param;
                        } else {
                            switch (eff.template.type) {
                                case 0:
                                case 12:
                                    int i = eff.param;
                                    hp += i;
                                    mp += i;
                                    break;
                                case 13:
                                    hp -= maxHP * 3 / 100;
                                    break;
                                case 4:
                                case 17:
                                    hp += eff.param;
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
            }

            if (this.options[136] > 0) {
                if (System.currentTimeMillis() - lastTimeReductionDame > 45000L) {
                    if ((int)NinjaUtils.nextInt(0, 100) < 10) {
                        lastTimeReductionDame = System.currentTimeMillis();
                        this.isReductionDame = true;
                    }
                } else if (isReductionDame && System.currentTimeMillis() - lastTimeReductionDame > 5000L) {
                    this.isReductionDame = false;
                }
            }

            hp += this.options[99];
            mp += this.options[99];
            if (isCool()) {
                hp /= 2;
                mp /= 2;
            }
            if (hp != 0 || mp != 0) {
                double preHP = this.hp;
                double preMP = this.mp;
                this.hp += hp;
                this.mp += mp;
                if (this.hp > this.maxHP) {
                    this.hp = this.maxHP;
                }
                if (this.mp > this.maxMP) {
                    this.mp = this.maxMP;
                }
                if (this.hp != preHP || this.mp != preMP) {
                    zone.getService().loadHP(this);
                    getService().updateHp();
                    getService().updateMp();
                    if (this.hp <= 0) {
                        startDie();
                    }
                }
            }
            if(this.user != null){
             if(this.user.id == 1){
               zone.getService().addEffect(this, 211, 500, 500, 0);
              // zone.getService().addEffect(this, 206, 500, 500, 0); 
               //zone.getService().addEffect(this, 209, 500, 500, 0); 
           }   
                }
            if (fashion[13] != null) {
                if (fashion[13].id >= 877) {
                    if (getSys() == 1) {
                        zone.getService().addEffect(this, 24, 10, 5, 0);
                    } else if (getSys() == 2) {
                        zone.getService().addEffect(this, 22, 10, 5, 0);
                        zone.getService().addEffect(this, 23, 10, 5, 0);
                    } else if (getSys() == 3) {
                        zone.getService().addEffect(this, 26, 10, 5, 0);
                        zone.getService().addEffect(this, 27, 10, 5, 0);
                    }
                }
                if (fashion[13].id == 879) {
                    zone.getService().addEffect(this, 25, 10, 5, 0);
                }
            }
            if (fashion[15] != null) {
                
             if (fashion[15].id == 1116) {
                    zone.getService().addEffect(this, 206, 999, 99, 0);
                }
             if (fashion[15].id == 1117) {
                    zone.getService().addEffect(this, 202, 999, 999, 0);
                }
             if (fashion[15].id == 1118) {
                    zone.getService().addEffect(this, 203, 999, 999, 0);
                }
             if (fashion[15].id == 1119) {
                    zone.getService().addEffect(this, 204, 999, 99, 0);
                }
             if (fashion[15].id == 1120) {
                    zone.getService().addEffect(this, 205, 999, 999, 0);
                }
             if (fashion[15].id == 1121) {
                    zone.getService().addEffect(this, 207, 999, 999, 0);
                }
             if (fashion[15].id == 1122) {
                    zone.getService().addEffect(this, 192, 999, 999, 0);
                }
             if (fashion[15].id == 1123) {
                    zone.getService().addEffect(this, 189, 999, 999, 0);
                }
             if (fashion[15].id == 1124) {
                    zone.getService().addEffect(this, 193, 999, 999, 0);
                }
             if (fashion[15].id == 1125) {
                    zone.getService().addEffect(this, 194, 999, 999, 0);
                }
             if (fashion[15].id == 1126) {
                    zone.getService().addEffect(this, 196, 999, 999, 0);
                }
             if (fashion[15].id == 1127) {
                    zone.getService().addEffect(this, 197, 999, 999, 0);
                }
            }
            if (this.zone.tilemap.isNotAllowPkPoint() && this.hieuChien > 0) {
                short[] xy = NinjaUtils.getXY(this.saveCoordinate);
                setXY(xy[0], xy[1]);
                changeMap(this.saveCoordinate);
            }

        }

    }

    public void updateEveryFiveSecond() {
        if (isAutoPlay()) {
            int index = getIndexItemByIdInBag(ItemName.THIEN_BIEN_LENH);
            if (index == -1 || this.bag[index].isExpired()) {
                setAutoPlay(false);
                getService().turnOffAuto();
            }
        }
        if (!(isBot())) {
            if (this.coin < 0 || this.coinInBox < 0 || this.yen < 0 || user.gold < 0) {
                service.serverDialog("Tài khoản bị âm lượng, xu hoặc yên.");
                user.lock();
                return;
            }
        }
        if (!this.isDead) {
            double hp = this.options[30] + this.options[120];
            double mp = this.options[27] + this.options[119];

            if (isCool()) {
                hp /= 2;
                mp /= 2;
            }

            if (this.zone.tilemap.id == 167) {
                this.hp -= this.hp * 20 / 100;
                addHp(-this.hp * 20 / 100);
                zone.getService().chat(this.id, "Ngột ngạt quá, chết tiệt");
            }

            if (hp != 0 || mp != 0) {
                double preHP = this.hp;
                double preMP = this.mp;
                this.hp += hp;
                this.mp += mp;
                if (this.hp > this.maxHP) {
                    this.hp = this.maxHP;
                }
                if (this.mp > this.maxMP) {
                    this.mp = this.maxMP;
                }
                if (this.hp != preHP || this.mp != preMP) {
                    zone.getService().loadHP(this);
                    getService().updateHp();
                    getService().updateMp();
                    if (this.hp <= 0) {
                        startDie();
                        return;
                    }
                }
            }
            if (haoQuang > -1) {
                int sys = getSys();
                int effId = 0;
                switch (sys) {
                    case 0:
                    case 1:
                        if (haoQuang == 0) {
                            effId = 9;
                        }
                        if (haoQuang == 1) {
                            effId = 10;
                        }
                        if (haoQuang == 2) {
                            effId = 11;
                        }
                        break;

                    case 2:
                        if (haoQuang == 0) {
                            effId = 3;
                        }
                        if (haoQuang == 1) {
                            effId = 4;
                        }
                        if (haoQuang == 2) {
                            effId = 5;
                        }
                        break;

                    case 3:
                        if (haoQuang == 0) {
                            effId = 6;
                        }
                        if (haoQuang == 1) {
                            effId = 7;
                        }
                        if (haoQuang == 2) {
                            effId = 8;
                        }
                        break;
                }
                zone.getService().addEffect(this, effId, 5, 5, 0);
            }
            if (fashion[13] != null) {
                if (fashion[13].id >= 877) {
                    if (getSys() == 1) {
                        zone.getService().addEffect(this, 24, 10, 5, 0);
                    } else if (getSys() == 2) {
                        zone.getService().addEffect(this, 22, 10, 5, 0);
                        zone.getService().addEffect(this, 23, 10, 5, 0);
                    } else if (getSys() == 3) {
                        zone.getService().addEffect(this, 26, 10, 5, 0);
                        zone.getService().addEffect(this, 27, 10, 5, 0);
                    }
                }
                if (fashion[13].id == 879) {
                    zone.getService().addEffect(this, 25, 10, 5, 0);
                }
            }
            if (fashion[15] != null) {
                
             if (fashion[15].id == 1116) {
                    zone.getService().addEffect(this, 206, 999, 99, 0);
                }
             if (fashion[15].id == 1117) {
                    zone.getService().addEffect(this, 202, 999, 999, 0);
                }
             if (fashion[15].id == 1118) {
                    zone.getService().addEffect(this, 203, 999, 999, 0);
                }
             if (fashion[15].id == 1119) {
                    zone.getService().addEffect(this, 204, 999, 99, 0);
                }
             if (fashion[15].id == 1120) {
                    zone.getService().addEffect(this, 205, 999, 999, 0);
                }
             if (fashion[15].id == 1121) {
                    zone.getService().addEffect(this, 207, 999, 999, 0);
                }
             if (fashion[15].id == 1122) {
                    zone.getService().addEffect(this, 192, 999, 999, 0);
                }
             if (fashion[15].id == 1123) {
                    zone.getService().addEffect(this, 189, 999, 999, 0);
                }
             if (fashion[15].id == 1124) {
                    zone.getService().addEffect(this, 193, 999, 999, 0);
                }
             if (fashion[15].id == 1125) {
                    zone.getService().addEffect(this, 194, 999, 999, 0);
                }
             if (fashion[15].id == 1126) {
                    zone.getService().addEffect(this, 196, 999, 999, 0);
                }
             if (fashion[15].id == 1127) {
                    zone.getService().addEffect(this, 197, 999, 999, 0);
                }
            }
            // get monsters within 2m
            if (this.options[134] > 0 || this.options[176] > 0 ) {
                int rand = (int)NinjaUtils.nextInt(100);
                int percentage = this.options[134] > 0 ? this.options[134] : this.options[176];
                if (rand < percentage) {
                    List<Mob> mobs = getMonsterByDistance(200);
                    for (Mob mob : mobs) {
                        if(!mob.isBoss){
                        double dameHit = mob.hp * 30 / 100;
                        mob.addHp(-dameHit);
                        if (mob.hp <= 0) {
                            mob.die();
                        }
                        zone.getService().attackMonster(dameHit, false, mob);
                        zone.getService().addEffect(mob, 65, 5, 5, 0);
                        }
                    }
                }
                if (this.options[135] > 0) {
                    if (rand < this.options[135]) {
                        List<Char> chars = getCharByDistance(200);
                        for (Char c : chars) {
                            double dameHit = c.hp * 20 / 100;
                            c.addHp(-dameHit);
                            if (c.hp <= 0) {
                                c.startDie();
                            }
                            zone.getService().attackCharacter(dameHit, 0, c);
                            zone.getService().addEffect(c, 64, 5, 5, 0);
                        }
                    }
                }
            }
            if (this.options[173] > 0) {
                int rand = (int)NinjaUtils.nextInt(100);
                if (rand < this.options[173]) {
                    List<Mob> mobs = getMonsterByDistance(200);
                    for (Mob mob : mobs) {
                        if(!mob.isBoss){
                        double dameHit = mob.hp * 50 / 100;
                        mob.addHp(-dameHit);
                        if (mob.hp <= 0) {
                            mob.die();
                        }
                        zone.getService().attackMonster(dameHit, false, mob);
                        zone.getService().addEffect(mob, 104, 5, 5, 0);
                        }
                    }
                }
            }
                if (this.options[175] > 0) {
                    int rand2 = (int)NinjaUtils.nextInt(100);
                    if (rand2 < this.options[175]) {
                        List<Char> chars = getCharByDistance(200);
                        for (Char c : chars) {
                            double hpchar = c.hp /100 *50;
                            double dameHit = c.hp * 30 / 100;
                            if(c.hp < hpchar){
                            c.addHp(+dameHit);
                            }
                            if (c.hp <= 0) {
                                c.startDie();
                            }
                            zone.getService().attackCharacter(dameHit, 0, c);
                            zone.getService().addEffect(c, 119, 5, 5, 0);
                        }
                      }
                    }
                }
            }
    

    public List<Mob> getMonsterByDistance(int distance) {
        ArrayList<Mob> monsters = new ArrayList<>();
        List<Mob> mobs = zone.getMonsters();
        for (Mob mob : mobs) {
            int mobDistance = NinjaUtils.getDistance(mob.x, mob.y, this.x, this.y);
            if (mobDistance <= distance) {
                monsters.add(mob);
            }
        }
        return monsters;
    }

    public List<Char> getCharByDistance(int distance) {
        ArrayList<Char> characters = new ArrayList<>();
        if (isLang() || isSchool()) {
            return characters;
        }
        List<Char> chars = zone.getChars();
        for (Char c : chars) {
            int charDistance = NinjaUtils.getDistance(c.x, c.y, this.x, this.y);
            if (charDistance <= distance && isMeCanAttackOtherPlayer(c)) {
                characters.add(c);
            }
        }
        return characters;
    }

    public short[] getFashion() {
        short[] thoiTrang = new short[10];
        thoiTrang[0] = this.ID_HAIR;
        thoiTrang[1] = this.ID_BODY;
        thoiTrang[2] = this.ID_LEG;
        thoiTrang[3] = this.ID_WEA_PONE;
        thoiTrang[4] = this.ID_PP;
        thoiTrang[5] = this.ID_NAME;
        thoiTrang[6] = this.ID_HORSE;
        thoiTrang[7] = this.ID_RANK;
        thoiTrang[8] = this.ID_MAT_NA;
        thoiTrang[9] = this.ID_BIEN_HINH;
        return thoiTrang;
    }

    public void updateHPMount(int hp) {
        Mount mount = this.mount[4];
        if (mount != null) {
            for (ItemOption option : mount.options) {
                if (option.optionTemplate.id == 66) {
                    option.param += hp;
                    if (option.param > 1000) {
                        option.param = 1000;
                    }
                    if (option.param < 0) {
                        option.param = 0;
                    }
                    break;
                }
            }
        }
    }

    public void upSkill(Message ms) {
        try {
            if (this.isDead) {
                return;
            }
            int templateId = ms.reader().readUnsignedShort();
            int point = ms.reader().readUnsignedByte();
            if (point > this.skillPoint || point < 0) {
                return;
            }
            int size = vSkill.size();
            for (int i = 0; i < size; i++) {
                if (vSkill.get(i).template.id == templateId) {
                    Skill check = vSkill.get(i);
                    int maxPoint = check.template.maxPoint;
                    if (check.point == maxPoint) {
                        serverDialog("Kỹ năng đã đạt cấp tối đa.");
                        return;
                    }
                    if (check.point + point > maxPoint) {
                        serverDialog("Kỹ năng chỉ có thể lên cấp tối đa là " + maxPoint);
                        return;
                    }
                    Skill skill = GameData.getInstance().getSkill(this.classId, templateId,
                            vSkill.get(i).point + point);
                    if (skill.level > this.level) {
                        serverDialog("Trình độ không đủ yêu cầu.");
                        return;
                    }
                    if (templateId >= 67 && templateId <= 72) {
                        if (clone != null) {
                            if (skill.point > clone.level) {
                                serverDialog("Trình độ của phân thân không đủ yêu cầu.");
                                return;
                            }
                        } else {
                            serverDialog("Hãy triệu hồi phân thân.");
                            return;
                        }
                    }
                    vSkill.set(i, skill);
                    if (skill.template.type == Skill.SKILL_AUTO_USE) {
                        for (int j = 0; j < vSupportSkill.size(); j++) {
                            if (vSupportSkill.get(j).template.id == templateId) {
                                vSupportSkill.set(j, skill);
                            }
                        }
                    } else if ((skill.template.type == Skill.SKILL_CLICK_USE_ATTACK
                            || skill.template.type == Skill.SKILL_CLICK_LIVE
                            || skill.template.type == Skill.SKILL_CLICK_USE_BUFF
                            || skill.template.type == Skill.SKILL_CLICK_NPC)
                            && (skill.template.maxPoint == 0 || (skill.template.maxPoint > 0 && skill.point > 0))) {

                        for (int j = 0; j < vSkillFight.size(); j++) {
                            if (vSkillFight.get(j).template.id == templateId) {
                                vSkillFight.set(j, skill);
                            }
                        }
                    }
                    for (int j = 0; j < this.onOSkill.length; j++) {
                        if (onOSkill[j] == templateId) {
                            onOSkill[j] = (byte) skill.template.id;
                        }
                    }
                    for (int j = 0; j < this.onKSkill.length; j++) {
                        if (onKSkill[j] == templateId) {
                            onKSkill[j] = (byte) skill.template.id;
                        }
                    }
                    if (onCSkill != null) {
                        for (int j = 0; j < this.onCSkill.length; j++) {
                            if (onCSkill[j] == templateId) {
                                onCSkill[j] = (byte) skill.template.id;
                            }
                        }
                    }
                    this.skillPoint -= point;
                    setAbility();
                    // this.hp = this.maxHP;
                    // this.mp = this.maxMP;
                    getService().updateHp();
                    getService().updateMp();
                    if (taskId == TaskName.NV_GIA_TANG_SUC_MANH && taskMain != null && taskMain.index == 1) {
                        taskNext();
                    }
                    getService().loadSkill();
                    byte type = 0;
                    if (!isHuman) {
                        type = 1;
                    }
                    getService().sendSkillShortcut("OSkill", onOSkill, type);
                    getService().sendSkillShortcut("KSkill", onKSkill, type);
                    if (onCSkill != null) {
                        getService().sendSkillShortcut("CSkill", onCSkill, type);
                    }
                    break;
                }
            }
        } catch (Exception ex) {
            Log.error("upskill err: " + ex.getMessage(), ex);
            serverDialog(ex.getMessage());
        }
    }

    public void upPotential(Message ms) {
        try {
            if (this.isDead) {
                return;
            }
            int index = ms.reader().readUnsignedByte();
            int point = ms.reader().readUnsignedShort();
            if (point <= 0 || index < 0 || index > 4) {
                return;
            }
            if (point > this.potentialPoint) {
                serverDialog("Bạn không đủ điểm.");
                return;
            }
            this.potential[index] += point;
            setAbility();
            // this.hp = this.maxHP;
            // this.mp = this.maxMP;
            getService().updateHp();
            getService().updateMp();
            this.potentialPoint -= point;
            if (taskId == TaskName.NV_GIA_TANG_SUC_MANH && taskMain != null && taskMain.index == 2) {
                taskNext();
            }
            getService().updatePotential();
        } catch (Exception ex) {
            Log.error("up potential err: " + ex.getMessage(), ex);
            serverDialog(ex.getMessage());
        }
    }

    public void unlockClanItem() {
        if (this.clan != null) {
            if (this.clan.getMemberByName(this.name).getType() == Clan.TYPE_TOCTRUONG) {
                int[] coin = {1000000, 5000000, 10000000, 20000000, 30000000};
                if (clan.itemLevel == 5) {
                    return;
                }
                int t = (clan.itemLevel + 1) * 5;
                if (clan.level >= t) {
                    int c = clan.coin;
                    int fc = coin[clan.itemLevel];
                    if (c >= fc) {
                        clan.itemLevel++;
                        clan.addCoin(-fc);
                        clan.getClanService().requestClanInfo();
                    } else {
                        serverMessage("Ngân quỹ không đủ.");
                    }
                } else {
                    serverMessage("Cấp độ gia tộc không đạt yêu cầu.");
                }
            } else {
                serverDialog("Bạn không có quyền này.");
            }
        }
    }

    public void acceptInviteClan(Message ms) {
        try {
            if (clan != null) {
                return;
            }
            int charId = ms.reader().readInt();
            Char _char = zone.findCharById(charId);
            if (_char != null && _char.clan != null) {
                PlayerInvite c = this.invite.findCharInvite(Invite.GIA_TOC, _char.id);
                if (c == null) {
                    serverDialog("Đã hết thời gian chấp nhận vào gia tộc.");
                    return;
                }
                Clan clan = _char.clan;
                List<Member> members = clan.memberDAO.getAll();
                if (members.size() < clan.getMemberMax()) {
                    this.clan = clan;
                    Member mem = Member.builder()
                            .classId(this.classId)
                            .level(this.level)
                            .type(Clan.TYPE_NORMAl)
                            .name(this.name)
                            .pointClan(0)
                            .pointClanWeek(0)
                            .build();
                    mem.setChar(this);
                    mem.setOnline(true);
                    clan.memberDAO.save(mem);
                    zone.getService().acceptInviteClan(this);
                    serverMessage("Bạn đã gia nhập vào gia tộc " + this.clan.getName());
                } else {
                    serverDialog("Gia tộc đã đủ thành viên.");
                }
            } else {
                serverDialog("Người mời đã rời khỏi khu vực.");
            }
        } catch (Exception ex) {
            Log.error("accept ivite clan err: " + ex.getMessage(), ex);
            serverDialog(ex.getMessage());
        }
    }

    public Friend[] getFriends() {
        if (this.friends == null) {
            return new Friend[0];
        }
        return this.friends.values().toArray(new Friend[this.friends.size()]);
    }

    public Friend[] getEnemies() {
        if (this.enemies == null) {
            return new Friend[0];
        }
        return this.enemies.values().toArray(new Friend[this.enemies.size()]);
    }

    public void removeItem(int index, int quantity, boolean isUpdate) {
        try {
            if (bag[index] != null) {
                bag[index].reduce(quantity);
                if (!bag[index].has()) {
                    bag[index] = null;
                    if (isUpdate) {
                        getService().removeItem(index);
                    }
                } else if (isUpdate) {
                    getService().useItemUpToUp(index, quantity);
                }
            }
        } catch (Exception ex) {
            Log.error("remove item: " + ex.getMessage(), ex);
            serverDialog(ex.getMessage());
        }
    }

    public boolean checkItemExist(Item item) {
        for (Item item1 : this.bag) {
            if (item1 != null && item1.id == item.id) {
                return true;
            }
        }
        return false;
    }

    public int getIndexItemByIdInBag(int id, boolean isLock) {
        for (Item item : this.bag) {
            if (item != null && item.id == id && item.isLock == isLock) {
                return item.index;
            }
        }
        return -1;
    }

    public int getIndexItemByIdInBag(int id) {
        for (Item item : this.bag) {
            if (item != null && item.id == id) {
                return item.index;
            }
        }
        return -1;
    }

    public List<Item> getListItemByID(int... ids) {
        List<Item> list = new ArrayList<>();
        for (Item item : this.bag) {
            for (int id : ids) {
                if (item != null && item.id == id) {
                    list.add(item);
                    break;
                }
            }
        }
        return list;
    }

    public int getIndexItemByIdInBox(int id, boolean isLock) {
        for (Item item : this.box) {
            if (item != null && item.id == id && item.isLock == isLock) {
                return item.index;
            }
        }
        return -1;
    }

    public int getQuantityItemById(int id) {
        int number = 0;
        try {
            for (byte i = 0; i < this.bag.length; i++) {
                Item item = this.bag[i];
                if (item != null && item.id == id) {
                    number += item.getQuantity();
                }
            }
        } catch (Exception e) {
        }
        return number;
    }

    public void warningClone() {
        serverMessage("Bạn đang trong chế độ thứ thân không thể dùng được chức năng này.");
    }

    public void warningTrade() {
        serverDialog("Không thể thực hiện khi đang giao dịch.");
    }

    public void warningBagFull() {
        serverDialog(language.getString("BAG_FULL"));
    }

    public void useItemChangeMap(Message ms) {
        try {
            if (!isHuman) {
                warningClone();
                return;
            }
            if (isDead) {
                serverDialog("Không thể sử dụng vật phẩm ở trạng thái này.");
                return;
            }
            if (zone.tilemap.isNotChangeMap()) {
                serverMessage("Không thể sử dụng tính năng này tại đây.");
                return;
            }
            byte indexUI = ms.reader().readByte();
            byte indexMenu = ms.reader().readByte();
            if (bag[indexUI] != null && (bag[indexUI].id == 35 || bag[indexUI].id == 37)) {
                short map = (new short[]{1, 27, 72, 10, 17, 22, 32, 38, 43, 48})[indexMenu];
                if (isCanEnterMap(map)) {
                    short[] xy = NinjaUtils.getXY(map);
                    setXY(xy[0], xy[1]);
                    changeMap(map);
                    if (bag[indexUI].id == 35 || (bag[indexUI].id == 37 && bag[indexUI].hasExpire()
                            && bag[indexUI].expire < (new Date()).getTime())) {
                        removeItem(indexUI, 1, true);
                    }
                } else {
                    getService().resetPoint();
                    serverDialog("Bạn chưa thể đến khu vực này. Hày hoàn thành nhiệm vụ trước.");
                }
            }
        } catch (Exception ex) {
            Log.error("use item change map err: " + ex.getMessage(), ex);
            serverDialog(ex.getMessage());
        }
    }

    public void useMount(Item item) {
        byte indexUI = (byte) item.index;
        byte index = (byte) (item.template.type - 29);
        if (index != 4) {
            if (this.mount[4] == null) {
                serverDialog("Chưa sử dụng thú cưỡi.");
                return;
            }
            if (this.mount[4].id == 485 || this.mount[4].id == 524) {
                if (item.id != 486 && item.id != 487 && item.id != 488 && item.id != 489) {
                    serverDialog("Trang bị xe không phù hợp.");
                    return;
                }
            } else if (this.mount[4].id == 443 || this.mount[4].id == 523 || this.mount[4].id == 776
                    || this.mount[4].id == 777 || this.mount[4].id == 798 || this.mount[4].id == 830
                    || this.mount[4].id == 850) {
                if (item.id != 439 && item.id != 440 && item.id != 441 && item.id != 442) {
                    serverDialog("Trang bị thú không phù hợp.");
                    return;
                }
            } else {
                serverDialog(String.format("%s không thể sử dụng trang bị.", mount[4].template.name));
                return;
            }
        } else {
            if (this.mount[0] != null || this.mount[1] != null || this.mount[2] != null || this.mount[3] != null) {
                serverDialog("Bạn cần phải tháo trang bị thú cưỡi đang sử dụng.");
                return;
            }
        }

        if (mount[index] != null) {
            Item it = Converter.getInstance().toItem(this.mount[index]);
            it.index = indexUI;
            it.isLock = true;
            this.bag[indexUI] = it;
        } else {
            this.bag[indexUI] = null;
        }
        Mount mount = Converter.getInstance().toMount(item);
        mount.isLock = true;
        if (index == 4 && mount.options.size() == 2) {
            mount.randomOptionMount();
        }
        this.mount[index] = mount;
        if (index == 4) {
            this.mount[4].fixMount();
        }
        getService().useItem(indexUI);
        zone.getService().loadMount(this);
        setFashion();
        setAbility();
    }

    public void useEquipment(Item item) {
        byte indexUI = (byte) item.index;
        byte index = item.template.type;
        if (item.template.fashion > -1 && item.id != 797 && item.id != ItemName.PET_UNG_LONG) {
            if (fashion[index] != null) {
                Item it = Converter.getInstance().toItem(fashion[index]);
                it.index = indexUI;
                it.isLock = true;
                bag[indexUI] = it;
            } else {
                bag[indexUI] = null;
            }
            Equip equip = Converter.getInstance().toEquip(item);
            equip.isLock = true;
            fashion[index] = equip;

            if (equip != null
                    && ((equip.id >= 814 && equip.id <= 818) || equip.id == ItemName.KHAU_TRANG
                    || equip.id == ItemName.NHAT_TU_LAM_PHONG || equip.id == ItemName.THIEN_NGUYET_CHI_NU
                    || equip.id == ItemName.SHIRAIJI || equip.id == ItemName.HAJIRO
                    || equip.id == ItemName.AO_NGU_THAN || equip.id == ItemName.AO_TAN_THOI
                    || (equip.id >= ItemName.KIEM_TB2 && equip.id <= ItemName.DG_TB2))
                    && equip.options.isEmpty()) { // random chỉ số
                equip.randomOption();
            }
        } else {
            if (item.template.isTypeWeapon()) {
                if (((this.classId == 0 || this.classId == 1) && !item.template.isKiem())
                        || ((this.classId == 2) && !item.template.isTieu())
                        || ((this.classId == 3) && !item.template.isKunai())
                        || ((this.classId == 4) && !item.template.isCung())
                        || ((this.classId == 5) && !item.template.isDao())
                        || ((this.classId == 6) && !item.template.isQuat())) {
                    serverDialog("Vũ khí không thích hợp.");
                    return;
                }
            }
            if (classId > 0 && item.id == 194) {
                serverDialog("Vũ khí không thích hợp.");
                return;
            }
            if (index == ItemTemplate.TYPE_AOCHOANG) {
                int sys = getSys();
                if (item.id == 420 && sys != 1) {
                    return;
                }
                if (item.id == 421 && sys != 2) {
                    return;
                }
                if (item.id == 422 && sys != 3) {
                    return;
                }
            }
            if (index == ItemTemplate.TYPE_BIKIP) {
                if ((this.classId == 1 && item.id != ItemName.BI_KIP_KIEM_THUAT)
                        || (this.classId == 2 && item.id != ItemName.BI_KIP_TIEU_THUAT)
                        || (this.classId == 3 && item.id != ItemName.BI_KIP_KUNAI)
                        || (this.classId == 4 && item.id != ItemName.BI_KIP_CUNG)
                        || (this.classId == 5 && item.id != ItemName.BI_KIP_DAO)
                        || (this.classId == 6 && item.id != ItemName.BI_KIP_QUAT) || this.classId == 0) {
                    return;
                }
            }
            if (equipment[index] != null) {
                Item it = Converter.getInstance().toItem(equipment[index]);
                it.index = indexUI;
                it.isLock = true;
                bag[indexUI] = it;
            } else {
                bag[indexUI] = null;
            }

            Equip equip = Converter.getInstance().toEquip(item);
            equip.isLock = true;
            equipment[index] = equip;
            if (equip != null && equip.id == ItemName.PET_UNG_LONG && equip.options.size() == 2) { // random chỉ số
                equip.randomOption();
            }
            if (taskMain != null) {
                if (taskId == TaskName.NV_LAN_DAU_DUNG_KIEM && taskMain.index == 0 && item.template.isTypeWeapon()) {
                    taskNext();
                } else if (taskId == TaskName.NV_NANG_CAP_TRANG_BI) {
                    if (item.upgrade > 0) {
                        if (taskMain.index == 1 && item.template.isTypeWeapon()) {
                            taskNext();
                        } else if (taskMain.index == 2 && item.template.isTypeAdorn()) {
                            taskNext();
                        } else if (taskMain.index == 3 && item.template.isTypeClothe()) {
                            taskNext();
                        }
                    }
                }
            }
        }
        getService().useItem(indexUI);
        if (index == ItemTemplate.TYPE_THUNUOI && item.template.fashion == -1) {
            em.setEffectPet();
        }
        setAbility();
        setFashion();
    }

    public void returnToPreviousPostion(Runnable runnable) {
        setXY(this.preX, this.preY);
        getService().resetPoint();
        if (runnable != null) {
            runnable.run();
        }
    }

    public void setFashion() {
        short horse = this.ID_HORSE;
        short body = this.ID_BODY;
        short bienHinh = this.ID_BIEN_HINH;
        short leg = this.ID_LEG;
        short hair = this.ID_HAIR;
        short matNa = this.ID_MAT_NA;
        short name = this.ID_NAME;
        short rank = this.ID_RANK;
        short weapon2 = this.ID_WEA_PONE;
        short pp = this.ID_PP;
        short shirt = this.body;
        short pant = this.leg;
        short mask = this.head;
        short weapon = this.weapon;
        short coat = this.coat;
        short glove = this.glove;
        int honor = this.honor;
        Mob mob = this.mobMe;
        if (fashionStrategy != null) {
            fashionStrategy.set(this);
        }
        setMobMe();
        if (bijuu[4] != null) {
            setBijuu(bijuu[4].id);
        }
        if (zone != null) {

            if (shirt != this.body) {
                zone.getService().loadShirt(this);
            }
            if (pant != leg) {
                zone.getService().loadPant(this);
            }
            if (mask != this.head) {
                zone.getService().loadMask(this);
            }
            if (weapon != this.weapon) {
                zone.getService().loadWeapon(this);
            }
            if (coat != this.coat) {
                zone.getService().loadCoat(this);
            }
            if (glove != this.glove) {
                zone.getService().loadGlove(this);
            }
            zone.getService().updateInfoChar(this);
            getService().updateInfoMe();
            if (mob != this.mobMe) {
                zone.getService().loadPet(this);
            }
            if (honor != this.honor) {
                zone.getService().loadHonor(this);
            }
        }
    }

    public void ngocKham(Message ms) {
        lockItem.lock();
        try {
            if (!isHuman) {
                warningClone();
                return;
            }
            if (trade != null) {
                warningTrade();
                return;
            }
            byte type = ms.reader().readByte();
            if (type == 0) {
                khamNgoc(ms);
            } else if (type == 1) {
                luyenNgoc(ms);
            } else if (type == 2) {
                gotNgoc(ms);
            } else {
                thaoNgoc(ms);
            }
        } catch (Exception e) {
            Log.error("err: " + e.getMessage(), e);
        } finally {
            lockItem.unlock();
        }

    }

    public void khamNgoc(Message ms) {
        try {
            int indexEquip = ms.reader().readUnsignedByte();
            int indexNgoc = ms.reader().readUnsignedByte();
            if (indexEquip < 0 || indexEquip >= numberCellBag) {
                return;
            }
            if (indexNgoc < 0 || indexNgoc >= numberCellBag) {
                return;
            }
            Item equip = this.bag[indexEquip];
            if (equip == null) {
                serverDialog("Không tìm thấy trang bị này. Vui lòng đăng nhập lại để kiểm tra!");
                return;
            }
            if (!(equip.template.isTypeClothe() || equip.template.isTypeAdorn() || equip.template.isTypeWeapon())) {
                serverDialog("Vật phẩm này không thể khảm ngọc.");
                return;
            }
            Item ngoc = this.bag[indexNgoc];
            if (ngoc == null) {
                serverDialog("Không tìm thấy ngọc này. Vui lòng đăng nhập lại để kiểm tra!");
                return;
            }
            if (!ngoc.template.isTypeNgocKham()) {
                serverDialog("Vật phẩm này không phải ngọc.");
                return;
            }
            ArrayList<Item> crystals = new ArrayList<>();
            boolean[] list = new boolean[numberCellBag];
            while (ms.reader().available() > 0) {
                int indexCrystal = ms.reader().readUnsignedByte();
                if (indexCrystal < 0 || indexCrystal >= numberCellBag) {
                    continue;
                }
                if (!list[indexCrystal]) {
                    list[indexCrystal] = true;
                    Item crystal = this.bag[indexCrystal];
                    if (crystal != null && crystal.template.isTypeCrystal()) {
                        crystals.add(crystal);

                    }
                }
            }
            if (crystals.isEmpty()) {
                serverDialog("Hãy cho đá vào để khảm ngọc.");
                return;
            }
            int yenKham = 0;
            for (ItemOption option : ngoc.options) {
                if (option.optionTemplate.id == 123) {
                    yenKham = option.param;
                    break;
                }
            }
            long sum = NinjaUtils.sum(this.coin, this.yen);
            if (yenKham > sum) {
                serverDialog("Không đủ yên hoặc xu để khảm ngọc.");
                return;
            }
            for (Item itm : equip.gems) {
                if (itm.id == ngoc.id) {
                    serverDialog("Không được khảm ngọc cùng loại.");
                    return;
                }
            }
            History history = new History(this.id, History.KHAM_NGOC);
            history.setBefore(this.coin, user.gold, this.yen);
            history.addItem(equip);
            if (yenKham > this.yen) {
                yenKham -= this.yen;
                this.yen = 0;
                this.coin -= yenKham;
            } else {
                this.yen -= yenKham;
            }
            history.setAfter(this.coin, user.gold, this.yen);

            long total = 0;
            for (Item crystal : crystals) {
                total += GameData.UP_CRYSTAL[crystal.template.id];
            }
            int percent = (int) (total * 100 / GameData.UP_WEAPON[ngoc.upgrade]);
            if (percent > GameData.MAX_PERCENT[ngoc.upgrade]) {
                percent = GameData.MAX_PERCENT[ngoc.upgrade];
            }
            int rand = (int)NinjaUtils.nextInt(100);
            int type = 6;
            if (rand < percent) {
                equip.addGem(ngoc);
                removeItem(ngoc.index, 1, false);
                getService().itemInfo(equip, (byte) 3, (byte) equip.index);
                type = 5;
            }
            equip.isLock = true;
            history.addItem(equip);
            history.addItem(ngoc);
            for (Item item : crystals) {
                history.addItem(item);
                removeItem(item.index, 1, false);
            }
            history.setTime(System.currentTimeMillis());
            History.insert(history);
            getService().upgrade((byte) type, equip);
        } catch (Exception e) {
            Log.error("err: " + e.getMessage(), e);
        }
    }

    public void luyenNgoc(Message ms) {
        try {
            int index = ms.reader().readUnsignedByte();
            if (index < 0 || index >= numberCellBag) {
                return;
            }
            Item item = this.bag[index];
            if (item == null) {
                serverDialog("Có lỗi xảy ra. Vui lòng đăng nhập lại để kiểm tra!");
                return;
            }
            if (item.template.type != ItemTemplate.TYPE_NGOC_KHAM) {
                serverDialog("Không thể luyện ngọc.");
                return;
            }
            if (item.upgrade >= 10) {
                serverDialog(language.getString("CAN_NOT_LUYENNGOC"));
                return;
            }
            int expUp = 0;
            ArrayList<Item> items = new ArrayList<>();
            boolean[] list = new boolean[numberCellBag];
            while (ms.reader().available() > 0) {
                int ngocIndex = ms.reader().readUnsignedByte();
                if (ngocIndex < 0 || ngocIndex >= numberCellBag || ngocIndex == index) {
                    continue;
                }
                if (!list[ngocIndex]) {
                    list[ngocIndex] = true;
                    Item ngoc = this.bag[ngocIndex];
                    if (ngoc != null && ngoc.template.type == ItemTemplate.TYPE_NGOC_KHAM) {
                        expUp += GameData.NGOC_KHAM_EXP[ngoc.upgrade][1];
                        items.add(ngoc);
                    }
                }
            }

            if (expUp == 0) {
                serverDialog("Hãy cho ngọc vào để luyện.");
                return;
            }
            History history = new History(this.id, History.LUYEN_NGOC);
            history.setBefore(this.coin, user.gold, this.yen);
            history.setAfter(this.coin, user.gold, this.yen);
            history.addItem(item);
            int expNew = 0;
            for (ItemOption option : item.options) {
                if (option.optionTemplate.id == 104) {
                    expNew = option.param;
                    break;
                }
            }
            expNew += expUp;
            int upgradeOld = item.upgrade;
            int expMax = GameData.NGOC_KHAM_EXP[upgradeOld][0];
            if (expNew > expMax) {
                expNew -= expMax;
                item.upgrade++;
                for (ItemOption option : item.options) {
                    switch (option.optionTemplate.id) {
                        case 73:
                            // tấn công
                            if (option.param > 0) {
                                int[] paramUp = {0, 50, 100, 150, 200, 250, 300, 350, 400, 450};
                                option.param += paramUp[upgradeOld];
                            } else {
                                option.param -= 50;
                            }
                            break;
                        case 115:
                            // né đòn
                            if (option.param > 0) {
                                int[] paramUp = {0, 10, 20, 30, 40, 50, 60, 70, 80, 90};
                                option.param += paramUp[upgradeOld];
                            } else {
                                int[] paramUp = {0, 60, 40, 20, 20, 15, 15, 10, 10, 5};
                                option.param -= paramUp[upgradeOld];
                            }
                            break;
                        case 124:
                            // giảm trừ sát thương
                            if (option.param > 0) {
                                int[] paramUp = {0, 10, 15, 20, 25, 30, 35, 40, 45, 50};
                                option.param += paramUp[upgradeOld];
                            } else {
                                option.param -= 20;
                            }
                            break;
                        case 114:
                            // chí mạng
                            if (option.param > 0) {
                                int[] paramUp = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
                                option.param += paramUp[upgradeOld];
                            } else {
                                option.param -= 4;
                            }
                            break;
                        case 126:
                            // phản đòn
                            if (option.param > 0) {
                                int[] paramUp = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
                                option.param += paramUp[upgradeOld];
                            } else {
                                option.param -= 4;
                            }
                            break;
                        case 118:
                            // kháng tất cả
                            if (option.param > 0) {
                                int[] paramUp = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
                                option.param += paramUp[upgradeOld];
                            } else {
                                int[] paramUp = {0, 20, 20, 20, 15, 15, 15, 10, 10, 5};
                                option.param -= paramUp[upgradeOld];
                            }
                            break;
                        case 102:
                            // sát thương lên quái
                            if (option.param > 0) {
                                int[] paramUp = {0, 100, 200, 400, 600, 800, 1000, 1200, 1400, 1600};
                                option.param += paramUp[upgradeOld];
                            } else {
                                option.param -= 200;
                            }
                            break;
                        case 105:
                            // sát thương chí mạng
                            if (option.param > 0) {
                                int[] paramUp = {0, 100, 200, 300, 400, 500, 600, 700, 900, 1200};
                                option.param += paramUp[upgradeOld];
                            } else {
                                option.param -= 200;
                            }
                            break;
                        case 103:
                            // sát thương lên người
                            if (option.param > 0) {
                                int[] paramUp = {0, 100, 150, 160, 170, 200, 220, 250, 300, 350};
                                option.param += paramUp[upgradeOld];
                            } else {
                                option.param -= 150;
                            }
                            break;
                        case 121:
                            // kháng sát thương chí mạng
                            if (option.param > 0) {
                                int[] paramUp = {0, 1, 2, 2, 2, 3, 3, 3, 4, 5};
                                option.param += paramUp[upgradeOld];
                            } else {
                                option.param -= 4;
                            }
                            break;
                        case 117:
                        case 125:
                            // hp, mp tối đa
                            if (option.param > 0) {
                                int[] paramUp = {0, 100, 150, 200, 250, 300, 350, 400, 450, 500};
                                option.param += paramUp[upgradeOld];
                            } else {
                                option.param -= 150;
                            }
                            break;
                        case 116:
                            // chính xác
                            if (option.param > 0) {
                                int[] paramUp = {0, 50, 50, 100, 150, 150, 200, 200, 250, 300};
                                option.param += paramUp[upgradeOld];
                            } else {
                                option.param -= 100;
                            }
                            break;
                        case 119:
                        case 229:
                            // hồi phục hp,
                            // mp
                            if (option.param > 0) {
                                int[] paramUp = {0, 2, 4, 6, 8, 10, 12, 14, 16, 18};
                                option.param += paramUp[upgradeOld];
                            } else {
                                option.param -= 10;
                            }
                            break;
                        case 104:
                            option.param = expNew;
                            break;
                        case 123:
                            int[] giaKham = {800000, 1600000, 2400000, 3200000, 4800000, 7200000, 10800000, 15600000,
                                20100000, 28100000};
                            option.param = giaKham[item.upgrade - 1];
                            break;
                        default:
                            break;
                    }
                }
            }

            for (ItemOption option : item.options) {
                if (option.optionTemplate.id == 104) {
                    option.param = expNew;
                    break;
                }
            }
            item.isLock = true;
            history.addItem(item);
            for (Item itm : items) {
                history.addItem(itm);
                removeItem(itm.index, 1, false);
            }
            history.setTime(System.currentTimeMillis());
            History.insert(history);

            getService().ngocKham((byte) 1, item);
            getService().itemInfo(item, (byte) 3, (byte) index);
            if (item.upgrade > upgradeOld) {
                serverDialog("Đã luyện ngọc lên cấp " + item.upgrade);
            }
        } catch (Exception e) {
            Log.error("err: " + e.getMessage(), e);
        }
    }

    public void gotNgoc(Message ms) {
        try {
            int index = ms.reader().readUnsignedByte();
            if (index < 0 || index >= numberCellBag) {
                return;
            }
            Item item = this.bag[index];
            ;
            if (item != null) {
                int coin = GameData.COIN_GOT_NGOC[item.upgrade];
                if (this.coin < coin) {
                    serverDialog(language.getString("NOT_ENOUGH_COIN_GOTNGOC"));
                    return;
                }
                History history = new History(this.id, History.GOT_NGOC);
                history.setBefore(this.coin, user.gold, this.yen);
                addCoin(-coin);
                history.setAfter(this.coin, user.gold, this.yen);
                history.addItem(item);
                for (ItemOption itemOption : item.options) {
                    int param = itemOption.param;
                    if (param < -1) {
                        double down = (int)NinjaUtils.nextInt(1, Math.abs(param) - 1);
                        itemOption.param += down;
                    }
                }
                history.addItem(item);
                history.setTime(System.currentTimeMillis());
                History.insert(history);
                serverDialog(String.format("%s đã được gọt.", item.template.name));
                getService().itemInfo(item, (byte) 3, (byte) index);
            }
        } catch (Exception e) {
            Log.error("err: " + e.getMessage(), e);
        }
    }

    public void thaoNgoc(Message ms) {
        try {
            int index = ms.reader().readUnsignedByte();
            Item item = null;
            try {
                item = this.bag[index];
            } catch (Exception e) {
                serverDialog("Có lỗi xảy ra. Vui lòng đăng nhập lại để kiểm tra!");
                return;
            }
            if (item.gems.isEmpty()) {
                serverDialog("Trang bị này không có ngọc.");
                return;
            }
            if (item.gems.size() > getSlotNull()) {
                warningBagFull();
                return;
            }
            int yenThaoNgoc = 0;
            for (Item gem : item.gems) {
                yenThaoNgoc += Item.GIA_KHAM[gem.upgrade - 1] / 2;
            }
            if (yenThaoNgoc > NinjaUtils.sum(this.coin, this.yen)) {
                serverDialog("Không đủ yên hoặc xu để tháo ngọc.");
                return;
            }
            History history = new History(this.id, History.THAO_NGOC);
            history.setBefore(this.coin, user.gold, this.yen);
            if (yenThaoNgoc > this.yen) {
                yenThaoNgoc -= this.yen;
                this.yen = 0;
                this.coin -= yenThaoNgoc;
            } else {
                this.yen -= yenThaoNgoc;
            }
            history.setAfter(this.coin, user.gold, this.yen);
            history.addItem(item);
            ArrayList<Item> items = new ArrayList<>();
            for (Item itm : item.gems) {
                items.add(itm);
                addItemToBag(itm);
            }
            item.gems.clear();
            history.addItem(item);
            for (Item itm : items) {
                history.addItem(itm);
            }
            history.setTime(System.currentTimeMillis());
            History.insert(history);
            getService().ngocKham((byte) 3, null);
            getService().itemInfo(item, (byte) 3, (byte) index);
        } catch (Exception e) {
            Log.error("err: " + e.getMessage(), e);
        }
    }

    private void pieceTogether(Item item) {
        int newItemID = -1;
        switch (item.id) {
            case ItemName.MANH_NON_JIRAI_:
                newItemID = ItemName.NON_JIRAI;
                break;

            case ItemName.MANH_NON_JUMITO:
                newItemID = ItemName.NON_JUMITO;
                break;

            case ItemName.MANH_AO_JIRAI_:
                newItemID = ItemName.AO_JIRAI;
                break;

            case ItemName.MANH_AO_JUMITO:
                newItemID = ItemName.AO_JUMITO;
                break;

            case ItemName.MANH_GANG_TAY_JIRAI_:
                newItemID = ItemName.GANG_TAY_JIRAI;
                break;

            case ItemName.MANH_GANG_TAY_JUMITO:
                newItemID = ItemName.GANG_TAY_JUMITO;
                break;

            case ItemName.MANH_QUAN_JIRAI_:
                newItemID = ItemName.QUAN_JIRAI_;
                break;

            case ItemName.MANH_QUAN_JUMITO:
                newItemID = ItemName.QUAN_JUMITO;
                break;

            case ItemName.MANH_GIAY_JIRAI_:
                newItemID = ItemName.GIAY_JIRAI;
                break;

            case ItemName.MANH_GIAY_JUMITO:
                newItemID = ItemName.GIAY_JUMITO;
                break;

            case ItemName.MANH_NGOC_BOI_JIRAI_:
                newItemID = ItemName.NGOC_BOI_JIRAI;
                break;

            case ItemName.MANH_NGOC_BOI_JUMITO:
                newItemID = ItemName.NGOC_BOI_JUMITO;
                break;

            case ItemName.MANH_DAY_CHUYEN_JIRAI_:
                newItemID = ItemName.DAY_CHUYEN_JIRAI;
                break;

            case ItemName.MANH_DAY_CHUYEN_JUMITO:
                newItemID = ItemName.DAY_CHUYEN_JUMITO;
                break;

            case ItemName.MANH_NHAN_JIRAI_:
                newItemID = ItemName.NHAN_JIRAI;
                break;

            case ItemName.MANH_NHAN_JUMITO:
                newItemID = ItemName.NHAN_JUMITO;
                break;

            case ItemName.MANH_PHU_JIRAI_:
                newItemID = ItemName.PHU_JIRAI;
                break;

            case ItemName.MANH_PHU_JUMITO:
                newItemID = ItemName.PHU_JUMITO;
                break;

        }
        int quantityNeed = 0;
        Item itm = getItemInCollectionBox(newItemID);
        if (itm == null) {
            quantityNeed = 1000;
        } else {
            if (itm.upgrade >= 10) {
                getService()
                        .serverMessage(String.format("%s đã đạt cấp tối đa, không thể nâng cấp", itm.template.name));
                return;
            }
            quantityNeed = 1000 * (itm.upgrade + 1);
        }
        if (!item.has(quantityNeed)) {
            if (itm == null) {
                getService().serverMessage(String.format("Phải có đủ %d mảnh mới có thể chế tạo", quantityNeed));
            } else {
                getService().serverMessage(String.format("Phải có đủ %d mảnh mới có thể nâng cấp", quantityNeed));
            }
        } else {
            removeItem(item.index, quantityNeed, true);
            if (itm != null) {
                itm.upgrade++;
                getService().serverMessage(String.format("Chúc mừng bạn đã nâng cấp thành công %s lên cấp %d",
                        itm.template.name, itm.upgrade));
            } else {
                itm = ItemFactory.getInstance().newItem(newItemID);
                itm.upgrade = 1;
                itm.yen = 0;
                collectionBox.add(itm);
                getService().serverMessage(String.format("Chúc mừng bạn đã tạo thành công %s", itm.template.name));
            }
        }
    }

    public Item getItemInCollectionBox(int id) {
        for (Item item : collectionBox) {
            if (item != null && item.id == id) {
                return item;
            }
        }
        return null;
    }

    public void useItemTaskWait(Item item) {
        try {
            if (item.id == ItemName.TUAN_THU_LENH) {
                if (!Event.isNoel()) {
                    getService().endWait("Vật phẩm chỉ được sử dụng ở sự kiện Noel.");
                    return;
                }
                if (zone.tilemap.isNotSummon()) {
                    getService().endWait("Không thể triệu hồi ở đây.");
                    return;
                }
                if (this.mob == null) {
                    MobTemplate template = MobManager.getInstance().find(230);
                    Mob monster = new Mob(127, (short) template.id, template.hp, template.level, this.x,
                            this.y, false, template.isBoss(), zone);
                    monster.addCharId(this.id);
                    this.mob = monster;
                    getService().addMonster(monster);
                    removeItem(item.index, 1, true);
                    getService().endWait();
                } else {
                    getService().endWait("Vui lòng tiêu diệt Bosss vua tuần lộc kia trước.");
                }
            } else {
                if (taskMain != null) {
                    if (this.isCatchItem) {
                        return;
                    }
                    if (item.id == ItemName.CAN_CAU) {
                        if (taskMain.taskId == TaskName.NV_REN_LUYEN_Y_CHI
                                && taskMain.index == 1) {
                            if (mapId == MapName.LANG_CHAKUMI && this.x >= 72 && this.x <= 288 && this.y == 360) {
                                if (getSlotNull() == 0) {
                                    warningBagFull();
                                    return;
                                }
                                isCatchItem = true;
                                threadPool.submit(new Thread(new Runnable() {

                                    public void run() {
                                        try {
                                            Thread.sleep(3000);
                                            if (!isCleaned) {
                                                int rd = (int)NinjaUtils.nextInt(10);
                                                if (rd == 0) {
                                                    taskNext();
                                                    Item item = ItemFactory.getInstance().newItem(ItemName.GUOC_GO);
                                                    item.isLock = true;
                                                    item.setQuantity(1);
                                                    addItemToBag(item);
                                                } else {
                                                    getService().endWait(
                                                            "Vẫn chưa câu được vật phẩm.");
                                                }
                                                removeItem(item.index, 1, true);
                                                isCatchItem = false;
                                            }
                                        } catch (Exception ex) {
                                            serverDialog(ex.getMessage());
                                        }
                                    }
                                }));
                            } else {
                                getService().endWait("Vị trí không thích hợp.");
                            }
                        }
                    } else if (item.id == ItemName.DIA_DO2 || item.id == ItemName.DIA_DO3
                            || item.id == ItemName.DIA_DO4) {
                        if (taskMain.taskId == TaskName.NV_TRUY_TIM_BAO_VAT
                                && taskMain.index == 1) {
                            ImageMap imageMap = Server.IMAGE_MAP_ARR[item.id - ItemName.DIA_DO2][0];
                            if (imageMap.getMapID() != mapId) {
                                Map map = MapManager.getInstance().find(imageMap.getMapID());
                                getService().showAlert(item.template.name,
                                        String.format(
                                                "Bạn đang ở rất xa bảo vật. Hãy đi đến khu vực ̀%s và xem tiếp địa đồ",
                                                map.tilemap.name));
                                return;
                            }
                            if (this.x >= imageMap.getX() && this.x <= imageMap.getX() + imageMap.getW()
                                    && this.y >= imageMap.getY() && this.y <= imageMap.getY() + imageMap.getH()) {
                                if (getSlotNull() == 0) {
                                    warningBagFull();
                                    return;
                                }
                                isCatchItem = true;
                                threadPool.submit(new Thread(new Runnable() {

                                    public void run() {
                                        try {
                                            Thread.sleep(3000);
                                            if (!isCleaned) {
                                                int rd = (int)NinjaUtils.nextInt(3);
                                                if (rd == 0) {
                                                    removeItem(item.index, 1, true);
                                                    taskNext();
                                                    Item item = ItemFactory.getInstance().newItem(ItemName.DA_CAP_12);
                                                    item.isLock = true;
                                                    item.setQuantity(1);
                                                    addItemToBag(item);
                                                } else {
                                                    getService().endWait(
                                                            "Bảo vật đang ở đây, hãy đào sâu hơn nữa.");
                                                }
                                                isCatchItem = false;
                                            }
                                        } catch (Exception ex) {
                                            Log.error("dao dia do err: " + ex.getMessage(), ex);
                                            serverDialog(ex.getMessage());
                                        }
                                    }
                                }));

                            } else {
                                getService().showAlert(item.template.name,
                                        "Bảo vật đang ở quanh đây, hãy tìm đúng vị trí trong hình của địa đồ và đào lấy bảo vật.");
                            }
                        }
                    } else if (item.id == ItemName.BINH_RONG) {
                        if (taskMain.index == 1) {
                            if ((mapId == MapName.HANG_HA && taskId == TaskName.NV_LAY_NUOC_HANG_SAU)
                                    || (mapId == MapName.DINH_ICHIDAI && taskId == TaskName.NV_GIUP_DO_DAN_LANG)) {

                                if (zone.tilemap.tileTypeAt(this.x, this.y - 12, TileMap.T_WATERFLOW)) {
                                    if (getSlotNull() > 0) {
                                        isCatchItem = true;
                                        threadPool.submit(new Thread(new Runnable() {

                                            public void run() {
                                                try {
                                                    Thread.sleep(3000);
                                                    if (!isCleaned) {
                                                        if (!isFailure) {
                                                            removeItem(item.index, 1, true);
                                                            Item binhNuoc = ItemFactory.getInstance()
                                                                    .newItem(ItemName.BINH_NUOC);
                                                            binhNuoc.isLock = true;
                                                            addItemToBag(binhNuoc);
                                                            updateTaskCount(1);
                                                        }
                                                        isFailure = false;
                                                        isCatchItem = false;
                                                        // getService().endWait();
                                                    }
                                                } catch (Exception ex) {
                                                    Log.error("muc nuoc err: " + ex.getMessage(), ex);
                                                    serverDialog(ex.getMessage());
                                                }
                                            }
                                        }));
                                    } else {
                                        warningBagFull();
                                    }
                                } else {
                                    serverDialog("Hãy đứng chỗ có nước để lấy");
                                }
                            } else {
                                if (taskId == TaskName.NV_LAY_NUOC_HANG_SAU) {
                                    serverDialog("Hãy đến Hang Ha để lấy nước");
                                } else {
                                    serverDialog("Hãy đến Đỉnh Ichidai để lấy nước");
                                }
                            }
                        } else {
                            serverDialog("Bạn không làm nhiệm vụ này");
                        }
                    }
                }
            }
        } finally {
            if (!isCatchItem) {
                getService().useItem(item.index);
            }
        }
    }

    private void useItemTypeOrder(Item item) {
        if (Event.isEvent()) {
            Event.getEvent().useItem(this, item);
        }

        if (Event.getLunarNewYear() != null) {
            switch (item.id) {
                case ItemName.LAM_SON_DA:
                case ItemName.TRUC_BACH_THIEN_LU:
                    if (getSlotNull() == 0) {
                        warningBagFull();
                        return;
                    }
                    Event.useVipEventItem(this, item.id == ItemName.LAM_SON_DA ? 1 : 2,
                            Event.getLunarNewYear().vipItems);
                    removeItem(item.index, 1, true);
                    return;
            }
        }

        if (item.id == ItemName.THIEN_BIEN_LENH) {
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, (!isAutoPlay ? "Bật" : "Tắt"), () -> {
                this.isAutoPlay = !this.isAutoPlay;
                if (this.isAutoPlay) {
                    getService().turnOnAuto();
                } else {
                    getService().turnOffAuto();
                }
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Cài đặt", () -> {
                menus.clear();
                menus.add(new Menu(CMDMenu.EXECUTE,
                        "Phạm vi: " + (this.range <= 0 || this.range > 360 ? "Toàn map" : this.range), () -> {
                            menus.clear();
                            menus.add(new Menu(CMDMenu.EXECUTE, "Toàn map", () -> {
                                setRange(-1);
                            }));
                            menus.add(new Menu(CMDMenu.EXECUTE, "Phạm vi 240", () -> {
                                setRange(240);
                            }));
                            menus.add(new Menu(CMDMenu.EXECUTE, "Phạm vi 360", () -> {
                                setRange(360);
                            }));
                            menus.add(new Menu(CMDMenu.EXECUTE, "Tùy chỉnh", () -> {
                                InputDialog input = new InputDialog(CMDInputDialog.EXECUTE, "Phạm vi", () -> {
                                    try {
                                        int range = this.input.intValue();
                                        if (range > 360) {
                                            range = -1;
                                        }
                                        setRange(range);
                                    } catch (Exception e) {
                                        getService().serverDialog(e.getMessage());
                                    }
                                });
                                setInput(input);
                                getService().showInputDialog();
                            }));
                            getService().openUIMenu();
                        }));
                menus.add(new Menu(CMDMenu.EXECUTE, "Tự động nhặt: " + AUTO_PICK_ITEM[getTypeAutoPickItem()], () -> {
                    menus.clear();
                    int i = 0;
                    for (String caption : AUTO_PICK_ITEM) {
                        final int type = i;
                        menus.add(new Menu(CMDMenu.EXECUTE, caption, () -> {
                            setTypeAutoPickItem(type);
                        }));
                        i++;
                    }
                    getService().openUIMenu();
                }));
                getService().openUIMenu();
            }));
            getService().openUIMenu();
        } else if (item.id == ItemName.RUONG_HAC_AM) {
            if (getSlotNull() == 0) {
                warningBagFull();
                return;
            }
            int keyIndex = this.getIndexItemByIdInBag(ItemName.KHOA_HAC_AM);
            if (keyIndex != -1) {
                int[] arrItem = {618, 619, 620, 621, 622, 623, 624, 625, 626, 627, 628, 629, 630, 631, 632, 633, 634,
                    635, 636, 637, 1095, 1096, 1097, 1098, 1099, 1100,1101, 1102, 1102, 1103, 1104, 1105, 1106, 1107, 1108,1130,1129,1131,1132,1133,1134};
                int itemId = (int)NinjaUtils.nextInt(arrItem);
                Item itm = ItemFactory.getInstance().newItem9X(itemId, (int)NinjaUtils.nextInt(20) == 1);
                addItemToBag(itm);
                removeItem(keyIndex, 1, true);
            } else {
                Item itm = ItemFactory.getInstance().newItem(RandomItem.RUONG_HAC_AM);
                addItemToBag(itm);
                if (item.id == ItemName.BAT_BAO || item.id == ItemName.RUONG_BACH_NGAN
                        || item.id == ItemName.RUONG_HUYEN_BI || item.id == ItemName.HARLEY_DAVIDSON) {
                    GlobalService.getInstance().chat("Hệ thống",
                            this.name + " sử dụng " + item.template.name + " nhận được " + itm.template.name);
                }
            }
            removeItem(item.index, 1, true);
        } else if (item.id == ItemName.TRUNG_VI_THU) {
            if (getSlotNull() == 0) {
                warningBagFull();
                return;
            }
            if (this.yen < 1000000) {
                serverMessage("Bạn không đủ 1m yên");
                return;
            }
            int keyIndexViThu = this.getIndexItemByIdInBag(ItemName.TRUNG_VI_THU);
            if (keyIndexViThu != -1) {
            int[] arrItem = {924, 925, 926, 927, 928, 929, 930, 931, 932, 933, 934, 935, 936, 937, 938, 939, 940, 941};
            int itemId = (int)NinjaUtils.nextInt(arrItem);
            Item itm = ItemFactory.getInstance().newItem(itemId);
            addItemToBag(itm);
            addYen(-1000000);
            removeItem(item.index, 1, true);
            }
        } else if (item.id == ItemName.NUOC_DIET_KHUAN) {// sự kiện tháng 7
            if (isInfected()) {
                cure();
                if (!duatop) {
                    addExp(5000000);
                }

                if (this.getSlotNull() > 0) {
                    Item itm = ItemFactory.getInstance().newItem(RandomItem.TRE_XANH_TRAM_DOT);
                    itm.initExpire();
                    addItemToBag(itm);
                    removeItem(item.index, 1, true);
                }
            } else {
                serverMessage("Bạn không bị nhiễm bệnh");
            }
        } else if (item.isPieceCollection()) {
            pieceTogether(item);
        } else if (item.id == ItemName.TIEN_HOA_DAN) {
            if (clan != null) {
                menus.clear();
                for (ThanThu thanThu : clan.thanThus) {
                    if (thanThu.getEggHatchingTime() == -1 && thanThu.getLevel() == ThanThu.MAX_LEVEL
                            && thanThu.getStars() < ThanThu.MAX_STAR) {
                        menus.add(new Menu(CMDMenu.EXECUTE, thanThu.getName(), () -> {
                            if (item.has()) {
                                int rd = (int)NinjaUtils.nextInt(100);
                                if (rd < 5) {
                                    thanThu.evolution();
                                    getService().serverDialog(
                                            String.format("Tiến hóa thành công! %s đã đạt %d sao",
                                                    thanThu.getName(), thanThu.getStars()));
                                } else {
                                    getService().serverDialog("Tiến hóa thất bại!");
                                }
                                removeItem(item.index, 1, true);
                            }
                        }));
                    }
                }
                if (menus.size() > 0) {
                    getService().openUIMenu();
                }
            } else {
                getService().serverDialog("Hãy tham gia vào gia tộc để thực hiện");
            }
        } else if (item.id == ItemName.HUYET_LONG_NGU || item.id == ItemName.TUYET_SA_NGU
                || item.id == ItemName.LINH_TAM_NGU) {
            if (clan != null) {
                ArrayList<ThanThu> thanThus = new ArrayList<>();
                for (ThanThu thanThu : clan.thanThus) {
                    if (thanThu.getEggHatchingTime() == -1 && thanThu.getLevel() < ThanThu.MAX_LEVEL) {
                        thanThus.add(thanThu);
                    }
                }
                if (thanThus.isEmpty()) {
                    getService().serverDialog("Không có thần thú nào để cho ăn.");
                    return;
                }
                if (thanThus.size() > 1) {
                    menus.clear();
                    for (ThanThu thanThu : thanThus) {
                        menus.add(new Menu(CMDMenu.EXECUTE, thanThu.getName(), () -> {
                            if (item.has()) {
                                int exp = 100;
                                if (item.id == ItemName.TUYET_SA_NGU) {
                                    exp = 200;
                                } else if (item.id == ItemName.LINH_TAM_NGU) {
                                    exp = 400;
                                }
                                thanThu.addExp(exp);
                                removeItem(item.index, 1, true);
                            }
                        }));
                    }
                    getService().openUIMenu();
                    return;
                }
                if (thanThus.size() == 1) {
                    ThanThu thanThu = thanThus.get(0);
                    int exp = 100;
                    if (item.id == ItemName.TUYET_SA_NGU) {
                        exp = 200;
                    } else if (item.id == ItemName.LINH_TAM_NGU) {
                        exp = 400;
                    }
                    thanThu.addExp(exp);
                    removeItem(item.index, 1, true);
                }
            } else {
                getService().serverDialog("Hãy tham gia vào gia tộc để thực hiện");
            }
        } else if (item.id == ItemName.TRIEU_HOI_THU_THAN) {
            if (clan != null) {
                menus.clear();
                for (ThanThu thanThu : clan.thanThus) {
                    if (thanThu.getEggHatchingTime() == -1) {
                        menus.add(new Menu(CMDMenu.TRIEU_HOI_THAN_THU, thanThu.getName(),
                                (int) thanThu.getType()));
                    }
                }
                if (menus.size() > 0) {
                    getService().openUIMenu();
                }
            } else {
                getService().serverDialog("Hãy tham gia vào gia tộc để thực hiện");
            }
        } else if (item.id == ItemName.HOA_THIEN_DIEU || item.id == ItemName.HOA_DA_YEN) {
            if (getSlotNull() == 0) {
                warningBagFull();
                return;
            }
            RandomCollection<Integer> rc = RandomItem.LINH_VAT;
            Event.useVipEventItem(this, item.id == ItemName.HOA_THIEN_DIEU ? 1 : 2, rc);
            removeItem(item.index, 1, true);
        } else if (item.id >= 895 && item.id <= 901) {
            if (!Event.isSummer()) {
                serverMessage("Vật phẩm này chỉ sử dụng được trong sự kiện hè.");
                return;
            }
            Npc npc = zone.getNpc(18);

            if (npc == null) {
                serverMessage("Vui lòng tới làng chài, gặp bà Rei để thương thảo về mặt hàng này.");
                return;
            }

            int distance = NinjaUtils.getDistance(npc.cx, npc.cy, this.x, this.y);
            if (distance > 100) {
                serverMessage("Hãy lại gần gặp bà Rei để thương thảo về mặt hàng này.");
                return;
            }
            Events.sellFish(this, item.id, 1);
        } else if (item.id == ItemName.DIEU_GIAY || item.id == ItemName.DIEU_VAI) {
            if (!Event.isSummer()) {
                serverMessage("Vật phẩm này chỉ sử dụng được trong sự kiện hè.");
                return;
            }
            int priceGold = 0;
            int[][] itemRequires = new int[][]{{item.id, 1}};
            RandomCollection<Integer> rc = item.id == ItemName.DIEU_GIAY ? RandomItem.TRE_XANH_TRAM_DOT
                    : RandomItem.TRE_VANG_TRAM_DOT;
            Events.useEventItem(this, 1, itemRequires, priceGold, rc);
            if((int)NinjaUtils.nextInt(100) < 40){
                long exp = (int)NinjaUtils.nextInt(1, 100) * 1000000;
            addExp(exp);
            serverMessage("Bạn Nhận Được "+exp+" exp");
             }
        } else if (item.id == ItemName.VAN_NGU_CAU) {
            if (this.isCatchItem) {
                return;
            }
            if (zone.tilemap.tileTypeAt(this.x, this.y - 12, TileMap.T_WATERFLOW)) {
                if (mapId == MapName.LANG_CHAI) {
                    List<Char> chars = zone.getChars();
                    int num = 0;
                    Char closet = null;
                    int closetD = -1;
                    for (Char c : chars) {
                        if (c == this) {
                            continue;
                        }
                        int d = NinjaUtils.getDistance(c.x, c.y, this.x, this.y);
                        if (d < 200) {
                            num++;
                        }
                        if (closetD == -1 || d < closetD) {
                            closet = c;
                        }

                    }
                    int indexDeCom = getIndexItemByIdInBag(ItemName.DE_COM);
                    int indexGiunDat = getIndexItemByIdInBag(ItemName.GIUN_DAT);
                    if (indexDeCom == -1 && indexGiunDat == -1) {
                        List<String> list = new ArrayList<>();
                        list.add("Chết rồi! hết mồi luôn rồi");
                        list.add("Hết mồi rồi! ra mua của ông Tabemono để câu tiếp thôi");
                        if (num > 3) {
                            list.add("Ai cho xin ít mồi câu với");
                        }
                        if (closet != null) {
                            list.add(String.format("Bạn @%s cho mình xin ít mồi được không?",
                                    closet.name));
                        }
                        int rdIndex = (int)NinjaUtils.nextInt(list.size());
                        getService().endDlg(true);
                        zone.getService().chat(this.id, list.get(rdIndex));
                        return;
                    }
                    int num2 = num;
                    if (getSlotNull() > 0) {
                        removeItem(item.index, 1, true);
                        if (indexGiunDat != -1) {
                            removeItem(indexGiunDat, 1, true);
                        } else {
                            removeItem(indexDeCom, 1, true);
                        }
                        isCatchItem = true;
                        Char _char = this;
                        threadPool.submit(new Thread(new Runnable() {
                            public void run() {
                                try {
                                    getService().endDlg(true);
                                    serverMessage("Đang thả câu...");
                                    Thread.sleep(1000L);
                                    if (!isCleaned) {
                                        int percentNotHit = 50;
                                        if (indexGiunDat == -1) {
                                            percentNotHit = 75;
                                        }
                                        percentNotHit -= (zone.getNumberChar() - 1);
                                        percentNotHit += num2 * 5;
                                        if (numberMissed > 20) {
                                            percentNotHit /= 2;
                                        }
                                        RandomCollection<Integer> rd = new RandomCollection<>();
                                        rd.add(percentNotHit, -1);
                                        rd.add(10 + numberMissed / 3, ItemName.HUYET_LONG_NGU);
                                        rd.add(5 + numberMissed / 4, ItemName.TUYET_SA_NGU);
                                        rd.add(3 + numberMissed / 5, ItemName.LINH_TAM_NGU);
                                        int id = rd.next();
                                        if (id == -1) {
                                            List<String> list = new ArrayList<>();
                                            list.add("Hụt mất rồi :(");
                                            if (numberMissed >= 3) {
                                                list.add("Lại hụt mất rồi");
                                            }
                                            if (numberMissed >= 6) {
                                                list.add(String.format("Hụt %d lần rồi", numberMissed));
                                            }
                                            if (numberMissed >= 10) {
                                                list.add("Chắc khó câu lắm đây ToT");
                                            }
                                            if (numberMissed >= 14) {
                                                list.add("Nản quá! cá ơi cắn đi mồi đi mà");
                                            }
                                            int rdIndex = (int)NinjaUtils.nextInt(list.size());
                                            zone.getService().chat(_char.id, list.get(rdIndex));
                                            numberMissed++;
                                        } else {
                                            List<String> list = new ArrayList<>();
                                            Item item2 = ItemFactory.getInstance().newItem(id);
                                            addItemToBag(item2);
                                            list.add(
                                                    String.format("Dính rồi! %s", item2.template.name));
                                            if (numberMissed == 0) {
                                                list.add(String.format("Một phát ăn ngay %s",
                                                        item2.template.name));
                                            }
                                            if (numberMissed > 5) {
                                                list.add("Cuối cùng cũng câu được một con");
                                            }
                                            if (id == ItemName.LINH_TAM_NGU) {
                                                if (numberMissed > 10) {
                                                    list.add("Haha! không uổng công");
                                                } else {
                                                    list.add(String.format(
                                                            "Hên qua! thả vài lần mà đã câu được %s",
                                                            item2.template.name));
                                                }
                                            }
                                            int rdIndex = (int)NinjaUtils.nextInt(list.size());
                                            zone.getService().chat(_char.id, list.get(rdIndex));
                                            numberMissed = 0;
                                        }
                                    }
                                    isCatchItem = false;
                                } catch (Exception ex) {
                                    Log.error("use item 'Van Ngu Cau' err: " + ex.getMessage(), ex);
                                    serverDialog(ex.getMessage());
                                }
                            }
                        }));
                    } else {
                        warningBagFull();
                    }
                } else {
                    getService().endDlg(true);
                    zone.getService().chat(this.id, "Ở đây quá tầm thường để câu cá hiếm");
                }
            } else {
                getService().endDlg(true);
                zone.getService().chat(this.id, "Sao mình có thể thông minh vậy được");
            }

        } else if (item.id == ItemName.CAN_CAU_CA) {
            if (!Event.isSummer()) {
                serverMessage("Vật phẩm này chỉ sử dụng được trong sự kiện hè.");
                return;
            }
            if (this.isCatchItem) {
                return;
            }
            if (zone.tilemap.tileTypeAt(this.x, this.y - 12, TileMap.T_WATERFLOW)) {
                if (getSlotNull() > 0) {
                    removeItem(item.index, 1, true);
                    isCatchItem = true;
                    Char _char = this;
                    threadPool.submit(new Thread(new Runnable() {
                        public void run() {
                            try {
                                getService().endDlg(true);
                                serverMessage("Đang thả câu...");
                                Thread.sleep(1000L);
                                if (!isCleaned) {
                                    int[] idItem = {895, 896, 899, 898, 897, 900, 901};
                                    int[] percent = {20, 20, 20, 20, 10, 5, 5};
                                    int index = NinjaUtils.randomWithRate(percent, 100);
                                    int id = idItem[index];

                                    Item item2 = ItemFactory.getInstance().newItem(id);
                                    addItemToBag(item2);
                                    if (!duatop) {
                                        addExp(5000000);
                                    }

                                    if (percent[index] <= 10) {
                                        zone.getService().chat(_char.id, "Haha! Được một con "
                                                + item2.template.name + " rồi nè!");
                                    }
                                    // addEventPoint(1, Events.TOP_FISHING);
                                }
                                isCatchItem = false;
                            } catch (Exception ex) {
                                Log.error("use item 'Can Cau Ca' err: " + ex.getMessage(), ex);
                                serverDialog(ex.getMessage());
                            }
                        }
                    }));
                } else {
                    warningBagFull();
                }
            } else {
                serverDialog("Hãy đến chỗ có cá để câu");
            }
        } else if (item.id == 890 || item.id == 891 || item.id == 892) {
            menus.clear();
            if (item.id == 890) {
                menus.add(new Menu(CMDMenu.UPGRADE_SOUL_STONE, "Ghép cấp 2", 880));
                menus.add(new Menu(CMDMenu.UPGRADE_SOUL_STONE, "Ghép cấp 3", 881));
                menus.add(new Menu(CMDMenu.UPGRADE_SOUL_STONE, "Ghép cấp 4", 882));
            } else if (item.id == 891) {
                menus.add(new Menu(CMDMenu.UPGRADE_SOUL_STONE, "Ghép cấp 5", 883));
                menus.add(new Menu(CMDMenu.UPGRADE_SOUL_STONE, "Ghép cấp 6", 884));
                menus.add(new Menu(CMDMenu.UPGRADE_SOUL_STONE, "Ghép cấp 7", 885));
            } else if (item.id == 892) {
                menus.add(new Menu(CMDMenu.UPGRADE_SOUL_STONE, "Ghép cấp 8", 886));
                menus.add(new Menu(CMDMenu.UPGRADE_SOUL_STONE, "Ghép cấp 9", 887));
            }
            getService().openUIMenu();
        } else if (item.id == ItemName.LAM_THAO_DUOC) {
            Effect eff = new Effect(23, 2 * 60 * 1000, 0);
            em.setEffect(eff);
            removeItem(item.index, 1, true);
        } else if (item.id == ItemName.SUNG_LUC || item.id == ItemName.CUNG
                || item.id == ItemName.KIEM_BA_CANH || item.id == ItemName.BONG) {
            if (Events.event != Events.SEA_GAME) {
                serverMessage("Vật phẩm này chỉ sử dụng được trong sự kiện SeaGame.");
                return;
            }
            int priceGold = 0;
            int[][] itemRequires;
            RandomCollection<Integer> rc = RandomItem.TRE_VANG_TRAM_DOT;
            if (item.id == ItemName.BONG) {
                itemRequires = new int[][]{{ItemName.BONG, 1}};
                priceGold = 20;
            } else if (item.id == ItemName.KIEM_BA_CANH) {
                itemRequires = new int[][]{{ItemName.KIEM_BA_CANH, 1}};
                priceGold = 20;
            } else if (item.id == ItemName.SUNG_LUC) {
                itemRequires = new int[][]{{ItemName.SUNG_LUC, 1}, {ItemName.VIEN_DAN, 7}};
                rc = RandomItem.TRE_XANH_TRAM_DOT;
            } else {
                itemRequires = new int[][]{{ItemName.CUNG, 1}, {ItemName.MUI_TEN, 10}};
                rc = RandomItem.TRE_XANH_TRAM_DOT;
            }

            Events.useEventItem(this, 1, itemRequires, priceGold, rc);
        } else if (item.id == ItemName.CUP_BAC || item.id == ItemName.CUP_VANG) {
            if (getSlotNull() == 0) {
                warningBagFull();
                return;
            }
            if (Events.event != Events.SEA_GAME) {
                serverMessage("Vật phẩm này chỉ sử dụng được trong sự kiện SeaGame.");
                return;
            }

            RandomCollection<Integer> rc = RandomItem.CUP_VANG;
            Event.useVipEventItem(this, item.id == ItemName.CUP_VANG ? 2 : 1, rc);
            removeItem(item.index, 1, true);
        } else if (item.id == ItemName.DE_NGOC || item.id == ItemName.BUOM_VANG
                || item.id == ItemName.BO_VANG) {
            if (getSlotNull() == 0) {
                warningBagFull();
                return;
            }

            RandomCollection<Integer> rc = RandomItem.LINH_VAT;
            Event.useVipEventItem(this, item.id == ItemName.DE_NGOC ? 1 : 2, rc);
            removeItem(item.index, 1, true);
        } else if (item.id == ItemName.KHAI_THU_LENH) {
            if (openMoreOptionForMount()) {
                removeItem(item.index, 1, true);
            }
        } else if (item.id == ItemName.TRE_VANG_TRAM_DOT || item.id == ItemName.THAU_THIT_CA) {
            if (Events.event != Events.HUNG_KING) {
                serverMessage("Vật phẩm này chỉ sử dụng được trong sự kiện giỗ tổ hùng vương.");
                return;
            }
            if (getSlotNull() == 0) {
                warningBagFull();
                return;
            }
            if (!this.duatop) {
                addExp(5000000);

            }
            if (NinjaUtils.nextBoolean()) {
                RandomCollection<Integer> rc = RandomItem.TRE_VANG_TRAM_DOT;
                int itemId = rc.next();
                Item itm = ItemFactory.getInstance().newItem(itemId);
                itm.initExpire();
                if (itm.id == ItemName.THONG_LINH_THAO) {
                    itm.setQuantity((int)(int)NinjaUtils.nextInt(5, 10));
                }

                if (itemId == ItemName.BAT_BAO || itemId == ItemName.RUONG_BACH_NGAN
                        || itemId == ItemName.RUONG_HUYEN_BI) {
                    GlobalService.getInstance().chat("Hệ thống", this.name + " sử dụng "
                            + item.template.name + " nhận được " + itm.template.name);
                }

                addItemToBag(itm);
            }
            removeItem(item.index, 1, true);
        } else if (item.id == ItemName.TRE_XANH_TRAM_DOT || item.id == ItemName.THAU_RAU_CU) {
            if (Events.event != Events.HUNG_KING) {
                serverMessage("Vật phẩm này chỉ sử dụng được trong sự kiện giỗ tổ hùng vương.");
                return;
            }
            if (getSlotNull() == 0) {
                warningBagFull();
                return;
            }
            if (!duatop) {
                addExp(3000000);
            }
            if (NinjaUtils.nextBoolean()) {
                RandomCollection<Integer> rc = RandomItem.TRE_XANH_TRAM_DOT;
                int itemId = rc.next();
                Item itm = ItemFactory.getInstance().newItem(itemId);
                itm.initExpire();
                if (itm.id == ItemName.THONG_LINH_THAO) {
                    itm.setQuantity((int)NinjaUtils.nextInt(5, 10));
                }

                if (itemId == ItemName.BAT_BAO || itemId == ItemName.RUONG_BACH_NGAN
                        || itemId == ItemName.RUONG_HUYEN_BI) {
                    GlobalService.getInstance().chat("Hệ thống", this.name + " sử dụng "
                            + item.template.name + " nhận được " + itm.template.name);
                }

                addItemToBag(itm);
            }
            removeItem(item.index, 1, true);
        } else if (item.id == ItemName.GIO_HOA_8_3) {
            if (Events.event != Events.WOMAN_DAY) {
                serverMessage("Vật phẩm này chỉ sử dụng được trong sự kiện 8/3.");
                return;
            }
            if (getSlotNull() == 0) {
                warningBagFull();
                return;
            }
            if (!this.notReceivedExp) {
                addExp(5000000);
            }
            if (NinjaUtils.nextBoolean()) {
                RandomCollection<Integer> rc = RandomItem.BANH_KHUC_CAY_CHOCOLATE;
                int itemId = rc.next();
                Item itm = ItemFactory.getInstance().newItem(itemId);
                itm.initExpire();

                if (itemId == ItemName.BAT_BAO || itemId == ItemName.RUONG_BACH_NGAN
                        || itemId == ItemName.RUONG_HUYEN_BI) {
                    GlobalService.getInstance().chat("Hệ thống", this.name + " sử dụng "
                            + item.template.name + " nhận được " + itm.template.name);
                }
                addItemToBag(itm);
            }
            removeItem(item.index, 1, true);
        } else if (item.id == ItemName.GIAY_RACH || item.id == ItemName.GIAY_BAC
                || item.id == ItemName.GIAY_VANG) {
            int yen = 0;
            if (item.id == ItemName.GIAY_RACH) {
                yen = (int)NinjaUtils.nextInt(20000, 100000);
            } else if (item.id == ItemName.GIAY_BAC) {
                yen = (int)NinjaUtils.nextInt(100000, 500000);
            } else if (item.id == ItemName.GIAY_VANG) {
                yen = (int)NinjaUtils.nextInt(500000, 2000000);
            }
            addYen(yen);
            serverMessage("Bạn nhận được " + NinjaUtils.getCurrency(yen) + " yên");
            removeItem(item.index, 1, true);
        } else if (item.id == ItemName.RUONG_CHIEN_TRUONG) {
            if (getSlotNull() == 0) {
                warningBagFull();
                return;
            }
            int indexItem2 = this.getIndexItemByIdInBag(ItemName.KHOA_RUONG);
            RandomCollection<Integer> rand = RandomItem.RUONG_CHIEN_TRUONG;
            int itemId = rand.next();
            if (indexItem2 != -1 && itemId == -1) {
                while (itemId == -1) {
                    itemId = rand.next();
                }
            }
            if (itemId != -1) {
                Item itm = ItemFactory.getInstance().newItem(itemId);
                if (itemId == ItemName.SON_TINH || itemId == ItemName.THUY_TINH) {
                    long[] AVATAR_EXPIRE = {86400000L, 172800000L, 259200000L};
                    itm.expire = System.currentTimeMillis()
                            + AVATAR_EXPIRE[(int)NinjaUtils.nextInt(AVATAR_EXPIRE.length)];
                    itm.options.add(new ItemOption(87, (int)NinjaUtils.nextInt(3000, 5000)));
                    itm.options.add(new ItemOption(82, (int)NinjaUtils.nextInt(3000, 5000)));
                }

                addItemToBag(itm);
                if (itemId == ItemName.BAT_BAO || itemId == ItemName.RUONG_BACH_NGAN) {
                    GlobalService.getInstance().chat("Hệ thống", this.name + " sử dụng "
                            + item.template.name + " nhận được " + itm.template.name);
                }
            } else {
                int[] yens = {500000, 1000000, 2000000, 5000000, 10000000};
                int yen = yens[NinjaUtils.randomWithRate(new int[]{100, 30, 15, 3, 1})];
                addYen(yen);
                serverMessage("Bạn nhận được " + NinjaUtils.getCurrency(yen) + " yên");
                if (yen == 10000000) {
                    GlobalService.getInstance().chat("Hệ thống",
                            this.name + " sử dụng " + item.template.name + " nhận được 10 triệu yên");
                }
            }

            removeItem(item.index, 1, true);
            if (indexItem2 != -1) {
                removeItem(indexItem2, 1, true);
            }
        } else if (item.id == ItemName.MANH_SACH_CO) {
            if (getSlotNull() == 0) {
                warningBagFull();
                return;
            }
            if (!item.has(1000)) {
                serverMessage("Bạn cần thu thập đủ 1000 mảnh sách cổ.");
                return;
            }
            int  id = -1;
            switch  (this.classId){
                case 0:{
                   serverDialog("Thứ Vô giáo dục như mài làm sao dùng được.");
                    return;
                }case 1:{
                    id = ItemName.SACH_VO_CONG_KIEM;
                    break;
                }case 2:{
                    id = ItemName.SACH_VO_CONG_TIEU;
                    break;
                }case 3:{
                    id = ItemName.SACH_VO_CONG_KUNAI;
                    break;
                }case 4:{
                    id = ItemName.SACH_VO_CONG_CUNG;
                    break;
                }case 5:{
                    id = ItemName.SACH_VO_CONG_DAO;
                    break;
                }case 6:{
                    id = ItemName.SACH_VO_CONG_QUAT;
                    break;
                }
            }
            Item itm = ItemFactory.getInstance().newItem(id);
            itm.isLock = true;
            addItemToBag(itm);
            removeItem(item.index, 1000, true);
        }else if (item.id >=1138 && item.id <=1151 ) {
            if (getSlotNull() == 0) {
                warningBagFull();
                return;
            }
            int[] arrItem = null;
            if (item.id== ItemName.MANH_MINH_GIAC_COT_NGOC_TUYEN) {
                if (!item.has(10000)) {
                getService().serverDialog("Không Đủ 10K Mảnh .");
                return;
            }
                arrItem = new int[]{ItemName.NON10xNam};
            }else if (item.id== ItemName.MANH_MINH_GIAC_COT_NGOC_TRAM) {
                if (!item.has(10000)) {
                getService().serverDialog("Không Đủ 10K Mảnh .");
                return;
            }
                arrItem = new int[]{ItemName.NON10xNu};
            }else if (item.id== ItemName.MANH_MINH_GIAC_COT_NGOC_GIAP) {
                if (!item.has(10000)) {
                getService().serverDialog("Không Đủ 10K Mảnh .");
                return;
            }
                arrItem = new int[]{ItemName.AO10xNam};
            }else if (item.id== ItemName.MANH_MINH_GIAC_COT_NGOC_GIAP_NU) {
               if (!item.has(10000)) {
                getService().serverDialog("Không Đủ 10K Mảnh .");
                return;
            }
                arrItem = new int[]{ItemName.AO10xNu};
            }else if (item.id== ItemName.MANH_MINH_GIAC_COT_NGOC_THU) {
               if (!item.has(10000)) {
                getService().serverDialog("Không Đủ 10K Mảnh .");
                return;
            }
                arrItem = new int[]{ItemName.GANG10xNam};
            }else if (item.id== ItemName.MANH_MINH_GIAC_COT_NGOC_THU_NU) {
               if (!item.has(10000)) {
                getService().serverDialog("Không Đủ 10K Mảnh .");
                return;
            }
                arrItem = new int[]{ItemName.GANG10xNu};
            } else if (item.id== ItemName.MANH_MINH_GIAC_COT_NGOC_HA_GIAP) {
               if (!item.has(10000)) {
                getService().serverDialog("Không Đủ 10K Mảnh .");
                return;
            }
                arrItem = new int[]{ItemName.QUAN10xNam};
            }else if (item.id== ItemName.MANH_MINH_GIAC_COT_NGOC_HA_GIAP_NU) {
                if (!item.has(10000)) {
                getService().serverDialog("Không Đủ 10K Mảnh .");
                return;
            }
                arrItem = new int[]{ItemName.QUAN10xNu};
            }else if (item.id== ItemName.MANH_MINH_GIAC_COT_NGOC_NGOA) {
               if (!item.has(10000)) {
                getService().serverDialog("Không Đủ 10K Mảnh .");
                return;
            }
                arrItem = new int[]{ItemName.GIAY10xNam};
            }else if (item.id== ItemName.MANH_MINH_GIAC_COT_NGOC_NGOA_NU) {
               if (!item.has(10000)) {
                getService().serverDialog("Không Đủ 10K Mảnh .");
                return;
            }
                arrItem = new int[]{ItemName.GIAY10xNu};
            }else if (item.id== ItemName.MANH_MINH_GIAC_COT_NGOC_LIEN) {
               if (!item.has(10000)) {
                getService().serverDialog("Không Đủ 10K Mảnh .");
                return;
            }
                arrItem = new int[]{ItemName.LIEn10x};
            }else if (item.id== ItemName.MANH_MINH_GIAC_COT_NGOC_GIOI) {
               if (!item.has(10000)) {
                getService().serverDialog("Không Đủ 10K Mảnh .");
                return;
            }
                arrItem = new int[]{ItemName.NHAN10x};
            }else if (item.id== ItemName.MANH_MINH_GIAC_COT_NGOC_PHU) {
                if (!item.has(10000)) {
                getService().serverDialog("Không Đủ 10K Mảnh .");
                return;
            }
                arrItem = new int[]{ItemName.PHU10x};
            }else if(item.id== ItemName.MANH_MINH_GIAC_COT_NGOC_BOI) {
                if (!item.has(10000)) {
                getService().serverDialog("Không Đủ 10K Mảnh .");
                return;
            }
                arrItem = new int[]{ItemName.BOI10x};
            }
                int itemId = (int)NinjaUtils.nextInt(arrItem);       
                Item itm = ItemFactory.getInstance().newItem9X(itemId,(int)NinjaUtils.nextInt(3)==1);
                addItemToBag(itm);
            removeItem(item.index, 10000, true);
        }
        
        
        else if (item.id == ItemName.KHI_BAO) {
            if (getSlotNull() == 0) {
                warningBagFull();
                return;
            }
            RandomCollection<Integer> rand = RandomItem.KHI_BAO;
            int id = rand.next();
            Item itm = ItemFactory.getInstance().newItem(id);
            addItemToBag(itm);
            removeItem(item.index, 1, true);
        } else if (item.id == ItemName.LANG_BAO) {
            if (getSlotNull() == 0) {
                warningBagFull();
                return;
            }
            RandomCollection<Integer> rand = RandomItem.LANG_BAO;
            int id = rand.next();
            Item itm = ItemFactory.getInstance().newItem(id);
            addItemToBag(itm);
            removeItem(item.index, 1, true);
        } else if (item.id == ItemName.RUONG_MAY_MAN_2) {
            if (getSlotNull() == 0) {
                warningBagFull();
                return;
            }
            RandomCollection<Integer> rand = new RandomCollection<>();
            if (DONG_XU[0] > 0) {
                rand.add(2, ItemName.DONG_XU_VANG);
            }
            if (DONG_XU[1] > 0) {
                rand.add(0.5, ItemName.DONG_XU_XANH);
            }
            if (DONG_XU[2] > 0) {
                rand.add(0.03, ItemName.DONG_XU_XANH_LA);
            }
            if (DONG_XU[3] > 0) {
                rand.add(0.05, ItemName.DONG_XU_TRANG);
            }
            if (DONG_XU[4] > 0) {
                rand.add(0.05, ItemName.DONG_XU_DO);
            }
            rand.add(10.8, ItemName.LANG_BAO);
            rand.add(10, ItemName.KHI_BAO);
            rand.add(10, ItemName.MAT_NA_SUPER_BROLY);
            rand.add(10, ItemName.MAT_NA_VEGETA);
            rand.add(10, ItemName.MAT_NA_ONNA_BUGEISHA);
            rand.add(10, ItemName.MAT_NA_KUNOICHI);
            rand.add(30, ItemName.LUC_THANH_HOA);
            rand.add(1, ItemName.PET_BONG_MA);
            rand.add(1, ItemName.PET_YEU_TINH);
            rand.add(5, ItemName.HUYET_NGOC);
            rand.add(5, ItemName.HUYEN_TINH_NGOC);
            rand.add(5, ItemName.LUC_NGOC);
            rand.add(5, ItemName.LAM_TINH_NGOC);
            int id = rand.next();
            Item itm = ItemFactory.getInstance().newItem(id);
            if (id == ItemName.LUC_THANH_HOA) {
                itm.setQuantity(3);
            } else if (id == ItemName.PET_BONG_MA || id == ItemName.PET_YEU_TINH
                    || id == ItemName.MAT_NA_SUPER_BROLY || id == ItemName.MAT_NA_VEGETA
                    || id == ItemName.MAT_NA_ONNA_BUGEISHA || id == ItemName.MAT_NA_KUNOICHI) {
                itm.expire = System.currentTimeMillis();
                long expire = 2;
                if (id == ItemName.PET_BONG_MA || id == ItemName.PET_YEU_TINH) {
                    expire = 1;
                }
                expire *= 30;
                expire *= 24;
                expire *= 60;
                expire *= 60;
                expire *= 1000;
                itm.expire += expire;
            }
            if (id == ItemName.DONG_XU_VANG) {
                DONG_XU[0]--;
            }
            if (id == ItemName.DONG_XU_XANH) {
                DONG_XU[1]--;
            }
            if (id == ItemName.DONG_XU_XANH_LA) {
                DONG_XU[2]--;
            }
            if (id == ItemName.DONG_XU_TRANG) {
                DONG_XU[3]--;
            }
            if (id == ItemName.DONG_XU_DO) {
                DONG_XU[4]--;
            }
            addItemToBag(itm);
            removeItem(item.index, 1, true);
        } else if (item.id == ItemName.DONG_XU_DO) {
            if (getSlotNull() == 0) {
                warningBagFull();
                return;
            }
            addItemToBag(ItemFactory.getInstance().newItem(ItemName.PET_YEU_TINH));
            removeItem(item.index, 1, true);
        } else if (item.id == ItemName.DONG_XU_TRANG) {
            if (getSlotNull() == 0) {
                warningBagFull();
                return;
            }
            addItemToBag(ItemFactory.getInstance().newItem(ItemName.PET_BONG_MA));
            removeItem(item.index, 1, true);
        } else if (item.id == ItemName.DONG_XU_XANH_LA) {
            if (getSlotNull() == 0) {
                warningBagFull();
                return;
            }
            addItemToBag(ItemFactory.getInstance().newItem(ItemName.RUONG_HUYEN_BI));
            removeItem(item.index, 1, true);
        } else if (item.id == ItemName.DONG_XU_XANH) {
            if (getSlotNull() == 0) {
                warningBagFull();
                return;
            }
            addItemToBag(ItemFactory.getInstance().newItem(ItemName.RUONG_BACH_NGAN));
            removeItem(item.index, 1, true);
        } else if (item.id == ItemName.DONG_XU_VANG) {
            if (getSlotNull() == 0) {
                warningBagFull();
                return;
            }
            addItemToBag(ItemFactory.getInstance().newItem(ItemName.BAT_BAO));
            removeItem(item.index, 1, true);
        } else if (item.id == ItemName.LONG_KHI) {
            if (getSlotNull() == 0) {
                warningBagFull();
                return;
            }
            if (!item.has(10000)) {
                serverMessage("Bạn không đủ 10.000 Lông khỉ.");
                return;
            }
            addItemToBag(ItemFactory.getInstance().newItem(ItemName.TON_HANH_GIA));
            removeItem(item.index, 10000, true);
        } else if (item.id == ItemName.HOA_TUYET || item.id == ItemName.PHA_LE || item.id == ItemName.NHAM_THACH_) {
            int quantityNeed = -1;
            int quantityDefault = 1000;
            int itemMaskId = ItemName.SANTA_CLAUS;
            switch (item.id) {
                case ItemName.PHA_LE -> {
                    itemMaskId = ItemName.YUKIMURA_;
                }
                case ItemName.NHAM_THACH_ -> {
                    itemMaskId = ItemName.SUMIMURA_;
                }
                default -> {
                    itemMaskId = ItemName.SANTA_CLAUS;
                }
            }

            Item itm = getItemInMaskBox(itemMaskId);
            if (itm != null) {
                if (itm.upgrade >= 10) {
                    getService().serverMessage(String.format("%s đã đạt cấp tối đa, không thể nâng cấp",
                            itm.template.name));
                    return;
                }
                quantityNeed = (itm.upgrade + 1) * quantityDefault;
            } else {
                quantityNeed = quantityDefault;
            }
            if (!item.has(quantityNeed)) {
                if (itm == null) {
                    getService().serverMessage(
                            String.format("Phải có đủ %d mảnh mới có thể chế tạo", quantityNeed));
                } else {
                    getService().serverMessage(
                            String.format("Phải có đủ %d mảnh mới có thể nâng cấp", quantityNeed));
                }
                return;
            }
            removeItem(item.index, quantityNeed, true);
            if (itm != null) {
                itm.upgrade++;
                itm.initOption();
                getService().serverMessage(
                        String.format("Chúc mừng bạn đã nâng cấp thành công %s lên cấp %d",
                                itm.template.name, itm.upgrade));
            } else {
                itm = ItemFactory.getInstance().newItem(itemMaskId);
                itm.upgrade = 1;
                itm.yen = 0;
                itm.initOption();
                maskBox.add(itm);
                getService().serverMessage(
                        String.format("Chúc mừng bạn đã tạo thành công %s", itm.template.name));
            }

        } else if (item.id == ItemName.THI_LUYEN_THIEP) {
            int time = 7200000;
            Effect eff = em.findByID((byte) 34);
            if (eff != null) {
                if (eff.getTimeLength() + time > 36000000) {
                    serverMessage("Chỉ có thể sử dụng tối đa 10 giờ.");
                    return;
                }
                eff.addTime(time);
                em.setEffect(eff);
            } else {
                Effect effect = new Effect(34, time, 0);
                em.setEffect(effect);
            }
            removeItem(item.index, 1, true);
        } else if (item.id == ItemName.THIEN_NHAN_PHU || item.id == ItemName.KHAI_NHAN_PHU) {
            if (isTNP || isKNP) {
                serverMessage("Vui lòng chờ hết hiệu lực nhãn phù.");
                return;
            }
            byte template = 40;
            if (item.id == ItemName.KHAI_NHAN_PHU) {
                template = 41;
            }
            Effect eff = new Effect(template, 5 * 60 * 60 * 1000, 0);
            em.setEffect(eff);
            removeItem(item.index, 1, true);
        } else if (item.id == ItemName.MANH_GIAY_VUN) {
            if (!item.has(300)) {
                serverMessage("Không đủ giấy vụn.");
                return;
            }
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Sách tiềm năng sơ", () -> {
                if (getSlotNull() == 0) {
                    warningBagFull();
                    return;
                }
                int index = getIndexItemByIdInBag(ItemName.MANH_GIAY_VUN);
                if (index != -1 && bag[index] != null && bag[index].has(300)) {
                    Item itm = ItemFactory.getInstance().newItem(ItemName.SACH_TIEM_NANG_SO_CAP);
                    addItemToBag(itm);
                    removeItem(index, 300, true);
                }
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Sách kỹ năng sơ", () -> {
                if (getSlotNull() == 0) {
                    warningBagFull();
                    return;
                }
                int index = getIndexItemByIdInBag(ItemName.MANH_GIAY_VUN);
                if (index != -1 && bag[index] != null && bag[index].has(300)) {
                    Item itm = ItemFactory.getInstance().newItem(ItemName.SACH_KY_NANG_SO_CAP);
                    addItemToBag(itm);
                    removeItem(index, 300, true);
                }
            }));
            getService().openUIMenu();
        } else if (item.id == ItemName.LENH_BAI_HANG_DONG) {
            if (this.countPB == 0) {
                if (countUseItemDungeo == 0) {
                    serverMessage("Đã hết lượt sử dụng.");
                    return;
                }
                Dungeon dungeon = (Dungeon) findWorld(World.DUNGEON);
                if (dungeon != null && !dungeon.isClosed()) {
                    serverMessage("Hang động hiện tại vẫn chưa kết thúc.");
                    return;
                }
                removeWorld(World.DUNGEON);
                this.countPB = 1;
                this.pointPB = 0;
                this.receivedRewardPB = false;
                this.countUseItemDungeo = 0;
                serverMessage("Bạn nhận được thêm 1 lần khám phá hang động.");
                removeItem(item.index, 1, true);
            } else {
                serverMessage("Bạn vẫn còn lượt vào hang động.");
            }
        } else if (item.id == ItemName.THE_BAI_KINH_NGHIEM_GIA_TOC_SO
                || item.id == ItemName.THE_BAI_KINH_NGHIEM_GIA_TOC_TRUNG
                || item.id == ItemName.THE_BAI_KINH_NGHIEM_GIA_TOC_CAO) {
            if (clan == null) {
                serverMessage("Cần có gia tộc để sử dụng.");
                return;
            }
            int exp = 0;
            int level = 0;
            if (item.id == ItemName.THE_BAI_KINH_NGHIEM_GIA_TOC_SO) {
                level = 1;
                exp = (int)NinjaUtils.nextInt(100, 200);
            } else if (item.id == ItemName.THE_BAI_KINH_NGHIEM_GIA_TOC_TRUNG) {
                level = 10;
                exp = (int)NinjaUtils.nextInt(300, 800);
            } else if (item.id == ItemName.THE_BAI_KINH_NGHIEM_GIA_TOC_CAO) {
                level = 15;
                exp = (int)NinjaUtils.nextInt(1000, 200);
            }
            if (level > clan.level) {
                serverMessage("Yêu cầu gia tộc phải đạt cấp " + level);
                return;
            }
            Member mem = clan.getMemberByName(this.name);
            if (mem != null) {
                mem.addPointClan(exp);
                // mem.addPointClanWeek(exp);
                clan.addExp(exp);
                removeItem(item.index, 1, true);
            } else {
                serverDialog("Có lỗi xảy ra.");
            }
        } else if (item.id == ItemName.XANG_A95) {
            Mount mount = this.mount[4];
            if (mount != null) {
                if (mount.id == ItemName.HARLEY_DAVIDSON || item.id == ItemName.XE_MAY) {
                    updateHPMount(200);
                    zone.getService().loadMount(this);
                    removeItem(item.index, 1, true);
                } else {
                    serverDialog("Không thể dùng cho thú cưỡi.");
                }
            } else {
                serverDialog("Bạn chưa sử dụng xe máy.");
            }
        } else if (item.id == ItemName.LINH_LANG_THAO) {
            Mount mount = this.mount[4];
            if (mount != null) {
                if (mount.id != ItemName.HARLEY_DAVIDSON && item.id != ItemName.XE_MAY) {
                    updateHPMount(200);
                    zone.getService().loadMount(this);
                    removeItem(item.index, 1, true);
                } else {
                    serverDialog("Không thể dùng cho xe máy.");
                }
            } else {
                serverDialog("Bạn chưa sử dụng thú cưỡi.");
            }
        } else if (item.id == ItemName.DANH_VONG_PHU) {
            if (this.countUseItemGlory > 0) {
                this.countGlory += 5;
                this.countUseItemGlory--;
                removeItem(item.index, 1, true);
                serverMessage("Bạn nhận được thêm 5 lần làm nhiệm vụ danh vọng.");
            } else {
                serverMessage("Đã hết số lần dùng trong ngày hôm nay rồi.");
            }
        } else if (item.id == ItemName.TA_THU_LENH) {
            if (this.countUseItemBeast > 0) {
                this.countLoopBoss++;
                this.countUseItemBeast--;
                removeItem(item.index, 1, true);
                serverMessage("Bạn nhận được thêm 1 lần làm nhiệm vụ tà thú.");
            } else {
                serverMessage("Đã hết số lần dùng trong ngày hôm nay rồi.");
            }
        } else if (item.id == ItemName.BACH_BIEN_LENH || item.id == ItemName.VO_HAN_BACH_BIEN_LENH) {
            if (zone.tilemap.isNotReturnTown()) {
                serverMessage("Không thể sử dụng tính năng này tại đây.");
                return;
            }
            short[] xy = NinjaUtils.getXY(this.saveCoordinate);
            setXY(xy[0], xy[1]);
            if (item.id == ItemName.BACH_BIEN_LENH) {
                removeItem(item.index, item.getQuantity(), true);
            }
            changeMap(this.saveCoordinate);
            Log.error("bach bien lenh map: " + saveCoordinate);
        } else if (item.id >= 695 && item.id <= 703) {
            int quantity = item.getQuantity() / 10;
            if (quantity <= 0) {
                serverDialog("Yêu cầu tối thiểu 10 viên " + item.template.name);
                return;
            }
            int slotNull = getSlotNull();
            if (slotNull == 0) {
                warningBagFull();
                return;
            }
            removeItem(item.index, quantity * 10, true);
            Item itm = ItemFactory.getInstance().newItem(item.id + 1);
            itm.setQuantity(quantity);
            itm.isLock = true;
            addItemToBag(itm);
        } else if (item.id == ItemName.THAT_THU_THU_BAO) {
            int slotNull = getSlotNull();
            if (slotNull == 0) {
                warningBagFull();
                return;
            }

            removeItem(item.index, 1, true);
            if (taskId == TaskName.NV_THU_TAI_MAY_MAN) {
                if (taskMain != null && taskMain.index == 2) {
                    updateTaskCount(1);
                }
            }
            int itemId = RandomItem.THAT_THU_BAO.next();
            Item itm = ItemFactory.getInstance().newItem(itemId);
            itm.setQuantity(1);
            addItemToBag(itm);
        } else if (item.id == ItemName.RUONG_MAY_MAN || item.id == ItemName.RUONG_TINH_XAO
                || item.id == ItemName.RUONG_MA_QUAI) {
            int slotNull = getSlotNull();
            if (slotNull == 0) {
                warningBagFull();
                return;
            }
            int[] arrItem = null;
            int[] arrPercent = null;
            boolean[] arrLock = null;
            if (item.id == 272) {
                arrItem = new int[]{3, 4, 5, 6, 242, 269, 12};
                arrPercent = new int[]{30, 25, 20, 15, 2, 1, 7};
                arrLock = new boolean[]{true, true, true, true, true, true, true};
            } else if (item.id == 282) {
                arrItem = new int[]{3, 4, 5, 6, 284, 270, 12};
                arrPercent = new int[]{30, 25, 20, 15, 2, 1, 7};
                arrLock = new boolean[]{true, true, true, true, true, true, true};
            } else {
                arrItem = new int[]{3, 4, 5, 6, 285, 271, 436, 12, 618, 619, 620, 621, 622, 623, 624,
                    625, 626, 627, 628, 629, 630, 631, 632, 633, 634, 635, 636, 637};
                arrPercent = new int[]{20, 15, 10, 9, 5, 1, 10, 10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1};
                arrLock = new boolean[]{true, true, true, true, true, true, false, true, false, false,
                    false, false, false, false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false};
            }
            if (taskId == TaskName.NV_THU_TAI_MAY_MAN) {
                if (taskMain != null && taskMain.index == 1) {
                    updateTaskCount(1);
                }
            }
            int index = NinjaUtils.randomWithRate(arrPercent, 100);
            int id = arrItem[index];
            boolean isLock = arrLock[index];
            if (id != 12) {
                Item itm = ItemFactory.getInstance().newItem9X(id);
                addItemToBag(itm);
            } else {
                addYen((int)NinjaUtils.nextInt(10, 100) * 1000);
            }
            removeItem(item.index, 1, true);
        } else if (item.id == 275 || item.id == 276 || item.id == 277 || item.id == 278) {
            byte templateId = 0;
            short param = 0;
            switch (item.id) {
                case 275:
                    templateId = 24;
                    param = 500;
                    break;

                case 276:
                    templateId = 25;
                    param = 500;
                    break;

                case 277:
                    templateId = 26;
                    param = 100;
                    break;

                case 278:
                    templateId = 27;
                    param = 1000;
                    break;
            }
            Effect effect = new Effect(templateId, 600000, param);
            em.setEffect(effect);
            setAbility();
            removeItem(item.index, 1, true);
        } else if (item.id == ItemName.BAT_BAO || item.id == ItemName.RUONG_BACH_NGAN
                || item.id == ItemName.RUONG_HUYEN_BI) {
            int slotNull = getSlotNull();
            if (slotNull == 0) {
                warningBagFull();
                return;
            }
            if (classId == 0) {
                serverDialog("Bạn không đủ sức khỏe để mở. Hãy vào trường để rèn luyện thêm.");
                return;
            }
            int upgrade = 12;
            if (item.id == ItemName.RUONG_BACH_NGAN) {
                upgrade = 14;
            } else if (item.id == ItemName.RUONG_HUYEN_BI) {
                upgrade = 16;
            }
            RandomCollection<Byte> storeTypes = new RandomCollection<>();
            storeTypes.add(3, StoreManager.TYPE_WEAPON);
            storeTypes.add(7, StoreManager.TYPE_NECKLACE);
            storeTypes.add(7, StoreManager.TYPE_RING);
            storeTypes.add(7, StoreManager.TYPE_PEARL);
            storeTypes.add(7, StoreManager.TYPE_SPELL);
            if (gender == 1) {
                storeTypes.add(10, StoreManager.TYPE_MEN_HAT);
                storeTypes.add(10, StoreManager.TYPE_MEN_SHIRT);
                storeTypes.add(10, StoreManager.TYPE_MEN_GLOVES);
                storeTypes.add(10, StoreManager.TYPE_MEN_PANT);
                storeTypes.add(10, StoreManager.TYPE_MEN_SHOES);
            } else {
                storeTypes.add(10, StoreManager.TYPE_WOMEN_HAT);
                storeTypes.add(10, StoreManager.TYPE_WOMEN_SHIRT);
                storeTypes.add(10, StoreManager.TYPE_WOMEN_GLOVES);
                storeTypes.add(10, StoreManager.TYPE_WOMEN_PANT);
                storeTypes.add(10, StoreManager.TYPE_WOMEN_SHOES);
            }
            byte rd = storeTypes.next();
            Store store = StoreManager.getInstance().find((byte) rd);
            int level = 50;
            if (this.level >= 60 || item.id == 385) {
                level = 70;
            }
            final int sys = (int)NinjaUtils.nextInt(1, 3);
            final int level2 = level;
            ItemStore itemStore = store.stream().filter((t) -> {
                ItemTemplate template = t.getTemplate();
                if (store.getType() == StoreManager.TYPE_WEAPON) {
                    if ((classId == 1 && !template.isKiem()) || (classId == 2 && !template.isTieu())
                            || (classId == 3 && !template.isKunai()) || (classId == 4 && !template.isCung())
                            || (classId == 5 && !template.isDao()) || (classId == 6 && !template.isQuat())) {
                        return false;
                    }
                } else {
                    if (t.getSys() != sys) {
                        return false;
                    }
                }
                if (((int) template.level / 10) == ((int) level2 / 10)) {
                    return true;
                }
                return false;
            }).findFirst().orElse(null);
            if (itemStore != null) {
                Item add = ItemFactory.getInstance().newItem(itemStore.getItemID());
                add.sys = itemStore.getSys();
                List<ItemOption> options = itemStore.getMaxOptions();
                for (ItemOption o : options) {
                    int templateId = o.optionTemplate.id;
                    int param = o.param;
                    add.options.add(new ItemOption(templateId, param));
                }
                add.next(upgrade);
                add.isLock = item.isLock;
                addItemToBag(add);
                removeItem(item.index, 1, true);
            } else {
                serverDialog("Có lỗi xảy ra! Vui lòng báo với quản trị viên.");
            }
        } else if (item.id == 254 || item.id == 255 || item.id == 256) {
            if (!isHuman) {
                warningClone();
                return;
            }
            if (item.id == 254 && this.level >= 30) {
                serverDialog("Vật phẩm chỉ có thể xóa kinh nghiệm âm dưới cấp 30");
                return;
            } else if (item.id == 255 && this.level >= 60) {
                serverDialog("Vật phẩm chỉ có thể xóa kinh nghiệm âm dưới cấp 60");
                return;
            }
            addExp(this.expDown);
            removeItem(item.index, 1, true);
            serverMessage("Đã xóa kinh nghiệm âm");
        } else if (item.id == ItemName.CAN_CAU_VANG) {
            if (this.isCatchItem) {
                return;
            }
            if (zone.tilemap.tileTypeAt(this.x, this.y - 12, TileMap.T_WATERFLOW)) {
                if (getSlotNull() > 0) {
                    removeItem(item.index, 1, true);
                    isCatchItem = true;
                    Char _char = this;
                    threadPool.submit(new Thread(new Runnable() {
                        public void run() {
                            try {
                                getService().endDlg(true);
                                serverMessage("Đang thả câu...");
                                Thread.sleep(3000L);
                                if (!isCleaned) {
                                    int[] idItem = {4, 5, 6, 7, 8, 9, 10, 11, -1, -2};
                                    int[] percent = {20, 15, 15, 15, 10, 3, 2, 1, 15, 25};
                                    int index = NinjaUtils.randomWithRate(percent, 100);
                                    int id = idItem[index];
                                    if (id == -1) {
                                        int exp = (int)NinjaUtils.nextInt(1, 10) * 100000;
                                        if (!duatop) {
                                            addExp(exp);
                                            serverMessage("Bạn nhận được " + NinjaUtils.getCurrency(exp)
                                                    + " kinh nghiệm");
                                        }

                                    } else if (id == -2) {
                                        zone.getService().chat(_char.id, "Ôi? Dây câu đứt mất rồi!");
                                    } else {
                                        Item item2 = ItemFactory.getInstance().newItem(id);
                                        if ((int)NinjaUtils.nextInt(250) == 1) {
                                            item2 = ItemFactory.getInstance().newItem(ItemName.LINH_CHI_NGAN_NAM);
                                        } else if ((int)NinjaUtils.nextInt(1000) == 0) {
                                            item2 = ItemFactory.getInstance().newItem(ItemName.LINH_CHI_VAN_NAM);
                                        }
                                        addItemToBag(item2);
                                        if (percent[index] < 10) {
                                            zone.getService().chat(_char.id,
                                                    "Hiếm lắm đấy! Là " + item2.template.name + " "
                                                    + (int)NinjaUtils.nextInt(300, 500) + " cm!");
                                        }
                                    }
                                }
                                isCatchItem = false;
                            } catch (Exception ex) {
                                Log.error("use item 'Can Cau Vang' err: " + ex.getMessage(), ex);
                                serverDialog(ex.getMessage());
                            }
                        }
                    }));
                } else {
                    warningBagFull();
                }
            } else {
                serverDialog("Hãy đứng chỗ có nước để câu");
            }
        } else if (item.id == ItemName.TIEN_HOA_THAO) {
            if (mount[4] != null) {
                if (mount[4].id == ItemName.HAC_NGUU) {
                    if (upgradeMount()) {
                        removeItem(item.index, 1, true);
                    }
                }
            }

        } else if (item.id == 454) {
            if (mount[4] != null) {
                if (mount[4].id != 776 && mount[4].id != 777) {
                    if (upgradeMount()) {
                        removeItem(item.index, 1, true);
                    }
                }
            }

        } else if (item.id == ItemName.LANG_HON_THAO) {
            if (addExpForMount(5, (byte) 0)) {
                removeItem(item.index, 1, true);
            }
        } else if (item.id == ItemName.LANG_HON_MOC) {
            if (addExpForMount(7, (byte) 0)) {
                removeItem(item.index, 1, true);
            }
        } else if (item.id == ItemName.DIA_LANG_THAO) {
            if (addExpForMount(14, (byte) 0)) {
                removeItem(item.index, 1, true);
            }
        } else if (item.id == ItemName.TAM_LUC_DIEP) {
            if (addExpForMount(20, (byte) 0)) {
                removeItem(item.index, 1, true);
            }
        } else if (item.id == ItemName.XICH_LAN_HOA_) {
            if (addExpForMount(25, (byte) 0)) {
                removeItem(item.index, 1, true);
            }
        } else if (item.id == ItemName.LUC_THANH_HOA) {
            if (addExpForMount(200, (byte) 0)) {
                removeItem(item.index, 1, true);
            }
        } else if (item.id == ItemName.TU_LINH_LIEN_HOA) {
            if (addExpForMount(400, (byte) 0)) {
                removeItem(item.index, 1, true);
            }
        } else if (item.id == ItemName.LINH_LANG_HO_DIEP) {
            if (addExpForMount(600, (byte) 0)) {
                removeItem(item.index, 1, true);
            }
        } else if (item.id == ItemName.BANH_RANG) {
            if (addExpForMount(100, (byte) 1)) {
                removeItem(item.index, 1, true);
            }
        } else if (item.id == ItemName.IK) {
            if (addExpForMount(250, (byte) 1)) {
                removeItem(item.index, 1, true);
            }
        } else if (item.id == ItemName.THUOC_CAI_TIEN) {
            if (addExpForMount(500, (byte) 1)) {
                removeItem(item.index, 1, true);
            }
        } else if (item.id == ItemName.THONG_LINH_THAO) {
            if (addExpForMount((int)NinjaUtils.nextInt(1, 10), (byte) 2)) {
                removeItem(item.index, 1, true);
            }
        } else if (item.id == ItemName.TU_HOA_DIA_DINH) {
            if (addExpForMount((int)NinjaUtils.nextInt(50, 150), (byte) 2)) {
                removeItem(item.index, 1, true);
            }
        } else if (item.id == ItemName.KIM_TUOC_THAO) {
            if (addExpForMount((int)NinjaUtils.nextInt(2, 40), (byte) 2)) {
                removeItem(item.index, 1, true);
            }
        } else if (item.id == ItemName.HOAN_LUONG_CHI_THAO) {
            if (!isHuman) {
                warningClone();
                return;
            }

            if (this.hieuChien == 0) {
                serverMessage("Điểm hiếu chiến của bạn đã bằng 0.");
                return;
            }

            if (this.zone.tilemap.isNotAllowPkPoint()) {
                serverMessage("Không thể sử dụng vật phẩm này tại đây.");
                return;
            }

            addPointPk(-5);
            serverMessage(String.format("Điểm hiếu chiến của bạn còn %d điểm", this.hieuChien));
            removeItem(item.index, 1, true);
        } else if (item.id == 279) {
            if (!isHuman) {
                warningClone();
                return;
            }
            InputDialog input = new InputDialog(CMDInputDialog.TELEPORT, "Nhập tên người cần tới");
            setInput(input);
            getService().showInputDialog();
        } else if (item.id == ItemName.NAM_LINH_CHI) {
            int time = 5 * 60 * 60 * 1000;
            short param = 2;
            byte template = 22;
            Effect eff = em.findByID(template);
            if (eff != null) {
                eff.addTime(time);
                em.setEffect(eff);
            } else {
                Effect effect = new Effect(template, time, param);
                effect.param2 = item.id;
                em.setEffect(effect);
            }
            removeItem(item.index, item.getQuantity(), true);
        } else if (item.id == ItemName.LINH_CHI_NGAN_NAM) {
            int time = 3600000;
            short param = 3;
            byte template = 32;
            Effect eff = em.findByID(template);
            if (eff != null) {
                eff.addTime(time);
                em.setEffect(eff);
            } else {
                Effect effect = new Effect(template, time, param);
                em.setEffect(effect);
            }
            removeItem(item.index, item.getQuantity(), true);
        } else if (item.id == ItemName.LINH_CHI_VAN_NAM) {
            int time = 3600000;
            short param = 4;
            byte template = 33;
            Effect eff = em.findByID(template);
            if (eff != null) {
                eff.addTime(time);
                em.setEffect(eff);
            } else {
                Effect effect = new Effect(template, time, param);
                em.setEffect(effect);
            }
            removeItem(item.index, item.getQuantity(), true);
        } else if (item.id == 38) {
            int yen = (int)NinjaUtils.nextInt(1000, 20000);
            addYen(yen);
            serverMessage("Bạn nhận được " + NinjaUtils.getCurrency(yen) + " yên");
            removeItem(item.index, 1, true);
        } else if (item.id == 240) {
            this.tayTiemNang++;
            removeItem(item.index, item.getQuantity(), true);
            serverMessage(String.format("Số lần tẩy điểm tiềm năng của bạn là %d", tayTiemNang));
        } else if (item.id == 241) {
            this.tayKyNang++;
            removeItem(item.index, item.getQuantity(), true);
            serverMessage(String.format("Số lần tẩy điểm kỹ năng của bạn là %d", tayTiemNang));
        } else if (item.id == 252) {
            if (this.limitKyNangSo < 3) {
                this.limitKyNangSo++;
                this.skillPoint++;
                getService().loadSkill();
                removeItem(item.index, item.getQuantity(), true);
                serverMessage("Bạn nhận được 1 điểm kỹ năng.");
            } else {
                serverDialog("Đã hết số lần sử dụng.");
            }
        } else if (item.id == 253) {
            if (this.limitTiemNangSo < 8) {
                this.limitTiemNangSo++;
                this.potentialPoint += 10;
                getService().updatePotential();
                removeItem(item.index, item.getQuantity(), true);
                serverMessage("Bạn nhận được 10 điểm tiềm năng.");
            } else {
                serverDialog("Đã hết số lần sử dụng.");
            }
        } else if (item.id == 308) {
            if (this.limitPhongLoi < 10) {
                this.limitPhongLoi++;
                this.skillPoint++;
                getService().loadSkill();
                removeItem(item.index, item.getQuantity(), true);
                serverMessage("Bạn nhận được 1 điểm kỹ năng.");
            } else {
                serverDialog("Đã hết số lần sử dụng.");
            }
        } else if (item.id == 309) {
            if (this.limitBangHoa < 10) {
                this.limitBangHoa++;
                this.potentialPoint += 10;
                getService().updatePotential();
                removeItem(item.index, item.getQuantity(), true);
                serverMessage("Bạn nhận được 10 điểm tiềm năng.");
            } else {
                serverDialog("Đã hết số lần sử dụng.");
            }
        } else if (item.id == 215 || item.id == 229 || item.id == 283 || item.id == 801 || item.id == 1128) {
             if (numberCellBag == 120) {
                 serverDialog("đã 120 ô ko thể mở thêm.");
                 return;
             }
            expandBag(item);
        } else if (item.id == ItemName.CO_LENH) {
            if (!isHuman) {
                warningClone();
                return;
            }
            if (mapId == 138 || mapId == 137 || mapId == 136 || mapId == 135 || mapId == 134) {
                serverMessage("Bạn đang trong làng cổ.");
                return;
            }
            setXY((short) 109, (short) 408);

            changeMap(138);
            removeItem(item.index, item.getQuantity(), true);
        } else if (item.id == ItemName.TRUYEN_THUYET_LENH) {
            if (!isHuman) {
                warningClone();
                return;
            }
            if (mapId == 162 || mapId == 163 || mapId == 164 || mapId == 165) {
                serverMessage("Bạn đang trong làng truyền thuyết.");
                return;
            }
            setXY((short) 171, (short) 408);
            changeMap(162);
            removeItem(item.index, item.getQuantity(), true);
        }
        if (item.id == ItemName.VI_THU_LENH) { 
            Calendar cal = Calendar.getInstance();
            int currentHour = cal.get(Calendar.HOUR_OF_DAY);
            int currentMinute = cal.get(Calendar.MINUTE);

            if (currentHour == 12 && currentMinute >= 0 && currentMinute <= 30) { //giờ vào map
                if (!isHuman) {
                    warningClone();
                    return;
                }
                setXY((short) 120, (short) 384);
                changeMap(171);
                removeItem(item.index, item.getQuantity(), true);

            } else {
                serverMessage("chỉ có thể vào map vào 12h00 đến 12h30.");
            }
        }else if (item.id == ItemName.THE_DOI_TEN) {
            int slotNull = getSlotNull();
            if (slotNull == 0) {
                warningBagFull();
                return;
            }else{
            changeName();
           // removeItem(item.index, 1, true);
            }
        }
        if (item.id == ItemName.htnv) {
            if (taskMain == null) {
                serverDialog("bạn ko có nhiện vụ chính để hoàn thành");
                return;
            }
            if (taskMain != null) {
                    if (taskMain.taskId >= 0 && taskMain.taskId <= 15) {
                        if (user.gold <= 100) {
                            serverDialog("Không đủ lượng!");
                            return;
                        }
                        if (user.gold >= 100) {
                            addGold(-100);
                            finishTask(true);
                            serverDialog("Bạn đã hoàn thành nhiện vụ và bị trừ 100 lượng!");
                        } else {
                            serverDialog("Không đủ lượng!");
                        }
                        return;
                    }
                    if (taskMain.taskId >= 16 && taskMain.taskId <= 30) {
                        if (user.gold <= 200) {
                            serverDialog("Không đủ lượng!");
                            return;
                        }
                        if (user.gold >= 200) {
                            addGold(-200);
                            finishTask(true);
                            serverDialog("Bạn đã hoàn thành nhiện vụ và bị trừ 200 lượng!");
                        } else {
                            serverDialog("Không đủ lượng!");
                        }
                        return;
                    }
                    if (taskMain.taskId >= 31 && taskMain.taskId <= 42) {
                        if (user.gold <= 300) {
                            serverDialog("Không đủ lượng!");
                            return;
                        }
                        if (user.gold >= 300) {
                            addGold(-300);
                            finishTask(true);
                            serverDialog("Bạn đã hoàn thành nhiện vụ và bị trừ 300 lượng!");
                        } else {
                            serverDialog("Không đủ lượng!");
                        }
                        return;
                    }
            }

        } else {
            learnSkill(item);
        }
    }

    public void useItem(Message ms) {
        lockItem.lock();
        try {
            if (isDead) {
                return;
            }
            if (trade != null) {
                warningTrade();
                return;
            }
            byte indexUI = ms.reader().readByte();
            if (indexUI >= 0 && indexUI <= numberCellBag) {
                Item item = bag[indexUI];
                if (item != null && item.has()) {
                    if (this.level < item.template.level) {
                        return;
                    }
                    if (item.isExpired()) {
                        removeItem(item.index, 1, true);
                        return;
                    }
                    if ((item.template.gender == 0 || item.template.gender == 1)
                            && item.template.gender != this.gender) {
                        return;
                    }
                    if (item.template.isTypeBody()) {
                        if (this.zone.tilemap.isFujukaSanctuary()) {
                            serverMessage("Không thể trang bị trong khu vực này");
                            return;
                        }
                        if (item.template.id == ItemName.SANTA_CLAUS) {
                            menus.clear();
                            menus.add(new Menu(CMDMenu.EXECUTE, "Trang bị", () -> {
                                useEquipment(item);
                            }));
                            menus.add(new Menu(CMDMenu.EXECUTE, "Tách", () -> {
                                int indexItem = this.getIndexItemByIdInBag(ItemName.SANTA_CLAUS);
                                if (indexItem != -1) {
                                    removeItem(indexItem, 1, true);
                                    Item newItem = ItemFactory.getInstance().newItem(ItemName.HOA_TUYET);
                                    newItem.setQuantity(10000);
                                    newItem.isLock = true;
                                    addItemToBag(newItem);
                                }
                            }));
                            getService().openUIMenu();
                            return;
                        } else if (item.template.isTypeBijuu()) {
                            doUseBijuu(item);
                        } else {
                            useEquipment(item);
                        }
                        return;
                    } else if (item.template.isTypeMount()) {
                        useMount(item);
                        return;
                    } else if (item.template.isTypeEquipmentBijuu()) {
                        doUseEquipmentBijuu(item);
                        return;
                    } else if (item.template.type == ItemTemplate.TYPE_TASK) {
                        if (item.id == ItemName.CHIA_KHOA_CO_QUAN2) {
                            if (mapId == MapName.HANG_MEIRO && this.y == 432 && this.x >= 1800 && this.x <= 1896) {
                                if (taskMain != null && taskMain.taskId == TaskName.NV_TRUY_TIM_DIA_DO
                                        && taskMain.index == 1) {
                                    removeItem(item.index, 1, true);
                                    Map map = MapManager.getInstance().find(MapName.DIA_DAO_CHIKATOYA);
                                    ChikatoyaTunnels chikatoyaTunnels = new ChikatoyaTunnels(0, map.tilemap, map);
                                    outZone();
                                    setXY((short) 35, (short) 96);
                                    chikatoyaTunnels.join(this);
                                }
                            }
                        }
                    } else if (item.template.type == ItemTemplate.TYPE_ORDER) {
                        useItemTypeOrder(item);
                        return;
                    } else if (item.template.type == ItemTemplate.TYPE_TASK_WAIT) {
                        useItemTaskWait(item);
                        return;
                    } else if (item.template.type == ItemTemplate.TYPE_DRAGONBALL) {
                        if (isCatchItem) {
                            return;
                        }
                        int[] index = new int[7];
                        for (int i = 222; i <= 228; i++) {
                            int in = this.getIndexItemByIdInBag(i);
                            if (in == -1) {
                                serverMessage("Cần phải hội tụ 7 viên ngọc mới có thể sử dụng");
                                return;
                            }
                            index[i - 222] = in;
                        }
                        if (getSlotNull() == 0) {
                            warningBagFull();
                            return;
                        }
                        isCatchItem = true;
                        getService().callEffectBall();
                        Thread.sleep(1500);
                        if (!isCleaned) {
                            int id = (new int[]{420, 421, 422})[getSys() - 1];
                            Item itm = ItemFactory.getInstance().newItem(id);
                            itm.isLock = true;
                            itm.sys = getSys();
                            addItemToBag(itm);
                            for (int i : index) {
                                removeItem(i, 1, true);
                            }
                        }
                        isCatchItem = false;
                    } else if (item.template.type == ItemTemplate.TYPE_HP) {
                        if (this.hieuChien >= 10) {
                            serverDialog("Điểm hiếu chiến quá cao.");
                            return;
                        }
                        if (this.hp >= this.maxHP) {
                            serverMessage("HP đã đầy.");
                            return;
                        }

                        int time = 3000;
                        short param = 0;
                        if (item.id == 13) {
                            param = 25;
                        } else if (item.id == 14) {
                            param = 90;
                        } else if (item.id == 15) {
                            param = 230;
                        } else if (item.id == 16) {
                            param = 400;
                        } else if (item.id == 17) {
                            param = 650;
                        } else if (item.id == 565) {
                            param = 1500;
                        }
                        byte template = 21;
                        Effect effect = new Effect(template, time, param);
                        em.setEffect(effect);
                        removeItem(item.index, 1, true);
                        if (this.zone.tilemap.isFujukaSanctuary()) {
                            ((FujukaSanctuary) this.zone).copyRecoveryEffect(this, effect);
                        }
                        return;
                    } else if (item.template.type == ItemTemplate.TYPE_MP) {
                        if (this.hieuChien >= 10) {
                            serverDialog("Điểm hiếu chiến quá cao.");
                            return;
                        }
                        if (this.mp >= this.maxMP) {
                            serverMessage("MP đã đầy.");
                        }

                        int mp = 0;
                        switch (item.id) {
                            case 18:
                                mp = 150;
                                break;

                            case 19:
                                mp = 500;
                                break;

                            case 20:
                                mp = 1000;
                                break;

                            case 21:
                                mp = 2000;
                                break;

                            case 22:
                                mp = 3500;
                                break;

                            case 566:
                                mp = 5000;
                                break;
                        }
                        this.mp += mp;
                        if (this.mp > this.maxMP) {
                            this.mp = this.maxMP;
                        }
                        getService().updateMp();
                        removeItem(item.index, 1, true);
                        return;
                    } else if (item.template.type == ItemTemplate.TYPE_EAT) {
                        if (this.hieuChien >= 10) {
                            serverDialog("Điểm hiếu chiến quá cao.");
                            return;
                        }
                        int time = 0;
                        short param = 0;
                        byte template = 0;
                        switch (item.id) {
                            case 23:
                                time = 1800000;
                                param = 3;
                                template = 0;
                                break;

                            case 24:
                                time = 1800000;
                                param = 20;
                                template = 1;
                                break;

                            case 25:
                                time = 1800000;
                                param = 30;
                                template = 2;
                                break;

                            case 26:
                                time = 1800000;
                                param = 40;
                                template = 3;
                                break;

                            case 27:
                                time = 1800000;
                                param = 50;
                                template = 4;
                                break;

                            case 29:
                                time = 1800000;
                                param = 60;
                                template = 28;
                                break;

                            case 30:
                                time = 3 * 24 * 60 * 60 * 1000;
                                param = 60;
                                template = 28;
                                break;

                            case 249:
                                time = 3 * 24 * 60 * 60 * 1000;
                                param = 40;
                                template = 3;
                                break;

                            case 250:
                                time = 3 * 24 * 60 * 60 * 1000;
                                param = 50;
                                template = 4;
                                break;

                            case 409:
                                time = 24 * 60 * 60 * 1000;
                                param = 75;
                                template = 30;
                                break;

                            case 410:
                                time = 24 * 60 * 60 * 1000;
                                param = 90;
                                template = 31;
                                break;

                            case 567:
                                time = 24 * 60 * 60 * 1000;
                                param = 120;
                                template = 35;
                                break;
                        }

                        Effect effect = new Effect(template, time, param);
                        em.setEffect(effect);
                        removeItem(item.index, item.getQuantity(), true);
                        if (taskId == TaskName.NV_DIET_SEN_TRU_COC && taskMain != null && taskMain.index == 1) {
                            taskNext();
                        }
                    }
                    if (item.template.isTypeStack()) {
                        getService().useItem(indexUI);
                    }
                }
            }
        } catch (Exception ex) {
            Log.error("use item err: " + ex.getMessage(), ex);
        } finally {
            lockItem.unlock();
        }
    }

    public void itemMountToBag(Message ms) {
        try {
            if (trade != null) {
                warningTrade();
                return;
            }
            byte indexUI = ms.reader().readByte();
            if (indexUI < 0 || indexUI > 4) {
                return;
            }
            if (this.mount[indexUI] == null) {
                return;
            }
            if (indexUI == 4 && (this.mount[0] != null || this.mount[1] != null || this.mount[2] != null
                    || this.mount[3] != null)) {
                serverDialog("Hãy tháo thú trang ra trước!");
                return;
            }
            for (int i = 0; i < numberCellBag; i++) {
                if (bag[i] == null) {
                    Item item = Converter.getInstance().toItem(mount[indexUI]);
                    item.isLock = true;
                    item.index = i;
                    this.bag[i] = item;
                    this.mount[indexUI] = null;
                    setAbility();
                    getService().itemMountToBag(indexUI, i);
                    zone.getService().loadMount(this);
                    setFashion();
                    if (item.isExpired()) {
                        removeItem(i, 1, true);
                    }
                    return;
                }
            }
            warningBagFull();
        } catch (Exception ex) {
            Log.error("item mount to bag err: " + ex.getMessage(), ex);
            serverDialog(ex.getMessage());
        }
    }

    public boolean isExpireMount() {
        if (this.mount[4] == null || this.mount[4].expire == -1 || this.mount[4].expire > System.currentTimeMillis()) {
            return false;
        }

        return true;
    }

    public void checkExpireMount() {
        try {
            int countMount = getCountMount();

            if (!isExpireMount() || getSlotNull() < countMount) {
                return;
            }

            for (int indexUI = 0; indexUI < 5; indexUI++) {
                if (this.mount[indexUI] == null) {
                    continue;
                }
                for (int a = 0; a < numberCellBag; a++) {
                    if (bag[a] == null) {
                        Item it = Converter.getInstance().toItem(mount[indexUI]);
                        it.isLock = true;
                        it.index = a;
                        this.mount[indexUI] = null;
                        this.bag[a] = it;
                        setAbility();
                        getService().itemMountToBag(indexUI, a);
                        zone.getService().loadMount(this);
                        setFashion();
                        if (it.isExpired()) {
                            removeItem(a, 1, true);
                        }
                        break;
                    }
                }
            }

        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void itemBagToBox(Message ms) {
        try {
            if (trade != null) {
                warningTrade();
                return;
            }
            if (!isHuman) {
                warningClone();
                return;
            }
            byte indexUI = ms.reader().readByte();
            if (indexUI < 0 || indexUI > this.numberCellBag) {
                return;
            }
            Item item = this.bag[indexUI];
            if (item == null) {
                return;
            }
            if (item.isExpired()) {
                removeItem(item.index, 1, true);
                return;
            }
            itemBagToBox(item);
        } catch (Exception ex) {
            Log.error("item bag to box err: " + ex.getMessage(), ex);
            serverDialog(ex.getMessage());
        }
    }

    public void itemBoxToBag(Message ms) {
        try {
            if (trade != null) {
                warningTrade();
                return;
            }
            if (!isHuman) {
                warningClone();
                return;
            }
            byte indexUI = ms.reader().readByte();
            int commandBox = getCommandBox();
            if (commandBox == RUONG_DO) {
                if (indexUI < 0 || indexUI > this.numberCellBox) {
                    return;
                }
                Item item = this.box[indexUI];
                if (item == null) {
                    return;
                }
                if (item.isExpired()) {
                    box[indexUI] = null;
                    return;
                }
                itemBoxToBag(item);
            } else if (commandBox == BO_SUU_TAP) {
                if (indexUI < 0 || indexUI > collectionBox.size()) {
                    return;
                }
                Item item = collectionBox.get(indexUI);
                if (item != null) {
                    if (collectionBox.size() >= 9) {
                        boolean isJirai = item.isJirai();
                        boolean isJumito = item.isJumito();
                        int upgradeMin = -1;
                        int count = 0;
                        for (Item itm : collectionBox) {
                            if (itm == null) {
                                continue;
                            }
                            boolean flag = false;
                            if (isJirai && itm.isJirai()) {
                                flag = true;
                            }
                            if (isJumito && itm.isJumito()) {
                                flag = true;
                            }
                            if (flag) {
                                count++;
                                if (upgradeMin == -1 || itm.upgrade < upgradeMin) {
                                    upgradeMin = itm.upgrade;
                                }
                            }
                        }
                        if (count == 9) {
                            int newItemID = -1;
                            if (isJirai) {
                                newItemID = ItemName.MAT_NA_JIRAI_;
                            }
                            if (isJumito) {
                                newItemID = ItemName.MAT_NA_JUMITO;
                            }
                            Item itm = getItemInMaskBox(newItemID);
                            if (itm != null) {
                                itm.upgrade = (byte) upgradeMin;
                                itm.initOption();
                                serverDialog(String.format("Chúc mừng bạn đã nâng cấp thành công cải trang %s cấp %s", itm.template.name, itm.upgrade));
                            } else {
                                itm = ItemFactory.getInstance().newItem(newItemID);
                                itm.yen = 5;
                                itm.upgrade = (byte) upgradeMin;
                                itm.initOption();
                                maskBox.add(itm);
                                serverDialog(String.format("Chúc mừng bạn đã tạo thành công %s", itm.template.name));
                            }
                        }
                    } else {
                        serverDialog("Hãy sưu tập đủ 9 món để sử dụng");
                    }
                }
            } else if (commandBox == CAI_TRANG) {
                if (indexUI < 0 || indexUI > maskBox.size()) {
                    return;
                }
                Item item = maskBox.get(indexUI);
                if (item != null) {
                    setMask(item);
                    setMaskId(item.id);
                    setAbility();
                    setFashion();
                    serverDialog("Mặc Thành Công Cải Trang " + item.template.name);
                }
            } else if (commandBox == DOI_LONG_DEN_XU) {
                Event event = Event.getEvent();
                event.doiLongDen(this, Event.DOI_BANG_XU, indexUI);
            } else if (commandBox == DOI_LONG_DEN_LUONG) {
                Event event = Event.getEvent();
                event.doiLongDen(this, Event.DOI_BANG_LUONG, indexUI);
            } else if (commandBox == THANG_NGUONG_KHAI_HOA) {
                if (indexUI < 0 || indexUI > this.equipment.length) {
                    return;
                }
                Item item = this.equipment[indexUI];
                int cap = 0;
                if (item != null) {
                    for (ItemOption option : item.options) {
                        if (option.optionTemplate.id == 156) {
                            cap = option.param;
                            break;
                        }
                    }
                    if (cap >= 9) {
                        serverDialog("Ngưỡng Khai Hoá Đã Đạt Mức Tối Đa");
                        return;
                    }
                    int[] xu = new int[]{1000000, 1500000, 2000000, 3000000, 4000000, 5000000, 7000000, 10000000, 15000000};
                    int[] luong = new int[]{1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 10000};
                    int[] tile = new int[]{70, 50, 40, 30, 25, 20, 10, 7, 3};
                    tempIndex = indexUI;
                    setConfirmPopup(new ConfirmPopup(CMDConfirmPopup.THANG_NGUONG_KHAI_HOA, String.format("Thăng Ngưỡng Khai Hoá Lên Cấp " + (cap + 1) + "\n - Cần %d Xu Và %d Lượng \n - Tỉ Lệ Thành Công: %s", xu[cap], luong[cap], tile[cap] + "%")));
                    getService().openUIConfirmID();
                }
            } else if (commandBox == THANG_CAP_KHAI_HOA) {
                if (indexUI < 0 || indexUI > this.equipment.length) {
                    return;
                }
                Item item = this.equipment[indexUI];
                tempIndex = indexUI;
                if (item != null) {
                    int capthangnguong = 0;
                    int capkhaihoa = 0;
                    for (ItemOption option : item.options) {
                        switch (option.optionTemplate.id) {
                            case 156:
                                capthangnguong = option.param;
                                break;
                            case 157:
                                capkhaihoa = option.param;
                                break;
                        }
                    }
                    if (capkhaihoa >= capthangnguong) {
                        serverDialog("Vui Lòng Thăng Ngương Khai Hoá Để Nâng Cấp ");
                        return;
                    }
                    if (capthangnguong < 1) {
                        serverDialog("Vui Lòng Thăng Ngưỡng Trước Khi Nâng Cấp ");
                        return;
                    }
                    if (capkhaihoa >= 9) {
                        serverDialog("Cấp Khai Hoá Đã Đạt Cấp Tối Đa");
                        return;
                    }
                    boolean flag = false;
                    for (ItemOption option : item.options) {
                        if (option.optionTemplate.id == 157 && option.param == 0) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        menus.clear();
                        menus.add(new Menu(CMDMenu.EXECUTE, "Sức Mạnh", () -> {
                            int[] xu1 = new int[]{1000000, 1500000, 2000000, 3000000, 4000000, 5000000, 7000000, 10000000, 20000000};
                            int[] luong1 = new int[]{500, 1000, 1500, 2000, 3000, 4000, 5000, 7000, 10000};
                            int[] tile1 = new int[]{70, 50, 40, 30, 25, 20, 10, 5, 2};
                            int[] da1 = new int[]{5, 10, 20, 30, 40, 50, 60, 70, 80};
                            int capkhaihoa1 = 0;
                            setConfirmPopup(new ConfirmPopup(CMDConfirmPopup.THANG_CAP_KHAI_HOA_1, String.format("Thăng Cấp Khai Hoá Lên Cấp " + (capkhaihoa1 + 1) + "\n - Cần %d Xu Và %d Lượng %d đá Sát Thương \n - Tỉ Lệ Thành Công: %s", xu1[capkhaihoa1], luong1[capkhaihoa1], da1[capkhaihoa1], tile1[capkhaihoa1] + "%")));
                            getService().openUIConfirmID();
                        }));
                        menus.add(new Menu(CMDMenu.EXECUTE, "Phòng Ngự", () -> {
                            int[] xu2 = new int[]{1000000, 1500000, 2000000, 3000000, 4000000, 5000000, 7000000, 10000000, 20000000};
                            int[] luong2 = new int[]{500, 1000, 1500, 2000, 3000, 4000, 5000, 7000, 10000};
                            int[] tile2 = new int[]{70, 50, 40, 30, 25, 20, 10, 5, 2};
                            int[] da2 = new int[]{5, 10, 20, 30, 40, 50, 60, 70, 80};
                            int capkhaihoa2 = 0;
                            setConfirmPopup(new ConfirmPopup(CMDConfirmPopup.THANG_CAP_KHAI_HOA_2, String.format("Thăng Cấp Khai Hoá Lên Cấp " + (capkhaihoa2 + 1) + "\n - Cần %d Xu Và %d Lượng %d đá Phòng Ngự \n - Tỉ Lệ Thành Công: %s", xu2[capkhaihoa2], luong2[capkhaihoa2], da2[capkhaihoa2], tile2[capkhaihoa2] + "%")));
                            getService().openUIConfirmID();
                        }));
                        menus.add(new Menu(CMDMenu.EXECUTE, "Sinh Mệnh", () -> {
                            int[] xu3 = new int[]{1000000, 1500000, 2000000, 3000000, 4000000, 5000000, 7000000, 10000000, 20000000};
                            int[] luong3 = new int[]{500, 1000, 1500, 2000, 3000, 4000, 5000, 7000, 10000};
                            int[] tile3 = new int[]{70, 50, 40, 30, 25, 20, 10, 5, 2};
                            int[] da3 = new int[]{5, 10, 20, 30, 40, 50, 60, 70, 80};
                            int capkhaihoa3 = 0;
                            setConfirmPopup(new ConfirmPopup(CMDConfirmPopup.THANG_CAP_KHAI_HOA_3, String.format("Thăng Cấp Khai Hoá Lên Cấp " + (capkhaihoa3 + 1) + "\n - Cần %d Xu Và %d Lượng %d đá Sinh Mệnh \n - Tỉ Lệ Thành Công: %s", xu3[capkhaihoa3], luong3[capkhaihoa3], da3[capkhaihoa3], tile3[capkhaihoa3] + "%")));
                            getService().openUIConfirmID();
                        }));
                        getService().openUIMenu();
                    } else {
                        for (ItemOption option : item.options) {
                            if (option.optionTemplate.id == 170) {
                                int[] xu = new int[]{1000000, 1500000, 2000000, 3000000, 4000000, 5000000, 7000000, 10000000, 20000000};
                                int[] luong = new int[]{500, 1000, 1500, 2000, 3000, 4000, 5000, 7000, 10000};
                                int[] tile = new int[]{70, 50, 40, 30, 25, 20, 10, 5, 2};
                                int[] da = new int[]{5, 10, 20, 30, 40, 50, 60, 70, 80};
                                setConfirmPopup(new ConfirmPopup(CMDConfirmPopup.THANG_CAP_KHAI_HOA170, String.format("Thăng Cấp Khai Hoá Lên Cấp " + (capkhaihoa + 1) + "\n - Cần %d Xu Và %d Lượng Và %d đá Sinh Mệnh\n - Tỉ Lệ Thành Công: %s", xu[capkhaihoa], luong[capkhaihoa], da[capkhaihoa], tile[capkhaihoa] + "%")));
                                getService().openUIConfirmID();
                                break;
                            }
                            if (option.optionTemplate.id == 171) {
                                int[] xu = new int[]{1000000, 1500000, 2000000, 3000000, 4000000, 5000000, 7000000, 10000000, 20000000};
                                int[] luong = new int[]{500, 1000, 1500, 2000, 3000, 4000, 5000, 7000, 10000};
                                int[] tile = new int[]{70, 50, 40, 30, 25, 20, 10, 5, 2};
                                int[] da = new int[]{5, 10, 20, 30, 40, 50, 60, 70, 80};
                                setConfirmPopup(new ConfirmPopup(CMDConfirmPopup.THANG_CAP_KHAI_HOA171, String.format("Thăng Cấp Khai Hoá Lên Cấp " + (capkhaihoa + 1) + "\n - Cần %d Xu Và %d Lượng Và %d đá Phòng Ngự \n - Tỉ Lệ Thành Công: %s", xu[capkhaihoa], luong[capkhaihoa], da[capkhaihoa], tile[capkhaihoa] + "%")));
                                getService().openUIConfirmID();
                                break;
                            }
                            if (option.optionTemplate.id == 172) {
                                int[] xu = new int[]{1000000, 1500000, 2000000, 3000000, 4000000, 5000000, 7000000, 10000000, 20000000};
                                int[] luong = new int[]{500, 1000, 1500, 2000, 3000, 4000, 5000, 7000, 10000};
                                int[] tile = new int[]{70, 50, 40, 30, 25, 20, 10, 5, 2};
                                int[] da = new int[]{5, 10, 20, 30, 40, 50, 60, 70, 80};
                                setConfirmPopup(new ConfirmPopup(CMDConfirmPopup.THANG_CAP_KHAI_HOA172, String.format("Thăng Cấp Khai Hoá Lên Cấp " + (capkhaihoa + 1) + "\n - Cần %d Xu Và %d Lượng Và %d đá Sát Thương \n - Tỉ Lệ Thành Công: %s", xu[capkhaihoa], luong[capkhaihoa], da[capkhaihoa], tile[capkhaihoa] + "%")));
                                getService().openUIConfirmID();
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.error("item box to bag err: " + ex.getMessage(), ex);
            serverDialog(ex.getMessage());
        }
    }
    public int tempIndex = -1;

    public Item getItemInMaskBox(int id) {
        for (Item item : maskBox) {
            if (item != null && item.id == id) {
                return item;
            }
        }
        return null;
    }

    public void itemBodyToBag(Message ms) {
        try {
            if (trade != null) {
                warningTrade();
                return;
            }
            byte indexUI = ms.reader().readByte();
            if (indexUI >= 0) {
                if (indexUI < 16) {
                    Equip equip = this.equipment[indexUI];
                    if (equip != null) {
                        for (int i = 0; i < numberCellBag; i++) {
                            if (bag[i] == null) {
                                Item item = Converter.getInstance().toItem(equip);
                                item.isLock = true;
                                item.index = i;
                                this.bag[i] = item;
                                this.equipment[indexUI] = null;

                                // xoa hieu ung long den
                                if (item.template.type == ItemTemplate.TYPE_THUNUOI) {
                                    if (item.id == 568 || item.id == 569 || item.id == 570 || item.id == 571) {
                                        Effect eff = null;
                                        switch (item.id) {
                                            case 568:
                                                eff = em.findByID((byte) 38);
                                                break;

                                            case 569:
                                                eff = em.findByID((byte) 36);
                                                break;

                                            case 570:
                                                eff = em.findByID((byte) 37);
                                                break;

                                            case 571:
                                                eff = em.findByID((byte) 39);
                                                break;
                                        }
                                        if (eff != null) {
                                            getService().removeEffect(eff);
                                            zone.getService().playerRemoveEffect(this, eff);
                                            em.removeEffect(eff);
                                        }
                                    }
                                }
                                setFashion();
                                setAbility();
                                getService().itemBodyToBag(indexUI, i);
                                if (item.isExpired()) {
                                    removeItem(i, 1, true);
                                }
                                return;
                            }
                        }
                        warningBagFull();
                        return;
                    }
                } else {
                    int index = indexUI - 16;
                    if (index < 16) {
                        Equip equip = this.fashion[index];
                        if (equip != null) {
                            for (int i = 0; i < numberCellBag; i++) {
                                if (bag[i] == null) {
                                    Item item = Converter.getInstance().toItem(equip);
                                    item.isLock = true;
                                    item.index = i;
                                    this.bag[i] = item;
                                    this.fashion[index] = null;
                                    setFashion();
                                    setAbility();
                                    getService().itemBodyToBag(indexUI, i);
                                    if (item.isExpired()) {
                                        removeItem(i, 1, true);
                                    }
                                    return;
                                }
                            }
                            warningBagFull();
                            return;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public boolean addItemToBag(Item item) {
        if (item == null) {
            return false;
        }
        int index = getIndexItemByIdInBag(item.id, item.isLock);
        if (index == -1 || !bag[index].template.isUpToUp || bag[index].hasExpire()) {
            for (int i = 0; i < this.numberCellBag; i++) {
                if (bag[i] == null) {
                    item.index = i;
                    bag[i] = item;
                    getService().addItem(item);
                    return true;
                }
            }
        } else {
            this.bag[index].add(item.getQuantity());
            if (this.bag[index].has()) {
                getService().addItem(this.bag[index]);
            } else {
                removeItem(index, bag[index].getQuantity(), true);
            }
            return true;
        }
        return false;
    }

    public boolean upgradeMount() {
        Mount mount = this.mount[4];
        if (mount == null) {
            return false;
        }
        if (mount.upgrade < 99) {
            serverMessage("Thú cưới chưa đạt cấp độ tối đa");
            return false;
        } else if (mount.sys < 4) {
            if (30 / (mount.sys + 1) > (int)NinjaUtils.nextInt(100)) {
                mount.sys++;
                mount.upgrade = 0;
                for (byte i = 0; i < mount.options.size(); i++) {
                    ItemOption op = mount.options.get(i);
                    if (op.optionTemplate.id == 65) {
                        op.param = 0;
                    } // else if (op.optionTemplate.id == 119 || op.optionTemplate.id == 120) {
                    // op.param += 100;
                    // }
                    else if (op.optionTemplate.id != 66) {
                        for (byte j = 0; j < ItemManager.MOUNT_OPTION_ID.length; j++) {
                            if (ItemManager.MOUNT_OPTION_ID[j] == op.optionTemplate.id) {
                                op.param -= (ItemManager.MOUNT_OPTION_PARAM[j] * 8);
                                break;
                            }
                        }
                    }
                }

                if (mount.sys == 4 && this.mount[4].id == ItemName.HAC_NGUU) {
                    this.mount[4].id = ItemName.KIM_NGUU;
                }

                zone.getService().loadMount(this);
                serverMessage("Nâng cấp thành công, thú cưới được tặng 1 sao");
            } else {
                if (this.mount[4].id == ItemName.HAC_NGUU) {
                    serverMessage("Nâng cấp thất bại, hao phí 1 Tiến hoá thảo");
                } else {
                    serverMessage("Nâng cấp thất bại, hao phí 1 Chuyển tinh thạch");
                }
            }
        } else {
            serverMessage("Không thể nâng thêm sao");
            return false;
        }
        return true;
    }

    public boolean openMoreOptionForMount() {
        Mount mount = this.mount[4];
        if (mount == null) {
            serverMessage("Vui lòng đeo thú cưỡi trước khi thực hiện");
        } else if (mount.upgrade < 99) {
            serverMessage("Thú cưới chưa đạt cấp độ tối đa");
        } else if (mount.sys < 4) {
            serverMessage("Thú cưỡi cần đạt 5 sao mới có thể thực hiện");
        } else {
            if (mount.id == ItemName.HOA_KY_LAN && mount.options.size() < 11) {
                if ((int)NinjaUtils.nextInt(20) == 0) {
                    boolean isHasOption58 = false;
                    boolean isHasOption128 = false;
                    boolean isHasOption127 = false;
                    boolean isHasOption130 = false;
                    boolean isHasOption131 = false;
                    boolean isHasOption173 = false;
                    boolean isHasOption174 = false;
                    boolean isHasOption175 = false;

                    for (ItemOption op : mount.options) {
                        if (op.optionTemplate.id == 58) {
                            isHasOption58 = true;
                        }
                        if (op.optionTemplate.id == 128) {
                            isHasOption128 = true;
                        }
                        if (op.optionTemplate.id == 127) {
                            isHasOption127 = true;
                        }
                        if (op.optionTemplate.id == 130) {
                            isHasOption130 = true;
                        }
                        if (op.optionTemplate.id == 131) {
                            isHasOption131 = true;
                        }
                        if (op.optionTemplate.id == 173) {
                            isHasOption173 = true;
                        }
                        if (op.optionTemplate.id == 174) {
                            isHasOption174 = true;
                        }
                        if (op.optionTemplate.id == 175) {
                            isHasOption175 = true;
                        }
                    }

                    if (!isHasOption58) {
                        mount.options.add(new ItemOption(58, 20));
                    }
                    if (!isHasOption128) {
                        mount.options.add(new ItemOption(128, 5));
                    }
                    if (!isHasOption127) {
                        mount.options.add(new ItemOption(127, 10));
                    }
                    if (!isHasOption130) {
                        mount.options.add(new ItemOption(130, 10));
                    }
                    if (!isHasOption131) {
                        mount.options.add(new ItemOption(131, 10));
                    }
                    if (!isHasOption173) {
                        mount.options.add(new ItemOption(173, (int)NinjaUtils.nextInt(1,10)));
                    }
                    if (!isHasOption174) {
                        mount.options.add(new ItemOption(174, 0));
                    }
                    if (!isHasOption175) {
                        mount.options.add(new ItemOption(175, 0));
                    }

                    serverMessage("Khai mở thành công, thú cưỡi được tăng thêm chỉ số");
                    return true;
                } else {
                    serverMessage("Khai mở thất bại, hao phí 1 Khai Thú Lệnh");
                    return true;
                }
            } else if (mount.id == ItemName.BACH_HO && mount.options.size() < 8) {
                if ((int)NinjaUtils.nextInt(20) == 0) {
                    boolean isHasOption58 = false;
                    boolean isHasOption94 = false;
                    for (ItemOption op : mount.options) {
                        if (op.optionTemplate.id == 58) {
                            isHasOption58 = true;
                        }
                        if (op.optionTemplate.id == 94) {
                            isHasOption94 = true;
                        }
                    }

                    if (!isHasOption58) {
                        mount.options.add(new ItemOption(58, 20));
                    }

                    if (!isHasOption94) {
                        mount.options.add(new ItemOption(94, 20));
                    }
                    serverMessage("Khai mở thành công, thú cưỡi được tăng thêm chỉ số");
                    return true;
                } else {
                    serverMessage("Khai mở thất bại, hao phí 1 Khai Thú Lệnh");
                    return true;
                }
            } else if (mount.id == ItemName.KIM_NGUU && mount.options.size() == 6) {
                if ((int)NinjaUtils.nextInt(20) == 0) {
                    mount.options.add(new ItemOption(128, (int)NinjaUtils.nextInt(8, 10)));
                    zone.getService().loadMount(this);
                    setAbility();
                    serverMessage("Khai mở thành công, thú cưỡi được tăng thêm một chỉ số");
                    return true;
                } else {
                    serverMessage("Khai mở thất bại, hao phí 1 Khai Thú Lệnh");
                    return true;
                }
            } else if (mount.id == ItemName.PHUONG_HOANG_BANG && mount.options.size() == 6) {
                if ((int)NinjaUtils.nextInt(20) == 0) {
                    mount.options.add(new ItemOption(134, (int)NinjaUtils.nextInt(5)));
                    zone.getService().loadMount(this);
                    setAbility();
                    serverMessage("Khai mở thành công, thú cưỡi được tăng thêm một chỉ số");
                    return true;
                } else {
                    serverMessage("Khai mở thất bại, hao phí 1 Khai Thú Lệnh");
                    return true;
                }
            } else {
                serverMessage("Thú cưỡi không có chỉ số ẩn, không thể khai mở");
            }
        }
        return false;
    }

    public void itemBagToBox(Item item) {
        int index = getIndexItemByIdInBox(item.id, item.isLock);
        if (index == -1 || !box[index].template.isUpToUp || box[index].hasExpire()
                || item.isLock != box[index].isLock) {
            for (int i = 0; i < this.numberCellBox; i++) {
                if (box[i] == null) {
                    int indexM = item.index;
                    item.index = i;
                    box[i] = item;
                    bag[indexM] = null;
                    getService().itemBagToBox(indexM, i);
                    return;
                }
            }
            serverDialog("Rương đồ không đủ chỗ trống");
            return;
        } else {
            box[index].add(item.getQuantity());
            getService().itemBagToBox(item.index, index);
            bag[item.index] = null;
            return;
        }
    }

    public void itemBoxToBag(Item item) {
        int index = getIndexItemByIdInBag(item.id, item.isLock);
        if (index == -1 || !bag[index].template.isUpToUp || bag[index].hasExpire()
                || item.isLock != bag[index].isLock) {
            for (int i = 0; i < this.numberCellBag; i++) {
                if (bag[i] == null) {
                    int indexM = item.index;
                    item.index = i;
                    bag[i] = item;
                    box[indexM] = null;
                    getService().itemBoxToBag(indexM, i);
                    return;
                }
            }
            warningBagFull();
            return;
        } else {
            bag[index].add(item.getQuantity());
            box[item.index] = null;
            getService().itemBoxToBag(item.index, index);
            return;
        }
    }

    public void updateItem(int index, int quantity) {
        if (bag[index] != null) {
            bag[index].add(quantity);
            if (!bag[index].has()) {
                getService().removeItem(index);
                bag[index] = null;
            } else {
                getService().updateItem(bag[index]);
            }
        }
    }

    public void boxSort() {
        try {
            Vector items = new Vector();
            for (int i = 0; i < this.box.length; i++) {
                Item item = this.box[i];
                if (item != null && item.template.isUpToUp && !item.hasExpire()) {
                    items.addElement(item);
                }
            }
            for (int i = 0; i < items.size(); i++) {
                Item itemi = (Item) items.elementAt(i);
                if (itemi != null) {
                    for (int j = i + 1; j < items.size(); j++) {
                        Item itemj = (Item) items.elementAt(j);
                        if (itemj != null && itemi.template.equals(itemj.template) && itemi.isLock == itemj.isLock) {
                            itemi.add(itemj.getQuantity());
                            this.box[itemj.index] = null;
                            items.setElementAt(null, j);
                        }
                    }
                }
            }
            for (int i = 0; i < this.box.length; i++) {
                if (this.box[i] != null) {
                    for (int j = 0; j <= i; j++) {
                        if (this.box[j] == null) {
                            this.box[j] = this.box[i];
                            this.box[j].index = j;
                            this.box[i] = null;
                            break;
                        }
                    }
                }
            }
            getService().boxSort();
        } catch (Exception e) {
            Log.error("err: " + e.getMessage(), e);
        }
    }

    public void bagSort() {
        if (trade != null) {
            warningTrade();
            return;
        }
        try {
            Vector items = new Vector();
            for (int i = 0; i < this.bag.length; i++) {
                Item item = this.bag[i];
                if (item != null && item.template.isUpToUp && !item.hasExpire()) {
                    items.addElement(item);
                }
            }
            for (int i = 0; i < items.size(); i++) {
                Item itemi = (Item) items.elementAt(i);
                if (itemi != null) {
                    for (int j = i + 1; j < items.size(); j++) {
                        Item itemj = (Item) items.elementAt(j);
                        if (itemj != null && itemi.template.equals(itemj.template) && itemi.isLock == itemj.isLock) {
                            itemi.add(itemj.getQuantity());
                            this.bag[itemj.index] = null;
                            items.setElementAt(null, j);
                        }
                    }
                }
            }
            for (int i = 0; i < this.bag.length; i++) {
                if (this.bag[i] != null) {
                    for (int j = 0; j <= i; j++) {
                        if (this.bag[j] == null) {
                            this.bag[j] = this.bag[i];
                            this.bag[j].index = j;
                            this.bag[i] = null;
                            break;
                        }
                    }
                }
            }
            getService().bagSort();
        } catch (Exception e) {
            Log.error("err: " + e.getMessage(), e);
        }
    }

    public void setXY(short... xy) {
        this.x = xy[0];
        this.y = xy[1];
    }

    public boolean isDontMove() {
        return isIce || isWind || isDontMove;
    }

    public void move(Message ms) {
        try {
            short x = ms.reader().readShort();
            short y = ms.reader().readShort();
            if (zone != null) {
                zone.move(this, x, y);
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }

    }

    public boolean isCrossMap(short cx, short cy) {
        if (!this.isDead) {
            if (!zone.tilemap.tileTypeAt(cx, cy - 1, TileMap.T_WATERFLOW)) {
                boolean isThroughtMap = false;
                if (this.zone.tilemap.tileId == 4 && cy >= this.zone.tilemap.pxh - 48) {
                    isThroughtMap = true;
                } else if (cx <= 0 || cx >= this.zone.tilemap.pxw
                        || cy <= 0 || cy >= this.zone.tilemap.pxh) {
                    isThroughtMap = true;
                } else if (zone.tilemap.isWood(cx, cy - 1)) {
                    isThroughtMap = true;
                } else if (zone.tilemap.isRock(cx, cy - 1)) {
                    isThroughtMap = true;
                } else if ((cx < 24 || cx > this.zone.tilemap.pxw - 24)
                        && !zone.tilemap.isInWaypoint(cx, cy)) {
                    isThroughtMap = true;
                } else if (cy >= this.zone.tilemap.pxh - 24
                        && !zone.tilemap.isInWaypoint(cx, cy)) {
                    isThroughtMap = true;
                }
                return isThroughtMap;
            }
        }
        return false;
    }

    public void chatGlobal(Message ms) {
        if (chatGlobal != null) {
            chatGlobal.read(ms);
            chatGlobal.wordFilter();
            chatGlobal.send();
        }
    }

    public void chatPrivate(Message ms) {
        try {
            String to = ms.reader().readUTF();
            String text = ms.reader().readUTF();
            Char _char = findCharByName(to);
            if (_char == null || this.name.equals(to)) {
                return;
            }
            if ((this.user.isAdmin() || this.user.isMod()) && text.startsWith("ban") && text.length() <= 6) {
                if (!this.user.isAdmin()) {
                    serverDialog("Dành cho admin thôi nhé bug cái lol mẹ mày");
                    return;
                }
                setInput(new InputDialog(CMDInputDialog.EXECUTE, "Thời hạn (giờ), bỏ trống nếu vĩnh viễn", () -> {
                    int hours = 0;
                    try {
                        hours = Integer.parseInt(input.getText());
                    } catch (Exception e) {
                    }
                    if (hours > 0) {
                        _char.user.lock(hours);
                        _char.user.addLog(this.name, "Khoá tài khoản trong " + hours + " giờ");
                    } else {
                        _char.user.lock();
                        _char.user.addLog(this.name, "Khoá tài khoản vĩnh viễn");
                    }
                    serverMessage(String.format("Đã khóa tài khoản của nhân vật %s!", _char.name));
                }));
                getService().showInputDialog();
                return;
            }
            if (text.length() > 300) {
                return;
            }
            text = text.replace("\n", " ");
            _char.getService().chat(this.name, text);
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void tradeInvite(Message ms) {
        try {
            if (user.kh == 0) {
                service.serverDialog("Tài khoản chưa được kích hoạt. Vui lòng truy cập zalo để kích hoạt tài khoản.");
                return;
            }
            if (!isHuman) {
                return;
            }
            if (isDead) {
                return;
            }
            if (zone.tilemap.isNotTrade()) {
                serverMessage("Không thể sử dụng tính năng này trong lôi đài.");
                return;
            }
            int charId = ms.reader().readInt();
            Char _char = zone.findCharById(charId);

            if (_char != null) {
                if (_char == this) {
                    return;
                }
                int distance = NinjaUtils.getDistance(_char.x, _char.y, this.x, this.y);
                if (distance > 100) {
                    serverDialog("Khoảng cách quá xa!");
                    return;
                }
                if (_char.trade != null) {
                    serverDialog("Người này đang giao dịch với người khác.");
                    return;
                }
                PlayerInvite p = _char.invite.findCharInvite(Invite.GIAO_DICH, this.id);
                if (p != null) {
                    serverDialog("Không thể mời giao dịch liên tục. Vui lòng thử lại sau 30s nữa.");
                    return;
                }
                _char.invite.addCharInvite(Invite.GIAO_DICH, this.id, 30);
                _char.getService().tradeInvite(this.id);
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public synchronized void selectCard(Message ms) {
        try {
            byte index = ms.reader().readByte();

            if (selectCard != null) {
                if (index < 0 || index > 9) {
                    return;
                }
                if (selectCard.select(this, index)) {
                    if (Event.isKoroKing()) {
                        KoroKing.addTrophy(this);
                    }
                }
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void tradeItemLock(Message ms) {
        if (user.kh == 0) {
            service.serverDialog("Tài khoản chưa được kích hoạt.");
            return;
        }
        if (trade == null || myTrade == null) {
            return;
        }
        if (!myTrade.isLock) {
            try {
                int xu = ms.reader().readInt();
                byte itemLength = ms.reader().readByte();
                if (xu > 0 && xu <= this.coin) {
                    this.myTrade.coinTradeOrder = xu;
                }

                String errFormat = "";
                boolean isMyMissTake = true;

                Char partner = partnerTrade.getChar();

                if (this.getCoin() < xu) {
                    errFormat = "%s không đủ xu để giao dịch";
                } else if (xu > 500000000) {
                    errFormat = "%s đã giao dịch quá giới hạn 500.000.000 xu";
                } else if (itemLength > partner.getSlotNull()) {
                    isMyMissTake = false;
                    errFormat = "%s không đủ chỗ trống trong hành trang";
                } else if (partner.getCoin() + xu > partner.coinMax) {
                    isMyMissTake = false;
                    errFormat = "%s đã đạt giới hạn chứa xu, vui lòng giao dịch ít hơn";
                }

                if (!errFormat.equals("")) {
                    tradeClose();
                    serverMessage(String.format(errFormat, isMyMissTake ? "Bạn" : partner.name));
                    partner.serverMessage(String.format(errFormat, isMyMissTake ? this.name : "Bạn"));
                    return;
                }

                ArrayList<Integer> list = new ArrayList<>();
                this.myTrade.itemTradeOrder = new Vector<>();
                for (int i = 0; i < itemLength; i++) {
                    int index = ms.reader().readByte();
                    if (index < 0 || index >= this.numberCellBag) {
                        continue;
                    }
                    if (bag[index] != null) {
                        if (NinjaUtils.checkExist(list, index)) {
                            continue;
                        }
                        this.myTrade.itemTradeOrder.add(bag[index]);
                        list.add(index);
                    }

                    if (bag[index].template.isBlackListItem()) {
                        serverDialog("Không thể giao vật phẩm này, chỉ có thể bán.");
                        return;
                    }
                }
                trade.tradeItemLock(myTrade);
                myTrade.isLock = true;
            } catch (Exception ex) {
                Log.error("err: " + ex.getMessage(), ex);
            }
        }
    }

    public void tradeClose() {
        try {
            if (user.kh == 0) {
                service.serverDialog("Tài khoản chưa được kích hoạt. Vui lòng truy cập zalo để kích hoạt tài khoản.");
                return;
            }
            if (this.trade != null) {
                trade.closeUITrade();
            } else {
                getService().tradeCancel();
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void tradeAccept() {
        if (user.kh == 0) {
            service.serverDialog("Tài khoản chưa được kích hoạt. Vui lòng truy cập zalo để kích hoạt tài khoản.");
            return;
        }
        if (trade == null || myTrade == null) {
            return;
        }
        if (!myTrade.accept && myTrade.isLock) {
            myTrade.accept = true;
            (myTrade == trade.traders[0] ? trade.traders[1] : trade.traders[0]).player.getService().tradeAccept();
        }
        if (trade.traders[0].accept && trade.traders[1].accept) {
            try {
                trade.update();
            } catch (Exception ex) {
                Log.error("err: " + ex.getMessage(), ex);
            }
        }
    }

    public void acceptInviteTrade(Message ms) {
        try {
            if (user.kh == 0) {
                service.serverDialog("Tài khoản chưa được kích hoạt. Vui lòng truy cập zalo để kích hoạt tài khoản.");
                return;
            }
            if (!isHuman) {
                return;
            }
            int charId = ms.reader().readInt();
            Char _char = zone.findCharById(charId);
            if (_char != null) {
                if (_char.trade != null) {
                    serverDialog("Người này đang giao dịch với người khác.");
                    return;
                }
                PlayerInvite c = invite.findCharInvite(Invite.GIAO_DICH, _char.id);
                if (c == null) {
                    serverDialog("Đã hết thời gian để chấp nhận yêu cầu giao dịch.");
                    return;
                }
                Trade trade = new Trade();
                this.trade = trade;
                _char.trade = trade;

                this.myTrade = trade.traders[0] = new Trader(this);
                _char.myTrade = trade.traders[1] = new Trader(_char);

                this.partnerTrade = _char.myTrade;
                _char.partnerTrade = this.myTrade;

                trade.openUITrade();
            } else {
                serverDialog("Không tìm thấy người này.");
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void cancelInviteTrade() {

    }

    public void chatPublic(Message ms) {
        try {
            String text = ms.reader().readUTF();
            if (text.length() > 300) {
                return;
            }
            text = text.replace("\n", " ");
            if (AdminService.getInstance().process(this, text)) {
                return;
            }
            zone.getService().chat(this.id, text);
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void chatParty(Message ms) {
        try {
            if (this.group == null) {
                return;
            }
            String text = ms.reader().readUTF();
            if (text.length() > 300) {
                return;
            }
            text = text.replace("\n", " ");
            group.getGroupService().chat(this.name, text);
        } catch (IOException ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void chatClan(Message ms) {
        if (this.clan != null) {
            try {
                String text = ms.reader().readUTF();
                if (text.length() > 300) {
                    return;
                }
                text = text.replace("\n", " ");
                clan.getClanService().chat(this.name, text);
            } catch (Exception ex) {
                Log.error("err: " + ex.getMessage(), ex);
            }
        }
    }

    public boolean isDontAttack() {
        return isIce || isWind;
    }

    public void attackMonster(Message ms) {
        try {
            if (this.isDead) {
                return;
            }
            long currentTimeMillis = System.currentTimeMillis();
            if (selectedSkill != null && !selectedSkill.isCooldown()) {
                selectedSkill.lastTimeUseThisSkill = currentTimeMillis;
            } else {
                return;
            }
            if ((this.faction == 0 && mapId == 99) || (this.faction == 1 && mapId == 103)) {
                return;
            }
            if (isDontAttack()) {
                return;
            }
            if (this.equipment[1] == null) {
                return;
            }
            if (selectedSkill == null) {
                return;
            }
            if (clone != null && !clone.isDead && clone.isNhanBan) {
                ms.reader().mark(10000);
                clone.attackMonster(ms);
                ms.reader().reset();
            }
            ArrayList<Mob> mobs = new ArrayList<>();
            int dx = selectedSkill.dx;
            int dy = selectedSkill.dy;
            int left = this.x - dx;
            int right = this.x + dx;
            int top = this.y - dy - 75;
            int bottom = this.y + dy;
            Mob mobFocus = null;
            Vector<Mob> m = new Vector<>();
            while (ms.reader().available() > 0) {
                int id = ms.reader().readUnsignedByte();
                Mob mob = null;
                if (id == 127) {
                    mob = this.mob;
                } else {
                    try {
                        mob = zone.findMoLivebByID(id);
                    } catch (Exception e) {
                    }
                }
                if (mob == null) {
                    continue;
                }
                if (!isMeCanAttackNpc(mob)) {
                    continue;
                }
                if (mob != null && mob.hp > 0) {
                    m.add(mob);
                }
            }

            if (m.size() > 0) {
                Mob mob = m.get(0);
                int skillTemplateId = selectedSkill.template.id;
                if (skillTemplateId == 42) {
                    setXY(mob.x, mob.y);
                    zone.getService().teleport(this);
                }
                int add = 0;
                if (mob.template.type == 4) {
                    add += 50;
                }
                int rangeMove = mob.template.rangeMove;
                if (mob.x >= (left - rangeMove) && mob.x <= (right + rangeMove) && mob.y >= (top - rangeMove - add)
                        && mob.y <= (bottom + rangeMove)) {
                    mobFocus = mob;
                    if (!mobs.contains(mob)) {
                        mobs.add(mob);
                    }
                }
            }
            byte maxFight = selectedSkill.maxFight;
            if (mobFocus != null) {
                int lent = m.size();
                if (lent > maxFight) {
                    lent = maxFight;
                }
                for (int i = 0; i < lent; i++) {
                    Mob mob2 = m.get(i);
                    if (mob2.status != 1 && mob2.status != 0 && !mob2.equals(mobFocus) && mobFocus.x - 100 <= mob2.x
                            && mob2.x <= mobFocus.x + 100 && mobFocus.y - 100 <= mob2.y && mob2.y <= mobFocus.y + 100) {
                        if (!mobs.contains(mob2)) {
                            mobs.add(mob2);
                        }
                    }
                }
            } else {
                ArrayList<Mob> mobss = new ArrayList<>();
                for (int i = 0; i < m.size(); i++) {
                    Mob mob2 = m.get(i);
                    mobss.add(mob2);
                    zone.getService().attackMonster(-1, false, mob2);
                }
                zone.getService().setSkillPaint_1(mobss, this, (byte) selectedSkill.template.id);
            }
            if (mobs.isEmpty()) {
                return;
            }
            int manaUse = selectedSkill.manaUse;
            if (this.mp < manaUse) {
            serverMessage("Bạn cần phải có MP để sử dụng chiêu.");
                return;
            }
            addMp(-manaUse);
            getService().updateMp();
            attackMonster(mobs);
            return;
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }

    }

    public void useSkillBuff(byte dir, Skill skill) {
        if (skill != null && !this.isDead) {
            long now = System.currentTimeMillis();
            if (skill.template.type == 2 && !skill.isCooldown() && this.mp >= skill.manaUse) {
                SkillOption[] options = skill.options;
                skill.lastTimeUseThisSkill = now;
                addMp(-skill.manaUse);
                getService().updateMp();
                if (skill.template.id == 67 || skill.template.id == 68 || skill.template.id == 69
                        || skill.template.id == 70 || skill.template.id == 71 || skill.template.id == 72) {

                    if (this.zone.tilemap.isFujukaSanctuary()) {
                        serverMessage("Không thể triệu hồi phân thân trong khu vực này");
                        return;
                    }

                    if (timeCountDown == 0 && !(isBot())) {
                        int index = getIndexItemByIdInBag(545);
                        if (index == -1) {
                            serverMessage("Không đủ " + ItemManager.getInstance().getItemName(545));
                            return;
                        } else {
                            this.timeCountDown = options[0].param * 60;
                            removeItem(index, 1, true);
                        }
                    }
                    if (clone == null) {
                        this.clone = new CloneChar(this, options[1].param);
                        clone.load();
                        clone.isDead = true;
                    }
                    if (clone.isDead) {
                        clone.create();
                        Arena arena = (Arena) findWorld(World.ARENA);
                        if (arena != null) {
                            if (arena.isOpened) {
                                clone.setTypePk(this.typePk);
                            }
                            arena.join(4, clone);
                        } else {
                            zone.join(clone);//// phân thân/
                        }
                        if (clone.equipment[ItemTemplate.TYPE_THUNUOI] != null) {
                            clone.getEm().setEffectPet();
                        }
                    }
                } else if (skill.template.id == SkillName.CHIEU_RAIKOU) {
                    Effect eff = new Effect(15, options[0].param * 1000L, 0);
                    em.setEffect(eff);
                } else if (skill.template.id == SkillName.CHIEU_HOSHITAMA) {
                    Effect eff = new Effect(16, 5000, options[0].param);
                    em.setEffect(eff);
                } else if (skill.template.id == SkillName.CHIEU_TAIYOUTAMA) {
                    Effect eff = new Effect(9, 30000, (int)this.damage * options[0].param / 100);
                    em.setEffect(eff);
                } else if (skill.template.id == SkillName.CHIEU_AOIAME) {
                    short x = (short) (this.x + (20 * dir));
                    short y = this.y;
                    if (!this.zone.tilemap.tileTypeAt(x, y, TileMap.T_TOP)) {
                        serverMessage("Cần phải có khoảng trống phía trước mới có thể thi triển chiêu này");
                        return;
                    }
                    Figurehead buNhin = new Figurehead(this.name, x, y, options[0].param);
                    zone.add(buNhin);
                    zone.getService().createBuNhin(buNhin);
                } else if (skill.template.id == SkillName.CHIEU_SOGEKIHEI) {
                    Effect eff = new Effect(10, 90000, options[0].param);
                    em.setEffect(eff);
                } else if (skill.template.id == SkillName.CHIEU_JOUTENHITOMI) {
                    Effect eff = new Effect(17, 5000, options[0].param);
                    em.setEffect(eff);
                } else if (skill.template.id == SkillName.CHIEU_TOTAAIGO) {
                    Effect eff = new Effect(11, options[0].param, 15000);
                    em.setEffect(eff);
                } else if (skill.template.id == 47) {
                    revival(skill);
                } else if (skill.template.id == 51) {
                    buffIminity(skill);
                } else if (skill.template.id == 52) {
                    effectResistance(skill);
                }
            }
        }
    }

    public void revival(Skill skill) {
        SkillOption[] options = skill.options;
        int param = options[0].param;
        int param2 = options[1].param;
        param += param * this.optionsSupportSkill[66] / 100;
        param2 += param2 * this.optionsSupportSkill[66] / 100;
        Effect eff = new Effect(8, 5000, param);
        em.setEffect(eff);
        if (this.group != null) {
            List<Char> list = this.group.getCharsInZone(mapId, zone.id);
            for (Char _char : list) {
                if (_char != this) {
                    int distance = NinjaUtils.getDistance(this.x, this.y, _char.x, _char.y);
                    int d = NinjaUtils.getDistance(skill.dx, skill.dy, 0, 0);
                    if (distance <= d) {
                        Effect eff2 = new Effect(8, 5000, param);
                        _char.em.setEffect(eff2);
                    }
                }
            }
        }
    }

    public void buffIminity(Skill skill) {
        SkillOption[] options = skill.options;
        int param = options[0].param;
        int param2 = options[1].param;
        param += param * this.optionsSupportSkill[66] / 100;
        param2 += param2 * this.optionsSupportSkill[66] / 100;
        Effect eff = new Effect(19, 90000, param);
        eff.param2 = (short) param2;
        em.setEffect(eff);
        setAbility();
        if (this.group != null) {
            List<Char> list = this.group.getCharsInZone(mapId, zone.id);
            for (Char _char : list) {
                if (_char != this) {
                    if (_char != null) {
                        int distance = NinjaUtils.getDistance(this.x, this.y, _char.x, _char.y);
                        int d = NinjaUtils.getDistance(skill.dx, skill.dy, 0, 0);
                        if (distance <= d) {
                            Effect eff2 = new Effect(19, 90000, param);
                            eff2.param2 = param2;
                            _char.em.setEffect(eff2);
                            _char.setAbility();
                        }
                    }
                }
            }
        }
    }

    public void effectResistance(Skill skill) {
        SkillOption[] options = skill.options;
        int param = options[0].param;
        param += param * this.optionsSupportSkill[66] / 100;
        Effect eff = new Effect(20, param * 1000, 3);
        eff.param2 = 2;
        em.setEffect(eff);
        if (this.group != null) {
            List<Char> list = this.group.getCharsInZone(mapId, zone.id);
            for (Char _char : list) {
                if (_char != this) {
                    int distance = NinjaUtils.getDistance(this.x, this.y, _char.x, _char.y);
                    int d = NinjaUtils.getDistance(skill.dx, skill.dy, 0, 0);
                    if (distance <= d) {
                        Effect eff2 = new Effect(20, param * 1000, 2);
                        eff2.param2 = 1;
                        _char.em.setEffect(eff2);
                    }
                }
            }
        }
    }

    public void useSkillBuff(Message ms) {
        try {
            byte dir = ms.reader().readByte();
            useSkillBuff(dir, selectedSkill);
        } catch (IOException ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void hoiSinh(Message ms) {
        try {
            if (zone.tilemap.isNotRivival()) {
                serverMessage("Không thể sử dụng thuật này tại đây.");
                return;
            }
            if (this.classId == 6 && !this.isDead) {
                int charId = ms.reader().readInt();
                Char _char = zone.findCharById(charId);
                if (_char != null && _char.isDead) {
                    for (Skill my : vSkill) {
                        if (my.template.id == 49) {
                            if (!my.isCooldown()) {
                                my.lastTimeUseThisSkill = System.currentTimeMillis();
                                ArrayList<Char> chars = new ArrayList<>();
                                chars.add(_char);
                                addMp(-my.manaUse);
                                getService().updateMp();
                                zone.getService().setSkillPaint_2(chars, this, (byte) selectedSkill.template.id);
                                int d = NinjaUtils.getDistance(my.dx, my.dy, 0, 0);
                                int distance = NinjaUtils.getDistance(this.x, this.y, _char.x, _char.y);
                                if (distance > d) {
                                    serverMessage("Khoảng cách quá xa");
                                    return;
                                }

                                _char.isDead = false;
                                _char.hp = _char.maxHP;
                                _char.mp = _char.maxMP;
                                _char.getService().meLive();
                                zone.getService().returnPointMap(_char);
                                for (SkillOption option : my.options) {
                                    if (option.optionTemplate.id == 28) {
                                        Effect effect = new Effect(11, 5000, option.param);
                                        _char.em.setEffect(effect);
                                        _char.setAbility();
                                    }
                                }

                            }
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.error("err: " + e.getMessage(), e);
        }
    }

    public void learnSkill(Item item) {
        if (item.id == 547 && !isHuman) {
            serverDialog("Thứ thân không thể chiêu này.");
            return;
        }
        if ((item.id >= 40 && item.id <= 93) || (item.id >= 311 && item.id <= 316) || (item.id >= 375 && item.id <= 380)
                || item.id == 547 || (item.id >= 552 && item.id <= 563) || (item.id >= 839 && item.id <= 844)) {
            short templateId = (short) (item.id - 39);
            if (item.id == 311) {
                templateId = 55;
            } else if (item.id == 312) {
                templateId = 56;
            } else if (item.id == 313) {
                templateId = 57;
            } else if (item.id == 314) {
                templateId = 58;
            } else if (item.id == 315) {
                templateId = 59;
            } else if (item.id == 316) {
                templateId = 60;
            } else if (item.id == 375) {
                templateId = 61;
            } else if (item.id == 376) {
                templateId = 62;
            } else if (item.id == 377) {
                templateId = 63;
            } else if (item.id == 378) {
                templateId = 64;
            } else if (item.id == 379) {
                templateId = 65;
            } else if (item.id == 380) {
                templateId = 66;
            } else if (item.id == 547) {
                switch (this.classId) {
                    case 1:
                        templateId = 67;
                        break;

                    case 2:
                        templateId = 68;
                        break;

                    case 3:
                        templateId = 69;
                        break;

                    case 4:
                        templateId = 70;
                        break;

                    case 5:
                        templateId = 71;
                        break;

                    case 6:
                        templateId = 72;
                        break;
                }
            } else if (item.id == 552) {
                templateId = 73;
            } else if (item.id == 553) {
                templateId = 78;
            } else if (item.id == 554) {
                templateId = 75;
            } else if (item.id == 555) {
                templateId = 76;
            } else if (item.id == 556) {
                templateId = 74;
            } else if (item.id == 557) {
                templateId = 77;
            } else if (item.id == 558) {
                templateId = 79;
            } else if (item.id == 559) {
                templateId = 83;
            } else if (item.id == 560) {
                templateId = 81;
            } else if (item.id == 561) {
                templateId = 82;
            } else if (item.id == 562) {
                templateId = 80;
            } else if (item.id == 563) {
                templateId = 84;
            } else if (item.id == 844) {
                templateId = 85;
            } else if (item.id == 841) {
                templateId = 86;
            } else if (item.id == 842) {
                templateId = 87;
            } else if (item.id == 839) {
                templateId = 88;
            } else if (item.id == 843) {
                templateId = 89;
            } else if (item.id == 840) {
                templateId = 90;
            }
            for (Skill my : vSkill) {
                if (my.template.id == templateId) {
                    serverDialog("Chiêu này đã học!");
                    return;
                }
            }
            Skill skill = GameData.getInstance().getSkill(this.classId, templateId, 1);
            if (skill != null) {
                vSkill.add(skill);
                if (skill.template.type == Skill.SKILL_AUTO_USE) {
                    vSupportSkill.add(skill);
                    setAbility();
                } else if ((skill.template.type == Skill.SKILL_CLICK_USE_ATTACK
                        || skill.template.type == Skill.SKILL_CLICK_LIVE
                        || skill.template.type == Skill.SKILL_CLICK_USE_BUFF
                        || skill.template.type == Skill.SKILL_CLICK_NPC)
                        && (skill.template.maxPoint == 0 || (skill.template.maxPoint > 0 && skill.point > 0))) {

                    vSkillFight.add(skill);
                }
                getService().useBookSkill((byte) item.index, (short) skill.id);
                getService().loadSkill();
                if (selectedSkill == null && skill.template.type == 1) {
                    selectSkill(templateId);
                }
                if (taskId == TaskName.NV_GIA_TANG_SUC_MANH && taskMain != null && taskMain.index == 0) {
                    taskNext();
                    taskNext();
                }
                removeItem(item.index, item.getQuantity(), false);
            } else {
                serverDialog("Sách nay không phù hợp!");
            }
        }
    }

    private void attackMonster(ArrayList<Mob> mobs) {
        if (this.isDead) {
            return;
        }
        try {
            selectedSkill.lastTimeUseThisSkill = System.currentTimeMillis();
            int skillTemplateId = selectedSkill.template.id;
            if (skillTemplateId == 4) {
                Mob mob = mobs.get(0);
                if (mob != null && !mob.isBoss && mob.levelBoss == 0 && mob.template.id != MobName.BACH_LONG_TRU
                        && mob.template.id != MobName.HAC_LONG_TRU) {
                    if (zone.tilemap.isDungeo()) {
                        Dungeon dun = (Dungeon) findWorld(World.DUNGEON);
                        if (dun != null) {
                            if (mob.levelBoss == 2) {
                                dun.addPointPB((int)NinjaUtils.nextInt(5, 20));
                            } else if (mob.levelBoss == 1) {
                                dun.addPointPB(6);
                            } else {
                                dun.addPointPB(1);
                            }
                        }
                    }
                    mob.die();
                    zone.addMobForWatingListRespawn(mob);
                    mob.dropItem(this, Mob.SUSHI);
                    zone.getService().npcChange(mob);
                } else {
                    serverMessage("Đối phương quá mạnh bạn không thể thi triển thuật này!");
                }
                return;
            } else if (skillTemplateId == 25 || skillTemplateId == 34) {
                SkillOption[] option = selectedSkill.options;
                int rand = (int)NinjaUtils.nextInt(100);
                if (rand <= option[0].param) {
                    Effect eff = new Effect(6, 3000, 0);
                    Mob mob = mobs.get(0);
                    mob.effects.put(eff.template.type, eff);
                    zone.setIce(mob, true);
                }
            } else if (skillTemplateId == 24) {
                SkillOption[] option = selectedSkill.options;
                Effect eff = new Effect(18, option[0].param * 1000, 0);
                Mob mob = mobs.get(0);
                mob.effects.put(eff.template.type, eff);
                zone.setMove(mob, true);
                return;
            } else if (skillTemplateId == 40) {
                SkillOption[] option = selectedSkill.options;
                Effect eff = new Effect(0, option[0].param * 1000, 0);
                Mob mob = mobs.get(0);
                mob.effects.put(eff.template.type, eff);
                zone.setDisable(mob, true);
                return;
            } else if (skillTemplateId == 43) {
                SkillOption[] option = selectedSkill.options;
                int rand = (int)NinjaUtils.nextInt(100);
                if (rand <= option[0].param) {
                    Effect eff = new Effect(7, 2000, 0);
                    Mob mob = mobs.get(0);
                    mob.effects.put(eff.template.type, eff);
                    zone.setWind(mob, true);
                }
            } else if (skillTemplateId == 7 || skillTemplateId == 16) {
                SkillOption[] option = selectedSkill.options;
                int rand = (int)NinjaUtils.nextInt(100);
                if (rand <= option[0].param) {
                    Effect eff = new Effect(5, 4000, 0);
                    Mob mob = mobs.get(0);
                    mob.effects.put(eff.template.type, eff);
                    zone.setFire(mob, true);
                }
            } else if (skillTemplateId == 9 || skillTemplateId == 61 || skillTemplateId == 73 || skillTemplateId == 79
                    || skillTemplateId == 18 || skillTemplateId == 62 || skillTemplateId == 78
                    || skillTemplateId == 83 || skillTemplateId == 85 || skillTemplateId == 86) {
                SkillOption[] option = selectedSkill.options;
                for (Mob mob : mobs) {
                    int rand = (int)NinjaUtils.nextInt(100);
                    if (rand <= option[0].param) {
                        Effect eff = new Effect(5, 2000, 0);
                        byte type = eff.template.type;
                        Effect eff2 = mob.effects.get(type);
                        if (eff2 != null) {
                            if (eff2.getTimeRemaining() > eff.getTimeLength()) {
                                continue;
                            }
                        }
                        mob.effects.put(type, eff);
                        zone.setFire(mob, true);
                    }
                }
            } else if (skillTemplateId == 27 || skillTemplateId == 63 || skillTemplateId == 75 || skillTemplateId == 81
                    || skillTemplateId == 36 || skillTemplateId == 64 || skillTemplateId == 76
                    || skillTemplateId == 82 || skillTemplateId == 87 || skillTemplateId == 88) {
                SkillOption[] option = selectedSkill.options;
                for (Mob mob : mobs) {
                    int rand = (int)NinjaUtils.nextInt(100);
                    if (rand <= option[0].param) {
                        Effect eff = new Effect(6, 2000, 0);
                        byte type = eff.template.type;
                        Effect eff2 = mob.effects.get(type);
                        if (eff2 != null) {
                            if (eff2.getTimeRemaining() > eff.getTimeLength()) {
                                continue;
                            }
                        }
                        mob.effects.put(type, eff);
                        zone.setIce(mob, true);
                    }
                }
            } else if (skillTemplateId == 45 || skillTemplateId == 65 || skillTemplateId == 74 || skillTemplateId == 80
                    || skillTemplateId == 54 || skillTemplateId == 66 || skillTemplateId == 77
                    || skillTemplateId == 84 || skillTemplateId == 89 || skillTemplateId == 90) {
                SkillOption[] option = selectedSkill.options;
                for (Mob mob : mobs) {
                    int rand = (int)NinjaUtils.nextInt(100);
                    if (rand <= option[0].param) {
                        Effect eff = new Effect(7, 1000, 0);
                        byte type = eff.template.type;
                        Effect eff2 = mob.effects.get(type);
                        if (eff2 != null) {
                            if (eff2.getTimeRemaining() > eff.getTimeLength()) {
                                continue;
                            }
                        }
                        mob.effects.put(type, eff);
                        zone.setWind(mob, true);
                    }
                }
            } else if (optionsSupportSkill[69] > 0) {
                for (Mob mob : mobs) {
                    int rand = (int)NinjaUtils.nextInt(100);
                    if (rand <= optionsSupportSkill[69]) {
                        Effect eff = new Effect(6, 2000, 0);
                        byte type = eff.template.type;
                        Effect eff2 = mob.effects.get(type);
                        if (eff2 != null) {
                            if (eff2.getTimeRemaining() > eff.getTimeLength()) {
                                continue;
                            }
                        }
                        mob.effects.put(type, eff);
                        zone.setIce(mob, true);
                    }
                }
            }

            boolean isFatal = this.fatal > (int)NinjaUtils.nextInt(1000);
            int add = 0;
            if (isInvisible) {
                Effect eff = em.findByType((byte) 11);
                if (eff != null) {
                    isFatal = true;
                    add = optionsSupportSkill[61];
                    getService().removeEffect(eff);
                    zone.getService().playerRemoveEffect(this, eff);
                    em.removeEffect(eff);
                }
            }
            // zone.lockChar.lock();
            // try {
            zone.getService().setSkillPaint_1(mobs, this, (byte) selectedSkill.template.id);
            // getService().updateMp();
            for (Mob mob : mobs) {
                mob.lock.lock();
                try {
                    if (!mob.isDead) {
                        if (mob.template.id == MobName.BOSS_TUAN_LOC || mob.template.id == MobName.QUAI_VAT) {
                            if (mob.chars.get(0) != this.id) {
                                continue;
                            }
                        }
                        if ((getEventPoint().getPoint(LunarNewYear.MYSTERY_BOX_LEFT) <= 0
                                && mob.template.id == MobName.HOP_BI_AN && this.mob == null)) {
                            continue;
                        }
                        if (clan == null && mob.template.id == MobName.NGUOI_TUYET) {
                            continue;
                        }

                        if (mob.template.id == MobName.BAO_QUAN && em.findByID((byte) 23) == null) {
                            continue;
                        }

                        if (mob.template.id == MobName.KORO_KING
                                && getIndexItemByIdInBag(ItemName.VIEN_THUOC_THAN_KY) == -1) {
                            continue;
                        }

                        if (mob.template.id == MobName.CHUOT_CANH_TY) {
                            if (fashion[2] == null
                                    || (fashion[2] != null && fashion[2].template.id != ItemName.AO_NGU_THAN
                                    && fashion[2].template.id != ItemName.AO_TAN_THOI)) {
                                continue;
                            }
                        }

                        int miss = (int)NinjaUtils.nextInt(mob.level + 10);
                        int exactly = NinjaUtils.nextInt((int)this.exactly + 100);
                        boolean isMiss = miss > exactly;
                        // boolean isMiss = NinjaUtil.nextInt(this.exactly + 10) < 4;
                        if (skillTemplateId == 7 || skillTemplateId == 25 || skillTemplateId == 34
                                || skillTemplateId == 43) {
                            isMiss = false;
                        }
                        if (isMiss) {
                            zone.getService().attackMonster(-1, false, mob);
                        } else {
//                          mob bi bong
                            if (isEffBong) {
                                Effect eff = em.findByType((byte) 5);
                                if (eff != null) {
                                    Effect eff2 = new Effect(9, eff.getTimeRemaining(), eff.param);
                                    eff2.param2 = this.id;
                                    mob.effects.put(eff2.template.type, eff2);
                                }
                            }
                            int damageBasic = (int)NinjaUtils.nextInt(this.damage2, this.damage);

                            if (zone.tilemap.isDungeoClan()) {// test
                                damageBasic = (int) ((long) mob.maxHP * 1 / 100);
                                if (mob.template.id == MobName.BAO_QUAN || mob.template.id == MobName.TU_HA_MA_THAN) {
                                    damageBasic = (int) ((long) mob.maxHP * 1 / 10000);
                                }
                            }
                            int dameHit = damageBasic;
                            if (isFatal) {
                                dameHit += damageBasic;
                                dameHit += damageBasic * (this.percentFatalDame + optionsSupportSkill[65] + add) / 100;
                                dameHit += this.fatalDame;
                            }
                            dameHit += options[102];
                            if (mob.effects != null) {
                                if (mob.effects.get((byte) 1) != null) {
                                    dameHit += damageBasic;
                                }
                            }
                            dameHit += options[113];
                            if (isInfected()) {
                                dameHit -= dameHit * 80 / 100;
                            }
                            if (isCool()) {
                                dameHit -= dameHit * 50 / 100;
                            }
                            if (mob.template.id == MobName.NGUOI_TUYET) {
                                dameHit = 1;
                            }
                            if (dameHit > 0) {
                                double preHP = mob.hp;
                                if (this.isModeRemove) {
                                    mob.hp = 0;
                                } else {
                                    if (mob.template.id == MobName.BU_NHIN) {
                                        mob.addHp(-(mob.maxHP / 5));
                                    } else {
                                        mob.addHp(-dameHit);
                                    }
                                }
                                if (mob.hp < 0) {
                                    mob.hp = 0;
                                }
                                double nextHP = mob.hp;
                                double hp = Math.abs(nextHP - preHP);
                                int dLevel = Math.abs(mob.level - this.level);
                                if (mob.template.id != MobName.BOSS_TUAN_LOC && mob.template.id != MobName.QUAI_VAT) {
                                    addExp(mob, (int)hp);
                                }
                                if (mob.hp <= 0) {
                                    mob.die();
                                }
                                if (mob.isDead) {
                                    Char killer = getOriginChar();
                                    mob.dead(killer);
                                } else {
                                    if (mob.id != 0 && !isNhanBan) {
                                        mob.addCharId(this.id);
                                    }
                                }

                                if (mob.template.id == MobName.HOP_BI_AN && mob.isDead) {
                                    zone.getService().addEffectAuto((byte) 8, (short) mob.x, mob.y, (byte) 0,
                                            (short) 1);
                                }

                                zone.getService().attackMonster(dameHit, isFatal, mob);

                                if (zone.tilemap.isDungeoClan()) {
                                    Territory.checkEveryAttack(this);
                                }
                            }
                            
                        }
                    }
                } finally {
                    mob.lock.unlock();
                }
            }
        } catch (Exception e) {
            Log.error("err: " + e.getMessage(), e);
        }
        // } finally {
        // zone.lockChar.unlock();
        // }
    }

    public void addExp(Mob mob, int dame) {
        int dLevel = Math.abs(mob.level - this.level);
        if (mob.template.id != MobName.BU_NHIN && (dLevel <= 10)) {
            long exp = 1;
            int a = this.level / 20;
            a = a == 0 ? 1 : a;
            int b = dame * a;
            int c = (mob.level - this.level) * a;
            exp = (b / 2) + (b * c / 100);
            if (mob.isBoss) {
                exp *= 6;
            } else if (mob.levelBoss == 1) {
                exp *= 2;
            } else if (mob.levelBoss == 2) {
                exp *= 5;
            }
            int add = 0;
            if (isEffExp) {
                Effect effAddExp = em.findByID((byte) 38);
                if (effAddExp != null) {
                    add = effAddExp.param;
                }
            }
            exp += exp * (options[100] + options[169] + optionsSupportSkill[29] + add) / 100;
            if (incrExp > 0) {
                exp *= incrExp;
            }
            if (zone.tilemap.isDungeo()) {
                exp *= 2;
            }
            if (exp > 0) {
                addExp(exp);
                if (zone.tilemap.isDungeo()) {
                    long expGroup = exp / 10;
                    Dungeon dun = (Dungeon) findWorld(World.DUNGEON);
                    if (dun != null) {
                        dun.addExp(this, expGroup);
                    }

                } else {
                    if (this.group != null) {
                        long expGroup = exp / 10;
                        List<Char> chars = this.group.getCharsInZone(this.mapId, zone.id);
                        for (Char _char : chars) {
                            if (_char != null && _char != this && !_char.isDead) {
                                int dLevel2 = Math.abs(this.level - _char.level);
                                if (dLevel2 <= 10) {
                                    _char.addExp(expGroup);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void attackCharacter(ArrayList<Char> chars) {
        if (this.isDead) {
            return;
        }
        Char originChar = getOriginChar();
        if (originChar.mapId != 138) {
            if (!originChar.isTest && (isVillage() || isSchool())) {
                return;
            }
        }

        if (isNonCombatState()) {
            return;
        }

        if (originChar.hieuChien >= 15) {
            serverDialog("Điểm hiếu chiến quá cao, không thể tiếp tục thi triển thuật này lên người.");
            return;
        }
        selectedSkill.lastTimeUseThisSkill = System.currentTimeMillis();
        int skillTemplateId = selectedSkill.template.id;
        if (skillTemplateId == 4) {
            serverDialog("Không thể thi triển thuật này lên người");
            return;
        } else if (skillTemplateId == 25 || skillTemplateId == 34) {
            SkillOption[] option = selectedSkill.options;
            int rand = (int)NinjaUtils.nextInt(100);
            if (rand <= option[0].param) {
                Effect eff = new Effect(6, 3000, 0);
                Char _char = chars.get(0);
                eff.addTime(-_char.options[41] * 1000);
                eff.addTime(options[44]);
                Effect eff2 = _char.em.findByType((byte) 16);
                if (eff2 != null) {
                    eff.addTime(-eff2.param2 * 1000);
                }
                _char.em.setEffect(eff);
            }
        } else if (skillTemplateId == 24) {
            SkillOption[] option = selectedSkill.options;
            Effect eff = new Effect(18, option[0].param * 1000, 0);
            Char _char = chars.get(0);
            _char.em.setEffect(eff);
            return;
        } else if (skillTemplateId == 43) {
            SkillOption[] option = selectedSkill.options;
            int rand = (int)NinjaUtils.nextInt(100);
            if (rand <= option[0].param) {
                Effect eff = new Effect(7, 3000, 0);
                Char _char = chars.get(0);
                eff.addTime(-_char.options[42] * 1000);
                eff.addTime(options[45] * 1000);
                _char.em.setEffect(eff);
            }
        } else if (skillTemplateId == 7) {
            SkillOption[] option = selectedSkill.options;
            int rand = (int)NinjaUtils.nextInt(100);
            if (rand <= option[0].param) {
                Effect eff = new Effect(5, 3000, 0);
                Char _char = chars.get(0);
                eff.addTime(-_char.options[40] * 1000);
                eff.addTime(options[43] * 1000);
                Effect eff2 = _char.em.findByType((byte) 16);
                if (eff2 != null) {
                    eff.addTime(-eff2.param * 1000);
                }
                _char.em.setEffect(eff);
            }
        } else if (skillTemplateId == 9 || skillTemplateId == 61 || skillTemplateId == 73 || skillTemplateId == 79
                || skillTemplateId == 18 || skillTemplateId == 62 || skillTemplateId == 78 || skillTemplateId == 83 || skillTemplateId == 85 || skillTemplateId == 86) {
            SkillOption[] option = selectedSkill.options;
            for (Char _char : chars) {
                int rand = (int)NinjaUtils.nextInt(100);
                if (rand <= option[0].param) {
                    Effect eff = new Effect(5, 2000, 0);
                    eff.addTime(-_char.options[40] * 1000);
                    eff.addTime(options[43] * 1000);
                    Effect eff2 = _char.em.findByType((byte) 16);
                    if (eff2 != null) {
                        eff.addTime(-eff2.param * 1000);
                    }
                    byte type = eff.template.type;
                    Effect eff3 = _char.em.findByType(type);
                    if (eff3 != null) {
                        if (eff3.getTimeRemaining() > eff.getTimeLength()) {
                            continue;
                        }
                    }
                    _char.em.setEffect(eff);
                }

            }

        } else if (skillTemplateId == 27 || skillTemplateId == 63 || skillTemplateId == 75 || skillTemplateId == 81
                || skillTemplateId == 36 || skillTemplateId == 64 || skillTemplateId == 76 || skillTemplateId == 82 || skillTemplateId == 87 || skillTemplateId == 88) {
            SkillOption[] option = selectedSkill.options;
            for (Char _char : chars) {
                int rand = (int)NinjaUtils.nextInt(100);
                if (rand <= option[0].param) {
                    Effect eff = new Effect(6, 2000, 0);
                    eff.addTime(-_char.options[41] * 1000);
                    eff.addTime(options[44] * 1000);
                    Effect eff2 = _char.em.findByType((byte) 16);
                    if (eff2 != null) {
                        eff.addTime(-eff2.param2 * 1000);
                    }
                    byte type = eff.template.type;
                    Effect eff3 = _char.em.findByType(type);
                    if (eff3 != null) {
                        if (eff3.getTimeRemaining() > eff.getTimeLength()) {
                            continue;
                        }
                    }
                    _char.em.setEffect(eff);
                }

            }
        } else if (skillTemplateId == 45 || skillTemplateId == 65 || skillTemplateId == 74 || skillTemplateId == 80
                || skillTemplateId == 54 || skillTemplateId == 66 || skillTemplateId == 77 || skillTemplateId == 84 || skillTemplateId == 89 || skillTemplateId == 90) {
            SkillOption[] option = selectedSkill.options;
            for (Char _char : chars) {
                int rand = (int)NinjaUtils.nextInt(100);
                if (rand <= option[0].param) {
                    Effect eff = new Effect(7, 1000, 0);
                    eff.addTime(-_char.options[42] * 1000);
                    eff.addTime(options[45] * 1000);
                    byte type = eff.template.type;
                    Effect eff3 = _char.em.findByType(type);
                    if (eff3 != null) {
                        if (eff3.getTimeRemaining() > eff.getTimeLength()) {
                            continue;
                        }
                    }
                    _char.em.setEffect(eff);
                }
            }
        }
        boolean isFatal = this.fatal > (int)NinjaUtils.nextInt(1000);
        if (isInvisible) {
            Effect eff = em.findByType((byte) 11);
            if (eff != null) {
                isFatal = true;
                getService().removeEffect(eff);
                zone.getService().playerRemoveEffect(this, eff);
                em.removeEffect(eff);
            }
        }
        zone.getService().setSkillPaint_2(chars, this, (byte) selectedSkill.template.id);
        for (Char pl : chars) {
            if (mapId != 138 && !pl.isTest && (isVillage() || isSchool())) {
                continue;
            }
            if (selectedSkill.template.type == 1) {
                if (mobMe != null && mobMe.damageOnPlayer > 0) {
                    mobMe.attack(pl, this);
                }
            }
            pl.lock.lock();
            try {
                if (!pl.isDead && !pl.isCleaned) {
                    int dameHit = (int)NinjaUtils.nextInt(this.damage2, this.damage);
                    int dameBasic = dameHit;
                    int dameMp = 0;
                    dameHit += options[103];
                    if (isEffDameDown) {
                        Effect eff2 = pl.em.findByID((byte) 37);
                        if (eff2 != null) {
                            dameHit -= dameHit * eff2.param / 100;
                        }
                    }
                    int dameFatal = 0;
                    if (isFatal) {
                        dameFatal += dameBasic;
                        dameFatal += (dameBasic * (this.percentFatalDame + optionsSupportSkill[65])) / 100;
                        dameFatal += this.fatalDame;
                        dameFatal -= dameHit * (pl.options[46] + pl.options[79]) / 100;
                    }
                    if (pl.isFire) {
                        dameHit += dameBasic;
                    }
                    boolean isSkipResistance = false;
                    if (isEffSkipResistance) {
                        Effect eff3 = pl.em.findByID((byte) 39);
                        if (eff3 != null) {
                            isSkipResistance = (int)NinjaUtils.nextInt(100) < eff3.param;
                        }
                    }

                    if (pl.optionsSupportSkill[69] > 0) { // dong bang skill kunai 6x
                        if (!this.isNhanBan) {
                            int rand = (int)NinjaUtils.nextInt(100);
                            if (rand <= pl.optionsSupportSkill[69]) {
                                Effect eff = new Effect(6, 1000, 0);
                                eff.addTime(-options[41] * 1000);
                                eff.addTime(pl.options[44] * 1000);
                                Effect eff2 = this.em.findByType((byte) 16);
                                if (eff2 != null) {
                                    eff.addTime(-eff2.param2 * 1000);
                                }
                                byte type = eff.template.type;
                                Effect eff3 = pl.em.findByType(type);
                                if (eff3 != null) {
                                    if (eff3.getTimeRemaining() > eff.getTimeLength()) {
                                        continue;
                                    }
                                }
                                em.setEffect(eff);
                            }
                        }
                    }

                    switch (getSys()) {
                        case 1:
                            if (!isSkipResistance) {
                                dameHit -= pl.resFire;
                            }
                            dameHit -= dameHit * pl.options[127] / 100;
                            dameHit -= pl.options[48];
                            dameHit += options[51];
                            dameHit += dameBasic * options[54] / 100;
                            break;

                        case 2:
                            if (!isSkipResistance) {
                                dameHit -= pl.resIce;
                            }

                            dameHit -= dameHit * pl.options[130] / 100;
                            dameHit -= pl.options[49];
                            dameHit += options[52];
                            dameHit += dameBasic * options[54] / 100;
                            if (isFatal) {
                                dameFatal += dameHit * optionsSupportSkill[65] / 100;
                            }
                            break;

                        case 3:
                            if (!isSkipResistance) {
                                dameHit -= pl.resWind;
                            }
                            dameHit -= dameHit * pl.options[131] / 100;
                            dameHit -= pl.options[50];
                            dameHit += options[53];
                            dameHit += dameBasic * options[54] / 100;
                            break;
                    }
                    int kstcm = pl.options[121] > 100 ? 100 : pl.options[121];
                    dameFatal -= dameFatal * kstcm / 100;
                    dameHit -= dameHit * (pl.options[63] + pl.options[98]) / 100;
                    if (pl.isReductionDame) {
                        dameHit -= dameHit * pl.options[136] / 100;
                    }
                    dameHit = (dameHit + (dameFatal / 3)) * (6 - ((pl.level - this.level) / 40)) / 100;
                    if (!isNhanBan) {
                        double reactDame = pl.reactDame;

                        if (pl.options[135] > 0) {
                            if ((int)NinjaUtils.nextInt(100) < pl.options[135]) {
                                reactDame = hp * 20 / 100;
                                zone.getService().addEffect(mob, 64, 5, 5, 0);
                            }
                        }

                        if (reactDame > 0) {
                            if (this.hp - reactDame > 1) {
                                addHp(-reactDame);
                            } else {
                                this.hp = 1;
                                reactDame = -1;
                            }
                            zone.getService().attackCharacter(reactDame, 0, this);
                        }
                    }
                    if (dameHit <= 0) {
                        dameHit = 1;
                    }
                    double randMiss = NinjaUtils.nextInt((int)pl.miss + 100);
                    double randExactly = NinjaUtils.nextInt((int)this.exactly + 100);
                    boolean isMiss = randMiss > randExactly;
                    if (pl.isMiss) {
                        isMiss = true;
                    }

                    // }
                    dameHit += options[113];
                    if (isInfected()) {
                        dameHit -= dameHit * 80 / 100;
                    }
                    if (isCool()) {
                        dameHit -= dameHit * 50 / 100;
                    }
                    if (isMiss) {
                        dameHit = 0;
                        dameMp = 0;
                    }
                    int num = dameHit + dameMp;
                    // num -= pl.dameDown;
                    if (num > 0) {
                        if (isFatal) {
                            dameHit *= -1;
                            dameMp *= -1;
                        }
                        zone.getService().attackCharacter(dameHit, dameMp, pl);
                        if (pl.isTest && pl.hp - num <= 0) {
                            Char pl2 = zone.findCharById(pl.testCharId);
                            int num2 = pl.hp;
                            pl.testEnd(num2, pl2);
                        } else {
                            pl.addHp(-num);
                        }
                        if (pl.hp <= 0) {
                            if (zone.tilemap.isChienTruong()) {
                                short pointAdd = 5;
                                int range = pl.nDead - pl.nKill;
                                if (range > 50) {
                                    pointAdd -= 1;
                                }
                                if (range > 100) {
                                    pointAdd -= 1;
                                }
                                if (range > 150) {
                                    pointAdd -= 1;
                                }
                                if (range > 200) {
                                    pointAdd -= 1;
                                }
                                if (range > 250) {
                                    pointAdd -= 1;
                                }
                                nKill += 1;
                                pl.nDead += 1;
                                addWarPoint(pointAdd);
                                getService().serverMessage("Bạn vừa đánh trọng thương " + pl.name);
                                pl.getService().serverMessage("Bạn bị " + this.name + " đánh trọng thương");
                            }
                            if ((originChar.typePk == 2 || originChar.typePk == 3) && originChar.gloryTask != null) {
                                if (originChar.gloryTask.type == GloryTask.CUU_SAT_NGUOI_KHAC
                                        && Math.abs(this.level - pl.level) <= 10
                                        && !originChar.gloryTask.isExistCharacterId(pl.id)) {
                                    originChar.gloryTask.updateProgress(1);
                                    originChar.gloryTask.addCharacterId(pl.id);
                                }
                            }
                            if (pl.enemies != null) {
                                if (originChar.typePk == 3) {
                                    originChar.addPointPk(1);
                                } else if (originChar.typePk == 2 || originChar.killCharId == pl.id) {
                                    originChar.addPointPk(2);
                                }
                                if (!(isBot())) {
                                    pl.enemies.put(this.name, new Friend(this.name, (byte) 0));
                                }
                            }
                            if (pl.hieuChien > 0) {
                                long expLevel = NinjaUtils.getExp(pl.level);
                                long expNext = Server.exps[pl.level];
                                long expD = (expNext * ((pl.hieuChien > 5) ? 5 : pl.hieuChien) / 50);
                                if (pl.exp - expD >= expLevel) {
                                    pl.exp -= expD;
                                    pl.expDown = 0;
                                } else {
                                    expD -= (pl.exp - expLevel);
                                    pl.exp = expLevel;
                                    pl.expDown += expD;
                                    long fiftyPercent = (expNext / 2);
                                    if (pl.expDown > fiftyPercent) {
                                        pl.expDown = fiftyPercent;
                                    }
                                }
                                pl.addPointPk(-1);
                            }
                            pl.startDie();
                        }
                    }
                }
            } finally {
                pl.lock.unlock();
            }
        }
    }

    public void attackAllType(Message ms, int type) {
        try {
            // Char originChar = this.getOriginChar();
            if (this.isDead) {
                return;
            }
            long currentTimeMillis = System.currentTimeMillis();
            if (!selectedSkill.isCooldown()) {
                selectedSkill.lastTimeUseThisSkill = currentTimeMillis;
            } else {
                return;
            }
            if (isDontAttack()) {
                return;
            }
            if (this.equipment[1] == null) {
                return;
            }
            if (selectedSkill == null) {
                return;
            }
            if (clone != null && !clone.isDead && clone.isNhanBan) {
                ms.reader().mark(10000);
                clone.attackAllType(ms, type);
                ms.reader().reset();
            }
            ArrayList<Mob> mobs = new ArrayList<>();
            ArrayList<Char> chars = new ArrayList<>();
            int left = this.x - selectedSkill.dx;
            int right = this.x + selectedSkill.dx;
            int top = this.y - selectedSkill.dy - 50;
            int bottom = this.y + selectedSkill.dy;
            // int dx = selectedSkill.dx + 20;
            int len = ms.reader().readByte();
            Vector<Mob> m = new Vector<>();
            for (int i = 0; i < len; i++) {
                int id = ms.reader().readUnsignedByte();
                if ((this.faction == 0 && mapId == 99) || (this.faction == 1 && mapId == 103)) {
                    continue;
                }
                Mob mob = null;
                if (id == 127) {
                    mob = this.mob;
                } else {
                    mob = zone.findMoLivebByID(id);
                }
                if (mob == null) {
                    continue;
                }
                if (!isMeCanAttackNpc(mob)) {
                    continue;
                }
                if (mob != null && mob.hp > 0) {
                    m.add(mob);
                }
            }
            Vector<Char> m2 = new Vector<>();
            while (ms.reader().available() > 0) {
                int id = ms.reader().readInt();
                if (id == -1) {
                    continue;
                }
                Char _char = zone.findCharById(id);
                if (_char != null && _char.hp > 0 && _char != this) {
                    if (!isMeCanAttackOtherPlayer(_char)) {
                        continue;
                    }
                    if (_char.isInvisible()) {
                        continue;
                    }
                    m2.add(_char);
                }
            }
            int focusX = 0;
            int focusY = 0;
            Char charFocus = null;
            Mob mobFocus = null;
            if (type == 1) {
                if (m.size() > 0) {
                    Mob mob = m.get(0);
                    int rangeMove = mob.template.rangeMove;
                    if (mob.x >= (left - rangeMove) && mob.x <= (right + rangeMove) && mob.y >= (top - rangeMove)
                            && mob.y <= (bottom + rangeMove)) {
                        mobFocus = mob;
                        if (!mobs.contains(mob)) {
                            mobs.add(mob);
                        }
                        focusX = mob.x;
                        focusY = mob.y;
                    }
                }
            } else if (type == 2) {
                if (m2.size() > 0) {
                    Char _char = m2.get(0);
                    if (left <= _char.x && _char.x <= right && top <= _char.y && _char.y <= bottom) {
                        charFocus = _char;
                        chars.add(_char);
                        focusX = _char.x;
                        focusY = _char.y;
                    }
                }
            }
            byte maxFight = selectedSkill.maxFight;
            if (mobFocus == null && charFocus == null) {
                ArrayList<Mob> mobss = new ArrayList<>();
                for (Mob mob : m) {
                    mobss.add(mob);
                    zone.getService().attackMonster(-1, false, mob);
                }
                zone.getService().setSkillPaint_1(mobss, this, (byte) selectedSkill.template.id);
                ArrayList<Char> chars2 = new ArrayList<>();
                for (Char _char : m2) {
                    chars2.add(_char);
                    zone.getService().attackCharacter(0, 0, _char);
                }
                zone.getService().setSkillPaint_2(chars2, this, (byte) selectedSkill.template.id);
            } else {
                int lent = m.size();
                if (lent > maxFight) {
                    lent = maxFight;
                }
                if (lent > 0) {
                    for (int i = 0; i < lent; i++) {
                        Mob mob2 = m.get(i);
                        if (mob2.status != 1 && mob2.status != 0 && mob2 != mobFocus && focusX - 100 <= mob2.x
                                && mob2.x <= focusX + 100 && focusY - 50 <= mob2.y && mob2.y <= focusY + 50) {
                            if (!mobs.contains(mob2)) {
                                mobs.add(mob2);
                            }
                        }
                    }
                }
                int lent2 = m2.size();
                if (lent2 > maxFight - lent) {
                    lent2 = maxFight - lent;
                }
                if (lent2 > 0) {
                    for (int i = 0; i < lent2; i++) {
                        Char _char2 = m2.get(i);
                        if (_char2 != charFocus && focusX - 100 <= _char2.x && _char2.x <= focusX + 100
                                && focusY - 50 <= _char2.y && _char2.y <= focusY + 50) {
                            if (!chars.contains(_char2)) {
                                chars.add(_char2);
                            }
                        }
                    }
                }
            }
            if (mobs.isEmpty() && chars.isEmpty()) {
                return;
            }
            int manaUse = selectedSkill.manaUse;
            if (this.mp < manaUse) {
                return;
            }
            addMp(-manaUse);
            getService().updateMp();
            attackMonster(mobs);
            attackCharacter(chars);
            return;
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public boolean isMeCanAttackNpc(Mob mob) {
        if (isLockFire) {
            return false;
        }
        if (mob == null || this.selectedSkill == null || this.selectedSkill.template.type == 2
                || this.selectedSkill.template.type == 3 || (this.selectedSkill.template.type == 4 && !mob.isDead)) {
            return false;
        }
        if (mob != null) {
            int templateID = mob.template.id;
            return !((templateID == MobName.VET_THU_HO && this.typePk == Char.PK_PHE1)
                    || (templateID == MobName.BACH_LONG_TRU && this.typePk == Char.PK_PHE1)
                    || (templateID == MobName.ONG_TU_SI && this.typePk == Char.PK_PHE2)
                    || (templateID == MobName.HAC_LONG_TRU && this.typePk == Char.PK_PHE2)
                    || (templateID == MobName.LAO_NHI && this.typePk == Char.PK_PHE1)
                    || (templateID == MobName.LAO_TAM && this.typePk == Char.PK_PHE2)
                    || (templateID == MobName.LAO_DAI && this.typePk == Char.PK_PHE3)
                    || (templateID == MobName.THUY_TINH && typePk == Char.PK_PHE1)
                    || (templateID == MobName.SON_TINH && typePk == Char.PK_PHE2));
        }

        return false;
    }

    public boolean isMeCanAttackOtherPlayer(Char cAtt) {
        if (isLockFire || cAtt.isInvisible()) {
            return false;
        }
        if (cAtt != null && cAtt.isNhanBan) {
            return false;
        }
        if (cAtt == null || this.selectedSkill == null || this.selectedSkill.template.type == 2
                || this.selectedSkill.template.type == 3 || (this.selectedSkill.template.type == 4 && !cAtt.isDead)) {
            return false;
        }

        return ((((this.typePk == PK_PHE3 && (cAtt.typePk == PK_PHE1 || cAtt.typePk == PK_PHE2))
                || (this.typePk == PK_PHE1 && (cAtt.typePk == PK_PHE2 || cAtt.typePk == PK_PHE3))
                || (this.typePk == PK_PHE2 && (cAtt.typePk == PK_PHE1 || cAtt.typePk == PK_PHE3))) && !isTeam(cAtt)
                && !isLang()) || (cAtt.typePk == PK_DOSAT && !isTeam(cAtt) && !isLang())
                || (this.typePk == PK_DOSAT && !this.isTeam(cAtt) && !isLang())
                || (this.typePk == PK_NHOM && cAtt.typePk == PK_NHOM && !this.isTeam(cAtt) && !isLang())
                || (this.testCharId >= 0 && this.testCharId == cAtt.id)
                || (this.killCharId >= 0 && this.killCharId == cAtt.id && !isLang())
                || (cAtt.killCharId >= 0 && cAtt.killCharId == this.id && !isLang())) && !cAtt.isDead;
    }

    public boolean isLang() {
        if (mapId == 1 || mapId == 27 || mapId == 72 || mapId == 10 || mapId == 17 || mapId == 22 || mapId == 32
                || mapId == 38 || mapId == 43 || mapId == 48) {
            return true;
        }
        return false;
    }

    public boolean isTeam(Char c) {
        if (group != null) {
            synchronized (group.memberGroups) {
                for (MemberGroup p : group.memberGroups) {
                    if (c.id == p.charId) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isMobSameParty(Mob mob) {
        if (mob != null) {
            int templateID = mob.template.id;
            if ((templateID == 142 && this.typePk == Char.PK_PHE1) || (templateID == 143 && this.typePk == Char.PK_PHE2)
                    || (templateID == 143 && this.typePk == Char.PK_PHE3)) {
                return true;
            }
            return false;
        }
        return false;
    }

    public void attackCharacter(Message ms) {
        try {
            if (this.isDead) {
                return;
            }
            long currentTimeMillis = System.currentTimeMillis();
            if (selectedSkill != null && !selectedSkill.isCooldown()) {
                selectedSkill.lastTimeUseThisSkill = currentTimeMillis;
            } else {
                return;
            }
            if (isDontAttack()) {
                return;
            }
            if (this.equipment[1] == null) {
                return;
            }
            if (selectedSkill == null) {
                return;
            }
            if (clone != null && !clone.isDead && clone.isNhanBan) {
                ms.reader().mark(10000);
                clone.attackCharacter(ms);
                ms.reader().reset();
            }
            ArrayList<Char> chars = new ArrayList<>();
            int num = 0;
            if (this.classId == 0 || this.classId == 1 || this.classId == 3 || this.classId == 5) {
                num = 40;
            }
            int num2 = this.x - selectedSkill.dx;
            int num3 = this.x + selectedSkill.dx;
            int num4 = this.y - selectedSkill.dy - num;
            int num5 = this.y + selectedSkill.dy;
            Char charFocus = null;
            Vector<Char> characterCanAttack = new Vector<>();
            // Char originChar = this.getOriginChar();
            while (ms.reader().available() > 0) {
                int id = ms.reader().readInt();
                Char _char = zone.findCharById(id);
                if (_char != null && _char != this && _char.hp > 0) {
                    if (!isMeCanAttackOtherPlayer(_char)) {
                        continue;
                    }
                    if (_char.isInvisible()) {
                        continue;
                    }
                    characterCanAttack.add(_char);
                }
            }
            if (characterCanAttack.size() > 0) {
                Char _char = characterCanAttack.get(0);
                int skillTemplateId = selectedSkill.template.id;
                if (skillTemplateId == 42) {
                    setXY(_char.x, _char.y);
                    zone.getService().teleport(this);
                    getService().resetPoint();
                    if (optionsSupportSkill[62] > 0) {
                        Effect eff = new Effect(18, optionsSupportSkill[62], 0);
                        _char.em.setEffect(eff);
                    }
                }
                if (num2 <= _char.x && _char.x <= num3 && num4 <= _char.y && _char.y <= num5) {
                    charFocus = _char;
                    if (!chars.contains(_char)) {
                        chars.add(_char);
                    }
                }
            }
            byte maxFight = selectedSkill.maxFight;
            if (charFocus != null) {
                int lent = characterCanAttack.size();
                if (lent > maxFight) {
                    lent = maxFight;
                }
                for (int i = 0; i < lent; i++) {
                    Char _char = characterCanAttack.get(i);
                    if (_char != charFocus && charFocus.x - 100 <= _char.x && _char.x <= charFocus.x + 100
                            && charFocus.y - 50 <= _char.y && _char.y <= charFocus.y + 50) {
                        if (!chars.contains(_char)) {
                            chars.add(_char);
                        }
                    }
                }
            } else {
                ArrayList<Char> charAttackNoDame = new ArrayList<>();
                for (int i = 0; i < characterCanAttack.size(); i++) {
                    Char _char = characterCanAttack.get(i);
                    charAttackNoDame.add(_char);
                    zone.getService().attackCharacter(0, 0, _char);
                }
                zone.getService().setSkillPaint_2(charAttackNoDame, this, (byte) selectedSkill.template.id);
            }
            if (chars.isEmpty()) {
                return;
            }
            int manaUse = selectedSkill.manaUse;
            if (this.mp < manaUse) {
                return;
            }
            addMp(-manaUse);
            getService().updateMp();
            attackCharacter(chars);
            return;
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void addHp(double add) {
        if (add < 0 && isCool()) {
            add *= 2;
        }
        this.hp += add;
    }

    public void addMp(double add) {
        if (add < 0 && isCool()) {
            add *= 2;
        }
        this.mp += add;
    }

    public void testInvite(Message msg) {
        try {
            if (!isHuman) {
                return;
            }
            if (zone.tilemap.isNotPk()) {
                serverMessage("Không thể sử dụng tính năng này trong lôi đài.");
                return;
            }
            Char _char = this.zone.findCharById(msg.reader().readInt());
            if (_char == null) {
                serverDialog("Hiện người này không còn trong khu vực này.");
                return;
            }
            PlayerInvite p = _char.invite.findCharInvite(Invite.TY_THI, this.id);
            if (p != null) {
                serverDialog("Không thể mời tỷ thí liên tục. Vui lòng thử lại sau 30s nữa.");
                return;
            }
            _char.invite.addCharInvite(Invite.TY_THI, this.id, 30);
            if (!_char.isTest) {
                _char.getService().testInvite(this.id);
            } else {
                serverDialog("Hiện tại người này đang tỷ thí với người khác.");
            }
        } catch (Exception e) {
            Log.error("err: " + e.getMessage(), e);
        }
    }

    public void testEnd(int num, Char pl) {
        if (num > 0) {
            this.hp = num;
        }
        pl.testCharId = 0;
        pl.isTest = false;
        zone.getService().testEnd(this.id, pl.id, num);
        this.isTest = false;
        this.testCharId = 0;
    }

    public void testAccept(Message msg) {
        try {
            if (!isHuman) {
                return;
            }
            Char player = zone.findCharById(msg.reader().readInt());
            if (player == null) {
                serverDialog("Hiện người này không còn trong khu vực này.");
                return;
            }
            if (player.isTest) {
                serverDialog("Hiện tại người này đang tỷ thí với người khác.");
                return;
            }
            PlayerInvite p = invite.findCharInvite(Invite.TY_THI, player.id);
            if (p != null) {
                player.testCharId = this.id;
                this.testCharId = player.id;
                this.isTest = true;
                player.isTest = true;
                zone.getService().testAccept(this.id, player.id);
            } else {
                serverDialog("Đã hết thời gian để chấp nhận.");
            }
        } catch (Exception e) {
        }
    }

    public void addCuuSat(Message msg) {
        try {
            if (!isHuman) {
                return;
            }
            if (isDead) {
                return;
            }
            if ((isVillage() || isSchool()) && mapId != 138) {
                serverDialog("Không được cừu sát ở trong trường hoặc làng.");
                return;
            }
            if (zone.tilemap.isNotPk()) {
                serverMessage("Không thể sử dụng tính năng này.");
                return;
            }
            if (killCharId == 0) {
                Char player = this.zone.findCharById(msg.reader().readInt());
                if (player != null && player.zone != null) {
                    Equip equip = player.equipment[ItemTemplate.TYPE_THUNUOI];
                    if (equip != null && (equip.id == ItemName.TUAN_LOC_BOSS || equip.id == ItemName.PET_BORU)) {
                        serverMessage("Không thể đồ sát người này.");
                        return;
                    }

                    this.killCharId = player.id;
                    this.isCuuSat = true;
                    player.getService().addCuuSat(id);
                    getService().meCuuSat(player.id);
                }
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void clearCuuSat() {
        try {
            Char player = this.zone.findCharById(this.killCharId);
            if (player != null) {
                player.getService().clearCuuSat(id);
                getService().clearCuuSat(id);
            }
            this.killCharId = 0;
            this.isCuuSat = false;
        } catch (Exception e) {
        }
    }

    public void returnTownFromDead(Message ms) {
        if (!this.isDead) {
            return;
        }
        if (!isHuman) {
            return;
        }
        if (zone.tilemap.isNotReturnTown()) {
            serverMessage("Không thể sử dụng tính năng này.");
            return;
        }
        wakeUpFromDead();
        zone.returnTownFromDead(this);
        getService().loadInfo();
    }

    public void wakeUpFromDead(Message ms) {
        try {
            if (!this.isDead) {
                return;
            }
            if (!isHuman) {
                return;
            }
            if (zone.tilemap.isNotRivival()) {
                serverMessage("Không thể sử dụng tính năng này tại đây.");
                return;
            }
            if (user.gold < 1) {
                serverDialog("Cần 1 lượng để hồi sinh tại chỗ.");
                return;
            }
            addGold(-1);
            wakeUpFromDead();
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void wakeUpFromDead() {
        try {
            if (!this.isDead) {
                return;
            }
            this.isDead = false;
            this.hp = this.maxHP;
            this.mp = this.maxMP;
            getService().meLive();
            getService().resetPoint();
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void updateWithBalanceMessage() {
        getService().loadInfo();
        if (this.getCoin() > 2000000000) {
            serverMessage("Bạn có " + NinjaUtils.getCurrency(this.getCoin()) + " xu trong hành trang");
        }
    }

    public void tayTiemNang(short npcId) {
        if ((npcId == 9 && getSys() == 1) || (npcId == 10 && getSys() == 2) || (npcId == 11 && getSys() == 3)) {
            if (this.tayTiemNang > 0) {
                this.potentialPoint = (short) (this.level * 10);
                if (this.level >= 70) {
                    this.potentialPoint += (this.level - 70) * 10;
                }
                if (this.level >= 80) {
                    this.potentialPoint += (this.level - 80) * 10;
                }
                if (this.level >= 90) {
                    this.potentialPoint += (this.level - 90) * 10;
                }
                if (this.level >= 100) {
                    this.potentialPoint += (this.level - 100) * 10;
                }
                if (this.level >= 130) {
                    this.potentialPoint += (this.level - 130) * 10;
                }
                if (this.level >= 145) {
                    this.potentialPoint += (this.level - 145) * 10;
                }
                this.potentialPoint += 10 * (this.limitTiemNangSo + this.limitBangHoa);
                this.potential[1] = 5;
                this.potential[2] = 5;
                if (this.classId == 1 || this.classId == 3 || this.classId == 5) {
                    this.potential[0] = 10;
                    this.potential[3] = 5;
                } else {
                    this.potential[0] = 5;
                    this.potential[3] = 10;
                }
                this.tayTiemNang--;
                setAbility();
                getService().updatePotential();
                if (tayTiemNang > 0) {
                    getService().npcChat(npcId, String.format(
                            "Ta đã giúp con tẩy điểm tiềm năng. Con vẫn có thể tẩy thêm được %d lần tẩy tiềm năng nữa.",
                            tayTiemNang));
                } else {
                    getService().npcChat(npcId,
                            "Ta đã giúp con tẩy điểm tiềm năng. Đây là lần cuối con được tẩy tiềm năng, hãy sử dụng cho thật tốt điểm tiềm năng nhé.");
                }
            } else {
                getService().npcChat(npcId, "Số lần tẩy điểm tiềm năng của con đã hết.");
            }
        } else {
            getService().npcChat(npcId, "Con không phải là học sinh của trường này, không thể tẩy điểm ở đây.");
        }
    }

    public boolean isCheckSkillPoint() {
        int skillPoint = (short) (this.level - 9);
        if (classId == 0) {
            skillPoint = 0;
        }
        skillPoint += this.limitKyNangSo + this.limitPhongLoi;
        double point = this.skillPoint;
        for (Skill skill : vSkill) {
            point += skill.point - 1;
        }

        return skillPoint >= point;
    }

    public boolean isCheckPotential() {
        int sum = (int) (NinjaUtils.sum(this.potential) + this.potentialPoint);
        int potentialPoint = 0;
        if (classId == 0) {
            potentialPoint = (this.level - 1) * 5 + 30;
            potentialPoint += 10 * (this.limitTiemNangSo + this.limitBangHoa);
            if (sum > potentialPoint) {
                potentialPoint = (this.level - 1) * 11 + 30;
                potentialPoint += 10 * (this.limitTiemNangSo + this.limitBangHoa);
            }
        } else {
            potentialPoint = (short) (this.level * 10);
            if (this.level >= 70) {
                potentialPoint += (this.level - 70) * 10;
            }
            if (this.level >= 80) {
                potentialPoint += (this.level - 80) * 10;
            }
            if (this.level >= 90) {
                potentialPoint += (this.level - 90) * 10;
            }
            if (this.level >= 100) {
                potentialPoint += (this.level - 100) * 10;
            }
            potentialPoint += 10 * (this.limitTiemNangSo + this.limitBangHoa);
            potentialPoint += 25;
        }
        return sum <= potentialPoint;
    }

    public void tayKyNang(short npcId) {
        if ((npcId == 9 && getSys() == 1) || (npcId == 10 && getSys() == 2) || (npcId == 11 && getSys() == 3)) {
            if (this.tayKyNang > 0) {
                this.skillPoint = (short) (this.level - 9);
                this.skillPoint += this.limitKyNangSo + this.limitPhongLoi;
                Vector<Skill> vSkillTemp = (Vector<Skill>) this.vSkill.clone();
                vSkillFight.clear();
                vSupportSkill.clear();
                vSkill.clear();
                for (int i = 0; i < vSkillTemp.size(); i++) {
                    Skill my = vSkillTemp.elementAt(i);
                    Skill skillNew = GameData.getInstance().getSkill(this.classId, my.template.id, 1);
                    for (int j = 0; j < this.onOSkill.length; j++) {
                        if (onOSkill[j] == my.template.id) {
                            onOSkill[j] = (byte) skillNew.template.id;
                        }
                    }
                    for (int j = 0; j < this.onKSkill.length; j++) {
                        if (onKSkill[j] == my.template.id) {
                            onKSkill[j] = (byte) skillNew.template.id;
                        }
                    }
                    if (onCSkill != null) {
                        for (int j = 0; j < this.onCSkill.length; j++) {
                            if (onCSkill[j] == my.template.id) {
                                onCSkill[j] = (byte) skillNew.template.id;
                            }
                        }
                    }
                    vSkill.add(skillNew);
                    if (skillNew.template.type == Skill.SKILL_AUTO_USE) {
                        vSupportSkill.add(skillNew);
                    } else if ((skillNew.template.type == Skill.SKILL_CLICK_USE_ATTACK
                            || skillNew.template.type == Skill.SKILL_CLICK_LIVE
                            || skillNew.template.type == Skill.SKILL_CLICK_USE_BUFF
                            || skillNew.template.type == Skill.SKILL_CLICK_NPC)
                            && (skillNew.template.maxPoint == 0
                            || (skillNew.template.maxPoint > 0 && skillNew.point > 0))) {

                        vSkillFight.add(skillNew);
                    }
                }
                this.tayKyNang--;
                setAbility();
                getService().loadSkill();
                byte type = 0;
                if (!isHuman) {
                    type = 1;
                }
                getService().sendSkillShortcut("OSkill", onOSkill, type);
                getService().sendSkillShortcut("KSkill", onKSkill, type);
                if (onCSkill != null) {
                    getService().sendSkillShortcut("CSkill", onCSkill, type);
                }
                if (tayKyNang > 0) {
                    getService().npcChat(npcId, String.format(
                            "Ta đã giúp con tẩy điểm kỹ năng. Con vẫn có thể tẩy thêm được %d lần tẩy kỹ năng nữa.",
                            tayKyNang));
                } else {
                    getService().npcChat(npcId,
                            "Ta đã giúp con tẩy điểm kỹ năng. Đây là lần cuối con được tẩy kỹ năng, hãy sử dụng cho thật tốt điểm kỹ năng nhé.");
                }
            } else {
                getService().npcChat(npcId, "Số lần tẩy điểm kỹ năng của con đã hết.");
            }
        } else {
            getService().npcChat(npcId, "Con không phải là học sinh của trường này, không thể tẩy điểm ở đây.");
        }
    }

    public void addSkill(Skill skill) {
        vSkill.add(skill);
        if (skill.template.type == Skill.SKILL_AUTO_USE) {
            vSupportSkill.add(skill);
        } else if ((skill.template.type == Skill.SKILL_CLICK_USE_ATTACK
                || skill.template.type == Skill.SKILL_CLICK_LIVE
                || skill.template.type == Skill.SKILL_CLICK_USE_BUFF
                || skill.template.type == Skill.SKILL_CLICK_NPC)
                && (skill.template.maxPoint == 0
                || (skill.template.maxPoint > 0 && skill.point > 0))) {
            vSkillFight.add(skill);
        }
    }

    public void expandBag(Item item) {
        if (!isHuman) {
            warningClone();
            return;
        }
        short[] ids = new short[]{215, 229, 283, 801 , 1128};
        byte[] numberCells = new byte[]{6, 6, 12, 24 , 42};
        if (numberUseExpanedBag < ids.length) {
            int i = ids[numberUseExpanedBag];
            int i2 = numberCells[numberUseExpanedBag];
            if (i == item.id) {
                this.numberCellBag += i2;
                this.numberUseExpanedBag++;
                Item[] bag = new Item[this.numberCellBag];
                for (int num14 = 0; num14 < this.bag.length; num14++) {
                    bag[num14] = this.bag[num14];
                }
                this.bag = bag;
                getService().expandBag(item);
                removeItem(item.index, item.getQuantity(), false);
            } else {
                String name = ItemManager.getInstance().getItemName(i);
                serverMessage("Hãy dùng sử dụng " + name);
            }
        } else {
            serverMessage("Bạn đã sử dụng tất cả các loại túi vải.");
        }
    }

    public void startDie() {
        try {
            zone.startDie(this);

            if (trade != null) {
                trade.closeUITrade();
            }
            if (escorted != null) {
                escortFailed();
            }
            if (escortedEvent != null) {
                escortEventFailed();
            }
            if (this.isTest && this.testCharId != 0) {
                Char player = this.zone.findCharById(this.testCharId);
                if (player != null) {
                    this.testEnd(1, player);
                }
            }
            if (taskId == TaskName.NV_THU_THAP_OAN_HON) {
                if (taskMain != null && taskMain.index == 1) {
                    int index = getIndexItemByIdInBag(ItemName.OAN_HON);
                    if (index != -1) {
                        removeItem(index, bag[index].getQuantity() / 2, true);
                    }
                    updateTaskCount(-taskMain.count / 2);
                }
            }
            this.isCuuSat = false;
            this.killCharId = 0;
            zone.getService().clearCuuSat(this);

            if (this.mob != null) {
                this.mob.die();
                this.zone.getService().attackMonster(this.mob.hp, false, this.mob);
                this.serverMessage("Quái vật quá mạnh, bạn không thể tiếp tục đánh do bị trọng thương");
                this.mob = null;
            }

            if (this.mapId == 138 || this.mapId == 137 || this.mapId == 136 || this.mapId == 135 || this.mapId == 134) {
                this.isDead = false;
                this.hp = this.maxHP;
                this.mp = this.maxMP;
                short[] xy = NinjaUtils.getXY(this.saveCoordinate);
                setXY(xy[0], xy[1]);
                changeMap(this.saveCoordinate);
                getService().loadInfo();
                return;
            }
            if (zone.tilemap.isChienTruong()) {
                this.isDead = false;
                this.hp = this.maxHP;
                this.mp = this.maxMP;
                int mapID = -1;
                short x = -1;
                short y = -1;
                if (this.faction == 0 || this.faction == 2) {
                    mapID = 98;
                    x = 104;
                    y = 336;
                }
                if (this.faction == 1) {
                    mapID = 104;
                    x = 104;
                    y = 240;
                }
                setXY(x, y);
                changeMap(mapID);
                return;
            }
            if (this.mapId == 162 || this.mapId == 163 || this.mapId == 164 || this.mapId == 165) {
                this.isDead = false;
                this.hp = this.maxHP;
                this.mp = this.maxMP;
                short[] xy = NinjaUtils.getXY(this.saveCoordinate);
                setXY(xy[0], xy[1]);
                changeMap(this.saveCoordinate);
                getService().loadInfo();
                return;
            }

            if (zone.tilemap.isFujukaSanctuary()) {

            }

            this.y = (short) (this.y / 24 * 24);
            if (this.y == 0) {
                while (this.zone.tilemap.tileTypeAt(this.x, this.y, TileMap.T_TOP)) {
                    this.y += 24;
                    if (this.y > zone.tilemap.pxh) {
                        break;
                    }
                }
            }
            while (!this.zone.tilemap.tileTypeAt(this.x, this.y, TileMap.T_TOP)) {
                this.y += 24;
                if (this.y > zone.tilemap.pxh) {
                    break;
                }
            }
            zone.getService().playerMove(this);
            getService().resetPoint();
            this.isDead = true;
            this.hp = 0;
            if (this.expDown > 0) {
                getService().meDieExpDown();
            } else {
                getService().meDie();
            }
            zone.getService().waitToDie(this);
        } finally {
            if (arenaT != null) {
                boolean win = (isBot()) ? true : false;
                arenaT.setWin(win);
            }
            if (zone instanceof Gymnasium) {
                Gymnasium gymnasium = (Gymnasium) zone;
                gymnasium.setWin();
            }
        }
    }

    public void changeZone(Message ms) {
        try {
            if (zone.tilemap.isNotChangeZone()) {
                serverDialog("Không thể chuyển khu");
                return;
            }
            PlayerInvite p = this.invite.findCharInvite(Invite.CHANGE_ZONE, -1);
            if (p != null) {
                //getService().endWait("Vui lòng chuyển khu lại sau " + p.time + " giây.");
                //getService().endDlg(true);
                //serverDialog("Vui lòng chuyển khu lại sau " + p.time + " giây.");
                return;
            }

            this.invite.addCharInvite(Invite.CHANGE_ZONE, -1, TIME_CHANGE_ZONE_LIMIT);
            byte zoneId = ms.reader().readByte();

            byte indexUI = ms.reader().readByte();
            Map map = zone.map;
            List<Zone> zones = map.getZones();
            if (zoneId < 0 || zoneId >= zones.size()) {
                return;
            }
            if (zones.get(zoneId).getNumberChar() >= 24) {
                getService().endWait("Khu vực đã đầy!");
                return;
            }
            outZone();
            joinZone(this.mapId, zoneId, -1);
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void splitItem(Message ms) {
        lockItem.lock();
        try {
            if (!isHuman) {
                warningClone();
                return;
            }
            int index = ms.reader().readUnsignedByte();
            if (index < 0 || index >= this.numberCellBag) {
                return;
            }
            Item item = this.bag[index];
            if (item != null
                    && (item.template.isTypeWeapon() || item.template.isTypeClothe() || item.template.isTypeAdorn())) {
                if (item.upgrade > 0) {
                    int num = 0;
                    if (item.template.isTypeWeapon()) {
                        for (byte i = 0; i < item.upgrade; i++) {
                            num += GameData.UP_WEAPON[i];
                        }
                    } else if (item.template.type % 2 == 0) {
                        for (byte i = 0; i < item.upgrade; i++) {
                            num += GameData.UP_CLOTHE[i];
                        }
                    } else {
                        for (byte i = 0; i < item.upgrade; i++) {
                            num += GameData.UP_ADORN[i];
                        }
                    }
                    num /= 2;
                    int num2 = 0;
                    List<Item> list = new ArrayList<>();
                    for (int n = GameData.UP_CRYSTAL.length - 1; n >= 0; n--) {
                        if (num >= GameData.UP_CRYSTAL[n]) {
                            Item item2 = ItemFactory.getInstance().newItem(n);
                            item2.isLock = true;
                            list.add(item2);
                            num -= GameData.UP_CRYSTAL[n];
                            n++;
                            num2++;
                        }
                    }
                    if (num2 > getSlotNull()) {
                        warningBagFull();
                        return;
                    }
                    int i2 = 0;
                    int size = list.size();
                    for (int i = 0; i < this.numberCellBag; i++) {
                        if (this.bag[i] == null && i2 < size) {
                            this.bag[i] = list.get(i2);
                            this.bag[i].index = i;
                            i2++;
                        }
                    }
                    int upgradeOld = item.upgrade;
                    item.next(-upgradeOld);
                    getService().splitItem(list);
                } else {
                    serverDialog(language.getString("NOT_UPGRADE"));
                }
            }
        } catch (Exception ex) {
            Log.error("tach da err: " + ex.getMessage(), ex);
        } finally {
            lockItem.unlock();
        }
    }

    public void upPearl(Message ms, boolean isCoin) {
        lockItem.lock();
        try {
            if (trade != null) {
                warningTrade();
                return;
            }
            if (!isHuman) {
                warningClone();
                return;
            }
            Vector<Item> crystals = new Vector<>();
            boolean[] list = new boolean[numberCellBag];
            while (ms.reader().available() > 0) {
                int indexItem = ms.reader().readUnsignedByte();
                if (indexItem < 0 || indexItem >= numberCellBag) {
                    continue;
                }
                if (!list[indexItem]) {
                    list[indexItem] = true;
                    if (bag[indexItem] != null && bag[indexItem].id <= 11) {
                        if (bag[indexItem].id == 11) {
                            serverDialog(language.getString("UP_MAX"));
                            return;
                        }
                        crystals.add(bag[indexItem]);
                    }
                }
            }
            int size = crystals.size();
            if (size > 24 || size < 2) {
                serverDialog(language.getString("NOT_ENOUGH_UPPEARL"));
                return;
            }
            int percent = 0;
            int i = 0;
            for (Item item : crystals) {
                percent += GameData.UP_CRYSTAL[item.id];
            }
            if (percent > 0) {
                for (i = GameData.UP_CRYSTAL.length - 1; i >= 0; i--) {
                    if (percent > GameData.UP_CRYSTAL[i]) {
                        break;
                    }
                }
            }
            if (i >= GameData.UP_CRYSTAL.length - 1) {
                i = GameData.UP_CRYSTAL.length - 2;
            }
            percent = percent * 100 / GameData.UP_CRYSTAL[i + 1];
            if (percent <= 40) {
                serverDialog("Tỉ lệ trên 40% mới có thể luyện");
                return;
            }
            int id = i + 1;
            int indexNull = getIndexByItem(null);
            if (indexNull == -1) {
                warningBagFull();
                return;
            }
            int coin = GameData.COIN_UP_CRYSTAL[i + 1];
            if (isCoin) {
                if (this.coin < coin) {
                    serverDialog(language.getString("NOT_ENOUGH_COIN_UPPEARL"));
                    return;
                }
                this.coin -= coin;
            } else {
                if (((long) this.coin + (long) this.yen) < coin) {
                    serverDialog(language.getString("NOT_ENOUGH_COIN_UPPEARL"));
                    return;
                }
                if (this.yen < coin) {
                    coin -= this.yen;
                    this.yen = 0;
                } else {
                    this.yen -= coin;
                    coin = 0;
                }
                this.coin -= coin;
            }
            for (Item item : crystals) {
                removeItem(item.index, item.getQuantity(), false);
            }
            byte type = 0;
            int rd = (int)NinjaUtils.nextInt(100);
            if (rd < percent) {
                type = 1;
                Item item = ItemFactory.getInstance().newItem(id);
                item.index = indexNull;
                item.isLock = true;
                bag[indexNull] = item;
                if (Event.isEvent() && rd < 10) {
                    int itemID = Event.getEvent().randomItemID();
                    if (itemID != -1) {
                        Item itemE = ItemFactory.getInstance().newItem(itemID);
                        itemE.setQuantity((int)NinjaUtils.nextInt(1, 5));
                        addItemToBag(itemE);
                    }
                }
            } else {
                Item item = ItemFactory.getInstance().newItem(id - 1);
                item.index = indexNull;
                item.isLock = true;
                bag[indexNull] = item;
            }
            getService().upPearl(isCoin, type, bag[indexNull]);
        } catch (Exception ex) {
            Log.error("luyen da err: " + ex.getMessage(), ex);
        } finally {
            lockItem.unlock();
        }
    }

    public int getIndexByItem(Item item) {
        for (int i = 0; i < this.numberCellBag; i++) {
            if (this.bag[i] == item) {
                return i;
            }
        }
        return -1;
    }

    public void convertUpgrade(Message ms) {
        lockItem.lock();
        try {
            if (!isHuman) {
                warningClone();
                return;
            }
            if (trade != null) {
                warningTrade();
                return;
            }
            int index1 = ms.reader().readUnsignedByte();
            int index2 = ms.reader().readUnsignedByte();
            int index3 = ms.reader().readUnsignedByte();
            int indexMax = Math.max(Math.max(index1, index2), index3);
            int indexMin = Math.min(Math.min(index1, index2), index3);
            if (indexMax >= this.numberCellBag || indexMin < 0) {
                return;
            }
            Item item1 = this.bag[index1];
            Item item2 = this.bag[index2];
            Item item3 = this.bag[index3];
            if (item1 == null || item2 == null || item3 == null) {
                return;
            }
            if (item1.template.isTypeWeapon() != item2.template.isTypeWeapon()
                    || item1.template.isTypeAdorn() != item2.template.isTypeAdorn()
                    || item1.template.isTypeClothe() != item2.template.isTypeClothe()) {
                serverDialog("Trang bị không cùng loại.");
                return;
            }
            if (item2.template.fashion > 0) {
                serverDialog("Không thể chuyển hóa sang trang bị này.");
                return;
            }
            if (item1.upgrade == 0) {
                serverDialog("Trang bị chưa nâng câp.");
                return;
            }
            if (item2.upgrade > 0) {
                serverDialog("Trang bị cần chuyển hoá sang không được nâng cấp.");
                return;
            }
            if (item1.template.level > item2.template.level) {
                serverDialog("Trang bị chuyển hoá sang phải có cấp độ ngang bằng hoặc lớn hơn.");
                return;
            }
            if (item3.template.type == 27) {
                if ((item3.id == 270 && item1.upgrade > 13) || (item3.id == 269 && item1.upgrade > 10)) {
                    serverDialog(item3.template.name + " không phù hợp để chuyển hoá trang bị này.");
                    return;
                }
                byte upgrade = item1.upgrade;
                item2.upgrade = 0;
                item2.next(upgrade);
                item2.isLock = true;
                item1.next(-upgrade);
                item1.isLock = true;
                getService().convertUpgrade(item1, item2);
                removeItem(item3.index, item3.getQuantity(), true);
            } else {
                serverDialog(item3.template.name + " không phải vật phẩm chuyển hoá");
            }
        } catch (Exception ex) {
            Log.error("chuyen hoa err: " + ex.getMessage(), ex);
        } finally {
            lockItem.unlock();
        }
    }

    public void dichChuyen(Message ms) {
        lockItem.lock();
        try {
            if (!isHuman) {
                warningClone();
                return;
            }
            if (trade != null) {
                warningTrade();
                return;
            }
            int index = ms.reader().readUnsignedByte();
            Item item = this.bag[index];
            if (item != null) {
                if (item.template.isTypeClothe() || item.template.isTypeAdorn() || item.template.isTypeWeapon()) {
                    if (item.upgrade >= 12) {
                        for (ItemOption option : item.options) {
                            if (option.optionTemplate.id == 85) {
                                serverDialog("Vật phẩm này đã dịch chuyển");
                                return;
                            }
                        }
                        Vector<Item> items = new Vector<>();
                        boolean[] list = new boolean[numberCellBag];
                        while (ms.reader().available() > 0) {
                            int ctt = ms.reader().readUnsignedByte();
                            if (ctt < 0 || ctt >= numberCellBag) {
                                continue;
                            }
                            if (!list[ctt]) {
                                list[ctt] = true;
                                if (this.bag[ctt] != null && this.bag[ctt].id == 454) {
                                    items.add(this.bag[ctt]);
                                }
                            }

                        }
                        if (items.size() >= 20) {
                            int indexOption = -1;
                            for (int i = 0; i < item.options.size(); i++) {
                                int type = item.options.get(i).optionTemplate.type;
                                if (type >= 0 && type <= 7) {
                                    continue;
                                }
                                indexOption = i;
                                break;
                            }
                            if (indexOption == -1) {
                                indexOption = item.options.size();
                            }
                            item.options.add(indexOption, new ItemOption(85, 0));
                            if (item.template.type == 0) {
                                int[] optionId = {95, 96, 97};
                                item.options.add(indexOption + 1, new ItemOption(optionId[item.sys - 1], 5));
                                item.options.add(indexOption + 2, new ItemOption(79, 5));
                            } else if (item.template.type == 1) {
                                item.options.add(indexOption + 1, new ItemOption(87, (int)NinjaUtils.nextInt(250, 400)));
                                int[] optionId = {88, 89, 90};
                                item.options.add(indexOption + 2,
                                        new ItemOption(optionId[item.sys - 1], (int)NinjaUtils.nextInt(350, 600)));
                            } else if (item.template.type == 2) {
                                item.options.add(indexOption + 1, new ItemOption(80, (int)NinjaUtils.nextInt(24, 28)));
                                item.options.add(indexOption + 2, new ItemOption(91, (int)NinjaUtils.nextInt(10, 14)));
                            } else if (item.template.type == 3) {
                                item.options.add(indexOption + 1, new ItemOption(81, 5));
                                item.options.add(indexOption + 2, new ItemOption(79, 5));
                            } else if (item.template.type == 4) {
                                item.options.add(indexOption + 1, new ItemOption(86, (int)NinjaUtils.nextInt(76, 124)));
//                               ti le gang
                                int percentGang = (int)NinjaUtils.nextInt(76, 124) == 124 ? ((int)NinjaUtils.nextInt(100) > 40 ? 124 : (int)NinjaUtils.nextInt(76, 124)) : (int)NinjaUtils.nextInt(76, 120);
                                item.options.add(indexOption + 2, new ItemOption(94, percentGang));
                            } else if (item.template.type == 5) {
                                int[] optionId = {95, 96, 97};
                                item.options.add(indexOption + 1, new ItemOption(optionId[item.sys - 1], 5));
                                item.options.add(indexOption + 2, new ItemOption(92, (int)NinjaUtils.nextInt(9, 11)));
                            } else if (item.template.type == 6) {
                                item.options.add(indexOption + 1, new ItemOption(83, (int)NinjaUtils.nextInt(250, 450)));
                                item.options.add(indexOption + 2, new ItemOption(82, (int)NinjaUtils.nextInt(250, 450)));
                            } else if (item.template.type == 7) {
                                int[] optionId = {95, 96, 97};
                                item.options.add(indexOption + 1, new ItemOption(optionId[item.sys - 1], 5));
                                optionId = new int[]{88, 89, 90};
                                item.options.add(indexOption + 2,
                                        new ItemOption(optionId[item.sys - 1], (int)NinjaUtils.nextInt(350, 600)));
                            } else if (item.template.type == 8) {
                                item.options.add(indexOption + 1, new ItemOption(83, (int)NinjaUtils.nextInt(250, 450)));
                                item.options.add(indexOption + 2, new ItemOption(84, (int)NinjaUtils.nextInt(76, 124)));
                            } else if (item.template.type == 9) {
                                item.options.add(indexOption + 1, new ItemOption(84, (int)NinjaUtils.nextInt(76, 124)));
                                item.options.add(indexOption + 2, new ItemOption(82, (int)NinjaUtils.nextInt(250, 450)));
                            }
                            getService().itemInfo(item, (byte) 3, (byte) index);
                            getService().endDlg(true);
                            serverMessage("Trang bị đã được dịch chuyển");
                            for (int i = 0; i < 20; i++) {
                                removeItem(items.get(i).index, 1, true);
                            }
                        } else {
                            serverDialog("Cần phải có đủ 20 chuyển tinh thạch để dịch chuyển trang bi");
                        }
                    } else {
                        serverDialog("Vui lòng nâng cấp trang bị lên cấp 12");
                    }
                } else {
                    serverDialog("Vật phẩm này không dùng trong dịch chuyển");
                }
            } else {
                serverDialog("Vui lòng đăng nhập lại");
            }
        } catch (IOException ex) {
            Log.error("dich chuyen err: " + ex.getMessage(), ex);
        } finally {
            lockItem.unlock();
        }
    }

    public void tinhLuyen(Message ms) {
        lockItem.lock();
        try {
            if (!isHuman) {
                warningClone();
                return;
            }
            if (trade != null) {
                warningTrade();
                return;
            }
            int indexUI = ms.reader().readUnsignedByte();
            Item item = this.bag[indexUI];
            if (item != null) {
                if (item.template.isTypeClothe() || item.template.isTypeAdorn() || item.template.isTypeWeapon()
                        || item.template.type == 12 || item.template.type == 15
                        || (item.template.isTypeMount() && item.template.type != 33)) {
                    for (int i = 0; i < item.options.size(); i++) {
                        ItemOption option = item.options.get(i);
                        int optionId = option.optionTemplate.id;
                        int optionParam = option.param;
                        if (optionId == 85) {
                            if (item.template.type == 15) {
                                int id = 0;
                                switch (item.id) {
                                    case 397:
                                    case 398:
                                        id = 834;
                                        break;

                                    case 399:
                                    case 400:
                                        id = 836;
                                        break;

                                    case 401:
                                    case 402:
                                        id = 835;
                                        break;
                                }
                                int cap = optionParam;
                                int quantity = (cap + 1) * 3;
                                int fee = (cap + 5) * 2;
                                if (fee > user.gold) {
                                    serverDialog(language.getString("NOT_ENOUGH_MONEY"));
                                    return;
                                }
                                int percent = 100 - (cap * 10) - ((cap + 1) / 2);
                                int totalQuantity = 0;
                                Vector<Item> items = new Vector<>();
                                boolean[] list = new boolean[numberCellBag];
                                while (ms.reader().available() > 0) {
                                    int index = ms.reader().readUnsignedByte();
                                    if (index < 0 || index >= numberCellBag) {
                                        continue;
                                    }
                                    if (!list[index]) {
                                        list[index] = true;
                                        Item itm = this.bag[index];
                                        if (itm != null) {
                                            if (itm.id == id) {
                                                items.add(itm);
                                                totalQuantity += itm.getQuantity();
                                            }
                                        }
                                    }
                                }
                                if (totalQuantity >= quantity) {
                                    getService().endDlg(true);
                                    for (Item itm : items) {
                                        if (itm.has(quantity)) {
                                            removeItem(itm.index, quantity, true);
                                        } else {
                                            quantity -= itm.getQuantity();
                                            removeItem(itm.index, itm.getQuantity(), true);
                                        }
                                        if (quantity <= 0) {
                                            break;
                                        }
                                    }
                                    addGold(-fee);
                                    int rd = (int)NinjaUtils.nextInt(100);
                                    if (rd < percent) {
                                        option.param++;
                                        for (ItemOption option1 : item.options) {
                                            if (option1.optionTemplate.type != 8 || option1.optionTemplate.id == 85) {
                                                continue;
                                            }
                                            int add = option1.param / 10;
                                            if (add == 0) {
                                                add = 1;
                                            }
                                            option1.param += add;
                                        }
                                        getService().itemInfo(item, (byte) 3, (byte) indexUI);
                                        serverMessage("Vật phẩm đã được tinh luyện thành công");
                                    } else {
                                        serverMessage("Tinh luyện thất bại");
                                    }
                                } else {
                                    serverDialog("Không đủ thạch.");
                                }
                            } else {
                                int[] percents = new int[]{60, 45, 34, 26, 20, 15, 11, 8, 6};
                                int[] yens = new int[]{150000, 247500, 408375, 673819, 1111801, 2056832, 4010822,
                                    7420021, 12243035};
                                byte[] quantitys = new byte[]{3, 5, 9, 4, 7, 10, 5, 7, 9};
                                int id = (optionParam >= 3) ? ((optionParam >= 6) ? 457 : 456) : 455;
                                Vector<Item> items = new Vector<>();
                                boolean[] list = new boolean[numberCellBag];
                                while (ms.reader().available() > 0) {
                                    int index = ms.reader().readUnsignedByte();
                                    if (index < 0 || index >= numberCellBag) {
                                        continue;
                                    }
                                    if (!list[index]) {
                                        list[index] = true;
                                        Item itm = this.bag[index];
                                        if (itm != null) {
                                            if (itm.id == id) {
                                                items.add(itm);
                                            }
                                        }
                                    }
                                }
                                int quantity = quantitys[optionParam];
                                if (items.size() < quantity) {
                                    ItemTemplate template = ItemManager.getInstance().getItemTemplate(id);
                                    serverDialog(
                                            "Cần phải có đủ " + quantity + " " + template.name
                                            + " để tinh luyện trang bị");
                                    return;
                                }
                                int coin = yens[optionParam];
                                if ((long) coin > (long) (((long) this.coin) + ((long) this.yen))) {
                                    serverDialog(language.getString("NOT_ENOUGH_MONEY"));
                                    return;
                                }
                                getService().endDlg(true);
                                int rand = (int)NinjaUtils.nextInt(100);
                                if (rand < percents[optionParam]) {
                                    for (ItemOption option1 : item.options) {
                                        if (option1.optionTemplate.type != 8 || option1.optionTemplate.id == 85) {
                                            continue;
                                        }
                                        if (option1.optionTemplate.id == 94) {
                                            int[] percentIncreases = new int[]{10, 10, 10, 20, 20, 30, 40, 50, 60};
                                            option1.param += percentIncreases[optionParam];
                                        } else if (option1.optionTemplate.id == 86) {
                                            int[] percentIncreases = new int[]{25, 30, 35, 40, 50, 60, 80, 115, 165};
                                            option1.param += percentIncreases[optionParam];
                                        } else if (option1.optionTemplate.id == 87) {
                                            int[] percentIncreases = new int[]{50, 60, 70, 90, 130, 180, 250, 330,
                                                500};
                                            option1.param += percentIncreases[optionParam];
                                        } else if (option1.optionTemplate.id == 88 || option1.optionTemplate.id == 89
                                                || option1.optionTemplate.id == 90) {
                                            int[] percentIncreases = new int[]{50, 70, 100, 140, 190, 250, 320, 400,
                                                500};
                                            option1.param += percentIncreases[optionParam];
                                        } else if (option1.optionTemplate.id == 92) {
                                            int[] percentIncreases = new int[]{5, 5, 5, 5, 5, 5, 10, 10, 20};
                                            option1.param += percentIncreases[optionParam];
                                        } else if (option1.optionTemplate.id == 95 || option1.optionTemplate.id == 96
                                                || option1.optionTemplate.id == 97) {
                                            int[] percentIncreases = new int[]{5, 5, 5, 5, 5, 5, 10, 10, 15};
                                            option1.param += percentIncreases[optionParam];
                                        } else if (option1.optionTemplate.id == 82 || option1.optionTemplate.id == 83) {
                                            int[] percentIncreases = new int[]{40, 60, 80, 100, 140, 220, 300, 420,
                                                590};
                                            option1.param += percentIncreases[optionParam];
                                        } else if (option1.optionTemplate.id == 84) {
                                            int[] percentIncreases = new int[]{25, 30, 35, 40, 50, 60, 80, 115, 165};
                                            option1.param += percentIncreases[optionParam];
                                        } else if (option1.optionTemplate.id == 79) {
                                            int[] percentIncreases = new int[]{1, 2, 2, 2, 2, 2, 3, 3, 4};
                                            option1.param += percentIncreases[optionParam];
                                        } else if (option1.optionTemplate.id == 81) {
                                            int[] percentIncreases = new int[]{1, 2, 2, 2, 2, 2, 3, 3, 4};
                                            option1.param += percentIncreases[optionParam];
                                        } else if (option1.optionTemplate.id == 80) {
                                            int[] percentIncreases = new int[]{5, 5, 5, 5, 10, 10, 15, 15, 20};
                                            option1.param += percentIncreases[optionParam];
                                        } else if (option1.optionTemplate.id == 91) {
                                            int[] percentIncreases = new int[]{5, 5, 5, 5, 5, 5, 10, 10, 15};
                                            option1.param += percentIncreases[optionParam];
                                        }

                                    }
                                    option.param++;
                                    getService().itemInfo(item, (byte) 3, (byte) indexUI);
                                    serverMessage("Vật phẩm đã được tinh luyện thành công");
                                } else {
                                    serverMessage("Tinh luyện thất bại");
                                }
                                if (coin > this.yen) {
                                    addCoin(-(coin - this.yen));
                                    addYen(-this.yen);
                                } else {
                                    addYen(-coin);
                                }
                                for (int j = 0; j < quantity; j++) {
                                    removeItem(items.get(j).index, 1, true);
                                }
                            }
                            return;
                        }
                    }
                    serverDialog("Vui lòng dịch chuyển trang bị trước");
                } else {
                    serverDialog("Vật phẩm này không thể tinh luyện");
                }
            }
        } catch (IOException ex) {
            Log.error("tinh luyen err: " + ex.getMessage(), ex);
        } finally {
            lockItem.unlock();
        }
    }

    public void luyenThach(Message ms) {
        lockItem.lock();
        try {
            if (!isHuman) {
                warningClone();
                return;
            }
            if (trade != null) {
                warningTrade();
                return;
            }
            Vector<Item> items = new Vector<>();
            boolean[] list = new boolean[numberCellBag];
            while (ms.reader().available() > 0) {
                int index = ms.reader().readUnsignedByte();
                if (index < 0 || index >= numberCellBag) {
                    continue;
                }
                if (!list[index]) {
                    list[index] = true;
                    Item item = this.bag[index];
                    if (item != null && (item.id == 10 || item.id == 11 || item.id == 455 || item.id == 456)) {
                        if (item.id == 10 || item.id == 11) {
                            items.add(0, item);
                        } else {
                            items.add(item);
                        }
                    }
                }
            }
            if (items.isEmpty()) {
                return;
            }
            Item item = items.get(0);
            if (item.id == 10 || item.id == 455) {
                for (int i = 1; i < items.size(); i++) {
                    Item item2 = items.get(i);
                    if (item2.id != 455) {
                        items.remove(item2);
                    }
                }
            } else if (item.id == 11 || item.id == 456) {
                for (int i = 1; i < items.size(); i++) {
                    Item item2 = items.get(i);
                    if (item2.id != 456) {
                        items.remove(item2);
                    }
                }
            }
            if (items.size() == 4 || items.size() == 9) {
                if (item.id == 10) {
                    if (getSlotNull() < 1) {
                        warningBagFull();
                        return;
                    }
                    for (int i = 0; i < 1; i++) {
                        Item itm = ItemFactory.getInstance().newItem(456);
                        itm.isLock = true;
                        addItemToBag(itm);
                    }
                }
                if (item.id == 455) {
                    if (getSlotNull() == 0) {
                        warningBagFull();
                        return;
                    }
                    Item itm = ItemFactory.getInstance().newItem(456);
                    itm.isLock = false;
                    addItemToBag(itm);
                }
                if (item.id == 11) {
                    if (getSlotNull() < 1) {
                        warningBagFull();
                        return;
                    }
                    for (int i = 0; i < 1; i++) {
                        Item itm = ItemFactory.getInstance().newItem(457);
                        itm.isLock = true;
                        addItemToBag(itm);
                    }
                }
                if (item.id == 456) {
                    if (getSlotNull() == 0) {
                        warningBagFull();
                        return;
                    }
                    Item itm = ItemFactory.getInstance().newItem(457);
                    itm.isLock = false;
                    addItemToBag(itm);
                }
                getService().endDlg(true);
                for (Item item2 : items) {
                    removeItem(item2.index, 1, true);
                }
            } else {
                serverDialog(
                        "Cần kết hợp 9 tử tinh thạch cùng loại hoặc 3 Tử tinh thạch (trung) và đá 12 hoặc 3 tử tinh thạch (sơ) đá 11");
            }
        } catch (IOException ex) {
            Log.error("luyen thach err: " + ex.getMessage(), ex);
        } finally {
            lockItem.unlock();
        }
    }

    public void upgradeItem(Message ms) {
        lockItem.lock();
        try {
            if (trade != null) {
                warningTrade();
                return;
            }
            if (!isHuman) {
                warningClone();
                return;
            }
            boolean isGold = ms.reader().readBoolean();
            int equipIndex = ms.reader().readUnsignedByte();
            Item item = bag[equipIndex];
            if (item == null) {
                return;
            }
            if ((!item.template.isTypeClothe() && !item.template.isTypeAdorn() && !item.template.isTypeWeapon())
                    || item.template.fashion > -1) {
                serverDialog(language.getString("BODY_NOTUPGRADE"));
                return;
            }
            if (item.upgrade >= item.template.getUpMax()) {
                serverDialog(language.getString("BODY_MAXUPGRADE"));
                return;
            }
            int numberBaoHiem = 0;
            int numberCrystal = 0;
            Vector<Item> crystals = new Vector<>();
            boolean[] list = new boolean[numberCellBag];
            while (ms.reader().available() > 0) {
                byte index = ms.reader().readByte();
                if (index < 0 || index >= numberCellBag) {
                    continue;
                }
                if (!list[index]) {
                    list[index] = true;
                    if (bag[index] != null && (bag[index].id <= 11 || bag[index].template.type == 28)
                            && bag[index].getQuantity() == 1) {
                        if (bag[index].template.isTypeCrystal()) {
                            numberCrystal++;
                        } else if (bag[index].template.type == 28) {
                            numberBaoHiem++;
                        }
                        crystals.add(bag[index]);
                    }
                }
            }
            if (crystals.size() > 18) {
                serverDialog(language.getString("CRYSTAL_FULL"));
                return;
            }
            if (numberBaoHiem > 1) {
                serverDialog("Chỉ được sử dụng một bảo hiểm!");
                return;
            }
            if (numberCrystal == 0) {
                serverDialog("Hãy chọn đá nâng cấp!");
                return;
            }
            long temp = 0;
            int percent = 0;
            int coin = 0;
            int gold = 0;
            for (int i = 0; i < crystals.size(); i++) {
                Item itm = crystals.get(i);
                if (itm != null && (int) itm.template.type == 26) {
                    temp += GameData.UP_CRYSTAL[itm.id];
                }
            }
            if (item.template.isTypeClothe()) {
                percent = (int) (temp * 100 / GameData.UP_CLOTHE[item.upgrade]);
                coin = GameData.COIN_UP_CLOTHE[item.upgrade];
            }
            if (item.template.isTypeAdorn()) {
                percent = (int) (temp * 100 / GameData.UP_ADORN[item.upgrade]);
                coin = GameData.COIN_UP_ADORN[item.upgrade];
            }
            if (item.template.isTypeWeapon()) {
                percent = (int) (temp * 100 / GameData.UP_WEAPON[item.upgrade]);
                coin = GameData.COIN_UP_WEAPON[item.upgrade];
            }
            int maxPercent = GameData.MAX_PERCENT[item.upgrade] - 5;
            maxPercent += maxPercent * Config.getInstance().getMaxPercentAdd();
            if (percent > maxPercent) {
                percent = maxPercent;
            }
            if (isGold) {
                percent = (int) ((double) percent * 1.5);
                gold = GameData.GOLD_UP[item.upgrade];
            }
            if (coin > NinjaUtils.sum(this.coin, this.yen)) {
                serverDialog(language.getString("NOT_ENOUGH_COIN_UPGRADE"));
                return;
            }
            if (isGold && user.gold < gold) {
                serverDialog(language.getString("NOT_ENOUGH_GOLD_UPGRADE"));
                return;
            }
            if (isGold) {
                addGold(-gold);
            }
            if (coin > this.yen) {
                addCoin(-(coin - this.yen));
                addYen(-this.yen);
            } else {
                addYen(-coin);
            }
            boolean isBaoHiem = false;
            for (int i = 0; i < crystals.size(); i++) {
                Item itm = crystals.get(i);
                if (itm != null && (itm.template.type == 26 || itm.template.type == 28)) {
                    if (!isBaoHiem) {
                        if (itm.id == 242 && item.upgrade < 8) {
                            isBaoHiem = true;
                        } else if (itm.id == 284 && item.upgrade < 12) {
                            isBaoHiem = true;
                        } else if (itm.id == 285 && item.upgrade < 14) {
                            isBaoHiem = true;
                        } else if (itm.id == 475 && item.upgrade < 16) {
                            isBaoHiem = true;
                        }
                    }
                    removeItem(itm.index, itm.getQuantity(), false);
                }
            }
            byte type = 1;
            int rand = (int)NinjaUtils.nextInt(100);
            int up1 = item.upgrade;
            int up2 = item.upgrade;
            if (rand < percent) {
                type = 1;
                up2 += 1;
                if (up2 == 8 && this.gloryTask != null) {
                    this.gloryTask.updateProgress(1);
                }
                if (Event.isEvent() && rand < 30) {
                    int itemID = Event.getEvent().randomItemID();
                    if (itemID != -1) {
                        Item itemE = ItemFactory.getInstance().newItem(itemID);
                        itemE.setQuantity((int)NinjaUtils.nextInt(1, 5));
                        addItemToBag(itemE);
                    }
                }
            } else {
                type = 0;
                if (!isBaoHiem && item.upgrade > 4) {
                    if (item.upgrade >= 14) {
                        up2 = 14;
                    } else if (item.upgrade >= 12) {
                        up2 = 12;
                    } else {
                        up2 = (byte) (item.upgrade / 4 * 4);
                    }
                }
            }
            item.isLock = true;
            item.next(up2 - up1);
            getService().upgrade(type, item);
        } catch (Exception ex) {
            Log.error("upgrade item err: " + ex.getMessage(), ex);
        } finally {
            lockItem.unlock();
        }
    }

    public void luckyDrawRefresh(Message ms) {
        if (!isHuman) {
            return;
        }
        byte typeLuck = typeVXMM;
        LuckyDraw lucky = LuckyDrawManager.getInstance().find(typeLuck);
        if (lucky != null) {
            lucky.show(this);
        }
    }

    public void moveTo(String name) {
        if (isDead) {
            return;
        }
        if (name.length() < 5) {
            return;
        }
        if (!isHuman) {
            warningClone();
            return;
        }
        if (zone.tilemap.isNotTeleport()) {
            serverMessage("Không thể sử dụng tính năng này.");
            return;
        }

        int index = getIndexItemByIdInBag(279);
        if (index == -1 && this.level < 10) {
            serverMessage("Để sử dụng chức năng này bạn phải có Vạn biến lệnh.");
            return;
        }
        if (name.equals(this.name)) {
            serverMessage("Không thể dịch chuyển đến chính mình.");
            return;
        }
        long now = System.currentTimeMillis();
        int second = (int) ((now - lastTimeTeleport) / 1000);
        if (second < 20) {
            serverMessage("Chỉ có thể sử dụng sau " + (20 - second) + " giây");
            return;
        }
        Char _char = Char.findCharByName(name);

        if (_char != null) {
            if (!_char.isHuman) {
                serverMessage("Người này đang trong chế độ thứ thân không thể dùng được chức năng này.");
                return;
            }
            if (_char.zone.tilemap.isNotTeleport()) {
                serverMessage("Không thể tới đây.");
                return;
            }
            try {
                if (_char.mapId == 0 || _char.mapId > 72) {
                    serverMessage("Không thể tới khu vực này.");
                    return;
                }
                if (_char.zone.getNumberChar() >= 24) {
                    serverMessage("Khu vực đã đầy!");
                    return;
                }
                setXY(_char.x, _char.y);
                if (_char.zone != this.zone) {
                    outZone();
                    joinZone(_char.mapId, _char.zone.id, -1);
                } else {
                    getService().endDlg(true);
                }
                lastTimeTeleport = now;
                zone.getService().teleport(this);
                getService().resetPoint();
            } catch (Exception ex) {
                Log.error("err: " + ex.getMessage(), ex);
            }
        } else {
            serverMessage("Hiện tại người này không online hoặc không tồn tại.");
        }
    }

    public void input(Message ms) {
        try {
            if (!isHuman) {
                warningClone();
                return;
            }
            if (trade != null) {
                warningTrade();
                return;
            }

            short inputID = ms.reader().readShort();

            Config serverConfig = Config.getInstance();

            if (inputID == CMDInputDialog.BET_LUCKYDRAW) {
                if (!serverConfig.isOpenVxmm()) {
                    serverMessage("Tính năng này đang bảo trì");
                    return;
                }
                if (this.user.kh == 0) {
                    serverDialog("Vui lòng kích hoạt tài khoản trước");
                    return;
                }
                betMessage(ms);
            } else if (inputID == CMDInputDialog.REFRESH_LUCKYDRAW) {
                luckyDrawRefresh(ms);
            } else if (inputID == CMDInputDialog.MENU_LUCKYDRAW) {
                menus.clear();
                menus.add(new Menu(CMDMenu.VXMM_1, "Vòng xoay vip"));
                menus.add(new Menu(CMDMenu.VXMM_2, "Vòng xoay thường"));
                getService().openUIMenu();
            } else {
                InputDialog input = getInput();

                if (input == null && inputID != 1) {
                    return;
                }
                try {
//                    System.out.println("INput Id: "+ inputID);
                    String charName = ms.reader().readUTF();
                    if (inputID == 1 && charName != null) {
                        input = new InputDialog(inputID, "Di chuyển tới");
                    }
                    if (inputID != input.getId()) {
                        return;
                    }
                    input.setText(charName);
                    ms.reader().reset();

                    int itemID = -1;
                    switch (input.getId()) {
                        case CMDInputDialog.EXECUTE:
                            input.execute();
                            break;
                        case CMDInputDialog.TELEPORT:
                            moveTo(input.getText());
                            break;
                        case CMDInputDialog.VXMMNORMAL:
                            if (this.user.kh == 0) {
                                serverDialog("Vui lòng kích hoạt tài khoản trước");
                                return;
                            }
                            bet(input.getText(), 0);
                            break;
                        case CMDInputDialog.VXMMVIP:

                            if (this.user.kh == 0) {
                                serverDialog("Vui lòng kích hoạt tài khoản trước");
                                return;
                            }
                            bet(input.getText(), 1);
                            break;
                        case CMDInputDialog.THACH_DAU:
                            inviteArena(input.getText());
                            break;
                        case CMDInputDialog.INVITE_WAR_CLAN:
                            inviteArenaWarClan(input.getText());
                            break;
                        case CMDInputDialog.DAT_CUOC:
                            betArena(input.getText());
                            break;

                        case CMDInputDialog.TAO_GIA_TOC:
                            createClan(input.getText().trim());
                            break;

                        case CMDInputDialog.MA_QUA_TANG:
                            useGiftCode(input.getText().trim());
                            break;
                    }

                } finally {
                    setInput(null);
                }
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void betArena(String content) {
        Arena arena = (Arena) findWorld(World.ARENA);
        if (arena == null) {
            return;
        }
        if (this.group != null && !this.group.isCheckLeader(this)) {
            return;
        }
        int coin = 0;
        try {
            coin = Integer.parseInt(content);
        } catch (Exception e) {
            getService().npcChat(NpcName.KANATA, "Số tiền cược không hợp lệ.");
            return;
        }
        if (coin < 1000) {
            getService().npcChat(NpcName.KANATA, "Tối thiểu phải là 1.000xu.");
            return;
        }
        if (coin > 1000000) {
            getService().npcChat(NpcName.KANATA, "Tối đa là 1.000.000xu.");
            return;
        }
        if (coin > this.coin) {
            serverDialog(language.getString("NOT_ENOUGH_MONEY"));
            return;
        }
        int team = arena.getTeam(this);
        arena.setMoney(team, coin);
    }

    public void inviteArena(String name) {
        if (!Config.getInstance().isArena()) {
            serverDialog("Tính năng này đang được bảo trì, vui lòng quay lại sau.");
            return;
        }
        if (name.equals(this.name)) {
            getService().npcChat(NpcName.KANATA, "Không thể thách đấu bản thân.");
            return;
        }
        Char _char = zone.findCharName(name);
        if (_char != null) {
            try {
                if (this.group != null) {
                    if (!this.group.isCheckLeader(this)) {
                        getService().npcChat(NpcName.KANATA, "Chỉ có nhóm trưởng mới có thể mời thách đấu.");
                        return;
                    }
                    int index = this.group.getIndexById(_char.id);
                    if (index != -1) {
                        getService().npcChat(NpcName.KANATA, "Không thể mời người cùng nhóm.");
                        return;
                    }
                }
                if (_char.group != null) {
                    if (!_char.group.isCheckLeader(_char)) {
                        getService().npcChat(NpcName.KANATA, "Đối phương không phải nhóm trưởng.");
                        return;
                    }
                }
                PlayerInvite p = _char.invite.findCharInvite(Invite.PK, this.id);
                if (p != null) {
                    serverDialog("Không thể mời thách đấu liên tục. Vui lòng thử lại sau 30s nữa.");
                    return;
                }
                _char.getService().testDungeonInvite(this.id);
                _char.invite.addCharInvite(Invite.PK, this.id, 30);
                getService().npcChat(NpcName.KANATA, "Ta đã gởi lời mời thách đấu đến " + name);
            } catch (Exception e) {
                Log.error("err: " + e.getMessage(), e);
            }
        } else {
            getService().npcChat(NpcName.KANATA, "Hiện tại " + name + " không có mặt ở đây.");
        }
    }

    public void inviteArenaWarClan(String name) {
        if (!Config.getInstance().isArena()) {
            serverDialog("Tính năng này đang được bảo trì, vui lòng quay lại sau.");
            return;
        }
         if (this.clan == null) {
             serverDialog("kẻ không bang không phái làm sao có quyền lên tiếng"); 
                return;
            }
        Member member = this.clan.getMemberByName(this.name);
        if (member != null) {
            int typeClan = member.getType();
            if (typeClan != Clan.TYPE_TOCTRUONG && typeClan != Clan.TYPE_TOCPHO) {
            if (typeClan != Clan.TYPE_TOCTRUONG) {
                serverDialog("Bạn không phải tộc trưởng");
                return;
            }
            }
        }
        if (name.equals(this.clan.name)) {
            getService().npcChat(NpcName.KAGAI, "Không thể thách đấu bản thân.");
            return;
        }
        Clan clan2 = Clan.mapClan.get(name);
        if (clan2 == null) {
            getService().npcChat(NpcName.KAGAI, "Gia tộc này không tồn tại");
            return;
        }
        Char _char = zone.findCharName(clan2.main_name);
        if (_char != null) {
            try {

                PlayerInvite p = _char.invite.findCharInvite(Invite.GIA_TOC_CHIEN, this.id);
                if (p != null) {
                    serverDialog("Không thể mời thách đấu liên tục. Vui lòng thử lại sau 30s nữa.");
                    return;
                }
                _char.getService().testWarClanInvite(this.id);
                _char.invite.addCharInvite(Invite.GIA_TOC_CHIEN, this.id, 30);
                getService().npcChat(NpcName.KAGAI, "Ta đã gởi lời mời thách đấu đến gia tộc" + name);
            } catch (Exception e) {
                Log.error("err: " + e.getMessage(), e);
            }
        } else {
            getService().npcChat(NpcName.KAGAI, "Hiện tại tộc trưởng gia tộc " + name + " không có mặt ở đây.");
        }
    }

    public void inviteTerritory(Message ms) {
        try {
            Territory territory = (Territory) findWorld(World.TERRITORY);
            if (territory == null) {
                return;
            }
            if (!isHuman) {
                return;
            }
            String charName = ms.reader().readUTF();
            Member member = this.clan.getMemberByName(charName);
            if (member != null && member.isOnline()) {
                if (!territory.started) {
                    territory.addGuest(member.getChar().id);
                    member.getChar().serverMessage(
                            "Bạn được " + this.name + " mời bạn vào lãnh địa gia tộc, hãy gặp NPC Kanata để điểm danh");
                } else {
                    serverDialog("Lãnh địa đã bắt đầu, không thể mời thêm.");
                }
            } else {
                serverDialog("Thành viên không hoạt động.");
            }
        } catch (Exception e) {
            Log.error("err: " + e.getMessage(), e);
        }
    }

    public void clanUseItem(Message ms) {
        try {
            if (this.clan == null) {
                return;
            }
            if (!isHuman) {
                return;
            }
            byte indexUI = ms.reader().readByte();
            if (indexUI < 0 || indexUI >= this.clan.getItems().length) {
                return;
            }
            Item item = this.clan.getItems()[indexUI];
            if (item == null) {
                return;
            }

            if (item.template.id == ItemName.LENH_BAI_GIA_TOC) {
                if (this.clan != null) {
                    if (this.clan.getOpenDun() == 0) {
                        if (this.clan.getUseCard() == 0) {
                            serverMessage("Đã hết lượt sử dụng.");
                            return;
                        }
                        Territory territory = (Territory) findWorld(World.TERRITORY);
                        if (territory != null && !territory.isClosed()) {
                            serverMessage("Lãnh địa hiện tại vẫn chưa kết thúc.");
                            return;
                        }
                        removeWorld(World.TERRITORY);
                        this.clan.openDun++;
                        this.clan.use_card--;
                        serverMessage("Gia tộc nhận được thêm 1 lần lãnh địa gia tộc.");
                        this.clan.removeItem(item, 1);
                        clan.getClanService().requestClanItem();
                    } else {
                        serverMessage("Gia tộc vẫn còn lượt vào lãnh địa.");
                    }
                }
            }

        } catch (Exception e) {
            Log.error("err: " + e.getMessage(), e);
        }
    }

    public void acceptInviteWarClan(Message ms) {
        try {
            if (!isHuman) {
                return;
            }
            int charId = ms.reader().readInt();
            if (invite.findCharInvite(Invite.GIA_TOC_CHIEN, charId) != null) {
                Char _char = zone.findCharById(charId);

                WarClan.initWarClan(this.clan, _char.clan);
                joinBattleClan(0, _char);
                joinBattleClan(1, this);
            } else {
                serverDialog("Đã hết thời gian để chấp nhận yêu cầu.");
            }
        } catch (Exception e) {
            Log.error("err: " + e.getMessage(), e);
        }
    }

    public void acceptInviteTestDun(Message ms) {
        try {
            if (!isHuman) {
                return;
            }
            int charId = ms.reader().readInt();
            if (invite.findCharInvite(Invite.PK, charId) != null) {
                Char _char = zone.findCharById(charId);
                if (_char != null) {
                    int mapBeforeEnterPB = this.mapId;
                    Arena arena = new Arena();
                    if (this.group != null) {
                        List<Char> chars = this.group.getChars();
                        for (Char _char2 : chars) {
                            _char2.mapBeforeEnterPB = mapBeforeEnterPB;
                            _char2.outZone();
                            _char2.addWorld(arena);
                            _char2.joinZone(-1, -1, 1);
                        }
                    } else {
                        this.mapBeforeEnterPB = mapBeforeEnterPB;
                        outZone();
                        this.addWorld(arena);
                        joinZone(-1, -1, 1);
                    }
                    if (_char.group != null) {
                        List<Char> chars = _char.group.getChars();
                        for (Char _char2 : chars) {
                            _char2.mapBeforeEnterPB = mapBeforeEnterPB;
                            _char2.outZone();
                            _char2.addWorld(arena);
                            _char2.joinZone(-1, -1, 2);
                        }
                    } else {
                        _char.mapBeforeEnterPB = mapBeforeEnterPB;
                        _char.outZone();
                        _char.addWorld(arena);
                        _char.joinZone(-1, -1, 2);
                    }
                    arena.isTwoTeamsEtered = true;
                } else {
                    serverDialog("Hiện tại người này không có mặt ở đây.");
                }
            } else {
                serverDialog("Đã hết thời gian để chấp nhận yêu cầu.");
            }
        } catch (Exception e) {
            Log.error("err: " + e.getMessage(), e);
        }
    }

    public void requestMatchInfo(Message ms) {
        try {
            if (!isSchool()) {
                return;
            }
            if (!isHuman) {
                return;
            }
            byte id = ms.reader().readByte();
            Arena arena = Arena.getArenaByID(id);
            if (arena != null) {
                if (arena.isOpened) {
                    this.mapBeforeEnterPB = this.mapId;
                    if ((int)NinjaUtils.nextInt(2) == 0) {
                        setXY((short) 130, (short) 360);
                    } else {
                        setXY((short) 635, (short) 360);
                    }
                    outZone();
                    addWorld(arena);
                    joinZone(-1, -1, 3);
                } else {
                    serverDialog("Trận đấu chưa bắt đầu.");
                }
            } else {
                serverDialog("Không tìm thấy trận đấu này.");
            }
        } catch (Exception e) {
            Log.error("err: " + e.getMessage(), e);
        }
    }

    public void useGiftCode(String code) {
        GiftCode.getInstance().use(this, code);
    }

    public void createClan(String name) {
        if (this.clan == null) {
            try {
                if (user.gold < 100000) {
                    getService().npcChat(NpcName.KANATA, "Bạn không đủ 100k lương.");
                    return;
                }
                if (this.level < 40) {
                    getService().npcChat(NpcName.KANATA, "Yêu cầu trình độ đạt cấp 40.");
                    return;
                }
                if (name.equals("")) {
                    getService().npcChat(NpcName.KANATA, "Tên không hợp lệ.");
                    return;
                }
                if (name.length() > 10) {
                    getService().npcChat(NpcName.KANATA, "Con hãy chọn cái tên ngắn hơn.");
                    return;
                }
                Pattern p = Pattern.compile("^[a-z0-9]+$");
                Matcher m1 = p.matcher(name);
                if (!m1.find()) {
                    getService().npcChat(NpcName.KANATA, "Tên tài khoản có kí tự lạ.");
                    return;
                }
                ClanDAO clanDAO = Clan.getClanDAO();
                if (clanDAO.checkExist(name)) {
                    getService().npcChat(NpcName.KANATA, "Tên gia tộc đã tồn tại.");
                    return;
                }
                addGold(-100000);
                Clan clan = new Clan();
                clan.setName(name);
                clan.setAssistName("");
                clan.setMainName(this.name);
                clan.setAlert("");
                clan.setLevel((byte) 1);
                clan.setCoin(0);
                clan.setItemLevel((byte) 0);
                clan.setExp(0);
                clan.setRegDate(new Date());
                clan.setOpenDun((byte) 1);
                clan.setUseCard(1);
                clan.writeLog(clan.getAssistName(), Clan.CREATE_CLAN, clan.getCoin());
                this.clan = clan;
                Clan.getClanDAO().save(clan);
                Member mem = Member.builder()
                        .classId(this.classId)
                        .level(this.level)
                        .type(Clan.TYPE_TOCTRUONG)
                        .name(this.name)
                        .pointClan(0)
                        .pointClanWeek(0)
                        .build();
                mem.setChar(this);
                mem.setOnline(true);
                clan.memberDAO.save(mem);

                getService().createClan();
                zone.getService().acceptInviteClan(this);

                getService().npcChat(NpcName.KANATA, "Gia tộc của con đã được thành lập.");
            } catch (Exception ex) {
                Log.error("create clan err: " + ex.getMessage(), ex);
                serverDialog(ex.getMessage());
            }
        } else {
            getService().npcChat(NpcName.KANATA, "Con đang ở trong 1 gia tộc nào đó.");
        }
    }

    public void goldUnpaid() {
        if (isUnpaid) {
            return;
        }
        isUnpaid = true;
        try {
            PreparedStatement stmt = DbManager.getInstance().getConnection(DbManager.GAME).prepareStatement(
                    "SELECT `amount_unpaid` FROM `users` WHERE `id` = ? LIMIT 1;", ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            stmt.setInt(1, user.id);
            ResultSet res = stmt.executeQuery();
            try {
                if (res.first()) {
                    int amount = res.getInt("amount_unpaid");
                    if (amount == 0) {
                        isUnpaid = false;
                        return;
                    } else {
                        addGold(amount);
                    }
                }
            } finally {
                res.close();
                stmt.close();
            }
            if (isUnpaid) {
                DbManager.getInstance().updateAmountUnpaid(user.id, 0);
                isUnpaid = false;
            }
        } catch (Exception e) {
            Log.error("err: " + e.getMessage(), e);
        }
    }

    public void giftcodeUnpaid() {
        if (isGiftCodeUnpaid) {
            return;
        }
        isGiftCodeUnpaid = true;
        try {
            PreparedStatement stmt = DbManager.getInstance().getConnection(DbManager.GAME).prepareStatement(
                    "SELECT `giftcode_unpaid` FROM `players` WHERE `id` = ? LIMIT 1;", ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            stmt.setInt(1, this.id);
            ResultSet res = stmt.executeQuery();
            try {
                if (res.first()) {
                    String giftCodeString = res.getString("giftcode_unpaid");
                    if (giftCodeString != null && !giftCodeString.equals("")) {
                        useGiftCode(giftCodeString);
                    }
                }
            } finally {
                res.close();
                stmt.close();
            }
            if (isGiftCodeUnpaid) {
                PreparedStatement stmt2 = DbManager.getInstance().getConnection(DbManager.SAVE_DATA)
                        .prepareStatement("UPDATE `players` SET `giftcode_unpaid` = '' WHERE `id` = ? LIMIT 1;");
                try {
                    stmt2.setInt(1, this.id);
                    stmt2.executeUpdate();
                } finally {
                    stmt2.close();
                }
                isGiftCodeUnpaid = false;
            }
        } catch (Exception e) {
            Log.error("err: " + e.getMessage(), e);
        }
    }

    public void bet(String money, int type) {
        if (!serverConfig.isOpenVxmm()) {
            serverMessage("Tính năng này đang bảo trì");
            return;
        }
        try {
            if (money == null || money.equals("")) {
                return;
            }
            Pattern p = Pattern.compile("^[0-9]+$");
            Matcher m1 = p.matcher(money);
            double percentCoinInBag = ((float) this.coin) / this.coinMax;

            if (!m1.find() || money.length() > 8) {
                serverMessage("Số xu không hợp lệ!");
                return;
            } else if (percentCoinInBag >= 0.8) {
                serverDialog("Xu trong hành trang hiện đang quá lớn. Nếu bạn thắng bạn có thể sẽ không nhận đủ số xu");
            }
            switch (type) {
                case 0:
                    typeVXMM = LuckyDrawManager.NORMAL;
                    break;

                case 1:
                    typeVXMM = LuckyDrawManager.VIP;
                    break;
            }
            LuckyDraw lucky = LuckyDrawManager.getInstance().find(typeVXMM);
            lucky.show(this);
            lucky.join(this, Integer.parseInt(money));
            lucky.show(this);
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void kickOption(Equip equip, int maxKick) {
        int num = 0;
        if (equip != null && equip.options != null) {
            for (int i = 0; i < equip.options.size(); i++) {
                ItemOption itemOption = (ItemOption) equip.options.get(i);
                itemOption.active = 0;
                if (itemOption.optionTemplate.type == 2) {
                    if (num < maxKick) {
                        itemOption.active = 1;
                        num++;
                    }
                } else if (itemOption.optionTemplate.type == 3 && equip.upgrade >= 4) {
                    itemOption.active = 1;
                } else if (itemOption.optionTemplate.type == 4 && equip.upgrade >= 8) {
                    itemOption.active = 1;
                } else if (itemOption.optionTemplate.type == 5 && equip.upgrade >= 12) {
                    itemOption.active = 1;
                } else if (itemOption.optionTemplate.type == 6 && equip.upgrade >= 14) {
                    itemOption.active = 1;
                } else if (itemOption.optionTemplate.type == 7 && equip.upgrade >= 16) {
                    itemOption.active = 1;
                }
            }
        }
    }

    public void updateKickOption() {
        int num = 2;
        int num2 = 2;
        int num3 = 2;
        if (this.equipment[0] == null) {
            num--;
        }
        if (this.equipment[6] == null) {
            num--;
        }
        if (this.equipment[5] == null) {
            num--;
        }
        kickOption(this.equipment[0], num);
        kickOption(this.equipment[6], num);
        kickOption(this.equipment[5], num);
        if (this.equipment[2] == null) {
            num2--;
        }
        if (this.equipment[8] == null) {
            num2--;
        }
        if (this.equipment[7] == null) {
            num2--;
        }
        kickOption(this.equipment[2], num2);
        kickOption(this.equipment[8], num2);
        kickOption(this.equipment[7], num2);
        if (this.equipment[4] == null) {
            num3--;
        }
        if (this.equipment[3] == null) {
            num3--;
        }
        if (this.equipment[9] == null) {
            num3--;
        }
        if (this.equipment[1] != null) {
            if (this.equipment[1].sys == this.getSys()) {
                if (this.equipment[1].options != null) {
                    for (int i = 0; i < this.equipment[1].options.size(); i++) {
                        ItemOption itemOption = (ItemOption) this.equipment[1].options.get(i);
                        if (itemOption.optionTemplate.type == 2) {
                            itemOption.active = 1;
                        }
                    }
                }
            } else if (this.equipment[1].options != null) {
                for (int j = 0; j < this.equipment[1].options.size(); j++) {
                    ItemOption itemOption2 = (ItemOption) this.equipment[1].options.get(j);
                    if (itemOption2.optionTemplate.type == 2) {
                        itemOption2.active = 0;
                    }
                }
            }
        }
        kickOption(this.equipment[4], num3);
        kickOption(this.equipment[3], num3);
        kickOption(this.equipment[9], num3);
    }

    public void setAbility() {
        if (abilityStrategy != null) {
            abilityStrategy.setAbility(this);
            if (zone != null) {
                zone.getService().refreshHP(this);
            }
        }
    }

    public void inputNumberSplit(Message ms) {
        try {
            if (trade != null) {
                warningTrade();
                return;
            }
            byte indexItem = ms.reader().readByte();
            int numSplit = ms.reader().readInt();
            numSplit = numSplit > Config.getInstance().getMaxQuantity() ? Config.getInstance().getMaxQuantity()
                    : numSplit;
            if (numSplit < 1) {
                return;
            }
            if (bag[indexItem] != null && bag[indexItem].template.isUpToUp) {
                int quantity = bag[indexItem].getQuantity();
                if (numSplit >= quantity) {
                    return;
                }
                for (int i = 0; i < this.numberCellBag; i++) {
                    if (bag[i] == null) {
                        bag[i] = Converter.getInstance().newItem(bag[indexItem]);
                        bag[i].index = i;
                        bag[i].setQuantity(numSplit);
                        getService().addItem(bag[i]);
                        bag[indexItem].reduce(numSplit);
                        getService().useItemUpToUp(indexItem, numSplit);
                        return;
                    }
                }
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void betMessage(Message ms) {
        try {
            String money = ms.reader().readUTF();
            byte typeLuck = ms.reader().readByte();
            if (money == null || money.equals("")) {
                return;
            }
            Pattern p = Pattern.compile("^[0-9]+$");
            Matcher m1 = p.matcher(money);
            if (!m1.find() || money.length() > 8) {
                serverMessage("Số xu không hợp lệ!");
                return;
            }
            LuckyDraw lucky = LuckyDrawManager.getInstance().find(typeLuck);
            lucky.join(this, Integer.parseInt(money));
            lucky.show(this);
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void selectSkill(Message ms) {
        try {
            short skillTemplateId = ms.reader().readShort();
            selectSkill(skillTemplateId);
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void selectSkill(short templateId) {
        for (Skill my : vSkillFight) {
            if (my.template.id == templateId) {
                selectedSkill = my;
                setAbility();
                return;
            }
        }
        for (Skill my : vSkillFight) {
            if (my.template.type == 1) {
                selectedSkill = my;
                setAbility();
                return;
            }
        }
    }

    public void openMenu(Message ms) {
        try {
            if (trade != null) {
                warningTrade();
                return;
            }
            short npcId = ms.reader().readShort();
            typeVXMM = -1;
            Npc npc = zone.getNpc(npcId);
            if (npc == null) {
                serverDialog("Không tìm thấy NPC này");
                return;
            }
            initMenu(npc.template.npcTemplateId);
            if (menus.size() > 0) {
                getService().openUIMenu();
            } else {
                getService().menu();
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void npcDeidara() {
        menus.add(new Menu(CMDMenu.EXECUTE, "Nói chuyện", () -> {
            String talk = (String) NinjaUtils.randomObject("Hãy cứu lấy ngôi làng này!!!",
                    "Nhiều báu vật của làng đã bị chúng cướp đi",
                    "Chúng thật mạnh, những dị bản nhẫn giả");
            getService().npcChat(NpcName.DEIDARA, talk);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Thánh địa Fujuka", () -> {
            if (countEnterFujukaSanctuary <= 0) {
                getService().npcChat(NpcName.DEIDARA, "Ngươi đã hết lượt tham gia.");
                return;
            }
            Map map = MapManager.getInstance().find(MapName.THANH_DIA_FUJUKA);
            FujukaSanctuary fs = new FujukaSanctuary(0, map, this);
            countEnterFujukaSanctuary--;
            mapBeforeEnterPB = mapId;
            outZone();
            setXY((short) 280, (short) 600);
            fs.join(this);
        }));
    }

    private void npcLongDen() {
        menus.add(new Menu(CMDMenu.EXECUTE, "Rước đèn", () -> {
            if (Event.isTrungThu()) {
                if (!isLeading()) {
                    if (level < 20) {
                        return;
                    }
                    int index = getIndexItemByIdInBag(ItemName.GIAY_THONG_HANH);
                    if (index == -1) {
                        serverDialog("Bạn không có giấy thông hành!");
                        return;
                    }
                    removeItem(index, 1, true);
                    Npc npc = zone.getNpc(NpcName.LONG_DEN_2);
                    if (npc != null) {
                        Bot bot = Bot.builder()
                                .id(-this.id)
                                .name(npc.template.name)
                                .level(0)
                                .typePk(PK_NORMAL)
                                .build();
                        bot.setDefault();
                        FashionCustom fashionCustom = FashionCustom.builder()
                                .head((short) npc.template.headId)
                                .body((short) npc.template.bodyId)
                                .leg((short) npc.template.legId)
                                .weapon((short) -1)
                                .build();
                        bot.setFashionStrategy(fashionCustom);
                        AbilityCustom abilityCustom = AbilityCustom.builder()
                                .hp(2000)
                                .build();
                        bot.setAbilityStrategy(abilityCustom);
                        bot.setAbility();
                        bot.setFashion();
                        bot.recovery();
                        bot.setXY((short) npc.cx, (short) npc.cy);
                        setEscortedEvent(bot);
                        getService().npcUpdate(npc.id, 15);
                        zone.join(bot);
                        setLeading(true);
                        eventPoint.addPoint(EventPoint.DIEM_TIEU_XAI, 1);
                    }
                }
            }
        }));
    }

    public void npcAdmin() {
        menus.add(new Menu(CMDMenu.EXECUTE, "Điểm danh mỗi ngày", () -> {          
            Date dateRollCall = NinjaUtils.getDate(user.lastAttendance);
            Date now = new Date();
            if (!DateUtils.isSameDay(now, dateRollCall)) {
                addYen(5000000);
                if (user.session.getCountAttendance() < 10) {
                addGold(500);                    
                } else {
                addGold(1000);
                }
                user.lastAttendance = now.getTime();
                user.session.addAttendance();
            } else {
                getService().npcChat(NpcName.ADMIN, "Con hãy chờ ngày tiếp theo để nhận quà tiếp.");
           
            }
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Nhận quà tân thủ", () -> {          
            if (!user.receivedFirstGift) {
             /*   Item x2 = ItemFactory.getInstance().newItem(385);
                x2.isLock = true;
                x2.setQuantity(1);
                x2.expire = System.currentTimeMillis() + 604800000L;
               
                addItemToBag(x2); 
            
                Item x3 = ItemFactory.getInstance().newItem(539);
                x3.isLock = true;
                x3.setQuantity(20);
                x3.expire = System.currentTimeMillis() + 604800000L;
                addItemToBag(x3); 
           
                Item x4 = ItemFactory.getInstance().newItem(540);
                x4.isLock = true;
                x4.setQuantity(20);
                x4.expire = System.currentTimeMillis() + 604800000L;
                addItemToBag(x4);   */
                addYen(100000000);
                addCoin(10000000);
                addGold(10000);
               // addExp(1999999999);
                user.receivedFirstGift = true;
            } else {
                getService().npcChat(NpcName.ADMIN, "Mỗi người chỉ nhận được 1 lần.");
              }
        }));
        String t = (notReceivedExp ? "Tắt" : "Bật");
        menus.add(new Menu(CMDMenu.EXECUTE, String.format("[%s]\nKhông nhận kinh nghiệm", t), () -> {
            this.notReceivedExp = !this.notReceivedExp;
            if (this.notReceivedExp) {
                serverDialog("Bật không nhận kinh nghiệm");
            } else {
                serverDialog("Tắt không nhận kinh nghiệm");
            }
        }));
       /* String y = (duatop ? "Tắt" : "Bật");
        menus.add(new Menu(CMDMenu.EXECUTE, String.format("[%s]\n đua top cao thủ", y), () -> {
            LocalDate fixedDate = LocalDate.of(2023, 8, 26);
            LocalDate currentDate = LocalDate.now();
            if (this.duatop) {
                serverDialog("Tắt đua top sự kiện\n bạn sẽ nhận xp bình thường \n nhưng sẽ không có trong danh sách xếp hạng đua top");
                this.duatop = false;
            } else {
                if (currentDate.isBefore(fixedDate) && this.duatop == false && !this.user.is1()) {

                    serverDialog("Bạn đã tắt đua top trước đó thì không thể bật lại cho đến khi sự kiện đua top kết thúc");
                } else {
                    serverDialog("Bạn đã đăng ký đua top cao thủ\n"
                            + "khi đua top cao thủ bạn sẽ không nhận được exp khi sử dụng vật phẩm sự kiện");
                    this.duatop = true;
                }

            }
        }));*/
       menus.add(new Menu(CMDMenu.EXECUTE, "Mã quà tặng", () -> {
            InputDialog input = new InputDialog(CMDInputDialog.MA_QUA_TANG, "Mã quà tặng");
            setInput(input);
            getService().showInputDialog();
        }));        
            
            menus.add(new Menu(CMDMenu.EXECUTE, "Hoàn thành nhiệm vụ nhanh (1000 lượng)", () -> {
                
                if(this.taskId >=42){
                     serverDialog("không Thể Hoàn Thành. Có Làm thì Mới Có Ăn!");
                     return;
                }
                if (user.gold >= 1000) {
                    addGold(-1000);
                    finishTask(true);
                    getService().npcChat(NpcName.ADMIN, "Ta đã hoàn thành giúp con rồi đó, hãy đi nhận nhiệm vụ tiếp theo đi!");
                } else {
                    serverDialog("Không đủ lượng!");
                }
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Hướng dẫn", () -> {
                String guide = "- Hoàn Thành Nhiệm Vụ" + "\n";
                guide += "  + Để hoàn thành nhiệm vụ cần nhận nhiệm vụ" + "\n";
                guide += "   và vào npcadmin ấn vào hoàn thành sẽ mất 1k lượng." + "\n";
                guide += "- Nhập Gifcode" + "\n";
                guide += "  + Ấn vào mã quà tặng ở npcadmin gifcode gồm có:" + "\n";
                guide += "   tanthu" + "\n";
                getService().showAlert("Hướng dẫn", guide);
            }));
        if (user.isAdmin()) {
            if (!user.isAdmin()) {
                return;
            }
            menus.add(new Menu(CMDMenu.EXECUTE, "Quản lý", () -> {
                AdminService.getInstance().openUIAdmin(this);
            }));
        }
    }

    public void removeAllItemTask() {
        for (int i = 0; i < this.numberCellBag; i++) {
            if (this.bag[i] != null && (this.bag[i].template.type == 24 || bag[i].template.type == 25)) {
                removeItem(i, this.bag[i].getQuantity(), true);
            }
        }
    }
public void removeItembag() {
        for (int i = 0; i < this.numberCellBag; i++) {
            if (this.bag[i] != null) {
                removeItem(i, this.bag[i].getQuantity(), true);
            }
        }
    }
    public void menu(Message ms) {
        try {
            if (trade != null) {
                warningTrade();
                return;
            }
            typeVXMM = -1;

            byte typeClose = 0;
            if (isVersionAbove(200)) {
                typeClose = ms.reader().readByte();
            }
            byte npcTemplateId = ms.reader().readByte();
            byte menuId = ms.reader().readByte();
            byte optionId = ms.reader().readByte();

            try {
                if (!isHuman) {
                    if (npcTemplateId != 7 && npcTemplateId != 8 && npcTemplateId != 9 && npcTemplateId != 10
                            && npcTemplateId != 11 && npcTemplateId != 12 && npcTemplateId != 13 && npcTemplateId != 5
                            && npcTemplateId != 39) {
                        getService().endWait("Bạn đang trong chế độ thứ thân không thể dùng được chức năng này.");
                        return;
                    }
                }
                if (npcTemplateId != 0) {
                    Npc npc = zone.getNpc(npcTemplateId);
                    if (npc == null) {
                        serverDialog("Không tìm thấy NPC này");
                        return;
                    }
                } else {
                    if (menus.isEmpty()) {
                        return;
                    }
                }
                if (menus.isEmpty()) {
                    if (npcTemplateId == NpcName.SHINWA) {
                        if (menuId == 0 && optionId >= 0 && optionId <= 11) {
                            Stall stall = StallManager.getInstance().findByID(optionId);
                            if (stall != null) {
                                stall.show(this);
                                return;
                            }
                        }
                    } else if (npcTemplateId == NpcName.KAMAKURA) {
                        if (menuId == 2 && optionId == 0) {
                            teleportVDMQ();
                            return;
                        }
                    } else if (npcTemplateId == NpcName.VUA_HUNG) {
                        if (menuId == 1 && optionId == 0) {
                            teleportUPYEN();
                            return;
                        }
                    } else if (npcTemplateId == NpcName.RIKUDOU) {
                        if (menuId == 1) {
                            if (optionId == 0) {
                                orderTaskDay();
                                return;
                            }
                            if (optionId == 1) {
                                cancelTaskDay();
                                return;
                            }
                            if (optionId == 2) {
                                finishTaskDay();
                                return;
                            }
                            if (optionId == 3) {
                                workTaskDay();
                                return;
                            }
                        } else if (menuId == 2) {
                            if (optionId == 0) {
                                orderTaskBoss();
                                return;
                            }
                            if (optionId == 1) {
                                cancelTaskBoss();
                                return;
                            }
                            if (optionId == 2) {
                                finishTaskBoss();
                                return;
                            }
                            if (optionId == 3) {
                                workTaskBoss();
                                return;
                            }
                        }
                    } else if (npcTemplateId == NpcName.TAJIMA) {
                        if (menuId == 3 && optionId == 0) {
                            if (isHuman && clone != null && clone.isNhanBan && !clone.isDead && timeCountDown > 0) {
                                clone.switchToMe();
                            }
                        }
                    }
                }

                Menu menuNew = null;
                if (menus.isEmpty()) {
                    initMenu(npcTemplateId);
                }
                if (menus.size() > 0 && menuId < menus.size()) {
                    menuNew = menus.get(menuId);
                    menus.clear();
                }
                if (menuNew != null) {
                    menu(menuNew, npcTemplateId);
                }
            } catch (Exception e) {
                Log.error("err: " + e.getMessage(), e);
            }
        } catch (Exception ex) {
            // Log.error("err: " + ex.getMessage(), ex);
            Log.info(ex);
        }
    }

    public void showUpgradeSoulStone(int itemID) {
        if (getSlotNull() == 0) {
            warningBagFull();
            return;
        }
        stoneItemId = itemID;
        if (stoneItemId < 880 || stoneItemId > 889) {
            return;
        }
        ItemTemplate itemTemplate = ItemManager.getInstance().getItemTemplate(stoneItemId);
        setConfirmPopup(new ConfirmPopup(CMDConfirmPopup.UPGRADE_SOUL_STONE, "Bạn có muốn ghép 10 " + itemTemplate.name
                + " + 1 gậy linh hồn + 5 bụi linh hồn với tỉ lệ thành công là 50% không?"));
        getService().openUIConfirmID();
    }

    public void takingTask() {
        if (taskMain == null) {
            TaskTemplate template = Task.getTaskTemplate(taskId);
            if (template == null) {
                return;
            }
            short count = 0;
            short[] counts = template.getCounts();
            if (counts[0] < 0) {
                count = counts[0];
            }
            taskMain = TaskFactory.getInstance().createTask(taskId, (byte) 0, count);

            if (taskMain.taskId == TaskName.NV_TRUYEN_TAI_TIN_TUC) {
                Item thu = ItemFactory.getInstance().newItem(ItemName.THU_LIEN_LAC);
                thu.isLock = true;
                thu.setQuantity(3);
                addItemToBag(thu);
            } else if (taskMain.taskId == TaskName.NV_LAY_NUOC_HANG_SAU) {
                Item binhnuoc = ItemFactory.getInstance().newItem(ItemName.BINH_RONG);
                binhnuoc.isLock = true;
                binhnuoc.setQuantity(50);
                addItemToBag(binhnuoc);
            } else if (taskMain.taskId == TaskName.NV_TRUY_TIM_DIA_DO) {
                Item chiaKhoa = ItemFactory.getInstance().newItem(ItemName.CHIA_KHOA_CO_QUAN2);
                chiaKhoa.isLock = true;
                chiaKhoa.setQuantity(1);
                addItemToBag(chiaKhoa);
            } else if (taskMain.taskId == TaskName.NV_TRUY_TIM_BAO_VAT) {
                Item diaDo = ItemFactory.getInstance().newItem(
                        (Integer) NinjaUtils.randomObject(ItemName.DIA_DO2, ItemName.DIA_DO3, ItemName.DIA_DO4));
                diaDo.isLock = true;
                diaDo.setQuantity(1);
                addItemToBag(diaDo);
            } else if (taskMain.taskId == TaskName.NV_THU_THAP_XAC_DOI_LUA) {
                Item tinhTheBang = ItemFactory.getInstance().newItem(ItemName.TINH_THE_BANG2);
                tinhTheBang.isLock = true;
                tinhTheBang.setQuantity(100);
                addItemToBag(tinhTheBang);
            } else if (taskMain.taskId == TaskName.NV_REN_LUYEN_Y_CHI) {
                Item canCau = ItemFactory.getInstance().newItem(ItemName.CAN_CAU);
                canCau.isLock = true;
                canCau.setQuantity(1);
                addItemToBag(canCau);
            } else if (taskMain.taskId == TaskName.NV_GIUP_DO_DAN_LANG) {
                Item binhnuoc = ItemFactory.getInstance().newItem(ItemName.BINH_RONG);
                binhnuoc.isLock = true;
                binhnuoc.setQuantity(150);
                addItemToBag(binhnuoc);
            }
            getService().sendTaskInfo();
            if (taskId == TaskName.NV_LAN_DAU_DUNG_KIEM && equipment[1] != null) {
                taskNext();
            }
            updateTaskLevelUp();
        }
    }

    private void menu(Menu menu, int npc) {
        switch (menu.getId()) {

            case CMDMenu.EXECUTE:
                menu.confirm();
                break;

            case CMDMenu.VXMM_1:
                menus.clear();
                menus.add(new Menu(CMDMenu.EXECUTE, "Thông tin", () -> {
                    typeVXMM = LuckyDrawManager.VIP;
                    LuckyDraw lucky = LuckyDrawManager.getInstance().find(LuckyDrawManager.VIP);
                    lucky.show(this);
                }));
                menus.add(new Menu(CMDMenu.EXECUTE, "Tham gia", () -> {
                    LuckyDraw lucky = LuckyDrawManager.getInstance().find(LuckyDrawManager.VIP);
                    InputDialog input = new InputDialog(CMDInputDialog.VXMMVIP, lucky.getName());
                    setInput(input);
                    getService().showInputDialog();
                }));
                menus.add(new Menu(CMDMenu.EXECUTE, "Luật chơi", () -> {
                    LuckyDraw lucky = LuckyDrawManager.getInstance().find(LuckyDrawManager.VIP);
                    getService().showAlert(lucky.getName(),
                            String.format(language.getString("LAW"), NinjaUtils.getCurrency(lucky.getXuWin()),
                                    NinjaUtils.getCurrency(lucky.getXuMax())));
                }));
                getService().openUIMenu();
                break;

            case CMDMenu.VXMM_2:
                menus.clear();
                menus.add(new Menu(CMDMenu.EXECUTE, "Thông tin", () -> {
                    typeVXMM = LuckyDrawManager.NORMAL;
                    LuckyDraw lucky = LuckyDrawManager.getInstance().find(LuckyDrawManager.NORMAL);
                    lucky.show(this);
                }));
                menus.add(new Menu(CMDMenu.EXECUTE, "Tham gia", () -> {
                    LuckyDraw lucky = LuckyDrawManager.getInstance().find(LuckyDrawManager.NORMAL);
                    InputDialog input = new InputDialog(CMDInputDialog.VXMMNORMAL, lucky.getName());
                    setInput(input);
                    getService().showInputDialog();
                }));
                menus.add(new Menu(CMDMenu.EXECUTE, "Luật chơi", () -> {
                    LuckyDraw lucky = LuckyDrawManager.getInstance().find(LuckyDrawManager.NORMAL);
                    getService().showAlert(lucky.getName(),
                            String.format(language.getString("LAW"), NinjaUtils.getCurrency(lucky.getXuWin()),
                                    NinjaUtils.getCurrency(lucky.getXuMax())));
                }));
                getService().openUIMenu();
                break;

            case CMDMenu.HOAN_THANH_NHIEM_VU:
                finishTask(false);
                break;

            case CMDMenu.NHAN_NHIEM_VU:
                menus.clear();
                menus.add(new Menu(CMDMenu.XAC_NHAN_NHAN_NHIEM_VU, "Nhận"));
                menus.add(new Menu(CMDMenu.CANCEL, "Hủy"));
                getService().openUIConfirm(npc, "Bạn có muốn nhận nhiệm vụ này.");
                break;

            case CMDMenu.XAC_NHAN_NHAN_NHIEM_VU:
                takingTask();
                break;

            case CMDMenu.LAM_NHIEM_VU:
                if (taskMain != null) {
                    if (npc == 14 || npc == 15 || npc == 16) {
                        taskNext();
                        int index = getIndexItemByIdInBag(214);
                        if (index != -1) {
                            this.removeItem(index, 1, true);
                        }
                    } else if (taskId == TaskName.NV_KIEN_THUC || taskId == TaskName.NV_BAI_HOC_VAO_TRUONG) {
                        questionAndAnswer();
                    }
                    break;
                }

            case CMDMenu.A:
                if (taskId == TaskName.NV_KIEN_THUC) {
                    if (nAnswer == 0 || nAnswer == 5) {
                        nAnswer++;
                    }
                }
                if (taskId == TaskName.NV_BAI_HOC_VAO_TRUONG) {
                    if (taskMain.index == 1) {
                        if (nAnswer == 1) {
                            nAnswer++;
                        }
                    } else if (taskMain.index == 6) {
                        if (nAnswer == 1) {
                            nAnswer++;
                        }
                    } else if (taskMain.index == 11) {
                        if (nAnswer == 0) {
                            nAnswer++;
                        }
                    }
                }
                break;

            case CMDMenu.B:
                if (taskId == TaskName.NV_KIEN_THUC) {
                    if (nAnswer == 2) {
                        nAnswer++;
                    }
                }
                if (taskId == TaskName.NV_BAI_HOC_VAO_TRUONG) {
                    if (taskMain.index == 1) {
                        if (nAnswer == 0 || nAnswer == 2 || nAnswer == 3) {
                            nAnswer++;
                        }
                    } else if (taskMain.index == 6) {
                        if (nAnswer == 4) {
                            nAnswer++;
                        }
                    } else if (taskMain.index == 11) {
                        if (nAnswer == 1 || nAnswer == 4) {
                            nAnswer++;
                        }
                    }
                }
                break;

            case CMDMenu.C:
                if (taskId == TaskName.NV_KIEN_THUC) {
                    if (nAnswer == 1 || nAnswer == 6) {
                        nAnswer++;
                    }
                }
                if (taskId == TaskName.NV_BAI_HOC_VAO_TRUONG) {
                    if (taskMain.index == 1) {
                        if (nAnswer == 4) {
                            nAnswer++;
                        }
                    } else if (taskMain.index == 6) {
                        if (nAnswer == 0 || nAnswer == 2 || nAnswer == 3) {
                            nAnswer++;
                        }
                    } else if (taskMain.index == 11) {
                        if (nAnswer == 2 || nAnswer == 3) {
                            nAnswer++;
                        }
                    }
                }
                break;

            case CMDMenu.D:
                if (taskId == TaskName.NV_KIEN_THUC) {
                    if (nAnswer == 3 || nAnswer == 4) {
                        nAnswer++;
                    }
                }
                if (taskId == TaskName.NV_BAI_HOC_VAO_TRUONG) {
                    if (taskMain.index == 1) {

                    } else if (taskMain.index == 6) {

                    } else if (taskMain.index == 11) {

                    }
                }
                break;

            case CMDMenu.TRIEU_HOI_THAN_THU:
                if (clan != null) {
                    int type = menu.getIntExtra();
                    ThanThu thanThu = clan.getThanThu(type);
                    if (thanThu != null) {
                        if (thanThu.getEggHatchingTime() == -1) {
                            int indexItem = getIndexItemByIdInBag(ItemName.TRIEU_HOI_THU_THAN);
                            if (indexItem != -1) {
                                Item item = ItemFactory.getInstance().newItem(thanThu.getItemID());
                                item.setQuantity(1);
                                item.isLock = true;
                                for (ItemOption o : thanThu.getOptions()) {
                                    item.options.add(new ItemOption(o.optionTemplate.id, o.param));
                                }
                                item.isLock = true;
                                item.expire = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000);
                                removeItem(indexItem, 1, true);
                                addItemToBag(item);
                            } else {
                                getService().serverDialog("Kho đã hết Triệu Hồi Thú Thần");
                            }
                        } else {
                            getService().serverDialog("Trứng thần thú đang trong quá trình ấp");
                        }
                    }
                }

                break;

            case CMDMenu.UPGRADE_SOUL_STONE:
                showUpgradeSoulStone(menu.getIntExtra());
                break;

        }
        if (taskMain != null) {
            if (taskId == TaskName.NV_KIEN_THUC && taskMain.index == 0) {
                if (nAnswer < 7) {
                    questionAndAnswer();
                } else {
                    taskNext();
                    taskNext();
                    taskNext();
                    taskNext();
                    taskNext();
                    nAnswer = 0;
                }
            }
            if (taskId == TaskName.NV_BAI_HOC_VAO_TRUONG
                    && (taskMain.index == 1 || taskMain.index == 6 || taskMain.index == 11)) {
                if (nAnswer < 5) {
                    questionAndAnswer();
                } else {
                    taskNext();
                    taskNext();
                    taskNext();
                    taskNext();
                    taskNext();
                    nAnswer = 0;
                }
            }
        }
    }

    private void finishTask(boolean quick) {
        if (taskMain != null && taskMain.index == taskMain.template.getSubNames().length - 1 || quick) {
            int num = this.getSlotNull();
            switch (taskId) {
                case TaskName.NHIEM_VU_CHAO_LANG:
                    addExp(200);
                    addYen(10000);
                    break;

                case TaskName.NV_KIEN_THUC:
                    if (num < 1) {
                        warningBagFull();
                        return;
                    }
                    addExp(400);
                    addYen(100);
                    receiveWoodenSword();
                    Skill skillNew = GameData.getInstance().getSkill(SkillName.CHIEU_CO_BAN, 0, 0);
                    vSkill.add(skillNew);
                    if (skillNew.template.type == Skill.SKILL_AUTO_USE) {
                        vSupportSkill.add(skillNew);
                    } else if ((skillNew.template.type == Skill.SKILL_CLICK_USE_ATTACK
                            || skillNew.template.type == Skill.SKILL_CLICK_LIVE
                            || skillNew.template.type == Skill.SKILL_CLICK_USE_BUFF
                            || skillNew.template.type == Skill.SKILL_CLICK_NPC)
                            && (skillNew.template.maxPoint == 0
                            || (skillNew.template.maxPoint > 0 && skillNew.point > 0))) {

                        vSkillFight.add(skillNew);
                    }
                    selectSkill((short) 0);
                    getService().loadClass();
                    getService().loadSkill();
                    break;

                case TaskName.NV_LAN_DAU_DUNG_KIEM:
                    addExp(800);
                    addYen(200);
                    break;

                case TaskName.NV_DIET_SEN_TRU_COC:
                    addExp(1600);
                    addYen(300);
                    break;

                case TaskName.NV_VAT_LIEU_TAO_GIAP:
                    if (num < 1) {
                        warningBagFull();
                        return;
                    }
                    int itemId = this.gender == 0 ? 198 : 197;
                    Item quan = ItemFactory.getInstance().newItem(itemId);
                    quan.sys = (byte) ((int)NinjaUtils.nextInt(3) + 1);
                    quan.isLock = true;
                    quan.options.add(new ItemOption(47, 1));
                    quan.options.add(new ItemOption(6, 5));
                    addItemToBag(quan);
                    addExp(3200);
                    addYen(400);
                    break;

                case TaskName.NV_HAI_THUOC_CUU_NGUOI:
                    if (num < 2) {
                        warningBagFull();
                        return;
                    }
                    Item hp = ItemFactory.getInstance().newItem(13);
                    hp.isLock = true;
                    hp.setQuantity(10);
                    addItemToBag(hp);
                    Item mp = ItemFactory.getInstance().newItem(18);
                    mp.isLock = true;
                    mp.setQuantity(10);
                    addItemToBag(mp);
                    addExp(4000);
                    addYen(500);
                    break;

                case TaskName.NV_KHAM_PHA_XA_LANG:
                    addExp(6000);
                    addYen(600);
                    break;

                case TaskName.NV_BAI_HOC_VAO_TRUONG:
                    addExp(8000);
                    addYen(800);
                    break;

                case TaskName.NV_TIM_HIEU_3_TRUONG:
                    if (num < 2) {
                        warningBagFull();
                        return;
                    }
                    Item ngoc1 = ItemFactory.getInstance().newItem(ItemName.NGOC_1_SAO);
                    ngoc1.isLock = true;
                    addItemToBag(ngoc1);
                    Item vhkdl = ItemFactory.getInstance().newItem(37);
                    vhkdl.isLock = true;
                    vhkdl.expire = System.currentTimeMillis() + 604800000L;
                    addItemToBag(vhkdl);
                    addExp(12000);
                    addYen(1000);
                    break;

                case TaskName.NV_GIA_TANG_SUC_MANH:
                    addExp(16000);
                    addYen(5000);
                    break;

                case TaskName.NV_BAI_HOC_DAU_TIEN:
                    addExp(22000);
                    addYen(10000);
                    break;

                case TaskName.NV_BAN_HUU_TAM_GIAO:
                    addExp(30000);
                    addYen(20000);
                    break;

                case TaskName.NV_NANG_CAP_TRANG_BI:
                    addExp(40000);
                    addYen(30000);
                    break;

                case TaskName.NV_THACH_DAU:
                    addExp(60000);
                    addYen(30000);
                    break;

                case TaskName.NV_THU_THAP_NGUYEN_LIEU:
                    addExp(80000);
                    addYen(40000);
                    break;

                case TaskName.NV_TRUYEN_TAI_TIN_TUC:
                    addExp(100000);
                    addYen(40000);
                    break;

                case TaskName.NV_REN_LUYEN_THE_LUC:
                    addExp(140000);
                    addYen(50000);
                    break;
                case TaskName.NV_DUA_JAIAN_TRO_VE:
                    addExp(200000);
                    addYen(60000);
                    break;
                case TaskName.NV_TIM_NGUYEN_LIEU_LAM_THUOC:
                    addExp(250000);
                    addYen(70000);
                    break;
                case TaskName.NV_LAY_NUOC_HANG_SAU:
                    addExp(300000);
                    addYen(80000);
                    break;
                case TaskName.NV_TIM_LAI_CAY_RIU:
                    addExp(360000);
                    addYen(90000);
                    break;
                case TaskName.NV_VUOT_QUA_THU_THACH:
                    addExp(400000);
                    addYen(100000);
                    break;
                case TaskName.NV_THU_THAP_CHIA_KHOA:
                    addExp(500000);
                    addYen(110000);
                    break;
                case TaskName.NV_TRUY_TIM_DIA_DO:
                    addExp(600000);
                    addYen(120000);
                    break;
                case TaskName.NV_TRUY_TIM_BAO_VAT:
                    addExp(700000);
                    addYen(130000);
                    break;
                case TaskName.NV_REN_LUYEN:
                    addExp(800000);
                    addYen(150000);
                    break;
                case TaskName.NV_THU_THAP_TINH_THE_BANG:
                    addExp(900000);
                    addYen(160000);
                    break;
                case TaskName.NV_THU_THAP_XAC_DOI_LUA:
                    addExp(1000000);
                    addYen(170000);
                    break;
                case TaskName.NV_KIEN_TRI_DIET_AC:
                    addExp(1100000);
                    addYen(180000);
                    break;
                case TaskName.NV_GIET_TINH_ANH:
                    addExp(1200000);
                    addYen(190000);
                    break;
                case TaskName.NV_TUAN_HOAN:
                    addExp(1300000);
                    addYen(200000);
                    break;
                case TaskName.NV_DU_TRU_LUONG_THUC:
                    addExp(1400000);
                    addYen(210000);
                    break;
                case TaskName.NV_REN_LUYEN_Y_CHI:
                    addExp(1500000);
                    addYen(220000);
                    break;
                case TaskName.NV_DIET_MA:
                    addExp(1600000);
                    addYen(230000);
                    break;
                case TaskName.NV_HAI_NAM:
                    addExp(1700000);
                    addYen(240000);
                    break;
                case TaskName.NV_GIUP_DO_DAN_LANG:
                    addExp(1800000);
                    addYen(250000);
                    break;
                case TaskName.NV_THU_THAP_OAN_HON:
                    addExp(1900000);
                    addYen(260000);
                    break;
                case TaskName.NV_THU_THACH_CUA_GURIIN:
                    addExp(2000000);
                    addYen(270000);
                    break;
                case TaskName.NV_THAP_SANG_BAN_LANG:
                    addExp(2100000);
                    addYen(280000);
                    break;
                case TaskName.NV_HOAT_DONG_HANG_NGAY:
                    addExp(2200000);
                    addYen(290000);
                    break;
                case TaskName.NV_THU_TAI_MAY_MAN:
                    addExp(2300000);
                    addYen(300000);
                    break;
                case TaskName.NV_CHIEN_TRUONG:
                    addExp(2400000);
                    addYen(400000);
                    break;

                case TaskName.NV_BAT_KHA_THI:
                    if (num < 1) {
                        warningBagFull();
                        return;
                    }
                    Item ngoc7 = ItemFactory.getInstance().newItem(ItemName.NGOC_7_SAO);
                    ngoc7.isLock = true;
                    addItemToBag(ngoc7);
                    addExp(10000000);
                    addYen(1000000);
                    break;

            }
            for (int i = 0; i < this.bag.length; i++) {
                if (this.bag[i] != null && ((int) this.bag[i].template.type == 25
                        || (int) this.bag[i].template.type == 23 || (int) this.bag[i].template.type == 24)) {
                    this.bag[i] = null;
                }
            }
            updateTask();
            return;
        }
    }

    public void receiveWoodenSword() {
        Item item = ItemFactory.getInstance().newItem(ItemName.KIEM_GO);
        item.isLock = true;
        item.options.add(new ItemOption(0, 10));
        item.options.add(new ItemOption(8, 1));
        item.yen = 0;
        addItemToBag(item);
    }

    public void upgradeSoulStone() {
        int index = getIndexItemByIdInBag(stoneItemId);
        int soulDustIndex = getIndexItemByIdInBag(893);
        int soulStickId = 0;
        if (stoneItemId <= 882) {
            soulStickId = 890;
        } else if (stoneItemId <= 885) {
            soulStickId = 891;
        } else {
            soulStickId = 892;
        }
        int soulStickIndex = getIndexItemByIdInBag(soulStickId);
        ItemTemplate itemTemplate = ItemManager.getInstance().getItemTemplate(stoneItemId);
        if (index == -1 || bag[index] == null || !bag[index].has(10)) {
            serverMessage("Bạn cần tối thiểu 10 " + itemTemplate.name + " để ghép lên cấp kế tiếp ");
            return;
        }
        if (soulDustIndex == -1 || bag[soulDustIndex] == null || !bag[soulDustIndex].has(10)) {
            serverMessage("Bạn cần tối thiểu 10 bụi linh hồn để có thể làm phép");
            return;
        }

        if (soulStickIndex == -1 || bag[soulStickIndex] == null || !bag[soulStickIndex].has()) {
            serverMessage("Bạn cần tối thiểu 1 gậy linh hồn để có thể làm phép");
            return;
        }
        removeItem(soulStickIndex, 1, true);
        removeItem(soulDustIndex, 5, true);
        if ((int)NinjaUtils.nextInt(100) < 30) {
            serverMessage("Ghép phép thất bại, bạn bị tổn thất 1 gậy linh hồn và 10 bụi linh hồn");
            return;
        }
        removeItem(index, 10, true);
        Item item = ItemFactory.getInstance().newItem(stoneItemId + 1);
        addItemToBag(item);
        serverMessage("Ghép phép thành công, bạn đã nhận được " + item.template.name);
    }

    public void upgradeAnToc() {
        if (fashion[13] == null) {
            serverMessage("Hãy sử dụng Ấn tộc để sử dụng chức năng này.");
            return;
        }
        if (fashion[13].upgrade >= 10) {
            serverMessage("Ấn tộc đã đạt cấp tối đa.");
            return;
        }
        int[] costs = {1000000, 1750000, 2250000, 3500000, 5200000, 7500000, 8200000, 10500000, 15000000};
        int[] costs2 = {1, 1, 2, 2, 3, 3, 4, 5, 6};
        int[] percents = {50, 35, 25, 20, 15, 12, 10, 8, 5};
        int[] items = {880, 881, 882, 883, 884, 885, 886, 887, 888};

        int index = fashion[13].upgrade - 1;
        int indexItem = getIndexItemByIdInBag(items[index], true);
        int indexGiayPhep = getIndexItemByIdInBag(ItemName.GIAY_PHEP_NANG_CAP);
        if (indexItem == -1) {
            indexItem = getIndexItemByIdInBag(items[index], false);
        }
        if (indexItem == -1) {
            ItemTemplate template = ItemManager.getInstance().getItemTemplate(items[index]);
            serverMessage("Yêu cầu phải có 10 viên " + template.name);
            return;
        }
        if (getSlotNull() == 0) {
            warningBagFull();
            return;
        }
        if (indexGiayPhep == -1 || this.bag[indexGiayPhep] == null
                || !this.bag[indexGiayPhep].has(costs2[index])) {
            serverMessage("Bạn cần tối thiểu " + costs2[index] + " giấy phép nâng cấp để có thể thăng cấp ấn tộc");
            return;
        }
        Item item = this.bag[indexItem];
        if (!item.has(10)) {
            serverMessage("Yêu cầu phải có 10 viên " + item.template.name);
            return;
        }
        int cost = costs[index];
        if (NinjaUtils.sum(this.yen, this.coin) < cost) {
            serverMessage("Không đủ xu hoặc yên để thăng cấp ấn tộc");
            return;
        }
        int percent = percents[index];
        int rand = (int)NinjaUtils.nextInt(100);
        if (rand < percent) {
            getService().deleteItemBody(fashion[13].template.type);
            Item itm = ItemFactory.getInstance().newItem(fashion[13].id + 1);
            itm.isLock = true;
            addItemToBag(itm);
            fashion[13] = null;
        } else {
            serverMessage("Thăng cấp thất bại.");
        }
        if (this.yen < cost) {
            addCoin(-(cost - this.yen));
            addYen(-this.yen);
        } else {
            addYen(-cost);
        }
        removeItem(indexItem, 10, true);
        removeItem(indexGiayPhep, costs2[index], true);
    }

    public void getTask(Message ms) {
        try {
            byte npcId = ms.reader().readByte();
            Npc npc = zone.getNpc(npcId);
            if (npc == null) {
                serverDialog("Không tìm thấy NPC này");
                return;
            }
            if (!isHuman) {
                if (npcId != 7 && npcId != 8 && npcId != 9 && npcId != 10 && npcId != 11 && npcId != 12 && npcId != 13
                        && npcId != 5 && npcId != 39) {
                    getService().endWait("Bạn đang trong chế độ thứ thân không thể dùng được chức năng này.");
                    return;
                }
            }
            byte menuId = ms.reader().readByte();
            Menu menuNew = null;
            if (menus.size() > 0 && menus.size() >= menuId) {
                menuNew = menus.get(menuId);
                menus.clear();
            }
            if (menuNew != null) {
                menu(menuNew, npcId);
                return;
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void questionAndAnswer() {
        try {
            String question = "";
            int npc = 4;
            if (taskId == TaskName.NV_KIEN_THUC) {
                npc = 4;
                switch (nAnswer) {
                    case 0:
                        question = "Trình độ tối đa có thể đạt được là bao nhiêu?";
                        menus.add(new Menu(CMDMenu.A, "Cấp 150"));
                        menus.add(new Menu(CMDMenu.B, "Cấp 130"));
                        menus.add(new Menu(CMDMenu.C, "Cấp 99"));
                        break;

                    case 1:
                        question = "Trình độ bao nhiêu mới có thể khám phá Vùng đất ma quỷ??";
                        menus.add(new Menu(CMDMenu.A, "Cấp 50"));
                        menus.add(new Menu(CMDMenu.B, "Cấp 100"));
                        menus.add(new Menu(CMDMenu.C, "Cấp 60"));
                        break;

                    case 2:
                        question = "Thức ăn dùng để làm gì?";
                        menus.add(new Menu(CMDMenu.A, "Ăn cho ngon"));
                        menus.add(new Menu(CMDMenu.B, "Hồi phục thể lực"));
                        menus.add(new Menu(CMDMenu.C, "Để phí tiền"));
                        break;

                    case 3:
                        question = "Khám phá làng cổ cần những điều kiện gì?";
                        menus.add(new Menu(CMDMenu.A, "Cấp 70"));
                        menus.add(new Menu(CMDMenu.B, "Có cổ lệnh"));
                        menus.add(new Menu(CMDMenu.C, "Không có điểm hiếu chiến"));
                        menus.add(new Menu(CMDMenu.D, "Tất cả"));
                        break;

                    case 4:
                        question = "Muốn tinh luyện trang bị cần phải chuẩn bị gì?";
                        menus.add(new Menu(CMDMenu.A, "Trang bị cấp 12"));
                        menus.add(new Menu(CMDMenu.B, "Tinh thạch"));
                        menus.add(new Menu(CMDMenu.C, "Xu hoặc yên"));
                        menus.add(new Menu(CMDMenu.D, "Tất cả"));
                        break;

                    case 5:
                        question = "Hashimoto hiện giờ đang ở đâu?";
                        menus.add(new Menu(CMDMenu.A, "Bờ biển Oura"));
                        menus.add(new Menu(CMDMenu.B, "Nghĩa địa Izuko"));
                        menus.add(new Menu(CMDMenu.C, "Hang Kugyou"));
                        break;

                    case 6:
                        question = "Nao đang cô độc ở đâu trong thế giới Ninja?";
                        menus.add(new Menu(CMDMenu.A, "Hang Ha"));
                        menus.add(new Menu(CMDMenu.B, "Rừng Toge"));
                        menus.add(new Menu(CMDMenu.C, "Mũi Nuranura"));
                        break;
                }
            } else if (taskId == TaskName.NV_BAI_HOC_VAO_TRUONG) {
                if (taskMain.index == 1) {
                    npc = 4;
                    switch (nAnswer) {
                        case 0:
                            question = "Con có thể trao đổi buôn bán những loại vật phẩm nào?";
                            menus.add(new Menu(CMDMenu.A, "Tiền yên và vật phẩm khoá"));
                            menus.add(new Menu(CMDMenu.B, "Tiền xu và vật phẩm không khoá"));
                            menus.add(new Menu(CMDMenu.C, "Tiền yên, xu và vật phẩm không khoá"));
                            break;

                        case 1:
                            question = "Có bao nhiều trường học quanh đây?";
                            menus.add(new Menu(CMDMenu.A, "3 trường"));
                            menus.add(new Menu(CMDMenu.B, "2 trường"));
                            menus.add(new Menu(CMDMenu.C, "4 trường"));
                            break;

                        case 2:
                            question = "Tổng cộng có bao nhiêu lớp học?";
                            menus.add(new Menu(CMDMenu.A, "3 lớp"));
                            menus.add(new Menu(CMDMenu.B, "6 lớp"));
                            menus.add(new Menu(CMDMenu.C, "12 lớp"));
                            break;

                        case 3:
                            question = "Nội công bao gồm những lớp nào?";
                            menus.add(new Menu(CMDMenu.A, "Kiếm, phi tiêu, quạt"));
                            menus.add(new Menu(CMDMenu.B, "Phi tiêu, quạt, cung"));
                            menus.add(new Menu(CMDMenu.C, "Kunai, cung, đao"));
                            break;

                        case 4:
                            question = "Ngoại công bao gồm những lớp nào?";
                            menus.add(new Menu(CMDMenu.A, "Phi tiêu, đao, cung"));
                            menus.add(new Menu(CMDMenu.B, "Quạt, kiếm, kunai"));
                            menus.add(new Menu(CMDMenu.C, "Kiếm, kunai, đao"));
                            break;
                    }
                } else if (taskMain.index == 6) {
                    npc = 5;
                    switch (nAnswer) {
                        case 0:
                            question = "Hiện tại con đang có bao nhiêu ô trong hành trang?";
                            menus.add(new Menu(CMDMenu.A, "10 ô"));
                            menus.add(new Menu(CMDMenu.B, "20 ô"));
                            menus.add(new Menu(CMDMenu.C, "30 ô"));
                            break;

                        case 1:
                            question = "Lưu toạ độ mặc định nhằm mục đích gì?";
                            menus.add(new Menu(CMDMenu.A, "Là nơi về khi bị thương"));
                            menus.add(new Menu(CMDMenu.B, "Nơi xuất hiện khi đăng nhập"));
                            menus.add(new Menu(CMDMenu.C, "Nơi đứng của người giữ rương"));
                            break;

                        case 2:
                            question = "Có mấy loại tiền tệ ở thế giới Ninja này?";
                            menus.add(new Menu(CMDMenu.A, "1 loại"));
                            menus.add(new Menu(CMDMenu.B, "2 loại"));
                            menus.add(new Menu(CMDMenu.C, "3 loại"));
                            break;

                        case 3:
                            question = "Tiền yên kiếm được bằng cách nào?";
                            menus.add(new Menu(CMDMenu.A, "Làm nhiệm vụ, tham gia hoạt động"));
                            menus.add(new Menu(CMDMenu.B, "Nhặt được khi đánh quái"));
                            menus.add(new Menu(CMDMenu.C, "Cả 2 trường hợp"));
                            break;

                        case 4:
                            question = "Loại tiền nào có thể trao đổi qua lại giữa các người chơi?";
                            menus.add(new Menu(CMDMenu.A, "Tiền yên"));
                            menus.add(new Menu(CMDMenu.B, "Tiền xu"));
                            menus.add(new Menu(CMDMenu.C, "Tiền lượng"));
                            break;
                    }
                } else if (taskMain.index == 11) {
                    npc = 6;
                    switch (nAnswer) {
                        case 0:
                            question = "Con có biết khi nâng cấp trang bị cần những gì không?";
                            menus.add(new Menu(CMDMenu.A, "Đá + yên hoặc xu"));
                            menus.add(new Menu(CMDMenu.B, "Đá"));
                            menus.add(new Menu(CMDMenu.C, "Yên hoặc xu"));
                            break;

                        case 1:
                            question = "Tách trang bị sau khi nâng cấp sẽ nhận được gì?";
                            menus.add(new Menu(CMDMenu.A, "Xu + 100% Đá khi ép vào"));
                            menus.add(new Menu(CMDMenu.B, "50% Đá đã ép vào"));
                            menus.add(new Menu(CMDMenu.C, "Không được gì cả"));
                            break;

                        case 2:
                            question = "Trang bị được nâng cấp tối đa lên cấp mấy?";
                            menus.add(new Menu(CMDMenu.A, "Cấp 12"));
                            menus.add(new Menu(CMDMenu.B, "Cấp 14"));
                            menus.add(new Menu(CMDMenu.C, "Cấp 16"));
                            break;

                        case 3:
                            question = "Trang bị căn bản có mấy loại?";
                            menus.add(new Menu(CMDMenu.A, "1 loại"));
                            menus.add(new Menu(CMDMenu.B, "2 loại"));
                            menus.add(new Menu(CMDMenu.C, "3 loại"));
                            break;

                        case 4:
                            question = "Sau khi nâng cấp trang bị sẽ như thế nào";
                            menus.add(new Menu(CMDMenu.A, "Không thay đổi"));
                            menus.add(new Menu(CMDMenu.B, "Lên cấp và chỉ số cao hơn"));
                            menus.add(new Menu(CMDMenu.C, "Trông đẹp hơn"));
                            break;
                    }
                }
            }
            getService().openUIConfirm(npc, question);
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public int getIdItemTask(int mobId) {
        if (taskMain != null) {
            if (mobId == MobName.NHIM_DA) {
                if (taskId == TaskName.NV_VAT_LIEU_TAO_GIAP && taskMain.index == 1) {
                    return ItemName.LONG_NHIM;
                }
            } else if (mobId == MobName.THO_XAM) {
                if (taskId == TaskName.NV_VAT_LIEU_TAO_GIAP && taskMain.index == 2) {
                    return ItemName.DA_THO;
                }
            } else if (mobId == MobName.KHI) {
                if (taskId == TaskName.NV_TIM_NGUYEN_LIEU_LAM_THUOC && taskMain.index == 1) {
                    return ItemName.NHI_HAU_TUU;
                }
            } else if (mobId == MobName.CHAU_CHAU) {
                if (taskId == TaskName.NV_TIM_NGUYEN_LIEU_LAM_THUOC && taskMain.index == 2) {
                    return ItemName.XAC_CHAU_CHAU;
                }
            } else if (mobId == MobName.OC_DA) {
                if (taskId == TaskName.NV_THU_THAP_NGUYEN_LIEU && taskMain.index == 2) {
                    return ItemName.VO_OC_DA;
                }
            } else if (mobId == MobName.THAO_DUOC) {
                if (taskId == TaskName.NV_HAI_THUOC_CUU_NGUOI && taskMain.index == 1) {
                    return ItemName.BONG_THAO_DUOC;
                }
            } else if (mobId == MobName.XUONG_KHO) {
                if (taskId == TaskName.NV_THU_THAP_CHIA_KHOA && taskMain.index == 1) {
                    return ItemName.CHIA_KHOA_CO_QUAN;
                }
            } else if (mobId == MobName.MA_DEM) {
                if (taskId == TaskName.NV_THAP_SANG_BAN_LANG && taskMain.index == 1) {
                    return ItemName.LONG_DEN_MA;
                }
            } else if (mobId == MobName.BUOM_PHAN) {
                if (taskId == TaskName.NV_THAP_SANG_BAN_LANG && taskMain.index == 2) {
                    return ItemName.CANH_BUOM;
                }
            } else if (mobId == MobName.HEO_RUNG) {
                if (taskId == TaskName.NV_TIM_LAI_CAY_RIU && taskMain.index == 1) {
                    return ItemName.RIU_BAC;
                }
            } else if (mobId == MobName.DOI_LUA) {
                if (taskId == TaskName.NV_THU_THAP_XAC_DOI_LUA && taskMain.index == 1) {
                    return ItemName.XAC_DOI_LUA;
                }
            } else if (mobId == MobName.CA_HO) {
                if (taskId == TaskName.NV_DU_TRU_LUONG_THUC && taskMain.index == 1) {
                    return ItemName.GIO_CA_HO;
                }
            } else if (mobId == MobName.RAN_TIA) {
                if (taskId == TaskName.NV_DU_TRU_LUONG_THUC && taskMain.index == 2) {
                    return ItemName.GIO_RAN_TIA;
                }
            } else if (mobId == MobName.OAN_HON) {
                if (taskId == TaskName.NV_THU_THAP_OAN_HON && taskMain.index == 1) {
                    return ItemName.OAN_HON;
                }
            }
        }
        return -1;
    }

    public void updateTaskLevelUp() {
        if (taskMain != null && taskMain.index == 0 && taskMain.template.getLeveRequire() > 0
                && this.level >= taskMain.template.getLeveRequire()) {
            taskNext();
        }
    }

    public void updateTaskPickItem(Item item) {
        if (taskMain != null) {
            short[] items = taskMain.template.getItems();
            if (items.length > 0) {
                if (items[taskMain.index] == item.id) {
                    if (taskId == TaskName.NV_TRUY_TIM_DIA_DO) {
                        ChikatoyaTunnels chikatoyaTunnels = (ChikatoyaTunnels) zone;
                        chikatoyaTunnels.setTimeMap(3);
                    }
                    updateTaskCount(!item.has() ? 1 : item.getQuantity());
                }
            }
        }
    }

    public void updateTaskKillMonster(Mob mob) {
        if (taskMain != null) {
            short[][] mobs = taskMain.template.getMobs();
            if (mobs.length > 0) {
                if ((mobs[taskMain.index][0] == mob.template.id)
                        && (mobs[taskMain.index][1] == 0 || mobs[taskMain.index][1] == mob.levelBoss)) {
                    updateTaskCount(1);
                }
            }

        }
    }

    public void openUIBox() {
        setCommandBox(RUONG_DO);
        getService().openUIBox();
    }

    public void npcKamakura() {
        menus.add(new Menu(CMDMenu.EXECUTE, "Mở rương", () -> {
            if (!isHuman) {
                warningClone();
                return;
            }
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Mở rương", () -> {
                openUIBox();
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Mở bộ sưu tập", () -> {
                setCommandBox(BO_SUU_TAP);
                getService().openUICollectionBox();
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Cải trang", () -> {
                setCommandBox(CAI_TRANG);
                getService().openUIMaskBox();
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Tháo cải trang", () -> {
                setMaskId(-1);
                this.mask = null;
                setAbility();
                setFashion();
                serverMessage("Tháo cải trang thành công.");
            }));

            menus.add(new Menu(CMDMenu.EXECUTE, "Kiểm tra xu", () -> {
                getService().showAlert("Trạng thái", "Xu hành giới hạn: " + NinjaUtils.getCurrency(this.coinMax) + "\nXu hiện có trong hành trang: " + NinjaUtils.getCurrency(this.coin) + "\nXu trong rương: " + NinjaUtils.getCurrency(this.coinInBox));
            }));

            getService().openUIMenu();
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Lưu tọa độ", () -> {
            if (!isHuman) {
                warningClone();
                return;
            }
            if ((isVillage() || isSchool()) && mapId != 138 && mapId != 162) {
                this.saveCoordinate = mapId;
                getService().npcChat(NpcName.KAMAKURA, "Lưu toạ độ thành công. Mày chết ta sẽ gọi hồn mày về đây");
            } else {
                if (this.mapId == 162) {
                    getService().npcChat(NpcName.KAMAKURA,
                            "Ngôi làng này rất nguy hiểm ta không thể để con về đây khi bị thương.");
                }
            }
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Vùng Đất Ma Quỷ", () -> {
            // menus.clear();
            // menus.add(new Menu(CMDMenu.CONFIRM, "Đi tới", () -> {
            // teleportVDMQ();
            // }));
            // menus.add(new Menu(CMDMenu.CONFIRM, "Hướng dẫn", () -> {
            // getService().npcChat(NpcName.KAMAKURA,
            // "Sử dụng vật phẩm Thí luyện thiếp để có thể tham gia phưu lưu tại Vùng Đất Ma
            // Quỷ. Thí luyện thiếp được bán tại NPC Goosho. Nhẫn thuật 80 sẽ xuất hiện tại
            // Vùng Đất Ma Quỷ.");
            // }));
            // getService().openUIMenu();
            teleportVDMQ();
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Mở giới hạn", () -> {
            if (!isHuman) {
                warningClone();
                return;
            }
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Xu hành trang", () -> {
                int levelUpgrade = ((int) this.coinMax - 1400000000) / 100000000;
                int fee = (levelUpgrade) * 200;
                menus.clear();
                menus.add(new Menu(CMDMenu.EXECUTE, String.format("Mở 100tr (%s lượng)", NinjaUtils.getCurrency(fee)),
                        () -> {
                            if (coinMax >= 3000000000L) {
                                getService().npcChat(NpcName.KAMAKURA, "Đã đạt giới hạn cao nhất");
                                return;
                            }
                            if (this.user.gold < fee) {
                                getService().npcChat(NpcName.KAMAKURA, "Không đủ lượng");
                                return;
                            }

                            addGold(-fee);
                            this.coinMax += 100000000;
                            getService().npcChat(NpcName.KAMAKURA, "Giới hạn xu của ngươi đã được tăng lên "
                                    + NinjaUtils.getCurrency(this.coinMax) + " xu");
                        }));
                getService().openUIMenu();
            }));
            getService().openUIMenu();
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Nói chuyện", () -> {
            if (!isHuman) {
                warningClone();
                return;
            }
            if (taskId == TaskName.NHIEM_VU_CHAO_LANG && taskMain != null && taskMain.index == 4) {
                taskNext();
            }
            String talk = (String) NinjaUtils.randomObject("Hãy an tâm giao đồ cho ta nào!",
                    "Trên người của ngươi toàn là những đồ có giá trị, Sao không cất bớt ở đây?",
                    "Ta giữ đồ chưa hề để thất lạc bao giờ.");
            if (this.getCoin() > 2000000000) {
                talk = "Ngươi đang có " + NinjaUtils.getCurrency(this.getCoin()) + " xu trong hành trang";
            }
            getService().npcChat(NpcName.KAMAKURA, talk);
        }));
    }

    public void admission(byte sys, short npc) {
        if (this.classId == 0) {
            if (taskId >= TaskName.NV_GIA_TANG_SUC_MANH && taskMain == null && this.level >= 10) {
                if (this.equipment[1] != null) {
                    getService().npcChat(npc,
                            "Con hãy gỡ bỏ tạp niệm bằng cách cất vũ khí đang sử dụng vào hành trang trước rồi hãy vào lớp học của ta.");
                    return;
                }
                this.classId = sys;
                this.skillPoint = (short) (this.level - 9);
                this.potentialPoint = (short) (this.level * 10);
                if (this.level >= 70) {
                    this.potentialPoint += (this.level - 70) * 10;
                }
                if (this.level >= 80) {
                    this.potentialPoint += (this.level - 80) * 10;
                }
                if (this.level >= 90) {
                    this.potentialPoint += (this.level - 90) * 10;
                }
                if (this.level >= 100) {
                    this.potentialPoint += (this.level - 100) * 10;
                }
                if (this.classId == 1 || this.classId == 3 || this.classId == 5) {
                    this.potential[0] = 15;
                    this.potential[1] = 5;
                    this.potential[2] = 5;
                    this.potential[3] = 5;
                } else {
                    this.potential[0] = 5;
                    this.potential[1] = 5;
                    this.potential[2] = 10;
                    this.potential[3] = 10;
                }
                vSkill.clear();
                this.onCSkill = new byte[]{};
                this.onKSkill = new byte[]{-1, -1, -1};
                this.onOSkill = new byte[]{-1, -1, -1, -1, -1};
                byte type = 0;
                if (!isHuman) {
                    type = 1;
                }
                getService().sendSkillShortcut("OSkill", this.onOSkill, type);
                getService().sendSkillShortcut("KSkill", this.onKSkill, type);
                getService().sendSkillShortcut("CSkill", this.onCSkill, type);
                switch (npc) {
                    case 11:
                        getService().npcChat(npc,
                                "Chào mừng con đến với trường Haruna. Con hãy sử dụng vũ khí và đọc sách võ công mà ta tặng (mở Menu/Bản thân/Hành trang) để bước đầu chuẩn bị cho việc học tập");
                        break;

                    case 9:
                        getService().npcChat(npc,
                                "Chào mừng con đến với trường Hirosaki. Con hãy sử dụng vũ khí và đọc sách võ công mà ta tặng (mở Menu/Bản thân/Hành trang) để bước đầu chuẩn bị cho việc học tập");
                        break;

                    case 10:
                        getService().npcChat(npc,
                                "Chào mừng con đến với trường Ookaza. Con hãy sử dụng vũ khí và đọc sách võ công mà ta tặng (mở Menu/Bản thân/Hành trang) để bước đầu chuẩn bị cho việc học tập");
                        break;
                }
                Item item = null;
                Item item2 = null;
                switch (sys) {
                    case 1:
                        item = ItemFactory.getInstance().newItem(40);
                        item.isLock = true;
                        item2 = ItemFactory.getInstance().newItem(94);
                        item2.isLock = true;
                        item2.sys = 1;
                        item2.options.add(new ItemOption(0, 100));
                        item2.options.add(new ItemOption(1, 100));
                        item2.options.add(new ItemOption(8, 10));
                        item2.options.add(new ItemOption(10, 5));
                        item2.options.add(new ItemOption(21, 100));
                        item2.options.add(new ItemOption(19, 10));
                        item2.options.add(new ItemOption(30, 5));
                        break;

                    case 3:
                        item = ItemFactory.getInstance().newItem(58);
                        item.isLock = true;
                        item2 = ItemFactory.getInstance().newItem(99);
                        item2.isLock = true;
                        item2.sys = 2;
                        item2.options.add(new ItemOption(0, 100));
                        item2.options.add(new ItemOption(1, 100));
                        item2.options.add(new ItemOption(8, 10));
                        item2.options.add(new ItemOption(10, 5));
                        item2.options.add(new ItemOption(21, 100));
                        item2.options.add(new ItemOption(19, 10));
                        item2.options.add(new ItemOption(30, 5));
                        break;

                    case 5:
                        item = ItemFactory.getInstance().newItem(76);
                        item.isLock = true;
                        item2 = ItemFactory.getInstance().newItem(104);
                        item2.isLock = true;
                        item2.sys = 3;
                        item2.options.add(new ItemOption(0, 100));
                        item2.options.add(new ItemOption(1, 100));
                        item2.options.add(new ItemOption(8, 10));
                        item2.options.add(new ItemOption(10, 5));
                        item2.options.add(new ItemOption(21, 100));
                        item2.options.add(new ItemOption(19, 10));
                        item2.options.add(new ItemOption(30, 5));
                        break;

                    case 2:
                        item = ItemFactory.getInstance().newItem(49);
                        item.isLock = true;
                        item2 = ItemFactory.getInstance().newItem(114);
                        item2.isLock = true;
                        item2.sys = 1;
                        item2.options.add(new ItemOption(0, 100));
                        item2.options.add(new ItemOption(1, 100));
                        item2.options.add(new ItemOption(9, 10));
                        item2.options.add(new ItemOption(10, 5));
                        item2.options.add(new ItemOption(22, 100));
                        item2.options.add(new ItemOption(19, 10));
                        item2.options.add(new ItemOption(30, 5));
                        break;

                    case 4:
                        item = ItemFactory.getInstance().newItem(67);
                        item.isLock = true;
                        item2 = ItemFactory.getInstance().newItem(109);
                        item2.isLock = true;
                        item2.sys = 2;
                        item2.options.add(new ItemOption(0, 100));
                        item2.options.add(new ItemOption(1, 100));
                        item2.options.add(new ItemOption(9, 10));
                        item2.options.add(new ItemOption(10, 5));
                        item2.options.add(new ItemOption(22, 100));
                        item2.options.add(new ItemOption(19, 10));
                        item2.options.add(new ItemOption(30, 5));
                        break;

                    case 6:
                        item = ItemFactory.getInstance().newItem(85);
                        item.isLock = true;
                        item2 = ItemFactory.getInstance().newItem(119);
                        item2.isLock = true;
                        item2.sys = 3;
                        item2.options.add(new ItemOption(0, 100));
                        item2.options.add(new ItemOption(1, 100));
                        item2.options.add(new ItemOption(9, 10));
                        item2.options.add(new ItemOption(10, 5));
                        item2.options.add(new ItemOption(22, 100));
                        item2.options.add(new ItemOption(19, 10));
                        item2.options.add(new ItemOption(30, 5));
                        break;
                }
                getService().loadClass();
                addItemToBag(item);
                addItemToBag(item2);
                selectedSkill = null;
                if (isHuman) {
                    taskMain = TaskFactory.getInstance().createTask(taskId, (byte) 0, (short) -1);
                    getService().sendTaskInfo();
                }
            } else {
                getService().npcChat(npc,
                        "Con vẫn chưa đủ điều kiện để vào lớp (trình độ từ cấp 10 và làm xong nhiệm vụ tìm hiểu trường");
            }
        } else {
            getService().npcChat(npc, "Con đã vào lớp từ trước rồi mà!");
        }
    }

    public void npcKazeto() {
        menus.add(new Menu(CMDMenu.EXECUTE, "Bảng xếp hạng", () -> {
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Top đại gia", () -> {
                showRankedList(0);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Top cao thủ", () -> {
                showRankedList(1);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Top gia tộc", () -> {
                showRankedList(2);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Top hang động", () -> {
                showRankedList(3);
            }));
            getService().openUIMenu();
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Nhập học", () -> {
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Ninja Đao", () -> {
                admission((byte) 5, (short) 11);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Ninja Quạt", () -> {
                admission((byte) 6, (short) 11);
            }));
            getService().openUIMenu();
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Tẩy điểm", () -> {
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Tiềm năng", () -> {
                tayTiemNang((short) 11);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Kỹ năng", () -> {
                tayKyNang((short) 11);
            }));
            getService().openUIMenu();
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Nói chuyện", () -> {
            String talk = (String) NinjaUtils.randomObject(
                    "Ngươi may mắn lắm mới gặp được ta đó, ta vốn là Thần Gió mà!",
                    "So với các trường khác, trường Gió là tốt nhất",
                    "Một số học sinh trường gió của chúng ta có thể chấp hai học sinh các trường kia.");
            getService().npcChat(NpcName.THAY_KAZETO, talk);
            if (taskId == TaskName.NV_TIM_HIEU_3_TRUONG && taskMain != null && taskMain.index == 3) {
                taskNext();
            }
        }));
//        menus.add(new Menu(CMDMenu.EXECUTE, "Giao chiến", () -> {
//            menus.clear();
//            menus.add(new Menu(CMDMenu.EXECUTE, "Tham gia", () -> {
//            }));
//            menus.add(new Menu(CMDMenu.EXECUTE, "Thành tích", () -> {
//            }));
//            menus.add(new Menu(CMDMenu.EXECUTE, "Hướng dẫn", () -> {
//            }));
//            getService().openUIMenu();
//        }));
    }

    public void npcOokamesama() {
        menus.add(new Menu(CMDMenu.EXECUTE, "Bảng xếp hạng", () -> {
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Top đại gia", () -> {
                showRankedList(0);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Top cao thủ", () -> {
                showRankedList(1);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Top gia tộc", () -> {
                showRankedList(2);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Top hang động", () -> {
                showRankedList(3);
            }));
            getService().openUIMenu();
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Nhập học", () -> {
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Ninja Kunai", () -> {
                admission((byte) 3, (short) 10);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Ninja Cung", () -> {
                admission((byte) 4, (short) 10);
            }));
            getService().openUIMenu();
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Tẩy điểm", () -> {
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Tiềm năng", () -> {
                tayTiemNang((short) 10);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Kỹ năng", () -> {
                tayKyNang((short) 10);
            }));
            getService().openUIMenu();
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Nói chuyện", () -> {
            String talk = (String) NinjaUtils.randomObject("Con có cảm thấy lạnh không?",
                    "Học, để thành tài, để thành người tốt, chứ không phải để ganh đua với đời.",
                    "Tập trung học tốt nhé con.");
            getService().npcChat(NpcName.THAY_OOKAMESAMA, talk);
            if (taskId == TaskName.NV_TIM_HIEU_3_TRUONG && taskMain != null && taskMain.index == 2) {
                taskNext();
            }
        }));
//        menus.add(new Menu(CMDMenu.EXECUTE, "Giao chiến", () -> {
//            menus.clear();
//            menus.add(new Menu(CMDMenu.EXECUTE, "Tham gia", () -> {
//            }));
//            menus.add(new Menu(CMDMenu.EXECUTE, "Thành tích", () -> {
//            }));
//            menus.add(new Menu(CMDMenu.EXECUTE, "Hướng dẫn", () -> {
//            }));
//            getService().openUIMenu();
//        }));
    }

    public void npcToyotomi() {
        menus.add(new Menu(CMDMenu.EXECUTE, "Bảng xếp hạng", () -> {
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Top đại gia", () -> {
                showRankedList(0);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Top cao thủ", () -> {
                showRankedList(1);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Top gia tộc", () -> {
                showRankedList(2);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Top hang động", () -> {
                showRankedList(3);
            }));
            getService().openUIMenu();
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Nhập học", () -> {
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Ninja Kiếm", () -> {
                admission((byte) 1, (short) 9);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Ninja Phi Tiêu", () -> {
                admission((byte) 2, (short) 9);
            }));
            getService().openUIMenu();
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Tẩy điểm", () -> {
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Tiềm năng", () -> {
                tayTiemNang((short) 9);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Kỹ năng", () -> {
                tayKyNang((short) 9);
            }));
            getService().openUIMenu();
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Nói chuyện", () -> {
            String talk = (String) NinjaUtils.randomObject(
                    "Trường ta dạy kiếm và phi tiêu, chẳng dạy các vũ khí vô danh khác",
                    "Ngươi muốn trở thành hỏa Ninja thì học, không thì cút.",
                    "Theo học ở đây là vinh hạnh của ngươi, biết chứ.");
            getService().npcChat(NpcName.CO_TOYOTOMI, talk);
            if (taskId == TaskName.NV_TIM_HIEU_3_TRUONG && taskMain != null && taskMain.index == 1) {
                taskNext();
            }
        }));
//        menus.add(new Menu(CMDMenu.EXECUTE, "Giao chiến", () -> {
//            menus.clear();
//            menus.add(new Menu(CMDMenu.EXECUTE, "Tham gia", () -> {
//            }));
//            menus.add(new Menu(CMDMenu.EXECUTE, "Thành tích", () -> {
//            }));
//            menus.add(new Menu(CMDMenu.EXECUTE, "Hướng dẫn", () -> {
//            }));
//            getService().openUIMenu();
//        }));
    }

    private void initMenu(int npcTemplateId) {
        menus.clear();
        short taskNpcId = getTaskNpcId();
        if (taskNpcId == npcTemplateId) {
            if (taskMain == null) {
                menus.add(new Menu(CMDMenu.NHAN_NHIEM_VU, Task.getTaskTemplate(taskId).getName()));
            } else if (taskMain.index == taskMain.template.getSubNames().length - 1) {
                menus.add(new Menu(CMDMenu.HOAN_THANH_NHIEM_VU, "Hoàn thành nhiệm vụ"));
            } else {
                if ((npcTemplateId == 14 || npcTemplateId == 15 || npcTemplateId == 16)) {
                    if (npcTemplateId == NpcName.FUJIWARA && taskId == TaskName.NV_TIM_LAI_CAY_RIU) {
                        menus.add(new Menu(CMDMenu.EXECUTE, "Vào trong hang", () -> {
                            Map map = MapManager.getInstance().find(MapName.HANG_INOSHISHI);
                            InoshishiCave z = new InoshishiCave(0, map.tilemap, map);
                            mapBeforeEnterPB = mapId;
                            outZone();
                            setXY((short) 35, (short) 0);
                            z.join(this);
                        }));
                    } else {
                        menus.add(new Menu(CMDMenu.LAM_NHIEM_VU, "Giao thư"));
                    }
                } else if (npcTemplateId == NpcName.THAY_OOKAMESAMA || npcTemplateId == NpcName.THAY_KAZETO
                        || npcTemplateId == NpcName.CO_TOYOTOMI) {
                    if (isSchool()) {
                        if (taskId == TaskName.NV_THACH_DAU) {
                            menus.add(new Menu(CMDMenu.EXECUTE, "Vào phòng thi đấu", () -> {
                                Map map = null;
                                Gymnasium z = null;
                                if (npcTemplateId == NpcName.THAY_KAZETO) {
                                    int mapID = MapName.NHA_THI_DAU_HARUNA;
                                    map = MapManager.getInstance().find(mapID);
                                    z = new HarunaGymnasium(0, map.tilemap, map);
                                } else if (npcTemplateId == NpcName.CO_TOYOTOMI) {
                                    int mapID = MapName.NHA_THI_DAU_HIROSAKI;
                                    map = MapManager.getInstance().find(mapID);
                                    z = new HirosakiGymnasium(0, map.tilemap, map);
                                } else {
                                    int mapID = MapName.NHA_THI_DAU_OOKAZA;
                                    map = MapManager.getInstance().find(mapID);
                                    z = new OokazaGymnasium(0, map.tilemap, map);
                                }
                                if (z != null) {
                                    outZone();
                                    z.setPlayer(this);
                                    setXY((short) 55, (short) 264);
                                    mapBeforeEnterPB = mapId;
                                    z.join(this);
                                }
                            }));
                        }
                    }
                } else if (npcTemplateId == NpcName.JAIAN) {
                    if (!isLeading()) {
                        getService().npcChat(npcTemplateId,
                                "Xin chào, ta là người dân ở làng Chài, trên dường đi đã bị bọn quái thú tấn công, xin hãy giúp ta tìm đường quay trở về.");
                        menus.add(new Menu(CMDMenu.EXECUTE, "Đi nào", () -> {
                            Task task = taskMain;
                            if (task != null && task.taskId == TaskName.NV_DUA_JAIAN_TRO_VE && task.index == 1) {
                                Npc npc = zone.getNpc(NpcName.JAIAN);
                                Bot bot = Bot.builder()
                                        .id(-this.id)
                                        .name(npc.template.name)
                                        .level(30)
                                        .typePk(PK_NORMAL)
                                        .build();
                                bot.setDefault();
                                FashionCustom fashionCustom = FashionCustom.builder()
                                        .head((short) npc.template.headId)
                                        .body((short) npc.template.bodyId)
                                        .leg((short) npc.template.legId)
                                        .weapon((short) -1)
                                        .build();
                                bot.setFashionStrategy(fashionCustom);
                                AbilityCustom abilityCustom = AbilityCustom.builder()
                                        .hp(2000)
                                        .build();
                                bot.setAbilityStrategy(abilityCustom);
                                bot.setMove(new JaianMove(this, (short) npc.cy));
                                bot.setAbility();
                                bot.setFashion();
                                bot.recovery();
                                bot.setXY((short) npc.cx, (short) npc.cy);
                                setEscorted(bot);
                                getService().npcUpdate(npc.id, 15);
                                zone.join(bot);
                                setLeading(true);
                            }
                        }));
                    }
                } else {
                    if (taskId == TaskName.NV_KIEN_THUC || taskId == TaskName.NV_BAI_HOC_VAO_TRUONG) {
                        menus.add(new Menu(CMDMenu.LAM_NHIEM_VU, taskMain.template.getName()));
                    }
                }
            }
        } else if (Events.event == Events.SUMMER
                && (npcTemplateId == 14 || npcTemplateId == 15 || npcTemplateId == 16)) {
            menus.add(new Menu(CMDMenu.TRAO_LINH_VAT, "Trao linh vật"));
        }
        if (zone.tilemap.isNhaThiDau()) {
            menus.add(new Menu(CMDMenu.EXECUTE, "Rời khỏi nơi này", () -> {
                short[] xy = NinjaUtils.getXY(mapBeforeEnterPB);
                setXY(xy[0], xy[1]);
                changeMap(mapBeforeEnterPB);
            }));
            return;
        }
        switch (npcTemplateId) {
            case NpcName.KANATA:
                npcKanata();
                break;
            case NpcName.FUROYA:
                npcFuroya();
                break;
            case NpcName.AMEJI:
                npcAmeji();
                break;
            case NpcName.KIRIKO:
                npcKiriko();
                break;
            case NpcName.TABEMONO:
                npcTabemono();
                break;
            case NpcName.KAMAKURA:
                npcKamakura();
                break;
            case NpcName.KENSHINTO:
                npcKenshinto();
                break;
            case NpcName.UMAYAKI:
                npcUmayaki_1();
                break;
            case NpcName.UMAYAKI_2:
                npcUmayaki_2();
                break;
            case NpcName.CO_TOYOTOMI:
                npcToyotomi();
                break;
            case NpcName.THAY_OOKAMESAMA:
                npcOokamesama();
                break;
            case NpcName.THAY_KAZETO:
                npcKazeto();
                break;
            case NpcName.TAJIMA:
                npcTajima();
                break;
            case NpcName.HASHIMOTO:
            case NpcName.FUJIWARA:
            case NpcName.NAO:
                if (Events.event == Events.SUMMER) {
                    menus.add(new Menu(CMDMenu.EXECUTE, "Dế Ngọc", () -> {
                        if (getSlotNull() == 0) {
                            getService().npcChat((short) npcTemplateId, "Hành trang của ngươi không có đủ chỗ trống");
                            return;
                        }
                        int indexItem = getIndexItemByIdInBag(ItemName.DE_NGOC);
                        if (indexItem != -1) {
                            RandomCollection<Integer> rc = RandomItem.LINH_VAT;
                            Event.useVipEventItem(this, 1, rc);
                            removeItem(indexItem, 1, true);
                        } else {
                            getService().npcChat((short) npcTemplateId, "Hãy tìm linh vật rồi đến gặp ta");
                        }
                    }));
                    menus.add(new Menu(CMDMenu.EXECUTE, "Bọ Vàng", () -> {
                        if (getSlotNull() == 0) {
                            getService().npcChat((short) npcTemplateId, "Hành trang của ngươi không có đủ chỗ trống");
                            return;
                        }
                        int indexItem = getIndexItemByIdInBag(ItemName.BO_VANG);
                        if (indexItem != -1) {
                            RandomCollection<Integer> rc = RandomItem.LINH_VAT;
                            Event.useVipEventItem(this, 2, rc);
                            removeItem(indexItem, 1, true);
                        } else {
                            getService().npcChat((short) npcTemplateId, "Hãy tìm linh vật rồi đến gặp ta");
                        }
                    }));
                    menus.add(new Menu(CMDMenu.EXECUTE, "Bướm vàng", () -> {
                        if (getSlotNull() == 0) {
                            getService().npcChat((short) npcTemplateId, "Hành trang của ngươi không có đủ chỗ trống");
                            return;
                        }
                        int indexItem = getIndexItemByIdInBag(ItemName.BUOM_VANG);
                        if (indexItem != -1) {
                            RandomCollection<Integer> rc = RandomItem.LINH_VAT;
                            Event.useVipEventItem(this, 2, rc);
                            removeItem(indexItem, 1, true);
                        } else {
                            getService().npcChat((short) npcTemplateId, "Hãy tìm linh vật rồi đến gặp ta");
                        }
                    }));

                }
                break;
            case NpcName.BA_REI:
                npcBaRei();
                break;
            case NpcName.OKANECHAN:
                npcOkanechan();
                break;
            case NpcName.MATSURUGI:
                npcMatsurugi();
                break;
            case NpcName.SOBA:
                npcShoba();
                break;
            case NpcName.RIKUDOU:
                npcRikudou();
                break;
            case NpcName.GOOSHO:
                npcGoosho();
                break;
            case NpcName.TRU_CO_QUAN:
                npcTruCoQuan();
                break;
            case NpcName.SHINWA:
                npcShinwa();
                break;
            case NpcName.RAKKII:
                npcRakkii();
                break;
            case NpcName.KAGAI:
                npcKagai();
                break;
            case NpcName.TIEN_NU:
                npcTienNu();
                break;
            case NpcName.VUA_HUNG:
                npcVuaHung();
                break;
            case NpcName.ADMIN:
                npcAdmin();
                break;
            case NpcName.CAY_THONG:
                npcCayThong();
                break;
            case NpcName.DEIDARA:
                npcDeidara();
                break;
            case NpcName.LONG_DEN_2:
                npcLongDen();
                break;
            case NpcName.TASHINO:
                npcTashino();
                break;

            case NpcName.HOA_MAI:
                npcHoaMai();
                break;

            case NpcName.EM_BE:
                npcEmBe();
                break;
                
            case NpcName.ruby:
                npcruby();
                break;
                
            case NpcName.NPCHuongDan:
                NPCHuongDan();
                break;
            case NpcName.NPCThuNuoi:
                NPCThuNuoi();
                break;
        }

    }

    public void escortFailed() {
        Bot escorted = getEscorted();
        escorted.outZone();
        Npc npc = zone.getNpc(NpcName.JAIAN);
        if (npc != null) {
            getService().npcUpdate(npc.id, 1);
        }
        setLeading(false);
        setEscorted(null);
    }

    public void npcKenshinto() {
        menus.add(new Menu(CMDMenu.EXECUTE, "Nâng cấp", () -> {
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Thường", () -> {
                getService().openUI((byte) 10);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Cẩn thận", () -> {
                getService().openUI((byte) 31);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Hướng dẫn", () -> {
                getService().npcChat(NpcName.KENSHINTO, "Bỏ Trang bị và Đá vào trong khung để nâng cấp, Khi nâng cấp cẩn thận thì phải có lượng.");
            }));
            getService().openUIMenu();
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Luyện đá", () -> {
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Yên", () -> {
                getService().openUI((byte) 12);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Xu", () -> {
                getService().openUI((byte) 11);

            }));
            getService().openUIMenu();
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Tách", () -> {
            getService().openUI((byte) 13);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Chuyển hóa", () -> {
            getService().openUI((byte) 33);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Luyện ngọc", () -> {
            getService().openUI((byte) 46);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Khảm", () -> {
            getService().openUI((byte) 47);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Gọt ngọc", () -> {
            getService().openUI((byte) 49);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Tháo ngọc", () -> {
            getService().openUI((byte) 50);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Nói chuyện", () -> {
            if (taskId == TaskName.NHIEM_VU_CHAO_LANG && taskMain != null && taskMain.index == 2) {
                taskNext();
            }
            String talk = (String) NinjaUtils.randomObject("Ngươi muốn cải tiến trang bị?", "Nâng cấp trang bị: Uy tín, giá cả phải chăng");
            getService().npcChat(NpcName.KENSHINTO, talk);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Khai Hoá", () -> {
            if (!isHuman) {
                warningClone();
                return;
            }
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Thăng Ngưỡng Khai Hoá", () -> {
                setCommandBox(THANG_NGUONG_KHAI_HOA);
                getService().openThangNguong();
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Thăng Cấp Khai Hoá", () -> {
                setCommandBox(THANG_CAP_KHAI_HOA);
                getService().openThangCap();
            }));
            getService().openUIMenu();
        }));

    }

    public void npcFuroya() {
        menus.add(new Menu(CMDMenu.EXECUTE, "Y phục", () -> {
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Nón", () -> {
                if (this.gender == 1) {
                    getService().openUI((byte) StoreManager.TYPE_MEN_HAT);
                } else {
                    getService().openUI((byte) StoreManager.TYPE_WOMEN_HAT);
                }
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Áo", () -> {
                if (this.gender == 1) {
                    getService().openUI((byte) StoreManager.TYPE_MEN_SHIRT);
                } else {
                    getService().openUI((byte) StoreManager.TYPE_WOMEN_SHIRT);
                }
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Găng tay", () -> {
                if (this.gender == 1) {
                    getService().openUI((byte) StoreManager.TYPE_MEN_GLOVES);
                } else {
                    getService().openUI((byte) StoreManager.TYPE_WOMEN_GLOVES);
                }
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Quần", () -> {
                if (this.gender == 1) {
                    getService().openUI((byte) StoreManager.TYPE_MEN_PANT);
                } else {
                    getService().openUI((byte) StoreManager.TYPE_WOMEN_PANT);
                }
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Giày", () -> {
                if (this.gender == 1) {
                    getService().openUI((byte) StoreManager.TYPE_MEN_SHOES);
                } else {
                    getService().openUI((byte) StoreManager.TYPE_WOMEN_SHOES);
                }
            }));
            getService().openUIMenu();
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Nói chuyện", () -> {
            String talk = (String) NinjaUtils.randomObject("Ngưới cần giày, giáp sắt, quần áo?",
                    "Giáp, giày giá rẻ đây!", "Không mặc giáp mua từ ta, ra khỏi trường ngươi sẽ gặp nguy hiểm.");
            getService().npcChat(NpcName.FUROYA, talk);
        }));
    }

    public void confirmID(Message ms) {
        try {
            if (trade != null) {
                warningTrade();
                return;
            }
            if (confirmPopup == null) {
                return;
            }
            try {
                byte id = ms.reader().readByte();
                if (confirmPopup.getId() != id) {
                    return;
                }
                if (!isHuman && id != CMDConfirmPopup.NANG_BI_KIP && id != CMDConfirmPopup.NANG_rb) {
                    return;
                }
                switch (id) {
                    case CMDConfirmPopup.CONFIRM:
                        confirmPopup.confirm();
                        break;
                    case CMDConfirmPopup.NANG_MAT:
                    case CMDConfirmPopup.NANG_MAT_VIP:
                        if (equipment[14] == null) {
                            serverMessage("Hãy sử dụng Mắt để sử dụng chức năng này.");
                            return;
                        }
                        if (equipment[14].upgrade >= 10) {
                            serverMessage("Mắt đã đạt cáp tối đa.");
                            return;
                        }
                        boolean isGold = id == CMDConfirmPopup.NANG_MAT_VIP;
                        int[] costs = {500000, 750000, 1250000, 2000000, 3000000, 4500000, 6000000, 7750000, 10000000};
                        int[] costs2 = {50, 60, 85, 100, 150, 200, 300, 450, 600};
                        int[] percents = {50, 35, 25, 20, 15, 12, 10, 8, 5};
                        int[] items = {695, 696, 697, 698, 699, 700, 701, 702, 703};
                        int point = (equipment[14].upgrade + 1) * 100;
                        if (pointAo < point || pointGangTay < point || pointGiay < point || pointNgocBoi < point || pointLien < point || pointNhan < point || pointNon < point || pointPhu < point || pointQuan < point || pointVuKhi < point) {
                            serverMessage("Không đủ " + point + " điểm danh vọng mỗi loại.");
                            return;
                        }
                        int index = equipment[14].upgrade - 1;
                        int indexItem = getIndexItemByIdInBag(items[index], true);
                        if (indexItem == -1) {
                            indexItem = getIndexItemByIdInBag(items[index], false);
                        }
                        if (indexItem == -1) {
                            ItemTemplate template = ItemManager.getInstance().getItemTemplate(items[index]);
                            serverMessage("Yêu cầu phải có 10 viên " + template.name);
                            return;
                        }
                        if (getSlotNull() == 0) {
                            warningBagFull();
                            return;
                        }
                        Item item = this.bag[indexItem];
                        if (!item.has(10)) {
                            serverMessage("Yêu cầu phải có 10 viên " + item.template.name);
                            return;
                        }
                        int cost = costs[index];
                        if (NinjaUtils.sum(this.yen, this.coin) < cost) {
                            serverMessage("Không đủ xu hoặc yên để nâng cấp Mắt");
                            return;
                        }
                        int percent = percents[index];
                        if (isGold) {
                            if (user.gold < costs2[index]) {
                                serverMessage("Không đủ lượng nâng cấp Mắt");
                                return;
                            }
                            percent *= 2;
                            addGold(-costs2[index]);
                        }
                        int rand = (int)NinjaUtils.nextInt(100);
                        if (rand < percent) {
                            getService().deleteItemBody(equipment[14].template.type);
                            Item itm = ItemFactory.getInstance().newItem(equipment[14].id + 1);
                            itm.isLock = true;
                            addItemToBag(itm);
                            equipment[14] = null;
                        } else {
                            serverMessage("Nâng cấp thất bại.");
                        }
                        if (this.yen < cost) {
                            addCoin(-(cost - this.yen));
                            addYen(-this.yen);
                        } else {
                            addYen(-cost);
                        }
                        removeItem(indexItem, 10, true);
                        break;
                    case CMDConfirmPopup.NANG_BI_KIP:
                        int itemID = -1;
                        switch (getSys()) {
                            case 1:
                                itemID = 834;
                                break;

                            case 2:
                                itemID = 836;
                                break;

                            case 3:
                                itemID = 835;
                                break;
                        }
                        if (itemID == -1) {
                            return;
                        }
                        Item item2 = this.equipment[ItemTemplate.TYPE_BIKIP];
                        if (item2 != null) {
                            if (item2.upgrade >= 16) {
                                getService().npcChat(NpcName.TASHINO, "Bí kíp của ngươi đã quá mạnh, ta không thể giúp được ngươi.");
                                return;
                            }
                            String name = ItemManager.getInstance().getItemName(itemID);
                            int indexItem2 = this.getIndexItemByIdInBag(itemID);
                            int[] quantity = {1, 3, 5, 7, 10, 12, 15, 17, 20, 22, 25, 27, 30, 35, 40, 50};
                            if (indexItem2 == -1 || this.bag[indexItem2] == null || quantity[item2.upgrade] > this.bag[indexItem2].getQuantity()) {
                                getService().npcChat(NpcName.TASHINO, String.format("Hãy mang %d viên %s đưa cho ta, ta sẽ giúp.", quantity[item2.upgrade], name));
                                return;
                            }
                            int[] fee = {100, 300, 500, 700, 1000, 1200, 1500, 1700, 2000, 2200, 2500, 2700, 3000, 3500, 4000, 5000};
                            if (fee[item2.upgrade] > user.gold) {
                                getService().npcChat(NpcName.TASHINO, String.format("Muốn ta giúp ngươi phải đưa ta %d lượng.", fee[item2.upgrade]));
                                return;
                            }
                            if (getSlotNull() == 0) {
                                getService().npcChat(NpcName.TASHINO, "Hãy chừa ra 1 chỗ trống trong hành trang của ngươi.");
                                return;
                            }
                            addGold(-fee[item2.upgrade]);
                            removeItem(indexItem2, quantity[item2.upgrade], true);
                            int[] percent2 = {100, 90, 80, 70, 60, 50, 40, 30, 25, 20, 15, 10, 8, 5, 3, 1};
                            if (percent2[item2.upgrade] >= (int)NinjaUtils.nextInt(100)) {
                                Item newItem = ItemFactory.getInstance().newItem(item2.id);
                                newItem.upgrade = (byte) (item2.upgrade + 1);
                                newItem.isLock = true;
                                for (ItemOption option : item2.options) {
                                    switch (option.optionTemplate.id) {
                                        case 87:
                                        case 88:
                                        case 89:
                                        case 90:
                                        case 82:
                                        case 83:
                                            option.param += 250;
                                            break;
                                        case 81:
                                        case 84:
                                        case 86:
                                        case 91:
                                        case 92:
                                        case 94:
                                        case 95:
                                        case 96:
                                        case 97:
                                            option.param += 5;
                                            break;
                                        case 99:
                                            option.param += 10;
                                            break;
                                        case 100:
                                            option.param += 1;
                                            break;
                                        default:
                                            option.param += option.param / 10;
                                            break;
                                    }
                                    newItem.options.add(option);
                                }
                                this.equipment[ItemTemplate.TYPE_BIKIP] = null;
                                getService().deleteItemBody(ItemTemplate.TYPE_BIKIP);
                                addItemToBag(newItem);
                                setFashion();
                                setAbility();
                                String talk = (String) NinjaUtils.randomObject("Chúc mừng ngươi, hãy tận hưởng những sức mạnh đó", "Thành công rồi, ngươi thấy sao", "Thành công rồi,Hãy xem thử, bí kíp của ngươi có vẻ đã mạnh hơn");
                                getService().npcChat(NpcName.TASHINO, talk);
                            } else {
                                String talk = (String) NinjaUtils.randomObject("Thất bại rồi ,Ta đã cố gắng hết sức", "Ta xin lỗi, lần sau ta sẽ cố gắng hơn nữa", "Thất bại rồi , Sức mạnh quá lớn, ta không thể kiểm soát được nó");
                                getService().npcChat(NpcName.TASHINO, talk);
                            }
                        }
                        break;

                    case CMDConfirmPopup.NANG_rb:
                        Item item4 = this.fashion[14];
                        if (item4 != null) {
                            if (item4.upgrade >= 16) {
                                getService().npcChat(NpcName.ruby, "Bí kíp của ngươi đã quá mạnh, ta không thể giúp được ngươi.");
                                return;
                            }
                            int[] fee = {100, 300, 500, 700, 1000, 1200, 1500, 1700, 2000, 2200, 2500, 2700, 3000, 3500, 4000, 5000};
                            if (fee[item4.upgrade] > user.gold) {
                                getService().npcChat(NpcName.ruby, String.format("Muốn ta giúp ngươi phải đưa ta %d lượng.", fee[item4.upgrade]));
                                return;
                            }
                            if (getSlotNull() == 0) {
                                getService().npcChat(NpcName.ruby, "Hãy chừa ra 1 chỗ trống trong hành trang của ngươi.");
                                return;
                            }
                            addGold(-fee[item4.upgrade]);
                            int[] percent2 = {100, 90, 80, 70, 60, 50, 40, 30, 25, 20, 15, 10, 8, 5, 3, 1};
                            if (percent2[item4.upgrade] >= (int)NinjaUtils.nextInt(100)) {
                                Item newItem = ItemFactory.getInstance().newItem(item4.id);
                                newItem.upgrade = (byte) (item4.upgrade + 1);
                                newItem.isLock = true;
                                for (ItemOption option : item4.options) {
                                    switch (option.optionTemplate.id) {
                                        case 87:
                                        case 88:
                                        case 89:
                                        case 90:
                                        case 82:
                                        case 83:
                                            option.param += 250;
                                            break;
                                        case 81:
                                        case 84:
                                        case 86:
                                        case 91:
                                        case 92:
                                        case 94:
                                        case 95:
                                        case 96:
                                        case 97:
                                            option.param += 5;
                                            break;
                                        case 99:
                                            option.param += 10;
                                            break;
                                        case 100:
                                            option.param += 1;
                                            break;
                                        default:
                                            option.param += option.param / 10;
                                            break;
                                    }
                                    newItem.options.add(option);
                                }
                                this.fashion[14] = null;
                                getService().deleteItemBody(14);
                                addItemToBag(newItem);
                                setFashion();
                                setAbility();
                                String talk = (String) NinjaUtils.randomObject("Chúc mừng ngươi, hãy tận hưởng những sức mạnh đó", "Thành công rồi, ngươi thấy sao", "Thành công rồi,Hãy xem thử, bí kíp của ngươi có vẻ đã mạnh hơn");
                                getService().npcChat(NpcName.ruby, talk);
                            } else {
                                String talk = (String) NinjaUtils.randomObject("Thất bại rồi ,Ta đã cố gắng hết sức", "Ta xin lỗi, lần sau ta sẽ cố gắng hơn nữa", "Thất bại rồi , Sức mạnh quá lớn, ta không thể kiểm soát được nó");
                                getService().npcChat(NpcName.ruby, talk);
                            }
                        }
                        break;
                    
                    case CMDConfirmPopup.NANG_THU_NUOI:
                        Item item5 = this.equipment[ItemTemplate.TYPE_THUNUOI];
                        if (item5 != null) {
                            if (item5.upgrade >= 10) {
                                getService().npcChat(NpcName.NPCThuNuoi, "Thú nuôi của ngươi đã quá mạnh, ta không thể giúp được ngươi.");
                                return;
                            }
                            if(item5.expire > 0){
                             getService().npcChat(NpcName.NPCThuNuoi, "Không thể nâng thú nuôi có hạn được !.");
                                return;   
                            }
                            int[] fee = {100, 300, 500, 700, 1000, 1200, 1500, 1700, 2000, 2200, 2500, 2700, 3000, 3500, 4000, 5000};
                            if (fee[item5.upgrade] > user.gold) {
                                getService().npcChat(NpcName.NPCThuNuoi, String.format("Muốn ta giúp ngươi phải đưa ta %d lượng.", fee[item5.upgrade]));
                                return;
                            }
                            if (getSlotNull() == 0) {
                                getService().npcChat(NpcName.NPCThuNuoi, "Hãy chừa ra 1 chỗ trống trong hành trang của ngươi.");
                                return;
                            }
                            addGold(-fee[item5.upgrade]);
                            int[] percent2 = {100, 90, 80, 70, 60, 50, 40, 30, 25, 20, 15, 10, 8, 5, 3, 1};
                            if (percent2[item5.upgrade] >= (int)NinjaUtils.nextInt(100)) {
                                Item newItem = ItemFactory.getInstance().newItem(item5.id);
                                newItem.upgrade = (byte) (item5.upgrade + 1);
                                newItem.isLock = true;
                                newItem.options.clear();
                                for (ItemOption option : item5.options) {
                                    switch (option.optionTemplate.id) {
                                        case 73:
                                        case 6:
                                            option.param += 250;
                                            break;
                                        case 14:
                                        case 94:
                                        case 10:
                                        case 84:
                                            option.param += 5;
                                            break;
                                        case 2:
                                        case 3:
                                        case 4:
                                            option.param += 10;
                                            break;
                                        case 88:
                                        case 89:
                                        case 90:
                                            option.param += 100;
                                            break;
                                        default: 
                                            option.param += option.param / 10;
                                            break;
                                    }
                                    newItem.options.add(option);
                                }
                                this.equipment[ItemTemplate.TYPE_THUNUOI] = null;
                                getService().deleteItemBody(10);
                                addItemToBag(newItem);
                                setFashion();
                                setAbility();
                                String talk = (String) NinjaUtils.randomObject("Chúc mừng ngươi, hãy tận hưởng những sức mạnh đó", "Thành công rồi, ngươi thấy sao", "Thành công rồi,Hãy xem thử, bí kíp của ngươi có vẻ đã mạnh hơn");
                                getService().npcChat(NpcName.NPCThuNuoi, talk);
                            } else {
                                String talk = (String) NinjaUtils.randomObject("Thất bại rồi ,Ta đã cố gắng hết sức", "Ta xin lỗi, lần sau ta sẽ cố gắng hơn nữa", "Thất bại rồi , Sức mạnh quá lớn, ta không thể kiểm soát được nó");
                                getService().npcChat(NpcName.NPCThuNuoi, talk);
                            }
                        }
                        break;    
                        
                    case CMDConfirmPopup.NHIEM_VU_DANH_VONG:
                        if (this.gloryTask != null) {
                            String title = this.gloryTask.getTaskTitle();
                            getService().showAlert("Thông báo", "Đã hủy nhiệm vụ " + title);
                            this.gloryTask = null;
                        } else {
                            serverMessage("Hiện tại con chưa nhận nhiệm vụ nào");
                        }
                        break;
                    case CMDConfirmPopup.UPGRADE_SOUL_STONE:
                        this.upgradeSoulStone();
                        break;
                    case CMDConfirmPopup.NANG_AN_TOC:
                        this.upgradeAnToc();
                        break;
                    case CMDConfirmPopup.THANG_NGUONG_KHAI_HOA: {
                        item = this.equipment[tempIndex];
                        if (item != null) {
                            int nguongkhaihoa = 0;
                            for (ItemOption option : item.options) {
                                if (option.optionTemplate.id == 156) {
                                    nguongkhaihoa = option.param;
                                    break;
                                }
                            }
                            if (nguongkhaihoa >= 9) {
                                serverDialog("Ngưỡng Khai Hoá đã đạt tối đa !");
                                return;
                            }
                            int[] xu = new int[]{1000000, 1500000, 2000000, 3000000, 4000000, 5000000, 7000000, 10000000, 15000000};
                            int[] luong = new int[]{1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 10000};
                            if (this.coin < xu[nguongkhaihoa]) {
                                serverDialog("Bạn không đủ xu!");
                                return;
                            }
                            if (this.user.gold < luong[nguongkhaihoa]) {
                                serverDialog("Bạn không đủ Lượng!");
                                return;
                            }
                            addCoin(-xu[nguongkhaihoa]);
                            addGold(-luong[nguongkhaihoa]);
                            int[] tile = new int[]{70, 50, 40, 30, 25, 20, 10, 7, 3};
                            if ((int)NinjaUtils.nextInt(1, 100) > tile[nguongkhaihoa]) {
                                serverMessage("Thăng Ngưỡng Thất Bại");
                            } else {
                                boolean param156 = false;
                                for (ItemOption option : item.options) {
                                    if (option.optionTemplate.id == 156) {
                                        option.param += 1;
                                        param156 = true;
                                        break;
                                    }
                                }
                                if (!param156) {
                                    item.options.add(new ItemOption(156, 1));// nguong khai hoa
                                    item.options.add(new ItemOption(157, 0));// cấp khai hóa 
                                }
                                serverMessage("Thăng Ngưỡng Thành công");
                                service.updateInfoMe();
                                service.openThangNguong();
                            }
                        }
                        break;
                    }
                    case CMDConfirmPopup.THANG_CAP_KHAI_HOA170: {
                        item = this.equipment[tempIndex];
                        if (item != null) {
                            for (ItemOption option : item.options) {
                                switch (option.optionTemplate.id) {
                                    case 157:
                                        int capkhaihoa = option.param;
                                        int indexSM = getIndexItemByIdInBag(ItemName.SM);
                                        int[] xu = new int[]{1000000, 1500000, 2000000, 3000000, 4000000, 5000000, 7000000, 10000000, 20000000};
                                        int[] luong = new int[]{500, 1000, 1500, 2000, 3000, 4000, 5000, 7000, 10000};
                                        int[] da = new int[]{5, 10, 20, 30, 40, 50, 60, 70, 80};
                                        if (this.coin < xu[capkhaihoa]) {
                                            serverDialog("Bạn không đủ Xu!");
                                            return;
                                        }
                                        if (this.user.gold < luong[capkhaihoa]) {
                                            serverDialog("Bạn không đủ Lượng!");
                                            return;
                                        }
                                        if (indexSM == -1 || bag[indexSM] == null || bag[indexSM].getQuantity() < da[capkhaihoa]) {
                                            serverDialog("Bạn không đủ đá Sinh Mệnh!");
                                            return;
                                        }
                                        removeItem(indexSM, da[capkhaihoa], true);
                                        addCoin(-xu[capkhaihoa]);
                                        addGold(-luong[capkhaihoa]);
                                        int[] tile = new int[]{70, 50, 40, 30, 25, 20, 10, 5, 2};
                                        if ((int)NinjaUtils.nextInt(1, 100) > tile[capkhaihoa]) {
                                            serverMessage("Thăng Cấp Khai Hoá Thất Bại");
                                        } else {
                                            option.param++;
                                            for (ItemOption option1 : item.options) {
                                                switch (option1.optionTemplate.id) {
                                                    case 158:
                                                    case 159:
                                                    case 168:
                                                        option1.param += 100;
                                                        break;
                                                    case 155:
                                                    case 160:
                                                        option1.param += 1;
                                                        break;
                                                    case 161:
                                                    case 162:
                                                    case 163:
                                                    case 164:
                                                    case 165:
                                                    case 166:
                                                        option1.param += 50;
                                                        break;
                                                    case 169:
                                                        option1.param += 2;
                                                        break;
                                                    case 167:
                                                        option1.param += 10;
                                                        break;
                                                }
                                            }
                                            service.updateInfoMe();
                                            service.openThangCap();
                                            serverMessage("Thăng Cấp Khai Hoá Thành công");
                                        }
                                }
                            }
                        }
                        break;
                    }

                    case CMDConfirmPopup.THANG_CAP_KHAI_HOA171: {
                        item = this.equipment[tempIndex];
                        if (item != null) {
                            for (ItemOption option : item.options) {
                                switch (option.optionTemplate.id) {
                                    case 157:
                                        int capkhaihoa = option.param;
                                        int indexPN = getIndexItemByIdInBag(ItemName.PN);
                                        int[] xu = new int[]{1000000, 1500000, 2000000, 3000000, 4000000, 5000000, 7000000, 10000000, 20000000};
                                        int[] luong = new int[]{500, 1000, 1500, 2000, 3000, 4000, 5000, 7000, 10000};
                                        int[] da = new int[]{5, 10, 20, 30, 40, 50, 60, 70, 80};
                                        if (this.coin < xu[capkhaihoa]) {
                                            serverDialog("Bạn không đủ Xu!");
                                            return;
                                        }
                                        if (this.user.gold < luong[capkhaihoa]) {
                                            serverDialog("Bạn không đủ Lượng!");
                                            return;
                                        }
                                        if (indexPN == -1 || bag[indexPN] == null || bag[indexPN].getQuantity() < da[capkhaihoa]) {
                                            serverDialog("Bạn không đủ đá Phòng Ngự!");
                                            return;
                                        }
                                        removeItem(indexPN, da[capkhaihoa], true);
                                        addCoin(-xu[capkhaihoa]);
                                        addGold(-luong[capkhaihoa]);
                                        int[] tile = new int[]{70, 50, 40, 30, 25, 20, 10, 5, 2};
                                        if ((int)NinjaUtils.nextInt(1, 100) > tile[capkhaihoa]) {
                                            serverMessage("Thăng Cấp Khai Hoá Thất Bại");
                                        } else {
                                            option.param++;
                                            for (ItemOption option1 : item.options) {
                                                switch (option1.optionTemplate.id) {
                                                    case 158:
                                                    case 159:
                                                    case 168:
                                                        option1.param += 100;
                                                        break;
                                                    case 160:
                                                        option1.param += 1;
                                                        break;
                                                    case 161:
                                                    case 162:
                                                    case 163:
                                                    case 164:
                                                    case 165:
                                                    case 166:
                                                        option1.param += 50;
                                                        break;
                                                    case 169:
                                                        option1.param += 2;
                                                        break;
                                                    case 167:
                                                        option1.param += 10;
                                                        break;
                                                }
                                            }
                                            service.updateInfoMe();
                                            service.openThangCap();
                                            serverMessage("Thăng Cấp Khai Hoá Thành công");
                                        }
                                }
                            }
                        }
                        break;
                    }
                    case CMDConfirmPopup.THANG_CAP_KHAI_HOA172: {
                        item = this.equipment[tempIndex];
                        if (item != null) {
                            for (ItemOption option : item.options) {
                                switch (option.optionTemplate.id) {
                                    case 157:
                                        int capkhaihoa = option.param;
                                        int indexST = getIndexItemByIdInBag(ItemName.ST);
                                        int[] xu = new int[]{1000000, 1500000, 2000000, 3000000, 4000000, 5000000, 7000000, 10000000, 20000000};
                                        int[] luong = new int[]{500, 1000, 1500, 2000, 3000, 4000, 5000, 7000, 10000};
                                        int[] da = new int[]{5, 10, 20, 30, 40, 50, 60, 70, 80};
                                        if (this.coin < xu[capkhaihoa]) {
                                            serverDialog("Bạn không đủ Xu!");
                                            return;
                                        }
                                        if (this.user.gold < luong[capkhaihoa]) {
                                            serverDialog("Bạn không đủ Lượng!");
                                            return;
                                        }
                                        if (indexST == -1 || bag[indexST] == null || bag[indexST].getQuantity() < da[capkhaihoa]) {
                                            serverDialog("Bạn không đủ đá Sát Thương!");
                                            return;
                                        }
                                        removeItem(indexST, da[capkhaihoa], true);
                                        addCoin(-xu[capkhaihoa]);
                                        addGold(-luong[capkhaihoa]);
                                        int[] tile = new int[]{70, 50, 40, 30, 25, 20, 10, 5, 2};
                                        if ((int)NinjaUtils.nextInt(1, 100) > tile[capkhaihoa]) {
                                            serverMessage("Thăng Cấp Khai Hoá Thất Bại");
                                        } else {
                                            option.param++;
                                            for (ItemOption option1 : item.options) {
                                                switch (option1.optionTemplate.id) {
                                                    case 158:
                                                    case 159:
                                                    case 168:
                                                        option1.param += 100;
                                                        break;
                                                    case 160:
                                                        option1.param += 1;
                                                        break;
                                                    case 161:
                                                    case 162:
                                                    case 163:
                                                    case 164:
                                                    case 165:
                                                    case 166:
                                                        option1.param += 50;
                                                        break;
                                                    case 169:
                                                        option1.param += 2;
                                                        break;
                                                    case 167:
                                                        option1.param += 10;
                                                        break;
                                                }
                                            }
                                            service.updateInfoMe();
                                            service.openThangCap();
                                            serverMessage("Thăng Cấp Khai Hoá Thành công");
                                        }
                                }
                            }
                        }
                        break;
                    }

                    case CMDConfirmPopup.THANG_CAP_KHAI_HOA_1: {
                        item = this.equipment[tempIndex];
                        if (item != null) {
                            int capthangnguong = 0;
                            for (ItemOption option : item.options) {
                                if (option.optionTemplate.id == 157) {
                                    int capkhaihoa = option.param;
                                    int indexST = getIndexItemByIdInBag(ItemName.ST);
                                    int[] xu = new int[]{1000000, 1500000, 2000000, 3000000, 4000000, 5000000, 7000000, 10000000, 20000000};
                                    int[] luong = new int[]{500, 1000, 1500, 2000, 3000, 4000, 5000, 7000, 10000};
                                    int[] da = new int[]{5, 10, 20, 30, 40, 50, 60, 70, 80};
                                    if (this.coin < xu[capkhaihoa]) {
                                        serverDialog("Bạn không đủ Xu!");
                                        return;
                                    }
                                    if (this.user.gold < luong[capkhaihoa]) {
                                        serverDialog("Bạn không đủ Lượng!");
                                        return;
                                    }
                                    if (indexST == -1 || bag[indexST] == null || bag[indexST].getQuantity() < da[capkhaihoa]) {
                                        serverDialog("Bạn không đủ đá Sát Thương!");
                                        return;
                                    }
                                    removeItem(indexST, da[capkhaihoa], true);
                                    addCoin(-xu[capkhaihoa]);
                                    addGold(-luong[capkhaihoa]);
                                    int[] tile = new int[]{70, 50, 40, 30, 25, 20, 10, 5, 2};
                                    if ((int)NinjaUtils.nextInt(1, 100) > tile[capthangnguong]) {
                                        serverMessage("Thăng Cấp Khai Hoá Thất Bại");
                                    } else {
                                        option.param++;
                                        boolean flag = false;
                                        for (ItemOption option1 : item.options) {
                                            if (option1.optionTemplate.id == 172) {
                                                flag = true;
                                                break;
                                            }
                                        }
                                        if (!flag) { //sát thương
                                            item.options.add(new ItemOption(172, 0));
                                            item.options.add(new ItemOption(166, 100));
                                            item.options.add(new ItemOption(168, 500));
                                            item.options.add(new ItemOption(167, 10));
                                            item.options.add(new ItemOption(169, 10));
                                        }
                                        service.updateInfoMe();
                                        service.openThangCap();
                                        serverMessage("Thăng Cấp Khai Hoá Thành công");
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                    }
                    case CMDConfirmPopup.THANG_CAP_KHAI_HOA_2: {
                        item = this.equipment[tempIndex];
                        if (item != null) {
                            int capthangnguong = 0;
                            for (ItemOption option : item.options) {
                                if (option.optionTemplate.id == 157) {
                                    int capkhaihoa = option.param;
                                    int indexPN = getIndexItemByIdInBag(ItemName.PN);
                                    int[] xu = new int[]{1000000, 1500000, 2000000, 3000000, 4000000, 5000000, 7000000, 10000000, 20000000};
                                    int[] luong = new int[]{500, 1000, 1500, 2000, 3000, 4000, 5000, 7000, 10000};
                                    int[] da = new int[]{5, 10, 20, 30, 40, 50, 60, 70, 80};
                                    if (this.coin < xu[capkhaihoa]) {
                                        serverDialog("Bạn không đủ Xu!");
                                        return;
                                    }
                                    if (this.user.gold < luong[capkhaihoa]) {
                                        serverDialog("Bạn không đủ Lượng!");
                                        return;
                                    }
                                    if (indexPN == -1 || bag[indexPN] == null || bag[indexPN].getQuantity() < da[capkhaihoa]) {
                                        serverDialog("Bạn không đủ đá Phòng Ngự!");
                                        return;
                                    }
                                    removeItem(indexPN, da[capkhaihoa], true);
                                    addCoin(-xu[capkhaihoa]);
                                    addGold(-luong[capkhaihoa]);
                                    int[] tile = new int[]{70, 50, 40, 30, 25, 20, 10, 5, 2};
                                    if ((int)NinjaUtils.nextInt(1, 100) > tile[capthangnguong]) {
                                        serverMessage("Thăng Cấp Khai Hoá Thất Bại");
                                    } else {
                                        option.param++;
                                        boolean flag = false;
                                        for (ItemOption option1 : item.options) {
                                            if (option1.optionTemplate.id == 171) {
                                                flag = true;
                                                break;
                                            }
                                        }
                                        if (!flag) { //phòng ngự
                                            item.options.add(new ItemOption(171, 0));
                                            item.options.add(new ItemOption(162, 100));
                                            item.options.add(new ItemOption(163, 50));
                                            item.options.add(new ItemOption(164, 50));
                                            item.options.add(new ItemOption(165, 50));
                                        }
                                        service.updateInfoMe();
                                        service.openThangCap();
                                        serverMessage("Thăng Cấp Khai Hoá Thành công");
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                    }
                    case CMDConfirmPopup.THANG_CAP_KHAI_HOA_3: {
                        item = this.equipment[tempIndex];
                        if (item != null) {
                            int capthangnguong = 0;
                            for (ItemOption option : item.options) {
                                if (option.optionTemplate.id == 157) {
                                    int capkhaihoa = option.param;
                                    int indexSM = getIndexItemByIdInBag(ItemName.SM);
                                    int[] xu = new int[]{1000000, 1500000, 2000000, 3000000, 4000000, 5000000, 7000000, 10000000, 20000000};
                                    int[] luong = new int[]{500, 1000, 1500, 2000, 3000, 4000, 5000, 7000, 10000};
                                    int[] da = new int[]{5, 10, 20, 30, 40, 50, 60, 70, 80};
                                    if (this.coin < xu[capkhaihoa]) {
                                        serverDialog("Bạn không đủ Xu!");
                                        return;
                                    }
                                    if (this.user.gold < luong[capkhaihoa]) {
                                        serverDialog("Bạn không đủ Lượng!");
                                        return;
                                    }
                                    if (indexSM == -1 || bag[indexSM] == null || bag[indexSM].getQuantity() < da[capkhaihoa]) {
                                        serverDialog("Bạn không đủ đá Sinh Mệnh!");
                                        return;
                                    }
                                    removeItem(indexSM, da[capkhaihoa], true);
                                    addCoin(-xu[capkhaihoa]);
                                    addGold(-luong[capkhaihoa]);
                                    int[] tile = new int[]{70, 50, 40, 30, 25, 20, 10, 5, 2};
                                    if ((int)NinjaUtils.nextInt(1, 100) > tile[capthangnguong]) {
                                        serverMessage("Thăng Cấp Khai Hoá Thất Bại");
                                    } else {
                                        option.param++;
                                        boolean flag = false;
                                        for (ItemOption option1 : item.options) {
                                            if (option1.optionTemplate.id == 170) {
                                                flag = true;
                                                break;
                                            }
                                        }
                                        if (!flag) { //sinh mệnh
                                            item.options.add(new ItemOption(170, 0));
                                            item.options.add(new ItemOption(158, 200));
                                            item.options.add(new ItemOption(159, 200));
                                            item.options.add(new ItemOption(160, 5));
                                            item.options.add(new ItemOption(161, 10));
                                        }
                                        service.updateInfoMe();
                                        service.openThangCap();
                                        serverMessage("Thăng Cấp Khai Hoá Thành công");
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            } finally {
                confirmPopup = null;
            }
        } catch (Exception e) {
            Log.error("err: " + e.getMessage(), e);
        }
    }

    public void npcTienNu() {
        if (Event.getEvent() != null) {
            Event.getEvent().menu(this);
        } else {
            menus.add(new Menu(CMDMenu.EXECUTE, "Nói chuyện", () -> {
                getService().npcChat(NpcName.TIEN_NU, "Hãy đón chờ các sự kiện tiếp theo nhé.");
            }));
        }
    }

    public void npcBaRei() {
        menus.add(new Menu(CMDMenu.EXECUTE, "Bán cá", () -> {
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Xương cá", () -> {
                ItemTemplate template = ItemManager.getInstance().getItemTemplate(895);
                InputDialog input = new InputDialog(CMDInputDialog.XUONG_CA, template.name);
                setInput(input);
                getService().showInputDialog();
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Giao lục", () -> {
                ItemTemplate template = ItemManager.getInstance().getItemTemplate(896);
                InputDialog input = new InputDialog(CMDInputDialog.GIAO_LUC, template.name);
                setInput(input);
                getService().showInputDialog();
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Cá Koi", () -> {
                ItemTemplate template = ItemManager.getInstance().getItemTemplate(897);
                InputDialog input = new InputDialog(CMDInputDialog.CA_KOI, template.name);
                setInput(input);
                getService().showInputDialog();
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Cá đĩa lam", () -> {
                ItemTemplate template = ItemManager.getInstance().getItemTemplate(898);
                InputDialog input = new InputDialog(CMDInputDialog.CA_DIA_LAM, template.name);
                setInput(input);
                getService().showInputDialog();
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Buồm tím", () -> {
                ItemTemplate template = ItemManager.getInstance().getItemTemplate(899);
                InputDialog input = new InputDialog(CMDInputDialog.BUOM_TIM, template.name);
                setInput(input);
                getService().showInputDialog();
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Sao biển xanh", () -> {
                ItemTemplate template = ItemManager.getInstance().getItemTemplate(900);
                InputDialog input = new InputDialog(CMDInputDialog.SAO_BIEN_XANH, template.name);
                setInput(input);
                getService().showInputDialog();
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Cua hoàng đế", () -> {
                ItemTemplate template = ItemManager.getInstance().getItemTemplate(901);
                InputDialog input = new InputDialog(CMDInputDialog.CUA_HOANG_DE, template.name);
                setInput(input);
                getService().showInputDialog();
            }));
            getService().openUIMenu();
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Nói chuyện", () -> {
            getService().npcChat(NpcName.BA_REI, "Làng chài là một trong những làng quan trọng nhất thế giới Ninja.");
        }));

    }

    public void npcMatsurugi() {
        if (taskMain != null && taskMain.taskId == TaskName.NV_TRUY_TIM_DIA_DO && taskMain.index == 1) {
            menus.add(new Menu(CMDMenu.EXECUTE, "Nhận chìa khóa", () -> {
                int index = getIndexItemByIdInBag(ItemName.CHIA_KHOA_CO_QUAN2);
                if (index != -1) {
                    removeItem(index, 1, true);
                }
                if (getSlotNull() == 0) {
                    warningBagFull();
                    return;
                }
                Item chiaKhoa = ItemFactory.getInstance().newItem(ItemName.CHIA_KHOA_CO_QUAN2);
                chiaKhoa.isLock = true;
                chiaKhoa.setQuantity(1);
                addItemToBag(chiaKhoa);
            }));
        }
        menus.add(new Menu(CMDMenu.EXECUTE, "Nói chuyện", () -> {

        }));
    }

    public void npcShoba() {
        if (taskMain != null && taskMain.taskId == TaskName.NV_REN_LUYEN_Y_CHI && taskMain.index == 1) {
            menus.add(new Menu(CMDMenu.EXECUTE, "Nhận cần câu", () -> {
                int index = getIndexItemByIdInBag(ItemName.CAN_CAU);
                if (index != -1) {
                    removeItem(index, 1, true);
                }
                if (getSlotNull() == 0) {
                    warningBagFull();
                    return;
                }
                Item canCau = ItemFactory.getInstance().newItem(ItemName.CAN_CAU);
                canCau.isLock = true;
                canCau.setQuantity(1);
                addItemToBag(canCau);
            }));
        }
        menus.add(new Menu(CMDMenu.EXECUTE, "Nói chuyện", () -> {

        }));
    }

    public void npcAmeji() {
        menus.add(new Menu(CMDMenu.EXECUTE, "Trang sức", () -> {
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Liên", () -> {
                getService().openUI((byte) StoreManager.TYPE_NECKLACE);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Nhẫn", () -> {
                getService().openUI((byte) StoreManager.TYPE_RING);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Ngọc bội", () -> {
                getService().openUI((byte) StoreManager.TYPE_PEARL);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Phù", () -> {
                getService().openUI((byte) StoreManager.TYPE_SPELL);
            }));
            getService().openUIMenu();
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Nhiệm vụ danh vọng", () -> {
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Nhận", () -> {
                if (this.level < 50) {
                    getService().npcChat(NpcName.AMEJI, "Trình độ từ cấp 50 trở lên mới được tham gia.");
                    return;
                }
                if (this.gloryTask == null) {
                    if (countGlory == 0) {
                        serverMessage("Con đã hoàn thành đủ số nhiệm vụ cho ngày hôm nay rồi");
                        return;
                    }
                    this.gloryTask = new GloryTask(this);
                    this.countGlory--;
                    getService().showAlert("Nhiệm vụ", this.gloryTask.getTask());
                } else {
                    serverMessage("Con hãy hoàn thành nhiệm vụ được giao trước.");
                }
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Trả", () -> {
                if (this.gloryTask != null) {
                    if (this.gloryTask.isComplete()) {
                        int equipType = this.gloryTask.getEquipType();
                        this.gloryTask = null;
                        int rand = equipType;// NinjaUtil.nextInt(10);
                        int point = 5;
                        String[] arrPointName = {"Nón", "Vũ khí", "Áo", "Liên", "Găng tay", "Nhẫn", "Quần", "Ngọc bội",
                            "Giày", "Phù"};
                        switch (rand) {
                            case 0:
                                pointNon += point;
                                break;

                            case 1:
                                pointVuKhi += point;
                                break;

                            case 2:
                                pointAo += point;
                                break;

                            case 3:
                                pointLien += point;
                                break;

                            case 4:
                                pointGangTay += point;
                                break;

                            case 5:
                                pointNhan += point;
                                break;

                            case 7:
                                pointNgocBoi += point;
                                break;

                            case 6:
                                pointQuan += point;
                                break;

                            case 9:
                                pointPhu += point;
                                break;

                            case 8:
                                pointGiay += point;
                                break;
                        }
                        if (Event.isKoroKing()) {
                            KoroKing.addTrophy(this);
                        }
                        getService().showAlert("Nhận được ", "- " + point + " điểm danh vọng " + arrPointName[rand]);
                    } else {
                        serverMessage("Con hãy hoàn thành nhiệm vụ được giao trước.");
                    }
                } else {
                    serverMessage("Hiện tại con chưa nhận nhiệm vụ nào");
                }
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Hủy", () -> {
                if (this.gloryTask != null) {
                    String title = this.gloryTask.getTaskTitle();
                    setConfirmPopup(new ConfirmPopup(CMDConfirmPopup.NHIEM_VU_DANH_VONG,
                            "Bạn có muốn hủy nhiệm vụ " + title + " hay không?"));
                    getService().openUIConfirmID();
                } else {
                    serverMessage("Hiện tại con chưa nhận nhiệm vụ nào");
                }
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Nhận Geningan", () -> {
                if (pointAo < 100 || pointGangTay < 100 || pointGiay < 100 || pointNgocBoi < 100 || pointLien < 100
                        || pointNhan < 100 || pointNon < 100 || pointPhu < 100 || pointQuan < 100 || pointVuKhi < 100) {
                    serverMessage("Không đủ 100 điểm danh vọng mỗi loại.");
                } else {
                    Item item = ItemFactory.getInstance().newItem(685);
                    item.isLock = true;
                    addItemToBag(item);
                }
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Nâng cấp", () -> {
                if (equipment[14] == null) {
                    serverMessage("Hãy sử dụng Nguyệt Nhãn để sử dụng chức năng này.");
                    return;
                }
                if (equipment[14].upgrade >= 10) {
                    serverMessage("Nguyệt Nhãn đã đạt cấp tối đa.");
                    return;
                }
                int[] costs = {500000, 750000, 1250000, 2000000, 3000000, 4500000, 6000000, 7750000, 10000000};
                int[] percents = {50, 35, 25, 20, 15, 12, 10, 8, 5};
                int index = equipment[14].upgrade - 1;
                setConfirmPopup(new ConfirmPopup(CMDConfirmPopup.NANG_MAT,
                        "Bạn có muốn nâng cấp " + equipment[14].template.name + " với " + costs[index]
                        + " xu hoặc yên với tỉ lệ thành công là " + percents[index] + "% không?"));
                getService().openUIConfirmID();

            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Nâng cấp VIP", () -> {
                if (equipment[14] == null) {
                    serverMessage("Hãy sử dụng Nguyệt Nhãn để sử dụng chức năng này.");
                    return;
                }
                if (equipment[14].upgrade >= 10) {
                    serverMessage("Nguyệt Nhãn đã đạt cấp tối đa.");
                    return;
                }
                int[] costs = {500000, 750000, 1250000, 2000000, 3000000, 4500000, 6000000, 7750000, 10000000};
                int[] costs2 = {50, 60, 85, 100, 150, 200, 300, 450, 600};
                int[] percents = {50, 35, 25, 20, 15, 12, 10, 8, 5};
                int index = equipment[14].upgrade - 1;
                setConfirmPopup(new ConfirmPopup(CMDConfirmPopup.NANG_MAT_VIP,
                        "Bạn có muốn nâng cấp " + equipment[14].template.name + " với " + costs[index]
                        + " xu hoặc yên và lượng " + costs2[index] + " với tỉ lệ thành công là "
                        + (percents[index] * 2) + "% không?"));
                getService().openUIConfirmID();
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Hướng dẫn", () -> {
                String str = "";
                if (this.gloryTask != null) {
                    str = this.gloryTask.getTask();
                }
                String str2 = "- Có thể nhận thêm " + this.countGlory + "  nhiệm vụ trong ngày.\n";
                String str3 = "- Sử dụng danh vọng phù để tăng số lần làm nhiệm vụ trong ngày.\n";
                String str4 = "- Có thể sử dụng thêm " + this.countUseItemGlory + "  Danh vọng phù trong ngày.";
                String show = str + str2 + str3 + str4;
                getService().showAlert("Nhiệm vụ", show);
            }));
            getService().openUIMenu();
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Nói chuyện", () -> {
            String talk = (String) NinjaUtils.randomObject("Con cần mua ngọc bội, nhẫn, dây chuyền, bùa hộ thân à?",
                    "Con chọn loại trang sức nào?", "Trang sức không chỉ để ngắm, nó còn tăng sức mạnh cho con");
            getService().npcChat(NpcName.AMEJI, talk);
        }));
    }

    public void npcTabemono() {
        menus.add(new Menu(CMDMenu.EXECUTE, "Thức ăn (Yên)", () -> {
            getService().openUI((byte) StoreManager.TYPE_FOOD_LOCK);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Thức ăn (Xu)", () -> {
            getService().openUI((byte) StoreManager.TYPE_FOOD);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Thiên Địa bảng", () -> {
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Đăng ký", () -> {
                ThienDiaManager thienDiaManager = ThienDiaManager.getInstance();
                ThienDiaData thienDiaData = thienDiaManager.getThienDiaData(this.level, this.classId);
                if (thienDiaData != null) {
                    try {
                        List<Ranking> rankingList = thienDiaData.getRankings();
                        Ranking r = thienDiaData.getRankingByPlayerID(this.id);
                        if (r != null) {
                            getService().npcChat(NpcName.TABEMONO, "Bạn đã đăng ký thành công rồi");
                            return;
                        }
                        thienDiaManager.removeRankingByID(this.id);
                        int rankAt = rankingList.size() + 1;
                        if (rankAt > 1000) {
                            rankAt = -1;
                        }
                        ResultSet rs = null;
                        PreparedStatement ps = null;
                        int rankingID = 0;
                        ps = DbManager.getInstance().getConnection(DbManager.SERVER).prepareStatement(
                                "INSERT INTO `ranking_list`(`player_id`, `match_id`, `rank_at`, `server_id`) VALUES (?,?,?,?)",
                                Statement.RETURN_GENERATED_KEYS);
                        try {
                            ps.setInt(1, this.id);
                            ps.setInt(2, thienDiaData.getId());
                            ps.setInt(3, rankAt);
                            ps.setInt(4, Config.getInstance().getServerID());
                            ps.executeUpdate();
                            rs = ps.getGeneratedKeys();
                            if (rs.next()) {
                                rankingID = rs.getInt(1);
                            }
                        } finally {
                            if (rs != null) {
                                rs.close();
                            }
                            ps.close();
                        }
                        Ranking ranking = Ranking.builder()
                                .id(rankingID)
                                .playerId(this.id)
                                .name(this.name)
                                .ranked(rankAt)
                                .build();
                        thienDiaData.addRanking(ranking);
                        getService().npcChat(NpcName.TABEMONO, "Bạn đã đăng ký thành công rồi");
                    } catch (SQLException ex) {
                        Log.error("err: " + ex.getMessage(), ex);
                    }
                } else {
                    getService().npcChat(NpcName.TABEMONO, "Không có bảng nào phù hợp với bạn");
                }
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Chinh phục", () -> {
                ThienDiaData thienDiaData = ThienDiaManager.getInstance().getThienDiaData(this.level, this.classId);
                List<Ranking> list = new ArrayList<>();
                if (thienDiaData != null) {
                    Ranking r = thienDiaData.getRankingByPlayerID(this.id);
                    if (r == null) {
                        getService().npcChat(NpcName.TABEMONO, "Bạn phải đăng ký mới có thể sử dụng chức năng này");
                        return;
                    }
                    List<Ranking> rankingList = thienDiaData.getRankings();
                    int rankAt = (r.getRanked() == -1) ? rankingList.size() : r.getRanked();
                    int start = rankAt - 10;
                    if (rankAt > rankingList.size()) {
                        rankAt = rankingList.size();
                    }
                    if (start < 0) {
                        start = 0;
                        rankAt = rankingList.size() + 1;
                    }
                    for (int i = start; i < rankAt - 1; i++) {
                        Ranking bot = rankingList.get(i);
                        bot.setStt(bot.isFighting() ? "đang đấu" : "có thể thách đấu");
                        list.add(bot);
                    }
                    getService().doShowRankedListUI(list);
                } else {
                    getService().npcChat(NpcName.TABEMONO, "Không có bảng nào phù hợp với bạn");
                }
            }));
            ThienDiaManager thienDiaManager = ThienDiaManager.getInstance();
            List<ThienDiaData> list = thienDiaManager.getList();
            for (ThienDiaData thienDiaData : list) {
                menus.add(new Menu(CMDMenu.EXECUTE, thienDiaData.getName(), () -> {
                    StringBuilder sb = new StringBuilder();
                    List<Ranking> rankingList = thienDiaData.getRankings();
                    int length = 10;
                    if (rankingList.size() < length) {
                        length = rankingList.size();
                    }
                    for (int i = 0; i < length; i++) {
                        Ranking ranking = rankingList.get(i);
                        sb.append(String.format("%d. ", ranking.getRanked())).append(ranking.getName()).append("\n");
                    }
                    getService().showAlert(thienDiaData.getName(), sb.toString());
                }));
            }
            getService().openUIMenu();
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Nói chuyện", () -> {
            if (taskId == TaskName.NHIEM_VU_CHAO_LANG && taskMain != null && taskMain.index == 1) {
                taskNext();
            }
            String talk = (String) NinjaUtils.randomObject("Thức ăn của ta là ngon nhất rồi!",
                    "Ăn xong đảm bảo ngươi sẽ quay lại lần sau.");
            getService().npcChat(NpcName.TABEMONO, talk);
        }));
        if (Event.isHalloween()) {
            menus.add(new Menu(CMDMenu.EXECUTE, "Lễ hội hóa trang", () -> {
                Calendar calendar = Calendar.getInstance();
                int h = calendar.get(Calendar.HOUR_OF_DAY);
                if (h < 19 || h >= 21) {
                    getService().npcChat(NpcName.TABEMONO, "Lễ hội thời trang chỉ mở cửa vào 19h-21h hằng ngày!");
                    return;
                }
                if (group == null || group.getNumberMember() < 2) {
                    serverDialog("Chỉ có thể tham gia khi đi theo nhóm 2 người trở lên!");
                    return;
                }

                Point invitation = getEventPoint().find(Halloween.INVITATION_NUMBER);
                int invationNumber = invitation.getPoint();
                if (invationNumber <= 0) {
                    getService().npcChat(NpcName.TABEMONO, "Ngươi cần sử dụng thư mời mới có thể tham gia!");
                    return;
                }

                if (this.fashion[11] == null || this.fashion[11].template.id < 814
                        || this.fashion[11].template.id > 818) {
                    getService().npcChat(NpcName.TABEMONO, "Ngươi cần mặc trang phục hóa trang mới có thể tham gia!");
                    return;
                }

                invitation.subPoint(1);
                MemberGroup memberGroup = group.findMember(id);
                NymozCave nymozCave = memberGroup.getNymozCave();
                if (nymozCave == null) {
                    if (group.isLeader(id)) {
                        Map map = MapManager.getInstance().find(MapName.HANG_NYMOZ);
                        nymozCave = new NymozCave(0, map);
                        group.setNymozCave(nymozCave);
                        group.getGroupService().serverMessage(
                                "Trưởng nhóm đã tham gia, hãy gặp Tabemono để tham gia lễ hội hóa trang!");
                    } else {
                        getService().npcChat(NpcName.TABEMONO, "Chỉ có trưởng nhóm mới có thể mở!");
                        return;
                    }
                }
                mapBeforeEnterPB = mapId;
                outZone();
                setXY((short) 360, (short) 672);
                nymozCave.join(this);
            }));
        }
    }

    public void npcKiriko() {
        menus.add(new Menu(CMDMenu.EXECUTE, "Dược phẩm (Yên)", () -> {
            getService().openUI((byte) StoreManager.TYPE_POTION_LOCK);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Dược phẩm (Xu)", () -> {
            getService().openUI((byte) StoreManager.TYPE_POTION);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Nói chuyện", () -> {
            if (taskId == TaskName.NHIEM_VU_CHAO_LANG && taskMain != null && taskMain.index == 0) {
                taskNext();
            }
            String talk = (String) NinjaUtils.randomObject("Đi đường cần mang ít dược phẩm",
                    "Không mang theo HP, Hp bên mình, con sẽ gặp nguy hiểm.");
            getService().npcChat((short) 3, talk);
        }));
        if (Event.isKoroKing() || Event.isTrungThu()) {
            menus.add(new Menu(CMDMenu.EXECUTE, "Hoa phục sinh", () -> {
                menus.clear();
                menus.add(new Menu(CMDMenu.EXECUTE, "Hoa thiên diệu", () -> {
                    Event.getEvent().action(this, TrungThu.HOA_PHUC_SINH, ItemName.HOA_THIEN_DIEU);
                }));
                menus.add(new Menu(CMDMenu.EXECUTE, "Hoa dạ yến", () -> {
                    Event.getEvent().action(this, TrungThu.HOA_PHUC_SINH, ItemName.HOA_DA_YEN);
                }));
                getService().openUIMenu();
            }));
        }
        
    }

    public void loadDisplay() {
        try {
            PreparedStatement ps = DbManager.getInstance().getConnection(DbManager.LOAD_CHAR).prepareStatement(
                    "SELECT `players`.`name`, `players`.`gender`, `players`.`class`, `players`.`last_logout_time`, `players`.`head`, `players`.`head2`, `players`.`body`, `players`.`weapon`, `players`.`leg`, `players`.`online`, CAST(JSON_EXTRACT(data, \"$.exp\") AS INT) AS `exp` FROM `players` WHERE `players`.`id` = ?");
            ps.setInt(1, this.id);
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    loadDisplay(rs);
                }
            } finally {
                rs.close();
                ps.close();
            }
        } catch (Exception ex) {
            Log.error("load display err: " + ex.getMessage(), ex);
        }
    }

    public void loadDisplay(ResultSet rs) {
        try {
            this.name = rs.getString("name");
            this.gender = rs.getByte("gender");
            this.classId = rs.getByte("class");
            this.online = rs.getBoolean("online");
            this.lastLogoutTime = rs.getLong("last_logout_time");
            Clazz clazz = GameData.getInstance().findClass(classId);
            this.school = clazz.getName();
            this.original_head = this.head = rs.getShort("head");
            this.weapon = -1;
            this.body = -1;
            this.leg = -1;
            this.exp = rs.getLong("exp");
            this.head = rs.getShort("head2");
            if (this.head == -1) {
                this.head = this.original_head;
            }
            this.weapon = rs.getShort("weapon");
            this.body = rs.getShort("body");
            this.leg = rs.getShort("leg");
            if (this.exp > Server.EXP_MAX) {
                this.exp = Server.EXP_MAX;
            }
            this.level = NinjaUtils.getLevel(this.exp);
        } catch (SQLException ex) {
            Log.error("load display err: " + ex.getMessage(), ex);
        }
    }

    public synchronized boolean load() {
        try {
            PreparedStatement stmt = DbManager.getInstance().getConnection(DbManager.LOAD_CHAR).prepareStatement(
                    SQLStatement.LOAD_PLAYER, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            stmt.setInt(1, this.id);
            ResultSet rs = stmt.executeQuery();
            try {
                if (!rs.first()) {
                    return false;
                }
                this.coin = rs.getLong("xu");
                this.coinInBox = rs.getLong("xuInBox");
                this.yen = rs.getLong("yen");
                JSONArray ja = (JSONArray) JSONValue.parse(rs.getString("map"));
                this.mapId = Short.parseShort(ja.get(0).toString());
                this.x = Short.parseShort(ja.get(1).toString());
                this.y = Short.parseShort(ja.get(2).toString());
                this.saveCoordinate = rs.getShort("saveCoordinate");
                if (this.saveCoordinate == 162) {
                    this.saveCoordinate = 22;
                }
                this.potentialPoint = rs.getShort("point");
                this.skillPoint = rs.getShort("spoint");
                JSONArray jArr = (JSONArray) JSONValue.parse(rs.getString("potential"));
                int len = jArr.size();
                this.potential = new int[4];
                for (int i = 0; i < 4; i++) {
                    if (jArr.get(i) != null) {
                        this.potential[i] = Integer.parseInt(jArr.get(i).toString());
                    } else {
                        this.potential[i] = 5;
                    }
                }
                jArr = (JSONArray) JSONValue.parse(rs.getString("skill"));
                len = jArr.size();
                this.vSkill = new Vector<>();
                this.vSupportSkill = new Vector<>();
                this.vSkillFight = new Vector<>();
                for (int i = 0; i < len; i++) {
                    JSONObject obj = (JSONObject) jArr.get(i);
                    int skillId = Integer.parseInt(obj.get("id").toString());
                    int point = Integer.parseInt(obj.get("point").toString());
                    Skill skill = GameData.getInstance().getSkill(this.classId, skillId, point);
                    if (skill.template.type == Skill.SKILL_AUTO_USE) {
                        this.vSupportSkill.add(skill);
                    } else if ((skill.template.type == Skill.SKILL_CLICK_USE_ATTACK
                            || skill.template.type == Skill.SKILL_CLICK_LIVE
                            || skill.template.type == Skill.SKILL_CLICK_USE_BUFF
                            || skill.template.type == Skill.SKILL_CLICK_NPC)
                            && (skill.template.maxPoint == 0 || (skill.template.maxPoint > 0 && skill.point > 0))) {

                        this.vSkillFight.add(skill);
                    }
                    this.vSkill.add(skill);
                }
                this.mount = new Mount[5];
                JSONArray jso = (JSONArray) JSONValue.parse(rs.getString("mount"));
                if (jso != null) {
                    int size = jso.size();
                    for (int i = 0; i < size; i++) {
                        Mount mount = new Mount((JSONObject) jso.get(i));
                        this.mount[mount.template.type - 29] = mount;
                    }
                    if (this.mount[4] != null) {
                        this.mount[4].fixMount();
                    }
                }
                this.bijuu = new Item[5];
                JSONArray jBijuu = (JSONArray) JSONValue.parse(rs.getString("bijuu"));
                if (jBijuu != null) {
                    int size = jBijuu.size();
                    for (int i = 0; i < size; i++) {
                        Item bi = new Item((JSONObject) jBijuu.get(i));
                        int index = 4;
                        if (bi.template.isTypeEquipmentBijuu()) {
                            index = bi.template.type - 35;
                        }
                        this.bijuu[index] = bi;
                    }
                }
                JSONArray effects = (JSONArray) JSONValue.parse(rs.getString("effect"));
                int size2 = effects.size();
                for (int i = 0; i < size2; i++) {
                    try {
                        JSONObject obj = (JSONObject) effects.get(i);
                        int id = Integer.parseInt(obj.get("id").toString());
                        int param = Integer.parseInt(obj.get("param").toString());
                        long startAt = 0;
                        long endAt = 0;
                        long timeLength = 0;
                        if (obj.containsKey("timeStart")) {
                            int timeStart = Integer.parseInt(obj.get("timeStart").toString());
                            int timeLength2 = Integer.parseInt(obj.get("timeLength").toString());
                            startAt = System.currentTimeMillis();
                            endAt = startAt + ((timeLength2 - timeStart) * 1000);

                        } else {
                            timeLength = Long.parseLong(obj.get("time_length").toString());

                            startAt = Long.parseLong(obj.get("start_at").toString());
                            endAt = Long.parseLong(obj.get("end_at").toString());

                            long timeRemaining = timeLength;
                            endAt = System.currentTimeMillis() + timeRemaining;
                            startAt = System.currentTimeMillis();
                        }
                        if (System.currentTimeMillis() < endAt) {
                            Effect eff = new Effect(id, startAt, endAt, param);
                            em.effect(eff, true);
                            em.add(eff);
                        }
                    } catch (Exception e) {
                        Log.error("err: " + e.getMessage(), e);
                    }
                }
                this.equipment = new Equip[16];
                jso = (JSONArray) JSONValue.parse(rs.getString("equiped"));
                if (jso != null) {
                    int size = jso.size();
                    for (int i = 0; i < size; i++) {
                        Equip equip = new Equip((JSONObject) jso.get(i));
                        if (equip.isExpired() || equip.isRemoveItem()) {
                            continue;
                        }
                        this.equipment[equip.template.type] = equip;
                    }
                }
                this.fashion = new Equip[16];
                jso = (JSONArray) JSONValue.parse(rs.getString("fashion"));
                if (jso != null) {
                    int size = jso.size();
                    for (int i = 0; i < size; i++) {
                        Equip equip = new Equip((JSONObject) jso.get(i));
                        if (equip.isExpired() || equip.isRemoveItem()) {
                            continue;
                        }
                        this.fashion[equip.template.type] = equip;
                    }
                }
                int clan = rs.getInt("clan");
                if (clan != 0) {
                    Optional<Clan> g = Clan.getClanDAO().get(clan);
                    if (g != null && g.isPresent()) {
                        this.clan = g.get();
                        Member mem = this.clan.getMemberByName(this.name);
                        if (mem == null) {
                            this.clan = null;
                        }
                    }
                }
                this.numberCellBag = rs.getByte("numberCellBag");
                this.numberCellBox = rs.getByte("numberCellBox");
                JSONObject json = (JSONObject) JSONValue.parse(rs.getString("data"));
                ParseData parse = new ParseData(json);
                this.expDown = parse.getLong("expDown");
                this.hieuChien = parse.getByte("hieuChien");
                this.countFinishDay = parse.getByte("countFinishDay");
                if (parse.containsKey("rank")) {
                    this.huyHieu = parse.getInt("rank");
                } else {
                    this.huyHieu = -1;
                }
                if (parse.containsKey("timeCountDown")) {
                    this.timeCountDown = parse.getInt("timeCountDown");
                } else {
                    this.timeCountDown = 0;
                }
                if (parse.containsKey("countLoopBoss")) {
                    this.countLoopBoss = parse.getInt("countLoopBoss");
                } else {
                    this.countLoopBoss = 2;
                }
                if (parse.containsKey("countUseItemGlory")) {
                    this.countUseItemGlory = parse.getInt("countUseItemGlory");
                } else {
                    this.countUseItemGlory = 6;
                }
                if (parse.containsKey("count_arena")) {
                    this.countArenaT = parse.getInt("count_arena");
                } else {
                    this.countArenaT = 1;
                }
                if (parse.containsKey("war")) {
                    JSONObject obj = (JSONObject) JSONValue.parse(parse.getString("war"));
                    this.warPoint = Integer.parseInt(obj.get("point").toString());
                    this.time = Long.parseLong(obj.get("time").toString());
                    this.faction = Byte.parseByte(obj.get("faction").toString());
                    if (obj.get("rewarded") != null) {
                        this.isRewarded = Boolean.parseBoolean(obj.get("rewarded").toString());
                    } else {
                        this.isRewarded = false;
                    }
                    if (obj.get("kill") != null) {
                        this.nKill = Integer.parseInt(obj.get("kill").toString());
                    } else {
                        this.nKill = 0;
                    }
                    if (obj.get("dead") != null) {
                        this.nDead = Integer.parseInt(obj.get("dead").toString());
                    } else {
                        this.nDead = 0;
                    }

                }
                if (parse.containsKey("clan_war")) {
                    JSONObject obj = (JSONObject) JSONValue.parse(parse.getString("clan_war"));
                    this.warPoint = Integer.parseInt(obj.get("point").toString());
                    this.time = Long.parseLong(obj.get("time").toString());
                    this.faction = Byte.parseByte(obj.get("faction").toString());
                    if (obj.get("rewarded") != null) {
                        this.isRewarded = Boolean.parseBoolean(obj.get("rewarded").toString());
                    } else {
                        this.isRewarded = false;
                    }
                    if (obj.get("kill") != null) {
                        this.nKill = Integer.parseInt(obj.get("kill").toString());
                    } else {
                        this.nKill = 0;
                    }
                    if (obj.get("dead") != null) {
                        this.nDead = Integer.parseInt(obj.get("dead").toString());
                    } else {
                        this.nDead = 0;
                    }

                } else {
                    this.warPoint = 0;
                    this.time = -1;
                    this.faction = -1;
                }
                if (parse.containsKey("countUseItemDungeo")) {
                    this.countUseItemDungeo = parse.getInt("countUseItemDungeo");
                } else {
                    this.countUseItemDungeo = 1;
                }
                if (parse.containsKey("countUseItemBeast")) {
                    this.countUseItemBeast = parse.getInt("countUseItemBeast");
                } else {
                    this.countUseItemBeast = 1;
                }
                if (parse.containsKey("countGlory")) {
                    this.countGlory = parse.getInt("countGlory");
                } else {
                    this.countGlory = 20;
                }
                if (parse.containsKey("sevenbeasts_id")) {
                    sevenBeastsID = parse.getInt("sevenbeasts_id");
                } else {
                    sevenBeastsID = -1;
                }
                if (parse.containsKey("not_received_exp")) {
                    this.notReceivedExp = parse.getBoolean("not_received_exp");
                } else {
                    this.notReceivedExp = false;
                }

                if (parse.containsKey("dhtp")) {
                    this.dhtp = parse.getBoolean("dhtp");
                } else {
                    this.dhtp = false;
                }

                if (parse.containsKey("dhdg")) {
                    this.dhdg = parse.getBoolean("dhdg");
                } else {
                    this.dhdg = false;
                }

                if (parse.containsKey("duatop")) {
                    this.duatop = parse.getBoolean("duatop");
                } else {
                    this.duatop = true;
                }

                if (parse.containsKey("type_sevenbeasts")) {
                    typeSevenBeasts = parse.getInt("type_sevenbeasts");
                } else {
                    typeSevenBeasts = -1;
                }

                if (parse.containsKey("countEnterFujukaSanctuary")) {
                    countEnterFujukaSanctuary = parse.getInt("countEnterFujukaSanctuary");
                } else {
                    countEnterFujukaSanctuary = 1;
                }

                long lastLoginTime = rs.getLong("last_login_time");
                Date now = new Date();
                Date date = NinjaUtils.getDate(lastLoginTime);
                boolean isNewDay = !DateUtils.isSameDay(date, now);
                if (isNewDay) {
                    this.countGlory = 20;
                    this.countUseItemGlory = 6;
                    this.countUseItemDungeo = 1;
                    this.countFinishDay = 20;
                    this.countLoopBoss = 2;
                    this.countUseItemBeast = 2;
                    this.warPoint = 0;
                    this.time = -1;
                    this.faction = -1;
                    this.updateHPMount(-200);
                    this.countArenaT = 1;
                    this.sevenBeastsID = -1;
                    this.typeSevenBeasts = -1;
                    this.countEnterFujukaSanctuary = 1;
                }
                this.countPB = parse.getByte("countPB");
                if (this.countPB == 0) {
                    if (isNewDay) {
                        this.countPB = 1;
                        this.pointPB = 0;
                        this.receivedRewardPB = false;
                    } else {
                        int dungeonId = -1;
                        if (parse.containsKey("dungeonId")) {
                            dungeonId = parse.getInt("dungeonId");
                        }
                        Dungeon dungeon = Dungeon.findDungeonById(dungeonId);
                        if (dungeon != null) {
                            this.addWorld(dungeon);
                            this.pointPB = parse.getInt("pointPB");
                            this.receivedRewardPB = parse.getBoolean("receivedRewardPB");
                        } else {
                            this.countPB = 1;
                            this.pointPB = 0;
                            this.receivedRewardPB = false;
                        }
                    }
                }
                this.limitKyNangSo = parse.getByte("limitKyNangSo");
                this.limitTiemNangSo = parse.getByte("limitTiemNangSo");
                this.reward = new boolean[5];
                if (parse.containsKey("reward")) {
                    JSONArray jA = parse.getJSONArray("reward");
                    for (int i = 0; i < 5; i++) {
                        if (jA.get(i) != null) {
                            this.reward[i] = Boolean.parseBoolean(jA.get(i).toString());
                        }
                    }
                }
                if (parse.containsKey("levelUpTime")) {
                    this.levelUpTime = parse.getLong("levelUpTime");
                } else {
                    this.levelUpTime = (new Date()).getTime();
                }
                if (parse.containsKey("tayTiemNang")) {
                    this.tayTiemNang = parse.getInt("tayTiemNang");
                } else {
                    this.tayTiemNang = 0;
                }
                if (parse.containsKey("tayKyNang")) {
                    this.tayKyNang = parse.getInt("tayKyNang");
                } else {
                    this.tayKyNang = 0;
                }
                if (parse.containsKey("limitBangHoa")) {
                    this.limitBangHoa = parse.getInt("limitBangHoa");
                } else {
                    this.limitBangHoa = 0;
                }
                if (parse.containsKey("limitPhongLoi")) {
                    this.limitPhongLoi = parse.getInt("limitPhongLoi");
                } else {
                    this.limitPhongLoi = 0;
                }
                if (parse.containsKey("numberUseExpanedBag")) {
                    this.numberUseExpanedBag = parse.getByte("numberUseExpanedBag");
                } else {
                    this.numberUseExpanedBag = 0;
                }
                if (parse.containsKey("numberUseExpanedBox")) {
                    this.numberUseExpanedBox = parse.getByte("numberUseExpanedBox");
                } else {
                    this.numberUseExpanedBox = 0;
                }
                if (parse.containsKey("gloryTask")) {
                    ParseData p = parse.getParseData("gloryTask");
                    int type = p.getInt("type");
                    int quantity = p.getInt("quantity");
                    int progress = p.getInt("progress");
                    int requireUseEquip = p.getInt("requireUseEquip");
                    this.gloryTask = new GloryTask(this, type, quantity, progress, requireUseEquip);
                }
                this.taskOrders = new ArrayList<>();
                if (parse.containsKey("taskOrder")) {
                    ParseData[] taskOrders = parse.getArrayParseData("taskOrder");
                    for (ParseData task : taskOrders) {
                        this.taskOrders.add(new TaskOrder(this, task.getByte("taskId"), task.getInt("count"),
                                task.getInt("maxCount"), task.getInt("killId"), task.getInt("mapId")));
                    }
                }
                if (parse.containsKey("lastTimeOutClan")) {
                    this.lastTimeOutClan = parse.getLong("lastTimeOutClan");
                } else {
                    this.lastTimeOutClan = 0;
                }
                if (parse.containsKey("coinMax")) {
                    this.coinMax = parse.getLong("coinMax");
                } else {
                    this.coinMax = 2000000000L;
                }
                if (parse.containsKey("maskId")) {
                    int maskId = parse.getInt("maskId");
                    this.setMaskId(maskId);
                } else {
                    this.setMaskId(-1);
                }
                if (parse.containsKey("pointAo")) {
                    this.pointAo = parse.getInt("pointAo");
                } else {
                    this.pointAo = 0;
                }
                if (parse.containsKey("pointGangTay")) {
                    this.pointGangTay = parse.getInt("pointGangTay");
                } else {
                    this.pointGangTay = 0;
                }
                if (parse.containsKey("pointGiay")) {
                    this.pointGiay = parse.getInt("pointGiay");
                } else {
                    this.pointGiay = 0;
                }
                if (parse.containsKey("pointLien")) {
                    this.pointLien = parse.getInt("pointLien");
                } else {
                    this.pointLien = 0;
                }
                if (parse.containsKey("pointNgocBoi")) {
                    this.pointNgocBoi = parse.getInt("pointNgocBoi");
                } else {
                    this.pointNgocBoi = 0;
                }
                if (parse.containsKey("pointNhan")) {
                    this.pointNhan = parse.getInt("pointNhan");
                } else {
                    this.pointNhan = 0;
                }
                if (parse.containsKey("pointNon")) {
                    this.pointNon = parse.getInt("pointNon");
                } else {
                    this.pointNon = 0;
                }
                if (parse.containsKey("pointPhu")) {
                    this.pointPhu = parse.getInt("pointPhu");
                } else {
                    this.pointPhu = 0;
                }
                if (parse.containsKey("pointQuan")) {
                    this.pointQuan = parse.getInt("pointQuan");
                } else {
                    this.pointQuan = 0;
                }
                if (parse.containsKey("pointTinhTu")) {
                    this.pointTinhTu = parse.getInt("pointTinhTu");
                } else {
                    this.pointTinhTu = 0;
                }
                if (parse.containsKey("pointUyDanh")) {
                    this.pointUyDanh = parse.getInt("pointUyDanh");
                } else {
                    this.pointUyDanh = 0;
                }
                if (parse.containsKey("pointVuKhi")) {
                    this.pointVuKhi = parse.getInt("pointVuKhi");
                } else {
                    this.pointVuKhi = 0;
                }
                this.bag = new Item[this.numberCellBag];
                jso = (JSONArray) JSONValue.parse(rs.getString("bag"));
                if (jso != null) {
                    int size = jso.size();
                    for (int i = 0; i < size; i++) {
                        Item item = new Item((JSONObject) jso.get(i));
                        if (item == null || !item.has() || item.isExpired() || item.isRemoveItem(true)) {
                            continue;
                        }
                        this.bag[item.index] = item;
                    }
                }
                this.box = new Item[this.numberCellBox];
                jso = (JSONArray) JSONValue.parse(rs.getString("box"));
                if (jso != null) {
                    int size = jso.size();
                    for (int i = 0; i < size; i++) {
                        Item item = new Item((JSONObject) jso.get(i));
                        if (item == null || !item.has() || item.isExpired() || item.isRemoveItem(true)) {
                            continue;
                        }
                        this.box[item.index] = item;
                    }
                }
                ArrayList<Item> maskBox = new ArrayList<>();
                Object obj2 = rs.getObject("mask_box");
                if (obj2 != null) {
                    JSONArray jA = (JSONArray) JSONValue.parse((String) obj2);
                    for (int i = 0; i < jA.size(); i++) {
                        JSONObject jO = (JSONObject) jA.get(i);
                        Item item = new Item(jO);
                        maskBox.add(item);
                        if (item.id == this.getMaskId()) {
                            this.setMask(item);
                        }
                    }
                }
                this.setMaskBox(maskBox);
                ArrayList<Item> collectionBox = new ArrayList<>();
                obj2 = rs.getObject("collection_box");
                if (obj2 != null) {
                    JSONArray jA = (JSONArray) JSONValue.parse((String) obj2);
                    for (int i = 0; i < jA.size(); i++) {
                        JSONObject jO = (JSONObject) jA.get(i);
                        Item item = new Item(jO);
                        collectionBox.add(item);
                    }
                }
                this.setCollectionBox(collectionBox);
                JSONArray j = (JSONArray) JSONValue.parse(rs.getString("onOSkill"));
                if (j != null) {
                    this.onOSkill = new byte[j.size()];
                    for (int t = 0; t < this.onOSkill.length; t++) {
                        if (t < j.size()) {
                            this.onOSkill[t] = Byte.parseByte(j.get(t).toString());
                        }
                    }
                } else {
                    this.onOSkill = new byte[0];
                }
                j = (JSONArray) JSONValue.parse(rs.getString("onCSkill"));
                this.onCSkill = new byte[]{-1, -1, -1, -1, -1};
                if (j != null) {
                    for (int t = 0; t < this.onCSkill.length; t++) {
                        if (t < j.size()) {
                            this.onCSkill[t] = Byte.parseByte(j.get(t).toString());
                        }
                    }
                }
                j = (JSONArray) JSONValue.parse(rs.getString("onKSkill"));
                this.onKSkill = new byte[]{-1, -1, -1};
                if (j != null) {
                    for (int t = 0; t < this.onKSkill.length; t++) {
                        if (t < j.size()) {
                            this.onKSkill[t] = Byte.parseByte(j.get(t).toString());
                        }
                    }
                }
                JSONArray friends = (JSONArray) JSONValue.parse(rs.getString("friends"));
                int size = friends.size();
                this.friends = new HashMap<>();
                for (int i = 0; i < size; i++) {
                    Friend fr = new Friend((JSONObject) friends.get(i));
                    this.friends.put(fr.name, fr);
                }

                JSONArray enemies = (JSONArray) JSONValue.parse(rs.getString("enemies"));
                size = enemies.size();
                this.enemies = new HashMap<>();
                for (int i = 0; i < size; i++) {
                    Friend fr = new Friend((JSONObject) enemies.get(i));
                    this.enemies.put(fr.name, fr);
                }
                this.taskId = rs.getShort("taskId");
                String tt = rs.getString("task");
                if (tt != null && !tt.equals("")) {
                    ParseData pd = new ParseData((JSONObject) JSONValue.parse(tt));
                    short taskID = pd.getShort("id");
                    byte taskIndex = pd.getByte("index");
                    short taskCount = pd.getShort("count");
                    TaskTemplate task = Task.getTaskTemplate(taskID);
                    if (task != null) {
                        this.taskMain = TaskFactory.getInstance().createTask(taskID, taskIndex, taskCount);
                    }
                }
                this.setLoadFinish(true);
                Object obj = rs.getObject("message");
                if (obj != null) {
                    this.message = obj.toString();
                }
                initListCanEnterMap();
                if (Event.getEvent() != null) {
                    loadEventPoint();
                }
                return true;
            } finally {
                rs.close();
                stmt.close();
            }
        } catch (Exception ex) {
            Log.error("load data err: " + ex.getMessage(), ex);
            ex.printStackTrace();
        }
        return false;
    }

    public void loadEventPoint() {
        try {
            Event event = Event.getEvent();
            EventPoint eventPoint = event.findEventPointByPlayerID(this.id);
            if (eventPoint == null) {
                eventPoint = event.createEventPoint();
                eventPoint.setPlayerID(this.id);
                eventPoint.setPlayerName(this.name);

                Gson g = new Gson();
                PreparedStatement ps = DbManager.getInstance().getConnection(DbManager.GAME)
                        .prepareStatement(SQLStatement.INSERT_EVENT_POINT, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, event.getId());
                ps.setInt(2, this.id);
                ps.setString(3, g.toJson(eventPoint.getPoints()));
                ps.setInt(4, Config.getInstance().getServerID());
                ps.executeUpdate();
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    eventPoint.setId(generatedKeys.getInt(1));
                }
                ps.close();
                event.addEventPoint(eventPoint);
            }
            setEventPoint(eventPoint);
        } catch (SQLException ex) {
            Logger.getLogger(Char.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateEventPoint() {
        try {
            EventPoint eventPoint = getEventPoint();
            if (eventPoint != null) {
                Gson g = new Gson();
                PreparedStatement ps = DbManager.getInstance().getConnection(DbManager.GAME)
                        .prepareStatement(SQLStatement.UPDATE_EVENT_POINT);
                ps.setString(1, g.toJson(eventPoint.getPoints()));
                ps.setInt(2, eventPoint.getId());
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Char.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void npcKagai() {
        menus.add(new Menu(CMDMenu.EXECUTE, "Chiến trường kẹo", () -> {
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Tham gia", () -> {
                if (!user.isAdmin()) {
                    serverDialog("Tính năng đang được phát triển!");
                    return;
                }
                CandyBattlefield candyBattlefield = MapManager.getInstance().getCandyBattlefield();
                if (candyBattlefield == null) {
                    setInput(new InputDialog(CMDInputDialog.EXECUTE, "Số người chơi từ 10 đến 40 (Số chẵn)", () -> {
                        try {
                            int n = input.intValue();
                            if (n < 10 || n > 40) {
                                serverDialog("Số lượng phải từ 10 đến 20 người chơi!");
                                return;
                            }
                            if (n % 2 != 0) {
                                serverDialog("Số lượng là số chẵn!");
                                return;
                            }
                            CandyBattlefield candy = new CandyBattlefield(1800);
                            addWorld(candy);
                            candy.join(this);
                            MapManager.getInstance().setCandyBattlefield(candy);
                            GlobalService.getInstance().chat("Chiến trường kẹo",
                                    "Chiến trường kẹo đã mở cửa, hãy gặp Kagai ở các trường để tham gia!");
                        } catch (Exception e) {
                            serverDialog(e.getLocalizedMessage());
                        }
                    }));
                    getService().showInputDialog();
                } else {
                    if (candyBattlefield.isOpened()) {
                        serverDialog("Chiến trường đã bắt đầu! Vui lòng quay lại sau.");
                        return;
                    }
                    if (candyBattlefield.getNumberPlayer() > +candyBattlefield.getMaxPlayer()) {
                        serverDialog("Chiến trường đã đạt tối đa! Vui lòng quay lại sau.");
                        return;
                    }
                    addWorld(candyBattlefield);
                    candyBattlefield.join(this);
                }
            }));
            menus.add(new Menu(CMDMenu.CANCEL, "Hướng dẫn"));
            getService().openUIMenu();
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Gia tộc chiến", () -> {
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Thách đấu", () -> {
                InputDialog input = new InputDialog(CMDInputDialog.INVITE_WAR_CLAN, "Nhập tên gia tộc đối thủ");
                setInput(input);
                getService().showInputDialog();
            }));
            getService().openUIMenu();
        }));
//        menus.add(new Menu(CMDMenu.EXECUTE, "Liên Server", () -> {
//            menus.clear();
//            menus.add(new Menu(CMDMenu.CANCEL, "1 ngày"));
//            menus.add(new Menu(CMDMenu.CANCEL, "3 ngày"));
//            menus.add(new Menu(CMDMenu.CANCEL, "Hướng dẫn"));
//            getService().openUIMenu();
//        }));
//        menus.add(new Menu(CMDMenu.EXECUTE, "Đổi điểm tinh tú", () -> {
//            menus.clear();
//            menus.add(new Menu(CMDMenu.CANCEL, "Bí kíp 3 ngày"));
//            menus.add(new Menu(CMDMenu.CANCEL, "Bí kíp 7 ngày"));
//            menus.add(new Menu(CMDMenu.CANCEL, "Bí kíp 15 ngày"));
//            getService().openUIMenu();
//        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Tinh luyện", () -> {
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Luyện tinh thạch", () -> {
                getService().openUI((byte) 43);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Tinh luyện vật phẩm", () -> {
                getService().openUI((byte) 44);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Dịch chuyển trang bị", () -> {
                getService().openUI((byte) 45);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Hướng dẫn", () -> {
                String guide = "- TINH LUYỆN TRANG BỊ" + "\n";
                guide += "  + Để tinh luyện trang bị cần phải có Tử tinh thạch." + "\n";
                guide += "  + Độ tinh luyện cấp 1, 2, 3 cần phải có Tử tinh thạch sơ cấp." + "\n";
                guide += "  + Độ tinh luyện cấp 4, 5, 6 cần phải có Tử tinh thạch trung cấp." + "\n";
                guide += "  + Độ tinh luyện cấp 7, 8, 9 cần phải có Tử tinh thạch cao cấp." + "\n";
                guide += "- LUYỆN TỬ TINH THẠCH" + "\n";
                guide += "  + Ghép 9 Tử tinh thạch sơ cấp sẽ nhận được 1 Tử tinh thạch trung cấp." + "\n";
                guide += "  + Ghép 9 Tử tinh thạch trung cấp sẽ nhận được 1 Tử tinh thạch cao cấp." + "\n";
                guide += "  + Ghép 3 Tử tinh thạch sơ cấp với Đá 11 sẽ nhận được 3 Tử tinh thạch trung cấp." + "\n";
                guide += "  + Ghép 3 Tử tinh thạch trung cấp với Đá 12 sẽ nhận được 3 Tử tinh thạch cao cấp." + "\n";
                guide += "- DỊCH CHUYỂN TRANG BỊ" + "\n";
                guide += "  + Trang bị dịch chuyển phải là trang bị +12 trở lên." + "\n";
                guide += "  + Trang bị sau khi dịch chuyển sẽ có thêm hai dòng thuộc tính để tinh luyện." + "\n";
                guide += "  + Để dịch chuyển cần phải có đủ 20 chuyển tinh thạch." + "\n";
                guide += "- THĂNG SAO THÚ CƯỠI" + "\n";
                guide += "  + Thú cưỡi đạt level 100 người chơi có thể lựa chọn thăng cấp sao." + "\n";
                guide += "  + Thăng sao thành công thú cưỡi sẽ trở về level 1 với tiềm năng tốt hơn." + "\n";
                guide += "  + Cần sử dụng chuyển tinh thạch để tăng sao thú cưỡi." + "\n";
                getService().showAlert("Hướng dẫn", guide);
            }));
            getService().openUIMenu();
        }));
    }

    public void npcOkanechan() {
         menus.add(new Menu(CMDMenu.EXECUTE, "Nói chuyện", () -> {
            if (taskId == TaskName.NHIEM_VU_CHAO_LANG && taskMain != null && taskMain.index == 3) {
                taskNext();
            }
            String talk = (String) NinjaUtils.randomObject(
                    "Ta là hiện thân của thần tài sẽ mang đến tài lộc cho mọi người.",
                    "Online mỗi ngày tham gia các hoạt động để tích lũy điểm hoạt động con nhé.",
                    "Con hãy chăm đánh quái, làm nhiệm vụ để có thêm nhiều yên hơn.");
            getService().npcChat(NpcName.OKANECHAN, talk);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Đổi lượng", () -> {
            if (this.user.kh == 0) {
                serverMessage("Bạn phải kích hoạt để dùng tính năng này");
                return;
            }
            if (this.level < 70) {
                serverMessage("Bạn phải lv70 trở lên mới dùng đc");
                return;
            }
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "500 lấy 2.500.000 yên", () -> {
                if (this.yen > 1000000000) {
                    serverMessage("bạn chỉ có thể đổi khi dưới 1 tỷ yên");
                    return;
                }
                if (this.user.gold < 500) {
                    serverMessage("Bạn cần có trên 500 lượng");
                    return;
                }
                if (user.gold >= 500) {
                    addGold(-500);
                    addYen(2500000);
                    getService().npcChat(NpcName.OKANECHAN, "Đổi Thành Công");
                } else {
                    getService().npcChat(NpcName.OKANECHAN, "Ngươi cần 500 lượng để đổi với ta.");
                }
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "5000 lấy 25.000.000 yên", () -> {
                if (this.yen > 1000000000) {
                    serverMessage("bạn chỉ có thể đổi khi dưới 1 tỷ yên");
                    return;
                }
                if (this.user.gold < 5000) {
                    serverMessage("Bạn cần có trên 5000 lượng");
                    return;
                }
                if (user.gold >= 5000) {
                    addGold(-5000);
                    addYen(25000000);
                    getService().npcChat(NpcName.OKANECHAN, "Đổi Thành Công");
                } else {
                    getService().npcChat(NpcName.OKANECHAN, "Ngươi cần 5000 lượng để đổi với ta.");
                }
            }));

            menus.add(new Menu(CMDMenu.EXECUTE, "50000 lấy 10.000.000 xu", () -> {
                if (this.coin > 1000000000) {
                    serverMessage("bạn chỉ có thể đổi khi dưới 1 tỷ xu");
                    return;
                }
                if (this.user.gold < 50000) {
                    serverMessage("Bạn cần có trên 50000 lượng");
                    return;
                }
                if (user.gold >= 50000) {
                    addGold(-50000);
                    addCoin(10000000);
                    getService().npcChat(NpcName.OKANECHAN, "Đổi Thành Công");
                } else {
                    getService().npcChat(NpcName.OKANECHAN, "Ngươi cần 50k lượng để đổi với ta.");
                }
            }));

            menus.add(new Menu(CMDMenu.EXECUTE, "500k lấy 100.000.000 xu", () -> {
                if (this.coin > 1000000000) {
                    serverMessage("bạn chỉ có thể đổi khi dưới 1 tỷ xu");
                    return;
                }
                if (this.user.gold < 500000) {
                    serverMessage("Bạn cần có trên 500k lượng");
                    return;
                }
                if (user.gold >= 500000) {
                    addGold(-500000);
                    addCoin(100000000);
                    getService().npcChat(NpcName.OKANECHAN, "Đổi Thành Công");
                } else {
                    getService().npcChat(NpcName.OKANECHAN, "Ngươi cần 500k lượng để đổi với ta.");
                }
            }));

            getService().openUIMenu();
        }));
         
        menus.add(new Menu(CMDMenu.EXECUTE, "ĐỔI VND", () -> {
            if (this.user.kh == 0) {
                serverMessage("Bạn phải kích hoạt để dùng tính năng này");
                return;
            }
            if (this.user.gold > 1000000000) {
                serverMessage("bạn chỉ có thể đổi khi dưới 1 tỷ lượng");
                return;
            }
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Xem số VND đang có", () -> {
                this.viewDiamond(this);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Xem Tổng Nạp", () -> {
                this.viewtongnap(this);
            }));
            //menus.add(new Menu(CMDMenu.EXECUTE, "đổi 10k coin lấy 4k lượng", () -> {
            //    this.doiluong10k(this);
            //}));
            //menus.add(new Menu(CMDMenu.EXECUTE, "đổi 20k coin lấy 9k lượng", () -> {
            //    this.doiluong20k(this);
            //}));
            //menus.add(new Menu(CMDMenu.EXECUTE, "đổi 50k coin lấy 26k lượng", () -> {
            //    this.doiluong50k(this);
            //}));
            //menus.add(new Menu(CMDMenu.EXECUTE, "đổi 100k coin lấy 55k lượng", () -> {
            //    this.doiluong100k(this);
            //}));
            //menus.add(new Menu(CMDMenu.EXECUTE, "đổi 200k coin lấy 115k lượng", () -> {
            //    this.doiluong200k(this);
            //}));
            //menus.add(new Menu(CMDMenu.EXECUTE, "đổi 500k coin lấy 290k lượng", () -> {
            //    this.doiluong500k(this);
            //}));
            //menus.add(new Menu(CMDMenu.EXECUTE, "đổi 1tr coin lấy 600k lượng", () -> {
            //    this.doiluong1000k(this);
            //}));
            getService().openUIMenu();
        }));
      
        menus.add(new Menu(CMDMenu.EXECUTE, "Nhận thưởng thăng cấp", () -> {
            if (this.user.kh == 0) {
                serverMessage("Bạn phải kích hoạt để dùng tính năng này");
                return;
            }
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Cấp 10", () -> {
                rewardLevel(0);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Cấp 20", () -> {
                rewardLevel(1);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Cấp 30", () -> {
                rewardLevel(2);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Cấp 40", () -> {
                rewardLevel(3);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Cấp 50", () -> {
                rewardLevel(4);
            }));
            getService().openUIMenu();
        }));
        
    }

    private void rewardLevel(int optionId) {
        int rewardLevel = (optionId + 1) * 10;
        if (this.level >= rewardLevel) {
            if (reward[optionId]) {
                getService().npcChat(NpcName.OKANECHAN, "Mỗi mốc chỉ được nhận một lần.");
                return;
            }
            if (getSlotNull() == 0) {
                warningBagFull();
                return;
            }
            reward[optionId] = true;
            Item ngoc = null;
            int gold = 0;
            switch (optionId) {
                case 0:
                    ngoc = ItemFactory.getInstance().newItem(223);
                    gold = 100;
                    break;

                case 1:
                    ngoc = ItemFactory.getInstance().newItem(224);
                    gold = 200;
                    break;

                case 2:
                    ngoc = ItemFactory.getInstance().newItem(225);
                    gold = 300;
                    break;

                case 3:
                    ngoc = ItemFactory.getInstance().newItem(226);
                    gold = 400;
                    break;

                case 4:
                    ngoc = ItemFactory.getInstance().newItem(227);
                    gold = 500;
                    break;
            }
            boolean rewarded = true;
            // if (Config.getInstance().SERVER == 2) {
            if (user.levelRewards[optionId] == 1) {
                rewarded = false;
            } else {
                user.levelRewards[optionId] = 1;
            }
            // }

            if (rewarded) {
                addGold(gold);
            }
            if (ngoc != null) {
                ngoc.isLock = true;
                addItemToBag(ngoc);
            }
        } else {
            getService().npcChat(NpcName.OKANECHAN, "Yêu cầu cấp " + rewardLevel + " để nhận thưởng");
        }
    }

    public void orderTaskDay() {
        for (TaskOrder task : this.taskOrders) {
            if (task.taskId == TaskOrder.TASK_DAY) {
                getService().npcChat(NpcName.RIKUDOU, "Nhiệm vụ lần trước ta giao, con vẫn chưa hoàn thành.");
                return;
            }
        }
        if (this.level < 30) {
            getService().npcChat(NpcName.RIKUDOU, "Con hãy có gắng luyện tập đạt cấp 30 rồi quay lại đây gặp ta.");
            return;
        }
        if (countFinishDay <= 0) {
            getService().npcChat(NpcName.RIKUDOU,
                    "Hôm nay con đã làm hết nhiệm vụ ta giao. Hãy quay lại vào ngày hôm sau..");
            return;
        }
        TaskOrder task = TaskFactory.getInstance().createTaskOrder(TaskOrder.TASK_DAY, this);
        if (task != null) {
            this.countFinishDay--;
            getService().sendTaskOrder(task);
            taskOrders.add(task);
            getService().npcChat(NpcName.RIKUDOU, "Đây là lần nhận nhiệm vụ thứ " + (20 - countFinishDay)
                    + " trong ngày hôm nay. Mỗi ngày được nhận tối đa 20 lần con nhé.");
        }
    }

    public void cancelTaskDay() {
        for (TaskOrder task : this.taskOrders) {
            if (task.taskId == TaskOrder.TASK_DAY) {
                getService().clearTaskOrder(task);
                taskOrders.remove(task);
                getService().npcChat(NpcName.RIKUDOU,
                        "Ta đã hủy nhiệm vụ của con. Lần sau cố gắng hoàn thành tốt nhiệm vụ con nhé.");
                return;
            }
        }
        getService().npcChat(NpcName.RIKUDOU, "Hiện tại con không có nhiệm vụ để hủy.");
    }

    public void finishTaskDay() {
        for (TaskOrder task : this.taskOrders) {
            if (task.taskId == TaskOrder.TASK_DAY) {
                if (task.isComplete()) {
                    getService().npcChat(NpcName.RIKUDOU, "Tốt lắm! Ta có phần thưởng dành cho con.");
                    this.pointUyDanh++;
                    addYen((int)NinjaUtils.nextInt(10000, 100000));
                    addGold((int)NinjaUtils.nextInt(50, 200));
                    if (this.clan != null) {
                        this.addClanPoint(10);
                    }
                    getService().clearTaskOrder(task);
                    taskOrders.remove(task);
                    if (taskId == TaskName.NV_TUAN_HOAN) {
                        if (taskMain != null && taskMain.index == 1) {
                            updateTaskCount(1);
                        }
                    }
                    if (taskId == TaskName.NV_HOAT_DONG_HANG_NGAY) {
                        if (taskMain != null && taskMain.index == 3) {
                            updateTaskCount(1);
                        }
                    }
                    if (Event.isKoroKing()) {
                        KoroKing.addTrophy(this);
                    }
                    if (Event.isEvent()) {
                        int itemID = Event.getEvent().randomItemID();
                        if (itemID != -1) {
                            Item itemE = ItemFactory.getInstance().newItem(itemID);
                            itemE.setQuantity((int)NinjaUtils.nextInt(1, 5));
                            addItemToBag(itemE);
                        }
                    }
                } else {
                    getService().npcChat(NpcName.RIKUDOU, "Con hãy hoàn thành nhiệm vụ rồi quay lại đây.");
                }
                return;
            }
        }
        getService().npcChat(NpcName.RIKUDOU,
                "Hãy làm việc cho ta bằng cách làm nhiệm vụ, sau khi hoàn thành con sẽ nhận được phần thưởng từ ta.");
    }

    public void workTaskDay() {
        for (TaskOrder task : this.taskOrders) {
            if (task.taskId == TaskOrder.TASK_DAY) {
                short map = (short) task.mapId;
                short[] xy = NinjaUtils.getFirstPosition(map);
                setXY(xy[0], xy[1]);
                changeMap(map);
                return;
            }
        }
        getService().npcChat(NpcName.RIKUDOU, "Hãy nhận nhiệm vụ mỗi ngày từ ta rồi mới sử dụng tính năng này.");
    }

    public void workTaskBoss() {
        for (TaskOrder task : this.taskOrders) {
            if (task.taskId == TaskOrder.TASK_BOSS) {
                short map = (short) task.mapId;
                short[] xy = NinjaUtils.getFirstPosition(map);
                setXY(xy[0], xy[1]);
                changeMap(map);
                return;
            }
        }
        getService().npcChat(NpcName.RIKUDOU, "Hãy nhận nhiệm vụ tà thú từ ta rồi mới sử dụng tính năng này.");
    }

    public void orderTaskBoss() {
        for (TaskOrder task : this.taskOrders) {
            if (task.taskId == TaskOrder.TASK_BOSS) {
                getService().npcChat(NpcName.RIKUDOU, "Nhiệm vụ lần trước ta giao, con vẫn chưa hoàn thành.");
                return;
            }
        }
        if (this.level < 30) {
            getService().npcChat(NpcName.RIKUDOU, "Con hãy có gắng luyện tập đạt cấp 30 rồi quay lại đây gặp ta.");
            return;
        }
        if (countLoopBoss <= 0) {
            getService().npcChat(NpcName.RIKUDOU,
                    "Nhiệm vụ truy bắt tà thú của ngày hôm nay đã hết con hãy quay lại vào ngày mai.");
            return;
        }
        TaskOrder task = TaskFactory.getInstance().createTaskOrder(TaskOrder.TASK_BOSS, this);
        if (task != null) {
            this.countLoopBoss--;
            getService().sendTaskOrder(task);
            taskOrders.add(task);
            getService().npcChat(NpcName.RIKUDOU, "Con hay sớm hoàn thành nhiệm vụ để nhận thưởng từ ta.");
        }
    }

    public void cancelTaskBoss() {
        for (TaskOrder task : this.taskOrders) {
            if (task.taskId == TaskOrder.TASK_BOSS) {
                getService().clearTaskOrder(task);
                taskOrders.remove(task);
                getService().npcChat(NpcName.RIKUDOU,
                        "Ta đã hủy nhiệm vụ của con. Lần sau cố gắng hoàn thành tốt nhiệm vụ con nhé.");
                return;
            }
        }
        getService().npcChat(NpcName.RIKUDOU, "Hiện tại con không có nhiệm vụ để hủy.");
    }

    public void finishTaskBoss() {
        for (TaskOrder task : this.taskOrders) {
            if (task.taskId == TaskOrder.TASK_BOSS) {
                if (task.isComplete()) {
                    getService().npcChat(NpcName.RIKUDOU, "Tốt lắm! Ta có phần thưởng dành cho con.");
                    Item item = ItemFactory.getInstance().newItem(251);
                    item.setQuantity(50);
                    addItemToBag(item);
                    if (this.clan != null) {
                        this.addClanPoint(10);
                    }
                    getService().clearTaskOrder(task);
                    taskOrders.remove(task);
                    if (taskId == TaskName.NV_TUAN_HOAN) {
                        if (taskMain != null && taskMain.index == 2) {
                            updateTaskCount(1);
                        }
                    }
                    if (taskId == TaskName.NV_HOAT_DONG_HANG_NGAY) {
                        if (taskMain != null && taskMain.index == 1) {
                            updateTaskCount(1);
                        }
                    }
                    if (Event.isKoroKing()) {
                        KoroKing.addTrophy(this);
                    }

                } else {
                    getService().npcChat(NpcName.RIKUDOU, "Con hãy hoàn thành nhiệm vụ rồi quay lại đây.");
                }
                return;
            }
        }
        getService().npcChat(NpcName.RIKUDOU,
                "Hãy làm việc cho ta bằng cách làm nhiệm vụ, sau khi hoàn thành con sẽ nhận được phần thưởng từ ta.");
    }

    public void npcRikudou() {
        if (mapId == 98 || mapId == 104) {
            menus.add(new Menu(CMDMenu.EXECUTE, "Rời khỏi nơi này", () -> {
                short[] xy = NinjaUtils.getXY((short) this.mapBeforeEnterPB);
                setXY(xy);
                changeMap(this.mapBeforeEnterPB);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Tổng kết", () -> {
                viewBattleSummary();
            }));
        } else {
            menus.add(new Menu(CMDMenu.EXECUTE, "Trang chủ", () -> {
                getService().openWeb("Trang chủ", "Hủy", "https://bophet.com", "Bạn muốn truy cập trang chủ?");
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "NV mỗi ngày", () -> {
                menus.clear();
                menus.add(new Menu(CMDMenu.EXECUTE, "Nhận", () -> {
                    orderTaskDay();
                }));
                menus.add(new Menu(CMDMenu.EXECUTE, "Hủy", () -> {
                    cancelTaskDay();
                }));
                menus.add(new Menu(CMDMenu.EXECUTE, "Hoàn thành", () -> {
                    finishTaskDay();
                }));
                menus.add(new Menu(CMDMenu.EXECUTE, "Đi làm NV", () -> {
                    workTaskDay();
                }));
                getService().openUIMenu();
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "NV Truy bắt Tà Thú", () -> {
                menus.clear();
                menus.add(new Menu(CMDMenu.EXECUTE, "Nhận", () -> {
                    orderTaskBoss();
                }));
                menus.add(new Menu(CMDMenu.EXECUTE, "Hủy", () -> {
                    cancelTaskBoss();
                }));
                menus.add(new Menu(CMDMenu.EXECUTE, "Hoàn Thành", () -> {
                    finishTaskBoss();
                }));
                menus.add(new Menu(CMDMenu.EXECUTE, "Đi làm NV", () -> {
                    workTaskBoss();
                }));
                getService().openUIMenu();
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Chiến trường", () -> {
                menus.clear();
                menus.add(new Menu(CMDMenu.EXECUTE, "Bạch giả", () -> {
                    joinBattle(0);
                }));
                menus.add(new Menu(CMDMenu.EXECUTE, "Hắc giả", () -> {
                    joinBattle(1);
                }));
                menus.add(new Menu(CMDMenu.EXECUTE, "Tổng kết", () -> {
                    viewBattleSummary();
                }));
                menus.add(new Menu(CMDMenu.EXECUTE, "Hướng dẫn", () -> {
                    getService().npcChat(NpcName.RIKUDOU, "Chiến trường là nơi hội tụ của 2 phe Nhẫn giả");
                }));
                menus.add(new Menu(CMDMenu.EXECUTE, "Top", () -> {
                    menus.add(new Menu(CMDMenu.EXECUTE, "Từ 30 đến 50", () -> {
                        menus.add(new Menu(CMDMenu.EXECUTE, "Top Tuần", () -> {
                            War.viewTop(this, War.TYPE_LEVEL_30_TO_50, War.TOP_WEEK);
                        }));
                        menus.add(new Menu(CMDMenu.EXECUTE, "Top Tháng", () -> {
                            War.viewTop(this, War.TYPE_LEVEL_30_TO_50, War.TOP_MONTH);
                        }));
                        getService().openUIMenu();
                    }));
                    menus.add(new Menu(CMDMenu.EXECUTE, "Từ 70 đến 90", () -> {
                        menus.add(new Menu(CMDMenu.EXECUTE, "Top Tuần", () -> {
                            War.viewTop(this, War.TYPE_LEVEL_70_TO_90, War.TOP_WEEK);
                        }));
                        menus.add(new Menu(CMDMenu.EXECUTE, "Top Tháng", () -> {
                            War.viewTop(this, War.TYPE_LEVEL_70_TO_90, War.TOP_MONTH);
                        }));
                        getService().openUIMenu();
                    }));
                    menus.add(new Menu(CMDMenu.EXECUTE, "Tất cả", () -> {
                        menus.add(new Menu(CMDMenu.EXECUTE, "Top Tuần", () -> {
                            War.viewTop(this, War.TYPE_ALL_LEVEL, War.TOP_WEEK);
                        }));
                        menus.add(new Menu(CMDMenu.EXECUTE, "Top Tháng", () -> {
                            War.viewTop(this, War.TYPE_ALL_LEVEL, War.TOP_MONTH);
                        }));
                        getService().openUIMenu();
                    }));
                    getService().openUIMenu();

                }));
                getService().openUIMenu();
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Thất thú ải", () -> {
                Calendar calendar = Calendar.getInstance();
                int d = calendar.get(Calendar.DAY_OF_WEEK);
                int h = calendar.get(Calendar.HOUR_OF_DAY);
                int m = calendar.get(Calendar.MINUTE);
                if (!(((h == 9 || h == 21) || (m < 30 && (h == 10 || h == 22))) && (d == 3 || d == 5 || d == 7))) {
                    getService().npcChat(NpcName.RIKUDOU,
                            "Thất thú ải chỉ mở 2 lần: từ 9-10h30' hoặc từ 21-22h30' vào thứ 3, 5, 7 hàng tuần.");
                    return;
                }
                if (this.level < 50) {
                    getService().npcChat(NpcName.RIKUDOU, "Yêu cầu trình độ cấp 50 trở lên mới có thể tham gia");
                    return;
                }
                SevenBeasts sevenBeasts = null;
                if (sevenBeastsID == -1) {
                    if (group == null || group.memberGroups.size() < 6) {
                        getService().npcChat(NpcName.RIKUDOU, "Phải lập nhóm và đủ 6 người thì ta mới cho vào.");
                        return;
                    }
                    if (group.memberGroups.get(0).charId != this.id) {
                        getService().npcChat(NpcName.RIKUDOU, "Chỉ nhóm trưởng mới được mở");
                        return;
                    }

                    int type = 0;
                    if (h == 9 && m < 30) {
                        type = 1;
                    }
                    if (h == 21 && m < 30) {
                        type = 2;
                    }
                    int rm = 30 - m;
                    sevenBeasts = new SevenBeasts(3600 + (rm * 60));
                    sevenBeasts.setMType((byte) type);
                    MapManager.getInstance().addSevenBeasts(sevenBeasts);
                    List<Char> chars = group.getChars();
                    for (Char p : chars) {
                        if (p.sevenBeastsID == -1) {
                            p.sevenBeastsID = sevenBeasts.getId();
                            p.typeSevenBeasts = sevenBeasts.getMType();
                            sevenBeasts.addCharId(p.id);
                        }
                    }
                    group.getGroupService().serverMessage("Nhóm trưởng đã mở thất thú ải");
                } else {
                    sevenBeasts = MapManager.getInstance().findSevenBeasts(sevenBeastsID);
                }
                if (sevenBeasts != null) {
                    if (sevenBeasts.isClosed()) {
                        getService().npcChat(NpcName.RIKUDOU, "Thất thú  ải đã đóng");
                        return;
                    }
                    if (sevenBeasts.getMType() != typeSevenBeasts) {
                        getService().npcChat(NpcName.RIKUDOU, "Mỗi ngày chỉ được tham gia một lần");
                        return;
                    }
                    addWorld(sevenBeasts);
                    this.mapBeforeEnterPB = mapId;
                    outZone();
                    setXY((short) 35, (short) 360);
                    sevenBeasts.join(this);
                }
            }));
        }
    }

    private void joinBattle(int optionId) {
        War warDaily = MapManager.getInstance().normalWar;
        if (warDaily != null) {
            if ((warDaily.type == War.TYPE_LEVEL_30_TO_50 && (this.level < 30 || this.level > 50))
                    || (warDaily.type == War.TYPE_LEVEL_70_TO_90 && (this.level < 70 || this.level > 90))) {
                getService().npcChat(NpcName.RIKUDOU, "Cấp độ không phù hợp");
                return;
            }
        }
        if (warDaily == null || warDaily.status == 2) {
            getService().npcChat(NpcName.RIKUDOU, "Chiến trường chưa mở đăng ký tham gia.");
            return;
        }
        if (warDaily.status == 0 || warDaily.status == 1) {
            if (this.faction != -1 && warDaily.time == this.time && this.faction != optionId) {
                getService().npcChat(NpcName.RIKUDOU, "Không thể chọn phe khác");
                return;
            }

            if (warDaily.status == 0 && this.faction == -1) {
                optionId = warDaily.numberJoinedWhite > warDaily.numberJoinedBlack ? 1 : 0;
                if (optionId == 0 && warDaily.numberJoinedWhite - warDaily.numberJoinedBlack > 10) {
                    getService().npcChat(NpcName.RIKUDOU, "Hãy đăng ký phe Hắc giả");
                    return;
                }
                if (optionId == 1 && warDaily.numberJoinedBlack - warDaily.numberJoinedWhite > 10) {
                    getService().npcChat(NpcName.RIKUDOU, "Hãy đăng ký phe Bạch giả");
                    return;
                }
            }

            if (warDaily.status == 1) {
                if (this.faction == -1 || this.time != warDaily.time) {
                    getService().npcChat(NpcName.RIKUDOU, "Con chưa đăng ký tham gia");
                    return;
                }
            }
            if (optionId == 0 && warDaily.whiteMembers.size() - warDaily.blackMembers.size() > 10) {
                getService().npcChat(NpcName.RIKUDOU, "Chênh lệch hai phe quá lớn hãy chờ người tham gia phe Hắc giả");
                return;
            }
            if (optionId == 1 && warDaily.blackMembers.size() - warDaily.whiteMembers.size() > 10) {
                getService().npcChat(NpcName.RIKUDOU, "Chênh lệch hai phe quá lớn hãy chờ người tham gia phe Bạch giả");
                return;
            }
            if (warDaily.status == 0 && (this.faction == -1 || this.time != warDaily.time)) {
                this.faction = (byte) optionId;
                this.warPoint = 0;
                if (optionId == 0) {
                    warDaily.numberJoinedWhite++;
                }
                if (optionId == 1) {
                    warDaily.numberJoinedBlack++;
                }
                this.time = warDaily.time;
                this.member = null;
                this.isRewarded = false;
            }
            short x = -1;
            short y = -1;
            int mapID = -1;
            if (faction == 0) {
                mapID = 98;
                x = 104;
                y = 336;
            }
            if (faction == 1) {
                mapID = 104;
                x = 104;
                y = 240;
            }
            if (mapID != -1) {
                mapBeforeEnterPB = this.mapId;
                member = getMemberNormalWar();
                if (member == null) {
                    member = new WarMember();
                    member.id = this.id;
                    member.name = this.name;
                    member.point = 0;
                    member.faction = this.faction;
                    member.type = (byte) warDaily.type;
                    warDaily.addMember(member);
                }
                setXY(x, y);
                changeMap(mapID);
                warDaily.addMember(this);
                war = warDaily;
            }
        }
    }

    private void joinBattleClan(int optionId, Char _char) {
        WarClan warDaily = MapManager.getInstance().warClan;

        if (warDaily == null || warDaily.status == 2) {
            getService().npcChat(NpcName.KAGAI, "Gia tộc chiến chưa mở để tham gia.");
            return;
        }
        if (warDaily.status == 0 || warDaily.status == 1) {

            if (warDaily.status == 0 && (_char.faction == -1 || _char.time != warDaily.time)) {
                _char.faction = (byte) optionId;
                _char.warPoint = 0;
                if (optionId == 0) {
                    warDaily.numberJoinedWhite++;
                }
                if (optionId == 1) {
                    warDaily.numberJoinedBlack++;
                }
                _char.time = warDaily.time;
                _char.member = null;
                _char.isRewarded = false;
            }
            short x = -1;
            short y = -1;
            int mapID = -1;
//            arrMapId = new short[]{117, 118, 119, 120, 121, 122, 123, 124};
            _char.faction = (byte) optionId;
            if (_char.faction == 0) {
                mapID = 117;
                x = 290;
                y = 456;
            }
            if (_char.faction == 1) {
                mapID = 124;
                x = 104;
                y = 240;
            }
            if (mapID != -1) {
                _char.mapBeforeEnterPB = this.mapId;
                _char.member = getMemberNormalWar();
                if (_char.member == null) {
                    _char.member = new WarMember();
                    _char.member.id = _char.id;
                    _char.member.name = _char.name;
                    _char.member.point = 0;
                    _char.member.faction = _char.faction;
//                    _char.member.type = (byte) warDaily.type;
                    warDaily.addMember(_char.member);
                }
                setXY(x, y);
                changeMap(mapID);
                warDaily.addMember(_char);
                _char.warClan = warDaily;
            }
        }
    }

    private void viewBattleSummary() {
        War warDaily = MapManager.getInstance().normalWar;
        if (mapId == 98 || mapId == 104) {
            warDaily = this.war;
        }
        if (warDaily != null) {
            warDaily.viewTop(this);
        } else {
            getService().npcChat(NpcName.RIKUDOU, "Chưa có dữ liệu.");
        }
    }

    public void npcUmayaki_1() {
        if (mapId >= 177 && mapId <= 182) {
            menus.add(new Menu(CMDMenu.EXECUTE, "về làng", () -> {
                teleport(22);
            }));
            return;
        }
        if (!isVillage()) {
            return;
        }
        menus.add(new Menu(CMDMenu.EXECUTE, "Nói chuyện", () -> {
            if (taskId == TaskName.NHIEM_VU_CHAO_LANG && taskMain != null && taskMain.index == 5) {
                taskNext();
            }
            String talk = (String) NinjaUtils.randomObject("Nhà ngươi muốn đi đâu?",
                    "Ngựa của ta rất khỏe có thể chạy ngàn dặm.", "Đi xe kéo của ta an toàn số một.");
            getService().npcChat(NpcName.UMAYAKI, talk);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Làng Kojin", () -> {
            teleport(10);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Làng Sanzu", () -> {
            teleport(17);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Làng Tone", () -> {
            teleport(22);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Làng chài", () -> {
            teleport(32);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Làng Chakumi", () -> {
            teleport(38);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Làng Echigo", () -> {
            teleport(43);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Làng Oshin", () -> {
            teleport(48);
        }));
    }

    private void teleport(int map) {
        if (isCanEnterMap(map)) {
            short[] xy = NinjaUtils.getXY(map);
            setXY(xy[0], xy[1]);
            changeMap(map);
        } else {
            serverDialog("Bạn chưa thể đến khu vực này. Hày hoàn thành nhiệm vụ trước.");
        }
    }

    private void teleportUPYEN() {
        if (this.level < 100) {
            serverDialog("Trình độ cấp 100 trở lên mới có thể sử dụng chức năng này.");
            return;
        }
        if (!isHuman) {
            getService().npcChat(NpcName.VUA_HUNG,
                    "Phân Thân Không Thể vào");
            return;
        }
//        if (this.level > 59 && this.level < 71) {
//        int mapID = 177;
//        short[] xy = NinjaUtils.getFirstPosition((short) mapID);
//        setXY(xy);
//        changeMap(mapID);
//        return;
//        }
//        if (this.level > 70 && this.level < 81){
//        int mapID = 178;
//        Map map = MapManager.getInstance().find(mapID);
//        short[] xy = NinjaUtils.getFirstPosition((short) mapID);
//        setXY(xy);
//        changeMap(mapID);
//        return;
//        }
//        if (this.level > 80 && this.level < 91) {
//        int mapID = 179;
//        Map map = MapManager.getInstance().find(mapID);
//        short[] xy = NinjaUtils.getFirstPosition((short) mapID);
//        setXY(xy);
//        changeMap(mapID);
//        return;
//        }
//        if (this.level > 90 && this.level < 101) {
//        int mapID = 180;
//        Map map = MapManager.getInstance().find(mapID);
//        short[] xy = NinjaUtils.getFirstPosition((short) mapID);
//        setXY(xy);
//        changeMap(mapID);
//        return;
//        }
        if (this.level >= 100 && this.level <= 110) {
            int mapID = 181;
            Map map = MapManager.getInstance().find(mapID);
            short[] xy = NinjaUtils.getFirstPosition((short) mapID);
            setXY(xy);
            changeMap(mapID);
            return;
        }
        if (this.level >= 111 && this.level <= 120) {
            int mapID = 182;
            Map map = MapManager.getInstance().find(mapID);
            short[] xy = NinjaUtils.getFirstPosition((short) mapID);
            setXY(xy);
            changeMap(mapID);
            return;
        }
        if (this.level >= 121 && this.level <= 130) {
            int mapID = 183;
            Map map = MapManager.getInstance().find(mapID);
            short[] xy = NinjaUtils.getFirstPosition((short) mapID);
            setXY(xy);
            changeMap(mapID);
            return;
        }
        if (this.level >= 131 && this.level <= 140) {
            int mapID = 184;
            Map map = MapManager.getInstance().find(mapID);
            short[] xy = NinjaUtils.getFirstPosition((short) mapID);
            setXY(xy);
            changeMap(mapID);
            return;
        }
        if (this.level >= 141) {
            int mapID = 185;
            Map map = MapManager.getInstance().find(mapID);
            short[] xy = NinjaUtils.getFirstPosition((short) mapID);
            setXY(xy);
            changeMap(mapID);
            return;
        }
    }

    private void teleportVDMQ() {
        if (this.level < 60) {
            serverDialog("Trình độ cấp 60 mới có thể sử dụng chức năng này.");
            return;
        }
        if (!isHuman) {
            Effect eff = em.findByID((byte) 34);
            if (eff == null) {
                getService().npcChat(NpcName.KAMAKURA,
                        "Con đã hết thời gian phưu lưu tại Vùng đất ma quỷ. Hãy sử dụng Thí luyện thiếp để tiếp tục phưu lưu.");
                return;
            }
        }
        setXY((short) 40, (short) 384);
        changeMap(139);
    }

    public void changeUsername() {
        InputDialog input = new InputDialog(CMDInputDialog.EXECUTE, "Tên đăng nhập mới", () -> {
            try {
                String newUsername = this.input.getText();
                if (newUsername.equals("") || newUsername.isEmpty()) {
                    serverDialog("Tên đăng nhập không được để trống.");
                    return;
                }

                setConfirmPopup(new ConfirmPopup(CMDConfirmPopup.CONFIRM,
                        String.format("Bạn có chắc chắn muốn đổi tên đăng nhập thành %s không?", newUsername), () -> {
                    try {
                        PreparedStatement stmt = DbManager.getInstance().getConnection(DbManager.UPDATE)
                                .prepareStatement(SQLStatement.CHECK_USERNAME, ResultSet.TYPE_SCROLL_SENSITIVE,
                                        ResultSet.CONCUR_READ_ONLY);
                        stmt.setString(1, newUsername);
                        ResultSet data = stmt.executeQuery();
                        if (data.first()) {
                            serverDialog("Tên đăng nhập đã tồn tại, vui lòng chọn một tên khác.");
                            return;
                        }

                        PreparedStatement stmt2 = DbManager.getInstance().getConnection(DbManager.UPDATE)
                                .prepareStatement(SQLStatement.UPDATE_USERNAME);
                        try {
                            stmt2.setString(1, newUsername);
                            stmt2.setInt(2, this.user.id);
                            stmt2.executeUpdate();
                        } finally {
                            stmt2.close();
                        }

                        serverDialog(String.format("Thay đổi thành công, tên đăng nhập mới của bạn là: %s.",
                                newUsername));
                    } catch (Exception e) {
                        e.printStackTrace();
                        serverDialog(String.format("Thay đổi thất bại.", newUsername));
                    }
                }));
                getService().openUIConfirmID();
            } catch (Exception e) {
                getService().serverDialog(e.getMessage());
            }
        });
        setInput(input);
        getService().showInputDialog();
    }

    public void npcTajima() {
        if (this.user.isDuplicate) {
            menus.add(new Menu(CMDMenu.EXECUTE, "Đổi tên đăng nhập", () -> {
                changeUsername();
            }));
        }
        if (this.level < 10 && equipment[ItemTemplate.TYPE_VUKHI] == null
                && getIndexItemByIdInBag(ItemName.KIEM_GO) == -1) {
            menus.add(new Menu(CMDMenu.EXECUTE, "Nhận kiếm gỗ", () -> {
                receiveWoodenSword();
            }));
        }
        menus.add(new Menu(CMDMenu.EXECUTE, "Hướng dẫn", () -> {
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Cách chơi", () -> {
                getService().showAlert("Trưởng làng", "Sử dụng các nút cảm ứng trên màn hình");
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Nhân vật", () -> {
                getService().showAlert("Trưởng làng",
                        "Kiếm, Kunai, Đao: Ưu tiên tăng sức mạnh (sức đánh) --> thể lực (HP) --> Thân pháp (Né đòn, chính xác) --> Chakra (MP).\n\nTiêu, Cung, Quạt: Ưu tiên tăng Chakra (Sức đánh, MP) ->thể lực (HP)--> Thân pháp(Né đòn, chính xác). Không tăng SM.");
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Chức năng PK", () -> {
                StringBuilder sb = new StringBuilder();
                sb.append("Pk thường: trạng thái hòa bình.").append("\n\n");
                sb.append(
                        "Pk phe: không đánh được người cùng nhóm hay cùng bang hội. Giết người không lên điểm hiếu chiến.")
                        .append("\n\n");
                sb.append("Pk dồ sát: có thể đánh tất cả người chơi. Giết 1 người sẽ lên 1 điểm hiếu chiến.")
                        .append("\n\n");
                sb.append("Điểm hiếu chiến cao sẽ không sử dụng bình HP, MP, Thức ăn.").append("\n\n");
                sb.append("Tỷ thí: chọn người chơi, chọn tỷ thí, chờ người đó đồng ý.").append("\n\n");
                sb.append("Cừu Sát: Chọn người chơi khác, chọn cừu sát, điểm hiếu chiến tăng 2").append("\n\n");
                getService().showAlert("Trưởng làng", sb.toString());
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Lập nhóm", () -> {
                StringBuilder sb = new StringBuilder();
                sb.append("Bạn có thể lập nhóm tối đa 6 người chơi.").append("\n\n");
                sb.append("Những người trong nhóm sẽ nhận được thêm x% điểm kinh nghiệm từ người khác.").append("\n\n");
                sb.append("Những người cùng nhóm sẽ cùng được vật phẩm, thành tích nếu cùng chung nhiệm vụ.")
                        .append("\n\n");
                sb.append(
                        "Để mời người vào nhóm, chọn người đó, và chọn mời vào nhóm. Để quản lý nhóm, chọn Menu/Tính năng/Nhóm")
                        .append("\n\n");
                getService().showAlert("Trưởng làng", sb.toString());
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Luyện đá", () -> {
                StringBuilder sb = new StringBuilder();
                sb.append(
                        "Đá dùng để nâng cấp trang bị. Bạn có thể mua từ cửa hàng hoặc nhặt khi đánh quái. Nâng cấp đá nhằm mực đích nâng cao tỉ lệ thành công khi nâng cấp trang bị cấp cao. Để luyện đá, bạn cần tìm Kenshinto.")
                        .append("\n\n");
                sb.append("Để đảm bảo thành công 100%, 4 viên đá cấp thấp sẽ luyện thành 1 viên đá cấp cao hơn.")
                        .append("\n\n");
                getService().showAlert("Trưởng làng", sb.toString());
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Nâng cấp", () -> {
                StringBuilder sb = new StringBuilder();
                sb.append(
                        "Nâng cấp trang bị nhằm mục đích gia tăng các chỉ số cơ bản của trang bị. Có các cấp trang bị sau +1, +2, +3, .. tối đa +16. Để thực hiện, bạn cần gặp NPC Kenshinto. Sau đó, tiến hành chọn vật phẩm và số lượng đá đủ để nâng cấp. Lưu ý, trang bị cấp độ 5 trở lên nâng cấp thất bại sẽ bị giảm cấp độ.")
                        .append("\n\n");
                sb.append(
                        "Bạn có thể tách một vật phẩm đã nâng cấp và thu lại 50% số đá đã dùng để nâng cấp trang bị đó.")
                        .append("\n\n");
                getService().showAlert("Trưởng làng", sb.toString());
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Điểm hoạt động", () -> {
                StringBuilder sb = new StringBuilder();
                sb.append(
                        "Khi tham gia các hoạt động trong game bạn sẽ nhận được điểm hoạt động. Qua một ngày điểm hoạt động sẽ bị trừ dần (nếu từ 1-49 trừ 1, 50-99 trừ 2, 100-149 trừ 3 ...). Mỗi tuần bạn sẽ có cơ hội đổi Yên sang Xu nếu có đủ điển hoạt động theo yêu cầu của NPC Okanechan.")
                        .append("\n\n");
                sb.append("Một tuần một lần duy nhất được đổi tối đa 70.000 Yên = 70.000 xu.").append("\n\n");
                getService().showAlert("Trưởng làng", sb.toString());
            }));
            getService().openUIMenu();
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Nói chuyện", () -> {
            if (taskMain != null) {
                getService().npcChat(NpcName.TAJIMA, "Con đang làm nhiệm vụ " + taskMain.template.getName()
                        + ", hãy chọn Menu/Nhiệm vụ để biết mình đang làm tới đâu.");
            } else {
                String talk = (String) NinjaUtils.randomObject("Đi thưa, về trình, nhé các con",
                        "Làng Tone là ngôi làng cổ xưa, đã có từ rất lâu.",
                        "Ta là Tajima, mọi việc ở đây đều do ta quản lý");
                getService().npcChat(NpcName.TAJIMA, talk);
            }
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Hủy vật phẩm và nhiệm vụ", () -> {
            if (isHuman) {
                clearTask();
            }
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Thứ thân", () -> {
            if (isHuman && clone != null && clone.isNhanBan && !clone.isDead && timeCountDown > 0) {
                clone.switchToMe();
            }
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Chủ thân", () -> {
            if (!isHuman && !isNhanBan) {
                ((CloneChar) this).human.switchToMe();
            }
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Nhận Top", () -> {
           this.rewardtop(this);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Chuyển tới Làng TONE mới ", () -> {
            int mapID = 187;
            Map map = MapManager.getInstance().find(mapID);
            short[] xy = NinjaUtils.getFirstPosition((short) mapID);
            setXY(xy);
            changeMap(mapID);
            return;
        }));
    }

    public void switchToMe() {
        Controller contrl = (Controller) user.session.getMessageHandler();
        contrl.setChar(this);
        Service sv = user.session.getService();
        sv.setChar(this);
        Zone z = clone.zone;
        clone.outZone();
        clone.outParty();
        clone.isNhanBan = true;
        clone.getEm().clearScrAllEffect(sv, z.getService(), clone);
        setAbility();
        this.hp = this.maxHP;
        this.mp = this.maxMP;
        setXY(clone.x, clone.y);
        setFashion();
        z.join(this);
        getService().sendSkillShortcut("OSkill", onOSkill, (byte) 0);
        getService().sendSkillShortcut("KSkill", onKSkill, (byte) 0);
        getService().sendSkillShortcut("CSkill", onCSkill, (byte) 0);
        getService().updateInfoMe();
        getService().onBijuuInfo(id, bijuu);
        em.displayAllEffect(sv, z.getService(), this);
        getService().loadMount(this);
    }

    public void npcUmayaki_2() {
        if (!isSchool()) {
            return;
        }
        menus.add(new Menu(CMDMenu.EXECUTE, "Trường Hirosaki", () -> {
            teleport(1);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Trường Haruna", () -> {
            teleport(27);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Trường Ookaza", () -> {
            teleport(72);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Nói chuyện", () -> {
            if (taskId == TaskName.NHIEM_VU_CHAO_LANG && taskMain != null && taskMain.index == 5) {
                taskNext();
            }
            String talk = (String) NinjaUtils.randomObject("Nhà ngươi muốn đi đâu?",
                    "Ngựa của ta rất khỏe có thể chạy ngàn dặm.", "Đi xe kéo của ta an toàn số một.");
            getService().npcChat(NpcName.UMAYAKI_2, talk);
        }));
    }

    public void npcTruCoQuan() {
        menus.add(new Menu(CMDMenu.EXECUTE, "Cắm chìa khóa", () -> {
            Territory territory = (Territory) findWorld(World.TERRITORY);
            if (territory != null) {
                int indexUI = getIndexItemByIdInBag(ItemName.CHIA_KHOA_LANH_DIA_GIA_TOC);
                if (indexUI == -1) {
                    serverMessage("Không có chìa khóa.");
                    return;
                }
                territory.openMap(this);
                removeItem(indexUI, 1, true);
            }
        }));
    }

    public void npcTashino() {

        menus.add(new Menu(CMDMenu.EXECUTE, "Luyện bí kíp", () -> {
            if (this.equipment[ItemTemplate.TYPE_BIKIP] != null) {
                if (this.user.gold >= 1000) {
                    if (getSlotNull() == 0) {
                        getService().npcChat(NpcName.TASHINO, "Hãy chừa 1 ô trống trong hành trang để nhận bí kíp.");
                        return;
                    }
                    addGold(-1000);
                    int itemId = this.equipment[ItemTemplate.TYPE_BIKIP].id;
                    getService().deleteItemBody(ItemTemplate.TYPE_BIKIP);
                    this.equipment[ItemTemplate.TYPE_BIKIP] = null;
                    Item item = ItemFactory.getInstance().newItem(itemId);
                    item.isLock = true;
                    int random = (int)NinjaUtils.nextInt(1, 5);
                    ArrayList<ItemOption> options = new ArrayList<>();
                    options.add(new ItemOption(81, (int)NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(82, (int)NinjaUtils.nextInt(500, 1500)));
                    options.add(new ItemOption(83, (int)NinjaUtils.nextInt(500, 1500)));
                    options.add(new ItemOption(84, (int)NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(86, (int)NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(87, (int)NinjaUtils.nextInt(100, 800)));
                    options.add(new ItemOption(88, (int)NinjaUtils.nextInt(100, 1000)));
                    options.add(new ItemOption(89, (int)NinjaUtils.nextInt(100, 1000)));
                    options.add(new ItemOption(90, (int)NinjaUtils.nextInt(100, 1000)));
                    options.add(new ItemOption(91, (int)NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(92, (int)NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(95, (int)NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(96, (int)NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(97, (int)NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(98, (int)NinjaUtils.nextInt(5, 10)));
                    options.add(new ItemOption(99, (int)NinjaUtils.nextInt(100, 300)));
//                  exp
                    options.add(new ItemOption(100, (int)NinjaUtils.nextInt(5, 10)));

                    item.options.add(new ItemOption(85, 0));
                    for (int i = 0; i < random; i++) {
                        int index = 0;
                        if (options.get(options.size() - 1).optionTemplate.id == 100) {
                            if ((int)NinjaUtils.nextInt(100) < 30) {
                                index = options.size() - 1;
                            }
                        } else {
                            index = (int)NinjaUtils.nextInt(options.size());
                        }
                        ItemOption iop = options.get(index);
                        item.options.add(iop);
                        options.remove(index);
                    }
                    addItemToBag(item);
                    setFashion();
                    setAbility();
                    String text = "";
                    if (random == 1 || random == 2) {
                        text = "Ta chỉ giúp được cho ngươi đến thế thôi, ta xin lỗi.";
                    } else if (random == 3 || random == 4) {
                        text = "Không tệ ngươi xem có ổn không.";
                    } else {
                        text = "Khá mạnh đó, ngươi thấy ta làm tốt không?";
                    }
                    getService().npcChat(NpcName.TASHINO, text);
                } else {
                    getService().npcChat(NpcName.TASHINO, "Hãy đưa ta 1.000 lượng ta mới giúp ngươi.");
                }
            } else {
                getService().npcChat(NpcName.TASHINO,
                        "Ngươi cần đeo bí kíp trên người đã rồi luyện nhé .");
            }
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Nói chuyện", () -> {
            String talk = (String) NinjaUtils.randomObject(
                    "Ta có thể giúp ngươi làm tăng sức mạnh cho bí kíp, ngươi chỉ cần trả cho ta ít ngân lượng.",
                    "Nhanh lên kẻo bị phát hiện.", "Chắc chắn ngươi sẽ cần ta giúp.");
            getService().npcChat(NpcName.TASHINO, talk);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Nâng cấp bí kíp", () -> {
            int itemID = -1;
            switch (getSys()) {
                case 1:
                    itemID = 834;
                    break;

                case 2:
                    itemID = 836;
                    break;

                case 3:
                    itemID = 835;
                    break;
            }
            if (itemID == -1) {
                return;
            }
            Item item = this.equipment[ItemTemplate.TYPE_BIKIP];
            if (item != null) {
                int cap = 0;
                for (ItemOption option : item.options) {
                    if (option.optionTemplate.id == 85) {
                        cap = option.param;
                        break;
                    }
                }
                if (cap >= 16) {
                    getService().npcChat(NpcName.TASHINO,
                            "Bí kíp của ngươi đã quá mạnh, ta không thể giúp được ngươi.");
                    return;
                }
                int quantity = (cap + 1) * 3;
                int fee = (cap + 5) * 2;
                int percent = GameData.UP_BI_KIP[cap];
                String name = ItemManager.getInstance().getItemName(itemID);
                setConfirmPopup(new ConfirmPopup(CMDConfirmPopup.NANG_BI_KIP, String.format(
                        "Ngươi có muốn nâng cấp bí kíp đang sử dụng lên cấp %d không? Cần %d viên %s với phí %d lượng - Tỉ lệ thành công: %s.",
                        (cap + 1), quantity, name, fee, percent + "%")));
                getService().openUIConfirmID();
            } else {
                getService().npcChat(NpcName.TASHINO,
                        "Ngươi hãy sử dụng bí kíp ta sẽ giúp ngươi làm thay đổi sức mạnh.");
            }
        }));
    }

    public void npcCayThong() {
        if (!Event.isNoel()) {
            serverMessage("Vật phẩm này chỉ sử dụng được trong sự kiện Noel.");
            return;
        }
        menus.add(new Menu(CMDMenu.EXECUTE, "Trang trí", () -> {
            if (getSlotNull() == 0) {
                warningBagFull();
                return;
            }
            int indexUI = getIndexItemByIdInBag(ItemName.QUA_TRANG_TRI);
            if (indexUI == -1) {
                serverMessage("Không có quà trang trí.");
                return;
            }
            Event event = Event.getEvent();
            Item item = this.bag[indexUI];
            getEventPoint().addPoint(Noel.TOP_DECORATION_GIFT_BOX, 1);
            getEventPoint().addPoint(EventPoint.DIEM_TIEU_XAI, 1);
            event.useEventItem(this, item.id, event.getItemsRecFromGold2Item());
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Hướng dẫn", () -> {
            getService().showAlert("Hướng dẫn",
                    "Để nhận thưởng tại cây thông may mắn, các bạn cần phải có Quà Trang Trí, với mỗi Quà Trang trí đặt tại cây thông bạn sẽ nhận thêm được một phần thưởng kinh nghiệm cùng với một phần quà ngẫu nhiên.");
        }));
    }

    public void npcHoaMai() {
        if (!Event.isLunarNewYear()) {
            serverMessage("Chỉ sử dụng được trong sự kiện tết.");
            return;
        }
        menus.add(new Menu(CMDMenu.EXECUTE, "May mắn đầu xuân", () -> {
            if (level < 20) {
                serverMessage("Không đủ sức khỏe.");
            }
            if (getSlotNull() == 0) {
                warningBagFull();
                return;
            }
            int indexUI = getIndexItemByIdInBag(ItemName.BUA_MAY_MAN);
            if (indexUI == -1) {
                serverMessage("Không có bùa may mắn.");
                return;
            }
            Event event = Event.getEvent();
            Item item = this.bag[indexUI];
            getEventPoint().addPoint(LunarNewYear.TOP_LUCKY_CHARM, 1);
            getEventPoint().addPoint(EventPoint.DIEM_TIEU_XAI, 1);
            event.useEventItem(this, item.id, event.getItemsRecFromGold2Item());
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Hướng dẫn", () -> {
            getService().showAlert("Hướng dẫn",
                    "- Để nhận thưởng tại cây mai, các bạn cần phải có Bùa May Mắn\n- Với mỗi Bùa May Mắn treo tại cây mai bạn sẽ nhận thêm được một phần thưởng kinh nghiệm cùng với một phần quà ngẫu nhiên.");
        }));
    }

    public void npcEmBe() {
        if (Events.event != Events.SUMMER) {
            return;
        }
        menus.add(new Menu(CMDMenu.EXECUTE, "Cho ăn kem", () -> {
            if (getSlotNull() == 0) {
                warningBagFull();
                return;
            }
            if (this.level < 20) {
                getService().npcChat(NpcName.EM_BE, "Ngươi còn chưa đạt cấp 20");
                return;
            }
            int priceGold = 0;
            int[][] itemRequires = new int[][]{{ItemName.HU_KEM_DAM, 1}};
            RandomCollection<Integer> rc = RandomItem.BUA_MAY_MAN;
            boolean isDone = Events.useEventItem(this, 1, itemRequires, priceGold, rc);
            if (isDone) {
                // addEventPoint(1, Events.TOP_ICE_CREAM);
            }
        }));
    }

    public void npcVuaHung() {
        Char _char = zone.findCharById(id);
        menus.add(new Menu(CMDMenu.EXECUTE, "Mốc nạp", () -> {
            if (this.user.kh == 0) {
                serverMessage("Bạn phải kích hoạt để dùng tính năng này");
                return;
            }
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Xem Tổng Nạp", () -> {
                this.viewtongnap(this);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "TOP NẠP", () -> {
                showRankedList(4);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Nhận mốc nạp 100k", () -> {
                this.mocnap100k(this);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Nhận mốc nạp 500k", () -> {
                this.mocnap500k(this);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Nhận mốc nạp 1tr", () -> {
                this.mocnap1tr(this);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Nhận mốc nạp 2tr", () -> {
                this.mocnap2tr(this);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Nhận mốc nạp 5tr", () -> {
                this.mocnap5tr(this);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Nhận mốc nạp 10tr", () -> {
                this.mocnap10tr(this);
            }));
            getService().openUIMenu();
            return;
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Dâng Thánh Vật", () -> {
            for (int i = 859; i <= 863; i++) {
                int indexUI = getIndexItemByIdInBag(i);
                if (indexUI == -1 || bag[indexUI] == null || !bag[indexUI].has()) {
                    getService().npcChat(NpcName.VUA_HUNG, "Hãy thu thập đủ 5 thánh vật rồi đến đây gặp ta");
                    return;
                }
            }

            for (int i = 859; i <= 863; i++) {
                removeItem(getIndexItemByIdInBag(i), 1, true);
            }

            RandomCollection<Integer> rc = RandomItem.THANH_VAT;
            int itemId = rc.next();
            Item itm = ItemFactory.getInstance().newItem(itemId);
            itm.isLock = false;
            itm.expire = System.currentTimeMillis();

            long month = (int)NinjaUtils.nextInt(1, 3);
            long expire = 86400000; // 1 day
            expire *= 30; // 30 day
            expire *= month;
            itm.expire += expire;
            if ((int)NinjaUtils.nextInt(1, 10) == 10) {
                itm.expire = -1;
            }

            if (itm.id == ItemName.MAT_NA_HO) {
                itm.randomOptionTigerMask();
            }

            addItemToBag(itm);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Tới Map Úp Yên", () -> {
            teleportUPYEN();
            return;
        }));
       /* menus.add(new Menu(CMDMenu.EXECUTE, "Mua điểm danh vọng(50k lượng)", () -> {
            if (user.gold >= 50000) {
                addGold(-50000);
                int point = 100;
                pointNon += point;
                pointVuKhi += point;
                pointAo += point;
                pointLien += point;
                pointGangTay += point;
                pointNhan += point;
                pointNgocBoi += point;
                pointQuan += point;
                pointPhu += point;
                pointGiay += point;
                serverDialog("đã tăng 100 điểm danh vọng mỗi loại!");
            } else {
                serverDialog("Không đủ lượng!");
            }
            return;
        }));*/
    /*   menus.add(new Menu(CMDMenu.EXECUTE, "Bỏ qua +1 nhiệm vụ !", () -> {
                if (_char.taskId > 0 && _char.taskId < 43){
                if (_char.user.gold >= 5000) {
                    getService().npcChat(NpcName.VUA_HUNG, "Ta đã hoàn thành giúp con rồi đó, nhớ thoát game vào lại để UPDATE nhiệm vụ!");
                    _char.addGold(-5000);
                    _char.taskId += 1;
                    _char.taskMain = null;
                    finishTask(true);
                   //getService().updateInfoMe();
                    
                } else {
                    serverDialog("Không đủ 5k lượng!");
                }
                return;
                }else{
           getService().npcChat(NpcName.VUA_HUNG, "Chưa có nhiệm vụ hoặc đã làm xong !"); 
        }
            }));*/
       // return; 
    }

    public void npcKanata() {
        if (mapId == 110) {
            menus.add(new Menu(CMDMenu.EXECUTE, "Rời khỏi nơi này", () -> {
                short[] xy = NinjaUtils.getXY(mapBeforeEnterPB);
                setXY(xy[0], xy[1]);
                changeMap(mapBeforeEnterPB);
            }));

            menus.add(new Menu(CMDMenu.EXECUTE, "Đặt cược", () -> {
                if (this.group != null && !this.group.isCheckLeader(this)) {
                    getService().npcChat(NpcName.KANATA, "Chỉ nhóm trưởng mới có thể đặt cược.");
                    return;
                }
                InputDialog input = new InputDialog(CMDInputDialog.DAT_CUOC, "Số tiền đặt cược");
                setInput(input);
                getService().showInputDialog();
            }));

            menus.add(new Menu(CMDMenu.EXECUTE, "Nói chuyện", () -> {
                getService().npcChat(NpcName.KANATA, "Ta không có chuyện gì để nói với ngươi.");
            }));
        } else if (mapId == MapName.DAU_TRUONG) {
            MapManager.getInstance().talentShow.showMenu(this);
        } else {
            menus.add(new Menu(CMDMenu.EXECUTE, "Binh khí", () -> {
                if (this.user.kh == 0) {
                    serverMessage("Bạn phải kích hoạt để dùng tính năng này");
                    return;
                }
                getService().openUI((byte) StoreManager.TYPE_WEAPON);
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Gia tộc", () -> {
                if (this.user.kh == 0) {
                    serverMessage("Bạn phải kích hoạt để dùng tính năng này");
                    return;
                }
                menus.clear();
                if (clan == null) {
                    menus.add(new Menu(CMDMenu.EXECUTE, "Thành lập", () -> {
                        if (clan == null) {
                            if (user.gold < 1500) {
                                getService().npcChat(NpcName.KANATA, "Phí thành lập 1.500 lượng.");
                                return;
                            }
                            InputDialog input = new InputDialog(CMDInputDialog.TAO_GIA_TOC, "Tên gia tộc");
                            setInput(input);
                            getService().showInputDialog();
                        }
                    }));
                } else {
                    menus.add(new Menu(CMDMenu.EXECUTE, "Lãnh địa gia tộc", () -> {
                        if (this.clan != null) {
                            Territory ter = Territory.getTerritory(this.clan.id);
                            Member member = this.clan.getMemberByName(this.name);
                            if (member == null) {
                                return;
                            }
                            int typeClan = member.getType();
                            boolean isClanLeader = typeClan == Clan.TYPE_TOCTRUONG || typeClan == Clan.TYPE_TOCPHO;

                            if (ter == null) {
                                if (clan.getOpenDun() <= 0) {
                                    getService().npcChat(NpcName.KANATA,
                                            "Gia tộc của ngươi đã hết lượt tham gia lãnh địa.");
                                    return;
                                }
                                if (getSlotNull() == 0) {
                                    getService().npcChat(NpcName.KANATA,
                                            "Hãy để lại 1 slot trống trong rương để nhận chìa khoá.");
                                    return;
                                }
                                if (!isClanLeader) {
                                    getService().npcChat(NpcName.KANATA,
                                            "Để mở lãnh địa gia tộc cần những thành viên cấp cao như Tộc Trưởng và Tộc Phó.");
                                    return;
                                }

                                ter = new Territory(this.clan.id);
                                Territory.addTerritory(this.clan.id, ter);

                                Item newItem = ItemFactory.getInstance().newItem(ItemName.CHIA_KHOA_LANH_DIA_GIA_TOC);
                                newItem.isLock = true;
                                newItem.expire = System.currentTimeMillis() + 600000;
                                addItemToBag(newItem);
                                this.clan.openDun -= 1;
                                this.clan.getClanService()
                                        .serverMessage(this.name + " đã cửa lãnh địa, hãy gặp Katana để điểm danh");
                            }

                            if (!isClanLeader && !ter.isInGuestList(this.id)) {
                                getService().npcChat(NpcName.KANATA, "Ngươi chưa được tộc trưởng mời vào lãnh địa.");
                                return;
                            }

                            if (ter.started && !ter.isInTerritory(this.id)) {
                                getService().npcChat(NpcName.KANATA,
                                        "Lãnh địa gia tộc đã bắt đầu, ngươi hãy tham gia vào lần sau.");
                                return;
                            }

                            addWorld(ter);
                            this.mapBeforeEnterPB = mapId;
                            setXY((short) 191, (short) 144);
                            outZone();
                            joinZone(80, -1, -1);
                        } else {
                            getService().npcChat(NpcName.KANATA, "Ngươi đã có gia tộc đéo đâu.");
                        }
                    }));
                    menus.add(new Menu(CMDMenu.EXECUTE, "Nhận ấn tộc", () -> {
                        if (this.clan != null) {
                            Member member = this.clan.getMemberByName(this.name);
                            if (member == null) {
                                return;
                            }
                            if (member.getPointClanWeek() < 1000) {
                                getService().npcChat(NpcName.KANATA,
                                        "Ngươi cần tối thiểu 1.000 điểm cống hiến tuần để đổi lấy ấn tộc cấp 1");
                                return;
                            }
                            if (getSlotNull() == 0) {
                                getService().npcChat(NpcName.KANATA,
                                        "Hãy chừa ra một chỗ trống trong hành trang của ngươi.");
                                return;
                            }
                            member.addPointClanWeek(-1000);
                            Item newItem = ItemFactory.getInstance().newItem(870);
                            newItem.isLock = true;
                            addItemToBag(newItem);
                        } else {
                            getService().npcChat(NpcName.KANATA, "Ngươi đã có gia tộc đéo đâu.");
                        }
                    }));
                    menus.add(new Menu(CMDMenu.EXECUTE, "Giấy phép nâng cấp", () -> {
                        if (this.clan != null) {
                            Member member = this.clan.getMemberByName(this.name);
                            if (member == null) {
                                return;
                            }
                            if (member.getPointClanWeek() < 350) {
                                getService().npcChat(NpcName.KANATA,
                                        "Ngươi cần tối thiểu 350 điểm cống hiến tuần để đổi lấy 1 giấy phép nâng cấp.");
                                return;
                            }
                            if (getSlotNull() == 0) {
                                getService().npcChat(NpcName.KANATA,
                                        "Hãy chừa ra một chỗ trống trong hành trang của ngươi.");
                                return;
                            }
                            member.addPointClanWeek(-350);
                            Item newItem = ItemFactory.getInstance().newItem(ItemName.GIAY_PHEP_NANG_CAP);
                            newItem.isLock = true;
                            addItemToBag(newItem);
                        } else {
                            getService().npcChat(NpcName.KANATA, "Ngươi phải có gia tộc trước đã.");
                        }
                    }));
                }
                menus.add(new Menu(CMDMenu.EXECUTE, "Thăng ấn", () -> {
                    if (fashion[13] == null) {
                        serverMessage("Hãy sử dụng Ấn tộc để sử dụng chức năng này.");
                        return;
                    }
                    if (fashion[13].upgrade >= 10) {
                        serverMessage("Ấn tộc đã đạt cấp tối đa.");
                        return;
                    }
                    int[] costs = {1000000, 1750000, 2250000, 3500000, 5200000, 7500000, 8200000, 10500000, 15000000};
                    int[] costs2 = {1, 1, 2, 2, 3, 3, 4, 5, 6};
                    int[] percents = {50, 35, 25, 20, 15, 12, 10, 8, 5};
                    int index = fashion[13].upgrade - 1;
                    setConfirmPopup(new ConfirmPopup(CMDConfirmPopup.NANG_AN_TOC,
                            "Bạn có muốn thăng cấp " + fashion[13].template.name + " với " + costs2[index]
                            + " giấy phép nâng cấp + " + costs[index] + " xu hoặc yên có tỉ lệ thành công là "
                            + percents[index] + "% không?"));
                    getService().openUIConfirmID();
                }));
                menus.add(new Menu(CMDMenu.EXECUTE, "Hướng dẫn", () -> {
                    getService().showAlert("Hướng dẫn",
                            "Thuở xa xưa, các vị thánh nhân trong các gia tộc đứng đầu thế giới Ninja đã truyền lại bí kíp để luyện thành Ấn Tộc. Bằng cách tham gia chiến đấu với các quái vật trong lãnh địa, thu thập các viên đá linh hồn và tích lũy điểm cống hiến. Với mỗi 350 điểm sẽ đổi được 1 giấy phép nâng cấp. Kết hợp những thứ đó sẽ luyện thành công");
                }));
                if (this.clan != null) {
                    menus.add(new Menu(CMDMenu.EXECUTE, "Hủy gia tộc", () -> {
                        cancelClan();
                        if (this.clan == null) {
                            getService().npcChat(NpcName.KANATA, "Đừng sai lầm nữa nhé");
                        }
                    }));
                }
                getService().openUIMenu();

            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Hang động sau trường", () -> {
                menus.clear();
                menus.add(new Menu(CMDMenu.EXECUTE, "Nhận thưởng", () -> {
                    getService().reviewDungeon();
                }));
                menus.add(new Menu(CMDMenu.EXECUTE, "Cấp 35", () -> {
                    openDungeon(1);
                }));
                menus.add(new Menu(CMDMenu.EXECUTE, "Cấp 45", () -> {
                    openDungeon(2);
                }));
                menus.add(new Menu(CMDMenu.EXECUTE, "Cấp 55", () -> {
                    openDungeon(3);
                }));
                menus.add(new Menu(CMDMenu.EXECUTE, "Cấp 65", () -> {
                    openDungeon(4);
                }));
                menus.add(new Menu(CMDMenu.EXECUTE, "Cấp 75", () -> {
                    openDungeon(5);
                }));
                menus.add(new Menu(CMDMenu.EXECUTE, "Cấp 95", () -> {
                    openDungeon(6);
                }));
                getService().openUIMenu();
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Lôi đài", () -> {
                menus.clear();
                menus.add(new Menu(CMDMenu.EXECUTE, "Thách đấu", () -> {
                    if (Arena.arenas.size() == 127) {
                        getService().npcChat(NpcName.KANATA, "Lôi dài quá tải. Vui lòng quay lại sau.");
                    } else {
                        InputDialog input = new InputDialog(CMDInputDialog.THACH_DAU, "Nhập tên nhân vật");
                        setInput(input);
                        getService().showInputDialog();
                    }
                }));
                menus.add(new Menu(CMDMenu.EXECUTE, "Xem thi đấu", () -> {
                    getService().testDunageonList();
                }));
                menus.add(new Menu(CMDMenu.EXECUTE, "Kết quả", () -> {
                    String content = "";
                    int size = Arena.results.size();
                    size = size > 20 ? 20 : size;
                    for (int i = 0; i < size; i++) {
                        content += (i + 1) + ". " + Arena.results.get(i) + "\n";
                    }
                    getService().showAlert("Kết quả", content);
                }));
                getService().openUIMenu();
            }));

            TalentShow talentShow = MapManager.getInstance().talentShow;
            if (talentShow.opened) {
                menus.add(new Menu(CMDMenu.EXECUTE, "Ninja tài năng", () -> {
                    menus.clear();
                    int tlsFaction = talentShow.getFactionByName(name);
                    if (tlsFaction != -1) {
                        menus.add(new Menu(CMDMenu.EXECUTE, "Tham gia", () -> {
                            TalentShow tls = MapManager.getInstance().talentShow;
                            if (!tls.opened) {
                                getService().npcChat(NpcName.KANATA, "Ninja tài năng chưa mở cửa hoặc đã kết thúc.");
                                return;
                            }
                            this.mapBeforeEnterPB = mapId;
                            int faction = tls.getFactionByName(name);

                            if (faction != -1) {
                                tls.addPlayer(name, faction);
                            }
                        }));
                    }
                    if (tlsFaction == -1) {
                        menus.add(new Menu(CMDMenu.EXECUTE, "Xem thi đấu", () -> {
                            TalentShow tls = MapManager.getInstance().talentShow;
                            if (!tls.opened) {
                                getService().npcChat(NpcName.KANATA, "Ninja tài năng chưa mở cửa hoặc đã kết thúc.");
                                return;
                            }
                            this.mapBeforeEnterPB = mapId;
                            int randPointX = (int)NinjaUtils.nextInt(70, 650);
                            setXY((short) randPointX, (short) 384);
                            changeMap(MapName.DAU_TRUONG);
                        }));
                    }
                    getService().openUIMenu();
                }));
            }

            if (this.user.isAdmin()) {
                menus.add(new Menu(CMDMenu.EXECUTE, talentShow.opened ? "Tắt TLS" : "Bật TLS", () -> {
                    TalentShow tls = MapManager.getInstance().talentShow;
                    tls.opened = !tls.opened;
                }));
            }

            menus.add(new Menu(CMDMenu.EXECUTE, "Nói chuyện", () -> {
                String talk = (String) NinjaUtils.randomObject("Haha, nhà ngươi cần vũ khí gì?",
                        "Ở chỗ ta có rất nhiều binh khí có giá trị", "Hãy chọn cho mình 1 món binh khí đi");
                getService().npcChat(NpcName.KANATA, talk);
            }));
        }
    }

    private void openDungeon(int optionId) {
        int[] pb = Dungeon.INFO[optionId - 1];
        Dungeon dungeon = (Dungeon) findWorld(World.DUNGEON);
        if (countPB == 0 && dungeon != null) {
            if (!dungeon.isClosed()) {
                this.mapBeforeEnterPB = mapId;
                setXY((short) pb[1], (short) pb[2]);
                outZone();
                joinZone(pb[0], -1, -1);
                getService().updatePointPB();
            } else {
                serverDialog("Hình trình khám phá hang động đã kết thúc.");
            }
        } else {
            if ((this.level / 10 == pb[4] / 10) || (optionId == 5 && this.level >= 70 && this.level <= 89)
                    || (optionId == 6 && this.level >= 90)) {
                if (this.group == null) {
                    this.mapBeforeEnterPB = mapId;
                    this.countPB = 0;
                    dungeon = new Dungeon(pb[3], 3600);
                    addWorld(dungeon);
                    Dungeon.addDungeon(dungeon);
                    setXY((short) pb[1], (short) pb[2]);
                    outZone();
                    joinZone(pb[0], -1, -1);
                    if (Event.isEvent()) {
                        int itemID = Event.getEvent().randomItemID();
                        if (itemID != -1) {
                            Item itemE = ItemFactory.getInstance().newItem(itemID);
                            itemE.setQuantity((int)NinjaUtils.nextInt(1, 5));
                            addItemToBag(itemE);
                        }
                    }
                } else {
                    if (group.isOpenPB) {
                        int indexParty = this.group.getIndexById(this.id);
                        Dungeon dun = (Dungeon) this.group.memberGroups.get(indexParty).find(World.DUNGEON);
                        if (dun == null) {
                            serverDialog("Không thể vào hang động.");
                            return;
                        }
                        if (dun.isClosed()) {
                            serverDialog("Hình trình khám phá hang động đã kết thúc.");
                            return;
                        }
                        this.mapBeforeEnterPB = mapId;
                        setXY((short) pb[1], (short) pb[2]);
                        addWorld(dun);
                        this.countPB = 0;
                        outZone();
                        joinZone(pb[0], -1, -1);
                        getService().updatePointPB();
                        if (Event.isEvent()) {
                            int itemID = Event.getEvent().randomItemID();
                            if (itemID != -1) {
                                Item itemE = ItemFactory.getInstance().newItem(itemID);
                                itemE.setQuantity((int)NinjaUtils.nextInt(1, 5));
                                addItemToBag(itemE);
                            }
                        }
                    } else {
                        if (this.group.getIndexById(this.id) != 0) {
                            serverDialog("Phải là trưởng nhóm mới có thể mở phó bản");
                        } else {
                            if (optionId == 4 && this.group.getNumberMember() > 1) {
                                serverDialog("Bạn chỉ có thể đi một mình.");
                                return;
                            }
                            this.mapBeforeEnterPB = mapId;
                            dungeon = new Dungeon(pb[3], 3600);
                            Dungeon.addDungeon(dungeon);
                            this.group.addWorld(dungeon);
                            this.group.isOpenPB = true;
                            this.countPB = 0;
                            addWorld(dungeon);
                            setXY((short) pb[1], (short) pb[2]);
                            outZone();
                            joinZone(pb[0], -1, -1);
                            if (this.group != null) {
                                this.group.getGroupService()
                                        .serverMessage(this.name + " đã mở cửa hang hang động sau trường cấp " + pb[4]
                                                + ", vào báo danh tại Kanata.");
                            }
                            if (Event.isEvent()) {
                                int itemID = Event.getEvent().randomItemID();
                                if (itemID != -1) {
                                    Item itemE = ItemFactory.getInstance().newItem(itemID);
                                    itemE.setQuantity((int)NinjaUtils.nextInt(1, 5));
                                    addItemToBag(itemE);
                                }
                            }
                        }
                    }
                }
            } else {
                serverDialog("Cấp độ không phù hợp");
            }
        }
    }

    public void npcGoosho() {
        menus.add(new Menu(CMDMenu.EXECUTE, "Cửa hàng", () -> {
            getService().openUI((byte) StoreManager.TYPE_MISCELLANEOUS);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Quầy sách", () -> {
            getService().openUI((byte) StoreManager.TYPE_BOOK);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Thời trang", () -> {
            getService().openUI((byte) StoreManager.TYPE_FASHION);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Gia tộc", () -> {
            getService().openUI((byte) StoreManager.TYPE_CLAN);
        }));
    }

    public void npcShinwa() {
        if (!Config.getInstance().isShinwa()) {
            serverMessage("Tính năng đang bảo trì.");
            return;
        }
        menus.add(new Menu(CMDMenu.EXECUTE, "Gian hàng", () -> {
            StallManager.getInstance().openUIMenu(this);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Bán vật phẩm", () -> {
            getService().openUI((byte) 36);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Nhận lại vật phâm", () -> {
            StallManager.getInstance().receiveItem(this);
        }));
    }

    public void requestViewDetails(Message ms) {
        try {
            if (!isHuman) {
                warningClone();
                return;
            }
            int id = ms.reader().readInt();
            Stall stall = StallManager.getInstance().findByID(viewAuctionTab);
            Item item = stall.find(id);
            if (item != null) {
                getService().requestViewDetails(id, item);
            }
        } catch (Exception e) {
            Log.error("err: " + e.getMessage(), e);
        }
    }

    public void npcRakkii() {
        menus.add(new Menu(CMDMenu.EXECUTE, "Lật hình", () -> {
            SelectCard.getInstance().open(this);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Mã quà tặng", () -> {
            InputDialog input = new InputDialog(CMDInputDialog.MA_QUA_TANG, "Mã quà tặng");
            setInput(input);
            getService().showInputDialog();
        }));
        menus.add(new Menu(CMDMenu.VXMM_1, "Vòng quay vip"));
        menus.add(new Menu(CMDMenu.VXMM_2, "Vòng quay thường"));
    }

    public void pickItem(Message ms) {
        lockItem.lock();
        try {
            if (isDead) {
                return;
            }
//            if (!isHuman) {
//                warningClone();
//                return;
//            }
            if (trade != null) {
                warningTrade();
                return;
            }
            short itemMapId = ms.reader().readShort();
            ItemMap itemMap = zone.findItemMapById(itemMapId);
            if (itemMap != null) {
                itemMap.lock.lock();
                try {
                    if (itemMap.isPickedUp()) {
                        return;
                    }
                    int distance = NinjaUtils.getDistance(this.x, this.y, itemMap.getX(), itemMap.getY());
                    if (distance < 150) {
                        Item item = itemMap.getItem();
                        if (item == null) {
                            return;
                        }
                        if (item.id == ItemName.CAY_NAM) {
                            if (taskMain == null || taskMain.taskId != TaskName.NV_HAI_NAM || taskMain.index != 1 || !isHuman) {
                                serverMessage("Không thể hái nấm");
                                return;
                            }
                        }
                        
                        if(item.id == ItemName.TRUNG_VI_THU){
                            Char _char = zone.findCharById(id);
                            int indexDAY_NENSHI = getIndexItemByIdInBag(ItemName.DAY_NENSHI);
                            if(indexDAY_NENSHI == -1 || _char.bag[indexDAY_NENSHI] == null || _char.bag[indexDAY_NENSHI].getQuantity() < 1){
                              serverMessage("Bạn cần có ít nhất 1 Dây NENSHI để nhặt trứng vỹ thú!");
                              return; 
                            }
                         removeItem(getIndexItemByIdInBag(ItemName.DAY_NENSHI), 1, true);
                        }
                        int ownerID = itemMap.getOwnerID();
                        if (ownerID == -1 || this.id == ownerID || itemMap.isCanPickup()) {
                            if (ownerID != -1 && item.template.type == 25 && this.id != ownerID) {
                                serverMessage("Vật phẩm của người khác");
                                return;
                            }
                            int index2 = -1;
                            int requireItemID = itemMap.getRequireItemID();
                            if (requireItemID != -1 && !NinjaUtils.isM1Tet()) {
                                index2 = getIndexItemByIdInBag(requireItemID);
                                if (index2 == -1) {
                                    String name = ItemManager.getInstance().getItemName(requireItemID);
                                    serverMessage("Bạn cần có " + name + " để nhặt");
                                    return;
                                }
                            }
                            boolean isTask = (item.template.type == ItemTemplate.TYPE_TASK
                                    || item.template.type == ItemTemplate.TYPE_TASK_WAIT
                                    || item.template.type == ItemTemplate.TYPE_TASK_SAVE);
                            if (item.id == ItemName.SUSHI) {
                                itemMap.setPickedUp(true);
                                this.hp += item.getQuantity();
                                if (this.hp > this.maxHP) {
                                    this.hp = this.maxHP;
                                }
                                zone.getService().loadHP(this);
                                getService().updateHp();
                            } else if (item.template.type != ItemTemplate.TYPE_MONEY) {
                                int num = getSlotNull();
                                if (item.template.isUpToUp) {
                                    int index = getIndexItemByIdInBag(item.id, item.isLock);
                                    if (index == -1 && num == 0) {
                                        warningBagFull();
                                        return;
                                    }
                                } else {
                                    if (num == 0) {
                                        warningBagFull();
                                        return;
                                    }
                                }
                                if (requireItemID != -1 && index2 != -1) {
                                    removeItem(index2, 1, true);
                                }
                                if (itemMap instanceof GiftBox) {
                                    Point p = getEventPoint().find(Noel.RECEIVED_GIFT);
                                    if (p.getPoint() > 0) {
                                        serverMessage("Mỗi người chỉ được nhận 1 hộp quà.");
                                        return;
                                    }
                                    p.addPoint(1);
                                }
                                if (itemMap instanceof Envelope) {
                                    Point p = getEventPoint().find(LunarNewYear.ENVELOPE);
                                    if (p.getPoint() >= 10) {
                                        serverMessage("Mỗi người chỉ được nhận tối đa 10 bao lì xì.");
                                        return;
                                    }
                                    p.addPoint(1);
                                }
                                itemMap.setPickedUp(true);

                                if (isTask) {
                                    item.isLock = true;
                                }
                                if (item.id == ItemName.CAY_NAM) {
                                    if (!this.isCatchItem) {
                                        isCatchItem = true;
                                        Char o = this;
                                        threadPool.submit(new Thread(new Runnable() {

                                            public void run() {
                                                try {
                                                    getService().showWait(zone.tilemap.name);
                                                    Thread.sleep(1000);
                                                    if (!isCleaned) {
                                                        if (!isFailure) {
                                                            Item itm = ItemFactory.getInstance()
                                                                    .newItem(ItemName.CAY_NAM);
                                                            itm.setQuantity(1);
                                                            itm.isLock = true;
                                                            addItemToBag(itm);
                                                            updateTaskPickItem(itm);
                                                            zone.getService().pickItem(o, itemMap);
                                                            zone.removeItem(itemMap);
                                                        } else {
                                                            itemMap.setPickedUp(false);
                                                        }
                                                        isFailure = false;
                                                        isCatchItem = false;
                                                        getService().endWait();
                                                    }
                                                } catch (Exception ex) {
                                                    serverDialog(ex.getMessage());
                                                }
                                            }
                                        }));
                                    }
                                } else if (item.id == ItemName.XAC_DOI_LUA) {
                                    int i = getIndexItemByIdInBag(ItemName.TINH_THE_BANG2);
                                    if (i != -1) {
                                        removeItem(i, 1, true);
                                        Item itm = ItemFactory.getInstance().newItem(ItemName.XAC_DOI_LUA2);
                                        itm.setQuantity(1);
                                        itm.isLock = true;
                                        addItemToBag(itm);
                                        if (taskId == TaskName.NV_THU_THAP_XAC_DOI_LUA) {
                                            updateTaskPickItem(itm);
                                        }
                                    } else {
                                        addHp(-1000);
                                        zone.getService().refreshHP(this);
                                        serverMessage("Bạn bị tiêu hao 1000HP vì bị dơi lửa đốt");
                                        if (this.hp <= 0) {
                                            startDie();
                                        }
                                    }
                                } else {
                                    addItemToBag(item);
                                    if (item.has(500)) {
                                        History history = new History(this.id, History.NHAT_VAT_PHAM);
                                        history.setBefore(this.coin, user.gold, this.yen);
                                        history.setAfter(this.coin, user.gold, this.yen);
                                        history.addItem(item);
                                        history.setCurrentMap(this.mapId, this.zone.id, itemMap.getId());
                                        history.setTime(System.currentTimeMillis());
                                        History.insert(history);
                                    }
                                }
                            } else {
                                itemMap.setPickedUp(true);
                                long sum = (long) this.yen + (long) item.getQuantity();
                                if (sum > 1500000000) {
                                    this.yen = 1500000000;
                                } else {
                                    this.yen += item.getQuantity();
                                }
                                if (this.yen < 0) {
                                    this.yen = 0;
                                }
                            }
                            if (item.id != ItemName.CAY_NAM) {
                                zone.getService().pickItem(this, itemMap);
                                zone.removeItem(itemMap);
                                updateTaskPickItem(item);
                                if (item.template.type == ItemTemplate.TYPE_TASK) {
                                    if (this.taskMain != null && this.group != null) {
                                        List<Char> chars = this.group.getCharsInZone(this.mapId, zone.id);
                                        for (Char _char : chars) {
                                            if (_char != null && _char != this && _char.isDead
                                                    && _char.getSlotNull() > 0) {
                                                if (_char.taskMain != null) {
                                                    if (_char.taskMain.taskId == this.taskMain.taskId
                                                            && _char.taskMain.index == this.taskMain.index) {
                                                        _char.addItemToBag(Converter.getInstance().newItem(item));
                                                        _char.updateTaskPickItem(item);

                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            serverMessage("Vật phẩm của người khác");
                        }
                    }
                } finally {
                    itemMap.lock.unlock();
                }
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        } finally {
            lockItem.unlock();
        }
    }

    public void throwItem(Message ms) {
        lockItem.lock();
        try {
            if (isDead) {
                return;
            }
//            if (!isHuman) {
//                warningClone();
//                return;
//            }
            if (trade != null) {
                warningTrade();
                return;
            }
            if ((Event.isNoel() && zone.getNumberItem() > 100) || zone.getNumberItem() > 30) {
                serverDialog("Vật phẩm ở đất đã quá nhiều.");
                return;
            }

            byte indexUI = ms.reader().readByte();
            if (indexUI < 0 || indexUI >= this.numberCellBag || this.bag[indexUI] == null || this.bag[indexUI].isLock) {
                return;
            }
            Item item = bag[indexUI];
            if (item != null) {
                if (item.template.id >= 292 && item.template.id <= 305) {
                    serverDialog("Không thể vưt vật phẩm này, chỉ có thể bán.");
                    return;
                }
                short x = (short) (this.x + (int)NinjaUtils.nextInt(-30, 30));
                short y = zone.tilemap.collisionY(x, this.y);
                this.bag[indexUI] = null;

                if (false) {
                    ItemMap itemMap = ItemMapFactory.getInstance()
                            .builder()
                            .id(zone.numberDropItem++)
                            .x(x)
                            .y(y)
                            .build();
                    itemMap.setItem(item);
                    itemMap.setOwnerID(-1);
                    itemMap.setExpired(0);

                    zone.addItemMap(itemMap);
                    zone.getService().throwItem(this, indexUI, itemMap);

                    Item itm = itemMap.getItem();
                    if (itm.isSaveHistory()) {
                        History history = new History(this.id, History.BO_VAT_PHAM);
                        history.setBefore(this.coin, user.gold, this.yen);
                        history.setAfter(this.coin, user.gold, this.yen);
                        history.addItem(item);
                        history.setCurrentMap(this.mapId, this.zone.id, itemMap.getId());
                        history.setTime(System.currentTimeMillis());
                        History.insert(history);
                    }
                    return;
                }

                removeItem(indexUI, item.getQuantity(), true);// bo ra dat mat luon vat pham

                // comment removeItem(indexUI, item.getQuantity(), false); neu muon nhin thay vat pham
                getService().saleItem(indexUI, item.getQuantity());

            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        } finally {
            lockItem.unlock();
        }

    }

    public void rewardCT() {
        if (war != null) {
            war.reward(this);
        }
    }

    public void sendClanItem(Message ms) {
        try {
            if (this.clan == null) {
                return;
            }
            if (!isHuman) {
                warningClone();
                return;
            }
            byte index = ms.reader().readByte();
            String name = ms.reader().readUTF();
            Item[] items = this.clan.getItems();
            if (items == null || index > items.length || items[index] == null) {
                return;
            }
            Member member = this.clan.getMemberByName(this.name);
            if (member != null) {
                int typeClan = member.getType();
                if (typeClan != Clan.TYPE_TOCTRUONG && typeClan != Clan.TYPE_TOCPHO) {
                    serverDialog("Bạn không phải tộc trưởng hoặc tộc phó.");
                    return;
                }
                Member mem = this.clan.getMemberByName(name);
                if (mem == null) {
                    serverDialog("Người này không trong gia tộc.");
                    return;
                }
                Char _char = mem.getChar();
                if (_char != null) {
                    int slot = _char.getSlotNull();
                    if (slot == 0) {
                        warningBagFull();
                        return;
                    }
                    Item item = Converter.getInstance().newItem(items[index]);
                    if (item.hasExpire()) {
                        item.expire += System.currentTimeMillis();
                    }
                    item.setQuantity(1);
                    if (item.template.isTypeBody()) {
                        int[][] max = new int[item.options.size()][2];
                        for (int i = 0; i < max.length; i++) {
                            ItemOption o = item.options.get(i);
                            max[i][0] = o.optionTemplate.id;
                            max[i][1] = o.param;
                        }
                        item.options.clear();
                        int[][] min = NinjaUtils.getOptionShop(max);
                        for (int a = 0; a < max.length; a++) {
                            int templateId = max[a][0];
                            int param = (int)NinjaUtils.nextInt(min[a][1], max[a][1]);
                            item.options.add(new ItemOption(templateId, param));
                        }
                    }
                    item.isLock = true;
                    _char.addItemToBag(item);
                    clan.removeItem(items[index], 1);
                    clan.getClanService().requestClanItem();
                    serverDialog("Phát vật phẩm cho " + name + " thành công.");
                } else {
                    serverDialog("Người này không online.");
                    return;
                }
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void outputCoinClan(Message ms) {
        try {
            if (clan == null) {
                return;
            }
            if (!isHuman) {
                warningClone();
                return;
            }
            int coin = ms.reader().readInt();
            serverMessage("Tính năng đang bảo trì.");
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void inputCoinClan(Message ms) {
        try {
            if (trade != null) {
                warningTrade();
                return;
            }
            if (clan == null) {
                return;
            }
            if (!isHuman) {
                warningClone();
                return;
            }
            int coin = ms.reader().readInt();
            if (coin < 1000 || coin > 10000000) {
                serverDialog("Vui lòng nhập trong khoảng từ 1.000xu đến 10.000.000xu.");
                return;
            }
            if (coin > this.coin) {
                serverDialog("Bạn không đủ xu.");
                return;
            }

            if (this.clan.main_name != this.name && !this.user.isAdmin() && this.user.kh == 0) {
                serverMessage("Bạn phải kích hoạt để dùng tính năng này");
                return;
            }

            clan.writeLog(this.name, Clan.MOVE_INPUT_MONEY, coin);
            clan.addCoin(coin);
            this.coin -= coin;
            getService().addCoinClan(this.getCoinInt());
            clan.getClanService().requestClanInfo();
            clan.getClanService().writeLog();
            clan.getClanService()
                    .serverMessage(this.name + " đóng góp vào ngân quỹ " + NinjaUtils.getCurrency(coin) + " xu");
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void moveOutClan(Message ms) {
        try {
            if (!isHuman) {
                warningClone();
                return;
            }
            String name = ms.reader().readUTF();
            if (name.equals(this.name)) {
                serverDialog("Bạn không thể tự trục xuất chính mình.");
                return;
            }
            if (this.clan != null) {
                int cType = this.clan.getMemberByName(this.name).getType();
                if (cType == Clan.TYPE_TOCTRUONG || cType == Clan.TYPE_TOCPHO) {
                    Member mem = this.clan.getMemberByName(name);
                    if (mem == null) {
                        serverMessage("Thành viên này không tồn tại.");
                        return;
                    }
                    int memType = mem.getType();
                    if (memType >= cType) {
                        serverMessage("Bạn không có quyền trục xuất người này.");
                        return;
                    }

                    int coin = 10000;
                    switch (memType) {

                        case Clan.TYPE_TOCPHO:
                            coin = 100000;
                            break;

                        case Clan.TYPE_TRUONGLAO:
                            coin = 50000;
                            break;

                        case Clan.TYPE_UUTU:
                            coin = 20000;
                            break;
                    }
                    if (this.clan.getCoin() < coin) {
                        serverMessage("Ngân quỹ không đủ.");
                        return;
                    }
                    clan.memberDAO.delete(mem);
                    clan.writeLog(name, Clan.MOVE_OUT_MEM, coin);
                    this.clan.addCoin(-coin);
                    clan.getClanService().requestClanMember();
                    Char _char = Char.findCharByName(name);
                    if (_char != null) {
                        _char.clan = null;
                        _char.zone.getService().moveOutClan(this);
                        _char.lastTimeOutClan = System.currentTimeMillis();
                    }
                    clan.getClanService().writeLog();
                    this.clan.getClanService().serverMessage(this.name + " đã trục xuất " + name + ", ngân quỹ trừ "
                            + NinjaUtils.getCurrency(coin) + " xu");
                } else {
                }
            } else {
                serverMessage("Bạn không có gia tộc");
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void outClan() {
        if (this.clan != null) {
            if (!isHuman) {
                warningClone();
                return;
            }
            Member mem = this.clan.getMemberByName(this.name);
            if (mem != null) {
                int cType = mem.getType();
                if (cType != Clan.TYPE_TOCTRUONG) {
                    try {
                        int coin = 10000;
                        switch (cType) {
                            case Clan.TYPE_TOCPHO:
                                coin = 100000;
                                break;

                            case Clan.TYPE_TRUONGLAO:
                                coin = 50000;
                                break;

                            case Clan.TYPE_UUTU:
                                coin = 20000;
                                break;
                        }
                        if (this.coin < coin) {
                            serverMessage("Bạn không đủ xu.");
                            return;
                        }
                        addCoin(-coin);
                        this.clan.memberDAO.delete(mem);
                        zone.getService().moveOutClan(this);
                        clan.getClanService().requestClanMember();
                        this.clan.getClanService().serverMessage(this.name + " đã rời gia tộc");
                        this.clan = null;
                        this.lastTimeOutClan = System.currentTimeMillis();
                        serverMessage("Bạn đã rời gia tộc thành công.");
                    } catch (Exception ex) {
                        Log.error("out clan err: " + ex.getMessage(), ex);
                    }
                } else {
                    serverMessage("Bạn là tộc trưởng nên không thể rời.");
                }
            }
        } else {
            serverMessage("Bạn không trong gia tộc.");
        }
    }

    public void clanUpLevel() {
        if (!isHuman) {
            warningClone();
            return;
        }
        if (this.clan != null) {
            int cType = this.clan.getMemberByName(this.name).getType();
            if (cType == Clan.TYPE_TOCTRUONG || cType == Clan.TYPE_TOCPHO) {
                int expNext = clan.getExpNext();
                int coin = clan.getCoin();
                int coinUp = clan.getCoinUp();
                if (clan.exp >= expNext) {
                    if (coin >= coinUp) {
                        clan.exp -= expNext;
                        clan.level++;
                        clan.writeLog(name, Clan.UP_LEVEL, -coinUp);
                        clan.addCoin(-coinUp);
                        clan.getClanService().requestClanInfo();
                        serverMessage("Gia tộc được lên cấp " + clan.level);
                    } else {
                        serverMessage("Ngân quỹ gia tộc không đủ");
                    }
                } else {
                    serverMessage("Không đủ kình nghiệm năng cấp gia tộc");
                }
            } else {
                serverMessage("Bạn không có quyền này.");
            }
        } else {
            serverMessage("Bạn không trong gia tộc.");
        }
    }

    public void changeClanType(Message ms) {
        try {
            if (!isHuman) {
                warningClone();
                return;
            }
            String name = ms.reader().readUTF();
            byte type = ms.reader().readByte();
            if (this.clan != null) {
                int cType = clan.getMemberByName(this.name).getType();
                if (cType == Clan.TYPE_TOCTRUONG) {
                    Member mem = this.clan.getMemberByName(name);
                    if (mem != null) {
                        if (type == Clan.TYPE_TOCPHO || type == Clan.TYPE_TRUONGLAO) {
                            if (type == Clan.TYPE_TOCPHO && this.clan.getNumberSameType(type) >= 1) {
                                serverMessage("Chức phó gia tộc đã đủ chỗ.");
                                return;
                            }
                            if (type == Clan.TYPE_TRUONGLAO && this.clan.getNumberSameType(type) >= 5) {
                                serverMessage("Chức trưởng lão gia tộc đã đủ chỗ.");
                                return;
                            }

                            mem.setType(type);
                            Connection conn = DbManager.getInstance().getConnection(DbManager.GAME);
                            PreparedStatement stmt = conn
                                    .prepareStatement("UPDATE `clan_member` SET `type` = ? WHERE `id` = ? LIMIT 1;");
                            try {
                                stmt.setInt(1, mem.getType());
                                stmt.setInt(2, mem.getId());
                                stmt.executeUpdate();
                            } finally {
                                stmt.close();
                            }
                            if (type == Clan.TYPE_TOCPHO) {
                                this.clan.setAssistName(name);
                                PreparedStatement stmt2 = conn.prepareStatement(
                                        "UPDATE `clan` SET `assist_name` = ? WHERE `id` = ? LIMIT 1;");
                                try {
                                    stmt2.setString(1, name);
                                    stmt2.setInt(2, clan.getId());
                                    stmt2.executeUpdate();
                                } finally {
                                    stmt2.close();
                                }
                            }
                            Char _char = Char.findCharByName(name);
                            if (_char != null) {
                                _char.zone.getService().acceptInviteClan(_char);
                            }
                            clan.getClanService().requestClanMember();
                            clan.getClanService().serverMessage(name + " được bổ nhiệm làm "
                                    + ((type == Clan.TYPE_TOCPHO) ? "tộc phó" : "trưởng lão"));

                        } else {
                            int preType = mem.getType();
                            Connection conn = DbManager.getInstance().getConnection(DbManager.GAME);
                            if (preType == Clan.TYPE_TOCPHO) {
                                this.clan.setAssistName("");
                                PreparedStatement stmt2 = conn.prepareStatement(
                                        "UPDATE `clan` SET `assist_name` = ? WHERE `id` = ? LIMIT 1;");
                                try {
                                    stmt2.setString(1, "");
                                    stmt2.setInt(2, clan.getId());
                                    stmt2.executeUpdate();
                                } finally {
                                    stmt2.close();
                                }
                            }
                            mem.setType(Clan.TYPE_NORMAl);
                            PreparedStatement stmt = conn
                                    .prepareStatement("UPDATE `clan_member` SET `type` = ? WHERE `id` = ? LIMIT 1;");
                            try {
                                stmt.setInt(1, mem.getType());
                                stmt.setInt(2, mem.getId());
                                stmt.executeUpdate();
                            } finally {
                                stmt.close();
                            }
                            Char _char = Char.findCharByName(name);
                            if (_char != null) {
                                _char.zone.getService().acceptInviteClan(this);
                            }
                            clan.getClanService().requestClanMember();
                            clan.getClanService().serverMessage(name + " đã bị bãi nhiệm");
                        }
                    } else {
                        serverMessage("Thành viên không tồn tại.");
                    }
                } else {
                    serverMessage("Bạn không phải tộc trưởng.");
                }
            } else {
                serverMessage("Bạn không có gia tộc.");
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void requestChangeMap() {
        if (this.isDead) {
            return;
        }
        zone.requestChangeMap(this);
    }

    public boolean isInvisible() {
        return isInvisible || isHide;
    }

    public void addParty(Message ms) {
        try {
            if (isDead) {
                return;
            }
            if (!isHuman) {
                warningClone();
                return;
            }
            if (zone.tilemap.isNotInvite()) {
                serverMessage("Không thể sử dụng tính năng này.");
                return;
            }
            String name = ms.reader().readUTF();
            if (this.name.equals(name)) {
                return;
            }
            Char _char = Char.findCharByName(name);
            if (_char != null) {
                if (this.group == null) {
                    createGroup();
                } else {
                    MemberGroup party = this.group.memberGroups.get(0);
                    if (party.charId != this.id) {
                        serverMessage("Bạn không phải trưởng nhóm");
                        return;
                    }
                }
                if (_char.group != null) {
                    serverMessage("Người này đã gia nhập nhóm khác");
                    return;
                }
                PlayerInvite p = _char.invite.findCharInvite(Invite.NHOM, this.id);
                if (p != null) {
                    serverDialog("Không thể mời vào nhóm liên tục. Vui lòng thử lại sau 30s nữa.");
                    return;
                }
                _char.invite.addCharInvite(Invite.NHOM, this.id, 30);
                _char.getService().partyInvite(this.id, this.name);
            } else {
                serverMessage("Người này không tồn tại hoặc không online.");
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void addPartyAccept(Message ms) {
        try {
            if (!isHuman) {
                warningClone();
                return;
            }
            if (this.group == null) {
                int charId = ms.reader().readInt();
                Char _char = ServerManager.findCharById(charId);
                if (_char != null) {
                    if (_char.group == null) {
                        serverMessage("Nhóm này không còn tồn tại");
                        return;
                    }
                    MemberGroup p = _char.group.memberGroups.get(0);
                    if (p.charId != _char.id) {
                        serverMessage("Người này không phải nhóm trưởng");
                        return;
                    }
                    PlayerInvite c = this.invite.findCharInvite(Invite.NHOM, _char.id);
                    if (c == null) {
                        serverDialog("Đã hết thời gian chấp nhận yêu cầu vào nhóm.");
                        return;
                    }
                    MemberGroup party = new MemberGroup();
                    party.charId = this.id;
                    party.classId = this.classId;
                    party.name = this.name;
                    party.setChar(this);
                    _char.group.add(party);
                    this.group = _char.group;
                } else {
                    serverMessage("Hiện tại người này không online.");
                }
            } else {
                serverMessage("Bạn đã gia nhập nhóm khác");
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public WarMember getMemberNormalWar() {
        War normalWar = MapManager.getInstance().normalWar;
        if (normalWar != null) {
            for (WarMember mem : normalWar.members) {
                if (mem.id == id) {
                    return mem;
                }
            }
        }
        return null;
    }

    public WarMember getMemberTalentWar() {
        War talentWar = MapManager.getInstance().talentWar;
        if (talentWar != null) {
            for (WarMember mem : talentWar.members) {
                if (mem.id == id) {
                    return mem;
                }
            }
        }
        return null;
    }

    public void addWarPoint(short point) {
        this.warPoint += point;
        if (this.gloryTask != null && this.gloryTask.type == GloryTask.NANG_CAP) {
            this.gloryTask.updateProgress(point);
        }

        if (taskId == TaskName.NV_CHIEN_TRUONG && taskMain != null && taskMain.index == 1) {
            updateTaskCount(point);
        }

        getService().warInfo();
        if (war != null && war.status == 1) {
            if (member != null) {
                member.point += point;
                if (this.faction == 0) {
                    war.whitePoint += point;
                }
                if (this.faction == 1) {
                    war.blackPoint += point;
                }
            }
        }
    }

    public void infection() {
        setInfected(true);
        setFashion();
    }

    public void cure() {
        setInfected(false);
        setFashion();
    }

    public void addPartyCancel(Message ms) {
        try {
            int charId = ms.reader().readInt();
            Char _char = ServerManager.findCharById(charId);

        } catch (IOException ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void outParty() {
        if (this.group != null) {
            synchronized (group) {
                if (this.group.memberGroups.size() > 1) {
                    int index = this.group.getIndexById(this.id);
                    if (index != -1) {
                        this.group.removeParty(index);
                    }
                }
                this.group = null;
                getService().outParty();
            }
        }

    }

    public void lockParty(Message ms) {
        try {
            boolean isLock = ms.reader().readBoolean();
            if (this.group != null && this.group.memberGroups.get(0).charId == this.id) {
                this.group.isLock = isLock;
                this.group.getGroupService().lockParty(isLock);
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void openFindParty() {
        if (!isHuman) {
            warningClone();
            return;
        }
        if (this.group == null) {
            try {
                HashMap<String, Group> groups = new HashMap<>();
                List<Char> chars = zone.getChars();
                for (Char _char : chars) {
                    if (_char != null && _char.group != null) {
                        groups.put(_char.group.memberGroups.get(0).name, _char.group);
                    }
                }
                getService().openFindParty(groups);
            } catch (Exception ex) {
                Log.error("err: " + ex.getMessage(), ex);
            }
        }
    }

    public void pleaseInputParty(Message ms) {
        try {
            if (!isHuman) {
                warningClone();
                return;
            }
            if (zone.tilemap.isNotInvite()) {
                serverMessage("Không thể sử dụng tính năng này.");
                return;
            }
            if (this.group != null) {
                return;
            }
            String name = ms.reader().readUTF();
            if (this.name.equals(name)) {
                return;
            }
            Char _char = Char.findCharByName(name);
            if (_char != null) {
                if (_char.group != null) {
                    if (_char.group.isLock) {
                        serverMessage("Nhóm này đã khóa, không thể xin gia nhập");
                        return;
                    }
                    if (_char.group.memberGroups.size() == 6) {
                        serverMessage("Nhóm đã đủ thành viên");
                        return;
                    }
                    if (_char.group.memberGroups.get(0).charId != _char.id) {
                        return;
                    }
                    PlayerInvite p = _char.invite.findCharInvite(Invite.XIN_VAO_NHOM, this.id);
                    if (p != null) {
                        serverMessage("Không thể xin vào nhóm liên tục. Vui lòng thử lại sau 30s nữa.");
                        return;
                    }
                    _char.invite.addCharInvite(Invite.XIN_VAO_NHOM, this.id, 30);
                    _char.getService().pleaseInputParty(this.name);
                } else {
                    serverMessage("Nhóm này không tồn tại");
                }
            } else {
                serverMessage("Người này không online hoặc không tồn tại");
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void moveMember(Message ms) {
        try {
            if (this.group != null) {
                if (this.group.memberGroups.get(0).charId == this.id) {
                    byte index = ms.reader().readByte();
                    if (index == 0 || index >= this.group.memberGroups.size()) {
                        serverMessage("Không thể trục xuất người này");
                        return;
                    }
                    Char _char = this.group.memberGroups.get(index).getChar();
                    _char.outParty();
                    // _char.sendMessage(new Message(83));
                    // this.group.removeParty(index);
                } else {
                    serverMessage("Bạn không phải trưởng nhóm");
                }
            }

        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void changeTeamLeader(Message ms) {
        try {
            if (!isHuman) {
                warningClone();
                return;
            }
            if (isDead) {
                return;
            }
            if (zone.tilemap.isNotInvite()) {
                serverMessage("Không thể sử dụng tính năng này.");
                return;
            }
            if (this.group != null) {
                if (this.group.memberGroups.get(0).charId == this.id) {
                    byte index = ms.reader().readByte();
                    if (index == 0 || index >= this.group.memberGroups.size()) {
                        serverMessage("Không thể nhường cho người này");
                        return;
                    }
                    this.group.changeLeader(index);
                } else {
                    serverMessage("Bạn không phải trưởng nhóm");
                }
            }

        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void acceptPleaseParty(Message ms) {
        if (this.group == null) {
            return;
        }
        if (this.group.memberGroups.size() < 6) {
            if (this.group.memberGroups.get(0).charId == this.id) {
                try {
                    String name = ms.reader().readUTF();
                    if (this.name.equals(name)) {
                        return;
                    }
                    Char _char = Char.findCharByName(name);
                    if (_char != null) {
                        if (!_char.isHuman) {
                            warningClone();
                            return;
                        }
                        if (_char.group == null) {
                            PlayerInvite c = this.invite.findCharInvite(Invite.XIN_VAO_NHOM, _char.id);
                            if (c == null) {
                                serverDialog("Đã hết thời gian chấp nhận yêu cầu tham gia nhóm.");
                                return;
                            }
                            _char.group = this.group;
                            MemberGroup p = new MemberGroup();
                            p.charId = _char.id;
                            p.classId = _char.classId;
                            p.name = _char.name;
                            p.setChar(_char);
                            _char.group.add(p);
                        } else {
                            serverDialog("Người này đã gia nhập nhóm khác");
                        }
                    } else {
                        serverDialog("Người này không online");
                    }
                } catch (IOException ex) {
                    Log.error("err: " + ex.getMessage(), ex);
                }
            }
        } else {
            serverDialog("Nhóm đã đủ thành viên");
        }
    }

    public void createGroup() {
        try {
            if (this.group != null) {
                serverDialog("Không thể tạo nhóm");
                return;
            }
            if (zone.tilemap.isNotInvite()) {
                serverMessage("Không thể sử dụng tính năng này trong lôi đài.");
                return;
            }
            int maxGroup = 6;
            if (this.mapId >= 134 && this.mapId <= 137) {
                maxGroup = 4;
            }
            int numberGroup = zone.getNumberGroup();
            if (numberGroup >= maxGroup) {
                serverMessage("Số nhóm trong khu vực đã đạt tối đa.");
                return;
            }
            Group group = new Group();
            MemberGroup party = new MemberGroup();
            party.charId = this.id;
            party.classId = this.classId;
            party.name = this.name;
            party.setChar(this);
            group.add(party);
            this.group = group;
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void changeTypePk(Message ms) {
        try {
            if (!isHuman) {
                warningClone();
                return;
            }
            if (zone.tilemap.isNotPk()) {
                serverMessage("Không thể sử dụng tính năng này tại đây.");
                return;
            }
            if (mapId >= 98 && mapId <= 104) {
                serverMessage("Không thể sử dụng tính năng này.");
                return;
            }

            byte type = ms.reader().readByte();
            if (type < 0 || type > 3) {
                return;
            }
            setTypePk(type);
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void setTypePk(byte typePk) {
        this.typePk = typePk;
        if (zone != null) {
            zone.getService().changePk(this);
        }
    }

    public void changeMap(int id) {
        try {
            int zoneId = NinjaUtils.randomZoneId(id);
            if (zoneId == -1) {
                serverDialog("Không tìm thấy nơi này!");
                return;
            }
            this.mapId = (short) id;
            if (isDead) {
                wakeUpFromDead();
            }
            boolean isSameGroup = false;
            if (this.group != null) {
                List<Char> chars = this.group.getCharsInMap(id);
                for (Char _char : chars) {
                    if (_char != null && _char != this) {
                        zoneId = _char.zone.id;
                        isSameGroup = true;
                        break;
                    }
                }
                if (id >= 134 && id <= 137) {
                    if (!isSameGroup) {
                        Map map = MapManager.getInstance().find(id);
                        Zone zone = map.getZoneById(zoneId);
                        int maxGruop = 4;
                        int numberGroup = zone.getNumberGroup();
                        if (numberGroup >= maxGruop) {
                            setXY(preX, preY);
                            getService().resetPoint();
                            serverDialog("Số nhóm trong khu vực này đã tối đa.");
                            return;
                        }
                    }
                    if (zone.getNumberChar() >= 24) {
                        setXY(preX, preY);
                        getService().resetPoint();
                        serverDialog("Khu vực đã đầy.");
                        return;
                    }
                }

            }
            outZone();
            joinZone(id, zoneId, -1);
            if (taskId == TaskName.NV_KHAM_PHA_XA_LANG && taskMain != null) {
                if ((mapId == 2 && taskMain.index == 1) || (mapId == 71 && taskMain.index == 2)
                        || (mapId == 26 && taskMain.index == 3)) {
                    taskNext();
                }
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void removeMemberFromWorld(Zone pre, Zone now) {
        synchronized (worlds) {
            worlds.forEach((t) -> {
                if (t.leaveWorld(pre, now)) {
                    t.removeMember(this);
                }
            });
        }
    }

    public void addMemberForWorld(Zone pre, Zone now) {
        synchronized (worlds) {
            worlds.forEach((t) -> {
                if (t.enterWorld(pre, now)) {
                    t.addMember(this);
                }
            });
        }
    }

    public void sendToSaleItem(Message ms) {
        try {
            if (this.user.kh == 0) {
                serverMessage("Vui lòng kích hoạt để sử dụng tính năng này");
                return;
            }
            if (!isHuman) {
                warningClone();
                return;
            }
            if (trade != null) {
                warningTrade();
                return;
            }
            if (!Config.getInstance().isShinwa()) {
                serverMessage("Tính năng đang bảo trì.");
                return;
            }
            byte index = ms.reader().readByte();
            int price = ms.reader().readInt();
            if (index < 0 || index >= this.numberCellBag) {
                return;
            }
            Item item = this.bag[index];
            if (item == null) {
                serverDialog("Không tìm thấy vật phẩm.");
                return;
            }
            if (price <= 0) {
                serverDialog("Giá xu phải lớn hơn 0 xu");
                return;
            }
            if (price > 100000000) {
                serverDialog("Giá xu tối đa là 100.000.000 xu");
                return;
            }
            int fee = Config.getInstance().getShinwaFee();
            int max = Config.getInstance().getShinwaMax();
            if (this.coin < fee) {
                serverDialog(String.format("Phí bán vật phẩm là %,d xu.", fee));
                return;
            }
            if (StallManager.getInstance().getTotalProductBySeller(name) > max) {
                serverDialog(String.format("Mỗi người chỉ có thể bán tối đa %,d vật phẩm.", max));
                return;
            }
            int type = item.template.type;
            max = Config.getInstance().getAuctionMax();
            Stall stall = StallManager.getInstance().findByType((byte) type);
            if (stall.getTotalProduct() > max) {
                serverDialog(String.format("Gian hàng chỉ có thể bán được %,d vật phẩm.", max));
                return;
            }
            StallManager.getInstance().addItem(this, item, price);
            this.coin -= fee;

            this.bag[index] = null;
            getService().sendItemToAuction(index);
        } catch (IOException e) {
            Log.error("err: " + e.getMessage(), e);
        }
    }

    public synchronized void buyItemAuction(Message ms) {
        try {
            if (this.user.kh == 0) {
                serverMessage("Vui lòng kích hoạt để sử dụng tính năng này");
                return;
            }
            if (!isHuman) {
                warningClone();
                return;
            }
            if (this.taskId < TaskName.NV_DIET_SEN_TRU_COC) {
                getService().endWait("Hãy hoàn nhiệm vụ diệt trừ cóc để sử dụng chức năng này.");
                return;
            }
            if (trade != null) {
                warningTrade();
                return;
            }
            if (!Config.getInstance().isShinwa()) {
                serverMessage("Tính năng đang bảo trì.");
                return;
            }
            int id = ms.reader().readInt();
            Stall stall = StallManager.getInstance().findByID(viewAuctionTab);
            if (stall == null) {
                return;
            }
            stall.buy(this, id);
        } catch (Exception e) {
            Log.error("err: " + e.getMessage(), e);
        }
    }

    public void boxCoinIn(Message ms) {
        try {
            if (trade != null) {
                warningTrade();
                return;
            }
            if (!isHuman) {
                warningClone();
                return;
            }
            int xu = ms.reader().readInt();
            long sum = (long) xu + (long) coinInBox;
            if (sum >= this.coinMax) {
                xu = (int) this.coinMax - (int) this.coinInBox;
                serverDialog("Số xu ở rương đã đầy. Bạn chỉ cất được " + xu + " xu");
            }
            if (xu < 0) {
                return;
            }
            if (xu > this.coin) {
                serverDialog("Số xu ở hành trang không đủ.");
                return;
            }
            this.coin -= xu;
            this.coinInBox += xu;
            getService().boxCoinIn(xu);
            updateWithBalanceMessage();
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void boxCoinOut(Message ms) {
        try {
            if (trade != null) {
                warningTrade();
                return;
            }
            if (!isHuman) {
                warningClone();
                return;
            }
            long xu = ms.reader().readInt();
            long sum = (long) xu + this.coin;
            if (sum > this.coinMax) {
                xu = this.coinMax - this.coin;
            }
            if (xu < 0) {
                return;
            }
            if (xu > this.coinInBox) {
                serverDialog("Số xu ở rương không đủ.");
                return;
            }
            this.coinInBox -= xu;
            this.coin += xu;
            getService().boxCoinOut((int) xu);
            updateWithBalanceMessage();
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void requestCharInfo(Message ms) {
        try {
            int num = ms.reader().readByte();
            ArrayList<Char> chars = new ArrayList<>();
            for (int i = 0; i < num; i++) {
                int charId = ms.reader().readInt();
                Char _char2 = zone.findCharById(charId);
                if (_char2 != null) {
                    chars.add(_char2);
                }
            }
            getService().requestPlayers(chars);
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void requestItemChar(Message ms) {
        try {
            int charId = ms.reader().readInt();
            byte indexUI = ms.reader().readByte();
            Char _char = ServerManager.findCharById(charId);
            if (_char != null) {
                if (indexUI >= 0) {
                    if (indexUI < 16) {
                        Equip equip = _char.equipment[indexUI];
                        if (equip != null) {
                            getService().requestItemChar(equip, indexUI);
                        }
                    } else {
                        int index = indexUI - 16;
                        if (index >= 0 && index < 16) {
                            Equip equip = _char.fashion[index];
                            if (equip != null) {
                                getService().requestItemChar(equip, indexUI);
                            }

                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void changeClanAlert(Message ms) {
        if (!isHuman) {
            warningClone();
            return;
        }
        Clan clan = this.clan;
        if (clan != null && clan.getMemberByName(this.name).getType() == Clan.TYPE_TOCTRUONG) {
            try {
                String alert = ms.reader().readUTF();
                clan.setAlert(alert);
                getService().changeClanAlert(alert);
                serverDialog("Thay đổi thông báo thành công.");
            } catch (Exception ex) {
                Log.error("err: " + ex.getMessage(), ex);
            }
        } else {
            serverDialog("Có lỗi xảy ra.");
        }
    }

    public void requestItem(Message ms) {
        try {
            byte typeUI = ms.reader().readByte();
            Store storeData = StoreManager.getInstance().find(typeUI);
            if (storeData != null) {
                storeData.show(this);
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void requestEnemies() {

    }

    public void requestItemInfo(Message ms) {
        try {
            byte typeUI = ms.reader().readByte();
            byte indexUI = ms.reader().readByte();
            if (indexUI < 0) {
                return;
            }
            if (typeUI == 2 || (typeUI >= 14 && typeUI <= 29) || typeUI == 32 || typeUI == 34 || typeUI == 8 || typeUI == 9 || typeUI == 7 || typeUI == 8) {
                Store store = StoreManager.getInstance().find(typeUI);
                if (store != null) {
                    ItemStore item = store.get(indexUI);
                    if (item != null) {
                        getService().itemStoreInfo(item, typeUI, indexUI);
                    }
                }
            } else if (typeUI == 3) {
                if (indexUI >= bag.length) {
                    return;
                }
                if (bag[indexUI] != null) {
                    getService().itemInfo(bag[indexUI], typeUI, indexUI);
                }
            } else if (typeUI == 4) {
                switch (getCommandBox()) {
                    case RUONG_DO:
                        if (indexUI >= box.length) {
                            return;
                        }
                        if (box[indexUI] != null) {
                            getService().itemInfo(box[indexUI], typeUI, indexUI);
                        }
                        break;
                    case BO_SUU_TAP:
                        if (indexUI >= collectionBox.size()) {
                            return;
                        }
                        Item itm = collectionBox.get(indexUI);
                        if (itm != null) {
                            getService().itemInfo(itm, typeUI, indexUI);
                        }
                        break;

                    case CAI_TRANG:
                        if (indexUI >= maskBox.size()) {
                            return;
                        }
                        Item itm2 = maskBox.get(indexUI);
                        if (itm2 != null) {
                            getService().itemInfo(itm2, typeUI, indexUI);
                        }
                        break;
                    case DOI_LONG_DEN_XU:
                    case DOI_LONG_DEN_LUONG:
                        List<Item> list = getListItemByID(ItemName.LONG_DEN_TRON, ItemName.LONG_DEN_CA_CHEP, ItemName.LONG_DEN_MAT_TRANG, ItemName.LONG_DEN_NGOI_SAO);
                        if (indexUI >= list.size()) {
                            return;
                        }
                        Item itm3 = list.get(indexUI);
                        if (itm3 != null) {
                            getService().itemInfo(itm3, typeUI, indexUI);
                        }
                        break;
                    case THANG_NGUONG_KHAI_HOA:
                    case THANG_CAP_KHAI_HOA:
                        if (indexUI >= this.equipment.length) {
                            return;
                        }
                        Item itm4 = this.equipment[indexUI];
                        if (itm4 != null) {
                            getService().itemInfo(itm4, typeUI, indexUI);
                        }
                        break;
                }
            } else if (typeUI == 5) {
                if (indexUI < 16) {
                    if (equipment[indexUI] != null) {
                        getService().equipmentInfo(equipment[indexUI], typeUI, indexUI);
                    }
                } else {
                    int index = indexUI - 16;
                    if (index >= fashion.length) {
                        return;
                    }
                    if (fashion[index] != null) {
                        getService().equipmentInfo(fashion[index], typeUI, indexUI);
                    }
                }
            } else if (typeUI == 30) {
                if (trade != null) {
                    trade.viewItemInfo(this, typeUI, indexUI);
                }
            }
            // else if (typeUI == 39) {
            // if (this.clan != null) {
            // Item[] items = this.clan.getItems();
            // if (items != null) {
            // getService().itemInfo(items[indexUI], typeUI, indexUI);
            // }
            // }
            // }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public int getSlotNull() {
        int number = 0;
        for (int i = 0; i < this.numberCellBag; i++) {
            if (bag[i] == null) {
                number++;
            }
        }
        return number;
    }

    public int getCountMount() {
        int number = 0;
        for (int i = 0; i < this.mount.length; i++) {
            if (this.mount[i] != null) {
                number++;
            }
        }
        return number;
    }

    public void saleItem(Message mss) {
        try {
            if (!isHuman) {
                warningClone();
                return;
            }
            if (trade != null) {
                warningTrade();
                return;
            }
            byte indexUI = mss.reader().readByte();
            mss.reader().mark(10000);
            int quantity = 1;
            if (mss.reader().available() > 0) {
                try {
                    quantity = mss.reader().readInt();
                } catch (Exception e) {
                    mss.reader().reset();
                    quantity = mss.reader().readShort();
                }
            }
            if (bag[indexUI] != null) {
                if (bag[indexUI].upgrade > 0 && !bag[indexUI].template.isTypeMount()
                        && !bag[indexUI].template.isTypeBiKip() && !bag[indexUI].template.isTypeMatThan()) {
                    serverDialog("Vật phẩm này đã nâng cấp không thể bán.");
                    return;
                }
                if (quantity < 1 || !bag[indexUI].has(quantity)) {
                    serverDialog("Số lượng bán không hợp lệ.");
                    return;
                }
                if (bag[indexUI].template.type == 24 || bag[indexUI].template.type == 25) {
                    serverDialog("Không thể bán vật phẩm nhiệm vụ.");
                    return;
                }
                if (bag[indexUI].template.type == 22 || bag[indexUI].template.isTypeYoroi()) {
                    serverDialog("Vật phẩm quý giá không thể bán.");
                    return;
                }

                int[] blackListItems = {568, 569, 570, 571, 397, 398, 399, 400, 401, 402};
                Item itm = bag[indexUI];
                if (itm.template.id > 11 && !IntStream.of(blackListItems).anyMatch(x -> x == itm.template.id)) {
                    History history = new History(this.id, History.BAN_VAT_PHAM);
                    history.setPrice(0, bag[indexUI].yen, 0);
                    history.setBefore(this.coin, user.gold, this.yen);
                    addYen(quantity * bag[indexUI].yen);
                    history.setAfter(this.coin, user.gold, this.yen);
                    history.addItem(bag[indexUI]);
                    history.setTime(System.currentTimeMillis());
                    History.insert(history);
                } else {
                    addYen(quantity * bag[indexUI].yen);
                }
                removeItem(indexUI, quantity, false);
                getService().saleItem(indexUI, quantity);
            }
        } catch (IOException ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void buyItem(Message mss) {
        try {
            if (!isVillage() && !isSchool() && this.mapId != 98 && this.mapId != 104) {
                return;
            }
            if (!isHuman) {
                warningClone();
                return;
            }
            if (trade != null) {
                warningTrade();
                return;
            }
            if (this.taskId < TaskName.NV_DIET_SEN_TRU_COC) {
                getService().endWait("Hãy hoàn nhiệm vụ diệt trừ cóc để sử dụng chức năng này.");
                return;
            }
            byte typeUI = mss.reader().readByte();
            byte indexUI = mss.reader().readByte();
            int quantity = 1;
            if (mss.reader().available() > 0) {
                quantity = mss.reader().readUnsignedShort();
            }
            quantity = quantity > Config.getInstance().getMaxQuantity() ? Config.getInstance().getMaxQuantity()
                    : quantity;
            if (quantity < 1 && quantity > 30000) {
                serverDialog("Số lượng không hợp lệ!");
                return;
            }
            Store store = StoreManager.getInstance().find(typeUI);
            if (store != null) {
                store.buy(this, indexUI, quantity);
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void viewInfo(Message mss) {
        try {
            String text = mss.reader().readUTF();
            Char _char = null;
            if (text.equals(this.name)) {
                if (clone != null && !clone.isNhanBan) {
                    _char = clone;
                } else {
                    _char = this;
                }
            } else {
                _char = findCharByName(text);
            }

            if (_char != null) {
                getService().viewInfo(_char);
            } else {
                serverMessage("Người này hiện tại không online.");
            }
        } catch (Exception ex) {
            Log.error("view info err: " + ex.getMessage(), ex);
        }
    }

    public void rewardDungeon() {
        Dungeon dungeon = (Dungeon) findWorld(World.DUNGEON);
        if (dungeon != null && !receivedRewardPB) {
            int team = dungeon.listCharId.size();
            int reward = pointPB / Dungeon.POINT[dungeon.level];
            if (dungeon.timeFinish > 0) {
                reward += team;
            }
            Item item = ItemFactory.getInstance().newItem(Dungeon.REWARD[dungeon.level]);
            item.setQuantity(reward);
            item.isLock = true;
            addItemToBag(item);
            this.receivedRewardPB = true;
            if (reward > rewardPB) {
                rewardPB = reward;
            }
            if (Event.isEvent()) {
                int itemID = Event.getEvent().randomItemID();
                if (itemID != -1) {
                    Item itemE = ItemFactory.getInstance().newItem(itemID);
                    itemE.setQuantity((int)NinjaUtils.nextInt(1, 5));
                    addItemToBag(itemE);
                }
            }
        }
    }

    public byte getSideClass() {
        if (this.classId == 6 || this.classId == 4 || this.classId == 2) {
            return 1;
        } else {
            return 0;
        }
    }

    public byte getSys() {
        if (this.classId == 1 || this.classId == 2) {
            return 1;
        }
        if (this.classId == 3 || this.classId == 4) {
            return 2;
        }
        if (this.classId == 5 || this.classId == 6) {
            return 3;
        }
        return 0;
    }

    private boolean isVillage() {
        short[] map = new short[]{10, 17, 22, 32, 38, 43, 48, 138, 162};
        for (short m : map) {
            if (this.mapId == m) {
                return true;
            }
        }
        return false;
    }

    private boolean isSchool() {
        short[] map = new short[]{1, 27, 72};
        for (short m : map) {
            if (this.mapId == m) {
                return true;
            }
        }
        return false;
    }

    public void updatePointPB(int point) {
        this.pointPB += point;
        getService().updatePointPB();
    }

    public synchronized void addGold(int gold) {
        if (gold == 0) {
            return;
        }
        long sum = (long) user.gold + (long) gold;
        int pre = user.gold;
        if (sum > 1500000000) {
            user.gold = 1500000000;
        } else {
            user.gold += gold;
        }
        if (user.gold < 0) {
            user.gold = 0;
        }
        gold = (user.gold - pre);
        if (gold > 0) {
            getService().addGold(gold);
        } else {
            getService().loadGold();
        }
    }

    public synchronized void addCoin(long xu) {
        if (xu == 0) {
            return;
        }
        if (trade != null) {
            trade.closeUITrade();
        }
        long sum = (long) this.coin + (long) xu;
        long pre = this.coin;

        if (sum >= this.coinMax) {
            this.coin = this.coinMax;
            System.out.println(this.coinMax);
        } else {
            this.coin += xu;
        }
        if (this.coin < 0) {
            this.coin = 0;
        }

        getService().addXu((int) (this.coin - pre));
        updateWithBalanceMessage();
    }

    public synchronized void addYen(long yen) {
        if (yen == 0) {
            return;
        }
        long sum = this.yen + yen;
        long pre = this.yen;
        if (sum >= 2000000000) {
            this.yen = 2000000000;
        } else {
            this.yen += yen;
        }
        if (this.yen < 0) {
            this.yen = 0;
        }
        getService().addYen((int) (this.yen - pre));
        // update();
    }

    public synchronized void addExp(long exp) {
        if (notReceivedExp || haveOptions[ItemOptionName.KHONG_NHAN_EXP_TYPE_0] || zone.tilemap.isupyen()) {
            return;
        }
        if (this.isNhanBan) {
            CloneChar clone = (CloneChar) this;
            clone.human.addExp(exp);
            return;
        }
        if (this.expDown > 0) {
            if (this.expDown - exp >= 0) {
                this.expDown -= exp;
            } else {
                exp -= this.expDown;
                this.expDown = 0;
                addExp(exp);
            }
            getService().addExpDown(exp);
        } else {
            this.exp += exp;
            if (this.exp > Server.EXP_MAX) {
                this.exp = Server.EXP_MAX;
                return;
            }
            getService().addExp(exp);
            int preLevel = this.level;
            this.level = NinjaUtils.getLevel(this.exp);
            int nextLevel = this.level;
            int num = nextLevel - preLevel;
            if (num > 0) {
                this.levelUpTime = (new Date()).getTime();
                if (this.classId == 0) {
                    this.potential[0] += 2 * num;
                    this.potential[1] += num;
                    this.potential[2] += num;
                    this.potential[3] += num;
                } else {
                    for (int i = preLevel; i < nextLevel; i++) {
                        if (i >= 10 && i <= 69) {
                            this.potentialPoint += 10;
                        } else if (i <= 79) {
                            this.potentialPoint += 20;
                        } else if (i <= 89) {
                            this.potentialPoint += 30;
                        } else if (i <= 99) {
                            this.potentialPoint += 40;
                        } else if (i <= 130) {
                            this.potentialPoint += 50;
                        } else if (i <= 145) {
                            this.potentialPoint += 60;
                        } else {
                            this.potentialPoint += 70;
                        }
                    }
                    this.skillPoint += num;
                }
                setAbility();
                updateTaskLevelUp();
                getService().levelUp();
                zone.getService().loadLevel(this);
            }
            // getService().addExp(exp);
        }
    }

    public void updateTaskCount(int count) {
        if (taskMain != null) {
            try {
                taskMain.count += count;
                short[] counts = taskMain.template.getCounts();
                getService().updateTaskCount(taskMain.count);
                if (taskMain.count >= counts[taskMain.index]) {
                    taskNext();
                }
            } catch (Exception ex) {
                Log.error("err: " + ex.getMessage(), ex);
            }
        }
    }

    public void taskNext() {
        if (taskMain != null) {
            taskMain.index++;
            TaskTemplate template = taskMain.template;
            taskMain.count = 0;
            short[] counts = taskMain.template.getCounts();
            if (taskMain.index > counts.length - 1) {
                taskMain.index = counts.length - 1;
            }
            getService().taskNext();
        }
    }

    public void updateTask() {
        if (taskMain != null) {
            taskMain = null;
            taskId++;
            getService().taskFinish();
        }
        initListCanEnterMap();
    }

    public void removeFriend(Message ms) {
        try {
            if (!isHuman) {
                warningClone();
                return;
            }
            String name = ms.reader().readUTF();
            if (this.isViewListFriend) {
                if (this.friends.get(name) != null) {
                    this.friends.remove(name);
                    getService().removeFriend(name);
                } else {
                    serverMessage("Người này không tồn tại.");
                }
            }
            if (!this.isViewListFriend) {
                if (this.enemies.get(name) != null) {
                    this.enemies.remove(name);
                    getService().removeEnemy(name);
                } else {
                    serverMessage("Người này không tồn tại.");
                }
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }

    }

    public void removeEnemy(Message ms) {
        try {
            if (!isHuman) {
                warningClone();
                return;
            }
            String name = ms.reader().readUTF();
            if (this.enemies.get(name) != null) {
                this.enemies.remove(name);
                getService().removeEnemy(name);
            } else {
                serverMessage("Người này không tồn tại.");
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }

    }

    public void addFriend(Message ms) {
        try {
            if (!isHuman) {
                warningClone();
                return;
            }
            String name = ms.reader().readUTF();
            Char _char = findCharByName(name);

            if (_char == null) {
                serverMessage("Người này không online hoặc không tồn tại!");
                return;
            }
            if (_char == this) {
                return;
            }
            if (!_char.isHuman) {
                return;
            }
            Friend friend = friends.get(name);
            if (friend != null) {
                serverMessage(name + " đã có trong danh sách bạn bè.");
                return;
            }
            Friend me = _char.friends.get(this.name);
            if (me != null) {
                me.type = 1;
                friends.put(_char.name, new Friend(_char.name, (byte) 1));
                getService().addFriend(name, 1);
                _char.getService().addFriend(this.name, 1);
                return;
            } else {
                friends.put(_char.name, new Friend(_char.name, (byte) 0));
            }
            if (taskId == TaskName.NV_BAN_HUU_TAM_GIAO && taskMain != null && taskMain.index == 1) {
                updateTaskCount(1);
            }
            getService().addFriend(name, 0);
            _char.getService().inviteFriend(this.name);
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void addPointPk(int p) {
        if (isBot()) {
            return;
        }
        this.hieuChien += p;
        if (this.hieuChien > 15) {
            this.hieuChien = 15;
        } else if (this.hieuChien < 0) {
            this.hieuChien = 0;
        }
    }

    public byte getTaskMapId() {
        if (taskId >= Server.npcTasks.length) {
            return -3;
        }
        byte b;
        if (taskMain == null) {
            b = Server.mapTasks[taskId][0];
        } else {
            b = Server.mapTasks[taskId][taskMain.index + 1];
        }
        if ((int) b == -1) {
            if (this.classId == 0 && taskId == 9) {
                b = -2;
            } else if (this.classId == 0 || this.classId == 1 || this.classId == 2) {
                b = 1;
            } else if (this.classId == 3 || this.classId == 4) {
                b = 72;
            } else if (this.classId == 5 || this.classId == 6) {
                b = 27;
            }
        }
        return b;
    }

    public byte getTaskNpcId() {
        byte result;
        try {
            if (taskId >= Server.npcTasks.length) {
                result = -3;
            } else {
                byte b;
                if (taskMain == null) {
                    b = Server.npcTasks[taskId][0];
                } else {
                    b = Server.npcTasks[taskId][taskMain.index + 1];
                }
                if ((int) b == -1) {
                    if (classId == 0 && taskId == TaskName.NV_GIA_TANG_SUC_MANH) {
                        b = -2;
                    } else if (classId == 0 || classId == 1 || classId == 2) {
                        b = 9;
                    } else if (classId == 3 || classId == 4) {
                        b = 10;
                    } else if (classId == 5 || classId == 6) {
                        b = 11;
                    }
                }
                result = b;
            }
        } catch (Exception ex) {
            result = -1;
        }
        return result;
    }

    public void clearTask() {
        try {
            if (this.level < 10) {
                getService().npcChat(NpcName.TAJIMA,
                        "Trình độ của con không đạt cấp 10 nên không được sử dụng chức năng này");
                return;
            }
            this.taskMain = null;
            for (int i = 0; i < this.bag.length; i++) {
                if (this.bag[i] != null && ((int) this.bag[i].template.type == 25
                        || (int) this.bag[i].template.type == 23 || (int) this.bag[i].template.type == 24)) {
                    this.bag[i] = null;
                }
            }
            getService().clearTask();
            getService().npcChat(NpcName.TAJIMA,
                    "Tất cả vật phẩm nhiệm vụ và nhiệm vụ hiện tại trên người con đã được hủy.");
        } catch (Exception e) {
        }
    }

    public int getCoinInt() {
        if (this.coin >= 2000000000) {
            return 2000000000;
        }
        return (int) this.coin;
    }

    public int getCoinInBoxInt() {
        if (this.coinInBox >= 2000000000) {
            return 2000000000;
        }
        return (int) this.coinInBox;
    }

    public int getYenInt() {
        if (this.yen >= 2000000000) {
            return 2000000000;
        }
        return (int) this.yen;
    }

    public long getCoin() {
        return this.coin;
    }

    public long getCoinInBox() {
        return this.coinInBox;
    }

    public long getYen() {
        return this.yen;
    }

    public void saveRms(Message mss) {
        try {
            String key = mss.reader().readUTF();
            int len = mss.reader().readInt();
            byte[] ab = new byte[len];
            mss.reader().read(ab);
            byte type = mss.reader().readByte();
            switch (key) {
                case "KSkill":
                    this.onKSkill = new byte[]{-1, -1, -1};
                    for (int i = 0; i < ab.length; i++) {
                        if (ab[i] != -1) {
                            for (Skill skill : this.vSkill) {
                                if (skill.template.id == ab[i]) {
                                    this.onKSkill[i] = ab[i];
                                    break;
                                }
                            }
                        }
                    }
                    break;

                case "OSkill":
                    this.onOSkill = new byte[]{-1, -1, -1, -1, -1};
                    for (int i = 0; i < ab.length; i++) {
                        if (ab[i] != -1) {
                            for (Skill skill : this.vSkill) {
                                if (skill.template.id == ab[i]) {
                                    this.onOSkill[i] = ab[i];
                                    break;
                                }
                            }
                        }
                    }
                    break;

                case "CSkill":
                    if (ab != null) {
                        this.onCSkill = ab;
                    }
                    break;
            }
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public void loadSkillShortcut(Message mss) {
        try {
            byte type = 0;
            if (!isHuman) {
                type = 1;
            }
            String key = mss.reader().readUTF();
            byte[] data = {};
            switch (key) {
                case "KSkill":
                    data = this.onKSkill;
                    break;

                case "OSkill":
                    data = this.onOSkill;
                    break;

                case "CSkill":
                    data = this.onCSkill;
                    break;
            }
            getService().sendSkillShortcut(key, data, type);
        } catch (Exception ex) {
            Log.error("err: " + ex.getMessage(), ex);
        }
    }

    public JSONArray getDefaultMap() {
        JSONArray jArr = new JSONArray();
        int mapId = this.saveCoordinate;
        if (zone != null) {
            if (zone.tilemap.isNotSave()) {
                mapId = this.mapBeforeEnterPB;

            }
            if (zone.tilemap.isChienTruong()) {
                if (war != null) {
                    war.removeMember(this);
                }
            }
        }
        short[] xy = NinjaUtils.getXY(mapId);
        jArr.add(mapId);
        jArr.add(xy[0]);
        jArr.add(xy[1]);
        return jArr;
    }

    public synchronized void saveData() {
        if (isLoadFinish() && !saving) {
            try {
                saving = true;
                Log.debug("save data " + this.name);
                this.lastLogoutTime = System.currentTimeMillis();
                JSONArray bags = new JSONArray();
                for (int i = 0; i < this.numberCellBag; i++) {
                    try {
                        if (this.bag[i] != null) {
                            bags.add(this.bag[i].toJSONObject());

                        }
                    } catch (Exception e) {
                    }
                }
                JSONArray boxs = new JSONArray();
                for (int i = 0; i < this.numberCellBox; i++) {
                    try {
                        if (this.box[i] != null) {
                            boxs.add(this.box[i].toJSONObject());
                        }
                    } catch (Exception e) {
                    }
                }
                JSONArray equipment = new JSONArray();
                for (int i = 0; i < 16; i++) {
                    try {
                        if (this.equipment[i] != null) {
                            equipment.add(this.equipment[i].toJSONObject());
                        }
                    } catch (Exception e) {
                    }
                }
                JSONArray fashion = new JSONArray();
                for (int i = 0; i < 16; i++) {
                    try {
                        if (this.fashion[i] != null) {
                            fashion.add(this.fashion[i].toJSONObject());
                        }
                    } catch (Exception e) {
                    }
                }
                JSONArray mounts = new JSONArray();
                for (int i = 0; i < 5; i++) {
                    try {
                        if (this.mount[i] != null) {
                            mounts.add(this.mount[i].toJSONObject());
                        }
                    } catch (Exception e) {
                    }
                }
                JSONArray maskBox = new JSONArray();
                for (Item item : this.maskBox) {
                    try {
                        if (item != null) {
                            maskBox.add(item.toJSONObject());
                        }
                    } catch (Exception e) {
                    }
                }
                JSONArray collectionBox = new JSONArray();
                for (Item item : this.collectionBox) {
                    try {
                        if (item != null) {
                            collectionBox.add(item.toJSONObject());
                        }
                    } catch (Exception e) {
                    }
                }
                JSONArray bijuus = new JSONArray();
                for (int i = 0; i < 5; i++) {
                    try {
                        if (this.bijuu[i] != null) {
                            bijuus.add(this.bijuu[i].toJSONObject());
                        }
                    } catch (Exception e) {
                    }
                }
                JSONArray skill = new JSONArray();
                if (this.vSkill != null) {
                    try {
                        for (Skill s : this.vSkill) {
                            skill.add(s.toJSONObject());
                        }
                    } catch (Exception e) {
                    }
                }
                JSONArray rewards = new JSONArray();
                if (this.reward != null) {
                    for (int i = 0; i < 5; i++) {
                        rewards.add(this.reward[i]);
                    }
                }
                JSONObject data = new JSONObject();
                data.put("exp", this.exp);
                data.put("expDown", this.expDown);
                data.put("countPB", this.countPB);
                if (this.countPB == 0) {
                    Dungeon dungeon = (Dungeon) findWorld(World.DUNGEON);
                    data.put("dungeonId", dungeon.getId());
                    data.put("receivedRewardPB", this.receivedRewardPB);
                }
                data.put("pointPB", this.pointPB);
                data.put("hieuChien", this.hieuChien);
                data.put("countFinishDay", this.countFinishDay);
                data.put("countLoopBoss", this.countLoopBoss);
                data.put("limitKyNangSo", this.limitKyNangSo);
                data.put("limitTiemNangSo", this.limitTiemNangSo);
                data.put("limitBangHoa", this.limitBangHoa);
                data.put("limitPhongLoi", this.limitPhongLoi);
                data.put("lastTimeOutClan", this.lastTimeOutClan);
                data.put("maskId", this.getMaskId());
                data.put("coinMax", this.coinMax);
                data.put("tayTiemNang", this.tayTiemNang);
                data.put("tayKyNang", this.tayKyNang);
                data.put("numberUseExpanedBag", this.numberUseExpanedBag);
                data.put("numberUseExpanedBox", this.numberUseExpanedBox);
                data.put("levelUpTime", this.levelUpTime);
                data.put("not_received_exp", this.notReceivedExp);
                data.put("duatop", this.duatop);
                data.put("dhtp", this.dhtp);
                data.put("dhdg", this.dhdg);
                data.put("count_arena", this.countArenaT);
                data.put("sevenbeasts_id", this.sevenBeastsID);
                data.put("type_sevenbeasts", this.typeSevenBeasts);

                if (gloryTask != null) {
                    JSONObject glory = new JSONObject();
                    glory.put("type", gloryTask.type);
                    glory.put("quantity", gloryTask.quantity);
                    glory.put("progress", gloryTask.progress);
                    glory.put("requireUseEquip", gloryTask.requireUseEquip);
                    data.put("gloryTask", glory);
                }

                JSONArray taskOrders = new JSONArray();
                for (TaskOrder task : this.taskOrders) {
                    JSONObject obj = new JSONObject();
                    obj.put("taskId", task.taskId);
                    obj.put("count", task.count);
                    obj.put("maxCount", task.maxCount);
                    obj.put("killId", task.killId);
                    obj.put("mapId", task.mapId);
                    taskOrders.add(obj);
                }
                if (this.huyHieu > -1) {
                    data.put("rank", this.huyHieu);
                }

                JSONObject warObj = new JSONObject();
                warObj.put("point", warPoint);
                warObj.put("time", time);
                warObj.put("faction", faction);
                warObj.put("kill", nKill);
                warObj.put("dead", nDead);
                warObj.put("rewarded", isRewarded);
                data.put("war", warObj);
                JSONObject clanwarObj = new JSONObject();
                clanwarObj.put("point", warPoint);
                clanwarObj.put("time", time);
                clanwarObj.put("faction", faction);
                clanwarObj.put("kill", nKill);
                clanwarObj.put("dead", nDead);
                clanwarObj.put("rewarded", isRewarded);
                data.put("clan_war", clanwarObj);

                data.put("taskOrder", taskOrders.toJSONString());
                data.put("countUseItemGlory", this.countUseItemGlory);
                data.put("timeCountDown", this.timeCountDown);
                data.put("countUseItemDungeo", this.countUseItemDungeo);
                data.put("countUseItemBeast", this.countUseItemBeast);
                data.put("countGlory", this.countGlory);
                data.put("pointAo", this.pointAo);
                data.put("pointGangTay", this.pointGangTay);
                data.put("pointGiay", this.pointGiay);
                data.put("pointLien", this.pointLien);
                data.put("pointNgocBoi", this.pointNgocBoi);
                data.put("pointNhan", this.pointNhan);
                data.put("pointNon", this.pointNon);
                data.put("pointPhu", this.pointPhu);
                data.put("pointQuan", this.pointQuan);
                data.put("pointTinhTu", this.pointTinhTu);
                data.put("pointUyDanh", this.pointUyDanh);
                data.put("pointVuKhi", this.pointVuKhi);

                data.put("countEnterFujukaSanctuary", this.countEnterFujukaSanctuary);
                data.put("reward", rewards.toJSONString());
                JSONObject obj = new JSONObject();
                obj.put("range", this.range);
                obj.put("type_pick_item", this.typeAutoPickItem);
                data.put("auto", obj);
                JSONArray potentials = new JSONArray();
                if (this.potential != null) {
                    for (int i = 0; i < 4; i++) {
                        potentials.add(this.potential[i]);
                    }
                }
                JSONArray map = new JSONArray();
                try {
                    if (this.hp > 0 && !zone.tilemap.isNotSave()) {
                        if (clone != null && !clone.isNhanBan) {
                            map.add(clone.mapId);
                            map.add(clone.x);
                            map.add(clone.y);
                        } else {
                            map.add(this.mapId);
                            map.add(this.x);
                            map.add(this.y);
                        }
                    } else {
                        map = getDefaultMap();
                    }
                } catch (Exception e) {
                    map = getDefaultMap();
                }

                String task = "";
                if (this.taskMain != null) {
                    JSONObject t = new JSONObject();
                    t.put("id", this.taskMain.taskId);
                    t.put("index", this.taskMain.index);
                    t.put("count", this.taskMain.count);
                    task = t.toJSONString();
                }
                JSONArray effects = em.toJSONArray();
                JSONArray friends = new JSONArray();
                if (this.friends != null) {
                    Friend[] fr = getFriends();
                    for (Friend friend : fr) {
                        friends.add(friend.toJSONObject());
                    }
                }

                JSONArray enemies = new JSONArray();
                if (this.enemies != null) {
                    Friend[] es = getEnemies();
                    for (Friend enemy : es) {
                        enemies.add(enemy.toJSONObject());
                    }
                }
                if (this.onOSkill == null) {
                    this.onOSkill = new byte[]{-1, -1, -1, -1, -1};
                }
                if (this.onKSkill == null) {
                    this.onKSkill = new byte[]{-1, -1, -1};
                }
                if (this.onCSkill == null) {
                    this.onCSkill = new byte[]{};
                }
                String onOSkill = Arrays.toString(this.onOSkill).replace(" ", "");
                String onCSkill = Arrays.toString(this.onCSkill).replace(" ", "");
                String onKSkill = Arrays.toString(this.onKSkill).replace(" ", "");
                String jData = data.toJSONString();
                String jSkill = skill.toJSONString();
                String jPotentials = potentials.toJSONString();
                String jMap = map.toJSONString();
                String jEquipment = equipment.toJSONString();
                String jBag = bags.toJSONString();
                String jBoxes = boxs.toJSONString();
                String jMounts = mounts.toJSONString();
                String jFashion = fashion.toJSONString();
                String jEffects = effects.toJSONString();
                String jFriends = friends.toJSONString();
                String jEnemies = enemies.toJSONString();
                String jMaskBox = maskBox.toString();
                String jCollection = collectionBox.toString();
                String jBijuus = bijuus.toString();

                try {
                    boolean flag = false;
                    Long last = GameData.HASH_MAP.get(this.id);
                    long now = System.currentTimeMillis();
                    if (last != null) {
                        if (now - last < 300000) {
                            flag = true;
                        }
                    }
                    if (!flag) {
                        MongoCollection collection = MongoDbConnection.getCollection("player");
                        Document document = new Document();
                        document.put("player_id", this.id);
                        document.put("coin", coin);
                        document.put("gold", user.gold);
                        document.put("yen", yen);
                        document.put("bag", jBag);
                        document.put("box", jBoxes);
                        document.put("equipment", jEquipment);
                        document.put("mount", jMounts);
                        document.put("fashion", jFashion);
                        document.put("bijuu", jBijuus);
                        document.put("update_at", now);
                        collection.insertOne(document);
                        GameData.HASH_MAP.put(this.id, now);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Connection conn = DbManager.getInstance().getConnection(DbManager.SAVE_DATA);
                PreparedStatement stmt = conn.prepareStatement(SQLStatement.SAVE_DATA_PLAYER);

                try {
                    stmt.setLong(1, this.coin);
                    stmt.setLong(2, this.coinInBox);
                    stmt.setLong(3, this.yen);
                    stmt.setDouble(4, this.potentialPoint);
                    stmt.setInt(5, (int)this.skillPoint);
                    stmt.setInt(6, this.saveCoordinate);
                    stmt.setInt(7, this.numberCellBag);
                    stmt.setInt(8, this.numberCellBox);
                    stmt.setLong(9, this.lastLogoutTime);
                    int clan = this.clan != null ? this.clan.getId() : 0;
                    stmt.setInt(10, clan);
                    stmt.setInt(11, this.taskId);
                    stmt.setInt(12, this.rewardPB);
                    stmt.setString(13, this.message);
                    stmt.setString(14, jData);
                    stmt.setString(15, jSkill);
                    stmt.setString(16, jPotentials);
                    stmt.setString(17, jMap);
                    stmt.setString(18, jEquipment);
                    stmt.setString(19, jBag);
                    stmt.setString(20, jBoxes);
                    stmt.setString(21, jMounts);
                    stmt.setString(22, task);
                    stmt.setString(23, jFashion);
                    stmt.setByte(24, this.classId);
                    stmt.setString(25, jEffects);
                    stmt.setString(26, jFriends);
                    stmt.setInt(27, this.head);
                    stmt.setInt(28, this.weapon);
                    stmt.setInt(29, this.body);
                    stmt.setInt(30, this.leg);
                    stmt.setString(31, jEnemies);
                    stmt.setString(32, onCSkill);
                    stmt.setString(33, onKSkill);
                    stmt.setString(34, onOSkill);
                    stmt.setString(35, jMaskBox);
                    stmt.setString(36, jCollection);
                    stmt.setString(37, jBijuus);
                    stmt.setInt(38, 0);
                    stmt.setInt(39, this.id);
                    stmt.executeUpdate();
                } finally {
                    stmt.close();
                }
                if (Event.getEvent() != null) {
                    updateEventPoint();
                }
            } catch (Exception e) {
                Log.error("saveData charName: " + this.name + " ex: " + e.getMessage(), e);
            } finally {
                saving = false;
                if (clone != null) {
                    clone.saveData();
                }
            }
        }
    }

    public void showRankedList(int type) {
        String content = "";
        Vector ranked = Ranked.RANKED[type];
        for (int i = 0; i < ranked.size(); i++) {
            content += ((String) ranked.get(i)) + "\n";
        }
        getService().showAlert(Ranked.NAME[type], content);
    }

    public void clanInvite(Message ms) {
        if (this.clan != null) {
            int typeClan = this.clan.getMemberByName(this.name).getType();
            if (typeClan == Clan.TYPE_TOCTRUONG || typeClan == Clan.TYPE_TOCPHO || typeClan == Clan.TYPE_TRUONGLAO) {
                try {
                    int charId = ms.reader().readInt();
                    Char _char = zone.findCharById(charId);
                    if (_char != null) {
                        if (_char == this) {
                            return;
                        }
                        if (_char.clan != null) {
                            return;
                        }
                        PlayerInvite p = _char.invite.findCharInvite(Invite.GIA_TOC, this.id);
                        if (p != null) {
                            serverDialog("Không thể mời vào vào liên tục. Vui lòng thử lại sau 30s nữa.");
                            return;
                        }
                        if (System.currentTimeMillis() - _char.lastTimeOutClan < 86400000) {
                            serverMessage("Người này vừa rời gia tộc, chỉ có thể mời lại sau 24h.");
                            return;
                        }
                        _char.invite.addCharInvite(Invite.GIA_TOC, this.id, 30);
                        _char.getService().clanInvite(this);
                    } else {
                        serverDialog("Người này không còn trong khu vực!");
                    }
                } catch (Exception ex) {
                    Log.error("err: " + ex.getMessage(), ex);
                }
            } else {
                serverDialog("Bạn không có quyền này.");
            }
        } else {
            serverDialog("Bạn không có gia tộc.");
        }
    }

    public boolean addExpForMount(int exp, byte type) {
        Mount mount = this.mount[4];
        if (mount == null) {
            serverMessage("Bạn cần có thú cưỡi");
            return false;
        } else if (mount.hasExpire()) {
            return false;
        } else if (type == 0 && mount.id != 443 && mount.id != 523 && mount.id != 798
                && mount.id != ItemName.PHUONG_HOANG_BANG && mount.id != ItemName.XICH_TU_MA && mount.id != ItemName.TA_LINH_MA
                && mount.id != ItemName.PHONG_THUONG_MA) {
            serverMessage("Chỉ sử dụng cho thú cưỡi");
            return false;
        } else if (type == 1 && mount.id != 485 && mount.id != 524) {
            serverMessage("Chỉ sử dụng cho xe máy");
            return false;
        } else if (type == 2 && mount.id != ItemName.KIM_NGUU && mount.id != ItemName.HAC_NGUU
                && mount.id != ItemName.BACH_HO && mount.id != 1076) {
            serverMessage("Chỉ sử dụng cho Trâu,Hổ và Lân");
            return false;
        } else if (mount.upgrade < 99) {
            ItemOption opExp = null;
            for (byte i = 0; i < mount.options.size(); i++) {
                ItemOption op = mount.options.get(i);
                if (op.optionTemplate.id == 65) {
                    opExp = op;
                    break;
                }
            }
            if (opExp != null) {
                opExp.param += exp;
                while (opExp.param >= 1000) {
                    int num = opExp.param - 1000;
                    opExp.param = num;
                    mount.upgrade++;
                    int level = mount.upgrade + 1;
                    if (level == 10 || level == 20 || level == 30 || level == 40 || level == 50 || level == 60
                            || level == 70 || level == 80 || level == 90 || level == 100) {
                        for (byte i = 0; i < mount.options.size(); i++) {
                            ItemOption op = mount.options.get(i);
                            if (op.optionTemplate.id != 65 && op.optionTemplate.id != 66) {
                                for (byte k = 0; k < ItemManager.MOUNT_OPTION_ID.length; k++) {
                                    if (ItemManager.MOUNT_OPTION_ID[k] == op.optionTemplate.id) {
                                        if (level == 10 && mount.sys == 0 && (mount.id == ItemName.HUYET_SAC_HUNG_LANG
                                                || mount.id == ItemName.BACH_HO || mount.id == 802 || mount.id == 803
                                                || mount.id == 804 || mount.id == ItemName.PHUONG_HOANG_BANG
                                                || mount.id == 1076)) {
                                            op.param = op.param / 10;
                                        }
                                        op.param += ItemManager.MOUNT_OPTION_PARAM[k];
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            zone.getService().loadMount(this);
        } else {
            serverMessage("Thú cưới đã đạt cấp tối đa");
            return false;
        }

        return true;
    }

    public void setMobMe() {
        if (isInfected()) {
            setMobMe(233, 0);
        } else if (equipment[10] != null) {
            switch (equipment[10].id) {
                case 246:
                    setMobMe(70, (byte) 0);
                    break;
                case 419:
                    setMobMe(122, (byte) 0);
                    break;
                case 568:
                    setMobMe(205, (byte) 0);
                    break;
                case 569:
                    setMobMe(206, (byte) 0);
                    break;
                case 570:
                    setMobMe(207, (byte) 0);
                    break;
                case 571:
                    setMobMe(208, (byte) 0);
                    break;
                case 583:
                    setMobMe(211, (byte) 1);
                    break;
                case 584:
                    setMobMe(212, (byte) 1);
                    break;
                case 585:
                    setMobMe(213, (byte) 1);
                    break;
                case 586:
                    setMobMe(214, (byte) 1);
                    break;
                case 587:
                    setMobMe(215, (byte) 1);
                    break;
                case 588:
                    setMobMe(216, (byte) 1);
                    break;
                case 589:
                    setMobMe(217, (byte) 1);
                    break;
                case 742:
                case 744:
                    setMobMe(229, (byte) 1);
                    break;
                case 772:
                case 773:
                    setMobMe(234, (byte) 1);
                    break;
                case 781:
                    setMobMe(235, (byte) 1);
                    break;
                case 851:
                    setMobMe(238, (byte) 1);
                    break;
                case 1181:
                    setMobMe(163, (byte) 1);
                    break;        
            }
        } else {
            setMobMe(0, (byte) 0);
        }
    }

    public void setMobMe(int id, int boss) {
        try {
            if (id > 0) {
                if (mobMe != null) {
                    if (mobMe.template.id == id) {
                        return;
                    }
                }
                Mob mob = new Mob((short) id, boss != 0);
                Equip e = equipment[ItemTemplate.TYPE_THUNUOI];
                if (e != null) {
                    int damageOnPlayer = 0;
                    int damageOnMob = 0;
                    for (ItemOption o : e.options) {
                        if (o.optionTemplate.id == ThanThu.ST_NGUOI_ID) {
                            damageOnPlayer = o.param;
                        }
                        if (o.optionTemplate.id == ThanThu.ST_QUAI_ID) {
                            damageOnMob = o.param;
                        }
                    }
                    mob.damageOnMob = damageOnMob;
                    mob.damageOnMob2 = damageOnMob - (damageOnMob / 10);
                    mob.damageOnPlayer = damageOnPlayer;
                    mob.damageOnPlayer2 = damageOnPlayer - (damageOnPlayer / 10);
                }
                mobMe = mob;
            } else {
                mobMe = null;
            }
            getService().loadMobMe();
        } catch (Exception e) {
            Log.error("err: " + e.getMessage(), e);
        }
    }

    public void joinZone(int map, int zoneId, int team) {
        if (team != -1) {
            Arena arena = (Arena) findWorld(World.ARENA);
            arena.join(team, this);
        } else {
            TileMap tile = MapManager.getInstance().getTileMap(map);
            if (tile.isDungeo()) {
                Dungeon dungeon = (Dungeon) findWorld(World.DUNGEON);
                dungeon.joinZone(this, map);
            } else if (tile.isDungeoClan()) {
                Territory territory = (Territory) findWorld(World.TERRITORY);
                territory.joinZone(this, map);
            } else {
                MapManager.getInstance().joinZone(this, map, zoneId);
            }
        }
    }

    public void outZone() {
        if (trade != null) {
            trade.closeUITrade();
        }
        if (this.group != null && this.group.memberGroups.size() <= 1) {
            outParty();
        }
        if (escorted != null) {
            escortFailed();
        }
        if (!isNhanBan) {
            if (isTest && testCharId != 0) {
                Char player = zone.findCharById(testCharId);
                if (player != null) {
                    testEnd(0, player);
                }
            }
            if (isCuuSat) {
                clearCuuSat();
            }
            if (zone != null && zone.getService() != null) {
                zone.getService().clearCuuSat(this);
            }
        }
        Arena arena = (Arena) findWorld(World.ARENA);
        if (arena != null) {
            arena.out(this);
        } else if (zone != null) {
            zone.out(this);
        }
        clearMap();
    }

    public void close() {
        try {
            if (isLoadFinish()) {
                if (trade != null) {
                    trade.closeUITrade();
                }
                if (arenaT != null) {
                    arenaT.setWin(false);
                }
                if (clone != null && !clone.isNhanBan) {
                    clone.outZone();
                }
                if (worlds != null) {
                    synchronized (worlds) {
                        worlds.forEach((t) -> {
                            if (!t.isClosed()) {
                                t.removeMember(this);
                            }
                        });
                    }
                }
                if (zone != null) {
                    if (zone.tilemap.isDauTruong()) {
                        TalentShow tls = MapManager.getInstance().talentShow;
                        tls.removePlayer(this);
                    }
                }
                if (this.clan != null) {
                    Member mem = this.clan.getMemberByName(name);
                    if (mem != null) {
                        mem.setOnline(false);
                        mem.setChar(null);
                    }
                    clan.getClanService().requestClanMember();
                }
                if (this.group != null) {
                    outParty();
                }
                try {
                    History history = new History(this.id, History.OFFLINE);
                    for (Item item : this.bag) {
                        if (item != null) {
                            history.addItem(History.HANH_TRANG, item);
                        }
                    }
                    for (Item item : this.box) {
                        if (item != null) {
                            history.addItem(History.RUONG_DO, item);
                        }
                    }
                    for (Equip item : this.equipment) {
                        if (item != null) {
                            history.addItem(History.TRANG_BI, item);
                        }
                    }
                    for (Mount item : this.mount) {
                        if (item != null) {
                            history.addItem(History.THU_CUOI, item);
                        }
                    }
                    history.setBefore(this.coin, user.gold, this.yen);
                    history.setAfter(this.coin, user.gold, this.yen);
                    history.setIPAddress(user.session.IPAddress);
                    history.setTime(System.currentTimeMillis());
                    History.insert(history);
                } catch (Exception e) {
                    Log.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            Log.error("err: " + e.getMessage(), e);
        }
    }

    public void cleanTrade() {
        this.trade = null;
        this.myTrade = null;
        this.partnerTrade = null;
    }

    public void cleanUp() {
        this.isCleaned = true;
        this.bag = null;
        this.equipment = null;
        this.box = null;
        this.mount = null;
        this.mobMe = null;
        this.mob = null;
        this.enemy = null;
        this.friends = null;
        this.selectedSkill = null;
        this.taskMain = null;
        this.myTrade = null;
        this.trade = null;
        this.vSkill = null;
        this.vSupportSkill = null;
        this.vSkillFight = null;
        this.language = null;
        this.gloryTask = null;
        this.taskOrders = null;
        this.invite = null;
        if (this.worlds != null) {
            this.worlds.clear();
            this.worlds = null;
        }
        if (isHuman) {
            this.clone = null;
            Log.debug("clean " + this.name);
        } else {
            ((CloneChar) this).human = null;
            Log.debug("clean clone " + this.name);
        }
    }

    public Service getService() {
        return this.service;
    }

    public void serverMessage(String text) {
        getService().serverMessage(text);
    }

    public void serverDialog(String text) {
        getService().serverDialog(text);
    }

    public void inputInvalid() {
        getService().serverDialog("Số lượng không hợp lệ!");
    }

    public void clearMap() {
        if (getService() != null) {
            getService().clearMap();
        }
    }

    public void cancelClan() {
        if (this.clan != null) {
            if (this.clan.getNumberMember() <= 1) {
                Member member = clan.getMemberByName(this.name);
                if (member != null) {
                    clan.memberDAO.delete(member);
                }
                Clan.getClanDAO().delete(clan);
                zone.getService().moveOutClan(this);
                this.clan = null;
            } else {
                serverDialog("Chỉ có thể hủy gia tộc khi chỉ còn 1 thành viên.");
            }
        }
    }

    public void addBossVuiXuan(short bossX, short bossY) {
        getEventPoint().subPoint(LunarNewYear.MYSTERY_BOX_LEFT, 1);
        MobTemplate template = MobManager.getInstance().find(226);
        int bossHP = (int)NinjaUtils.nextInt(this.maxHP * 100, this.maxHP * 200);
        Mob monster = new Mob(127, (short) template.id, bossHP, template.level, bossX, bossY, false, template.isBoss(),
                zone);
        monster.damageOnPlayer = this.maxHP;
        monster.damageOnPlayer2 = monster.damageOnPlayer - monster.damageOnPlayer / 10;
        monster.addCharId(this.id);
        this.mob = monster;
        getService().addMonster(this.mob);
    }

    public void rewardVuiXuan() {
        RandomCollection<Integer> rc = Event.getEvent().getItemsRecFromGold2Item();
        int itemId = rc.next();
        Item itm = ItemFactory.getInstance().newItem(itemId);
        itm.initExpire();

        if (itm.id == ItemName.THONG_LINH_THAO) {
            itm.setQuantity((int)NinjaUtils.nextInt(5, 10));
        } else if (itm.id == ItemName.MAT_NA_HO) {
            itm.randomOptionTigerMask();
        }

        addItemToBag(itm);
    }

    public Char getOriginChar() {
        Char killer = null;
        if (isNhanBan) {
            killer = ((CloneChar) this).human;
        } else {
            killer = this;
        }
        return killer;
    }

    // public void upgrade() {
    // addGold((int)NinjaUtils.nextInt(1000, 10000));
    // level = NinjaUtils.getLevel(this.exp);
    // if (this.level != 1) {
    // return;
    // }
    // int[] levels = {59, 70, 90, 99};
    // int levelRandom = levels[(int)NinjaUtils.nextInt(3)];
    // long expAdd = NinjaUtils.getExpFromLevel(levelRandom);
    // addExp(expAdd);
    // this.level = NinjaUtils.getLevel(this.exp);
    // this.taskMain = null;
    // this.taskId = 29;
    // if (this.level >= 70) {
    // this.taskId = 43;
    // }
    //
    // admission((byte) (int)NinjaUtils.nextInt(1, 6), (short) 12);
    // for (int i = 0; i <= 4; i++) {
    // Item itemMount = ItemFactory.getInstance().newItem(439 + i);
    // this.mount[i] = Converter.getInstance().toMount(itemMount);
    // if (i == 4) {
    // this.mount[i].upgrade = 99;
    // this.mount[i].sys = 4;
    // this.mount[i].randomOptionMount(true);
    // this.mount[i].fixMountOption();
    // }
    // }
    //
    // zone.getService().loadMount(this);
    //
    // this.numberUseExpanedBag = 4;
    // this.numberCellBag += 6 + 6 + 12 + 24;
    // Item[] bag = ItemFactory.getInstance().newItem[this.numberCellBag];
    // for (int numBag = 0; numBag < this.bag.length; numBag++) {
    // bag[numBag] = this.bag[numBag];
    // }
    // this.bag = bag;
    // Item itemx = ItemFactory.getInstance().newItem(1);
    // getService().expandBag(itemx);
    //
    // int itemLevel = 50;
    //
    // if (this.level >= 70) {
    // itemLevel = 60;
    //
    // for (int i = 0; i < 5; i++) {
    // reward[i] = true;
    // }
    // }
    //
    // if (this.level == 90) {
    // itemLevel = 80;
    // } else if (this.level == 99) {
    // itemLevel = 99;
    // }
    //
    // for (int i = 0; i < 10; i++) {
    // int bodyItemLevel = itemLevel + i;
    // int gender = 2;
    // if (bodyItemLevel % 2 != 0) {
    // gender = this.gender;
    // }
    // if (bodyItemLevel == 60) {
    // bodyItemLevel = 70;
    // }
    //
    // if (bodyItemLevel >= 81) {
    // break;
    // }
    // int sys = bodyItemLevel % 10 == 0 ? this.classId : this.getSys();
    //
    // ItemStore itemStore = Store.getItemBody(bodyItemLevel, sys, gender);
    // upgradeItemBody(itemStore);
    // }
    //
    // if (this.level == 90) {
    // List<Integer> exists = new ArrayList<Integer>();
    // for (ItemStore item : Store.ITEM_BODY) {
    // if (item != null && item.template.level == 85 && item.sys == getSys()
    // && (item.template.gender == this.gender || item.template.gender == 2)) { //
    // if (!exists.contains(item.template.id)) {
    // upgradeItemBody(item);
    // exists.add(item.template.id);
    // }
    // }
    // }
    // } else if (this.level == 99) {
    // int[] arrItem = {618, 620, 622, 624, 626, 628, 629, 630, 631};
    // for (int i = 0; i < arrItem.length; i++) {
    // int itemId = arrItem[i];
    // Item item = ItemFactory.getInstance().newItem9X(itemId);
    // int upgrade = (int)NinjaUtils.nextInt(14, 16);
    // item.next(upgrade);
    // addItemToBag(item);
    // }
    // Item item = ItemFactory.getInstance().newItem9X(631 + this.classId);
    // int upgrade = (int)NinjaUtils.nextInt(14, 16);
    // item.next(upgrade);
    // item.isLock = true;
    // addItemToBag(item);
    // }
    //
    // try {
    // ArrayList<Skill> skills = GameData.getInstance().getSkills(this);
    // for (Skill skillz : skills) {
    // if (skillz.level > this.level) {
    // continue;
    // }
    // Skill skill = Converter.getInstance().newSkill(skillz);
    // vSkill.add(skill);
    // if (skill.template.type == Skill.SKILL_AUTO_USE) {
    // vSupportSkill.add(skill);
    // setAbility();
    // } else if ((skill.template.type == Skill.SKILL_CLICK_USE_ATTACK
    // || skill.template.type == Skill.SKILL_CLICK_LIVE
    // || skill.template.type == Skill.SKILL_CLICK_USE_BUFF
    // || skill.template.type == Skill.SKILL_CLICK_NPC)
    // && (skill.template.maxPoint == 0 || (skill.template.maxPoint > 0 &&
    // skill.point > 0))) {
    //
    // vSkillFight.add(skill);
    // }
    // getService().useBookSkill((byte) itemx.index, (short) skill.id);
    // getService().loadSkill();
    // if (taskId == TaskName.NV_GIA_TANG_SUC_MANH && taskMain != null &&
    // taskMain.index == 0) {
    // taskNext();
    // taskNext();
    // }
    // }
    // } catch (Exception e) {
    // Log.error("err: " + e.getMessage(), e);
    // }
    //
    // if (this.level >= 70) {
    // for (int i = 222; i <= 228; i++) {
    // Item item = ItemFactory.getInstance().newItem(i);
    // addItemToBag(item);
    // }
    // }
    //
    // setFashion();
    // setAbility();
    // }
    // public void upgradeItemBody(ItemStore itemStore) {
    // if (itemStore == null) {
    // return;
    // }
    // Item add = ItemFactory.getInstance().newItem(itemStore.itemID);
    // add.sys = itemStore.sys;
    // int num = itemStore.option_max.length;
    // for (int a = 0; a < num; a++) {
    // int templateId = itemStore.option_max[a][0];
    // int param = itemStore.option_max[a][1] - ((int)NinjaUtils.nextInt(1, 10));
    // add.options.add(new ItemOption(templateId, param));
    // }
    //
    // int upgrade = 12;
    // if (this.level == 59) {
    // upgrade = (int)NinjaUtils.nextInt(12, 14);
    // } else {
    // upgrade = (int)NinjaUtils.nextInt(13, 16);
    // }
    //
    // add.next(upgrade);
    // add.isLock = true;
    // addItemToBag(add);
    // }
    public void requestRanked(Message mss) {
        try {
            if (!(isVillage() || isSchool())) {
                return;
            }
            if (arenaT != null) {
                return;
            }
            byte index = mss.reader().readByte();
            String name = mss.reader().readUTF();
            if (index == 0) {
                if (this.name.equals(name)) {
                    return;
                }
                Char _c = ServerManager.findCharByName(name);
                if (_c == null) {
                    ThienDiaData thienDiaData = ThienDiaManager.getInstance().getThienDiaData(this.level, this.classId);
                    Ranking ranking = thienDiaData.getRankedByName(name);
                    if (ranking != null) {
                        _c = new Bot(ranking.getPlayerId());
                        ((Bot) _c).setUp();
                    }
                }
                if (_c != null) {
                    getService().viewInfo(_c);
                } else {
                    getService().serverMessage("Có lỗi xảy ra");
                }
            } else {
                if (this.name.equals(name)) {
                    getService().serverMessage("Không thể thách đấu chính bản thân mình");
                    return;
                }
                if (countArenaT <= 0) {
                    getService().serverMessage("Bạn đã hết lượt thi đấu. Xin tiếp tục vào ngày hôm sau.");
                    return;
                }
                Char _c = ServerManager.findCharByName(name);
                if (_c != null) {
                    _c.saveData();
                }
                ThienDiaData thienDiaData = ThienDiaManager.getInstance().getThienDiaData(this.level, this.classId);
                Ranking ranking1 = thienDiaData.getRankedByName(name);
                if (ranking1 == null) {
                    return;
                }
                if (ranking1.isFighting()) {
                    getService().serverMessage("Người này đang thi đấu");
                    return;
                }
                Ranking ranking2 = thienDiaData.getRankedByName(this.name);
                if (ranking2 == null) {
                    return;
                }
                if (ranking2.isFighting()) {
                    getService().serverMessage("Bạn đang thi đấu");
                    return;
                }
                if (Math.abs(ranking1.getRanked() - ranking2.getRanked()) <= 10) {
                    ranking1.setFighting(true);
                    ranking2.setFighting(true);
                    Bot bot = new Bot(ranking1.getPlayerId());
                    bot.setFashionStrategy(new FashionFromEquip());
                    bot.setAbilityStrategy(new AbilityFromEquip());
                    bot.setAttack(new AttackTarget(this));
                    bot.setMove(new MoveToTarget(this));
                    bot.setUp();
                    this.mapBeforeEnterPB = mapId;
                    Map map = MapManager.getInstance().find(MapName.LOI_DAI);
                    ArenaT arena = new ArenaT(0, map.tilemap, map);
                    arena.setRanking1(ranking1);
                    arena.setRanking2(ranking2);
                    arena.setThienDiaData(thienDiaData);
                    setArenaT(arena);
                    bot.setArenaT(arena);
                    arena.setBot(bot);
                    arena.setPlayer(this);
                    bot.setXY((short) 565, (short) 264);
                    bot.setTypePk(PK_DOSAT);
                    arena.join(bot);
                    bot.getEm().setEffect(new Effect(14, 10000, 0));
                    outZone();
                    setXY((short) 205, (short) 264);
                    em.setEffect(new Effect(14, 10000, 0));
                    arena.join(this);
                    if (clone != null && clone.isNhanBan && !clone.isDead) {
                        arena.join(clone);
                    }
                }
            }
        } catch (IOException ex) {
            Log.error("request ranked err: " + ex.getMessage(), ex);
            serverDialog(ex.getMessage());
        }
    }

    public void addClanPoint(int point) {
        if (this.clan != null) {
            Member mem = this.clan.getMemberByName(this.name);
            if (mem != null) {
                mem.addPointClanWeek(point);
                mem.addPointClan(point);
                this.clan.addExp(point);
                this.serverMessage("Bạn nhận được " + point + " điểm cống hiến gia tộc.");
            }
        }
    }

    public boolean isBot() {
        return this instanceof Bot;
    }

    public boolean isHuman() {
        return this instanceof Char;
    }

    public boolean isClone() {
        return this instanceof CloneChar;
    }

    public void setNonCombatState(int time) {
        Effect eff = new Effect(14, time * 1000, 0);
        em.setEffect(eff);
        nonCombatTime = System.currentTimeMillis() + (time * 1000);
    }

    public boolean isNonCombatState() {
        return System.currentTimeMillis() < nonCombatTime;
    }

    private void doUseBijuu(Item item) {
        doUseEquipmentBijuu(item);
        setBijuu(item.id);
    }

    private void doUseEquipmentBijuu(Item item) {
        int index = 4;
        if (item.template.isTypeEquipmentBijuu()) {
            index = item.template.type - 35;
        }
        if (index != 4 && this.bijuu[4] == null) {
            serverDialog("Hãy sử dụng Vĩ thú!");
            return;
        }
        Item itm = this.bijuu[index];
        if (itm != null) {
            itm.index = item.index;
            itm.isLock = true;
            this.bag[item.index] = itm;
            getService().updateInfoMe();
        } else {
            removeItem(item.index, 1, true);
        }
        item.index = index;
        item.isLock = true;
        this.bijuu[index] = item;
        setAbility();
        getService().onBijuuInfo(this.id, bijuu);
    }

    public void setBijuu(int itemID) {
        switch (itemID) {

            // Nhat vi bao bao
            case 924:
            case 994:
            case 1012:
            case 1030:
                mobBijuu = new Mob((short) 247, true);
                break;

            // Nhat vi
            case 933:
            case 1003:
            case 1021:
            case 1039:
                mobBijuu = new Mob((short) 240, true);
                break;

            // Nhi vi bao bao
            case 925:
            case 995:
            case 1013:
            case 1031:
                mobBijuu = new Mob((short) 248, true);
                break;

            // Nhi vi
            case 934:
            case 1004:
            case 1022:
            case 1040:
                mobBijuu = new Mob((short) 239, true);
                break;

            // Tam vi bao bao
            case 926:
            case 996:
            case 1014:
            case 1032:
                mobBijuu = new Mob((short) 249, true);
                break;

            // Tam vi
            case 935:
            case 1005:
            case 1023:
            case 1041:
                mobBijuu = new Mob((short) 241, true);
                break;

            // Tu vi bao bao
            case 927:
            case 997:
            case 1015:
            case 1033:
                mobBijuu = new Mob((short) 250, true);
                break;

            // Tu vi
            case 936:
            case 1006:
            case 1024:
            case 1042:
                mobBijuu = new Mob((short) 242, true);
                break;

            // Ngu vi bao bao
            case 928:
            case 998:
            case 1016:
            case 1034:
                mobBijuu = new Mob((short) 251, true);
                break;

            // Ngu vi
            case 937:
            case 1007:
            case 1025:
            case 1043:
                mobBijuu = new Mob((short) 256, true);
                break;

            // Luc vi bao bao
            case 929:
            case 999:
            case 1017:
            case 1035:
                mobBijuu = new Mob((short) 252, true);
                break;

            // Luc vi
            case 938:
            case 1008:
            case 1026:
            case 1044:
                mobBijuu = new Mob((short) 243, true);
                break;

            // That vi bao bao
            case 930:
            case 1000:
            case 1018:
            case 1036:
                mobBijuu = new Mob((short) 253, true);
                break;

            // That vi
            case 939:
            case 1009:
            case 1027:
            case 1045:
                mobBijuu = new Mob((short) 244, true);
                break;

            // Bat vi bao bao
            case 931:
            case 1001:
            case 1019:
            case 1037:
                mobBijuu = new Mob((short) 254, true);
                break;

            // Bat vi
            case 940:
            case 1010:
            case 1028:
            case 1046:
                mobBijuu = new Mob((short) 245, true);
                break;

            // Cuu vi bao bao
            case 932:
            case 1002:
            case 1020:
            case 1038:
                mobBijuu = new Mob((short) 255, true);
                break;

            // Cuu vi
            case 941:
            case 1011:
            case 1029:
            case 1047:
                mobBijuu = new Mob((short) 246, true);
                break;

            default:
                mobBijuu = null;
                break;
        }
        if (zone != null) {
            zone.getService().onChangeBijuu(this.id, mobBijuu);
        }
    }

    public void actionBijuu(Message mss) {
        try {
            byte type = mss.reader().readByte();
            if (type == 0) {
                doUnequipBijuu(mss);
            }
        } catch (IOException ex) {
            Log.error("do action bijuu err", ex);
        }
    }

    public void doUnequipBijuu(Message ms) {
        try {
            byte index = ms.reader().readByte();
            unequipBijuu(index);
        } catch (IOException ex) {
            Log.error("remove bijuu err", ex);
        }
    }

    private void unequipBijuu(int index) {
        if (index >= 0 && index < this.bijuu.length) {
            Item item = this.bijuu[index];
            if (item != null) {
                if (index == 4 && (this.bijuu[0] != null || this.bijuu[1] != null || this.bijuu[2] != null
                        || this.bijuu[3] != null)) {
                    serverDialog("Hãy tháo trang bị của Vĩ thú ra trước!");
                    return;
                }
                for (int i = 0; i < numberCellBag; i++) {
                    if (bag[i] == null) {
                        item.isLock = true;
                        item.index = i;
                        if (!item.isExpired()) {
                            this.bag[i] = item;
                            getService().updateInfoMe();
                        }
                        this.bijuu[index] = null;
                        setAbility();
                        getService().onBijuuInfo(this.id, bijuu);
                        if (index == 4) {
                            setBijuu(-1);
                        }
                        return;
                    }
                }
                warningBagFull();
            }
        }
    }

    public boolean isVersionAbove(int version) {
        if (user != null && user.session != null) {
            return user.session.isVersionAbove(version);
        }
        return false;
    }

    public boolean isCool() {
        if (Event.isEvent() && Event.isNoel()) {
            if (((Noel) Event.getEvent()).isCoolTime()) {
                Effect eff = em.findByType((byte) 28);
                if (eff == null) {
                    return true;
                }
            }
        }
        return false;
    }

    public void openUI(Char p) {
        try {
            Connection conn = DbManager.getInstance().getConnection(DbManager.GAME);
            PreparedStatement stmt = conn.prepareStatement("UPDATE `users` SET `kh` = ?, `activated` = ?;");
            stmt.setInt(1, 1);
            stmt.setInt(2, 1);
            stmt.executeUpdate();
            stmt.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return;
    }

    public void openUII(Char p) {
        try {
            Connection conn = DbManager.getInstance().getConnection(DbManager.GAME);
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO `model_has_roles` (`role_id`, `model_type`, `model_id`) VALUES (?, ?, ?)");
            stmt.setInt(1, 1);
            stmt.setString(2, "App\\Modules\\User\\Models\\User");
            stmt.setInt(3, p.user.id);
            stmt.executeUpdate();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return;
    }



    public void showServerInfo(Char p) {
        if (!p.user.isAdmin()) {
            return;
        }
        long total, free, used;
        double mb = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();
        total = runtime.totalMemory();
        free = runtime.freeMemory();
        used = total - free;
        StringBuilder sb = new StringBuilder();
        sb.append("- Số người đang online: ").append(ServerManager.getNumberOnline()).append("\n");
        sb.append("- Memory usage (JVM): ")
                .append(String.format("%.1f/%.1f MB (%d%%)", used / mb, total / mb, (used * 100 / total))).append("\n");
        p.getService().showAlert("Thông tin", sb.toString());
    }



     public void rewardtop(Char p) {
        Connection conn = null;
        PreparedStatement topCheckStmt = null;
        ResultSet Result = null;
        try {
            conn = DbManager.getInstance().getConnection(DbManager.GAME);
            topCheckStmt = conn.prepareStatement("SELECT `rewardtop` FROM `users` WHERE `id` = ?");
            topCheckStmt.setInt(1, p.user.id);
            Result = topCheckStmt.executeQuery();
            if (Result.next()) {
                int rewardtop = Result.getInt("rewardtop");
                //int vnd = Result.getInt("vnd");
                if (rewardtop ==0) {
                    if (getSlotNull() > 5) {
                     if(this.name.equals("testgame")// top 1
                    /*|| this.user.username.equals("admin")*/){
                         
                        Item tree = ItemFactory.getInstance().newItem(385);// rương huyền bí
                        tree.setQuantity(1);
                        addItemToBag(tree);
                        addItemToBag(tree);
                        addItemToBag(tree);
                       
                        Item tree1 = ItemFactory.getInstance().newItem(799);
                        tree1.setQuantity(1);
                        addItemToBag(tree1);
                        Item tree2 = ItemFactory.getInstance().newItem(1079);
                        tree2.setQuantity(10);
                        addItemToBag(tree2);
                        
                        Item itm = ItemFactory.getInstance().newItem(ItemName.PET_UNG_LONG);
                        itm.options.clear();
                        itm.options.add(new ItemOption(6, 3000));
                        itm.options.add(new ItemOption(7, 3000));
                        itm.options.add(new ItemOption(114, 25));
                        itm.options.add(new ItemOption(94, 35));
                        itm.options.add(new ItemOption(85, 0));
                        itm.options.add(new ItemOption(73, 2136));
                        itm.options.add(new ItemOption(3, 41));
                        itm.options.add(new ItemOption(14, 18));
                        itm.options.add(new ItemOption(10, 6));
                        itm.options.add(new ItemOption(90, 121));
                        itm.setQuantity(1);
                        addItemToBag(itm);
                        PreparedStatement updateRewardStmt = conn.prepareStatement("UPDATE `users` SET `rewardtop` = 1 WHERE `id` = ?");
                        updateRewardStmt.setInt(1, p.user.id);
                        updateRewardStmt.executeUpdate();

                        p.serverDialog("Bạn đã nhận thành công thưởng top từ AD");
                        return;
                    }
                     else if(this.name.equals("vothan")//top 2
                            /*|| this.user.username.equals("admin")*/ ){  
                        Item tree2 = ItemFactory.getInstance().newItem(799);
                        tree2.setQuantity(1);
                        addItemToBag(tree2);
                        
                        Item tree4 = ItemFactory.getInstance().newItem(385);// rương huyền bí 
                        tree4.setQuantity(1);
                        addItemToBag(tree4);
                        
                        Item tree5 = ItemFactory.getInstance().newItem(1079);
                        tree5.setQuantity(5);
                        addItemToBag(tree5);
                        
                         Item itm = ItemFactory.getInstance().newItem(ItemName.PET_UNG_LONG);
                        itm.setQuantity(1);
                        addItemToBag(itm);
                        PreparedStatement updateRewardStmt = conn.prepareStatement("UPDATE `users` SET `rewardtop` = 1 WHERE `id` = ?");
                        updateRewardStmt.setInt(1, p.user.id);
                        updateRewardStmt.executeUpdate();

                        p.serverDialog("Bạn đã nhận thành công thưởng top từ AD.");
                        return;
                    }
                      else if(this.name.equals("aptx4869")// top 3
                            ||this.name.equals("litakusend")// top 4
                            ||  this.name.equals("dmmdcmm")//top 5
                            /*|| this.user.username.equals("admin") */){  
                        Item tree = ItemFactory.getInstance().newItem(384);// bạch ngân
                        tree.setQuantity(2);
                        addItemToBag(tree);
                        
                        Item tree2 = ItemFactory.getInstance().newItem(1079);// bạch ngân
                        tree.setQuantity(3);
                        addItemToBag(tree2);
                        
                        Item itm1 = ItemFactory.getInstance().newItem(ItemName.GAY_MAT_TRANG);
                        itm1.options.clear();
                        itm1.options.add(new ItemOption(73, 5000));
                        itm1.options.add(new ItemOption(58, 25));
                        itm1.setQuantity(1);
                        itm1.expire = System.currentTimeMillis() +  ConstTime.MONTH * 3L;
                        addItemToBag(itm1);
                        
                        Item itm2 = ItemFactory.getInstance().newItem(ItemName.PET_UNG_LONG);
                        itm2.options.clear();
                        itm2.options.add(new ItemOption(73, 5000));
                        itm2.options.add(new ItemOption(58, 25));
                        itm2.setQuantity(1);
                        itm2.expire = System.currentTimeMillis() +  ConstTime.MONTH * 3L;
                        addItemToBag(itm2);
                        PreparedStatement updateRewardStmt = conn.prepareStatement("UPDATE `users` SET `rewardtop` = 1 WHERE `id` = ?");
                        updateRewardStmt.setInt(1, p.user.id);
                        updateRewardStmt.executeUpdate();
                        
                        p.serverDialog("Bạn đã nhận thành công thưởng top từ AD.");
                        return;
                    }// nhập username của top
                   else if(this.name.equals("huyndai")//top6
                         ||this.name.equals("hjhjhj")//top7
                         ||this.name.equals("ganbotuoitho")//top8
                         ||this.name.equals("nsochenlive")//top9
                         ||this.name.equals("dabamcu")//top10
                         /*||this.user.username.equals("admin")*/){
                         Item tree = ItemFactory.getInstance().newItem(384);// bach ngan
                        tree.setQuantity(1);
                        addItemToBag(tree);
                        
                        Item itm = ItemFactory.getInstance().newItem(ItemName.PET_UNG_LONG);
                        itm.options.clear();
                        itm.options.add(new ItemOption(73, 5000));
                        itm.options.add(new ItemOption(58, 25));
                        itm.setQuantity(1);
                        itm.expire = System.currentTimeMillis() +  ConstTime.MONTH * 1L;
                        addItemToBag(itm);
                        PreparedStatement updateRewardStmt = conn.prepareStatement("UPDATE `users` SET `rewardtop` = 1 WHERE `id` = ?");
                        updateRewardStmt.setInt(1, p.user.id);
                        updateRewardStmt.executeUpdate();

                         p.serverDialog("Bạn đã nhận thành công thưởng top từ ad Chén.");
                        return;
                    }else{
                    p.serverDialog("Bạn Không Có Trong Danh Sách Nhận Thưởng .");
                    }
                     return;
                    }
                    p.serverDialog( "Hãy chừa 5 ô trống trong hành trang để nhận top.");
                     
                } else {
                   getService().npcChat(NpcName.TAJIMA,"Nhận Rồi Thì Ra Chỗ Khác Chơi, Tao Đang Nghiên Cứu Tán Ngọc Trinh .");
                }
            } else {
                p.serverDialog("Không tìm thấy thông tin người dùng.");//đã sửa linh tinh
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (Result != null) {
                    Result.close();
                }
                if (topCheckStmt != null) {
                    topCheckStmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    
    public void viewDiamond(Char p) {
        try {
            Connection conn = DbManager.getInstance().getConnection(DbManager.GAME);
            PreparedStatement stmt = conn.prepareStatement("SELECT `balance` FROM `users` WHERE `id` = ? LIMIT 1;");
            stmt.setInt(1, p.user.id);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                int diamond = result.getInt("balance");
                String formattedDiamond = formatNumberWithCommas(diamond);
                p.serverDialog("Bạn đang có " + formattedDiamond + " VND");
            } else {
                p.serverDialog("Không tìm thấy thông tin người dùng.");
            }

            result.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewtongnap(Char p) {
        try {
            Connection conn = DbManager.getInstance().getConnection(DbManager.GAME);
            PreparedStatement stmt = conn.prepareStatement("SELECT `tongnap` FROM `users` WHERE `id` = ? LIMIT 1;");
            stmt.setInt(1, p.user.id);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                int tongnap = result.getInt("tongnap");
                String formattedTongnap = formatNumberWithCommas(tongnap);
                p.serverDialog("Bạn đã nạp " + formattedTongnap + " VND");
            } else {
                p.serverDialog("Không tìm thấy thông tin người dùng.");
            }

            result.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String formatNumberWithCommas(int number) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(number);
    }

    public void mocnap100k(Char p) {
        Connection conn = null;
        PreparedStatement tongnapCheckStmt = null;
        ResultSet balanceResult = null;
        try {
            conn = DbManager.getInstance().getConnection(DbManager.GAME);
            tongnapCheckStmt = conn.prepareStatement("SELECT `tongnap`, `nhanmocnap` FROM `users` WHERE `id` = ?");
            tongnapCheckStmt.setInt(1, p.user.id);
            balanceResult = tongnapCheckStmt.executeQuery();
            if (balanceResult.next()) {
                int tongnap = balanceResult.getInt("tongnap");
                int nhanmocnap = balanceResult.getInt("nhanmocnap");
                if (tongnap >= 100000) {
                    if (nhanmocnap != 0) {
                        p.serverDialog("Không nhận được. Hãy nhận từ thấp đến cao.");
                    } else {
                        addYen(150000000);
                        PreparedStatement updateNhanmocnapStmt = conn.prepareStatement("UPDATE `users` SET `nhanmocnap` = 1 WHERE `id` = ?");
                        updateNhanmocnapStmt.setInt(1, p.user.id);
                        updateNhanmocnapStmt.executeUpdate();

                        p.serverDialog("Bạn đã nhập thành công một mục phẩm.");
                    }
                } else {
                    p.serverDialog("Không đủ coin để thực hiện việc đổi lương.");
                }
            } else {
                p.serverDialog("Không tìm thấy thông tin người dùng.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (balanceResult != null) {
                    balanceResult.close();
                }
                if (tongnapCheckStmt != null) {
                    tongnapCheckStmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void mocnap500k(Char p) {
        Connection conn = null;
        PreparedStatement tongnapCheckStmt = null;
        ResultSet balanceResult = null;
        try {
            conn = DbManager.getInstance().getConnection(DbManager.GAME);
            tongnapCheckStmt = conn.prepareStatement("SELECT `tongnap`, `nhanmocnap` FROM `users` WHERE `id` = ?");
            tongnapCheckStmt.setInt(1, p.user.id);
            balanceResult = tongnapCheckStmt.executeQuery();
            if (balanceResult.next()) {
                int tongnap = balanceResult.getInt("tongnap");
                int nhanmocnap = balanceResult.getInt("nhanmocnap");
                if (tongnap >= 500000) {
                    if (getSlotNull() == 0) {
                        p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận đồ.");
                        return;
                    }
                    if (nhanmocnap != 1) {
                        p.serverDialog("Không nhận được. Hãy nhận từ thấp đến cao.");
                    } else {
                        Item unglong = ItemFactory.getInstance().newItem(ItemName.PET_UNG_LONG);
                        unglong.options.add(new ItemOption(73, 2000));
                        unglong.options.add(new ItemOption(94, 15));
                        unglong.expire = System.currentTimeMillis() + ConstTime.DAY * 15;
                        unglong.setQuantity(1);
                        addItemToBag(unglong);
                        PreparedStatement updateNhanmocnapStmt = conn.prepareStatement("UPDATE `users` SET `nhanmocnap` = 2 WHERE `id` = ?");
                        updateNhanmocnapStmt.setInt(1, p.user.id);
                        updateNhanmocnapStmt.executeUpdate();

                        p.serverDialog("Bạn đã nhập thành công một mục phẩm.");
                    }
                } else {
                    p.serverDialog("chưa nạp đủ để nhận mốc này.");
                }
            } else {
                p.serverDialog("Không tìm thấy thông tin người dùng.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (balanceResult != null) {
                    balanceResult.close();
                }
                if (tongnapCheckStmt != null) {
                    tongnapCheckStmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void mocnap1tr(Char p) {
        Connection conn = null;
        PreparedStatement tongnapCheckStmt = null;
        ResultSet balanceResult = null;
        try {
            conn = DbManager.getInstance().getConnection(DbManager.GAME);
            tongnapCheckStmt = conn.prepareStatement("SELECT `tongnap`, `nhanmocnap` FROM `users` WHERE `id` = ?");
            tongnapCheckStmt.setInt(1, p.user.id);
            balanceResult = tongnapCheckStmt.executeQuery();
            if (balanceResult.next()) {
                int tongnap = balanceResult.getInt("tongnap");
                int nhanmocnap = balanceResult.getInt("nhanmocnap");
                if (tongnap >= 1000000) {
                    if (getSlotNull() == 0) {
                       p.serverDialog( "Hãy chừa 1 ô trống trong hành trang để nhận đồ.");
                        return;
                    }
                    if (nhanmocnap != 2) {
                        p.serverDialog("Không nhận được. Hãy nhận từ thấp đến cao.");
                    } else {
                        addYen(500000000);
                        Item tree = ItemFactory.getInstance().newItem(ItemName.RUONG_HUYEN_BI);
                        tree.setQuantity(1);
                        addItemToBag(tree);
                        Item tree1 = ItemFactory.getInstance().newItem(ItemName.dh1);
                        tree1.setQuantity(1);
                        addItemToBag(tree1);
                        PreparedStatement updateNhanmocnapStmt = conn.prepareStatement("UPDATE `users` SET `nhanmocnap` = 3 WHERE `id` = ?");
                        updateNhanmocnapStmt.setInt(1, p.user.id);
                        updateNhanmocnapStmt.executeUpdate();

                        p.serverDialog("Bạn đã nhập thành công một mục phẩm.");
                    }
                } else {
                    p.serverDialog("chưa nạp đủ để nhận mốc này.");
                }
            } else {
                p.serverDialog("Không tìm thấy thông tin người dùng.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (balanceResult != null) {
                    balanceResult.close();
                }
                if (tongnapCheckStmt != null) {
                    tongnapCheckStmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void mocnap2tr(Char p) {
        Connection conn = null;
        PreparedStatement tongnapCheckStmt = null;
        ResultSet balanceResult = null;
        try {
            conn = DbManager.getInstance().getConnection(DbManager.GAME);
            tongnapCheckStmt = conn.prepareStatement("SELECT `tongnap`, `nhanmocnap` FROM `users` WHERE `id` = ?");
            tongnapCheckStmt.setInt(1, p.user.id);
            balanceResult = tongnapCheckStmt.executeQuery();
            if (balanceResult.next()) {
                int tongnap = balanceResult.getInt("tongnap");
                int nhanmocnap = balanceResult.getInt("nhanmocnap");
                if (tongnap >= 2000000) {
                    if (nhanmocnap != 3) {
                        p.serverDialog("Không nhận được. Hãy nhận từ thấp đến cao.");
                    } else {
                        addGold(50000);
                        addYen(700000000);
                        PreparedStatement updateNhanmocnapStmt = conn.prepareStatement("UPDATE `users` SET `nhanmocnap` = 4 WHERE `id` = ?");
                        updateNhanmocnapStmt.setInt(1, p.user.id);
                        updateNhanmocnapStmt.executeUpdate();

                        p.serverDialog("Bạn đã nhập thành công một mục phẩm.");
                    }
                } else {
                    p.serverDialog("chưa nạp đủ để nhận mốc này.");
                }
            } else {
                p.serverDialog("Không tìm thấy thông tin người dùng.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (balanceResult != null) {
                    balanceResult.close();
                }
                if (tongnapCheckStmt != null) {
                    tongnapCheckStmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void mocnap5tr(Char p) {
        Connection conn = null;
        PreparedStatement tongnapCheckStmt = null;
        ResultSet balanceResult = null;
        try {
            conn = DbManager.getInstance().getConnection(DbManager.GAME);
            tongnapCheckStmt = conn.prepareStatement("SELECT `tongnap`, `nhanmocnap` FROM `users` WHERE `id` = ?");
            tongnapCheckStmt.setInt(1, p.user.id);
            balanceResult = tongnapCheckStmt.executeQuery();
            if (balanceResult.next()) {
                int tongnap = balanceResult.getInt("tongnap");
                int nhanmocnap = balanceResult.getInt("nhanmocnap");
                if (tongnap >= 5000000) {
                    if (getSlotNull() == 0) {
                       p.serverDialog("Hãy chừa 1 ô trống trong hành trang để nhận đồ.");
                        return;
                    }
                    if (nhanmocnap != 4) {
                        p.serverDialog("Không nhận được. Hãy nhận từ thấp đến cao.");
                    } else {
                        Item tree = ItemFactory.getInstance().newItem(385);
                        tree.setQuantity(1);
                        addItemToBag(tree);
                        addGold(100000);
                        PreparedStatement updateNhanmocnapStmt = conn.prepareStatement("UPDATE `users` SET `nhanmocnap` = 5 WHERE `id` = ?");
                        updateNhanmocnapStmt.setInt(1, p.user.id);
                        updateNhanmocnapStmt.executeUpdate();

                        p.serverDialog("Bạn đã nhập thành công một mục phẩm.");
                    }
                } else {
                    p.serverDialog("chưa nạp đủ để nhận mốc này.");
                }
            } else {
                p.serverDialog("Không tìm thấy thông tin người dùng.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (balanceResult != null) {
                    balanceResult.close();
                }
                if (tongnapCheckStmt != null) {
                    tongnapCheckStmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void mocnap10tr(Char p) {
        Connection conn = null;
        PreparedStatement tongnapCheckStmt = null;
        ResultSet balanceResult = null;
        try {
            conn = DbManager.getInstance().getConnection(DbManager.GAME);
            tongnapCheckStmt = conn.prepareStatement("SELECT `tongnap`, `nhanmocnap` FROM `users` WHERE `id` = ?");
            tongnapCheckStmt.setInt(1, p.user.id);
            balanceResult = tongnapCheckStmt.executeQuery();
            if (balanceResult.next()) {
                int tongnap = balanceResult.getInt("tongnap");
                int nhanmocnap = balanceResult.getInt("nhanmocnap");
                if (tongnap >= 10000000) {
                    if (getSlotNull() <= 5) {
                       p.serverDialog( "Hãy chừa 5 ô trống trong hành trang để nhận đồ.");
                        return;
                    }
                    if (nhanmocnap != 5) {
                        p.serverDialog("Không nhận được. Hãy nhận từ thấp đến cao.");
                    } else {
                        Item tree = ItemFactory.getInstance().newItem(385);
                        tree.setQuantity(1);
                        addItemToBag(tree);
                        Item tree1 = ItemFactory.getInstance().newItem(385);
                        tree1.setQuantity(1);
                        addItemToBag(tree1);
                        Item tree2 = ItemFactory.getInstance().newItem(385);
                        tree2.setQuantity(1);
                        addItemToBag(tree2);
                        addYen(1000000000);
                        Item unglong = ItemFactory.getInstance().newItem(ItemName.PET_UNG_LONG);
                        unglong.options.add(new ItemOption(73, 5000));
                        unglong.options.add(new ItemOption(94, 25));
                        unglong.options.add(new ItemOption(136, 30));
                        unglong.expire = System.currentTimeMillis() + ConstTime.DAY * 9999L;
                        unglong.setQuantity(1);
                        addItemToBag(unglong);
                        PreparedStatement updateNhanmocnapStmt = conn.prepareStatement("UPDATE `users` SET `nhanmocnap` = 6 WHERE `id` = ?");
                        updateNhanmocnapStmt.setInt(1, p.user.id);
                        updateNhanmocnapStmt.executeUpdate();

                        p.serverDialog("Bạn đã nhập thành công một mục phẩm.");
                    }
                } else {
                    p.serverDialog("chưa nạp đủ để nhận mốc này.");
                }
            } else {
                p.serverDialog("Không tìm thấy thông tin người dùng.");//đã sửa linh tinh
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (balanceResult != null) {
                    balanceResult.close();
                }
                if (tongnapCheckStmt != null) {
                    tongnapCheckStmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void doiluong10k(Char p) {
        Connection conn = null;
        PreparedStatement balanceCheckStmt = null;
        ResultSet balanceResult = null;
        try {
            conn = DbManager.getInstance().getConnection(DbManager.GAME);
            balanceCheckStmt = conn.prepareStatement("SELECT `balance`, `luong` FROM `users` WHERE `id` = ?");
            balanceCheckStmt.setInt(1, p.user.id);
            balanceResult = balanceCheckStmt.executeQuery();
            if (balanceResult.next()) {
                int oldBalance = balanceResult.getInt("balance");
                int oldLuong = balanceResult.getInt("luong");
                if (oldBalance >= 10000) {
                    int subtractedBalance = 10000;
                    int addedLuong = 4000;
                    PreparedStatement updateStmt = conn.prepareStatement("UPDATE `users` SET `balance` = `balance` - ?, `luong` = `luong` + ? WHERE `id` = ?");
                    updateStmt.setInt(1, subtractedBalance);
                    updateStmt.setInt(2, addedLuong);
                    updateStmt.setInt(3, p.user.id);
                    int rowsAffected = updateStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        int newBalance = oldBalance - subtractedBalance;
                        int newLuong = oldLuong + addedLuong;
                        p.addGold(addedLuong);
                        p.serverDialog("Đổi lương thành công bạn cộng [+4k lượng] .");
                        PreparedStatement historyStmt = conn.prepareStatement("INSERT INTO `history` (`id`, `old_balance`, `new_balance`, `subtracted_balance`, `old_luong`, `new_luong`, `added_luong`) VALUES (?, ?, ?, ?, ?, ?, ?)");
                        historyStmt.setInt(1, p.user.id);
                        historyStmt.setInt(2, oldBalance);
                        historyStmt.setInt(3, newBalance);
                        historyStmt.setInt(4, subtractedBalance);
                        historyStmt.setInt(5, oldLuong);
                        historyStmt.setInt(6, newLuong);
                        historyStmt.setInt(7, addedLuong);
                        historyStmt.executeUpdate();
                        historyStmt.close();
                    } else {
                        p.serverDialog("Không thể thực hiện việc đổi lương.");
                    }
                    updateStmt.close();
                } else {
                    p.serverDialog("Không đủ coin để thực hiện việc đổi lương.");
                }
            } else {
                p.serverDialog("Không tìm thấy thông tin người dùng.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (balanceResult != null) {
                    balanceResult.close();
                }
                if (balanceCheckStmt != null) {
                    balanceCheckStmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void doiluong20k(Char p) {
        Connection conn = null;
        PreparedStatement balanceCheckStmt = null;
        ResultSet balanceResult = null;
        try {
            conn = DbManager.getInstance().getConnection(DbManager.GAME);
            balanceCheckStmt = conn.prepareStatement("SELECT `balance`, `luong` FROM `users` WHERE `id` = ?");
            balanceCheckStmt.setInt(1, p.user.id);
            balanceResult = balanceCheckStmt.executeQuery();
            if (balanceResult.next()) {
                int oldBalance = balanceResult.getInt("balance");
                int oldLuong = balanceResult.getInt("luong");
                if (oldBalance >= 20000) {
                    int subtractedBalance = 20000;
                    int addedLuong = 9000;
                    PreparedStatement updateStmt = conn.prepareStatement("UPDATE `users` SET `balance` = `balance` - ?, `luong` = `luong` + ? WHERE `id` = ?");
                    updateStmt.setInt(1, subtractedBalance);
                    updateStmt.setInt(2, addedLuong);
                    updateStmt.setInt(3, p.user.id);
                    int rowsAffected = updateStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        int newBalance = oldBalance - subtractedBalance;
                        int newLuong = oldLuong + addedLuong;
                        p.addGold(addedLuong);
                        p.serverDialog("Đổi lương thành công bạn cộng [+9k lượng] .");
                        PreparedStatement historyStmt = conn.prepareStatement("INSERT INTO `history` (`id`, `old_balance`, `new_balance`, `subtracted_balance`, `old_luong`, `new_luong`, `added_luong`) VALUES (?, ?, ?, ?, ?, ?, ?)");
                        historyStmt.setInt(1, p.user.id);
                        historyStmt.setInt(2, oldBalance);
                        historyStmt.setInt(3, newBalance);
                        historyStmt.setInt(4, subtractedBalance);
                        historyStmt.setInt(5, oldLuong);
                        historyStmt.setInt(6, newLuong);
                        historyStmt.setInt(7, addedLuong);
                        historyStmt.executeUpdate();
                        historyStmt.close();
                    } else {
                        p.serverDialog("Không thể thực hiện việc đổi lương.");
                    }
                    updateStmt.close();
                } else {
                    p.serverDialog("Không đủ coin để thực hiện việc đổi lương.");
                }
            } else {
                p.serverDialog("Không tìm thấy thông tin người dùng.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (balanceResult != null) {
                    balanceResult.close();
                }
                if (balanceCheckStmt != null) {
                    balanceCheckStmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void doiluong50k(Char p) {
        Connection conn = null;
        PreparedStatement balanceCheckStmt = null;
        ResultSet balanceResult = null;
        try {
            conn = DbManager.getInstance().getConnection(DbManager.GAME);
            balanceCheckStmt = conn.prepareStatement("SELECT `balance`, `luong` FROM `users` WHERE `id` = ?");
            balanceCheckStmt.setInt(1, p.user.id);
            balanceResult = balanceCheckStmt.executeQuery();
            if (balanceResult.next()) {
                int oldBalance = balanceResult.getInt("balance");
                int oldLuong = balanceResult.getInt("luong");
                if (oldBalance >= 50000) {
                    int subtractedBalance = 50000;
                    int addedLuong = 26000;
                    PreparedStatement updateStmt = conn.prepareStatement("UPDATE `users` SET `balance` = `balance` - ?, `luong` = `luong` + ? WHERE `id` = ?");
                    updateStmt.setInt(1, subtractedBalance);
                    updateStmt.setInt(2, addedLuong);
                    updateStmt.setInt(3, p.user.id);
                    int rowsAffected = updateStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        int newBalance = oldBalance - subtractedBalance;
                        int newLuong = oldLuong + addedLuong;
                        p.addGold(addedLuong);
                        p.serverDialog("Đổi lương thành công bạn cộng [+26k lượng] .");
                        PreparedStatement historyStmt = conn.prepareStatement("INSERT INTO `history` (`id`, `old_balance`, `new_balance`, `subtracted_balance`, `old_luong`, `new_luong`, `added_luong`) VALUES (?, ?, ?, ?, ?, ?, ?)");
                        historyStmt.setInt(1, p.user.id);
                        historyStmt.setInt(2, oldBalance);
                        historyStmt.setInt(3, newBalance);
                        historyStmt.setInt(4, subtractedBalance);
                        historyStmt.setInt(5, oldLuong);
                        historyStmt.setInt(6, newLuong);
                        historyStmt.setInt(7, addedLuong);
                        historyStmt.executeUpdate();
                        historyStmt.close();
                    } else {
                        p.serverDialog("Không thể thực hiện việc đổi lương.");
                    }
                    updateStmt.close();
                } else {
                    p.serverDialog("Không đủ coin để thực hiện việc đổi lương.");
                }
            } else {
                p.serverDialog("Không tìm thấy thông tin người dùng.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (balanceResult != null) {
                    balanceResult.close();
                }
                if (balanceCheckStmt != null) {
                    balanceCheckStmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void doiluong100k(Char p) {
        Connection conn = null;
        PreparedStatement balanceCheckStmt = null;
        ResultSet balanceResult = null;
        try {
            conn = DbManager.getInstance().getConnection(DbManager.GAME);
            balanceCheckStmt = conn.prepareStatement("SELECT `balance`, `luong` FROM `users` WHERE `id` = ?");
            balanceCheckStmt.setInt(1, p.user.id);
            balanceResult = balanceCheckStmt.executeQuery();
            if (balanceResult.next()) {
                int oldBalance = balanceResult.getInt("balance");
                int oldLuong = balanceResult.getInt("luong");
                if (oldBalance >= 100000) {
                    int subtractedBalance = 100000;
                    int addedLuong = 55000;
                    PreparedStatement updateStmt = conn.prepareStatement("UPDATE `users` SET `balance` = `balance` - ?, `luong` = `luong` + ? WHERE `id` = ?");
                    updateStmt.setInt(1, subtractedBalance);
                    updateStmt.setInt(2, addedLuong);
                    updateStmt.setInt(3, p.user.id);
                    int rowsAffected = updateStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        int newBalance = oldBalance - subtractedBalance;
                        int newLuong = oldLuong + addedLuong;
                        p.addGold(addedLuong);
                        p.serverDialog("Đổi lương thành công bạn cộng [+55k lượng] .");
                        PreparedStatement historyStmt = conn.prepareStatement("INSERT INTO `history` (`id`, `old_balance`, `new_balance`, `subtracted_balance`, `old_luong`, `new_luong`, `added_luong`) VALUES (?, ?, ?, ?, ?, ?, ?)");
                        historyStmt.setInt(1, p.user.id);
                        historyStmt.setInt(2, oldBalance);
                        historyStmt.setInt(3, newBalance);
                        historyStmt.setInt(4, subtractedBalance);
                        historyStmt.setInt(5, oldLuong);
                        historyStmt.setInt(6, newLuong);
                        historyStmt.setInt(7, addedLuong);
                        historyStmt.executeUpdate();
                        historyStmt.close();
                    } else {
                        p.serverDialog("Không thể thực hiện việc đổi lương.");
                    }
                    updateStmt.close();
                } else {
                    p.serverDialog("Không đủ coin để thực hiện việc đổi lương.");
                }
            } else {
                p.serverDialog("Không tìm thấy thông tin người dùng.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (balanceResult != null) {
                    balanceResult.close();
                }
                if (balanceCheckStmt != null) {
                    balanceCheckStmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void doiluong200k(Char p) {
        Connection conn = null;
        PreparedStatement balanceCheckStmt = null;
        ResultSet balanceResult = null;
        try {
            conn = DbManager.getInstance().getConnection(DbManager.GAME);
            balanceCheckStmt = conn.prepareStatement("SELECT `balance`, `luong` FROM `users` WHERE `id` = ?");
            balanceCheckStmt.setInt(1, p.user.id);
            balanceResult = balanceCheckStmt.executeQuery();
            if (balanceResult.next()) {
                int oldBalance = balanceResult.getInt("balance");
                int oldLuong = balanceResult.getInt("luong");
                if (oldBalance >= 200000) {
                    int subtractedBalance = 200000;
                    int addedLuong = 115000;
                    PreparedStatement updateStmt = conn.prepareStatement("UPDATE `users` SET `balance` = `balance` - ?, `luong` = `luong` + ? WHERE `id` = ?");
                    updateStmt.setInt(1, subtractedBalance);
                    updateStmt.setInt(2, addedLuong);
                    updateStmt.setInt(3, p.user.id);
                    int rowsAffected = updateStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        int newBalance = oldBalance - subtractedBalance;
                        int newLuong = oldLuong + addedLuong;
                        p.addGold(addedLuong);
                        p.serverDialog("Đổi lương thành công bạn cộng [+115k lượng] .");
                        PreparedStatement historyStmt = conn.prepareStatement("INSERT INTO `history` (`id`, `old_balance`, `new_balance`, `subtracted_balance`, `old_luong`, `new_luong`, `added_luong`) VALUES (?, ?, ?, ?, ?, ?, ?)");
                        historyStmt.setInt(1, p.user.id);
                        historyStmt.setInt(2, oldBalance);
                        historyStmt.setInt(3, newBalance);
                        historyStmt.setInt(4, subtractedBalance);
                        historyStmt.setInt(5, oldLuong);
                        historyStmt.setInt(6, newLuong);
                        historyStmt.setInt(7, addedLuong);
                        historyStmt.executeUpdate();
                        historyStmt.close();
                    } else {
                        p.serverDialog("Không thể thực hiện việc đổi lương.");
                    }
                    updateStmt.close();
                } else {
                    p.serverDialog("Không đủ coin để thực hiện việc đổi lương.");
                }
            } else {
                p.serverDialog("Không tìm thấy thông tin người dùng.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (balanceResult != null) {
                    balanceResult.close();
                }
                if (balanceCheckStmt != null) {
                    balanceCheckStmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void doiluong500k(Char p) {
        Connection conn = null;
        PreparedStatement balanceCheckStmt = null;
        ResultSet balanceResult = null;
        try {
            conn = DbManager.getInstance().getConnection(DbManager.GAME);
            balanceCheckStmt = conn.prepareStatement("SELECT `balance`, `luong` FROM `users` WHERE `id` = ?");
            balanceCheckStmt.setInt(1, p.user.id);
            balanceResult = balanceCheckStmt.executeQuery();
            if (balanceResult.next()) {
                int oldBalance = balanceResult.getInt("balance");
                int oldLuong = balanceResult.getInt("luong");
                if (oldBalance >= 500000) {
                    int subtractedBalance = 500000;
                    int addedLuong = 290000;
                    PreparedStatement updateStmt = conn.prepareStatement("UPDATE `users` SET `balance` = `balance` - ?, `luong` = `luong` + ? WHERE `id` = ?");
                    updateStmt.setInt(1, subtractedBalance);
                    updateStmt.setInt(2, addedLuong);
                    updateStmt.setInt(3, p.user.id);
                    int rowsAffected = updateStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        int newBalance = oldBalance - subtractedBalance;
                        int newLuong = oldLuong + addedLuong;
                        p.addGold(addedLuong);
                        p.serverDialog("Đổi lương thành công bạn cộng [+290k lượng] .");
                        PreparedStatement historyStmt = conn.prepareStatement("INSERT INTO `history` (`id`, `old_balance`, `new_balance`, `subtracted_balance`, `old_luong`, `new_luong`, `added_luong`) VALUES (?, ?, ?, ?, ?, ?, ?)");
                        historyStmt.setInt(1, p.user.id);
                        historyStmt.setInt(2, oldBalance);
                        historyStmt.setInt(3, newBalance);
                        historyStmt.setInt(4, subtractedBalance);
                        historyStmt.setInt(5, oldLuong);
                        historyStmt.setInt(6, newLuong);
                        historyStmt.setInt(7, addedLuong);
                        historyStmt.executeUpdate();
                        historyStmt.close();
                    } else {
                        p.serverDialog("Không thể thực hiện việc đổi lương.");
                    }
                    updateStmt.close();
                } else {
                    p.serverDialog("Không đủ coin để thực hiện việc đổi lương.");
                }
            } else {
                p.serverDialog("Không tìm thấy thông tin người dùng.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (balanceResult != null) {
                    balanceResult.close();
                }
                if (balanceCheckStmt != null) {
                    balanceCheckStmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void doiluong1000k(Char p) {
        Connection conn = null;
        PreparedStatement balanceCheckStmt = null;
        ResultSet balanceResult = null;
        try {
            conn = DbManager.getInstance().getConnection(DbManager.GAME);
            balanceCheckStmt = conn.prepareStatement("SELECT `balance`, `luong` FROM `users` WHERE `id` = ?");
            balanceCheckStmt.setInt(1, p.user.id);
            balanceResult = balanceCheckStmt.executeQuery();
            if (balanceResult.next()) {
                int oldBalance = balanceResult.getInt("balance");
                int oldLuong = balanceResult.getInt("luong");
                if (oldBalance >= 1000000) {
                    int subtractedBalance = 1000000;
                    int addedLuong = 600000;
                    PreparedStatement updateStmt = conn.prepareStatement("UPDATE `users` SET `balance` = `balance` - ?, `luong` = `luong` + ? WHERE `id` = ?");
                    updateStmt.setInt(1, subtractedBalance);
                    updateStmt.setInt(2, addedLuong);
                    updateStmt.setInt(3, p.user.id);
                    int rowsAffected = updateStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        int newBalance = oldBalance - subtractedBalance;
                        int newLuong = oldLuong + addedLuong;

                        p.addGold(addedLuong);
                        p.serverDialog("Đổi lương thành công bạn cộng [+600k lượng] .");
                        PreparedStatement historyStmt = conn.prepareStatement("INSERT INTO `history` (`id`, `old_balance`, `new_balance`, `subtracted_balance`, `old_luong`, `new_luong`, `added_luong`) VALUES (?, ?, ?, ?, ?, ?, ?)");
                        historyStmt.setInt(1, p.user.id);
                        historyStmt.setInt(2, oldBalance);
                        historyStmt.setInt(3, newBalance);
                        historyStmt.setInt(4, subtractedBalance);
                        historyStmt.setInt(5, oldLuong);
                        historyStmt.setInt(6, newLuong);
                        historyStmt.setInt(7, addedLuong);
                        historyStmt.executeUpdate();
                        historyStmt.close();
                    } else {
                        p.serverDialog("Không thể thực hiện việc đổi lương.");
                    }
                    updateStmt.close();
                } else {
                    p.serverDialog("Không đủ coin để thực hiện việc đổi lương.");
                }
            } else {
                p.serverDialog("Không tìm thấy thông tin người dùng.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (balanceResult != null) {
                    balanceResult.close();
                }
                if (balanceCheckStmt != null) {
                    balanceCheckStmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void DeleteClone(Char p) {
    Connection conn = null;
    PreparedStatement balanceCheckStmt = null;
    try {
        conn = DbManager.getInstance().getConnection(DbManager.GAME);
        balanceCheckStmt = conn.prepareStatement("DELETE FROM `users` WHERE `status` = 0 AND `activated` = 0 AND `kh` = 0;");
        //balanceCheckStmt.setInt(1, p.user.id);
        int rowsDeleted = balanceCheckStmt.executeUpdate();

        // Check if any rows were deleted
        if (rowsDeleted > 0) {
            p.serverDialog("Dọn clone thành công!.");
        } else {
            p.serverDialog("Không tìm thấy clone nào để dọn.");
        }

        balanceCheckStmt.close();
    } catch (SQLException ex) {
        Log.error("DeleteClone err: " + ex.getMessage(), ex);

        // Return a meaningful message to the user
        p.serverDialog("Có lỗi xảy ra khi dọn clone!.");
    } finally {
        // Close the connection, even if an exception occurred
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(Char.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
    public void changeName() {
        if(this.clan == null){
        int index = getIndexItemByIdInBag(ItemName.THE_DOI_TEN);
        InputDialog input = new InputDialog(CMDInputDialog.EXECUTE, "Tên mới", () -> {
            try {
               String newname = this.input.getText();
               
                if (newname.equals("") || newname.isEmpty()) {
                    serverDialog("Tên không được để trống.");
                    return;
                }Pattern p = Pattern.compile("^[a-z0-9]+$");
                  Matcher m1 = p.matcher(newname);
                 if (!m1.find()) {
                  service.serverDialog("Tên nhân vật không được chứa ký tự đặc biệt!");
                  return;
                }if (newname.length() < 5 || newname.length() > 15) {
                service.serverDialog("Tên tài khoản chỉ cho phép từ 5 đến 15 ký tự!");
                return;
            }if (newname.equals("ADMIN") ||newname.equals("admin") ) {
                    serverDialog("không thể sử dụng tên này.");
                    return;
                }
                setConfirmPopup(new ConfirmPopup(CMDConfirmPopup.CONFIRM,
                        String.format("Bạn có chắc chắn muốn đổi tên thành %s không?", newname), () -> {
                            try {
                                
                                PreparedStatement stmt = DbManager.getInstance().getConnection(DbManager.UPDATE)
                                        .prepareStatement(SQLStatement.CHECK_NAME, ResultSet.TYPE_SCROLL_SENSITIVE,
                                                ResultSet.CONCUR_READ_ONLY);
                                stmt.setString(1, newname);
                                ResultSet data = stmt.executeQuery();
                                if (data.first()) {
                                    serverDialog("Tên đã tồn tại, vui lòng chọn một tên khác.");
                                    return;
                                }

                                PreparedStatement stmt2 = DbManager.getInstance().getConnection(DbManager.UPDATE)
                                        .prepareStatement(SQLStatement.UPDATE_NAME);
                                try {
                                   
                                    stmt2.setString(1, newname);
                                    stmt2.setInt(2, this.id);
                                    stmt2.executeUpdate();
                                } finally {
                                    stmt2.close();
                                }
                               // Item item = ItemFactory.getInstance().newItem(ItemName.THE_DOI_TEN);
                                removeItem(index, 1, true);
                                serverDialog(String.format("Thay đổi thành công, tên mới của bạn là: %s.",newname));
                                service.serverDialog("Tự Động Thoát Sau 1s.");
                                 int TimeSeconds = 3 ;
                                    while (TimeSeconds > 0) {
                                            TimeSeconds--;
                                try {
                                    Thread.sleep(500L);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(Char.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                            }
                                if (!isCleaned) {
                                    user.session.disconnect();
                                } 
                                
                                getService().updateInfoMe();
                                service.serverDialog("Vui lòng thoát game để hoàn thành đổi tên.");
                            } catch (Exception e) {
                                e.printStackTrace();
                                serverDialog(String.format("Thay đổi thất bại.", newname));
                            }
                        }));
                getService().openUIConfirmID();
            } catch (Exception e) {
                getService().serverDialog(e.getMessage());
            }
        });
        setInput(input);
        getService().showInputDialog();
        }else{
         serverDialog("Bạn Phải Thoát Gia Tộc Trước Khi Tiến Hành Đổi Tên.");   
        }
        return;
    }



    public void npcruby() {
        Char _char = zone.findCharById(id);
        menus.add(new Menu(CMDMenu.EXECUTE, "Nhận chỉ số", () -> {
            if (this.fashion[14] == null) {
                getService().npcChat(NpcName.ruby,
                        "Ngươi cần đeo ruby thần trên người đã rồi luyện nhé .");
                return;
            }
            if (this.fashion[14] == null) {
                getService().npcChat(NpcName.ruby,
                        "Ngươi cần đeo bí kíp ruby thần trên người đã rồi luyện nhé .");
                return;
            }
            Item item1 = this.fashion[14];
            if (item1.upgrade >= 1) {
                getService()
                        .serverMessage(String.format("%s đã nâng cấp, không thể luyện chỉ số", item1.template.name));
                return;
            }
            if (this.fashion[14] != null) {
                if (this.user.gold >= 1000) {
                    if (getSlotNull() == 0) {
                        getService().npcChat(NpcName.ruby, "Hãy chừa 1 ô trống trong hành trang để nhận bí kíp.");
                        return;
                    }
                    addGold(-1000);
                    int itemId = this.fashion[14].id;
                    getService().deleteItemBody(14);
                    this.fashion[14] = null;
                    Item item = ItemFactory.getInstance().newItem(itemId);
                    item.isLock = true;
                    int random = (int)NinjaUtils.nextInt(1, 5);
                    ArrayList<ItemOption> options = new ArrayList<>();
                    options.add(new ItemOption(81, (int)NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(82, (int)NinjaUtils.nextInt(500, 1500)));
                    options.add(new ItemOption(83, (int)NinjaUtils.nextInt(500, 1500)));
                    options.add(new ItemOption(84, (int)NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(86, (int)NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(87, (int)NinjaUtils.nextInt(100, 800)));
                    options.add(new ItemOption(88, (int)NinjaUtils.nextInt(100, 1000)));
                    options.add(new ItemOption(89, (int)NinjaUtils.nextInt(100, 1000)));
                    options.add(new ItemOption(90, (int)NinjaUtils.nextInt(100, 1000)));
                    options.add(new ItemOption(91, (int)NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(92, (int)NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(95, (int)NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(96, (int)NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(97, (int)NinjaUtils.nextInt(10, 20)));
                    options.add(new ItemOption(98, (int)NinjaUtils.nextInt(5, 10)));
                    options.add(new ItemOption(99, (int)NinjaUtils.nextInt(100, 300)));
//                  exp
                    options.add(new ItemOption(100, (int)NinjaUtils.nextInt(5, 10)));

                    item.options.add(new ItemOption(85, 0));
                    for (int i = 0; i < random; i++) {
                        int index = 0;
                        if (options.get(options.size() - 1).optionTemplate.id == 100) {
                            if ((int)NinjaUtils.nextInt(100) < 30) {
                                index = options.size() - 1;
                            }
                        } else {
                            index = (int)NinjaUtils.nextInt(options.size());
                        }
                        ItemOption iop = options.get(index);
                        item.options.add(iop);
                        options.remove(index);
                    }
                    addItemToBag(item);
                    setFashion();
                    setAbility();
                    String text = "";
                    if (random == 1 || random == 2) {
                        text = "Ta chỉ giúp được cho ngươi đến thế thôi, ta xin lỗi.";
                    } else if (random == 3 || random == 4) {
                        text = "Không tệ ngươi xem có ổn không.";
                    } else {
                        text = "Khá mạnh đó, ngươi thấy ta làm tốt không?";
                    }
                    getService().npcChat(NpcName.ruby, text);
                } else {
                    getService().npcChat(NpcName.ruby, "Hãy đưa ta 1.000 lượng ta mới giúp ngươi.");
                }
            } else {
                getService().npcChat(NpcName.ruby,
                        "Ngươi cần đeo ruby thần trên người đã rồi luyện nhé .");
            }
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Nói chuyện", () -> {
            String talk = (String) NinjaUtils.randomObject(
                    "Ta có thể giúp ngươi làm tăng sức mạnh cho ruby, ngươi chỉ cần trả cho ta ít ngân lượng.",
                    "Nhanh lên kẻo bị phát hiện.", "Chắc chắn ngươi sẽ cần ta giúp.");
            getService().npcChat(NpcName.ruby, talk);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Nâng cấp ruby", () -> {
            Item item = this.fashion[14];
            if (item != null) {
                int cap = 0;
                for (ItemOption option : item.options) {
                    if (option.optionTemplate.id == 85) {
                        cap = option.param;
                        break;
                    }
                }
                if (cap >= 16) {
                    getService().npcChat(NpcName.ruby,
                            "Bí kíp của ngươi đã quá mạnh, ta không thể giúp được ngươi.");
                    return;
                }
                int fee = (cap + 5) * 2;
                int percent = GameData.UP_BI_KIP[cap];
                setConfirmPopup(new ConfirmPopup(CMDConfirmPopup.NANG_rb, String.format(
                        "Ngươi có muốn nâng cấp ruby đang sử dụng lên cấp %d không? với phí %d lượng - Tỉ lệ thành công: %s.",
                        (cap + 1), fee, percent + "%")));
                getService().openUIConfirmID();
            } else {
                getService().npcChat(NpcName.ruby,
                        "Ngươi hãy sử dụng ruby ta sẽ giúp ngươi làm thay đổi sức mạnh.");
            }
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Nâng Rương Đồ", () -> {
            if (this.user.kh == 0) {
                serverMessage("Bạn phải kích hoạt để dùng tính năng này");
                return;
            }
            menus.clear();
            menus.add(new Menu(CMDMenu.EXECUTE, "Nâng lên 36 ô", () -> {
            if (this.getIndexItemByIdInBag(ItemName.RUONG_DO_CAP_1) < 1) {
                 getService().npcChat(NpcName.ruby, "Cần có vật phẩm rương cấp 1 để nâng!");
            }else if(this.numberCellBox > 30){
              getService().npcChat(NpcName.ruby, "Bạn đã nâng rương cấp 1 hoặc đã hơn 30 số ô rương!");
              return;
            }else{
            this.numberCellBox += 6;
            int keyIndex = this.getIndexItemByIdInBag(ItemName.RUONG_DO_CAP_1);
            removeItem(keyIndex, 1, true);
            getService().npcChat(NpcName.ruby, "Nâng thành công nhớ thoát game để lưu!");
                 }
                }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Nâng lên 42 ô", () -> {
            if (this.getIndexItemByIdInBag(ItemName.RUONG_DO_CAP_2) < 1) {
                 getService().npcChat(NpcName.ruby, "Cần có vật phẩm rương cấp 1 để nâng!");
            }else if(this.numberCellBox < 31){
              getService().npcChat(NpcName.ruby, "Yêu cầng nâng rương cấp 1 trước!");
              return;
            }else if(this.numberCellBox > 36){
              getService().npcChat(NpcName.ruby, "Bạn đã nâng rương cấp 2 hoặc đã hơn 36 số ô rương!");
              return;
            }else{
            this.numberCellBox += 6;
            int keyIndex = this.getIndexItemByIdInBag(ItemName.RUONG_DO_CAP_2);
            removeItem(keyIndex, 1, true);
            getService().npcChat(NpcName.ruby, "Nâng thành công nhớ thoát game để lưu!");
                 }
            }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Nâng lên 54 ô", () -> {
            if (this.getIndexItemByIdInBag(ItemName.RUONG_DO_CAP_3) < 1) {
                 getService().npcChat(NpcName.ruby, "Cần có vật phẩm rương cấp 1 để nâng!");
            }else if(this.numberCellBox < 35){
              getService().npcChat(NpcName.ruby, "Hãy nâng rương cấp 2 trước !");
            }else if(this.numberCellBox > 53){
              getService().npcChat(NpcName.ruby, "Bạn đã nâng rương cấp 3 hoặc đã hơn 54 số ô rương!");
              return;
            }else{
            this.numberCellBox += 12;
            int keyIndex = this.getIndexItemByIdInBag(ItemName.RUONG_DO_CAP_3);
            removeItem(keyIndex, 1, true);
            getService().npcChat(NpcName.ruby, "Nâng thành công nhớ thoát game để lưu!");
                 }
        }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Nâng lên 78 ô", () -> {
            if (this.getIndexItemByIdInBag(ItemName.RUONG_DO_CAP_4) < 1) {
                 getService().npcChat(NpcName.ruby, "Cần có vật phẩm rương cấp 1 để nâng!");
            }else if(this.numberCellBox < 53){
              getService().npcChat(NpcName.ruby, "Hãy nâng rương cấp 3 trước!");
            }else if(this.numberCellBox > 77){
              getService().npcChat(NpcName.ruby, "Bạn đã nâng rương cấp 4 hoặc đã hơn 78 số ô rương!");
              return;
            }else{
            this.numberCellBox += 24;
            int keyIndex = this.getIndexItemByIdInBag(ItemName.RUONG_DO_CAP_4);
            removeItem(keyIndex, 1, true);
            getService().npcChat(NpcName.ruby, "Nâng thành công nhớ thoát game để lưu!");
                 }
        }));
            menus.add(new Menu(CMDMenu.EXECUTE, "Nâng lên 120 ô", () -> {
            if (this.getIndexItemByIdInBag(ItemName.RUONG_DO_CAP_5) < 1) {
                 getService().npcChat(NpcName.ruby, "Cần có vật phẩm rương cấp 1 để nâng!");
            }else if(this.numberCellBox < 77){
              getService().npcChat(NpcName.ruby, "Hãy nâng rương cấp 4 trước !");
            }else if(this.numberCellBox > 119){
              getService().npcChat(NpcName.ruby, "Bạn đã nâng rương cấp 5 hoặc đã hơn 120 số ô rương!");
              return;
            }else{
            this.numberCellBox += 42;
            int keyIndex = this.getIndexItemByIdInBag(ItemName.RUONG_DO_CAP_5);
            removeItem(keyIndex, 1, true);
            getService().npcChat(NpcName.ruby, "Nâng thành công nhớ thoát game để lưu!");
                 }
        }));
            getService().openUIMenu();
            return;
            }));
     /*  menus.add(new Menu(CMDMenu.EXECUTE, "Chuyển Sinh ", () -> {
            if (this.level < 150) {
                getService().npcChat(NpcName.ruby, "Ngươi còn chưa đạt cấp 150");
                return;
            }
            if(this.user.gold < 100000){
                getService().npcChat(NpcName.ruby, "Ngươi còn chưa có đủ 100k lượng !");
                return;
            }
                this.user.gold -= 100000;
                _char.potential = _char.potential;
                _char.potentialPoint = _char.potentialPoint;
                _char.hp = _char.hp;
                _char.mp = _char.mp;
                _char.exp = 1; 
                //getService().updateInfoMe();
                getService().npcChat(NpcName.ruby, "Chuyển sinh thành công +11960 sức mạnh. Tự động thoát sau 5 giây");
                _char.potentialPoint += 1190;   
                    int TimeSeconds = 3 ;
                    while (TimeSeconds > 0) {
                            TimeSeconds--;
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Char.class.getName()).log(Level.SEVERE, null, ex);
                }
                            }
                if (!isCleaned) {
                    user.session.disconnect();
                }
              return;
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Hoàn thành nhiệm vụ từ level 9 đến level 36", () -> {
                if (_char.taskId < 25){
                if (_char.user.gold >= 50000) {
                    getService().npcChat(NpcName.ruby, "Ta đã hoàn thành giúp con rồi đó!");
                    _char.addGold(-50000);
                    _char.taskId = 25;
                    _char.taskMain = null;
                    //finishTask(true);
                   //getService().updateInfoMe();
                   int TimeSeconds = 3 ;
                    while (TimeSeconds > 0) {
                            TimeSeconds--;
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Char.class.getName()).log(Level.SEVERE, null, ex);
                }
                            }
                    
                } else {
                    serverDialog("Không đủ lượng!");
                }
                if (!isCleaned) {
                    user.session.disconnect();
                }
                return;
                }else{
           getService().npcChat(NpcName.ruby, "Chưa có nhiệm vụ hoặc đã làm xong !"); 
        }
            }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Hoàn thành nhiệm vụ đến level 42", () -> {
                if (_char.taskId <43){
                if (user.gold >= 100000) {
                    getService().npcChat(NpcName.ruby, "Ta đã hoàn thành giúp con rồi đó!");
                    _char.addGold(-100000);
                    _char.taskId = 43;
                    _char.taskMain = null;
                    //finishTask(true);
                    int TimeSeconds = 3 ;
                    while (TimeSeconds > 0) {
                            TimeSeconds--;
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Char.class.getName()).log(Level.SEVERE, null, ex);
                }
                            }
                   //getService().updateInfoMe();
                } else {
                    serverDialog("Không đủ lượng!");
                }
                if (!isCleaned) {
                    user.session.disconnect();
                }
                return;
                }else{
           getService().npcChat(NpcName.ruby, "Chưa có nhiệm vụ hoặc đã làm xong !"); 
        }
            }));
        return; */
    }
    public void NPCHuongDan(){
    Char _char = zone.findCharById(id);
    menus.add(new Menu(CMDMenu.EXECUTE, "Hướng dẫn về làm nhiệm vụ", () -> {
                String guide = "- Hoàn Thành Nhiệm Vụ" + "\n";
                guide += " Trước tiên bạn cần có lượng để hoàn thành.\n Nếu không có lượng thì bạn sẽ phải làm từng bước theo chỉ dẫn của game" + "\n";
                guide += "  + Để hoàn thành nhiệm vụ cần nhận nhiệm vụ" + "\n";
                guide += "   và vào npcadmin ấn vào hoàn thành sẽ mất 1k lượng." + "\n";
                guide += "- Nhập Gifcode" + "\n";
                guide += "  + Ấn vào mã quà tặng ở npcadmin gifcode gồm có:" + "\n";
                guide += "   tanthu" + "\n";
                getService().showAlert("Hướng dẫn", guide);
            }));
    menus.add(new Menu(CMDMenu.EXECUTE, "Hướng dẫn về chức năng Game", () -> {
                String guide = "- CHỨC NĂNG GAME" + "\n";
                guide += "SEVER sẽ bảo trì vào 00h00 phút cho đến 00h15 sẽ mở khởi động cho ae chơi!" + "\n";
                guide += "Về chức năng game thì gồm có như sau:" + "\n";
                guide += "Nâng cấp ấn tộc, bí kíp, khai hóa, khai lưỡng, chiến trường, chiến trường kẹo, \n vân vân tự tìm hiểu cho vui" + "\n";
                guide += "Về vật phẩm sẽ kiếm ở sự kiện hoặc đánh quái các khu vực có quái vật \n hoặc kiếm nguyên liệu ở "
                      + "\nVùng đất ma quỷ bao gồm: exp thú,phân thân lệnh, các trang bị thú cưỡi, chuyển tinh thạch, tử tinh thạch,..."
                      + "\nĐánh BOSS sẽ rơi ra sách võ công 10x,8x,9x các vật phẩm nâng cấp. "
                      + "\nLàng cổ gồm các vật phẩm: trang bị xe, thú , vật phẩm nâng cấp bí kíp "
                      + "\nLàng truyền thuyết: mảnh sách 12x, vật phẩm nâng bí kíp, nâng trang bị"
                      + "\nBoss làng truyền thuyết sẽ đánh ra các vật phẩm như vật phẩm nâng khai hóa, bí kíp, xe máy víp, rương bát bảo, bạch ngân" + "\n";
                getService().showAlert("Hướng dẫn", guide);
            }));
    menus.add(new Menu(CMDMenu.EXECUTE, "Hướng dẫn về cách chơi và Phiên bản", () -> {
                String guide = "- CÁCH CHƠI" + "\n";
                guide += "Tải các phiên bản trên web về cài thui!"+ "\n";
                getService().showAlert("Hướng dẫn", guide);
            }));
    }
    
     public void NPCThuNuoi() {
         Char _char = zone.findCharById(id);
         
        menus.add(new Menu(CMDMenu.EXECUTE, "Luyện thú nuôi", () -> {
            if (this.equipment[ItemTemplate.TYPE_THUNUOI] != null && (this.equipment[ItemTemplate.TYPE_THUNUOI].id < 924 
                    || this.equipment[ItemTemplate.TYPE_THUNUOI].id > 1047)) {
                if(this.equipment[ItemTemplate.TYPE_THUNUOI].upgrade > 0){
                 getService().npcChat(NpcName.NPCThuNuoi, "Không thể luyện lại thú nuôi.");
                        return;   
                }else if(this.equipment[ItemTemplate.TYPE_THUNUOI].expire > 0){
                  getService().npcChat(NpcName.NPCThuNuoi, "Không thể luyện thú nuôi có hạn!.");
                        return;  
                }
                if (this.user.gold >= 10000) {
                    if (getSlotNull() == 0) {
                        getService().npcChat(NpcName.NPCThuNuoi, "Hãy chừa 1 ô trống, 10k lượng trong hành trang để nhận lại thú nuôi.");
                        return;
                    }
                    addGold(-10000);
                    int itemId = this.equipment[ItemTemplate.TYPE_THUNUOI].id;
                    getService().deleteItemBody(ItemTemplate.TYPE_THUNUOI);
                    this.equipment[ItemTemplate.TYPE_THUNUOI] = null;
                    Item item = ItemFactory.getInstance().newItem(itemId);
                    item.isLock = true;
                    //item.options.clear();
                    int random = (int)NinjaUtils.nextInt(4, 6);
                    ArrayList<ItemOption> options = new ArrayList<>();
                    options.add(new ItemOption(73, (int)NinjaUtils.nextInt(1000, 2500)));
                    options.add(new ItemOption(14, (int)NinjaUtils.nextInt(5, 20)));
                    options.add(new ItemOption(94, (int)NinjaUtils.nextInt(5, 30)));
                    options.add(new ItemOption(6, (int)NinjaUtils.nextInt(1000, 2500)));
                    options.add(new ItemOption(88, (int)NinjaUtils.nextInt(100, 500)));
                    options.add(new ItemOption(89, (int)NinjaUtils.nextInt(100, 500)));
                    options.add(new ItemOption(90, (int)NinjaUtils.nextInt(100, 500)));
                    options.add(new ItemOption(10, (int)NinjaUtils.nextInt(5, 50)));
                    options.add(new ItemOption(2, (int)NinjaUtils.nextInt(10, 100)));
                    options.add(new ItemOption(3, (int)NinjaUtils.nextInt(10, 100)));
                    options.add(new ItemOption(4, (int)NinjaUtils.nextInt(10, 100)));
                    options.add(new ItemOption(84, (int)NinjaUtils.nextInt(5, 50)));
                    item.options.add(new ItemOption(85, 0));
                    for (int i = 0; i < random; i++) {
                        int index = 0;
                        if (options.get(options.size() - 1).optionTemplate.id == 100) {
                            if ((int)NinjaUtils.nextInt(100) < 30) {
                                index = options.size() - 1;
                            }
                        } else {
                            index = (int)NinjaUtils.nextInt(options.size());
                        }
                        ItemOption iop = options.get(index);
                        item.options.add(iop);
                        options.remove(index);
                    }
                    addItemToBag(item);
                    setFashion();
                    setAbility();
                    String text = "";
                    if (random == 1 || random == 2) {
                        text = "Ta chỉ giúp được cho ngươi đến thế thôi, ta xin lỗi.";
                    } else if (random == 3 || random == 4) {
                        text = "Không tệ ngươi xem có ổn không.";
                    } else {
                        text = "Khá mạnh đó, ngươi thấy ta làm tốt không?";
                    }
                    getService().npcChat(NpcName.NPCThuNuoi, text);
                } else {
                    getService().npcChat(NpcName.NPCThuNuoi, "Hãy đưa ta 10.000 lượng ta mới giúp ngươi.");
                }
            } else {
                getService().npcChat(NpcName.NPCThuNuoi,
                        "Ngươi cần đeo thú nuôi trên người đã rồi luyện nhé .");
            }
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Nói chuyện", () -> {
            String talk = (String) NinjaUtils.randomObject(
                    "Ta có thể giúp ngươi làm tăng sức mạnh cho thú nuôi , ngươi chỉ cần trả cho ta ít nhiều ngân lượng.",
                    "Nhanh lên kẻo bị phát hiện.", "Chắc chắn ngươi sẽ cần ta giúp.");
            getService().npcChat(NpcName.NPCThuNuoi, talk);
        }));
        menus.add(new Menu(CMDMenu.EXECUTE, "Nâng cấp thú nuôi", () -> {
            Item item = this.equipment[ItemTemplate.TYPE_THUNUOI];
            if (this.equipment[ItemTemplate.TYPE_THUNUOI] != null && (this.equipment[ItemTemplate.TYPE_THUNUOI].id < 924 
                    || this.equipment[ItemTemplate.TYPE_THUNUOI].id > 1047)) {
                int cap = 0;
                for (ItemOption option : item.options) {
                    if (option.optionTemplate.id == 85) {
                        cap = option.param;
                        break;
                    }
                }
                if (cap >= 10) {
                    getService().npcChat(NpcName.NPCThuNuoi,
                            "Thú nuôi của ngươi đã quá mạnh, ta không thể giúp được ngươi.");
                    return;
                }
                int fee = (cap + 5) * 400;
                int percent = GameData.UP_THU_NUOI[cap];
                setConfirmPopup(new ConfirmPopup(CMDConfirmPopup.NANG_THU_NUOI, String.format(
                        "Ngươi có muốn nâng cấp ThuNuoi đang sử dụng lên cấp %d không? với phí %d lượng - Tỉ lệ thành công: %s.",
                        (cap + 1), fee, percent + "%")));
                getService().openUIConfirmID();
            } else {
                getService().npcChat(NpcName.NPCThuNuoi,
                        "Ngươi hãy sử dụng ThuNuoi ta sẽ giúp ngươi làm thay đổi sức mạnh.");
            }
        }));
    }

}
