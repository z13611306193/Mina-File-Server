package com.fudian.mina.client;

import com.fudian.mina.common.*;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * @author zyg
 *         Client main program
 */
public class MinaClientCustom {

    private static final String MINA_HOST = Init.getProperty("IP");

    private static final int MINA_PORT = Integer.valueOf(Init.getProperty("PORT"));

    private static final String downloadPath = Init.getProperty("DISK");

    private static ExecutorService mExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static void main(String[] args) {

        // create a connector
        IoConnector connector = new NioSocketConnector(Runtime.getRuntime().availableProcessors() + 1);

        // add custom codec factory filter
        connector.getFilterChain().addLast("myCoder", new ProtocolCodecFilter(new CustomProtocolCodecFactory(Charset.forName("UTF-8"))));

        // add thread pool filter
        connector.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1)));

        // add logger filter
        connector.getFilterChain().addLast("logger", new LoggingFilter());

        // set min read buffer size
        connector.getSessionConfig().setMinReadBufferSize(1024);

        // set max read buffer size
        connector.getSessionConfig().setMaxReadBufferSize(4096);

        // set idle time
        connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60);

        // set handler
        connector.setHandler(new MyClientHandler());

        // connector execute connect
        ConnectFuture future = connector.connect(new InetSocketAddress(MINA_HOST, MINA_PORT));

        // add listener
        future.addListener(new IoFutureListener<IoFuture>() {

            public void operationComplete(IoFuture future) {
                // get ioSession
                IoSession session = future.getSession();
                printFun();
                switchFun(session);
            }
        });
    }

    private static void printFun() {
        System.out.println("*********FUNCTION MENU*********");
        System.out.println("**    1.Upload File          **");
        System.out.println("**    2.Download File        **");
        System.out.println("**    3.Batch Upload File    **");
        System.out.println("**    4.Batch Download File  **");
        System.out.println("**    5.Delete File          **");
        System.out.println("**    6.Rename File          **");
        System.out.println("**    7.Replace File         **");
        System.out.println("**    99.Exit                **");
        System.out.println("*******************************");
        System.out.println("Please enter function number:");
    }

    private static void switchFun(IoSession session) {
        Scanner scanner = new Scanner(System.in);
        String number = scanner.nextLine();

        while (true) {
            if (number.trim().equals("1")) {
                break;
            } else if (number.trim().equals("2")) {
                break;
            } else if (number.trim().equals("3")) {
                break;
            } else if (number.trim().equals("4")) {
                break;
            } else if (number.trim().equals("5")) {
                break;
            } else if (number.trim().equals("6")) {
                break;
            } else if (number.trim().equals("7")) {
                break;
            } else if (number.trim().equals("99")) {
                System.out.println("退出");
                System.exit(0);
            } else {
                System.out.println("这都能输错,瞎?");
                number = scanner.nextLine();
            }
        }

        File file;
        String fileId;
        String fileName;
        String type;
        long fileSize;
        int j = -1;
        String date;
        // function call
        switch (number) {
            case "1":
                System.out.println("输入文件所在路径:");
                String filePath = scanner.nextLine();
                file = new File(filePath);
                fileId = UUID.randomUUID().toString().replaceAll("-", "");
                fileName = file.getName();
                j = fileName.lastIndexOf(".");
                type = fileName.substring(j + 1);
                fileName = fileName.substring(0, j);
                fileSize = file.length();
                mExecutorService.execute(new SendData(session, file, fileId, fileName, type, fileSize));
                break;
            case "2":
                System.out.println("请输入日期(201906):");
                date = scanner.nextLine();
                System.out.println("请输入文件名(a.avi):");
                fileName = scanner.nextLine();
                System.out.println("请输入文件名ID:");
                fileId = scanner.nextLine();
                mExecutorService.execute(new ReceivedData(session, date, fileName, fileId));
                break;
            case "3":
                LinkedList<File> fileList = new LinkedList<>();
                for (int i = 1; i <= 200; i++) {
                    File batchFile = new File("D:/testUpload/" + i + ".txt");
                    fileList.add(batchFile);
                }
                file = fileList.removeFirst();
                fileId = UUID.randomUUID().toString().replaceAll("-", "");
                fileName = file.getName();
                j = fileName.lastIndexOf(".");
                type = fileName.substring(j + 1);
                fileName = fileName.substring(0, j);
                fileSize = file.length();
                mExecutorService.execute(new SendData(session, file, fileId, fileName, type, fileSize));
                session.setAttribute("fileList", fileList);
                break;
            case "4":
                date = "201907";
                try {
                    LinkedList<String> strings = (LinkedList<String>) searchCurrentMonthFileNameCollection("D:/uploadFile/");
                    String s = strings.removeFirst();
                    fileId = s.substring(0, s.indexOf("_"));
                    fileName = s.substring(s.indexOf("_") + 1);
                    mExecutorService.execute(new ReceivedData(session,date,fileName,fileId));
                    session.setAttribute("fileNames",strings);
                } catch (RoadNotExistException e) {
                    e.printStackTrace();
                }
                break;
            case "5":
                System.out.println("输入日期(201906):");
                date = scanner.nextLine();
                System.out.println("输入要删除的文件ID:");
                fileId = scanner.nextLine();
                System.out.println("输入要删除的文件名:");
                fileName = scanner.nextLine();
                FutureTask<Boolean> futureTask = new FutureTask<>(new DeleteData(session, date, fileName, fileId));
                mExecutorService.execute(futureTask);
                break;
            case "6":
                System.out.println("输入日期(201906):");
                date = scanner.nextLine();
                System.out.println("输入文件ID:");
                fileId = scanner.nextLine();
                System.out.println("请输入原文件名:");
                fileName = scanner.nextLine();
                System.out.println("请输入新文件名:");
                String newFileName = scanner.nextLine();
                mExecutorService.execute(new RenameData(session, date, fileName, newFileName, fileId));
                break;
            case "7":
                System.out.println("输入日期(201906):");
                date = scanner.nextLine();
                System.out.println("输入被替换的文件ID:");
                fileId = scanner.nextLine();
                System.out.println("输入被替换的文件的文件名:");
                fileName = scanner.nextLine();
                System.out.println("请输入替换成的文件的文件路径:");
                String newFileName1 = scanner.nextLine();
                mExecutorService.execute(new RenameData(session, date, fileName, "temp" + fileName, fileId));
                file = new File(newFileName1);
                String name = fileName.substring(0, fileName.lastIndexOf("."));
                type = fileName.substring(fileName.lastIndexOf(".") + 1);
                session.setAttribute("replaceFile", new SendData(session, file, fileId, name, type, file.length()));
                session.setAttribute("deleteFile", new DeleteData(session, date, "temp" + fileName, fileId));
                session.setAttribute("renameFile", new RenameData(session, date, "temp" + fileName, fileName, fileId));
                break;
        }
    }

    /**
     * 搜索指定文件夹下所有文件的名字并返回成集合结构
     *
     * @param uploadFolder
     * @return
     * @throws RoadNotExistException
     */
    private static List<String> searchCurrentMonthFileNameCollection(String uploadFolder) throws RoadNotExistException {
        List<String> fileNames = new LinkedList<>();
        String[] dates = new SimpleDateFormat("yyyy-MM").format(new Date()).split("-");
        String filePath = uploadFolder + dates[0] + "/" + dates[1];
        File file = new File(filePath);
        File[] array = file.listFiles();
        for (int i = 0; i < array.length; i++) {
            if (array[i].isFile()) {
                fileNames.add(array[i].getName());
            }
        }
        return fileNames;
    }

    /**
     * rename data
     */
    public static class RenameData implements Runnable {

        private IoSession session;

        private String date;

        private String fileName;

        private String newFileName;

        private String fileId;

        public RenameData(IoSession session, String date, String fileName, String newFileName, String fileId) {
            this.session = session;
            this.date = date;
            this.fileName = fileName;
            this.newFileName = newFileName;
            this.fileId = fileId;
        }

        @Override
        public void run() {
            try {
                session.write(new CustomPack<RenameRequestPack>(new RenameRequestPack(401, date, fileId, fileName, newFileName)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * delete data
     */
    public static class DeleteData implements Callable<Boolean> {

        private IoSession session;

        private String date;

        private String fileName;

        private String fileId;

        public DeleteData(IoSession session, String date, String fileName, String fileId) {
            this.session = session;
            this.date = date;
            this.fileName = fileName;
            this.fileId = fileId;
        }

        @Override
        public Boolean call() throws Exception {
            try {
                session.write(new CustomPack<DownloadAndDeleteRequestPack>(new DownloadAndDeleteRequestPack(301, date, fileId, fileName)));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * received data
     */
    public static class ReceivedData implements Runnable {

        private IoSession session;

        private String date;

        private String fileName;

        private String fileId;

        public ReceivedData(IoSession session, String date, String fileName, String fileId) {
            this.session = session;
            this.date = date;
            this.fileName = fileName;
            this.fileId = fileId;
        }

        @Override
        public void run() {
            try {
                File file = new File(downloadPath + fileName);
                int tempFileNameNumber = 1;
                File tempFile = null;
                while (file.exists() && file.isFile()) {
                    String newFileName = file.getName().substring(0, file.getName().lastIndexOf(".")) + "(" + tempFileNameNumber + ")" + file.getName().substring(file.getName().lastIndexOf("."));
                    tempFile = new File(downloadPath + newFileName);
                    tempFileNameNumber++;
                }
                if (tempFile != null) {
                    file = tempFile;
                }
                FileOutputStream outputStream = new FileOutputStream(file);
                session.setAttribute(session.getRemoteAddress(), outputStream);
                session.write(new CustomPack<>(new DownloadAndDeleteRequestPack(201, date, fileId, fileName)));
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("本地IO异常,文件下载失败");
            }
        }
    }

    /**
     * send data
     */
    public static class SendData implements Runnable {

        private IoSession session;
        private File file;
        private String fileId;
        private String name;
        private String type;
        private long fileSize;

        public SendData(IoSession session, File file, String fileId, String name, String type, long fileSize) {
            this.session = session;
            this.file = file;
            this.fileId = fileId;
            this.name = name;
            this.type = type;
            this.fileSize = fileSize;
        }

        @Override
        public void run() {
            try {
                FileInputStream inputStream = new FileInputStream(file);
                System.out.println("----------------------------Data ready to send-----------------------------");
                byte[] bytes = null;
                session.write(new CustomPack<>(new UploadRequestHeadPack(101, fileId, name, fileSize, type)));
                while (true) {
                    String result = (String) session.getAttribute("headResult");
                    if (result != null && !"".equals(result)) {
                        session.removeAttribute("headResult");
                        if (result.equals("true")) {
                            break;
                        } else {
                            System.out.println("服务器IO异常");
                            return;
                        }
                    }
                    Thread.sleep(100);
                }
                if (file.length() < 1073741824L) {
                    bytes = new byte[4096]; //400k
                    sendData(bytes, 1, inputStream);
                } else if (file.length() < 2147483648L) {
                    bytes = new byte[3072]; //300k
                    sendData(bytes, 1, inputStream);
                } else if (file.length() < 4294967296L) {
                    bytes = new byte[2048]; //200k
                    sendData(bytes, 1, inputStream);
                } else {
                    bytes = new byte[1024]; //100k
                    sendData(bytes, 1, inputStream);
                }

                inputStream.close();
                Thread.sleep(1000);
                session.write(new CustomPack<>(new MessageTransferPack(105, "true")));
                System.out.println("----------------------------Data transmission completed-----------------------------");
            } catch (FileNotFoundException e) {
                System.out.println("这文件不存在");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("IO异常");
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("中断异常");
            }
        }

        private void sendData(byte[] bytes, int millis, FileInputStream inputStream) throws IOException, InterruptedException {
            int length = -1;
            long temp = 0L;
            while ((length = inputStream.read(bytes)) != -1) {
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                temp += length;
                byte[] temps = new byte[length];
                System.arraycopy(bytes, 0, temps, 0, length);
                session.write(new CustomPack<>(new UploadRequestBodyPack(103, temps)));
                System.out.println(this.name + "." + this.type + " 已发送" + temp + "字节,总共" + this.fileSize + "字节,正在上传.....");
                while (true) {
                    String result = (String) session.getAttribute("bodyResult");
                    if (result != null && !"".equals(result)) {
                        session.removeAttribute("bodyResult");
                        if (result.equals("true")) {
                            break;
                        } else {
                            System.out.println("服务器IO异常");
                            System.out.println("正在尝试重新发送...");
                            session.write(new CustomPack<>(new UploadRequestBodyPack(103, temps)));
                            return;
                        }
                    }
                    Thread.sleep(1);
                }
            }
        }
    }
}