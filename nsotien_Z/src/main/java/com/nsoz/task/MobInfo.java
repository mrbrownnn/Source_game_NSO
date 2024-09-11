package com.nsoz.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class MobInfo {

    private int mapID;
    private int mobID;
    private int level;

}
