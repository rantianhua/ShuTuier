package weike.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Rth on 2015/2/22.
 */
public class HttpManager {

    private static HttpManager instance = null;
    private static int NUMBER_OF_CORES ;    //处于活动的内核数量
    private final BlockingQueue<Runnable> decodeWorkQueue; //队列任务
    private static final int KEEP_ALIVE_TIME = 1;   //线程被关闭前保持空闲状态的时间
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;  //解译时间的时间单位值
    private ThreadPoolExecutor threadPool = null;

    static {
        //创建一个单一分静态实例
        instance = new HttpManager();
    }

    //私有构造方法
    private HttpManager() {
        NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
        decodeWorkQueue = new LinkedBlockingQueue<Runnable>();
        threadPool = new ThreadPoolExecutor(
                NUMBER_OF_CORES,       // Initial pool size
                NUMBER_OF_CORES,       // Max pool size
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                decodeWorkQueue);
    }

    public static void startTask(HttpTask task) {
        instance.threadPool.execute(task);
    }

    public static void startTask(DouBanTask task) {
        instance.threadPool.execute(task);
    }

}
