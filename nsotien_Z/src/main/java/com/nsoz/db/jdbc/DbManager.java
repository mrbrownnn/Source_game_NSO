
package com.nsoz.db.jdbc;

import com.nsoz.constants.SQLStatement;
import com.nsoz.item.Item;
import com.nsoz.lib.ZConnection;
import com.nsoz.server.Config;
import com.nsoz.util.Log;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbManager {

    private static DbManager instance = null;
    private HikariDataSource hikariDataSource;
    private List<ZConnection> connections;
    private final int timeOut;

    public static final int GAME = 0;
    public static final int LOGIN = 1;
    public static final int SAVE_DATA = 2;
    public static final int SERVER = 3;
    public static final int UPDATE = 4;
    public static final int GIFT_CODE = 5;
    public static final int LOAD_CHAR = 6;
    public static final int CREATE_CHAR = 7;

    public static DbManager getInstance() {
        if (instance == null) {
            instance = new DbManager();
        }
        return instance;
    }

    private DbManager() {
        timeOut = 10;
        connections = new ArrayList<>();
        connections.add(new ZConnection(timeOut));
        connections.add(new ZConnection(timeOut));
        connections.add(new ZConnection(timeOut));
        connections.add(new ZConnection(timeOut));
        connections.add(new ZConnection(timeOut));
        connections.add(new ZConnection(timeOut));
        connections.add(new ZConnection(timeOut));
        connections.add(new ZConnection(timeOut));
    }

    public Connection getConnection() throws SQLException {
        return this.hikariDataSource.getConnection();
    }

    public boolean start() {
        if (this.hikariDataSource != null) {
            Log.warn("DB Connection Pool has already been created.");
            return false;
        } else {
            try {
                Config serverConfig = Config.getInstance();
                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(Config.getInstance().getJdbcUrl());
                config.setDriverClassName(serverConfig.getDbDriver());
                config.setUsername(serverConfig.getDbUser());
                config.setPassword(serverConfig.getDbPassword());
                config.addDataSourceProperty("minimumIdle", serverConfig.getDbMinConnections());
                config.addDataSourceProperty("maximumPoolSize", serverConfig.getDbMaxConnections());
                config.setConnectionTimeout(serverConfig.getDbConnectionTimeout());
                config.setLeakDetectionThreshold(serverConfig.getDbLeakDetectionThreshold());
                config.setIdleTimeout(serverConfig.getDbIdleTimeout());
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

                this.hikariDataSource = new HikariDataSource(config);
                Log.info("DB Connection Pool has created.");
                return true;
            } catch (Exception e) {
                Log.error("DB Connection Pool Creation has failed.");
                return false;
            }
        }
    }

    public ArrayList<HashMap<String, Object>> convertResultSetToList(ResultSet rs) {
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        try {
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            int count = resultSetMetaData.getColumnCount();
            while (rs.next()) {
                HashMap<String, Object> map = new HashMap<>();
                int index = 1;
                while (index <= count) {
                    int type = resultSetMetaData.getColumnType(index);
                    String name = resultSetMetaData.getColumnName(index);
                    switch (type) {
                        case -5: {
                            map.put(name, rs.getLong(index));
                            break;
                        }
                        case 6: {
                            map.put(name, rs.getFloat(index));
                            break;
                        }
                        case 12: {
                            map.put(name, rs.getString(index));
                            break;
                        }
                        case 1: {
                            map.put(name, rs.getString(index));
                            break;
                        }
                        case 4: {
                            map.put(name, rs.getInt(index));
                            break;
                        }
                        case 16: {
                            map.put(name, rs.getBoolean(index));
                            break;
                        }
                        case -7: {
                            map.put(name, rs.getByte(index));
                            break;
                        }

                        default: {
                            map.put(name, rs.getObject(index));
                            break;
                        }
                    }
                    ++index;
                }
                list.add(map);
            }
            rs.close();
        } catch (SQLException sQLException) {
            Log.error("convertResultSetToList ex: " + sQLException.getMessage());
        }
        return list;
    }

    public Connection getConnection(int index) throws SQLException {
        if (hikariDataSource == null) {
            while (hikariDataSource == null) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return connections.get(index).getConnection();
    }

    public void shutdown() {
        try {
            if (this.hikariDataSource != null) {
                this.hikariDataSource.close();
                Log.info("DB Connection Pool is shutting down.");
            }
            this.hikariDataSource = null;
        } catch (Exception e) {
            Log.warn("Error when shutting down DB Connection Pool");
        }
    }

    public int update(String sql, Object... params) {
        try {
            Connection conn = getInstance().getConnection(UPDATE);
            PreparedStatement stmt = conn.prepareStatement(sql);
            try {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
                int result = stmt.executeUpdate();
                return result;
            } finally {
                stmt.close();
            }
        } catch (SQLException e) {
            Log.error("update() EXCEPTION: " + e.getMessage(), e);
        }
        return -1;
    }

    public int updateAmountUnpaid(int userID, int amount) {
        return update(SQLStatement.UPDATE_AMOUNT_UNPAID, amount, userID);
    }

    public int updateGold(int userID, int amount) {
        return update(SQLStatement.UPDATE_GOLD, amount, userID);
    }

    public int updateCoin(int playerID, int amount) {
        return update(SQLStatement.UPDATE_COIN, amount, playerID);
    }

    public int updateYen(int playerID, int amount) {
        return update(SQLStatement.UPDATE_YEN, amount, playerID);
    }

    public void addGold(int userID, int amount) {
        update(SQLStatement.ADD_GOLD, amount, userID);
    }

    public int addCoin(int playerID, int amount) {
        return update(SQLStatement.ADD_COIN, amount, playerID);
    }

    public int addYen(int playerID, int amount) {
        return update(SQLStatement.ADD_YEN, amount, playerID);
    }

    public int updateMessage(int playerID, String message) {
        return update(SQLStatement.UPDATE_MESSAGE, message, playerID);
    }

    public int updateProduct(Item item) {
        return update(SQLStatement.UPDATE_PRODUCT, item.getProductStatus(), item.getProductTime(), item.getProductUniqueId());
    }

    public void active_account(String username) {
        update(SQLStatement.ACTIVE_ACCOUNT, 1, 1, username);
    }

    public void lock_account(String username) {
        update(SQLStatement.LOCK_ACCOUNT, 0, username);
    }

    public void banuntil_account(String username, int time) {
        update(SQLStatement.BANUNI_ACCOUNT, LocalDateTime.now().plusHours(time), username);
        update(SQLStatement.LOCK_ACCOUNT, 1, username);
    }

    public int getIdUser(String username) {
        int id = -1;
        try {
            PreparedStatement stmt = DbManager.getInstance().getConnection(DbManager.GAME).prepareStatement(SQLStatement.GET_ID_USERNAME, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, username);
            ResultSet res = stmt.executeQuery();
            if (res.first()) {
                id = res.getInt("id");
            }
            res.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public boolean setAdmin(String username) {
        int id = getIdUser(username);
        if (id != -1) {
            try {
                update(SQLStatement.SET_ADMIN, 1, "App\\Modules\\User\\Models\\User", id);
                return true;
            } catch (Exception e) {
                Log.error("LOI");
            }
        }
        return false;
    }

    public void insertItemToStall(Item item) {
        try {
            PreparedStatement stmt = getConnection(DbManager.SERVER).prepareStatement(SQLStatement.INSERT_ITEM_TO_STALL,
                    Statement.RETURN_GENERATED_KEYS);

            stmt.setInt(1, Config.getInstance().getServerID());
            stmt.setString(2, item.getProductSeller());
            stmt.setString(3, item.toJSONObject().toJSONString());
            stmt.setInt(4, item.getProductPrice());
            stmt.setInt(5, item.getProductStatus());
            stmt.setInt(6, item.getProductTime());
            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                item.setProductUniqueId(generatedKeys.getInt(1));
            }
            stmt.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
