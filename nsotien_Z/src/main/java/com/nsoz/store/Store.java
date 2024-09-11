
package com.nsoz.store;

import com.nsoz.constants.ItemName;
import com.nsoz.constants.SQLStatement;
import com.nsoz.constants.TaskName;
import com.nsoz.convert.Converter;
import com.nsoz.db.jdbc.DbManager;
import com.nsoz.event.Event;
import com.nsoz.event.eventpoint.EventPoint;
import com.nsoz.item.Item;
import com.nsoz.item.ItemFactory;
import com.nsoz.item.ItemTemplate;
import com.nsoz.model.Char;
import com.nsoz.model.History;
import com.nsoz.option.ItemOption;
import com.nsoz.server.Config;
import com.nsoz.util.NinjaUtils;
import com.nsoz.util.ProgressBar;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

@Getter
public class Store {

    private int type;
    private String name;
    private List<ItemStore> items;

    public Store(int type, String name) {
        this.type = type;
        this.name = name;
        this.items = new ArrayList<>();

    }


    public boolean load() {
        try {
            Connection conn = DbManager.getInstance().getConnection(DbManager.GAME);
            PreparedStatement stmt = conn.prepareStatement(SQLStatement.GET_STORE_DATA,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            stmt.setInt(1, this.type);
            ResultSet resultSet = stmt.executeQuery();
            resultSet.last();
            ProgressBar pb = new ProgressBar(this.name, resultSet.getRow());
            resultSet.beforeFirst();
            while (resultSet.next()) {
                try {
                    int id = resultSet.getInt("id");
                    int itemID = resultSet.getInt("item_id");
                    boolean lock = resultSet.getBoolean("lock");
                    int coin = resultSet.getInt("coin");
                    int gold = resultSet.getInt("gold");
                    int yen = resultSet.getInt("yen");
                    byte sys = resultSet.getByte("sys");
                    long expire = resultSet.getLong("expire");
                    JSONArray jArr = new JSONArray(resultSet.getString("options"));
                    List<ItemOption> options = new ArrayList<>();
                    for (int i = 0; i < jArr.length(); i++) {
                        JSONObject obj = jArr.getJSONObject(i);
                        int oID = obj.getInt("id");
                        int oParam = obj.getInt("param");
                        options.add(new ItemOption(oID, oParam));
                    }
                    ItemStore item = new ItemStore(id, itemID, sys, coin, yen, gold, lock, expire, options);
                    item.setMaxOptions(options);
                    add(item);
                    pb.setExtraMessage(item.getItemID() + " finished!");
                    pb.step();
                } catch (Exception e) {
                    pb.setExtraMessage(e.getMessage());
                    pb.reportError();
                    return false;
                }
            }
            pb.setExtraMessage("Finished!");
            pb.reportSuccess();
            resultSet.close();
            stmt.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int count() {
        return items.size();
    }

    public void add(ItemStore item) {
        items.add(item);
    }

    public void remove(ItemStore item) {
        items.remove(item);
    }

    public ItemStore find(int itemID) {
        for (ItemStore item : items) {
            if (item.getItemID() == itemID) {
                return item;
            }
        }
        return null;
    }

    public ItemStore get(int index) {
        if (index < 0 || index >= items.size()) {
            return null;
        }
        return items.get(index);
    }

    public void buy(Char p, int indexUI, int quantity) {
        ItemStore item = get(indexUI);
        if (item == null) {
            return;
        }
        ItemTemplate template = item.getTemplate();
        int slotNull = p.getSlotNull();
        if ((template.isUpToUp && slotNull == 0) || (!template.isUpToUp && slotNull < quantity)) {
            p.warningBagFull();
            return;
        }
        long giaXu = ((long) item.getCoin()) * ((long) quantity);
        long giaYen = ((long) item.getYen()) * ((long) quantity);
        long giaLuong = ((long) item.getGold()) * ((long) quantity);
        if (giaXu < 0 || giaYen < 0 || giaLuong < 0) {
            return;
        }
        if (giaXu > p.coin || giaLuong > p.user.gold || giaYen > p.yen) {
            p.serverDialog(p.language.getString("Bạn Không Đủ Tiền"));
            return;
        }
        History history = new History(p.id, History.MUA_VAT_PHAM);
        history.setPrice((int) giaXu, (int) giaYen, (int) giaLuong);
        history.setBefore(p.coin, p.user.gold, p.yen);
        if (Event.isVietnameseWomensDay() || Event.isInternationalWomensDay()|| Event.isTrungThu() ) {
            int point = (int) ((giaLuong / 10)); //(giaXu / 1000000) + 
            if (point > 0) {
                p.getEventPoint().addPoint(EventPoint.DIEM_TIEU_XAI, point);
                p.serverMessage(String.format("Bạn nhận được %s điểm tiêu xài.", NinjaUtils.getCurrency(point)));
            }
        }
        if (Event.isLunarNewYear()){
            int point = (int) ((giaLuong / 10));
            if (point > 0) {
                Item pieceFirework = ItemFactory.getInstance().newItem(ItemName.MANH_PHAO_HOA);
                pieceFirework.setQuantity(point);
                p.addItemToBag(pieceFirework);
            }
        }
        p.coin -= giaXu;
        p.user.gold -= giaLuong;
        p.yen -= giaYen;
        p.getService().buy();
        history.setAfter(p.coin, p.user.gold, p.yen);
        int n = quantity;
        if (template.isUpToUp) {
            n = 1;
        }
//      Ngoc 10
//      new item la ngoc o phien ban test thi ngoc +10
        if(template.id >= 652 && template.id <= 655 && Config.getInstance().isTestVersion()){
            for (int i = 0; i < n; i++) {

                Item newItem = Converter.getInstance().toItem(item, Converter.MAX_OPTION);

                if (template.isUpToUp) {
                    newItem.setQuantity(quantity);
                } else {
                    newItem.setQuantity(1);
                }
                newItem.setLock(false);
                p.addItemToBag(newItem);
                history.addItem(newItem);
            }
        }
        else
            for (int i = 0; i < n; i++) {
                Item newItem = Converter.getInstance().toItem(item, Converter.RANDOM_OPTION);
                if (template.isUpToUp) {
                    newItem.setQuantity(quantity);
                } else {
                    newItem.setQuantity(1);
                }
                if (giaYen > 0 || giaLuong > 0) {
                    newItem.isLock = true;
                }
                p.addItemToBag(newItem);
                history.addItem(newItem);
            }
        if (template.id == ItemName.COM_NAM && p.taskId == TaskName.NV_DIET_SEN_TRU_COC
                && p.taskMain != null && p.taskMain.index == 0) {
            p.updateTaskCount(quantity);
        }
        history.setTime(System.currentTimeMillis());
        History.insert(history);
    }


    public Stream<ItemStore> stream() {
        return items.stream();
    }

    public void show(Char p) {
        p.getService().openUIShop((byte) type, items);
    }
}
