package diexun.main;

import java.io.File;
import java.util.Calendar;

import diexun.config.Constants;
import diexun.util.WorkDirecMake;

/**
 * 文件扫描类
 *
 * @author xiaolong
 *
 */
public class Scanner {

    private static String OS = System.getProperty("os.name").toLowerCase();
    public static final boolean IS_NETWORK;// = false; //false内网测试 , true表示外网发布

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
    public static final String APPAREL_FTP_ADDRESS;
    public static final String APPAREL_FTP_USER;
    public static final String APPAREL_FTP_PSW;
    public static final String APPAREL_SERVICE_COLUMN_URL;
    public static final String APPAREL_SERVICE_ATTRIBUTE_URL;

    public static final String SHOE_WEBQUERY;
    public static final String SHOE_WEBINSERT;
    public static final String SHOE_FTPBEAN;

    public static final String BAG_WEBQUERY;
    public static final String BAG_WEBINSERT;
    public static final String BAG_FTPBEAN;

    public static final String EPD_WEBQUERY;
    public static final String EPD_WEBINSERT;
    public static final String EPD_FTPBEAN;

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
            APPAREL_FTP_ADDRESS = "192.168.2.9";
            APPAREL_FTP_USER = "sxxl_sso";
            APPAREL_FTP_PSW = "<sxxl_sso>";
            APPAREL_SERVICE_COLUMN_URL = "http://service.sxxl.cc/Server/column?WSDL";
            APPAREL_SERVICE_ATTRIBUTE_URL = "http://service.sxxl.cc/Server/attribute?WSDL";

            SHOE_WEBQUERY = "http://service.shoes.cc/Server/Relationattribute?WSDL";
            SHOE_WEBINSERT = "http://service.shoes.cc/Server/Column?WSDL";
            SHOE_FTPBEAN = "192.168.2.9;21;xieyetest;123456";

            BAG_WEBQUERY = "http://service.bags.cc/Server/Relationattribute?WSDL";
            BAG_WEBINSERT = "http://service.bags.cc/Server/Column?WSDL";
            BAG_FTPBEAN = "192.168.2.9;21;xiangbao;xiangbao123";

            EPD_WEBQUERY="http://service.manager.diexun.dev/Server/uploadpic?wsdl";
            EPD_WEBINSERT="http://service.manager.diexun.dev/Server/autoupload?wsdl";
            EPD_FTPBEAN="192.168.2.9;21;epdtest;123456";

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
            APPAREL_FTP_ADDRESS = "192.168.1.14";
            APPAREL_FTP_USER = "diexun_Automation";
            APPAREL_FTP_PSW = "<dXDx2014_sdfescksfvc>";
            APPAREL_SERVICE_COLUMN_URL = "http://service.sxxl.com/Server/column?WSDL";
            APPAREL_SERVICE_ATTRIBUTE_URL = "http://service.sxxl.com/Server/attribute?WSDL";

            SHOE_WEBQUERY = "http://search.shoes.xiebaowang.com/Server/Relationattribute?WSDL";
            SHOE_WEBINSERT = "http://search.shoes.xiebaowang.com/Server/Column?WSDL";
            SHOE_FTPBEAN = "121.201.55.164;21;xiebao_Automation;7hHrsRVH29609F0B8xQX";

            BAG_WEBQUERY = "http://search.bags.xiebaowang.com/Server/Relationattribute?WSDL";
            BAG_WEBINSERT = "http://search.bags.xiebaowang.com/Server/Column?WSDL";
            BAG_FTPBEAN = "121.201.55.164;21;xiebao_Automation;7hHrsRVH29609F0B8xQX";

            EPD_WEBQUERY = "http://search.epd.shoes.xiebaowang.com/Server/Relationattribute?WSDL";
            EPD_WEBINSERT = "http://search.epd.shoes.xiebaowang.com/Server/Column?WSDL";
            EPD_FTPBEAN = "121.201.55.166;21;xiebaoepd_Automation;<7hHrsRVH29609F0B8xQX>";
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

	private void hander(File website) {
		File[] listFiles = website.listFiles();
		for (File file : listFiles) {
			System.out.println(file.getName());
		}
	}
	
	private boolean isSubjectD(File file) {
		if (file.getName().contains("D")) {
			return true;
		}
		return false;
	}
}
