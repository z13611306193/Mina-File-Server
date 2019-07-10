package com.fudian.mina.client;

import com.fudian.mina.common.*;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * @author zyg
 *         Client request handler
 */
public class MyClientHandler extends IoHandlerAdapter {

    private static ExecutorService mExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    @Override
    public void messageReceived(final IoSession session, Object message) throws Exception {
        CustomPack pack = (CustomPack) message;

        if (pack.getBean() instanceof UploadRequestBodyPack) {//接收下载数据
            UploadRequestBodyPack uploadRequestBodyPack = (UploadRequestBodyPack) pack.getBean();
            if (uploadRequestBodyPack.getAction() == 202) {
                FileOutputStream outputStream = (FileOutputStream) session.getAttribute(session.getRemoteAddress());
                outputStream.write(uploadRequestBodyPack.getData());
            }
        } else if (pack.getBean() instanceof MessageTransferPack) {// 消息接收
            MessageTransferPack messageTransferPack = (MessageTransferPack) pack.getBean();
            System.out.println(messageTransferPack.getMessage());
            if(messageTransferPack.getAction()==102){
                session.setAttribute("headResult",messageTransferPack.getMessage());
            }else if(messageTransferPack.getAction()==104){
                session.setAttribute("bodyResult",messageTransferPack.getMessage());
            } else if (messageTransferPack.getAction() == 106) {
                MinaClientCustom.DeleteData deleteData = (MinaClientCustom.DeleteData) session.getAttribute("deleteFile");
                if (deleteData != null) {
                    mExecutorService.execute(new FutureTask<>(deleteData));
                }
                LinkedList<File> fileList= (LinkedList<File>) session.getAttribute("fileList");
                if(fileList!=null&&fileList.size()!=0){
                    File file = fileList.removeFirst();
                    String fileId = UUID.randomUUID().toString().replaceAll("-", "");
                    String fileName = file.getName();
                    int j = fileName.lastIndexOf(".");
                    String type = fileName.substring(j+1);
                    fileName = fileName.substring(0, j);
                    long fileSize = file.length();
                    mExecutorService.execute(new MinaClientCustom.SendData(session, file,fileId,fileName,type,fileSize));
                    session.setAttribute("fileList", fileList);
                }
            } else if (messageTransferPack.getAction() == 199) {
                MinaClientCustom.RenameData renameData = (MinaClientCustom.RenameData) session.getAttribute("renameFile");
                if (renameData != null) {
                    System.out.println("文件替换失败");
                    mExecutorService.execute(renameData);
                }
            } else if (messageTransferPack.getAction() == 204) {
                FileOutputStream outputStream = (FileOutputStream) session.getAttribute(session.getRemoteAddress());
                outputStream.flush();
                outputStream.close();
                System.out.println("文件下载成功!");
                LinkedList<String> fileNames = (LinkedList<String>) session.getAttribute("fileNames");
                if(fileNames!=null&&fileNames.size()!=0){
                    String date = "201907";
                    String s = fileNames.removeFirst();
                    String fileId = s.substring(0, s.indexOf("_"));
                    String fileName = s.substring(s.indexOf("_") + 1);
                    mExecutorService.execute(new MinaClientCustom.ReceivedData(session,date,fileName,fileId));
                    session.setAttribute("fileNames",fileNames);
                }
            } else if (messageTransferPack.getAction() == 302) {
                if (session.getAttribute("deleteFile") != null) {
                    System.out.println("文件替换成功,缓存文件清理成功");
                }
            } else if (messageTransferPack.getAction() == 399) {
                MinaClientCustom.DeleteData deleteData = (MinaClientCustom.DeleteData) session.getAttribute("deleteFile");
                if (deleteData != null) {
                    System.out.println("文件替换成功,但是缓存文件清理失败");
                    System.out.println("正在尝试重新清理");
                    mExecutorService.execute(new FutureTask<>(deleteData));
                }
            } else if (messageTransferPack.getAction() == 402) {
                MinaClientCustom.SendData sendData = (MinaClientCustom.SendData) session.getAttribute("replaceFile");
                if (sendData != null) {
                    mExecutorService.execute(sendData);
                }
            } else if (messageTransferPack.getAction() == 499) {
                if(session.getAttribute("renameFile")!=null){
                    System.out.println("文件替换失败");
                }
            }
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        System.err.println("The client handles message exceptions:" + cause.getMessage());
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        if (status == IdleStatus.BOTH_IDLE) {
            System.out.println("Session entered idle, ready to close session");
            session.closeNow();
            System.exit(-1);
        }
    }
}
