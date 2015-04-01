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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.util.ConnectionDetector;
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
    @InjectView(R.id.ibn_qq_login)
    ImageButton ibnQQLogin;
    @InjectView(R.id.btn_just_look)
    Button btnLook;
    @InjectView(R.id.img_login_bg)
    ImageView imgLoginBg;
    @InjectView(R.id.test)
    TextView tvTest;

    private Tencent tencent;
    private IUiListener loginListener;
    public static final String TAG = "LoginActivity";
    private ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        initView();
    }

    private void initView() {
        ButterKnife.inject(this);
        btnLook.setOnClickListener(this);
        ibnQQLogin.setOnClickListener(this);

        //Utils.loadBlurBitmap(this, imgLoginBg, R.drawable.login_bg, 15, 0, 0);
        //启动ogo动画
        SharedPreferences sp = getSharedPreferences(weike.util.Constants.SP_USER,0);
        if(!sp.getBoolean(weike.util.Constants.USER_ONLINE_KEY,false)){
            //用户登录过，第三方账号未注销
            rlLogin.setVisibility(View.GONE);
            statrMainAnim(0);
        }else{
            statrMainAnim(1);
        }
    }


    private void statrMainAnim(int mode) {
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
            final ValueAnimator loginViewAnim = ValueAnimator.ofFloat(0,
                    -getResources().getDimensionPixelSize(R.dimen.login_view_height));
            loginViewAnim.setDuration(800);
            loginViewAnim.setInterpolator(new DecelerateInterpolator());
            loginViewAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    rlLogin.setTranslationY((Float) animation.getAnimatedValue());
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

                break;
            case R.id.ibn_qq_login:
                if(pd == null) {
                    pd = new ProgressDialog(this);
                }
                pd.setMessage("正在跳转至QQ...");
                pd.show();
                loginQQ();
                break;
            default:
                break;
        }
    }

    private void loginQQ()
    {
        //检查网络
        if(ConnectionDetector.isConnectingToInternet(this)) {
            tencent = Tencent.createInstance("1104326437", this.getApplicationContext());
            if (!tencent.isSessionValid())
            {
                tencent.login(this, "get_simple_userinfo", new IUiListener() {
                    @Override
                    public void onComplete(Object o) {
                        JSONObject values = (JSONObject)o;
                        if(values != null) {
                            updateOpenIdAndToken(values);
                            updateUserInfo();
                            finishLogin();
                        }else {
                            Log.e(TAG,"Obeject is null in login");
                        }
                    }

                    @Override
                    public void onError(UiError uiError) {

                    }

                    @Override
                    public void onCancel() {

                    }


                });
            }else {
                //注销qq
                tencent.logout(this);
                updateUserInfo();
            }
        }else {
            Toast.makeText(this,"网络不可用",Toast.LENGTH_SHORT).show();
        }
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
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
            }
        }
    };

    private void updateUserInfo() {
        if(tencent == null) {
            tencent = Tencent.createInstance("1104326437",this);
        }
        if(tencent.isSessionValid()) {
            UserInfo info = new UserInfo(this,tencent.getQQToken());
            info.getUserInfo(new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    if(o != null) {
                        try {
                            JSONObject json=  (JSONObject) o;
                            String nicName = json.getString(weike.util.Constants.QQNICNAME);
                            String iconUrl = json.getString(weike.util.Constants.QQICONURL);
                            String sex = json.getString(weike.util.Constants.SEX);
                            Log.e(TAG,nicName+"----"+iconUrl + "---" + sex);
                            Toast.makeText(LoginActivity.this,nicName+"----"+iconUrl + "---" + sex,Toast.LENGTH_LONG).show();
                            SharedPreferences sp = getSharedPreferences(weike.util.Constants.SP_USER,0);
                            SharedPreferences.Editor editor = sp.edit();
                            if(!TextUtils.isEmpty(nicName) && !TextUtils.isEmpty(iconUrl) && !TextUtils.isEmpty(sex)) {
                                editor.putString(weike.util.Constants.QQNICNAME,nicName);
                                editor.putString(weike.util.Constants.QQICONURL,iconUrl);
                                editor.putString(weike.util.Constants.SEX,sex);
                                editor.putBoolean(weike.util.Constants.USER_ONLINE_KEY,true);
                            }else {
                                editor.putBoolean(weike.util.Constants.USER_ONLINE_KEY,false);
                            }
                            editor.apply();
                        } catch (JSONException e) {
                            Log.e(TAG,"error in updateUserInfo",e);
                        }
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
        else {
            //清除已经储存的用户信息
            Toast.makeText(LoginActivity.this,"的饭店里的",Toast.LENGTH_LONG).show();
        }

    }

    //初始化openid和access_token
    private void updateOpenIdAndToken(JSONObject json) {
        try{
            String token = json.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires  = json.getString(Constants.PARAM_EXPIRES_IN);
            String openId = json.getString(Constants.PARAM_OPEN_ID);
            if(!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
                SharedPreferences sp = getSharedPreferences(weike.util.Constants.SP_USER,0);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(Constants.PARAM_ACCESS_TOKEN,token);
                editor.putString(Constants.PARAM_OPEN_ID, openId);
                editor.putLong(Constants.PARAM_EXPIRES_IN, System.currentTimeMillis() + Long.parseLong(expires));
                editor.putBoolean(weike.util.Constants.USER_ONLINE_KEY, true);
                editor.apply();
                token = null;
                openId = null;
                expires = null;
            }else {
                Log.e("openid is null","token or expires is null");
            }
        }catch (Exception e) {
            Log.e(TAG,"error in initOpenIdAndToken",e);
        }finally {
            json = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.RESULT_LOGIN) {
                tencent.handleLoginData(data, loginListener);
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
}
