package com.nsoz.model;

public class Part {

    public Part(byte type) {
        this.type = type;
        if (type == 0) {
            this.pi = new PartImage[8];
        }
        if (type == 1) {
            this.pi = new PartImage[18];
        }
        if (type == 2) {
            this.pi = new PartImage[10];
        }
        if (type == 3) {
            this.pi = new PartImage[2];
        }
    }

    public byte type;
    public PartImage[] pi;
}
