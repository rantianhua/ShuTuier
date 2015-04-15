package weike.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Rth on 2015/4/10.
 * 检查网络状态的接收器
 */
public class ConnectReceiver extends BroadcastReceiver {

    private GetNetState getNetState = null;

    public ConnectReceiver(GetNetState getNetState) {
        this.getNetState = getNetState;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conn = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();
        if(info != null) {
            if(info.getState() == NetworkInfo.State.CONNECTED) {
                getNetState.sendState(true);
            }else {
                getNetState.sendState(false);
            }
        }else {
            getNetState.sendState(false);
        }
    }

    //向目标发送网络状态的接口
    public interface GetNetState {
        void sendState(boolean state);
    }
}
