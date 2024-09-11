package com.nsoz.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import lombok.Getter;
import lombok.Setter;

public class Message {

    @Setter
    @Getter
    private byte command;
    private ByteArrayOutputStream os;
    private DataOutputStream dos;
    private ByteArrayInputStream is;
    public DataInputStream dis;

    public Message(int command) {
        this((byte) command);
    }

    public Message(byte command) {
        this.command = command;
        os = new ByteArrayOutputStream();
        dos = new DataOutputStream(os);
    }

    public Message() {
        os = new ByteArrayOutputStream();
        dos = new DataOutputStream(os);
    }

    public Message(byte command, byte[] data) {
        this.command = command;
        is = new ByteArrayInputStream(data);
        dis = new DataInputStream(is);
    }

    public byte[] getData() {
        return os.toByteArray();
    }

    public DataInputStream reader() {
        return dis;

    }

    public DataOutputStream writer() {
        return dos;
    }

    public void cleanup() {
        try {
            if (dis != null) {
                dis.close();
            }
            if (dos != null) {
                dos.close();
            }
        } catch (IOException e) {
        }
    }

}
