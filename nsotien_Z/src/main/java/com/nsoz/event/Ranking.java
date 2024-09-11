package com.nsoz.event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Comparator;
import java.util.Vector;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.nsoz.db.jdbc.DbManager;
import com.nsoz.model.Char;
import com.nsoz.server.Config;
import com.nsoz.util.Log;

import java.util.ArrayList;

public class Ranking {

    private static Vector<ListLeaderBoard> listLeaderBoard = new Vector<>();

    public static void loadListLeaderBoard() {
        try {
            Log.info("Loading list leader board");
            for (int i = 0; i < 3; i++) {
                ListLeaderBoard newList = new ListLeaderBoard(i);
                Connection conn = DbManager.getInstance().getConnection(DbManager.SERVER);
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT `name`, CAST(JSON_EXTRACT(event_point, \"$[" + i
                        + "]\") AS INT) AS `eventPoint` FROM players where `server_id` = ? ORDER BY `eventPoint` DESC LIMIT ?;");
                stmt.setInt(1, Config.getInstance().getServerID());
                stmt.setInt(2, newList.max);
                ResultSet res = stmt.executeQuery();
                listLeaderBoard.add(newList);

                while (res.next()) {
                    String name = res.getString("name");
                    int point = res.getInt("eventPoint");
                    newList.leaders.add(new LeaderBoard(name, point));
                }
                res.close();
                stmt.close();

                newList.sortAndGetLowestScore();
            }
            Log.info("Load list leader board successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateLeaderBoard(Char _char, int type) {
        ListLeaderBoard list = listLeaderBoard.get(type);
        if (list != null) {
            list.updateLeaderBoard(_char);
        }
    }

    public static void showLeaderBoard(Char _char, int type, String format) {
        ListLeaderBoard list = listLeaderBoard.get(type);
        if (list != null) {
            list.showLeaderBoard(_char, format);
        }
    }

}

class ListLeaderBoard {

    protected ArrayList<LeaderBoard> leaders;
    private ReadWriteLock lock;
    private int lowestScore = 0;
    private int type = 0;
    protected int max;

    public ListLeaderBoard(int type) {
        this.type = type;
        this.max = 20;
        this.leaders = new ArrayList<>();
        this.lock = new ReentrantReadWriteLock();
    }

    public void sort() {
        lock.writeLock().lock();
        try {
            leaders.sort(new PointSorter());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void updateLeaderBoard(Char _char) {
//        int charEventPoint = _char.eventPoints[type];
//        if (charEventPoint > 0) {
//            int index = getIndexByChar(_char);
//            try {
//                lock.writeLock().lock();
//                if (index == -1) {
//                    if (leaders.size() < max) {
//                        leaders.add(new LeaderBoard(_char.name, charEventPoint));
//                    } else {
//                        if (charEventPoint > lowestScore) {
//                            leaders.remove(max - 1);
//                            leaders.add(new LeaderBoard(_char.name, charEventPoint));
//                        }
//                    }
//                } else {
//                    leaders.get(index).point = charEventPoint;
//                }
//            } finally {
//                lock.writeLock().unlock();
//            }
//            sortAndGetLowestScore();
//        }
    }

    public void sortAndGetLowestScore() {
        if (leaders.size() > 0) {
            sort();
            lowestScore = leaders.get(leaders.size() - 1).point;
        }

    }

    public int getIndexByChar(Char _char) {
        lock.readLock().lock();
        try {
            int i = 0;
            for (LeaderBoard leader : leaders) {
                if (leader.name.equals(_char.name)) {
                    return i;
                }
                i++;
            }
            return -1;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void showLeaderBoard(Char _char, String format) {
        lock.readLock().lock();
        try {
            StringBuilder sb = new StringBuilder();
            int n = 1;
            for (LeaderBoard leader : leaders) {
                sb.append(n).append(". ").append(leader.name).append(" ").append(String.format(format, leader.point)).append("\n");
                n++;
            }
            _char.getService().showAlert("TOP SỰ KIỆN", sb.toString());
        } finally {
            lock.readLock().unlock();
        }
    }
}

class LeaderBoard {

    public LeaderBoard(String name, int point) {
        this.name = name;
        this.point = point;
    }

    public String name;
    public Integer point;
}

class PointSorter implements Comparator<LeaderBoard> {

    @Override
    public int compare(LeaderBoard o1, LeaderBoard o2) {
        return o2.point.compareTo(o1.point);
    }
}
