package diexun.ftp;

/**
 * @author xiaolong
 */
public class FTPBean {
    private String ftp_ip;
    private int ftp_port;
    private String ftp_username;
    private String ftp_password;
    
    public FTPBean()  {
    }

    public FTPBean(String ipPort) {
            String[] infomations = ipPort.split(";");
            this.ftp_ip = infomations[0];
            this.ftp_port = Integer.parseInt(infomations[1]);
            this.ftp_username = infomations[2];
            this.ftp_password = infomations[3];
    }

    public FTPBean(String ftp_ip, int ftp_port, String ftp_username, String ftp_password) {
            this.ftp_ip = ftp_ip;
            this.ftp_port = ftp_port;
            this.ftp_username = ftp_username;
            this.ftp_password = ftp_password;
    }

    public String getFtp_ip() {
            return ftp_ip;
    }
    public void setFtp_ip(String ftp_ip) {
            this.ftp_ip = ftp_ip;
    }
    public int getFtp_port() {
            return ftp_port;
    }
    public void setFtp_port(int ftp_port) {
            this.ftp_port = ftp_port;
    }
    public String getFtp_username() {
            return ftp_username;
    }
    public void setFtp_username(String ftp_username) {
            this.ftp_username = ftp_username;
    }
    public String getFtp_password() {
            return ftp_password;
    }
    public void setFtp_password(String ftp_password) {
            this.ftp_password = ftp_password;
    }
}
