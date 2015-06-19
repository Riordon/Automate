package diexun.main;

import java.util.TimerTask;

public class ScanTimerTask extends TimerTask {

    private MonitorTimer mt;
    private int type; // 正常的还是及时的

    public ScanTimerTask(MonitorTimer mt) {
        this.mt = mt;
    }

    @Override
    public void run() {
        if (type == MonitorTimer.NORMAL) { // 正常模式
    //       SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss ");
    //        System.out.println(sdf.format(new Date()) + " :我是监听器,我现在" + (Scanner.NORMAL_INTERVAL / 60000) + "分钟运行一次");
            Scanner fs = new Scanner(mt);
            fs.startScanning();
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
