package weike.my_service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import weike.shutuier.MainActivity;
import weike.util.Constants;
import weike.util.HttpManager;
import weike.util.HttpTask;

/**
 * Created by Rth on 2015/4/20.
 * 该服务监听有没有消息（其他用户对本用户的留言信息）
 */
public class LocalMessageServer extends Service {

    private String openId;
    private Timer timer;
    private Handler messageNumberHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        openId = getSharedPreferences(Constants.SP_USER,0).getString(Constants.UID,"");
        initHandlers();
    }

    private void initHandlers() {
        messageNumberHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int number = msg.arg1;
                if(number > 0) {
                    Intent intent = new Intent();
                    intent.setAction(MainActivity.LOCAL_MESSAGE_ACTION);
                    intent.putExtra(Constants.MESSAGE_NUMBER,number);
                    sendLocalBroadCast(intent);
                }
            }
        };
    }

    //定时访问后台是否有留言消息
    private void initTimer() {
        timer = new Timer();
        TimerTask  task = new TimerTask() {
            @Override
            public void run() {
                try {
                    if(MainActivity.netConnect) {
                        HttpTask httpTask = new HttpTask(LocalMessageServer.this,Constants.MESSAGENUMBERLINK+openId,
                                messageNumberHandler,null,null);
                        HttpManager.startTask(httpTask);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        timer.schedule(task,0,60*1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(timer == null) {
            initTimer();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        HttpManager.cance();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //发送本地广播
    private void sendLocalBroadCast(Intent intent) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        Log.e("Server","send braocast");
        localBroadcastManager.sendBroadcast(intent);
        intent = null;
    }
}
