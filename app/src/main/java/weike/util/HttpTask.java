package weike.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.stream.JsonReader;

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
import org.json.JSONObject;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import weike.data.ChangBookSateData;
import weike.data.CommentData;
import weike.data.CommitBookData;
import weike.data.UserInfoData;
import weike.fragment.BaseInfoFragment;
import weike.fragment.HandleBookDialogFragment;
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
        Log.e("HttpTask","url is "+url);
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
        switch (from) {
            case "CommitBook":
                CommitBookData data = CommitBookData.getInstance();
                list.add(new BasicNameValuePair("openId",data.getUid()));
                list.add(new BasicNameValuePair("count", data.getBookNumber()));
                list.add(new BasicNameValuePair("old", String.valueOf(data.getHowOld())));
                list.add(new BasicNameValuePair("category",data.getCategory()));
                list.add(new BasicNameValuePair("method",data.getSendCondition()));
                list.add(new BasicNameValuePair("old_price",data.getoPrice()));
                list.add( new BasicNameValuePair("price",data.getsPrice()));
                list.add(new BasicNameValuePair("name",data.getBookName()));
                list.add( new BasicNameValuePair("ISBN",data.getIsbn()));
                list.add(new BasicNameValuePair("author", data.getBookAuthor()));
                list.add(new BasicNameValuePair("img",data.getCoverUrl()));
                list.add(new BasicNameValuePair("type",data.getStatus()));
                list.add(new BasicNameValuePair("publish",data.getPublisher()));
                list.add(new BasicNameValuePair("content",data.getDescription()));
                CommitBookData.clear();
                break;
            case LoginActivity.TAG:
                UserInfoData dataInfo = UserInfoData.getInstance();
                list.add(new BasicNameValuePair("thirdName",dataInfo.getNicName()));
                list.add(new BasicNameValuePair("OpenId",dataInfo.getOpenId()));
                list.add(new BasicNameValuePair("Sex", dataInfo.getSex()));
                list.add(new BasicNameValuePair("Head",dataInfo.getUserUrl()));
                break;
            case HandleBookDialogFragment.TAG:
                ChangBookSateData dataHandleBook = ChangBookSateData.getInstance();
                list.add(new BasicNameValuePair("ID",dataHandleBook.getId()));
                list.add(new BasicNameValuePair("close",dataHandleBook.getClose()));
                ChangBookSateData.clear();
                break;
            case BaseInfoFragment.TAG:
                UserInfoData dataBaseInfo = UserInfoData.getInstance();
                list.add(new BasicNameValuePair("openId",
                        con.getSharedPreferences(Constants.SP_USER,0)
                                .getString(Constants.UID,"")));
                list.add(new BasicNameValuePair("img",dataBaseInfo.getUserUrl()));
                list.add(new BasicNameValuePair("thirdName",dataBaseInfo.getNicName()));
                list.add(new BasicNameValuePair("Sex",dataBaseInfo.getSex()));
                list.add(new BasicNameValuePair("School",dataBaseInfo.getSchool()));
                list.add(new BasicNameValuePair("teleNum",dataBaseInfo.getPhoneNumber()));
                list.add(new BasicNameValuePair("qqNum",dataBaseInfo.getQqNumber()));
                list.add(new BasicNameValuePair("weixinNum",dataBaseInfo.getWxNumber()));
                list.add(new BasicNameValuePair("Mail",dataBaseInfo.getEmail()));
                list.add(new BasicNameValuePair("address",dataBaseInfo.getAddress()));
                break;
            default:
                CommentData dataComment = CommentData.getInstance();
                list.add(new BasicNameValuePair("id_maker",dataComment.getUid()));
                list.add(new BasicNameValuePair("content", dataComment.getContent()));
                list.add( new BasicNameValuePair("id_book", String.valueOf(dataComment.getBookId())));
                CommentData.clear();
                break;

        }
        if(handler != null) {
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
                    if(TextUtils.isEmpty(content)) message.what = 1;
                    Log.e("post","get content is " + content);
                    if((from.equals("CommitBook") || from.equals(HandleBookDialogFragment.TAG))
                            && message.what != 1) {
                        message.what =  0;
                        message.obj = content;
                    }else if(from.equals(BaseInfoFragment.TAG) && message.what != 1) {
                        if(Utils.changeBaseInfo(content)) {
                            message.what = 0;
                        }else {
                            message.what = 1;
                        }
                    }
                    else if(from.equals(LoginActivity.TAG) && message.what != 1) {
                        if(Utils.loginSuccess(content,con)) {
                            message.what = 0;
                        }else {
                            message.what = 1;
                        }
                    }
                    else {
                        if(Utils.isCommentSucceed(content) && message.what != 1){
                            message.what = 0;
                        }else {
                            message.what = 1;
                        }
                    }
                }
            }catch (Exception e) {
                message.what = 1;
                Log.e("HttpTask","error in dopost",e);
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
    }

    private void doGet() {
        get = new HttpGet(url);
        Message msg = handler.obtainMessage();
        try{
            response = client.execute(get);
            entity = response.getEntity();
            if(entity != null ) {
                content = EntityUtils.toString(entity);
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
        if(msg.what != 1 && !TextUtils.isEmpty(content)) {
            Log.e("get","content is " + content );
            try{
                if(url.contains(Constants.OLink) || url.contains(Constants.SEARCHLINK)) {
                    Utils.getListData(content,from);
                }else if(url.contains(Constants.DetailLink)) {
                    if( !Utils.getDetailData(content)) {
                        msg.what = 1;
                    }
                }else if(url.contains(Constants.BASEMYCOMMIT)) {
                    msg.obj = Utils.getMyCommitData(content);
                }else if(url.contains(Constants.MESSAGELISTLINK)) {
                    //获取被留言的书的列表
                    Utils.getMessageList(content);
                }else if(url.contains(Constants.MESSAGENUMBERLINK)) {
                    if(!TextUtils.isEmpty(content)) {
                        JSONObject json = null;
                        try {
                            json = new JSONObject(content);
                            msg.arg1 = json.getInt("msg");
                        }catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            json = null;
                        }
                    }
                }
                else {
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
