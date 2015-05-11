package weike.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import weike.data.BookItem;
import weike.data.BookMessageListData;
import weike.data.BookOtherData;
import weike.data.ListBookData;
import weike.data.MessageBookData;
import weike.data.UserInfoData;

/**
 * Created by Rth on 2015/2/9.
 */
public class Utils {


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

    public static void recycleBitmap(ImageView imageView) {
        if(imageView == null) return;
        Drawable drawable = imageView.getDrawable();
        if(drawable != null && drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if(bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }

    public static Bitmap showTakenPictures(String path,int reqWidth,int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path,options);
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
        if(!TextUtils.isEmpty(content)) {
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
                        String value = reader.nextString();
//                        if(key.equals("ID") || key.equals("collection") || key.equals("mark") || key.equals("share")) {
//                            value = reader.nextInt();
//                        }else{
//                            value = reader.nextString();
//                        }
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
    private static void updateData(String key, String value, BookItem item) {
        switch (key) {
            case "ID":
                item.setId(value);
                break;
            case "Name":
                item.setBookName(value);
                break;
            case "InternetImg":
                String url = value;
                url = url.replaceAll("\\\\","");
                Log.d("URL",url);
                item.setImgUrl(url);
                break;
            case "new":
                item.setHowOld(value);
                break;
            case "Author":
                item.setAuthorName( value);
                break;
            case "Press":
                item.setPublisher(value);
                break;
            case "detail":
                item.setDetail( value);
                break;
            case "Status":
                item.setStatue(value);
                break;
            case "Oprice":
                item.setOriginPrice(value);
                break;
            case "Sprice":
                item.setSellPrice(value);
                break;
            case "share":
                item.setShareNumber(value);
                break;
            case "mark":
                item.setMessageNumber(value);
                break;
            case "Other":
                item.setRemark(value);
                break;
            case "subMenu":
                item.setSubClassify(value);
                break;
            default:
                break;
        }
    }

    public static void getDetailData(String content) {
        if(TextUtils.isEmpty(content)) return;
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
                            case "share":
                                BookOtherData.getInstance().setShareNumber(v);
                                break;
                            case "mark":
                                BookOtherData.getInstance().setMarkNumber(v);
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

    public static boolean isCommentSucceed(String content) {
        Log.e("isCommentSucceed",content + "isCommentSucceed");
        if(TextUtils.isEmpty(content)) return false;
        JsonReader reader = null;
        Boolean value = false;
        try {
            reader = new JsonReader(new StringReader(content));
            reader.setLenient(true);
            reader.beginObject();
            while (reader.hasNext()) {
                String key  = reader.nextName();
                value =  reader.nextBoolean();
                key = null;
            }
            reader.endObject();
        }catch (Exception e) {
            Log.e("Utils/getDetailData","error in isCommentSucceed",e);
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

    public static String getMyCommitUrl(String openId,String mode) {
        return Constants.BASEMYCOMMIT + mode + "&openId="  + openId;
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
                editor.putString(Constants.School,json.getString("School"));
                editor.putString(Constants.PhoneNumber,json.getString("teleNum"));
                editor.putString(Constants.QQNumber,json.getString("qqNum"));
                editor.putString(Constants.WxNumber,json.getString("weixinNum"));
                editor.putString(Constants.Email,json.getString("Mail"));
                editor.putBoolean(weike.util.Constants.USER_ONLINE_KEY,true);
                editor.apply();
            } catch (JSONException e) {
                Log.e("loginSuccess","error in save baseInfo",e);
                if(json != null) {
                    json  = null;
                }
                return false;
            }
            json = null;
            return true;
        }
    }

    //生成缩略图文件
    public static void haveSimpleFile(String pic,int w,int h) {
        try {
            Bitmap bitmap = Utils.showTakenPictures(pic,w,h);
            if(bitmap != null) {
                FileOutputStream outputStream = new FileOutputStream(new File(Utils.getPicturePath()+Constants.TEMP_PIC));
                bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取被留言的书的列表
    public static void getMessageList(String content) {
        if(TextUtils.isEmpty(content)) return;
        BookMessageListData data = BookMessageListData.getInstance();
        JsonReader reader = new JsonReader(new StringReader(content));
        try {
            reader.beginArray();
            while (reader.hasNext()) {
                MessageBookData messageBookData = new MessageBookData();
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if(name.equals("Name")) {
                        messageBookData.setName(reader.nextName());
                    }
                    if(name.equals("ID")) {
                        messageBookData.setId(reader.nextString());
                    }
                }
                reader.endObject();
                data.getList().add(messageBookData);
            }
            reader.endArray();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
