package weike.shutuier;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.SyncListener;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.Reply;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import myinterface.UserInfoChangeListener;
import weike.data.UserInfoData;
import weike.util.ConnectReceiver;
import weike.util.Constants;
import weike.util.HttpManager;
import weike.util.HttpTask;
import weike.util.Utils;

/**
 * Created by Rth on 2015/3/8.
 */
public class LoginActivity extends Activity implements View.OnClickListener,ConnectReceiver.GetNetState
{

    @InjectView(R.id.img_logo_login)
    ImageView logo;
    @InjectView(R.id.tv_my_purpose)
    TextView tvMyPurpose;
    @InjectView(R.id.ll_login_view)
    LinearLayout llLogin;
    @InjectView(R.id.btn_just_look)
    Button btnLook;
    @InjectView(R.id.img_qq_login)
    ImageView imgQQLogin;
    @InjectView(R.id.img_sina_login)
    ImageView imgSinaLogin;

    public static final String TAG = "LoginActivity";
    private ProgressDialog pd = null;
    //友盟授权接口
    UMSocialService controller = UMServiceFactory.getUMSocialService("com.umeng.login");
    private SharedPreferences sp = null;
    //网络状态接收器
    private ConnectReceiver netReceiver = new ConnectReceiver(this);
    public static boolean netConnect = false ;    //记录接收的网络状态
    private UserInfoData userInfo = UserInfoData.getInstance();
    public static UserInfoChangeListener listener = null;
    private Tencent tencent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(netReceiver,filter);
        initController();
        feedbackNotice();
        initView();
    }

    //打开反馈通知
    private void feedbackNotice() {
        Conversation mComversation = new FeedbackAgent(this).getDefaultConversation();
        mComversation.sync(new SyncListener() {
            @Override
            public void onSendUserReply(List<Reply> replyList) {
            }
            @Override
            public void onReceiveDevReply(List<Reply> replyList) {
                String content = "";
                if(replyList.size() > 0) {
                    if (replyList.size() == 1) {
                        content = replyList.get(0).content;
                    } else {
                        content = "有 " +  replyList.size() + "条反馈";
                    }
                    try {
                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        String tickerText = "有新的回复";
                        Intent intentToLaunch = new Intent(LoginActivity.this, FeedbackActivity.class);//将CustomActivity替换成自定义Activity类名
                        intentToLaunch.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        int requestCode = (int) SystemClock.uptimeMillis();
                        PendingIntent contentIntent = PendingIntent.getActivity(LoginActivity.this, requestCode,
                                intentToLaunch, PendingIntent.FLAG_UPDATE_CURRENT);

                        int smallIcon = getPackageManager().getPackageInfo(
                                getPackageName(), 0).applicationInfo.icon;

                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                                LoginActivity.this)
                                .setSmallIcon(smallIcon)
                                .setContentTitle(tickerText).setTicker(tickerText)
                                .setContentText(content).setAutoCancel(true)
                                .setContentIntent(contentIntent);
                        notificationManager.notify(0, mBuilder.build());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initController() {
        //设置新浪SSO handler
        controller.getConfig().setSsoHandler(new SinaSsoHandler());
    }

    private void initView() {
        ButterKnife.inject(this);
        btnLook.setOnClickListener(this);
        imgQQLogin.setOnClickListener(this);
        imgSinaLogin.setOnClickListener(this);
        if(sp == null) {
            sp = getSharedPreferences(weike.util.Constants.SP_USER, 0);
        }
        llLogin.setVisibility(View.INVISIBLE);
        if(sp.getBoolean(Constants.APP_FORST_OPEN,true)) {
            try {
                File file = new File(Utils.getPicturePath() + Constants.USERICONFILE);
                if(file.exists()) {
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(Constants.APP_FORST_OPEN,false);
            editor.apply();
        }
        //判断用户是否登陆
        if(!sp.getBoolean(Constants.USER_ONLINE_KEY,false)) {
            //用户未登陆
            startMainAnim(1);
        }else {
            startMainAnim(0);
        }

    }


    private void startMainAnim(int mode) {
        //平移动画
        ObjectAnimator translate = ObjectAnimator.ofFloat(0, "translate",-200);
        translate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                logo.setTranslationY((Float) animation.getAnimatedValue());
                tvMyPurpose.setTranslationY((Float) animation.getAnimatedValue());
            }
        });
        //渐入且缩放动画
        ObjectAnimator alpha = ObjectAnimator.ofFloat(0f,"alpha",1.0f);
        alpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offset = (Float) animation.getAnimatedValue();
                logo.setAlpha((offset));
                logo.setScaleX(offset);
                logo.setScaleY(offset);
                tvMyPurpose.setAlpha((offset));
                tvMyPurpose.setScaleX(offset);
                tvMyPurpose.setScaleY(offset);
            }
        });
        AnimatorSet set = new AnimatorSet();
        set.setDuration(1500);
        set.setInterpolator(new DecelerateInterpolator());
        set.play(translate).after(alpha);
        if(mode == 1) {
            final ValueAnimator loginViewAnim = ValueAnimator.ofFloat(getResources().getDimensionPixelSize(R.dimen.login_view_height),
                    0);
            loginViewAnim.setDuration(500);
            loginViewAnim.setInterpolator(new DecelerateInterpolator());
            loginViewAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    llLogin.setTranslationY((Float) animation.getAnimatedValue());
                }
            });
            loginViewAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    llLogin.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    loginViewAnim.start();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }else {
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                    overridePendingTransition(R.anim.right_in,R.anim.left_out);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        set.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_just_look:
                goToMainActivity();
                break;
            case R.id.img_qq_login:
                userInfo.setLoginWay(Constants.QQ);
                if(netConnect) {
                    qqLogin();
                }else {
                    showToast("网络不可用");
                }
                break;
            case R.id.img_sina_login:
                userInfo.setLoginWay(Constants.SINA);
                doLogin(SHARE_MEDIA.SINA);
                break;
            default:
                break;
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        LoginActivity.this.finish();
        overridePendingTransition(R.anim.right_in,R.anim.left_out);
    }

    private void showToast(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    private void doLogin(final SHARE_MEDIA media) {
        if(!netConnect) {
            showToast("网络未连接");
            return;
        }
        if(pd == null) {
            pd = new ProgressDialog(this);
        }
        pd.setMessage("正在跳转...");
        pd.show();
        controller.doOauthVerify(this,media,new SocializeListeners.UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onComplete(Bundle bundle, SHARE_MEDIA share_media) {
                if(bundle != null && !TextUtils.isEmpty(bundle.getString("uid"))) {
                    Log.e(TAG,"doLogin ， the info is " + bundle.toString());
                    //获取用户信息
                    getPlatformInfo(media);
                }else {
                    showToast("授权失败");
                }
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA share_media) {

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {

            }
        });
    }

    private void getPlatformInfo(final SHARE_MEDIA media) {
        controller.getPlatformInfo(this,media,new SocializeListeners.UMDataListener() {
            @Override
            public void onStart() {
            }
            @Override
            public void onComplete(int i, Map<String, Object> info) {
                if(i == 200 && info != null) {
                    userInfo.setUserUrl((String) info.get("profile_image_url"));
                    userInfo.setNicName( (String)info.get("screen_name"));
                    int n = (int) info.get("gender");
                    String sex =  (n == 1 ? "男" : "女");
                    userInfo.setSex(sex);
                    userInfo.setOpenId(String.valueOf(info.get("uid")));
                    Log.e(TAG, info.toString());
                    finishLogin();
                }else {
                    showToast("获取平台数据失败");
                }
            }
        });
    }

    private void finishLogin() {
        if(pd == null) {
            pd = new ProgressDialog(this);
        }
        pd.setMessage("正在登陆...");
        if(!pd.isShowing()){
            pd.show();
        }
        HttpTask task = new HttpTask(this,weike.util.Constants.LOGINLINK,loginHan,TAG,"post");
        HttpManager.startTask(task);
    }

    Handler loginHan = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(pd.isShowing()) {
                pd.dismiss();
            }
            if(msg.what == 0) {
                if(listener != null) {
                    listener.userInfoChanged();
                }
                goToMainActivity();
            }else {
                showToast("登陆失败！");
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = controller.getConfig().getSsoHandler(requestCode);
        if(ssoHandler != null){
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        if(requestCode == com.tencent.connect.common.Constants.REQUEST_API) {
            if (resultCode == com.tencent.connect.common.Constants.RESULT_LOGIN) {
                tencent.handleLoginData(data, qqLoginListener);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(pd !=null ){
            if(pd.isShowing()) {
                pd.dismiss();
            }
            pd = null;
        }
    }

    private IUiListener qqLoginListener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            try {
                JSONObject object = (JSONObject)o;
                String uid = object.getString(com.tencent.connect.common.Constants.PARAM_OPEN_ID);
                if(!TextUtils.isEmpty(uid)) {
                    userInfo.setOpenId(uid);
                }
                //获取qq用户基本信息
                if(pd == null) {
                    pd = new ProgressDialog(LoginActivity.this);
                }
                pd.setMessage("正在获取信息...");
                getQQInfo(tencent);
            } catch (JSONException e) {
                Log.e(TAG,"error in get ak",e);
            }
        }

        @Override
        public void onError(UiError uiError) {

        }

        @Override
        public void onCancel() {

        }
    };

    //qq登陆，因为一些特殊原因，单独写出qq登陆
    private void qqLogin() {
        if(pd == null) {
            pd = new ProgressDialog(this);
        }
        pd.setMessage("正在跳转至QQ...");
        pd.show();
        tencent = Tencent.createInstance("101196425",this);
        if(tencent != null && !tencent.isSessionValid()) {
            tencent.login(this,"all",qqLoginListener);
        }
    }

    private void getQQInfo(Tencent tencent) {
        if(tencent != null && tencent.isSessionValid()) {
            UserInfo info = new UserInfo(this,tencent.getQQToken());
            info.getUserInfo(new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    if(o != null) {
                        Log.e(TAG,o.toString());
                        try{
                            JSONObject json=  (JSONObject) o;
                            String nicName = json.getString(Constants.NICNAME);
                            String iconUrl = json.getString(Constants.QQ_FIGURE2);
                            if(TextUtils.isEmpty(iconUrl)) {
                                iconUrl = json.getString(Constants.QQ_FIGURE1);
                            }
                            String sex = json.getString(weike.util.Constants.SEX);
                            Log.e(TAG, nicName + "----" + iconUrl + "---" + sex);
                            userInfo.setNicName(nicName);
                            userInfo.setUserUrl(iconUrl);
                            userInfo.setSex(sex);
                        }catch (Exception e) {
                            Log.e(TAG,"error in get qq useinfo ",e);
                        }
                        finishLogin();
                    }
                }

                @Override
                public void onError(UiError uiError) {

                }

                @Override
                public void onCancel() {

                }
            });
        }
    }

    @Override
    public void sendState(boolean state) {
        netConnect = state;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(netReceiver != null) {
            this.unregisterReceiver(netReceiver);
            netReceiver = null;
        }
        if(listener != null) {
            listener = null;
        }
        UserInfoData.recycle();
    }
}
