package diexun.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import diexun.bean.CommonBean;
import diexun.bean.EPDClothingBean;
import diexun.bean.EPDShoebagBean;
import diexun.cache.CacheSystem;
import diexun.config.Constants;
import diexun.execute.CallableImpl;
import diexun.util.WorkDirecMake;

/**
 * @author xiaolong
 *
 */
public class Scanner {

    private static String OS = System.getProperty("os.name").toLowerCase();
    public static final boolean IS_NETWORK;// = false; //false内网测试 , true表示外网发布

    public static final ExecutorService service = Executors.newFixedThreadPool(50);
    
    static {
        System.out.println("OS: " + OS);
        if (OS.contains("win")) // window
        {
            IS_NETWORK = false;
        } else // linux
        {
            IS_NETWORK = true;
        }
    }
    public static final String XML_CODING;//项目在工具运行时用UTF-8, 打包后改称GBK 
    public static final String COMSEQARTOR;
    public static final String ROOT_DIRECTORY;// 业务工作目录
    public static final String SYSTEM_ROOT_DIR; // 系统处理后的主题存放处
    public static final String USER_XML_PATH; //用户配置文件存放位置
    public static final String CACHE_ROOT;
    public static final String LOG_PATH;

    public static final String EPD_SHOEBAG_WEBQUERY;
    public static final String EPD_SHOEBAG_WEBINSERT;
    public static final String EPD_SHOEBAG_FTPBEAN;

    public static int NORMAL_INTERVAL;

    static {
        if (!IS_NETWORK) {
            NORMAL_INTERVAL = 5000;//测试5秒一次
            /*Windows路径*/
            XML_CODING = "UTF-8";
            COMSEQARTOR = File.separator + File.separator;
            ROOT_DIRECTORY = "e:" + File.separator + "\u4e1a\u52a1\u5de5\u4f5c\u76ee\u5f55"; //业务工作目录
            SYSTEM_ROOT_DIR = "e:" + File.separator + "AutoUploadFile";
            CACHE_ROOT = SYSTEM_ROOT_DIR + File.separator + "cache";
            LOG_PATH = SYSTEM_ROOT_DIR + File.separator + "server_logs";
            USER_XML_PATH = SYSTEM_ROOT_DIR + File.separator + "config" + File.separator + "user.xml";

            EPD_SHOEBAG_WEBQUERY="http://service.manager.diexun.dev/Server/getattrinfo?wsdl";
            EPD_SHOEBAG_WEBINSERT="http://service.manager.diexun.dev/Server/autoupload?wsdl";
            EPD_SHOEBAG_FTPBEAN="192.168.2.9;21;epdtest;123456";

        } else {
            NORMAL_INTERVAL = 20000;//外网20秒一次
            /*Liunx路径*/
            XML_CODING = "UTF-8";
            COMSEQARTOR = File.separator;
            ROOT_DIRECTORY = File.separator + "data" + File.separator
                    + "Automation" + File.separator + "\u4e1a\u52a1\u5de5\u4f5c\u76ee\u5f55";
            SYSTEM_ROOT_DIR = File.separator + "data" + File.separator
                    + "Automation" + File.separator + "AutoUploadFile";
            CACHE_ROOT = SYSTEM_ROOT_DIR + File.separator + "cache";
            LOG_PATH = SYSTEM_ROOT_DIR + File.separator + "server_logs";
            USER_XML_PATH = SYSTEM_ROOT_DIR + File.separator + "config" + File.separator + "user.xml";

            EPD_SHOEBAG_WEBQUERY = "http://search.epd.shoes.xiebaowang.com/Server/Relationattribute?WSDL";
            EPD_SHOEBAG_WEBINSERT = "http://search.epd.shoes.xiebaowang.com/Server/Column?WSDL";
            EPD_SHOEBAG_FTPBEAN = "121.201.55.166;21;xiebaoepd_Automation;<7hHrsRVH29609F0B8xQX>";
        }
    }

    private MonitorTimer mt;

