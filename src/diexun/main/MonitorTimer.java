package diexun.main;

import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class MonitorTimer {

    private Timer timer;
    public static final int NORMAL = 1;// 正常的
    public static final int TIMELY = 2;// 及时的
    public static final long PACK_DELAY = 300000; //延迟5分钟打包

    private final static ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(1);

    public MonitorTimer() {
    }

    public void startMonitor() {
        timer = new Timer();
        ScanTimerTask normalTask = new ScanTimerTask(this);
        normalTask.setType(NORMAL);
//        scheduled.scheduleWithFixedDelay(normalTask, 0, Scanner.NORMAL_INTERVAL, TimeUnit.MILLISECONDS);

        timer.schedule(normalTask, 0, Scanner.NORMAL_INTERVAL);
    }

    public ScheduledExecutorService getScheduledService() {
        return scheduled;
    }

    public Timer getTimer() {
        if (timer == null) {
            timer = new Timer();
        }
        return timer;
    }
}
