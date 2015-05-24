package weike.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.tauth.Tencent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.update.UmengUpdateAgent;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import myinterface.UserInfoChangeListener;
import weike.shutuier.FeedbackActivity;
import weike.shutuier.R;
import weike.util.Constants;
import weike.util.Utils;

/**
 * Created by Rth on 2015/2/9.
 */

public class SettingFragment extends Fragment implements View.OnClickListener{

    @InjectView(R.id.tv_setting_about_us)
    TextView tvAboutUs;
    @InjectView(R.id.rl_setting_update_version)
    RelativeLayout rlVersion;
    @InjectView(R.id.tv_setting_your_idea)
    TextView tvIdea;
    @InjectView(R.id.btn_logout)
    Button btnLogout;
    @InjectView(R.id.tv_setting_version)
    TextView tvVersion;

    private int arrowNextSize;
    private static Context context;

    //友盟授权接口
    UMSocialService controller = UMServiceFactory.getUMSocialService("com.umeng.login");
    public static UserInfoChangeListener userInfoListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrowNextSize = getResources().getDimensionPixelSize(R.dimen.arrow_setting_size);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_setting,container,false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        ButterKnife.inject(this,v);
        Drawable arrow = getResources().getDrawable(R.drawable.arrow_next);
        arrow.setBounds(0,0,arrowNextSize,arrowNextSize);
        tvAboutUs.setCompoundDrawables(null,null,arrow,null);
        tvAboutUs.setOnClickListener(this);
        tvIdea.setCompoundDrawables(null,null,arrow,null);
        tvIdea.setOnClickListener(this);
        rlVersion.setOnClickListener(this);
        btnLogout.setOnClickListener(this);

    }

    public static SettingFragment getInstance(Context con) {
        context = con;
        return new SettingFragment();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_setting_about_us:
                break;
            case R.id.tv_setting_your_idea:
                startActivity(new Intent(context, FeedbackActivity.class));
                if(getActivity() != null) {
                    getActivity().overridePendingTransition(R.anim.right_in,R.anim.left_out);
                }
                break;
            case R.id.btn_logout:
                logout();
                break;
            case R.id.rl_setting_update_version:
                UmengUpdateAgent.forceUpdate(context);
                break;
        }
    }

    private void logout() {
        switch (context.getSharedPreferences(Constants.SP_USER,0).getString(Constants.LOGIN_WAY, "")) {
            case Constants.QQ:
                Tencent tencent = Tencent.createInstance("1104326437",context);
                tencent.logout(context);
                clearLocal();
                Toast.makeText(context, "已登出",
                        Toast.LENGTH_SHORT).show();
                callUserInfoListener();
                break;
            case Constants.SINA:
                logOut( SHARE_MEDIA.SINA);
                break;
            default:
                break;
        }
    }

    private void logOut(SHARE_MEDIA media) {
//        controller.deleteOauth(context,media,
//                listeners);
        clearLocal();
        Toast.makeText(context, "已登出.",
                Toast.LENGTH_SHORT).show();
        //清空用户信息
        callUserInfoListener();
    }

    private void callUserInfoListener() {
        if(userInfoListener != null) {
            userInfoListener.userInfoChanged();
        }
    }

    SocializeListeners.SocializeClientListener listeners = new SocializeListeners.SocializeClientListener() {
        @Override
        public void onStart() {

        }

        @Override
        public void onComplete(int status, SocializeEntity socializeEntity) {
            if (status == 200) {
                Toast.makeText(context, "已登出.",
                        Toast.LENGTH_SHORT).show();
                //清空用户信息
                clearLocal();
                callUserInfoListener();
            } else {
                Toast.makeText(context, "登出失败" + status,
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void clearLocal() {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SP_USER,0).edit();
            editor.clear();
            editor.apply();
            File file = new File(Utils.getPicturePath()+Constants.USERICONFILE);
            file.delete();
            file = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(userInfoListener  != null) {
            userInfoListener = null;
        }
        context = null;
    }
}


