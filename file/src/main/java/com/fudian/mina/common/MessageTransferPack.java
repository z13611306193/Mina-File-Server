package com.fudian.mina.common;

import java.io.Serializable;

/**
 * Created by ZYGisComputer on 2019/7/2.
 */
public class MessageTransferPack implements Serializable{

    private int length;

    private int action;

    private String message;

    public MessageTransferPack(int action, String message) {
        this.action = action;
        this.message = message;
        this.length = 4+4+4+message.getBytes().length;
    }

    @Override
    public String toString() {
        return "MessageTransferPack{" +
                "length=" + length +
                ", action=" + action +
                ", message='" + message + '\'' +
                '}';
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
