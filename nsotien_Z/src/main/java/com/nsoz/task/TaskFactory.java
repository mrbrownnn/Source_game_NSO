package com.nsoz.task;

import com.nsoz.map.Map;
import com.nsoz.map.MapManager;
import com.nsoz.model.Char;
import com.nsoz.util.NinjaUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskFactory {

    private static final TaskFactory instance = new TaskFactory();

    public static TaskFactory getInstance() {
        return instance;
    }

    private final List<MobInfo> taskBosss = new ArrayList<>();
    private final List<MobInfo> taskDays = new ArrayList<>();

    public void addMobInfoTaskBoss(MobInfo info) {
        if (taskBosss.stream().filter(m -> m.getMobID() == info.getMobID() && m.getLevel() == info.getLevel()
                && m.getMapID() == info.getMapID()).count() == 0) {
            taskBosss.add(info);
        }
    }

    public void addMobInfoTaskDay(MobInfo info) {
        if (taskDays.stream().filter(m -> m.getMobID() == info.getMobID() && m.getLevel() == info.getLevel()
                && m.getMapID() == info.getMapID()).count() == 0) {
            taskDays.add(info);
        }
    }

    public int getLevellMobInfoClosestLevel(int level) {
        int levelMob = 0;
        int levelMin = -1;
        for (MobInfo info : taskDays) {
            if (levelMin == -1 || Math.abs(info.getLevel() - level) < levelMin) {
                levelMin = Math.abs(info.getLevel() - level);
                levelMob = info.getLevel();
            }
        }
        return levelMob;
    }

    public MobInfo getMobInfoTaskBossClosestLevel(int level) {
        MobInfo info = null;
        int min = -1;
        for (MobInfo mobInfo : taskBosss) {
            if (min == -1 || Math.abs(mobInfo.getLevel() - level) < min) {
                min = Math.abs(mobInfo.getLevel() - level);
                info = mobInfo;
            }
        }
        return info;
    }

    public MobInfo randomMobInfoTaskDay(int level) {
        List<MobInfo> list = taskDays.stream().filter((mobInfo) -> (Math.abs(mobInfo.getLevel() - level) <= 5))
                .collect(Collectors.toList());
        int size = list.size();
        int rd = NinjaUtils.nextInt(size);
        return list.get(rd);
    }

    public TaskOrder createTaskOrder(byte type, Char p) {
        TaskOrder task = new TaskOrder(p, type);
        if (type == TaskOrder.TASK_DAY) {
            int level = getLevellMobInfoClosestLevel(p.level);
            MobInfo info = randomMobInfoTaskDay(level);
            task.setTask(0, (int)NinjaUtils.nextInt(20, 40), "Nhiệm vụ hàng ngày",
                    "Ghi chú: đi đến " + MapManager.getInstance().getTileMap(info.getMapID()).name + " để làm nhiệm vụ.",
                    info.getMobID(), info.getMapID());
        } else if (type == TaskOrder.TASK_BOSS) {
            MobInfo info = getMobInfoTaskBossClosestLevel(p.level);
            task.setTask(0, 1, "Nhiệm vụ truy bắt tà thú",
                    "Ghi chú: đi đến " + MapManager.getInstance().getTileMap(info.getMapID()).name + " để làm nhiệm vụ.",
                    info.getMobID(), info.getMapID());
        }
        return task;
    }

    public Task createTask(short taskID, byte taskIndex, short taskCount) {
        return new Task(taskID, taskIndex, taskCount);
    }
}
