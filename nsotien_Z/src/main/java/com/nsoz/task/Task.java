
package com.nsoz.task;

import java.util.ArrayList;

/**
 *
 * @author PC
 */
public class Task {

    public static ArrayList<TaskTemplate> arrTaskTemplate;

    public static TaskTemplate getTaskTemplate(int id) {
        for (TaskTemplate task : arrTaskTemplate) {
            if (task.getTaskId() == id) {
                return task;
            }
        }
        return null;
    }

    public int index;
    public short taskId;
    public short count;
    public TaskTemplate template;

    public Task(short taskId, byte index, short count) {
        this.taskId = taskId;
        this.index = index;
        this.count = count;
        this.template = getTaskTemplate(taskId);
    }

    public boolean isComplete() {
        return index >= template.getSubNames().length - 1;
    }
}