    public static volatile boolean exit = false;

    public Scanner(MonitorTimer mt) {
        this.mt = mt;
    }

    /**
     * 定时器30秒钟一次开始扫描函数
     */
    public void startScanning() {
        if (!exit) {
            Calendar cal = Calendar.getInstance();
            int day = cal.get(Calendar.DATE);
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);

            String day_dir = new StringBuilder().append(ROOT_DIRECTORY)
                    .append(File.separator).append(year).append(File.separator)
                    .append(month).append(File.separator).append(day).toString();
            File file = new File(day_dir);
            if (!file.exists()) { // 如果找不到该目录下的路径 ,去生成目录
                new WorkDirecMake(day_dir).makeFiles();
            }
            
            File file1 = new File(CACHE_ROOT);

            if (!file1.exists()) {
                new WorkDirecMake().makeCacheFile(CACHE_ROOT);
                new WorkDirecMake().makeCacheTheme(CACHE_ROOT, year, month, day);
            }
            scanNextLevelFile(file, day, month, year); // 扫描日期下面的目录
        }
    }

    private void scanNextLevelFile(File upFile, int day, int month, int year) {
        File[] files = upFile.listFiles();
        for (File website : files) {
        	if (website.getName().equals(Constants.MAP_EN_CH.get(Main.WEBSITE))) { //只处理指定网站的文件夹
        		hander(website);
        	}
        }
    }

	private void hander(File fFile) {
		File[] listFiles = fFile.listFiles();
		for (File file : listFiles) {
			if (file.getName().endsWith(".jpg")) {
				handerSubject(fFile);
				break;
			} else if(file.getName().endsWith("#")) { //是主题文件夹
				handerSubjectD(file);
			} else {
			}
				hander(file);
			}
		}
	private void handerSubjectD(File file) { //处理主题文件夹
		File[] subjectFiles = file.listFiles();
		for (File fSub : subjectFiles) {
			handerSubject(fSub);
		}
	}
	
	private void handerSubject(File fFile) { //处理主题
		String absolutePath = fFile.getAbsolutePath();
		if (CacheSystem.isScan(absolutePath)) return;
		CacheSystem.writeTheme(absolutePath);
		String[] split = absolutePath.split("\\\\");
		CommonBean bean = null;
		String webSite = split[5];
		switch (webSite) {
			case "EPD鞋包":
				bean = new EPDShoebagBean();
				break;
			case "EPD服装":
				bean = new EPDClothingBean();
				break;
			default:
				break;
		}
		
		for (int i = 6; i < split.length-1; i++) {
			bean.addAttribute(split[i]);
		}
		bean.setTitle(split[split.length-1]);
		
		CallableImpl impl = new CallableImpl(bean, fFile);
		service.execute(impl);
	}

	private boolean isSubjectD(String name) {
		if (name.contains("#")) {
			return true;
		}
		return false;
	}
	
    /**
     * 处理日志
     *
     * @param mesg
     */
	private static final ReentrantLock logLock = new ReentrantLock();
    public static void proLog(String mesg) {
        logLock.lock();
        try {
            Calendar cal = Calendar.getInstance();
            int day = cal.get(Calendar.DATE);
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);
            String logFileName = new StringBuilder().append(year).append("-")
                    .append(month).append("-").append(day).append(".txt")
                    .toString();
            File logRoot = new File(LOG_PATH);
            if (!logRoot.exists()) {
                logRoot.mkdirs();
            }
            File txtFile = new File(LOG_PATH + File.separator + logFileName);
            if (!txtFile.exists()) {
                try {
                    txtFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(
                        txtFile.getAbsolutePath(), true));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String curDate = sdf.format(new Date());
                String outputMesg = new StringBuilder().append(curDate)
                        .append(": ").append(mesg).toString();
                bw.write(outputMesg);
                bw.write("\r\n");
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != bw) {
                    try {
                        bw.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Scanner.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } finally {
            logLock.unlock();
        }
    }
}
