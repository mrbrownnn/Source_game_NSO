
package com.nsoz.lib;

/**
 *
 * Được share bởi Youtube : nsotien tv
 */
public class Resource {

    private long timeRemoveResource;
    private byte[] data;
    private long createdAt;

    public Resource(byte[] data, long timeRemoveResource) {
        this.data = data;
        this.timeRemoveResource = timeRemoveResource;
        this.createdAt = System.currentTimeMillis();
    }

    public boolean isExpired() {
        //return (System.currentTimeMillis() - createdAt) > timeRemoveResource;
        return false;
    }

    public byte[] getData() {
        return this.data;
    }
}
