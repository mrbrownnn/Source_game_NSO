
package com.nsoz.map.zones;

import com.nsoz.ability.AbilityCustom;
import com.nsoz.bot.attack.AttackAround;
import com.nsoz.bot.move.MoveToTarget;
import com.nsoz.bot.Bot;
import com.nsoz.constants.ItemName;
import com.nsoz.constants.MapName;
import com.nsoz.constants.SkillName;
import com.nsoz.effect.Effect;
import com.nsoz.fashion.FashionCustom;
import com.nsoz.item.Item;
import com.nsoz.item.ItemFactory;
import com.nsoz.map.Map;
import com.nsoz.model.Char;
import com.nsoz.model.Room;
import com.nsoz.option.SkillOption;
import com.nsoz.server.GameData;
import com.nsoz.skill.Skill;
import com.nsoz.util.NinjaUtils;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Admin
 */
public class FujukaSanctuary extends AreaWithCountdownTime {

    static final String[] recoveryMessages = {"Mỗi lần ngươi chết, ta lại cảm thấy cơ thể tràn đầy năng lượng", "Wowww, nguồn sức mạnh này", "Có vẻ như ngươi là một phiên bản thất bại"};
    static final String[] defiantMessages = {"Ngươi cũng chỉ là một trong số chúng ta, nhưng là phiên bản yếu kém hơn", "Hãy đón lấy những đòn tấn công này đi", "Sức mạnh của ngươi là một thứ gì đó rẻ mạt"};

    private ArrayList<Bot> bots;
    private ArrayList<Room> rooms;
    private boolean finished;
    private Char player;
    private int step;

    public FujukaSanctuary(int id, Map map, Char player) {
        super(id, map.tilemap, map);
        this.countDown = 1800;
        this.bots = new ArrayList<>();
        this.rooms = new ArrayList<>();
        this.player = player;
        this.step = 0;
        initMap();
        initBot();
    }

    public void initMap() {
        Room room1 = Room.builder()
                .x(24)
                .y(0)
                .w(312)
                .h(192)
                .minX(552)
                .maxX(648)
                .minY(480)
                .maxY(504)
                .tx((short) 180)
                .ty((short) 192)
                .build();
        rooms.add(room1);
        Room room2 = Room.builder()
                .x(360)
                .y(0)
                .w(264)
                .h(192)
                .minX(672)
                .maxX(768)
                .minY(432)
                .maxY(456)
                .tx((short) 492)
                .ty((short) 192)
                .build();
        rooms.add(room2);
        Room room3 = Room.builder()
                .x(648)
                .y(0)
                .w(288)
                .h(192)
                .minX(792)
                .maxX(888)
                .minY(480)
                .maxY(504)
                .tx((short) 792)
                .ty((short) 192)
                .build();
        rooms.add(room3);
        Room room4 = Room.builder()
                .x(936)
                .y(0)
                .w(312)
                .h(192)
                .minX(1032)
                .maxX(1128)
                .minY(480)
                .maxY(504)
                .tx((short) 1092)
                .ty((short) 192)
                .build();
        rooms.add(room4);
        Room room5 = Room.builder()
                .x(1272)
                .y(0)
                .w(264)
                .h(192)
                .minX(1152)
                .maxX(1248)
                .minY(432)
                .maxY(456)
                .tx((short) 1404)
                .ty((short) 192)
                .build();
        rooms.add(room5);
        Room room6 = Room.builder()
                .x(1560)
                .y(0)
                .w(312)
                .h(192)
                .minX(1272)
                .maxX(1368)
                .minY(480)
                .maxY(504)
                .tx((short) 1716)
                .ty((short) 192)
                .build();
        rooms.add(room6);
    }

    public void nextStep() {
        boolean flag = false;
        for (Bot bot : bots) {
            if (bot.isDead) {
                if (bot.getRoom() != null) {
                    out(bot);
                    bot.setRoom(null);
                    Item item = ItemFactory.getInstance().newItem(ItemName.RUONG_HAC_AM);
                    item.setQuantity(1);
                    item.isLock = true;
                    player.addItemToBag(item);
                }
            } else {
                flag = true;
            }
        }
        if (this.step == 0) {
            if (!flag && player.getRoom() == null) {
                for (Bot bot : bots) {
                    bot.recovery();
                    bot.isDead = false;
                    bot.recovery();
                    bot.setXY(player.x, player.y);
                    join(bot);
                }
                this.step = 1;
            }
        } else if (this.step == 1) {
            if (!flag) {
                finish();
                this.step = 2; // finish
            }
        }
    }

