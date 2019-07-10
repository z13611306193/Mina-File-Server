package com.fudian.mina.common;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author zyg
 */
public class UploadRequestBodyPack implements Serializable{

    private int length;

    private int action;

    private byte[] data;

    public UploadRequestBodyPack(int action, byte[] data) {
        this.action = action;
        this.data = data;
        this.length = 4 + 4 + data.length;
    }

    @Override
    public String toString() {
        return "UploadRequestBodyPack{" +
                "length=" + length +
                ", action=" + action +
                ", data=" + Arrays.toString(data) +
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

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}