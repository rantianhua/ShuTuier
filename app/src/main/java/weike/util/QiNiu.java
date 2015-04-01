package weike.util;

import android.util.Log;

import com.loopj.android.http.Base64;

import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Rth on 2015/3/18.
 */
public class QiNiu {
    private static final String bucket = "shutuier";    //七牛空间名
    private static final String SK = "frWQI8QPogc9sJSWLtjiYU5m1ER7ogy5-3nZWsox";
    private static final String AK = "gTh9TiM54BbogCWEWKsdBy9Ub2A0K1lTpSoLRl_Y";

    public static  String getUploadToken(String fileName) {

        String token = null;
        try{
            //一：将上传策略转换成Json格式
            JSONObject policy = new JSONObject();
            StringBuilder scope = new StringBuilder();
            scope.append(bucket);
            scope.append(":");
            scope.append(fileName);
            policy.put("scope",scope.toString());   //只允许上传指定key的文件，而key就是文件名
            scope = null;
            policy.put("deadline",getTimer());
            //policy.put("insertOnly",1); //不允许修改
            //policy.put("callbackFetchKey",1);   //启动fetchKey上传模式
            // policy.put("returnBody ","{\"name\" : \"$(fname))}");
            Log.d("policy",policy.toString());

            //二：对JSON编码的上传策略进行URL安全的Base64编码，得到待签名字符串：
            String encodPutPolicy = Base64.encodeToString(policy.toString().getBytes("UTF-8"), Base64.URL_SAFE);
            Log.i("encodPutPolicy","encodPutPolicy is "  + encodPutPolicy);

            //三：使用SecertKey对上一步生成的待签名字符串计算HMAC-SHA1签名：并对签名进行URL安全的Base64编码：
            String encodSign = hmac_sha1(SK,encodPutPolicy);
            Log.i("encodSign","encodSign is "  + encodSign);

            //五：将AccessKey、encodedSign和encodedPutPolicy用:连接起来：
            token = AK + ':' + encodSign + ':' + encodPutPolicy;
            Log.i("token","token is "  + token);

        }catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }

    private static long getTimer() {
        return System.currentTimeMillis() + 3600;    //上传凭证的有效期
    }

    //对字符串进行HMAC-SHA1签名
    private static String hmac_sha1(String key,String message) {
        String reString = null;
        try {
            byte[] data = key.getBytes("UTF-8");
            //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
            SecretKey secretKey = new SecretKeySpec(data, "HmacSHA1");
            //生成一个指定 Mac 算法 的 Mac 对象
            Mac mac = Mac.getInstance("HmacSHA1");
            //用给定密钥初始化 Mac 对象
            mac.init(secretKey);

            byte[] text = message.getBytes("UTF-8");
            //完成 Mac 操作
            byte[] text1 = mac.doFinal(text);
            reString = Base64.encodeToString(text1, Base64.URL_SAFE);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return reString;
    }

}
