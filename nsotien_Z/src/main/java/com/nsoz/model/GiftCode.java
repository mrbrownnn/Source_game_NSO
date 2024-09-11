
package com.nsoz.model;

import com.nsoz.constants.SQLStatement;
import com.nsoz.db.jdbc.DbManager;
import com.nsoz.item.Item;
import com.nsoz.lib.ZConnection;
import com.nsoz.server.Config;
import com.nsoz.util.NinjaUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class GiftCode {

    private static final GiftCode instance = new GiftCode();

    public static GiftCode getInstance() {
        return instance;
    }

    public void use(Char player, String code) {
        try {
            int lent = code.length();
            if (code.equals("") || lent < 5 || lent > 30) {
                player.getService().serverDialog("Mã quà tặng có chiều dài từ 5 đến 30 ký tự.");
                return;
            }

            PreparedStatement stmt = DbManager.getInstance().getConnection(DbManager.GIFT_CODE).prepareStatement(
                    SQLStatement.GET_GIFT_CODE, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            stmt.setString(1, code);
            stmt.setInt(2, Config.getInstance().getServerID());
            ResultSet res = stmt.executeQuery();
            try {
                if (!res.first()) {
                    player.getService().serverDialog("Mã quà tặng không tồn tại hoặc đã hết hạn.");
                    return;
                }

                int id = res.getInt("id");
                byte status = res.getByte("status");
                byte type = res.getByte("type");
                byte serverId = res.getByte("server_id");
                byte used = res.getByte("used");
                if (status == 1) {
                    player.getService().serverDialog("Mã quà tặng đã được sử dụng");
                    return;
                } else if (type == 1 && isUsedGiftCode(player, code)) {
                    player.getService().serverDialog("Mỗi người chỉ được sử dụng 1 lần.");
                    return;
                } else if (player.user.session.getCountUseGiftCode() >= 20) {
                    player.getService().serverDialog("Mỗi ngày chỉ có thể nhập tối đa 20 mã quà tặng.");
                    return;
                }else if(type == 1 && used == 0 ){
                    player.getService().serverDialog("Đã hết số lần sử dụng mã quà tặng này.");
                    return;
                }
                int gold = res.getInt("gold");
                int yen = res.getInt("yen");
                int coin = res.getInt("coin");

                JSONArray arrItem = (JSONArray) (new JSONParser().parse(res.getString("items")));

                int size = arrItem.size();

                if (size > player.getSlotNull()) {
                    player.getService().serverDialog("Bạn không đủ chỗ trống trong hành trang.");
                    return;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("Chúc mừng, bạn đã được tặng").append("\n\n");

                if (gold > 0) {
                    player.addGold(gold);
                    sb.append(String.format("- %s lượng", NinjaUtils.getCurrency(gold))).append("\n");
                }

                if (yen > 0) {
                    player.addYen(yen);
                    sb.append(String.format("- %s yên", NinjaUtils.getCurrency(yen))).append("\n");
                }

                if (coin > 0) {
                    player.addCoin(coin);
                    sb.append(String.format("- %s xu", NinjaUtils.getCurrency(coin))).append("\n");
                }

                for (int i = 0; i < size; i++) {
                    JSONObject itemObj = (JSONObject) arrItem.get(i);
                    Item newItem = new Item(itemObj);

                    if (newItem.options.isEmpty()) {
                        newItem.initOption();
                    }

                    player.addItemToBag(newItem);
                    sb.append(
                            String.format("- x%s %s", NinjaUtils.getCurrency(newItem.getQuantity()), newItem.template.name))
                            .append("\n");
                }

                player.user.session.addUseGiftCode();

                player.getService().showAlert("Mã quà tặng", sb.toString());

                addUsedGiftCode(player, code);


                if(used > 0 && type == 1){
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    res.updateByte("used", (byte) (used -1));
                    res.updateTimestamp("updated_at", timestamp);
                    res.updateRow();
                }else if (type == 0) {
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    res.updateByte("status", (byte) 1);
                    res.updateTimestamp("updated_at", timestamp);
                    res.updateRow();
                }

            } finally {
                res.close();
                stmt.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isUsedGiftCode(Char player, String giftCode) {
        try {
            PreparedStatement stmt = DbManager.getInstance().getConnection(DbManager.GIFT_CODE).prepareStatement(
                    SQLStatement.CHECK_EXIST_USED_GIFT_CODE, ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, giftCode);
            stmt.setInt(2, player.id);
            stmt.setInt(3, player.user.id);
            ResultSet res = stmt.executeQuery();
            try {
                if (res.first()) {
                    return true;
                }
            } finally {
                res.close();
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addUsedGiftCode(Char player, String giftCode) {
        try {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            PreparedStatement stmt = DbManager.getInstance().getConnection(DbManager.GIFT_CODE).prepareStatement(SQLStatement.INSERT_USED_GIFT_CODE);
            stmt.setInt(1, player.id);
            stmt.setInt(2, player.user.id);
            stmt.setString(3, giftCode);
            stmt.setTimestamp(4, timestamp);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
