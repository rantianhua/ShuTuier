package weike.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.gson.stream.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import weike.data.BookItem;
import weike.data.BookOtherData;
import weike.data.ListBookData;
import weike.data.UserInfoData;

/**
 * Created by Rth on 2015/2/9.
 */
public class Utils {


    //得到显示用户头像的ImageView的大小
    public static Point getUserPhotoSize(Resources res) {
        int size = (int) (64 * res.getDisplayMetrics().density);
        return new Point(size,size);
    }

    public static int getDrawerWidth(Resources res) {
        return (int) res.getDisplayMetrics().widthPixels;
    }


    public static Point getBackgroundSize(Resources res) {
        int width = getDrawerWidth(res);

        int height = (9 * width) / 16;

        return new Point(width,height);
    }

    public static Bitmap getCroppedBitmapDrawable(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap.recycle();
        return output;
    }

    public static Bitmap decodeSampleBitmapFromResource(Resources res, int resId,int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        //先不分配内存
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        //计算缩小比列
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // 分配内存，加载缩略图
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 图片本身的宽高
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static void recycleDrawable(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            bitmapDrawable.getBitmap().recycle();
        }
    }

    public static void loadBitmap(Resources res,ImageView view,int resId,int reqWidth,int reqHeight,int colorId) {
        BitmapWorkerTask task = new BitmapWorkerTask(res,view,reqWidth,reqHeight,colorId);
        task.execute(resId);
    }

    //提取透明位图
    public static Bitmap getAlphaBitmap(Resources res,Bitmap bitmap,int color) {
       Bitmap alphaBitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(alphaBitmap);
        Paint paint = new Paint();
        paint.setColor(res.getColor(color));
        //从原图中提取只包含alpha的位图
        Bitmap newAlpha = bitmap.extractAlpha();
        bitmap.recycle();
        canvas.drawBitmap(newAlpha, 0, 0, paint);
        return alphaBitmap;
    }

