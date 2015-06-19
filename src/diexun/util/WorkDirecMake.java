/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diexun.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import diexun.config.Constants;

/**
 *
 * @author 罗远康 (业务工作目录文件生成类)
 */
public class WorkDirecMake {

    private static final String FOLDER_BUILD_TXT = "/file/folder_build.xml";
    private final String day_dir;

    public WorkDirecMake(String day_dir) {
        this.day_dir = day_dir;
    }

    public WorkDirecMake() {
        day_dir = null;
    }

    /**
     * 创建缓存目录
     *
     * @param cachePath
     */
    public void makeCacheFile(String cachePath) {
        System.out.println("makeCacheFile");
        File cacheFile = new File(cachePath);
        boolean isMkdir = cacheFile.mkdirs();
        if (isMkdir) {
            File f1 = new File(cachePath + File.separator + Constants.EPD_SHOEBAG_WEBSITE);
            File f2 = new File(cachePath + File.separator + Constants.EPD_CLOTHING_SEBSITE);
            
            f1.mkdir();
            f2.mkdir();
        }
    }

    /**
     * 生成文件函数
     *
     */
    public void makeFiles() {
        System.out.println("====生成今天的工作目录====");
        File dateFile = new File(day_dir);
        boolean isMkdir = dateFile.mkdirs(); //生成日期下的文件夹
        if (isMkdir) {
            readBuildTxt();
        }
    }

    /**
     * 读取生成文件
     *
     */
    private void readBuildTxt() {
        InputStream is = WorkDirecMake.class.getResourceAsStream(FOLDER_BUILD_TXT);
        if (null != is) {
            try {
                SAXReader sax = new SAXReader();
                Document xmlDoc = sax.read(is);
                Element root = xmlDoc.getRootElement();
                List<Element> list = root.elements();
                for (Element element : list) { //循环网站
                    recElement(element);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException ex) {
                    Logger.getLogger(WorkDirecMake.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * 递归Element
     *
     * @param element
     */
    private void recElement(Element element) {
        StringBuilder sb = new StringBuilder();
        Element tempElement = element;
        sb.append(tempElement.attributeValue("name"));
        while (!"root".equals(tempElement.getParent().getName())) {
            tempElement = tempElement.getParent();
            if (tempElement.attributeValue("name") != null) {
                sb.append(",");
                sb.append(tempElement.attributeValue("name"));
            }
        }
        String[] tArr = sb.toString().split(",");
        sb = new StringBuilder();
        sb.append(day_dir);
        for (int i = tArr.length - 1; i >= 0; i--) {
            sb.append(File.separator);
            sb.append(tArr[i]);
        }
        File file = new File(sb.toString());
        if (!file.exists()) {
            file.mkdir();
        }
        List<Element> elements = element.elements();
        if (null == elements || elements.isEmpty()) {
            return;
        }
        for (Element element1 : elements) {
            recElement(element1);
        }
    }
}
