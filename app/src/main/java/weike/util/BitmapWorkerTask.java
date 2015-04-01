package weike.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by Rth on 2015/2/10.
 */
public class BitmapWorkerTask extends AsyncTask {

    //缓存ImageView的弱引用
    private final WeakReference<ImageView> imageViewWeakReference;
    //图片的资源id
    private int data = 0;
    private Resources resources = null;
    private int reqWidth=0,reqHeight=0,colorId = 0;

    public BitmapWorkerTask(Resources res,ImageView imageView,int width,int height,int colorId) {
        //确保imageView会被回收
        imageViewWeakReference = new WeakReference<>(imageView);
        resources = res;
        reqWidth = width;
        reqHeight = height;
        this.colorId = colorId;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        data = (int)params[0];
        return Utils.decodeSampleBitmapFromResource(resources,data, reqWidth,reqHeight);
    }

    @Override
    protected void onPostExecute(Object o) {
        if(o != null) {
            final ImageView imageView = imageViewWeakReference.get();
            if(imageView != null) {
                if(data == -1) {
                    imageView.setImageBitmap((Bitmap)o);
                }
                else {
                    imageView.setImageBitmap(Utils.getAlphaBitmap(resources,(Bitmap)o,colorId));
                }
            }
        }
    }
}
