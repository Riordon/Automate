/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diexun.ftp;

import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author luoyuankang
 */
public class Client extends FTPClient {

    private String dirwork;

    /**
     * @return the dirwork
     */
    public String getDirwork() {
        return dirwork;
    }

    /**
     * @param dirwork the dirwork to set
     */
    public void setDirwork(String dirwork) {
        this.dirwork = dirwork;
    }

}
