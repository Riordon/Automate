/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diexun.ftp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import diexun.main.Scanner;

/**
 * 这个类 帮助FTP连接和发送消息（多次尝试）
 *
 * @author luoyuankang
 */
public class FTPRecService {

    /**
     * 尝试多次获取代理
     *
     * @param workDir 当前主题的工作路径
     * @return
     */
    public static FtpClientProxy getFtpClinetProxy(String workDir) {
        FtpClientProxy fcp = null;
        for (int i = 0; i < 3; i++) {
            try {
                fcp = new FtpClientProxy(workDir);
                if (null != fcp.getClient() && fcp.getClient().isAvailable() && fcp.getClient().isConnected()) {
                    return fcp;
                } else {
                    Scanner.proLog("else 释放 ftp");
                    fcp.release();//释放当前FTP客户端
                }
            } catch (InterruptedException | IOException ex) {
                if (null != fcp) {
                    Scanner.proLog("catch 释放 ftp");
                    fcp.release();//释放当前FTP客户端
                }
                Logger.getLogger(FTPRecService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    /**
     * 尝试多次发送文件（当前仅当有一次成功就退出）
     *
     * @param proxy
     * @param writerName
     * @param filePath
     * @return
     */
    public static boolean cycleUploadFile(FtpClientProxy proxy, final String writerName, final String filePath) {
        File file = new File(filePath);
        InputStream is = null;
        for (int i = 0; i < 3; i++) {
            try {
                is = new FileInputStream(file);
                BufferedInputStream reader = new BufferedInputStream(is);
                boolean isSuc = proxy.storeFile(writerName, reader);
                if (isSuc) {
                    return true;
                } else {
                    Scanner.proLog("发生传输过程中错误 当前FTP客户端状况是:" + proxy.getClient().isAvailable() + ":" + proxy.getClient().isConnected());
                }
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
        }
        return false;
    }
}
