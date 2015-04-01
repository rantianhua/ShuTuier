package weike.util;

/**
 *该类检查网络是否已连接
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector {

    public static boolean isConnectingToInternet(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
          if (connectivity != null)
          {
              NetworkInfo[] info = connectivity.getAllNetworkInfo();
              connectivity = null;
              if (info != null)
                  for(NetworkInfo ni : info) {
                      if(ni.getState() == NetworkInfo.State.CONNECTED) {
                          return true;
                      }
                  }
          }
          return false;
    }
}
