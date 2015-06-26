/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diexun.ftp;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author luoyuankang
 */
public class FtpClientInfo {

    private String ftpIp; // ftp的IP地址  
    private int ftpPort; // ftp的端口  
    private String ftpUserName; // ftp的用户名  
    private String ftpPassword; // ftp的密码  
    private int maxConnects; // 最大连接数  
    private long timeout; // 超时时间 ,默认60  
    private TimeUnit timeUnit;// 超时时间单位,默认为秒  
    private int bufSize;
    private int connectTimeout;

    public FtpClientInfo() {
        timeout = 60;
        timeUnit = TimeUnit.SECONDS;
        bufSize = 2048;
        connectTimeout = 10000;
    }

    public String getFtpIp() {
        return ftpIp;
    }

    public void setFtpIp(String ftpIp) {
        this.ftpIp = ftpIp;
    }

    public int getFtpPort() {
        return ftpPort;
    }

    public void setFtpPort(int ftpPort) {
        this.ftpPort = ftpPort;
    }

    public String getFtpUserName() {
        return ftpUserName;
    }

    public void setFtpUserName(String ftpUserName) {
        this.ftpUserName = ftpUserName;
    }

    public String getFtpPassword() {
        return ftpPassword;
    }

    public void setFtpPassword(String ftpPassword) {
        this.ftpPassword = ftpPassword;
    }

    public int getMaxConnects() {
        return maxConnects;
    }

    public void setMaxConnects(int maxConnects) {
        this.maxConnects = maxConnects;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    /**
     * @return the bufSize
     */
    public int getBufSize() {
        return bufSize;
    }

    /**
     * @param bufSize the bufSize to set
     */
    public void setBufSize(int bufSize) {
        this.bufSize = bufSize;
    }

    /**
     * @return the connectTimeout
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * @param connectTimeout the connectTimeout to set
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

}
