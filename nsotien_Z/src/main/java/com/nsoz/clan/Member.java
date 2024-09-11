
package com.nsoz.clan;

import com.nsoz.model.Char;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Admin
 */
@Setter
@Getter
public class Member {

    private int id;
    private int classId;
    private int level;
    private int type;
    private String name;
    private int pointClan;
    private int pointClanWeek;
    private boolean online;
    private Char p;
    @Getter
    @Setter
    private boolean saving;

    @Builder
    public Member(int id, int classId, int level, int type, String name, int pointClan, int pointClanWeek) {
        this.id = id;
        this.classId = classId;
        this.level = level;
        this.type = type;
        this.name = name;
        this.pointClan = pointClan;
        this.pointClanWeek = pointClanWeek;
    }

    public void setChar(Char p) {
        this.p = p;
    }

    public Char getChar() {
        return p;
    }

    public void addPointClan(int point) {
        this.pointClan += point;
    }

    public void addPointClanWeek(int point) {
        this.pointClanWeek += point;
    }
}
