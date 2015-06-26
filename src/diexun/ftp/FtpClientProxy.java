/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diexun.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import diexun.main.Scanner;

/**
 * FTPClient代理类
 *
 * @author luoyuankang
 */
public class FtpClientProxy {

    private Client client;

    public FtpClientProxy(String workDir) throws InterruptedException, SocketException, IOException {
        client = FTPPool.poll();
        Scanner.proLog("poll ftp :" + FTPPool.ftpQueue.size());
        if (null == client) {
            client = new Client();
        }
        if (!client.isConnected() || !client.isAvailable()) {
            close();
            FtpClientInfo info = FTPPool.getFtpClientInfo();// 获取ftpClient信息  
            client.setConnectTimeout(info.getConnectTimeout());
            client.connect(info.getFtpIp(), info.getFtpPort());// 连接  
            int reply = client.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                Scanner.proLog("Ftp连接出错");
                close();
                return;
            }
            boolean isLog = client.login(info.getFtpUserName(), info.getFtpPassword());// 登陆  
            if (!isLog) {
                client.logout();// 注销请求
                Scanner.proLog("Ftp登录出错");
                close();
                return;
            }
            client.setFileType(FTPClient.BINARY_FILE_TYPE);// 设置为二进制  
            client.setBufferSize(info.getBufSize());
            client.enterLocalPassiveMode();
        }
        if (null != client && client.isConnected() && client.isAvailable()) {
            if (null == client.getDirwork() || !client.getDirwork().equals(workDir)) {
                boolean isSuc = client.changeWorkingDirectory("../../../" + workDir);//设置工作路径
                Scanner.proLog("isSuc:" + isSuc);
            }

        }
    }

    public final void close() {
        Scanner.proLog("close ftp");
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
                client = null;
            }
        } catch (IOException ex) {
        }
    }

    /**
     * 释放ftp Client
     *
     */
    public void release() {
        Scanner.proLog("release ftp :" + FTPPool.ftpQueue.size());
        if (getClient() == null) {
            return;
        }
        try {
            FTPPool.add(getClient());
            Scanner.proLog("add ftp :" + FTPPool.ftpQueue.size());
        } catch (InterruptedException ex) {
            Logger.getLogger(FtpClientProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 上传文件
     *
     * @param remote
     * @param local
     * @return
     * @throws IOException
     */
    public boolean storeFile(String remote, InputStream local) throws IOException {
        return client.storeFile(remote, local);
    }

    /**
     * 获取本地端口
     *
     * @return
     */
    public int getLocalPort() {
        return client.getLocalPort();
    }

    /**
     * @return the client
     */
    public Client getClient() {
        return client;
    }

    /**
     * @param client the client to set
     */
    public void setClient(Client client) {
        this.client = client;
    }

}
