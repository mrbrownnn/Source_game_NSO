
package com.nsoz.event.eventpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * Được share bởi Youtube : nsotien tv
 */
@Setter
@Getter
public class EventPoint {

    public static final String DIEM_TIEU_XAI = "spending_point";

    private int id;
    private List<Point> points;
    private String playerName;
    private int playerID;

    public EventPoint() {
        this.points = new ArrayList<>();
    }

    public void addIfMissing(Set<String> keys) {
        for (String key : keys) {
            Point p = find(key);
            if (p == null) {
                add(new Point(key, 0, 0));
            }
        }
    }

    public void add(Point point) {
        this.points.add(point);
    }

    public void remove(Point point) {
        this.points.remove(point);
    }

    public boolean addPoint(String key, int point) {
        Point p = find(key);
        if (p != null) {
            p.addPoint(point);
            return true;
        }
        return false;
    }

    public boolean subPoint(String key, int point) {
        Point p = find(key);
        if (p != null) {
            p.subPoint(point);
            return true;
        }
        return false;
    }

    public boolean setPoint(String key, int point) {
        Point p = find(key);
        if (p != null) {
            p.setPoint(point);
            return true;
        }
        return false;
    }

    public int getPoint(String key) {
        Point p = find(key);
        if (p != null) {
            return p.getPoint();
        }
        return 0;
    }

    public boolean setRewarded(String key, int reward) {
        Point p = find(key);
        if (p != null) {
            p.setRewarded(reward);
            return true;
        }
        return false;
    }

    public int getRewarded(String key) {
        Point p = find(key);
        if (p != null) {
            return p.getRewarded();
        }
        return -1;
    }

    public Point find(String key) {
        for (Point p : points) {
            if (p.getKey().equals(key)) {
                return p;
            }
        }
        return null;
    }
}
