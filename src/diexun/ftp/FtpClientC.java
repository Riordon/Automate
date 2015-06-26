package diexun.ftp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import diexun.main.Scanner;

public class FtpClientC {

    private static final int FTP_PORT = 21;

    private final String addr;
    private final String username;
    private final String password;
    private FTPClient client;
    private String workDire; //服务器上的相对路径

    public FtpClientC(String addr, String username, String password) {
        this.addr = addr;
        this.username = username;
        this.password = password;
    }

    /**
     * 进行多次尝试写入文件到服务器
     *
     * @param writerName
     * @param filePath
     * @return
     */
    public boolean cycleWriterFile(final String writerName, final String filePath) {
        System.out.println("filePath:" + filePath);
        boolean isSuc = false;
        for (int i = 0; i < 3; i++) {
            isSuc = writeFile(writerName, filePath);
            if (isSuc) {
                break;
            }
        }
        if (!isSuc) {
            boolean isCreate = cycleCreateFtp();
            if (isCreate) {
                isSuc = cycleWriterFile(writerName, filePath);
            }
        }
        return isSuc;
    }

    /**
     * 写文件到服务器
     *
     * @param writerName //写入图片名
     * @param filePath //图片本地路径
     * @return
     */
    public boolean writeFile(final String writerName, final String filePath) {
        if (null == client) {
            return false;
        }
        File file = new File(filePath);
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            BufferedInputStream reader = new BufferedInputStream(is);
            if (null != workDire) {
                client.changeWorkingDirectory(workDire);
            }
            return client.storeFile(writerName, reader);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 写文件到服务器 ftp上传文件，容易出现上传出错的情况 一般为Connect time out
     *
     * @param writerName //写入图片名
     * @param filePath //图片本地路径
     * @param ftpDirectory
     * @return
     */
    public boolean writeFile(final String writerName, final String filePath, String ftpDirectory) {
        if (null == client) {
            logger.error("图片路径：{}, 异常：client 为null", filePath);
            return false;
        }
        File file = new File(filePath);
        InputStream is = null;
        BufferedInputStream reader = null;
        try {
            if (file.exists() && file.isFile()) {
                is = new FileInputStream(file);
                reader = new BufferedInputStream(is);
                if (null != ftpDirectory) {
                    // 从 / 下指定.这样才能切换两次
                    if (!ftpDirectory.startsWith("/")){
                        ftpDirectory = "/"+ftpDirectory;
                    }
                    client.changeWorkingDirectory(ftpDirectory);
                }
                boolean ret = client.storeFile(writerName, reader);
                return ret;
            }
        } catch (Exception e) {
            // 传送过程中，连接断开，导致没传成功，则重连
            if (!isConnected()) {
                if (refreash()) {
                    boolean ret = false;
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (reader != null) {
                            reader.close();
                        }
                        is = new FileInputStream(file);
                        reader = new BufferedInputStream(is);
                        ret = client.storeFile(writerName, reader);
                    } catch (IOException ex) {
                    }
                    return ret;
                }
            }
            logger.error("图片路径：{}, 异常：{}", filePath, e.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public void close() {
        try {
            if (client != null && isConnected()) {
                client.disconnect();
                client = null;
            }
        } catch (IOException ex) {
        }
    }
    private final static Logger logger = LoggerFactory.getLogger(FtpClientC.class);
    private int count = 1;

    public boolean refreash() {
        if (count >= 3) {
            return false;
        }
        boolean conn = createFtpConnction();
        while (!conn && count++ < 3) {
            close();
            conn = createFtpConnction();
        }
        if (conn) {
            this.shoeKeepLive();
        }
        return conn;
    }

    public boolean isConnected() {
        return client.isConnected();
    }

    public void shoeKeepLive() {
        try {
            client.setBufferSize(4096);
            client.setKeepAlive(true);
            client.setTcpNoDelay(true);
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 多次尝试连接
     *
     * @return
     */
    public boolean cycleCreateFtp() {
        boolean isSuc = false;
        for (int i = 0; i < 5; i++) {
            isSuc = createFtpConnction();
            if (isSuc) {
                break;
            }
        }
        return isSuc;
    }

    /**
     * 创建FTP连接
     *
     * @return
     */
    public boolean createFtpConnction() {
        close();
        client = new FTPClient();
        try {
            client.setConnectTimeout(10000);//设置超时时间10秒
            client.connect(addr, FTP_PORT);
            int reply = client.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                client.disconnect();
                System.out.println("Ftp连接出错");
                Scanner.proLog("Ftp连接出错");
                close();
                return false;
            }
            boolean isLog = client.login(username, password);
            if (!isLog) {
                client.logout();// 注销请求
                System.out.println("Ftp登录出错");
                Scanner.proLog("Ftp登录出错");
                close();
                return false;
            } else {
//                client.setControlEncoding("UTF-8");
                client.setBufferSize(2048);
                client.setFileType(FTPClient.BINARY_FILE_TYPE); // 设置成二进制文件类型
                client.enterLocalPassiveMode();// 进入本地被动模式
            }
        } catch (IOException e) {
            System.out.println("Ftp连接异常");
            Scanner.proLog("Ftp连接异常");
            close();
            return false;
        }
        return true;
    }

    /**
     * @return the workDire
     */
    public String getWorkDire() {
        return workDire;
    }

    /**
     * @param workDire the workDire to set
     */
    public void setWorkDire(String workDire) {
        this.workDire = workDire;
    }
}
