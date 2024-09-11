package com.nsoz.util;

import java.util.Timer;
import java.util.TimerTask;

public class ProgressBar {

    private String animation = "\\-/";
    private int width = 40;
    private double value = 0;
    private double maxRange = 100d;

    private int animationIndex = 0;
    private final Timer timer;
    private int flag = 0;
    private String name;
    private String extraMessge;
    private int textBarLength;

    public ProgressBar(String name, double maxRange) {
        this.name = name;
        this.maxRange = maxRange;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                animationIndex++;
                render();
            }
        }, 100, 100);
    }

    public void setExtraMessage(String extraMessage) {
        this.extraMessge = extraMessage.replaceAll("\n", " ");
    }

    public void setValue(double value) {
        this.value = value;
        this.render();
    }

    public void step() {
        this.value++;
        this.render();
    }

    public void reportSuccess() {
        this.flag = 1;
        this.render();
        this.stop();
    }

    public void reportError() {
        this.flag = -1;
        this.render();
        this.stop();
    }

    public void render() {
        if (textBarLength > 0) {
            String spaces = StringUtils.repeat(' ', textBarLength) + "\r";
            System.out.print(spaces);
        }
        int currentBlock = (int) (value / maxRange * width);
        char symbol = getSymbol();
        String filledBlock = StringUtils.repeat('#', currentBlock);
        String remainBlock = StringUtils.repeat('-', width - currentBlock);
        String text = String.format("%s [%s] [%s%s] %.0f/%.0f %s", this.name, symbol, filledBlock, remainBlock, value,
                maxRange, this.extraMessge == null ? "" : this.extraMessge);
        System.out.print(text);
        if (flag == 0) {
            System.out.print("\r");
        } else {
            System.out.print("\n");
        }
        textBarLength = text.length();

    }

    private char getSymbol() {
        switch (flag) {
            case 1:
                return 'V';
            case -1:
                return 'X';
            default:
                return animation.charAt(animationIndex % animation.length());
        }
    }

    public void stop() {
        timer.cancel();
    }
}
