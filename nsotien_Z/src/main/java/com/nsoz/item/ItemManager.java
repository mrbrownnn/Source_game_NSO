package com.nsoz.item;

import java.util.ArrayList;

import com.nsoz.db.jdbc.DbManager;
import com.nsoz.model.Char;
import com.nsoz.server.Config;
import com.nsoz.server.Server;
import com.nsoz.util.NinjaUtils;
import com.nsoz.util.ProgressBar;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;

public class ItemManager {

    private static final ItemManager instance = new ItemManager();

    public static ItemManager getInstance() {
        return instance;
    }

    public static final int[] MOUNT_OPTION_ID = new int[]{6, 7, 10, 67, 68, 69, 70, 71, 72, 73, 74};
    public static final int[] MOUNT_OPTION_PARAM = new int[]{50, 50, 10, 5, 10, 10, 5, 5, 5, 100, 50};

    private final ArrayList<ItemTemplate> listItemGloryTask = new ArrayList<>();
    private final ArrayList<ItemTemplate> itemTemplates = new ArrayList<>();
    private final ArrayList<ItemOptionTemplate> optionTemplates = new ArrayList<>();
    @Getter
    private byte[] data;

    public void init() {
        for (ItemTemplate template : itemTemplates) {
            if (template.level >= 10 && template.level <= 49 && template.fashion == -1) {
                listItemGloryTask.add(template);
            }
        }
    }

    public int getOptionSize() {
        return this.optionTemplates.size();
    }

    public String getItemName(int index) {
        return itemTemplates.get(index).name;
    }

    public ItemTemplate getItemTemplate(int index) {
        return itemTemplates.get(index);
    }

    public ItemOptionTemplate getItemOptionTemplate(int index) {
        return optionTemplates.get(index);
    }

    public void setData() {
        try {
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bas);
            dos.writeByte(Config.getInstance().getItemVersion());
            dos.writeByte(optionTemplates.size());
            for (ItemOptionTemplate item : optionTemplates) {
                dos.writeUTF(item.name);
                dos.writeByte(item.type);
            }
            dos.writeShort(itemTemplates.size());
            for (ItemTemplate item : itemTemplates) {
                dos.writeByte(item.type);
                dos.writeByte(item.gender);
                dos.writeUTF(item.name);
                dos.writeUTF(item.description);
                dos.writeByte(item.level);
                dos.writeShort(item.icon);
                dos.writeShort(item.part);
                dos.writeBoolean(item.isUpToUp);
            }
            dos.flush();
            data = bas.toByteArray();
            dos.close();
            bas.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int randomItemGloryTask(Char _char) {
        ArrayList<Integer> list = new ArrayList<>();
        for (ItemTemplate template : listItemGloryTask) {
            if ((template.isTypeClothe() && template.gender == _char.gender) || template.isTypeAdorn()
                    || template.isTypeWeapon()) {
                if (template.isTypeWeapon() && !(template.checkSys(_char.classId))) {
                    continue;
                }
                list.add(template.id);
            }
        }
        int index = NinjaUtils.nextInt(list.size());
        return list.get(index);
    }

    public void load() {
        loadItem();
        loadItemOption();
    }

    public void loadItem() {
        try {
            Connection conn = DbManager.getInstance().getConnection(DbManager.GAME);
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `item`;", ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = stmt.executeQuery();
            resultSet.last();
            ProgressBar pb = new ProgressBar("Loading Item", resultSet.getRow());
            resultSet.beforeFirst();
            while (resultSet.next()) {
                try {
                    ItemTemplate item = new ItemTemplate();
                    item.id = resultSet.getInt("id");
                    item.name = resultSet.getString("name");
                    item.type = resultSet.getByte("type");
                    item.gender = resultSet.getByte("gender");
                    item.level = resultSet.getShort("level");
                    item.part = resultSet.getShort("part");
                    item.fashion = resultSet.getShort("fashion");
                    item.icon = resultSet.getShort("icon");
                    item.description = resultSet.getString("description");
                    item.isUpToUp = resultSet.getBoolean("isUpToUp");
                    add(item);
                    pb.setExtraMessage(item.name + " finished!");
                    pb.step();
                } catch (Exception e) {
                    pb.setExtraMessage(e.getMessage());
                    pb.reportError();
                    return;
                }
            }
            pb.setExtraMessage("Finished!");
            pb.reportSuccess();
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadItemOption() {
        try {
            Connection conn = DbManager.getInstance().getConnection(DbManager.GAME);
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `item_option`;",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = stmt.executeQuery();
            resultSet.last();
            ProgressBar pb = new ProgressBar("Loading Item Option", resultSet.getRow());
            resultSet.beforeFirst();
            while (resultSet.next()) {
                try {
                    ItemOptionTemplate itemOption = new ItemOptionTemplate();
                    itemOption.id = resultSet.getInt("id");
                    itemOption.name = resultSet.getString("name");
                    itemOption.type = resultSet.getByte("type");
                    add(itemOption);
                    pb.setExtraMessage(itemOption.id + " finished!");
                    pb.step();
                } catch (Exception e) {
                    pb.setExtraMessage(e.getMessage());
                    pb.reportError();
                    return;
                }
            }
            pb.setExtraMessage("Finished!");
            pb.reportSuccess();
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void add(ItemTemplate entry) {
        itemTemplates.add(entry);
    }

    public void add(ItemOptionTemplate option) {
        optionTemplates.add(option);
    }

    public void remove(ItemTemplate itemTemplate) {
        itemTemplates.remove(itemTemplate);
    }

    public void remove(ItemOptionTemplate itemOptionTemplate) {
        optionTemplates.remove(itemOptionTemplate);
    }

}
