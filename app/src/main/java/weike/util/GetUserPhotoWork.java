package weike.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Rth on 2015/4/13.
 * 该类用来统一获取用户头像并显示的方式
 */
public class GetUserPhotoWork extends AsyncTask<Void,Void,Bitmap>{

    private final String TAG = "GetUserPhotoWork";
    //缓存ImageView的弱引用
    private final WeakReference<ImageView> imageViewWeakReference;
    private Context  context = null;
    private boolean round;

    public GetUserPhotoWork(ImageView img,Context con,boolean round) {
        //确保imageView会被回收
        imageViewWeakReference = new WeakReference<>(img);
        context = con;
        this.round = round;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap bitmap = null;
        //现在本地查找
        String path  = Utils.getPicturePath() + Constants.USERICONFILE;
        try {
            File f  = new File(path);
            if(f.exists()) {
                bitmap = BitmapFactory.decodeFile(path);
                Log.e(TAG,"从本地文件中获取头像");
            }else {
                SharedPreferences sp = context.getSharedPreferences(Constants.SP_USER,0);
                String picUrl = sp.getString(Constants.USERURL,"");
                if(!TextUtils.isEmpty(picUrl)) {
                    URL url = new URL(picUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap =  BitmapFactory.decodeStream(is);
                    //保存到本地
                    FileOutputStream out  = new FileOutputStream(f);
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
                    out.flush();
                    out.close();
                    out = null;
                    Log.e(TAG, "从网络中获取头像");
                    is.close();
                    is = null;
                    conn.disconnect();
                    conn = null;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "error in open user photo file ", e);
        }
        if(bitmap == null) {
            return  null;
        }
        if(round) {
            return Utils.getCroppedBitmapDrawable(bitmap);
        }else {
            return bitmap;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap != null) {
            final ImageView imageView = imageViewWeakReference.get();
            if(imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
