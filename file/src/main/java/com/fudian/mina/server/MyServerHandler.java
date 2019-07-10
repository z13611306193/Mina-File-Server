package com.fudian.mina.server;

import com.fudian.mina.common.*;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zyg
 *         Server request handler
 */
public class MyServerHandler extends IoHandlerAdapter {

    //任务执行线程池
    private static ExecutorService mExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    //上传文件夹
    private static String uploadFilePath = Init.getProperty("DISK");

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        System.out.println(session.getRemoteAddress() + " the connected");
        super.sessionCreated(session);
    }

    @Override
    public void messageReceived(final IoSession session, Object message) throws Exception {

        CustomPack pack = (CustomPack) message;

        //文件上传
        if (pack.getBean() instanceof UploadRequestHeadPack) {
            UploadRequestHeadPack uploadRequestHeadPack = (UploadRequestHeadPack) pack.getBean();
            if (uploadRequestHeadPack.getAction() == 101) {
                try {
                    System.out.println("第一次上传");
                    createFile(uploadRequestHeadPack, session);//创建文件
                    session.write(new CustomPack<>(new MessageTransferPack(102, "true")));
                } catch (IOException e) {
                    e.printStackTrace();
                    session.write(new CustomPack<>(new MessageTransferPack(102, "false")));
                    session.write(new CustomPack<>(new MessageTransferPack(199, "IO异常,文件上传失败")));
                }
            }
        } else if (pack.getBean() instanceof MessageTransferPack) {
            MessageTransferPack messageTransferPack = (MessageTransferPack) pack.getBean();
            if (messageTransferPack.getAction() == 105) {
                try {
                    System.out.println("文件数据接收完毕");
                    destroyFile(session);//资源的释放
                    session.write(new CustomPack<>(new MessageTransferPack(106, "文件上传成功!")));
                    System.out.println("文件接收成功");
                } catch (IOException e) {
                    e.printStackTrace();
                    session.write(new CustomPack<>(new MessageTransferPack(199, "IO异常资源释放失败,文件上传失败!")));
                }
            }
        } else if (pack.getBean() instanceof UploadRequestBodyPack) {
            final UploadRequestBodyPack uploadRequestBodyPack = (UploadRequestBodyPack) pack.getBean();
            if (uploadRequestBodyPack.getAction() == 103) { //接受数据
                mExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        String fileName = (String) session.getAttribute(session.getRemoteAddress());
                        FileOutputStream outputStream = (FileOutputStream) session.getAttribute("outputStream");
                        try {
                            outputStream.write(uploadRequestBodyPack.getData());
                            session.setAttribute("readFileSize", (long) (session.getAttribute("readFileSize")) + uploadRequestBodyPack.getData().length);
                            String outName = fileName.substring(fileName.indexOf("_") + 1);
                            System.out.println(outName + " 已接收 " + session.getAttribute("readFileSize") + " 字节,总共 " + session.getAttribute("fileSize") + " 字节");
                            //日志
//                            session.write(new CustomPack<>(new MessageTransferPack(104, outName + " 已上传 " + readFileSizeMap.get(fileName) + " 字节,总共 " + fileSizeMap.get(fileName) + " 字节, 正在上传...")));
                            session.write(new CustomPack<>(new MessageTransferPack(104, "true")));
                        } catch (IOException e) {
                            e.printStackTrace();
                            try {
                                session.write(new CustomPack<>(new MessageTransferPack(104, "false")));
                                session.write(new CustomPack<>(new MessageTransferPack(199, "IO异常,文件上传失败!")));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
            }
        } else if (pack.getBean() instanceof DownloadAndDeleteRequestPack) { //文件下载
            final DownloadAndDeleteRequestPack downloadAndDeleteRequestPack = (DownloadAndDeleteRequestPack) pack.getBean();
            if (downloadAndDeleteRequestPack.getAction() == 201) {
                mExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        String date = downloadAndDeleteRequestPack.getDate();
                        String fileName = downloadAndDeleteRequestPack.getFileName();
                        String fileId = downloadAndDeleteRequestPack.getFileId();
                        String newFileName = fileId + "_" + fileName;
                        date = dateFormat(date);
                        String filePath = uploadFilePath + date + newFileName;
                        File file = new File(filePath);
                        if (!file.exists() || !file.isFile()) {
                            try {
                                throw new FileNotFoundException();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                                System.out.println("文件不存在");
                                try {
                                    session.write(new CustomPack<>(new MessageTransferPack(299, "文件不存在,文件下载失败!")));
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        try {
                            FileInputStream inputStream = new FileInputStream(file);
                            System.out.println("----------------------------Data ready to send-----------------------------");
                            byte[] bytes;
                            if (file.length() < 1073741824L) {
                                bytes = new byte[4096]; //400k
                                sendData(bytes, 1, inputStream, fileName, file.length());
                            } else if (file.length() < 2147483648L) {
                                bytes = new byte[3072]; //300k
                                sendData(bytes, 1, inputStream, fileName, file.length());
                            } else if (file.length() < 4294967296L) {
                                bytes = new byte[2048]; //200k
                                sendData(bytes, 1, inputStream, fileName, file.length());
                            } else {
                                bytes = new byte[1024]; //100k
                                sendData(bytes, 1, inputStream, fileName, file.length());
                            }
                            inputStream.close();
                            session.write(new CustomPack<>(new MessageTransferPack(204, "true")));
                            System.out.println("----------------------------Data transmission completed-----------------------------");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            try {
                                session.write(new CustomPack<>(new MessageTransferPack(299, "文件不存在,文件下载失败")));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            try {
                                session.write(new CustomPack<>(new MessageTransferPack(299, "IO异常,文件下载失败!")));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }

                    private void sendData(byte[] bytes, int milliSecond, FileInputStream inputStream, String fileName, long fileSize) throws IOException {
                        int length = -1;
                        long sendSize = 0;
                        while ((length = inputStream.read(bytes)) != -1) {
                            try {
                                Thread.sleep(milliSecond);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            sendSize += length;
                            byte[] temps = new byte[length];
                            System.arraycopy(bytes, 0, temps, 0, length);
                            session.write(new CustomPack<>(new UploadRequestBodyPack(202, temps)));
                            System.out.println(fileName + " 已发送" + sendSize + "字节,总共" + fileSize + "字节");
                            session.write(new CustomPack<>(new MessageTransferPack(203, fileName + " 已下载 " + sendSize + " 字节,总共 " + fileSize + " 字节,正在下载...")));
                        }
                    }
                });
            } else if (downloadAndDeleteRequestPack.getAction() == 301) {
                String date = downloadAndDeleteRequestPack.getDate();
                String fileName = downloadAndDeleteRequestPack.getFileName();
                String fileId = downloadAndDeleteRequestPack.getFileId();
                date = dateFormat(date);
                String filePath = uploadFilePath + date + fileId + "_" + fileName;
                File file = new File(filePath);
                if (!file.exists() || !file.isFile()) {
                    try {
                        throw new FileNotFoundException();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        System.out.println("文件不存在");
                        session.write(new CustomPack<>(new MessageTransferPack(399, "文件不存在,文件删除失败!")));
                    }
                } else {
                    boolean delete = file.delete();
                    if (delete) {
                        System.out.println("文件删除成功!");
                        session.write(new CustomPack<>(new MessageTransferPack(302, "文件删除成功!")));
                    } else {
                        System.out.println("文件删除失败");
                        session.write(new CustomPack<>(new MessageTransferPack(302, "文件删除失败!")));
                    }
                }
            }

        } else if (pack.getBean() instanceof RenameRequestPack) {//文件重命名
            RenameRequestPack renameRequestPack = (RenameRequestPack) pack.getBean();
            if (renameRequestPack.getAction() == 401) {
                String date = renameRequestPack.getDate();
                String oldFileName = renameRequestPack.getOldFileName();
                String newFileName = renameRequestPack.getNewFileName();
                String fileId = renameRequestPack.getFileId();
                date = dateFormat(date);
                String oldFilePath = uploadFilePath + date + fileId + "_" + oldFileName;
                String newFilePath = uploadFilePath + date + fileId + "_" + newFileName;
                File file = new File(oldFilePath);
                if (!file.exists() || !file.isFile()) {
                    try {
                        throw new FileNotFoundException();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        System.out.println("文件不存在");
                        session.write(new CustomPack<>(new MessageTransferPack(499, "文件不存在,文件重命名失败!")));
                    }
                } else {
                    File newFile = new File(newFilePath);
                    boolean b = file.renameTo(newFile);
                    if (b) {
                        System.out.println("文件名修改成功!");
                        session.write(new CustomPack<>(new MessageTransferPack(402, "文件名修改成功!")));
                    } else {
                        System.out.println("文件名修改失败!");
                        session.write(new CustomPack<>(new MessageTransferPack(499, "文件名修改失败!")));
                    }
                }
            }
        }else{
            session.write(new CustomPack<>(new MessageTransferPack(999,"无效的请求")));
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        cause.printStackTrace();
        System.out.println("The server handles message exceptions:" + cause);
        if (!session.isClosing()) {
            //处理无效文件
            String fileName = (String) session.getAttribute(session.getRemoteAddress());
            if(fileName!=null&&!fileName.equalsIgnoreCase("")){
                session.removeAttribute(session.getRemoteAddress());
                FileOutputStream o = (FileOutputStream) session.getAttribute("outputStream");
                o.flush();
                o.close();
                session.removeAttribute("outputStream");
                File file = (File) session.getAttribute("file");
                file.delete();
                session.removeAttribute("file");
                session.removeAttribute("fileSize");
                session.removeAttribute("readFileSize");
                session.removeAttribute("isClose");

            }
            session.closeNow();
        }
    }

    private String dateFormat(String date) {
        StringBuilder tempDate = new StringBuilder(date);
        tempDate.insert(4, "/");
        tempDate.insert(tempDate.length(), "/");
        return tempDate.toString();
    }

    private void createFile(UploadRequestHeadPack uploadRequestHeadPack, IoSession session) throws FileNotFoundException {
        String[] dates = new SimpleDateFormat("yyyy-MM").format(new Date()).split("-");
        String privateUploadFilePath = uploadFilePath;
        privateUploadFilePath = privateUploadFilePath + dates[0] + "/" + dates[1];
        File folder = new File(privateUploadFilePath);
        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdirs();
        }
        String fileName = uploadRequestHeadPack.getFileId() + "_" + uploadRequestHeadPack.getFileName() + "." + uploadRequestHeadPack.getFileType();
        File file = new File(privateUploadFilePath + "/" + fileName);
        FileOutputStream outputStream = new FileOutputStream(file);
        session.setAttribute(session.getRemoteAddress(), fileName);
        session.setAttribute("outputStream", outputStream);
        session.setAttribute("file", file);
        session.setAttribute("fileSize", uploadRequestHeadPack.getFileSize());
        session.setAttribute("readFileSize", 0L);
        session.setAttribute("isClose", false);

    }

    private void destroyFile(IoSession session) throws IOException {
        System.out.println("已上传:" + session.getAttribute("readFileSize"));
        System.out.println("总共:" + session.getAttribute("fileSize"));
        FileOutputStream outputStream = (FileOutputStream) session.getAttribute("outputStream");
        outputStream.flush();
        outputStream.close();
        session.setAttribute("isClose", true);
        session.removeAttribute(session.getRemoteAddress());
        session.removeAttribute("outputStream");
        session.removeAttribute("file");
        session.removeAttribute("fileSize");
        session.removeAttribute("readFileSize");
        session.removeAttribute("isClose");
    }
}