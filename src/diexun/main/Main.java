package diexun.main;

import diexun.tcp.NettyServer;

/**
 * @author xiaolong
 */
public class Main {
    public static String PORT = "9090";
    public static String WEBSITE = "epdshoebag";
	
	public static void main(String[] args) {
        if (args != null && args.length == 2) {
        	PORT = args[0].trim();
        	WEBSITE = args[1].trim();
        	
            final MonitorTimer mt = new MonitorTimer(); // 扫描任务监听器
            mt.startMonitor();
            
            final NettyServer tcpServer = new NettyServer(); // TCP监听器
            tcpServer.init();
            tcpServer.start();
        }
	}

}
