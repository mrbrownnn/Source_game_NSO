
package com.nsoz.event.eventpoint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * Được share bởi Youtube : nsotien tv
 */
@Setter
@Getter
@AllArgsConstructor

public class Point {

    private String key;
    private int point;
    private int rewarded;

    public void addPoint(int point) {
        this.point += point;
    }

    public void subPoint(int point) {
        this.point -= point;
    }
}
