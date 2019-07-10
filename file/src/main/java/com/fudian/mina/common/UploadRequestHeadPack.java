package com.fudian.mina.common;

import java.io.Serializable;

/**
 * @author zyg
 */
public class UploadRequestHeadPack implements Serializable{

    private int length;

    private int action;

    private String fileId;

    private String fileName;

    private long fileSize;

    private String fileType;

    public UploadRequestHeadPack(int action, String fileId, String fileName, long fileSize, String fileType) {
        this.action = action;
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.length = 4 + 4 + 8 +fileId.getBytes().length + fileName.getBytes().length + fileType.getBytes().length;
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

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @Override
    public String toString() {
        return "CustomPack{" +
                "length=" + length +
                ", action=" + action +
                ", fileId='" + fileId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", fileType='" + fileType + '\'' +
                '}';
    }
}