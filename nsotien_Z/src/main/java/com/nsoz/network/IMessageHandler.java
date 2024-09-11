package com.nsoz.network;

public interface IMessageHandler {

    public void onMessage(Message message);

    public void onConnectionFail();

    public void onDisconnected();

    public void onConnectOK();

    public void messageNotMap(Message ms);

    public void messageSubCommand(Message ms);

    public void messageNotLogin(Message ms);

    public void newMessage(Message ms);

}
