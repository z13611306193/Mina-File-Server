package com.fudian.mina.common;

/**
 * @author zyg
 * Road king Exception
 */
public class RoadNotExistException extends Exception {

    public RoadNotExistException() {
        super("The specified upload path does not exist, please check and try again!");
    }

}
