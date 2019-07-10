package com.fudian.mina.common;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author zyg
 */
public class CustomPack<T extends Object> implements Serializable{

    private int length;

    private T bean;

    public CustomPack(T bean) throws IOException {
        this.bean = bean;
        this.length = 4 + Serializer.serialize(bean).length;
    }

    @Override
    public String toString() {
        return "CustomPack{" +
                "length=" + length +
                ", bean=" + bean +
                '}';
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public T getBean() {
        return bean;
    }

    public void setBean(T bean) {
        this.bean = bean;
    }
}