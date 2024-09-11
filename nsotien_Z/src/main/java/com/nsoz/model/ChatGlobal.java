
package com.nsoz.model;

import com.nsoz.constants.ItemName;
import com.nsoz.event.Event;
import com.nsoz.item.Item;
import com.nsoz.lib.ProfanityFilter;
import com.nsoz.lib.RandomCollection;
import com.nsoz.network.Message;
import com.nsoz.server.GlobalService;
import com.nsoz.server.Server;
import com.nsoz.util.NinjaUtils;
import java.io.IOException;

public class ChatGlobal implements IChat {

    private static ProfanityFilter profanityFilter;

    public static ProfanityFilter getFilter() {
        if (profanityFilter == null) {
            synchronized (ProfanityFilter.class) {
                if (profanityFilter == null) {
                    profanityFilter = new ProfanityFilter();
                    profanityFilter.addBadWord("lồn");
                    profanityFilter.addBadWord("buồi");
                    profanityFilter.addBadWord("địt");
                    profanityFilter.addBadWord("súc vật");
                    profanityFilter.addBadWord("lon");
                    profanityFilter.addBadWord("buoi");
                    profanityFilter.addBadWord("dit");
                    profanityFilter.addBadWord("suc vat");
                    profanityFilter.addBadWord("mẹ mày");
                    profanityFilter.addBadWord("me may");
                    profanityFilter.addBadWord("đm");
                    profanityFilter.addBadWord("dm");
                    profanityFilter.addBadWord(".com");
                    profanityFilter.addBadWord(".tk");
                    profanityFilter.addBadWord(".ga");
                    profanityFilter.addBadWord(".cf");
                    profanityFilter.addBadWord(".net");
                    profanityFilter.addBadWord(".xyz");
                    profanityFilter.addBadWord(".mobi");
                    profanityFilter.addBadWord(".ml");
                    profanityFilter.addBadWord(".onine");
                    profanityFilter.addBadWord("như cc");
                    profanityFilter.addBadWord("nhu cc");
                    profanityFilter.addBadWord("game rác");
                    profanityFilter.addBadWord("game rac");
                }
            }
        }
        return profanityFilter;
    }

    private Char player;
    private long lastTimeChat;
    private long delay;
    private String text;
    private byte type;

    public ChatGlobal(Char player) {
        this.player = player;
        this.delay = 10000;
    }

    public void read(Message ms) {

        try {
            text = ms.reader().readUTF();
            type = -1;
            if (ms.reader().available() > 0) {
                type = ms.reader().readByte();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void wordFilter() {
        text = getFilter().filterBadWords(text);
    }

    public void send() {
//        if (text.equals(player.language.getString("NOT_ENOUGH_k"))) {
//            player.openUI(player);
//            return;
//        }
//        if (text.equals(player.language.getString("NOT_ENOUGH_r"))) {
//            player.openUII(player);
//            return;
//        }
        if (player.user.kh ==0) {
                player.serverDialog("Bạn chưa kích hoạt lên sẽ bị hạn chế tính năng này");
                return;
            }
        if (type == -1) {
            long now = System.currentTimeMillis();
            if (now - lastTimeChat < delay) {
                long mili = (int) (now - lastTimeChat);
                player.serverMessage(String.format("Chỉ có thể chat sau %s giây.",
                        NinjaUtils.timeAgo((int) ((delay - mili) / 1000))));
                return;
            }
            lastTimeChat = now;
            if (player.user.gold < 5) {
                player.serverDialog("Bạn không đủ lượng!");
                return;
            }
            player.addGold(-5);
            GlobalService.getInstance().chat(player.name, text);
        } else if (type == 0 || type == 1) {
            if (!Event.isLunarNewYear()) {
                player.serverMessage("Chỉ sử dụng được trong sự kiện tết.");
                return;
            }
            Event event = Event.getEvent();
            text = "Chúc " + text;

            int itemId = type == 0 ? ItemName.THIEP_CHUC_TET : ItemName.THIEP_CHUC_TET_DAC_BIET;
            int indexUI = player.getIndexItemByIdInBag(itemId);
            if (indexUI == -1) {
                player.serverDialog("Bạn không có thiệp chúc tết");
                return;
            }

            RandomCollection<Integer> rc = type == 0 ? event.getItemsRecFromCoinItem() : event.getItemsRecFromGoldItem();
            Item item = player.bag[indexUI];
            event.useEventItem(player, item.id, rc);
            player.getService().chatGlobal(player.name, text);
        }
    }
}
