package com.bin.rpc.rpcservice.impl;

import com.bin.rpc.rpcservice.ShowMessage;

public class ShowMessageImpl implements ShowMessage {
    @Override
    public String showMessage(String msg) {
        return "message-INFO:" + msg;
    }
}
