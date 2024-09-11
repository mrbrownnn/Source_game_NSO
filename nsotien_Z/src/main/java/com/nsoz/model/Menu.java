
package com.nsoz.model;

import lombok.Setter;
import lombok.Getter;

/**
 *
 * @author Admin
 */
@Getter
public class Menu {

    private int id;
    private String name;
    private Object extra;

    @Setter
    private Runnable runnable;

    public Menu(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Menu(int id, String name, Runnable runnable) {
        this.id = id;
        this.name = name;
        this.runnable = runnable;
    }

    public Menu(int id, String name, Object extra) {
        this.id = id;
        this.name = name;
        this.extra = extra;
    }

    public int getIntExtra() {
        return (int) extra;
    }

    public void confirm() {
        if (runnable != null) {
            runnable.run();
        }
    }
}
