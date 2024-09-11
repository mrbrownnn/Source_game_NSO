package com.nsoz.server;

import com.nsoz.util.Log;
import com.nsoz.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import lombok.Getter;

/**
 *
 * @author Administrator
 */
@Getter
public class Config {

    private static final Config instance = new Config();
    private boolean vxmm;

    public static Config getInstance() {
        return instance;
    }

    // Version
    private int dataVersion = 26;
    private int itemVersion = 26;
    private int mapVersion = 26;
    private int skillVersion = 26;
    private boolean isTestVersion = false;

    // Server
    private int serverID;
    private int port;

    // MySql
    private String dbHost;
    private int dbPort;
    private String dbUser;
    private String dbPassword;
    private String dbName;
    private String dbDriver;
    private int dbMinConnections;
    private int dbMaxConnections;
    private int dbConnectionTimeout;
    private int dbLeakDetectionThreshold;
    private int dbIdleTimeout;

    // MongoDB
    private String mongodbHost;
    private int mongodbPort;
    private String mongodbName;
    private String mongodbUser;
    private String mongodbPassword;

    // Socket.IO
    private String websocketHost;
    private int websocketPort;

    // Game
    private double maxPercentAdd;
    private int sale;
    private boolean showLog;
    private boolean shinwa;
    private int shinwaFee;
    private int auctionMax;
    private int shinwaMax;
    private boolean arena;
    private int ipAddressLimit;
    private int maxQuantity;
    private String serverDir;
    private String notification;
    private int messageSizeMax;
    private String event;

    //


    public boolean load() {
        try {
            FileInputStream input = new FileInputStream(new File("config.properties"));
            Properties props = new Properties();
            props.load(new InputStreamReader(input, StandardCharsets.UTF_8));
            props.forEach((t, u) -> {
                Log.info(String.format("Config - %s: %s", t, u));
            });
            showLog = Boolean.parseBoolean(props.getProperty("server.log.display"));
            serverID = Integer.parseInt(props.getProperty("server.id"));
            port = Integer.parseInt(props.getProperty("server.port"));
            dbDriver = props.getProperty("db.driver");
            dbHost = props.getProperty("db.host");
            dbPort = Integer.parseInt(props.getProperty("db.port"));
            dbUser = props.getProperty("db.user");
            dbPassword = props.getProperty("db.password");
            dbName = props.getProperty("db.dbname");
            dbMaxConnections = Integer.parseInt(props.getProperty("db.maxconnections"));
            dbMinConnections = Integer.parseInt(props.getProperty("db.minconnections"));
            dbConnectionTimeout = Integer.parseInt(props.getProperty("db.connectionTimeout"));
            dbLeakDetectionThreshold = Integer.parseInt(props.getProperty("db.leakDetectionThreshold"));
            dbIdleTimeout = Integer.parseInt(props.getProperty("db.idleTimeout"));
            mongodbHost = props.getProperty("mongodb.host");
            mongodbPort = Integer.parseInt(props.getProperty("mongodb.port"));
            mongodbUser = props.getProperty("mongodb.user");
            mongodbPassword = props.getProperty("mongodb.password");
            mongodbName = props.getProperty("mongodb.dbname");
            websocketHost = props.getProperty("websocket.host");
            websocketPort = Integer.parseInt(props.getProperty("websocket.port"));
            maxPercentAdd = Integer.parseInt(props.getProperty("game.upgrade.percent.add"));
            sale = Integer.parseInt(props.getProperty("game.store.discount"));
            shinwa = Boolean.parseBoolean(props.getProperty("game.shinwa.active"));
            shinwaFee = Integer.parseInt(props.getProperty("game.shinwa.fee"));
            auctionMax = Integer.parseInt(props.getProperty("game.shinwa.max"));
            shinwaMax = Integer.parseInt(props.getProperty("game.shinwa.player.max"));
            arena = Boolean.parseBoolean(props.getProperty("game.arena.active"));
            ipAddressLimit = Integer.parseInt(props.getProperty("game.login.limit"));
            maxQuantity = Integer.parseInt(props.getProperty("game.quantity.display.max"));
            if (props.containsKey("server.resources.dir")) {
                serverDir = props.getProperty("server.resources.dir");
            } else {
                serverDir = System.getProperty("user.dir");
            }
            if (props.containsKey("server.notification")) {
                notification = props.getProperty("server.notification");
            }
            if (props.containsKey("game.data.version")) {
                dataVersion = Integer.parseInt(props.getProperty("game.data.version"));
            }
            if (props.containsKey("game.item.version")) {
                itemVersion = Integer.parseInt(props.getProperty("game.item.version"));
            }
            if (props.containsKey("game.map.version")) {
                mapVersion = Integer.parseInt(props.getProperty("game.map.version"));
            }
            if (props.containsKey("game.skill.version")) {
                skillVersion = Integer.parseInt(props.getProperty("game.skill.version"));
            }
            if (props.containsKey("client.data.size.max")) {
                messageSizeMax = Integer.parseInt(props.getProperty("client.data.size.max"));
            } else {
                messageSizeMax = 1024;
            }
            if (props.containsKey("game.event")) {
                event = props.getProperty("game.event");
            }

            if (props.containsKey("game.isTest")) {
                isTestVersion = Boolean.parseBoolean(props.getProperty("game.isTest"));
            }


            if (props.containsKey("open.vxmm")) {
                vxmm = Boolean.parseBoolean(props.getProperty("open.vxmm"));
            }else{
                vxmm = false;
            }


        } catch (IOException | NumberFormatException ex) {
            Log.error("load config err: " + ex.getMessage(), ex);
            return false;
        }
        return true;
    }

    public boolean isTestVersion(){
        return this.isTestVersion;
    }
    public String getJdbcUrl() {
        return "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
    }

    public boolean isOpenVxmm() {
        return this.vxmm;
    }

    public String getMongodbUrl() {
        if (!StringUtils.isNullOrEmpty(mongodbUser) && !StringUtils.isNullOrEmpty(mongodbPassword)) {
            return String.format("mongodb://%s:%s@%s:%d/%s", mongodbUser, mongodbPassword, mongodbHost, mongodbPort, mongodbName);
        }
        return String.format("mongodb://%s:%d", mongodbHost, mongodbPort);
    }

}
