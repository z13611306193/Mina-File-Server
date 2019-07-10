package com.fudian.mina.common;

/**
 * @author zyg
 * Transport convention class
 */
public class Constants {

    /**
     * SERVER RECEIVED
     */

    // server received download flag
    public static final int SERVER_RECEIVED_DOWNLOAD = -400;

    // server received upload head flag
    public static final int SERVER_RECEIVED_UPLOAD_HEAD = 0;

    // server received upload body flag
    // public static final int SERVER_RECEIVED_UPLOAD_BODY = 1-N;

    // server received upload tail flag
    public static final int SERVER_RECEIVED_UPLOAD_TAIL = -1;

    // server received download request key
    public static final String SERVER_RECEIVED_DOWNLOAD_REQUEST_KEY = "downloadRequest";

    // server received download date key
    public static final String SERVER_RECEIVED_DOWNLOAD_DATE_KEY = "downloadDate";

    // server received download filename key
    public static final String SERVER_RECEIVED_DOWNLOAD_FILENAME_KEY = "downloadFileName";

    // server received download uuid key
    public static final String SERVER_RECEIVED_DOWNLOAD_UUID_KEY = "downloadUuid";

    //server received upload filename key
    public static final String SERVER_RECEIVED_UPLOAD_FILENAME_KEY = "uploadFileName";

    //server received upload content key
    public static final String SERVER_RECEIVED_UPLOAD_CONTENT_KEY = "uploadContent";

    //server received upload request key
    public static final String SERVER_RECEIVED_UPLOAD_REQUEST_KEY = "uploadRequest";

    /**
     * SERVER SEND
     */

    // server send server error flag
    public static final int SERVER_SEND_SERVER_ERROR = -500;

    // server send download head flag
    public static final int SERVER_SEND_DOWNLOAD_HEAD = 0;

    // server send download body flag
    // public static final int SERVER_SEND_DOWNLOAD_BODY = 1-N;

    // server send download tail flag
    public static final int SERVER_SEND_DOWNLOAD_TAIL = -2;

    // server send message flag
    public static final int SERVER_SEND_MESSAGE = -100;

    // server send uuid flag
    public static final int SERVER_SEND_UUID = -1;

    // server send file upload success flag
    public static final int SERVER_SEND_FILE_UPLOAD_SUCCESS = -200;

    //server send download filename key
    public static final String SERVER_SEND_DOWNLOAD_FILENAME_KEY = "downloadFileName";

    //server send download content key
    public static final String SERVER_SEND_DOWNLOAD_CONTENT_KEY = "downloadContent";

    //server send download request key
    public static final String SERVER_SEND_DOWNLOAD_REQUEST_KEY = "downloadRequest";

    /**
     * CLIENT RECEIVED
     */

    // client received server error flag
    public static final int CLIENT_RECEIVED_SERVER_ERROR = -500;

    // client received download head flag
    public static final int CLIENT_RECEIVED_DOWNLOAD_HEAD = 0;

    // client received download body flag
    // public static final int CLIENT_RECEIVED_DOWNLOAD_BODY = 1-N;

    // client received download tail flag
    public static final int CLIENT_RECEIVED_DOWNLOAD_TAIL = -2;

    // client received message flag
    public static final int CLIENT_RECEIVED_MESSAGE = -100;

    // client received uuid flag
    public static final int CLIENT_RECEIVED_UUID = -1;

    // client received file upload success flag
    public static final int CLIENT_RECEIVED_FILE_UPLOAD_SUCCESS = -200;

    //client received download filename key
    public static final String CLIENT_RECEIVED_DOWNLOAD_FILENAME_KEY = "downloadFileName";

    //client received download content key
    public static final String CLIENT_RECEIVED_DOWNLOAD_CONTENT_KEY = "downloadContent";

    //client received download request key
    public static final String CLIENT_RECEIVED_DOWNLOAD_REQUEST_KEY = "downloadRequest";

    /**
     * CLIENT SEND
     */

    // client send download flag
    public static final int CLIENT_SEND_DOWNLOAD = -400;

    // client send upload head flag
    public static final int CLIENT_SEND_UPLOAD_HEAD = 0;

    // client send upload body flag
    // public static final int CLIENT_SEND_UPLOAD_BODY = 1-N;

    // client send upload tail flag
    public static final int CLIENT_SEND_UPLOAD_TAIL = -1;

    // client send download request key
    public static final String CLIENT_SEND_DOWNLOAD_REQUEST_KEY = "downloadRequest";

    // client send download date key
    public static final String CLIENT_SEND_DOWNLOAD_DATE_KEY = "downloadDate";

    // client send download filename key
    public static final String CLIENT_SEND_DOWNLOAD_FILENAME_KEY = "downloadFileName";

    // client send download uuid key
    public static final String CLIENT_SEND_DOWNLOAD_UUID_KEY = "downloadUuid";

    //client send upload filename key
    public static final String CLIENT_SEND_UPLOAD_FILENAME_KEY = "uploadFileName";

    //client send upload content key
    public static final String CLIENT_SEND_UPLOAD_CONTENT_KEY = "uploadContent";

    //client send upload request key
    public static final String CLIENT_SEND_UPLOAD_REQUEST_KEY = "uploadRequest";

}
