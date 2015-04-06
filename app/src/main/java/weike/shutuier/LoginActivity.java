package weike.shutuier;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.tencent.tauth.Tencent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.util.Constants;
import weike.util.HttpManager;
import weike.util.HttpTask;

/**
 * Created by Rth on 2015/3/8.
 */
public class LoginActivity extends Activity implements View.OnClickListener
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

    private Tencent tencent;
    public static final String TAG = "LoginActivity";
    private ProgressDialog pd = null;
    //友盟授权接口
    UMSocialService controller = UMServiceFactory.getUMSocialService("com.umeng.login");
    private SharedPreferences sp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        initController();
        initView();
    }

    private void initController() {
        //设置新浪SSO handler
        controller.getConfig().setSsoHandler(new SinaSsoHandler());
        //配置qq登陆
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, "1104326437",
                "Jj4RMmh7LOSdOeSU");
        qqSsoHandler.addToSocialSDK();
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
                break;
            case R.id.tv_login_qq:
                doLogin(SHARE_MEDIA.QQ);
                break;
            case R.id.tv_login_sina:
                doLogin(SHARE_MEDIA.SINA);
                break;
            case R.id.tv_login_wx:
                doLogin(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.tv_login_renren:
                doLogin(SHARE_MEDIA.RENREN);
                break;
            default:
                break;
        }
    }

    private void doLogin(final SHARE_MEDIA media) {
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
                    if(share_media == SHARE_MEDIA.QQ) {
                        String uid = bundle.getString("uid");
                        if(!TextUtils.isEmpty(uid)) {
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString(weike.util.Constants.UID,uid);
                            editor.apply();
                        }
                    }
                    Log.e(TAG,"doLogin ， the info is " + bundle.toString());
                    getPlatformInfo(media);
                }else {
                    Toast.makeText(LoginActivity.this,"授权失败+" ,Toast.LENGTH_LONG).show();
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
                Toast.makeText(LoginActivity.this,"开始获取平台数据",Toast.LENGTH_LONG).show();
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
                    if(media != SHARE_MEDIA.QQ) {
                        long uid = (long)info.get("uid");
                        int n = (int) info.get("gender");
                        sex = (n == 1 ? "男" : "女");
                        editor.putString(weike.util.Constants.UID,String.valueOf(uid));
                    }else {
                        sex = (String) info.get("gender");
                    }
                    editor.putString(weike.util.Constants.SEX,sex);
                    editor.apply();
                    Log.e(TAG, info.toString());
                    finishLogin();
                }else {
                    Toast.makeText(LoginActivity.this,"获取平台数据失败",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void finishLogin() {
        if(pd == null) {
            pd = new ProgressDialog(this);
        }
        pd.setMessage("正在登陆...");
        pd.show();
        HttpTask task = new HttpTask(this,weike.util.Constants.LOGINLINK,loginHan,TAG,"post");
        HttpManager.startTask(task);
    }

    Handler loginHan = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            pd.dismiss();
            if(msg.obj.toString().equals("true")){
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean(weike.util.Constants.USER_ONLINE_KEY,true);
                editor.apply();
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
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
}
