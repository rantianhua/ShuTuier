package weike.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Rth on 2015/3/19.
 */
public class DouBanTask implements Runnable {
    private Handler han;
    private String url;

    public DouBanTask(String url,Handler han) {
        this.url = url;
        this.han = han;
    }

    @Override
    public void run() {
        getBookFromDouBan();
    }

    private void getBookFromDouBan() {
        Message msg = han.obtainMessage();
        StringBuilder content = null;
        try {
            URL l = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) l.openConnection();
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader reader = new InputStreamReader(connection.getInputStream(),"utf-8");
                int i;
                content = new StringBuilder();
                while ((i = reader.read()) != -1) {
                    content.append((char)i);
                }
                Log.d("DouBanTask","content is " + content.toString());
                reader.close();
                reader = null;
            }else {
                Log.d("DouBanTask","resultCOde is " + connection.getResponseCode());
                msg.what = 1;
            }
            connection.disconnect();
            connection = null;
        }catch (Exception e) {
            e.printStackTrace();
        }
        if(content != null && msg.what != 1) {
            msg.obj = content.toString();
            msg.what = 0;
        }else {
            if(msg.what != 1)
                msg.what = 2;
        }
        han.sendMessage(msg);
    }
}
