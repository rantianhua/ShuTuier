package weike.my_service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by Rth on 2015/4/20.
 * 该服务监听有没有消息（其他用户对本用户的留言信息）
 */
public class LocalMessageServer extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        showToast("services on create");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showToast("services on onStartCommand");
        return START_NOT_STICKY;
    }

    //耗时操作
    private void doInBackground() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        showToast("services on destroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        showToast("something bing on service");
        return null;
    }

    private void showToast(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
