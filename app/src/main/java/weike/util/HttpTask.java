package weike.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

import weike.data.CommentData;
import weike.data.CommitBookData;
import weike.shutuier.LoginActivity;

/**
 * Created by Rth on 2015/2/22.
 */
public class HttpTask implements Runnable {

    private String url = null;
    private HttpClient client = null;
    private HttpGet get = null;
    private HttpPost post = null;
    private HttpResponse response = null;
    private HttpEntity entity = null;
    private Handler handler = null;
    private String from = null; //区分来自哪里的任务
    private String method = "get";
    private String content = null;
    private Context con = null;

    public HttpTask(Context context,String url,Handler handler,String from,String method) {
        this.url = url;
        this.handler = handler;
        this.from = from;
        if(method != null) {
            this.method = method;
        }
        this.con = context;
    }

    @Override
    public void run() {
        client = new DefaultHttpClient();
        switch (method) {
            case "get":
                 doGet();
                break;
            case "post":
                Log.i("post","post");
                doPost();
                break;
            default:
                break;
        }
    }

    private void doPost() {
        List<NameValuePair> list = new ArrayList<>();
        if(from.equals("CommitBook")) {
            CommitBookData data = CommitBookData.getInstance();
            NameValuePair pair1 = new BasicNameValuePair("publisher","1");
            NameValuePair pair2 = new BasicNameValuePair("Name",data.getBookName());
            NameValuePair pair3 = new BasicNameValuePair("Author", data.getBookAuthor());
            NameValuePair pair4 = new BasicNameValuePair("Press",data.getPublisher());
            NameValuePair pair5 = new BasicNameValuePair("Oprice",data.getoPrice());
            NameValuePair pair6 = new BasicNameValuePair("Sprice",data.getsPrice());
            NameValuePair pair7 = new BasicNameValuePair("Number", data.getBookNumber());
            NameValuePair pair8 = new BasicNameValuePair("Menu",data.getMainClassify());
            NameValuePair pair9 = new BasicNameValuePair("subMenu",data.getSubClassify());
            NameValuePair pair10 = new BasicNameValuePair("Other",data.getRemark());
            NameValuePair pair11= new BasicNameValuePair("InternetImg",data.getCoverUrl());
            NameValuePair pair12= new BasicNameValuePair("new", data.getHowOld());
            NameValuePair pair13= new BasicNameValuePair("Status",data.getStatus());
            NameValuePair pair14= new BasicNameValuePair("Prule", data.getSendCondition());
            NameValuePair pair15= new BasicNameValuePair("detail",data.getDescription());
            NameValuePair pair16= new BasicNameValuePair("ISBN",data.getIsbn());
            list.add(pair1);
            list.add(pair2);
            list.add(pair3);
            list.add(pair4);
            list.add(pair5);
            list.add(pair6);
            list.add(pair7);
            list.add(pair8);
            list.add(pair9);
            list.add(pair10);
            list.add(pair11);
            list.add(pair12);
            list.add(pair13);
            list.add(pair14);
            list.add(pair15);
            list.add(pair16);
            CommitBookData.clear();
        }else if(from.equals(LoginActivity.TAG)){
            SharedPreferences sp = con.getSharedPreferences(Constants.SP_USER,0);
            list.add(new BasicNameValuePair("thirdName",sp.getString(Constants.QQNICNAME,"")));
            list.add(new BasicNameValuePair("OpenId",sp.getString(com.tencent.connect.common.Constants.PARAM_OPEN_ID,"")));
            list.add(new BasicNameValuePair("Sex", sp.getString(Constants.SEX,"")));
            list.add(new BasicNameValuePair("Head",sp.getString(Constants.QQICONURL, "")));
            Log.e("list is " ,list.toString());
        } else{
            CommentData data = CommentData.getInstance();
            NameValuePair pair1 = new BasicNameValuePair("id_maker","1");
            NameValuePair pair2 = new BasicNameValuePair("content", data.getContent());
            NameValuePair pair3 = new BasicNameValuePair("id_book", String.valueOf(data.getBookId()));
            NameValuePair pair4 = new BasicNameValuePair("send_time",data.getSendTime());
            list.add(pair1);
            list.add(pair2);
            list.add(pair3);
            list.add(pair4);
            CommentData.clear();
        }
        Message message = handler.obtainMessage();
        try{
            HttpEntity  httpEntity = new UrlEncodedFormEntity(list, HTTP.UTF_8);
            post = new HttpPost(url);
            post.addHeader("charset",HTTP.UTF_8);
            post.setEntity(httpEntity);
            response = client.execute(post);
            entity = response.getEntity();
            if(entity != null) {
                content = EntityUtils.toString(entity);
                Log.i("HTTPTask","content is  " + content);
                if((from.equals("CommitBook")  || from.equals(LoginActivity.TAG) ) && message.what != 1) {
                    message.what =  0;
                    message.obj = content;
                }else {
                    if(Utils.isCommentSucceed(content) && message.what != 1){
                        message.what = 0;
                    }
                }
            }
        }catch (Exception e) {
            message.what = 1;
            Log.e("HttpTask","error in dopost");
            e.printStackTrace();
        }finally {
            if(entity != null) {
                entity = null;
            }
            if(response != null) {
                response = null;
            }
            if(post != null) {
                post.abort();
                post = null;
            }
            list.clear();
            list = null;
        }
        handler.sendMessage(message);
    }

    private void doGet() {
        get = new HttpGet(url);
        Message msg = handler.obtainMessage();
        try{
            response = client.execute(get);
            entity = response.getEntity();
            if(entity != null) {
                content = EntityUtils.toString(entity);
                if (content != null) {
                    Log.i("doGet","content is "  +content);
                }
            }
        }catch (Exception e) {
            Log.e("HttpTask/doGet","error in doGet",e);
            msg.what = 1;
        }finally {
            if(entity != null) {
                entity = null;
            }
            if(response != null) {
                response = null;
            }
            if(get != null) {
                get.abort();
                get = null;
            }
        }
        if(msg.what != 1) {
            try{
                Log.e("HttpTask","url is " + url);
                if(url.contains(Constants.OLink)) {
                    Log.e("HttpTask","开始解析数据");
                    Utils.getListData(content,from);
                }else if(url.contains(Constants.DetailLink)) {
                    Utils.getDetailData(content);
                }else {
                    Log.e("HttpTask","没有数据要数据");
                }
            }catch (Exception e) {
                Log.e("CutJosn","error in cutjson",e);
                msg.what = 2;
            }
            if(msg.what != 1 && msg.what !=2) {
                msg.what =0;
            }
        }
        handler.sendMessage(msg);
    }

}