    public void finish() {
        int chestQuantity = getChestQuantity();
        player.addClanPoint(20);
        for (int i = 0; i < chestQuantity; i++) {
            if (player.getSlotNull() > 0) {
                Item item = ItemFactory.getInstance().newItem(ItemName.RUONG_HAC_AM);
                item.setQuantity(1);
                item.isLock = true;
                player.addItemToBag(item);
            }
        }
        int keyQuantity =(int) NinjaUtils.nextInt(1, chestQuantity);
        for (int i = 0; i < keyQuantity; i++) {
            if (player.getSlotNull() > 0) {
                Item item = ItemFactory.getInstance().newItem(ItemName.KHOA_HAC_AM);
                item.setQuantity(1);
                item.isLock = false;
                player.addItemToBag(item);
            }
        }
//        List<Char> chars = getChars();
//        for (Char p : chars) {
//            if (!p.isNhanBan && !(p instanceof Bot)) {
//                p.addClanPoint(20);
//                Item item = ItemFactory.getInstance().newItem(ItemName.RUONG_HAC_AM);
//                item.quantity = getChestQuantity();
//                item.isLock = true;
//                p.addItemToBag(item);
//
//                item = ItemFactory.getInstance().newItem(ItemName.KHOA_HAC_AM);
//                item.quantity = NinjaUtils.nextInt(1, getChestQuantity());
//                item.isLock = false;
//                p.addItemToBag(item);
//            }
//        }
        this.finished = true;
        setTimeMap(60);
        getService().serverMessage("Đã tiêu diệt hết dị bản, chúc mừng ngươi");
    }

    public int getChestQuantity() {
        return (this.countDown / 60) / 3;
    }

    @Override
    public void join(Char p) {
        if (p.isNhanBan) {
            return;
        }
        super.join(p);
        if (!(p instanceof Bot) && p.isHuman) {
            p.setTypePk(Char.PK_NORMAL);
        }
    }

    @Override
    public void out(Char p) {
        super.out(p);
        Room r = p.getRoom();
        if (r != null) {
            r.setHavePlayer(false);
            p.setRoom(null);
            if (p instanceof Bot) {
                r.setBot(null);
            }
        }
        if (!(p instanceof Bot) && p.isHuman) {
            p.setTypePk(Char.PK_NORMAL);
        }
    }

    @Override
    public void returnTownFromDead(@NotNull Char p) {
        p.setXY((short) 964, (short) 552);
        out(p);
        join(p);
    }

    @Override
    public void move(@NotNull Char p, short x, short y) {
        Room room = p.getRoom();
        if (room == null) {
            room = getRoom(p.x, p.y);
            if (room != null && !room.isHavePlayer() && room.getBot() != null && !room.getBot().isDead) {
                p.setRoom(room);
                room.setHavePlayer(true);
                room.setPlayer(p);
                p.preX = room.getTx();
                p.preY = room.getTy();
                p.setXY(room.getTx(), room.getTy());
                p.getService().resetPoint();
                getService().teleport(p);
                return;
            }
            if (!(x >= 24 && x <= 1896 && y >= 264 && y <= 696)) {
                return;
            }
        } else {
            if (p instanceof Bot && !room.isHavePlayer()) {
                return;
            }
            if (x < room.getX() || x > (room.getX() + room.getW()) || y < room.getY()
                    || y > (room.getY() + room.getH())) {
                p.returnToPreviousPostion(() -> {
                });
                return;
            }
        }

        super.move(p, x, y);
    }

    public Room getRoom(short x, short y) {
        for (Room room : rooms) {
            if (x >= room.getMinX() && x <= room.getMaxX() && y > room.getMinY() && y <= room.getMaxY()) {
                return room;
            }
        }
        return null;
    }

    public void initBot() {
        synchronized (rooms) {
            int i = 0;
            short[] wps = {12, 15, 41, 14, 13, 16, 249};
            for (Room r : rooms) {
                initBot(i + 1, r, wps[i]);
                i++;
            }
        }
    }

