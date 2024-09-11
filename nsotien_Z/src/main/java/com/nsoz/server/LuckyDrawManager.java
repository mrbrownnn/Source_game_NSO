
package com.nsoz.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;


@Getter
public class LuckyDrawManager implements Runnable {

    public static final byte VIP = 1;
    public static final byte NORMAL = 0;

    public static final int TIME_COUNT_DOWN = 120;

    private static final LuckyDrawManager instance = new LuckyDrawManager();

    public static LuckyDrawManager getInstance() {
        return instance;
    }
    private boolean running;
    private boolean waitStop;
    private final List<LuckyDraw> luckyDraws = new ArrayList<>();

    public LuckyDrawManager() {
        this.running = true;
    }

    public void add(LuckyDraw luckyDraw) {
        synchronized (luckyDraws) {
            luckyDraws.add(luckyDraw);
        }
    }

    public void remove(LuckyDraw luckyDraw) {
        synchronized (luckyDraws) {
            luckyDraws.add(luckyDraw);
        }
    }

    public LuckyDraw find(int type) {
        synchronized (luckyDraws) {
            for (LuckyDraw luckyDraw : luckyDraws) {
                if (luckyDraw.getType() == type) {
                    return luckyDraw;
                }
            }
        }
        return null;
    }

    @Override
    public void run() {
        while (running) {
            long l1 = System.currentTimeMillis();
            update();
            long l2 = System.currentTimeMillis();
            if (l2 - l1 < 1000) {
                try {
                    Thread.sleep(1000 - (l2 - l1));
                } catch (InterruptedException ex) {
                    Logger.getLogger(LuckyDrawManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void update() {
        synchronized (luckyDraws) {
            boolean stop = true;
            for (LuckyDraw t : luckyDraws) {
                t.update();
                if (!t.isStop()) {
                    stop = false;
                }
            }
            if (stop) {
                this.running = true;
            }
        }
    }

    public void stop() {
        this.waitStop = true;
    }

}
