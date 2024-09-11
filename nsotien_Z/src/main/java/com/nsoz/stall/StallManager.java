
package com.nsoz.stall;

import com.nsoz.constants.CMDMenu;
import com.nsoz.db.jdbc.DbManager;
import com.nsoz.item.Item;
import com.nsoz.model.Char;
import com.nsoz.model.Menu;
import com.nsoz.server.Config;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class StallManager implements Runnable {

    public static final byte STATUS_ON_SALE = 0;
    public static final byte STATUS_BOUGHT = 1;
    public static final byte STATUS_RECEIVED = 2;

    public static final byte TYPE_DA = 26;
    public static final byte TYPE_NON = 0;
    public static final byte TYPE_VU_KHI = 1;
    public static final byte TYPE_AO = 2;
    public static final byte TYPE_DAY_CHUYEN = 3;
    public static final byte TYPE_GANG_TAY = 4;
    public static final byte TYPE_NHAN = 5;
    public static final byte TYPE_QUAN = 6;
    public static final byte TYPE_BOI = 7;
    public static final byte TYPE_GIAY = 8;
    public static final byte TYPE_BUA = 9;
    public static final byte TYPE_LINH_TINH = 10;

    public static final byte[] AUCTION = {TYPE_DA, TYPE_NON, TYPE_VU_KHI, TYPE_AO, TYPE_DAY_CHUYEN, TYPE_GANG_TAY,
        TYPE_NHAN, TYPE_QUAN, TYPE_BOI, TYPE_GIAY, TYPE_BUA, TYPE_LINH_TINH};
    private static int autoIncrement = 0;

    public synchronized static int autoIncrement() {
        return autoIncrement++;
    }

    private static final StallManager instance = new StallManager();

    public static StallManager getInstance() {
        return instance;
    }

    private List<Stall> stalls;
    private boolean running;
    private long lastUpdate;

    public StallManager() {
        this.running = true;
        this.stalls = new ArrayList<>();
        init();
        load();
    }

    public void init() {
        add(new Stall(0, TYPE_DA, "Đá"));
        add(new Stall(1, TYPE_NON, "Nón"));
        add(new Stall(2, TYPE_VU_KHI, "Vũ khí"));
        add(new Stall(3, TYPE_AO, "Áo"));
        add(new Stall(4, TYPE_DAY_CHUYEN, "Dây chuyền"));
        add(new Stall(5, TYPE_GANG_TAY, "Găng tay"));
        add(new Stall(6, TYPE_NHAN, "Nhẫn"));
        add(new Stall(7, TYPE_QUAN, "Quần"));
        add(new Stall(8, TYPE_BOI, "Ngọc Bội"));
        add(new Stall(9, TYPE_GIAY, "Giày"));
        add(new Stall(10, TYPE_BUA, "Bùa"));
        add(new Stall(11, TYPE_LINH_TINH, "Linh tinh"));
    }

    public void addItem(Char p, Item item, int price) {
        item.setProductID(autoIncrement());
        item.setProductPrice(price);
        item.setProductSeller(p.name);
        item.setProductStatus(STATUS_ON_SALE);
        item.setProductChanged(false);
        item.setProductTime(86400);
        Stall stall = findByType(item.template.type);
        if (stall != null) {
            stall.add(item);
            DbManager.getInstance().insertItemToStall(item);
        }
    }

    public void load() {
        PreparedStatement ps = null;
        try {
            ps = DbManager.getInstance().getConnection(DbManager.GAME)
                    .prepareStatement("SELECT * FROM `shinwa` WHERE `status` = ? AND `server_id` = ?");
            ps.setInt(1, STATUS_ON_SALE);
            ps.setInt(2, Config.getInstance().getServerID());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String seller = rs.getString("seller");
                int price = rs.getInt("price");
                byte status = rs.getByte("status");
                int time = rs.getInt("time");
                JSONObject obj = (JSONObject) JSONValue.parse(rs.getString("item"));
                Item item = new Item(obj);
                item.setProductID(autoIncrement());
                item.setProductPrice(price);
                item.setProductSeller(seller);
                item.setProductStatus(status);
                item.setProductTime(time);
                item.setProductUniqueId(id);
                Stall stall = findByType(item.template.type);
                if (stall != null) {
                    stall.add(item);
                }
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void openUIMenu(Char p) {
        p.menus.clear();
        for (Stall stall : stalls) {
            p.menus.add(new Menu(CMDMenu.EXECUTE, stall.getName(), () -> {
                stall.show(p);
            }));

        }
        p.getService().openUIMenu();
    }

    public void save() {
        stalls.forEach(t -> {
            t.save();
        });
    }

    public void add(Stall stall) {
        stalls.add(stall);
    }

    public void remove(Stall stall) {
        stalls.remove(stall);
    }

    public Stall findByType(byte type) {
        boolean flag = false;
        for (byte t : AUCTION) {
            if (t == type) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            type = TYPE_LINH_TINH;
        }
        for (Stall stall : stalls) {
            if (stall.getType() == type) {
                return stall;
            }
        }
        return null;
    }

    public Stall findByID(int id) {
        for (Stall stall : stalls) {
            if (stall.getId() == id) {
                return stall;
            }
        }
        return null;
    }

    public int getTotalProductBySeller(String productSeller) {
        int total = 0;
        for (Stall stall : stalls) {
            total += stall.getTotalProductBySeller(productSeller);
        }
        return total;
    }

    public void receiveItem(Char p) {
        stalls.forEach((t) -> {
            List<Item> expiredProductList = t.getExpiredProductListBySeller(p.name);
            for (Item item : expiredProductList) {
                if (item.getProductStatus() == STATUS_ON_SALE) {
                    if (p.addItemToBag(item)) {
                        item.setProductStatus(STATUS_RECEIVED);
                        DbManager.getInstance().updateProduct(item);
                        item.setProductChanged(false);
                    } else {
                        return;
                    }
                }
            }
        });
    }

    public void update() {
        stalls.forEach((t) -> {
            t.update();
        });
        long l = System.currentTimeMillis();
        if (l - lastUpdate > 900000) {
            lastUpdate = l;
            save();
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (running) {
            long l1 = System.currentTimeMillis();
            update();
            long l2 = System.currentTimeMillis();
            if (l2 - l1 < 1000) {
                try {
                    Thread.sleep(1000 - (l2 - l1));
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop() {
        this.running = false;
    }
}
