/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diexun.ftp;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * FTP连接池
 *
 * @author luoyuankang
 */
public abstract class FTPPool {

    public static ArrayBlockingQueue<Client> ftpQueue;
    private static FtpClientInfo ftpClientInfo;

    /**
     * 初始化连接池
     *
     * @param info
     */
    public static void init(FtpClientInfo info) {
        ftpClientInfo = info;
        ftpQueue = new ArrayBlockingQueue<Client>(ftpClientInfo.getMaxConnects(), true);// 初始化队列容量  
        Client ftpClient;
        for (int i = 0; i < ftpClientInfo.getMaxConnects(); i++) {
            ftpClient = new Client();
            ftpQueue.add(ftpClient);
        }
    }

    public static FtpClientInfo getFtpClientInfo() {
        return ftpClientInfo;
    }

    /**
     * 向线程池中添加FTPClient
     *
     * @param ftpClient
     * @return
     */
    public static void add(Client ftpClient) throws InterruptedException {
        boolean b = ftpQueue.contains(ftpClient);
        if (!b) {
            ftpQueue.put(ftpClient);
        }
    }

    /**
     * 获取FTPClient，如果线程池为空，则等待到FtpClientInfo中所设置的超时时间
     *
     * @return
     * @throws InterruptedException
     */
    public static Client poll() throws InterruptedException {
        return ftpQueue.poll(ftpClientInfo.getTimeout(), ftpClientInfo.getTimeUnit());
    }

    /**
     * 获取FTPClient，如果线程池为空，则一直等待
     *
     * @return
     * @throws InterruptedException
     */
    public static Client take() throws InterruptedException {
        return ftpQueue.take();
    }

}
