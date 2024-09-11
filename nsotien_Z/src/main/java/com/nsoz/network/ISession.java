package com.nsoz.network;

/**
 * @author ASD
 *
 */
public interface ISession {

    public abstract boolean isConnected();

    public abstract void setHandler(IMessageHandler messageHandler);

    public abstract void setService(Service service);

    public abstract void sendMessage(Message message);

    public abstract void close();

}
