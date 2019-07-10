package com.fudian.mina.common;

import java.io.Serializable;

/**
 * Created by ZYGisComputer on 2019/7/2.
 */
public class DownloadAndDeleteRequestPack implements Serializable {

    private int length;

    private int action;

    private String date;

    private String fileId;

    private String fileName;

    public DownloadAndDeleteRequestPack(int action,String date, String fileId, String fileName) {
        this.action = action;
        this.date = date;
        this.fileId = fileId;
        this.fileName = fileName;
        this.length = 4+4+date.getBytes().length+fileId.getBytes().length+fileName.getBytes().length;
    }

    @Override
    public String toString() {
        return "DownloadAndDeleteRequestPack{" +
                "length=" + length +
                ", action=" + action +
                ", date='" + date + '\'' +
                ", fileId='" + fileId + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
