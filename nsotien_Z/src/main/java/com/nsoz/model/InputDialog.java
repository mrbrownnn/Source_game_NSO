
package com.nsoz.model;

import com.mysql.cj.util.StringUtils;
import lombok.Data;

/**
 *
 * @author Admin
 */
@Data
public class InputDialog {

    private int id;
    private String title;
    private String text;
    private Runnable runnable;

    public InputDialog(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public InputDialog(int id, String title, Runnable runnable) {
        this.id = id;
        this.title = title;
        this.runnable = runnable;
    }

    public int intValue() throws NumberFormatException {
        return Integer.parseInt(getText());
    }

    public boolean isEmpty() {
        return StringUtils.isEmptyOrWhitespaceOnly(getText());
    }

    public void execute() {
        if (runnable != null) {
            runnable.run();
        }
    }
}
