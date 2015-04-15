package weike.shutuier;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.util.ConnectReceiver;
import weike.util.Constants;
import weike.util.HttpManager;
import weike.util.HttpTask;

/**
 * Created by Rth on 2015/3/8.
 */
public class LoginActivity extends Activity implements View.OnClickListener,ConnectReceiver.GetNetState
{

    @InjectView(R.id.img_logo_login)
    ImageView logo;
    @InjectView(R.id.tv_my_purpose)
    TextView tvMyPurpose;
    @InjectView(R.id.rl_login_view)
    RelativeLayout rlLogin;
    @InjectView(R.id.btn_just_look)
    Button btnLook;
    @InjectView(R.id.img_login_bg)
    ImageView imgLoginBg;
    @InjectView(R.id.tv_login_qq)
    TextView tvQQLogin;
    @InjectView(R.id.tv_login_sina)
    TextView tvSinaLogin;
    @InjectView(R.id.tv_login_wx)
    TextView tvWxLogin;
    @InjectView(R.id.tv_login_renren)
    TextView tvRRLogin;

    public static final String TAG = "LoginActivity";
    private ProgressDialog pd = null;
    //友盟授权接口
    UMSocialService controller = UMServiceFactory.getUMSocialService("com.umeng.login");
    private SharedPreferences sp = null;
    //网络状态接收器
    private ConnectReceiver netReceiver = new ConnectReceiver(this);
    public static boolean netConnect = false ;    //记录接收的网络状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(netReceiver,filter);
        initController();
        initView();
    }

    private void initController() {
        //设置新浪SSO handler
        controller.getConfig().setSsoHandler(new SinaSsoHandler());
    }

    private void initView() {
        ButterKnife.inject(this);
        btnLook.setOnClickListener(this);
        tvQQLogin.setOnClickListener(this);
        tvSinaLogin.setOnClickListener(this);
        tvWxLogin.setOnClickListener(this);
        tvRRLogin.setOnClickListener(this);
        if(sp == null) {
            sp = getSharedPreferences(weike.util.Constants.SP_USER, 0);
        }
        rlLogin.setVisibility(View.INVISIBLE);
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
                    rlLogin.setTranslationY((Float) animation.getAnimatedValue());
                }
            });
            loginViewAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    rlLogin.setVisibility(View.VISIBLE);
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
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                LoginActivity.this.finish();
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
                break;
            case R.id.tv_login_qq:
                saveLoginWay(Constants.QQ);
                if(netConnect) {
                    qqLogin();
                }else {
                    showToast("网络不可用");
                }
                break;
            case R.id.tv_login_sina:
                saveLoginWay(Constants.SINA);
                doLogin(SHARE_MEDIA.SINA);
                break;
            case R.id.tv_login_wx:
                saveLoginWay(Constants.WX);
                doLogin(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.tv_login_renren:
                doLogin(SHARE_MEDIA.RENREN);
                break;
            default:
                break;
        }
    }

    private void saveLoginWay(String way){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.LOGIN_WAY,way);
        editor.apply();
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
                    String nicName = (String)info.get("screen_name");
                    String url  = (String) info.get("profile_image_url");
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(weike.util.Constants.NICNAME,nicName);
                    editor.putString(weike.util.Constants.USERURL,url);
                    String sex = null;
                    long uid = (long)info.get("uid");
                    int n = (int) info.get("gender");
                    sex = (n == 1 ? "男" : "女");
                    editor.putString(weike.util.Constants.UID,String.valueOf(uid));
                    editor.putString(weike.util.Constants.SEX,sex);
                    editor.apply();
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
            if(msg.obj.equals("true")) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean(weike.util.Constants.USER_ONLINE_KEY,true);
                editor.apply();
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
            }else{
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

    //qq登陆，因为一些特殊原因，单独写出qq登陆
    private void qqLogin() {
        if(pd == null) {
            pd = new ProgressDialog(this);
        }
        pd.setMessage("正在跳转至QQ...");
        pd.show();
        final Tencent tencent = Tencent.createInstance("1104326437",this);
        if(tencent != null && !tencent.isSessionValid()) {
            tencent.login(this,"all",new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    try {
                        JSONObject object = (JSONObject)o;
                        String uid = object.getString(com.tencent.connect.common.Constants.PARAM_OPEN_ID);
                        if(!TextUtils.isEmpty(uid)) {
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString(Constants.UID,uid);
                            editor.apply();
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
            });
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
                            SharedPreferences.Editor editor = sp.edit();
                            if(!TextUtils.isEmpty(nicName) && !TextUtils.isEmpty(iconUrl) && !TextUtils.isEmpty(sex)) {
                                editor.putString(Constants.NICNAME,nicName);
                                editor.putString(Constants.USERURL,iconUrl);
                                editor.putString(weike.util.Constants.SEX,sex);
                                editor.putBoolean(weike.util.Constants.USER_ONLINE_KEY,true);
                            }else {
                                editor.putBoolean(weike.util.Constants.USER_ONLINE_KEY,false);
                            }
                            editor.apply();
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
    }
}
