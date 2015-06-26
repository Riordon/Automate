/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diexun.user;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import diexun.main.Scanner;

/**
 *
 * @author luoyuankang
 */
public class UserManager {

    private static List<User> userCache = null; //用户信息缓存
    private static final ReentrantLock userLock = new ReentrantLock();//锁

    /**
     * 获取用户列表
     *
     * @return
     */
    public static List<User> getUserList() {
        File userXmlFile = new File(Scanner.USER_XML_PATH);
        if (!userXmlFile.exists()) {
            writerXML(userXmlFile, "users");
        }
        List<User> result = new ArrayList<User>();
        SAXReader sax = new SAXReader();
        XMLWriter xmlWriter = null;
        try {
            Document xmlDoc = sax.read(userXmlFile);
            Element root = xmlDoc.getRootElement();// 根节点
            List<Element> list = root.elements();
            if (null != list && !list.isEmpty()) {
                User user;
                for (Element temp : list) {
                    user = new User();
                    user.setUsername(temp.elementText("username"));
                    user.setPassword(temp.elementText("password"));
                    user.setRole(temp.elementText("role"));
                    user.setWebsite(temp.elementText("website"));
                    user.setPush_website(temp.elementText("push_website"));
                    user.setPush_sort(temp.elementText("push_sort"));
                    user.setPush_column(temp.elementText("push_column"));
                    user.setPush_column_type(temp.elementText("push_column_type"));
                    result.add(user);
                }
                return result;
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != xmlWriter) {
                    xmlWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static void writerXML(File xmlFile, String rootName) {
        Document document = DocumentHelper.createDocument(); // 创建文档
        document.setXMLEncoding(Scanner.XML_CODING);
        document.addElement(rootName); // 添加属性根节点
        FileOutputStream fileWriter = null;
        XMLWriter xmlWriter = null;
        try {
            fileWriter = new FileOutputStream(xmlFile);//FileWriter
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding(Scanner.XML_CODING);
            xmlWriter = new XMLWriter(fileWriter, format);
            xmlWriter.write(document); // 写入文件中
            xmlWriter.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (null != fileWriter) {
                    fileWriter.close();
                }
                if (null != xmlWriter) {
                    xmlWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

	/**
     * 获取用户列表，通过缓存
     *
     * @return
     */
    public static List<User> getUserListByCache() {
        if (null == userCache) {
            userCache = getUserList();
        }
        return userCache;
    }

    /**
     * 刷新用户缓存
     */
    public static void refUserListCache() {
        userCache = getUserList();
    }

    /**
     * 用户登录，验证用户名和密码是否正确
     *
     * @param userName
     * @param passWord
     * @return
     */
    public static boolean userLogin(String userName, String passWord) {
        User user = getUserByName(userName);
        if (null == user) {
            return false;
        }
        if (user.getPassword().equals(passWord.trim())) {
            return true;
        }
        return false;
    }

    /**
     * 添加一个用户
     *
     * @param user
     * @return
     */
    public static boolean addUser(User user) {
        File userXmlFile = new File(Scanner.USER_XML_PATH);
        if (!userXmlFile.exists()) {
            writerXML(userXmlFile, "users");
        }
        userLock.lock();
        try {
            SAXReader sax = new SAXReader();
            FileOutputStream fileWriter = null;
            XMLWriter xmlWriter = null;
            try {
                Document xmlDoc = sax.read(userXmlFile);
                Element root = xmlDoc.getRootElement();// 根节点
                Element userEle = root.addElement("user"); //单个用户节点
                Element userNameEle = userEle.addElement("username");
                userNameEle.setText(user.getUsername());
                Element passWordEle = userEle.addElement("password");
                passWordEle.setText(user.getPassword());
                Element roleEle = userEle.addElement("role");
                roleEle.setText(user.getRole());
                Element websiteEle = userEle.addElement("website");
                websiteEle.setText(user.getWebsite());
                Element pushWebsiteEle = userEle.addElement("push_website");
                pushWebsiteEle.setText(""); //推送项目初始化为空串
                Element pushSortEle = userEle.addElement("push_sort");
                pushSortEle.setText("");
                Element pushColumnEle = userEle.addElement("push_column");
                pushColumnEle.setText("");
                Element pushColumnTypeEle = userEle.addElement("push_column_type");
                pushColumnTypeEle.setText("");

                fileWriter = new FileOutputStream(userXmlFile);
                OutputFormat format = OutputFormat.createPrettyPrint();
                format.setEncoding(Scanner.XML_CODING);
                xmlWriter = new XMLWriter(fileWriter, format);
                xmlWriter.write(xmlDoc); // 写入文件中
                refUserListCache();//添加用户成功要更新一下缓存
                return true;
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != xmlWriter) {
                        xmlWriter.close();
                    }
                    if (null != fileWriter) {
                        fileWriter.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            userLock.unlock();
        }
        return false;
    }

    public static boolean updateUser(User user) {
        File userXmlFile = new File(Scanner.USER_XML_PATH);
        if (!userXmlFile.exists()) {
            writerXML(userXmlFile, "users");
        }
        userLock.lock();
        try {
            SAXReader sax = new SAXReader();
            FileOutputStream fileWriter = null;
            XMLWriter xmlWriter = null;
            try {
                Document xmlDoc = sax.read(userXmlFile);
                Element root = xmlDoc.getRootElement();// 根节点
                List<Element> list = root.elements();
                boolean isSuc = false;
                if (null != list && !list.isEmpty()) {
                    String name;
                    for (Element temp : list) {
                        name = temp.elementText("username");
                        if (name.equals(user.getUsername())) {
                            if (null != user.getPassword()) {
                                temp.element("password").setText(user.getPassword());
                            }
                            if (null != user.getRole()) {
                                temp.element("role").setText(user.getRole());
                            }
                            if (null != user.getWebsite()) {
                                temp.element("website").setText(user.getWebsite());
                            }
                            if (null != user.getPush_website()) {
                                temp.element("push_website").setText(user.getPush_website());
                            }
                            if (null != user.getPush_sort()) {
                                temp.element("push_sort").setText(user.getPush_sort());
                            }
                            if (null != user.getPush_column()) {
                                temp.element("push_column").setText(user.getPush_column());
                            }
                            if (null != user.getPush_column_type()) {
                                temp.element("push_column_type").setText(user.getPush_column_type());
                            }
                            isSuc = true;
                            break;
                        }
                    }
                }
                fileWriter = new FileOutputStream(userXmlFile);
                OutputFormat format = OutputFormat.createPrettyPrint();
                format.setEncoding(Scanner.XML_CODING);
                xmlWriter = new XMLWriter(fileWriter, format);
                xmlWriter.write(xmlDoc); // 写入文件中
                refUserListCache();//修改用户成功要更新一下缓存
                return isSuc;
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != xmlWriter) {
                        xmlWriter.close();
                    }
                    if (null != fileWriter) {
                        fileWriter.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            userLock.unlock();
        }
        return false;
    }

    /**
     * 获取单个用户，根据用户的名称
     *
     * @param userName
     * @return
     */
    public static User getUserByName(String userName) {
        if (userName == null || "".equals(userName)) {
            return null;
        }
        getUserListByCache();
        userName = userName.trim();
        for (User user : userCache) {
            if (user.getUsername().equals(userName)) {
                return user;
            }
        }
        return null;
    }

    /**
     * 删除一个用户
     *
     * @param userName
     * @return
     */
    public static boolean delUser(String userName) {
        File userXmlFile = new File(Scanner.USER_XML_PATH);
        if (!userXmlFile.exists()) {
            return false;
        }
        userLock.lock();
        try {
            SAXReader sax = new SAXReader();
            FileOutputStream fileWriter = null;
            XMLWriter xmlWriter = null;
            try {
                Document xmlDoc = sax.read(userXmlFile);
                Element root = xmlDoc.getRootElement();// 根节点
                List<Element> list = root.elements();
                if (null != list && !list.isEmpty()) {
                    for (Element temp : list) {
                        if (userName.equals(temp.elementText("username"))) {
                            root.remove(temp);
                            break;
                        }
                    }
                }
                fileWriter = new FileOutputStream(userXmlFile);
                OutputFormat format = OutputFormat.createPrettyPrint();
                format.setEncoding(Scanner.XML_CODING);
                xmlWriter = new XMLWriter(fileWriter, format);
                xmlWriter.write(xmlDoc); // 写入文件中
                refUserListCache();//修改用户成功要更新一下缓存
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != xmlWriter) {
                        xmlWriter.close();
                    }
                    if (null != fileWriter) {
                        fileWriter.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            userLock.unlock();
        }
        return false;
    }

}
