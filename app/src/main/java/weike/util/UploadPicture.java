package weike.util;

import android.util.Log;

import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import java.io.File;
import java.io.FileNotFoundException;

import qiniu.PutPolicy;

/**
 * Created by Rth on 2015/4/13.
 */
public class UploadPicture  {

    private String filePath = null;  //待上传的图片文件
    private UpCompletionHandler upHandler = null;
    private final String TAG = "UploadPicture";

    public UploadPicture(String filePath, UpCompletionHandler upHandler) {
        this.filePath = filePath;
        this.upHandler = upHandler;
    }

    public void upToQiNiu() throws Exception{
        PutPolicy policy = new PutPolicy("shutuier",System.currentTimeMillis()+3600,1);
        String token = policy.token(null);
        Log.e(TAG,"uptoken is " + token);
        File f = new File(filePath);
        if(f.exists()) {
            UploadManager manager = new UploadManager();
            manager.put(f,null,token,upHandler,null);
        }else {
            throw new FileNotFoundException("file not found for path " + filePath);
        }
    }
}
