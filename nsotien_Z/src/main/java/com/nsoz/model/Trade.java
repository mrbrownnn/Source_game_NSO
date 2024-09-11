package com.nsoz.model;

import com.nsoz.item.Item;

public class Trade {

    public Trader[] traders = new Trader[2];
    public boolean isFinish = false;

    public void openUITrade() {
        try {
            traders[0].player.getService().openUITrade(traders[1].player.name);
            traders[1].player.getService().openUITrade(traders[0].player.name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeUITrade() {
        try {
            Char _char1 = traders[0].player;
            Char _char2 = traders[1].player;
            _char1.getService().tradeCancel();
            _char2.getService().tradeCancel();
            _char1.cleanTrade();
            _char2.cleanTrade();
            isFinish = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tradeItemLock(Trader trader) {
        try {
            (trader == this.traders[0] ? this.traders[1] : this.traders[0]).player.getService().tradeItemLock(trader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void viewItemInfo(Char _char, byte type, byte index) {
        try {
            Trader trader = (_char == this.traders[0].player) ? this.traders[1] : this.traders[0];
            _char.getService().viewItemInfo(trader, type, index);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        try {
            if (!this.isFinish && traders[0].accept && traders[1].accept) {
                Char _char1 = traders[0].player;
                Char _char2 = traders[1].player;
                boolean isError = false;
                String error1 = "";
                String error2 = "";
                try {
                    int num = _char1.getSlotNull();
                    if (traders[1].itemTradeOrder.size() > num) {
                        isError = true;
                        error1 = "Hành trang của bạn không đủ chỗ trống";
                        error2 = "Hành trang của đối phương không đủ chỗ trống";
                        return;
                    }
                    num = _char2.getSlotNull();
                    if (traders[0].itemTradeOrder.size() > num) {
                        isError = true;
                        error2 = "Hành trang của bạn không đủ chỗ trống";
                        error1 = "Hành trang của đối phương không đủ chỗ trống";
                        return;
                    }
                    if (traders[0].coinTradeOrder > _char1.coin) {
                        isError = true;
                        error1 = "Bạn không đủ xu để giao dịch";
                        error2 = "Đối phương không đủ xu để giao dịch";
                        return;
                    }
                    if (traders[1].coinTradeOrder > _char2.coin) {
                        isError = true;
                        error2 = "Bạn không đủ xu để giao dịch";
                        error1 = "Đối phương không đủ xu để giao dịch";
                        return;
                    }
                    if (traders[0].coinTradeOrder > 500000000) {
                        isError = true;
                        error1 = "Số xu tối đa có thể giao dịch là 500.000.000xu.";
                        error2 = "Đối phương đã giao dịch quá giới hạn 500.000.000xu.";
                        return;
                    }
                    if (traders[1].coinTradeOrder > 500000000) {
                        isError = true;
                        error2 = "Số xu tối đa có thể giao dịch là 500.000.000xu.";
                        error1 = "Đối phương đã giao dịch quá giới hạn 500.000.000xu.";
                        return;
                    }
                    History history1 = new History(_char1.id, History.GIAO_DICH);
                    History history2 = new History(_char2.id, History.GIAO_DICH);
                    history1.setPartnerID(_char2.name);
                    history2.setPartnerID(_char1.name);
                    int numberItem = traders[1].itemTradeOrder.size();
                    if (numberItem > 0) {
                        for (Item item : traders[1].itemTradeOrder) {
                            int id = item.id;
                            int index = item.index;
                            int quantity = item.getQuantity();
                            if (_char2.bag[index] == null || _char2.bag[index].id != id
                                    || _char2.bag[index].getQuantity() != quantity || _char2.bag[index].isLock) {
                                isError = true;
                                error2 = "Vật phẩm ở ô " + (index + 1) + " không hợp lệ.";
                                error1 = "Đối phương giao dịch vật phẩm không hợp lệ.";
                                return;
                            }
                        }
                    }
                    numberItem = traders[0].itemTradeOrder.size();
                    if (numberItem > 0) {
                        for (Item item : traders[0].itemTradeOrder) {
                            int id = item.id;
                            int index = item.index;
                            int quantity = item.getQuantity();
                            if (_char1.bag[index] == null || _char1.bag[index].id != id
                                    || _char1.bag[index].getQuantity() != quantity || _char1.bag[index].isLock) {
                                isError = true;
                                error1 = "Vật phẩm ở ô " + (index + 1) + " không hợp lệ.";
                                error2 = "Đối phương giao dịch vật phẩm không hợp lệ.";
                                return;
                            }
                        }
                    }
                    numberItem = traders[1].itemTradeOrder.size();
                    if (numberItem > 0) {
                        for (Item item : traders[1].itemTradeOrder) {
                            int index = item.index;
                            int quantity = item.getQuantity();
                            if (_char2.bag[index] != null && _char2.bag[index].has(quantity)) {
                                history1.addItem(History.GIAO_DICH_NHAN, item);
                                history2.addItem(History.GIAO_DICH_GUI, item);
                                _char1.addItemToBag(item);
                                _char2.bag[index] = null;
                                _char2.getService().removeItem(index);
                            }
                        }
                    }
                    numberItem = traders[0].itemTradeOrder.size();
                    if (numberItem > 0) {
                        for (Item item : traders[0].itemTradeOrder) {
                            int index = item.index;
                            int quantity = item.getQuantity();
                            if (_char1.bag[index] != null && _char1.bag[index].has(quantity)) {
                                history1.addItem(History.GIAO_DICH_GUI, item);
                                history2.addItem(History.GIAO_DICH_NHAN, item);
                                _char2.addItemToBag(item);
                                _char1.bag[index] = null;
                                _char1.getService().removeItem(index);
                            }
                        }
                    }
                    history1.setBefore(_char1.coin, _char1.user.gold, _char1.yen);
                    history2.setBefore(_char2.coin, _char2.user.gold, _char2.yen);
                    _char1.coin += traders[1].coinTradeOrder - traders[0].coinTradeOrder;
                    if (_char1.coin > _char1.coinMax) {
                        _char1.coin = _char1.coinMax;
                    }
                    _char2.coin += traders[0].coinTradeOrder - traders[1].coinTradeOrder;
                    if (_char2.coin > _char2.coinMax) {
                        _char2.coin = _char2.coinMax;
                    }
                    history1.setAfter(_char1.coin, _char1.user.gold, _char1.yen);
                    history2.setAfter(_char2.coin, _char2.user.gold, _char2.yen);
                    long now = System.currentTimeMillis();
                    history1.setTime(now);
                    history2.setTime(now);
                    History.insert(history1);
                    History.insert(history2);

                    _char1.getService().tradeOk();
                    _char2.getService().tradeOk();
                    _char1.updateWithBalanceMessage();
                    _char2.updateWithBalanceMessage();
                    this.isFinish = true;
                } finally {
                    if (isError) {
                        closeUITrade();
                        _char1.serverMessage(error1);
                        _char2.serverMessage(error2);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
