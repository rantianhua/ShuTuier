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
        Log.e("HttpTask","post url is" + url);
        List<NameValuePair> list = new ArrayList<>();
        switch (from) {
            case "CommitBook":
                CommitBookData data = CommitBookData.getInstance();
                list.add(new BasicNameValuePair("publisher",data.getUid()));
                list.add(new BasicNameValuePair("Name",data.getBookName()));
                list.add(new BasicNameValuePair("Author", data.getBookAuthor()));
                list.add(new BasicNameValuePair("Oprice",data.getoPrice()));
                list.add( new BasicNameValuePair("Sprice",data.getsPrice()));
                list.add(new BasicNameValuePair("Press",data.getPublisher()));
                list.add(new BasicNameValuePair("Number", data.getBookNumber()));
                list.add(new BasicNameValuePair("sortId",data.getCategory()));
                list.add(new BasicNameValuePair("Other",data.getRemark()));
                list.add(new BasicNameValuePair("InternetImg",data.getCoverUrl()));
                list.add(new BasicNameValuePair("new", String.valueOf(data.getHowOld())));
                list.add(new BasicNameValuePair("Status",data.getStatus()));
                list.add(new BasicNameValuePair("Prule", data.getSendCondition()));
                list.add(new BasicNameValuePair("detail",data.getDescription()));
                list.add( new BasicNameValuePair("ISBN",data.getIsbn()));
                CommitBookData.clear();
                break;
            case LoginActivity.TAG:
                UserInfoData dataInfo = UserInfoData.getInstance();
                list.add(new BasicNameValuePair("thirdName",dataInfo.getNicName()));
                list.add(new BasicNameValuePair("OpenId",dataInfo.getOpenId()));
                list.add(new BasicNameValuePair("Sex", dataInfo.getSex()));
                list.add(new BasicNameValuePair("Head",dataInfo.getUserUrl()));
                Log.e("list is ", list.toString());
                break;
            case HandleBookDialogFragment.TAG:
                ChangBookSateData dataHandleBook = ChangBookSateData.getInstance();
                list.add(new BasicNameValuePair("ID",dataHandleBook.getId()));
                list.add(new BasicNameValuePair("close",dataHandleBook.getClose()));
                Log.e("list is ", list.toString());
                ChangBookSateData.clear();
                break;
            case BaseInfoFragment.TAG:
                UserInfoData dataBaseInfo = UserInfoData.getInstance();
                list.add(new BasicNameValuePair("OpenId",
                        con.getSharedPreferences(Constants.SP_USER,0)
                                .getString(Constants.UID,"")));
                list.add(new BasicNameValuePair("Head",dataBaseInfo.getUserUrl()));
                list.add(new BasicNameValuePair("thirdName",dataBaseInfo.getNicName()));
                list.add(new BasicNameValuePair("Sex",dataBaseInfo.getSex()));
                list.add(new BasicNameValuePair("birth",dataBaseInfo.getBirthday()));
                list.add(new BasicNameValuePair("School",dataBaseInfo.getSchool()));
                list.add(new BasicNameValuePair("teleNum",dataBaseInfo.getPhoneNumber()));
                list.add(new BasicNameValuePair("qqNum",dataBaseInfo.getQqNumber()));
                list.add(new BasicNameValuePair("weixinNum",dataBaseInfo.getWxNumber()));
                list.add(new BasicNameValuePair("Mail",dataBaseInfo.getEmail()));
                Log.e("list is ", list.toString());
                break;
            default:
                CommentData dataComment = CommentData.getInstance();
                list.add(new BasicNameValuePair("id_maker",dataComment.getUid()));
                list.add(new BasicNameValuePair("content", dataComment.getContent()));
                list.add( new BasicNameValuePair("id_book", String.valueOf(dataComment.getBookId())));
                list.add(new BasicNameValuePair("send_time",dataComment.getSendTime()));
                Log.e("list is ", list.toString());
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
                    Log.e("HTTPTask","content is  " + content);
                    if((from.equals("CommitBook") || from.equals(HandleBookDialogFragment.TAG) || from.equals(BaseInfoFragment.TAG))
                            && message.what != 1) {
                        message.what =  0;
                        message.obj = content;
                    }else if(from.equals(LoginActivity.TAG) && message.what != 1) {
                        if(Utils.loginSuccess(content,con)) {
                            message.what = 0;
                        }else {
                            message.what = 1;
                        }
                    }
                    else {
                        if(Utils.isCommentSucceed(content) && message.what != 1){
                            message.what = 0;
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
        Log.e("doget","url is " + url);
        get = new HttpGet(url);
        Message msg = handler.obtainMessage();
        try{
            response = client.execute(get);
            entity = response.getEntity();
            if(entity != null ) {
                content = EntityUtils.toString(entity);
                if (content != null) {
                    Log.e("doGet","content is "  +content);
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
                if(url.contains(Constants.OLink) || url.contains(Constants.SEARCHLINK)) {
                    Utils.getListData(content,from);
                }else if(url.contains(Constants.DetailLink)) {
                    Utils.getDetailData(content);
                }else if(url.contains(Constants.BASEMYCOMMIT)) {
                    msg.obj = Utils.getMyCommitData(content);
                }else if(url.contains(Constants.MESSAGELISTLINK)) {
                    //获取被留言的书的列表
                    Utils.getMessageList(content);
                }else if(url.contains(Constants.MESSAGENUMBERLINK)) {
                    if(!TextUtils.isEmpty(content)) {
                        JsonReader reader = new JsonReader(new StringReader(content));
                        try {
                            reader.beginObject();
                            reader.nextName();
                            msg.arg1 = Integer.valueOf(reader.nextString());
                            reader.endObject();
                        }catch (Exception e) {
                            e.printStackTrace();
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
