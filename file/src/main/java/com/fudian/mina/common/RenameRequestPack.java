package com.fudian.mina.common;

import java.io.Serializable;

/**
 * Created by ZYGisComputer on 2019/7/2.
 */
public class RenameRequestPack implements Serializable {

    private int length;

    private int action;

    private String date;

    private String fileId;

    private String oldFileName;

    private String newFileName;

    public RenameRequestPack(int action, String date, String fileId, String oldFileName, String newFileName) {
        this.action = action;
        this.date = date;
        this.fileId = fileId;
        this.oldFileName = oldFileName;
        this.newFileName = newFileName;
        this.length = 4 + date.getBytes().length + fileId.getBytes().length + oldFileName.getBytes().length + newFileName.getBytes().length;
    }

    @Override
    public String toString() {
        return "RenameRequestPack{" +
                "length=" + length +
                ", action=" + action +
                ", date='" + date + '\'' +
                ", fileId='" + fileId + '\'' +
                ", oldFileName='" + oldFileName + '\'' +
                ", newFileName='" + newFileName + '\'' +
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getOldFileName() {
        return oldFileName;
    }

    public void setOldFileName(String oldFileName) {
        this.oldFileName = oldFileName;
    }

    public String getNewFileName() {
        return newFileName;
    }

    public void setNewFileName(String newFileName) {
        this.newFileName = newFileName;
    }
}
