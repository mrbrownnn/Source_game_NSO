
package com.nsoz.model;

import com.nsoz.constants.ItemName;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;


public class MountDataManager {

    private static final MountDataManager instance = new MountDataManager();

    public static MountDataManager getInstance() {
        return instance;
    }

    @Getter
    private final List<MountData> mountDatas = new ArrayList<>();

    public void add(MountData mount) {
        mountDatas.add(mount);
    }

    public void remove(MountData mount) {
        mountDatas.remove(mount);
    }

    public MountData find(int itemID) {
        for (MountData m : mountDatas) {
            if (m.getItemID() == itemID) {
                return m;
            }
        }
        return null;
    }

    public void init() {
        MountData hacNguu = new MountData(ItemName.HAC_NGUU);
        hacNguu.addFrameStand((short) 3049);
        hacNguu.addFrameStand((short) 3050);
        hacNguu.addFrameMove((short) 3051);
        hacNguu.addFrameMove((short) 3051);
        hacNguu.addFrameMove((short) 3052);
        hacNguu.addFrameMove((short) 3052);
        hacNguu.addFrameMove((short) 3053);
        hacNguu.addFrameMove((short) 3053);
        hacNguu.addFrameJumpUp((short) 3054);
        hacNguu.addFrameJumpDown((short) 3055);
        hacNguu.addFrameDead((short) 3056);
        hacNguu.addFrameBehind((short) 3049);
        hacNguu.addFrameBehind((short) 3049);
        hacNguu.addFrameBehind((short) 3049);
        hacNguu.addFrameBehind((short) 3050);
        hacNguu.addFrameBehind((short) 3050);
        hacNguu.addFrameBehind((short) 3050);
        add(hacNguu);

        MountData kimNguu = new MountData(ItemName.KIM_NGUU);
        kimNguu.addFrameStand((short) 3057);
        kimNguu.addFrameStand((short) 3058);
        kimNguu.addFrameMove((short) 3059);
        kimNguu.addFrameMove((short) 3059);
        kimNguu.addFrameMove((short) 3060);
        kimNguu.addFrameMove((short) 3060);
        kimNguu.addFrameMove((short) 3061);
        kimNguu.addFrameMove((short) 3061);
        kimNguu.addFrameMove((short) 3062);
        kimNguu.addFrameMove((short) 3062);
        kimNguu.addFrameJumpUp((short) 3063);
        kimNguu.addFrameJumpDown((short) 3064);
        kimNguu.addFrameDead((short) 3065);
        kimNguu.addFrameBehind((short) 3057);
        kimNguu.addFrameBehind((short) 3057);
        kimNguu.addFrameBehind((short) 3057);
        kimNguu.addFrameBehind((short) 3078);
        kimNguu.addFrameBehind((short) 3058);
        kimNguu.addFrameBehind((short) 3058);
        add(kimNguu);
    }
}