    public static void getListData(String content,final String from) {
        if(content != null) {
            if(content.equals("null")) return;
            JsonReader reader = null;
            try{
                reader = new JsonReader(new StringReader(content));
                reader.setLenient(true);
                reader.beginArray();
                while (reader.hasNext()) {
                    reader.beginObject();
                    BookItem item = new BookItem();
                    while (reader.hasNext()) {
                        String key = reader.nextName();
                        Object value = null;
                        if(key.equals("ID") || key.equals("collection") || key.equals("mark") || key.equals("share")) {
                            value = reader.nextInt();
                        }else{
                            value = reader.nextString();
                        }
                        updateData(key, value, item);
                    }
                    ListBookData.getInstance(from).addItems(item);
                    reader.endObject();
                }
                reader.endArray();
            }catch (Exception e) {
                Log.e("Utils/getListData","error in getListData",e);
            }finally {
                try {
                    if(reader != null) {
                        reader.close();
                        reader = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void updateData(String key, Object value, BookItem item) {
        switch (key) {
            case "ID":
                item.setId((int)value);
                break;
            case "Name":
                item.setBookName((String)value);
                break;
            case "InternetImg":
                String url = (String)value;
                url = url.replaceAll("\\\\","");
                Log.d("URL",url);
                item.setImgUrl(url);
                break;
            case "new":
                item.setHowOld((String)value);
                break;
            case "Author":
                item.setAuthorName((String) value);
                break;
            case "Press":
                item.setPublisher((String)value);
                break;
            case "detail":
                item.setDetail((String) value);
                break;
            case "Status":
                item.setStatue((String)value);
                break;
            case "Oprice":
                item.setOriginPrice((String)value);
                break;
            case "Sprice":
                item.setSellPrice((String)value);
                break;
            case "share":
                item.setShareNumber((int)value);
                break;
            case "mark":
                item.setMessageNumber((int)value);
                break;
            case "Other":
                item.setRemark((String)value);
                break;
            case "subMenu":
                item.setSubClassify((String)value);
                break;
            default:
                break;
        }
    }

    public static void getDetailData(String content) {
        if(content == null || content.equals("null")) return;
        JsonReader reader = null;
        try{
            reader = new JsonReader(new StringReader(content));
            reader.setLenient(true);
            reader.beginObject();
            while (reader.hasNext()) {
                String key = reader.nextName();
                if(key.equals("book") || key.equals("publisher")) {
                    reader.beginObject();
                    while (reader.hasNext()) {
                        String k = reader.nextName();
                        String v = reader.nextString();
                        switch (k) {
                            case "ISBN":
                                BookOtherData.getInstance().setISBN(v);
                                break;
                            case "thirdName":
                                BookOtherData.getInstance().setOwnerName(v);
                                break;
                            case "Head":
                                BookOtherData.getInstance().setHeadUrl(v);
                                break;
                            case "weixinNum":
                                BookOtherData.getInstance().setWxNumber(v);
                                break;
                            case "qqNum":
                                BookOtherData.getInstance().setQqNumber(v);
                                break;
                            case "teleNum":
                                BookOtherData.getInstance().setTeleNumber(v);
                                break;
                            case "Mail":
                                BookOtherData.getInstance().setMail(v);
                                break;
                            default:
                                break;
                        }
                    }
                    reader.endObject();
                }else{
                    Map<String,String> mm = new HashMap<>();
                    reader.beginObject();
                    while (reader.hasNext()) {
                        String k = reader.nextName();
                        k = null;
                        reader.beginObject();
                        while (reader.hasNext()) {
                            String kk = reader.nextName();
                            String vv = reader.nextString();
                            mm.put(kk,vv);
                        }
                        reader.endObject();
                    }
                    reader.endObject();
                    BookOtherData.getInstance().getList().add(mm);
                }
            }
            reader.endObject();
        }catch (Exception e) {
            Log.e("Utils/getDetailData","error in getDetailData",e);
        }finally {
            try {
                if(reader != null) {
                    reader.close();
                    reader = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    public static Map<String, String> ObjectToMap(Object obj) throws Exception {
//        Map<String, String> mapValue = new HashMap<String, String>();
//        Class<?> cls = obj.getClass();
//        Field[] fields = cls.getDeclaredFields();
//        for (Field field : fields) {
//            String name = field.getName();
//            String strGet = "get" + name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
//            Method methodGet = cls.getDeclaredMethod(strGet);
//            Object object = methodGet.invoke(obj);
//            String value = object != null ? object.toString() : "";
//            mapValue.put(name, value);
//        }
//        return mapValue;
//    }

    public static boolean isCommentSucceed(String content) {
        Log.i("isCommentSucceed",content + "isCommentSucceed");
        if(content == null || content.equals("null")) return false;
        JsonReader reader = null;
        Boolean value = false;
        try {
            reader = new JsonReader(new StringReader(content));
            reader.setLenient(true);
            reader.beginObject();
            while (reader.hasNext()) {
                String key  = reader.nextName();
                value =  Boolean.valueOf(reader.nextString());
            }
            reader.endObject();
        }catch (Exception e) {
            Log.e("Utils/getDetailData","error in isCommentSucceed",e);
        }finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(reader != null) {
                reader = null;
            }
        }
        return value;
    }

    public static String getPicturePath() {
        //先判断手机是否有SD卡
        String path = null;
        if(android.os.Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED )) {
            path = Environment .getExternalStorageDirectory() + "/ShuTuierPictures";
        }else {
            path = Environment .getDataDirectory() + "/ShuTuierPictures";
        }
        try{
            File f = new File(path);
            if(!f.exists()) {
                f.mkdir();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("GetPicturePath","path is " + path );
        return path;
    }

    //访问豆瓣api的url
    public static String getDouBanUrl(String isbn) {
        return Constants.BASEDOUBANURL + isbn + Constants.DOUBANURLFEILS;
    }

    //解析来自豆瓣api的json数据
    public static Map<String,String> cutDoubanData(String data){
        JsonReader reader = null;
        Map<String ,String> map = new HashMap<>();
        try {
            reader = new JsonReader(new StringReader(data));
            reader.beginObject();
            while (reader.hasNext()) {
                String key = reader.nextName();
                if(key.equals("author")) {
                    reader.beginArray();
                    StringBuilder sb = new StringBuilder();
                    while (reader.hasNext()) {
                        sb.append(reader.nextString());
                    }
                    reader.endArray();
                    map.put(key,sb.toString());
                    sb = null;
                }else {
                    map.put(key, reader.nextString());
                }
            }
            reader.endObject();
        }catch (Exception  e) {
            Log.e("cutDouBanData","error in cut douban json",e);
        }
        return map;
    }

    //得到屏幕的宽度
    public static int getWindowWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    //得到屏幕的高度
    public static int getWindowHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static void loadBlurBitmap(final Context con, final ImageView userBg, final int user_bg, final int radius,final int w,final int h) {

        new AsyncTask<Integer,String,Bitmap>() {
            @Override
            protected void onPostExecute(Bitmap o) {
                if(o != null) {
                    userBg.setImageBitmap(o);
                }
            }

            @Override
            protected Bitmap doInBackground(Integer[] params) {
                int picId = params[0];
                //先取图片的缩略图在进行高斯模糊处理
                //decodeSampleBitmapFromResource(con.getResources(), picId, w, h)
                return Blur.fastblur(con, BitmapFactory.decodeResource(con.getResources(), user_bg), radius);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
        }.execute(user_bg,null,null);
    }

    public static String getMyCommitUrl(String id,String type) {
        return Constants.BASEMYCOMMIT + id + "/name/"  + type;
    }

    public static ArrayList<Map<String,String>> getMyCommitData(String content) {
        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
        if(!TextUtils.isEmpty(content)) {
            JsonReader reader = null;
            try {
                reader = new JsonReader(new StringReader(content));
                reader.beginArray();
                while (reader.hasNext()) {
                    Map<String ,String> map = new HashMap<>();
                    reader.beginObject();
                    while (reader.hasNext()) {
                        map.put(reader.nextName(),reader.nextString());
                    }
                    reader.endObject();
                    list.add(map);
                }
                reader.endArray();
            }catch (Exception  e) {
                Log.e("getMyCommitData","error in cut mySell json",e);
            }
        }
        return list;
    }

    public static boolean loginSuccess(String content,Context con) {
        if(TextUtils.isEmpty(content)) {
            return false;
        }else {
            Log.e("loginSuccess",content);
            JSONObject json = null;
            try {
                json = new JSONObject(content);
                //将用户信息保存在本地
                SharedPreferences sp = con.getSharedPreferences(Constants.SP_USER,0);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(Constants.UID, UserInfoData.getInstance().getOpenId());
                editor.putString(Constants.LOGIN_WAY, UserInfoData.getInstance().getLoginWay());
                editor.putString(Constants.USERURL,json.getString("Head"));
                editor.putString(Constants.NICNAME,json.getString("thirdName"));
                editor.putString(Constants.SEX,json.getString("Sex"));
                editor.putString(Constants.Birthday,json.getString("birth"));
                editor.putString(Constants.Hobbit,json.getString("Interest"));
                editor.putString(Constants.School,json.getString("School"));
                editor.putString(Constants.PhoneNumber,json.getString("teleNum"));
                editor.putString(Constants.QQNumber,json.getString("qqNum"));
                editor.putString(Constants.WxNumber,json.getString("weixinNum"));
                editor.putString(Constants.Email,json.getString("Mail"));
                editor.putBoolean(weike.util.Constants.USER_ONLINE_KEY,true);
                editor.apply();
            } catch (JSONException e) {
                e.printStackTrace();
                if(json != null) {
                    json  = null;
                }
                return false;
            }
            if(json != null) {
                json  = null;
            }
            return true;
        }
    }
}