    public void initBot(int classID, Room r, short wp) {
        Bot bot = Bot.builder().id(-(classID + 100))
                .name(player.name)
                .level(player.level)
                .typePk(Char.PK_DOSAT)
                .classId((byte) classID)
                .build();
        bot.setDefault();
        FashionCustom fashionCustom = FashionCustom.builder()
                .head(player.head)
                .body(player.body)
                .leg(player.leg)
                .glove(player.glove)
                .coat(player.coat)
                .weapon(wp)
                .fBody(player.ID_BODY)
                .fHair(player.ID_HAIR)
                .fHorse(player.ID_HORSE)
                .fLeg(player.ID_LEG)
                .fMask(player.ID_MAT_NA)
                .fName(player.ID_NAME)
                .fPP(player.ID_PP)
                .fRank(player.ID_RANK)
                .fTransform(player.ID_BIEN_HINH)
                .fWeapon(player.ID_WEA_PONE)
                .build();
        bot.setFashionStrategy(fashionCustom);
        AbilityCustom abilityCustom = AbilityCustom.builder()
                .hp(player.maxHP > 50000 ? player.maxHP * 8 : 50000)
                .mp(player.maxHP > 50000 ? player.maxHP * 8 : 50000)
                .damage(player.damage)
                .damage2(player.damage2)
                .miss(player.miss)
                .exactly(player.exactly)
                .fatal(player.fatal)
                .speed(player.speed)
                .reactDame(player.reactDame)
                .resFire(player.resFire)
                .resIce(player.resIce)
                .resWind(player.resWind)
                .build();
        bot.setAbilityStrategy(abilityCustom);
        bot.setRoom(r);
        bot.setAbility();
        AttackAround attackAround = new AttackAround();
        List<Skill> skills = GameData.getInstance().getAllSkill((byte) classID, bot.level);
        for (Skill skill : skills) {
            if (skill.template.type == Skill.SKILL_AUTO_USE) {
                for (SkillOption option : skill.options) {
                    bot.optionsSupportSkill[option.optionTemplate.id] += option.param;
                }
            }
            if (skill.template.id == SkillName.NHAN_THUAT_KAGE_BUNSHIN_1
                    || skill.template.id == SkillName.NHAN_THUAT_KAGE_BUNSHIN_2
                    || skill.template.id == SkillName.NHAN_THUAT_KAGE_BUNSHIN_3
                    || skill.template.id == SkillName.NHAN_THUAT_KAGE_BUNSHIN_4
                    || skill.template.id == SkillName.NHAN_THUAT_KAGE_BUNSHIN_5
                    || skill.template.id == SkillName.NHAN_THUAT_KAGE_BUNSHIN_6) {
                continue;
            }
            attackAround.addSkill(skill);
        }
        bot.setAttack(attackAround);
        bot.setMove(new MoveToTarget(player));
        bot.options = player.options;
        bot.setEm(player.getEm().clone(bot));
        bot.options[99] += 300;
        bot.setFashion();
        bot.recovery();
        bot.setXY(r.getTx(), r.getTy());
        join(bot);
        bots.add(bot);
        r.setBot(bot);
        bot.setRoom(r);
    }

    long lastTimeChatPublic;

    @Override
    public void update() {
        if (!finished) {
            for (Room r : rooms) {
                if (r.isHavePlayer()) {
                    Char p = r.getPlayer();
                    if (r.getBot().isDead) {
                        p.setRoom(null);
                        r.setHavePlayer(false);
                        p.setXY((short) ((r.getMinX() + r.getMaxX()) / 2), (short) r.getMaxY());
                        p.getService().resetPoint();
                        getService().teleport(p);
                    } else {
                        Bot bot = r.getBot();
                        if (System.currentTimeMillis() - lastTimeChatPublic > 10000) {
                            lastTimeChatPublic = System.currentTimeMillis();
                            getService().chat(bot.id, defiantMessages[NinjaUtils.nextInt(defiantMessages.length - 1)]);
                        }
                    }
                }
            }
            nextStep();
        }
        super.update();
    }

    @Override
    public void startDie(@NotNull Char p) {
        if (!p.isBot()) {
            Room room = p.getRoom();
            if (room != null) {
                Bot bot = room.getBot();
                if (!bot.isDead) {
                    bot.maxHP = bot.maxHP * 110 / 100;
                    bot.damage = bot.damage * 110 / 100;
                    bot.damage2 = bot.damage - (bot.damage / 10);
                    bot.recovery();
                    getService().refreshHP(bot);
                    getService().chat(bot.id, recoveryMessages[NinjaUtils.nextInt(recoveryMessages.length - 1)]);
                }
            }
            super.startDie(p);
        }
    }

    public void copyRecoveryEffect(Char _char, Effect effect) {
        Room room = _char.getRoom();
        if (room != null) {
            Bot bot = room.getBot();
            if (!bot.isDead) {
                bot.getEm().setEffect(effect);
            }
        }
    }

    @Override
    public void close() {
        if (!isClosed()) {
            List<Char> chars = getChars();
            for (Char c : chars) {
                if (c.isNhanBan || c instanceof Bot) {
                    continue;
                }
                c.setXY((short) 852, (short) 360);
                c.changeMap(MapName.LANG_SHIIBA);
            }
        }
        super.close();
    }
}
