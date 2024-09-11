
package com.nsoz.model;

import com.nsoz.network.Message;

public interface IChat {

    public void read(Message ms);

    public void wordFilter();

    public void send();
}
