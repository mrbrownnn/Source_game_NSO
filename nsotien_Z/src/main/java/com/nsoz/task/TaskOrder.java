package com.nsoz.task;

import com.nsoz.map.MapManager;
import com.nsoz.model.Char;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskOrder {

    public static final byte TASK_DAY = 0;
    public static final byte TASK_BOSS = 1;
    public static final byte TASK_GIOITHIEU = 2;
    public static final byte TASK_SUKIEN1 = 3;
    public static final byte TASK_SUKIEN2 = 4;
    public static final byte TASK_SUKIEN3 = 5;
    public static final byte TASK_SUKIEN4 = 6;
    public int taskId;
    public int count;
    public int maxCount;
    public String name;
    public String description;
    public int killId;
    public int mapId;
    public Char p;

    public TaskOrder(Char p, byte type) {
        this.p = p;
        this.taskId = type;
    }

    public TaskOrder(Char p, byte taskId, int count, int maxCount, int killId, int mapId) {
        this.p = p;
        this.count = count;
        this.maxCount = maxCount;
        this.taskId = taskId;
        this.killId = killId;
        this.mapId = mapId;
        switch (taskId) {

            case TASK_DAY:
                this.name = "Nhiệm vụ hàng ngày";
                break;

            case TASK_BOSS:
                this.name = "Nhiệm vụ truy bắt tà thú";
                break;
        }
        this.description = "Ghi chú: đi đến " + MapManager.getInstance().getTileMap(mapId).name + " để làm nhiệm vụ.";
    }

    public void setTask(int count, int maxCount, String name, String description, int killId, int mapId) {
        this.count = count;
        this.maxCount = maxCount;
        this.name = name;
        this.description = description;
        this.killId = killId;
        this.mapId = mapId;
    }

    public boolean isComplete() {
        return this.count >= this.maxCount;
    }

    public void updateTask(int count) {
        this.count += count;
        if (this.count > this.maxCount) {
            this.count = this.maxCount;
        }
        p.getService().updateTaskOrder(this);
    }
}
