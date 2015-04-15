package weike.fragment;

import android.content.SharedPreferences;
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
import com.umeng.fb.FeedbackAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import myinterface.UserInfoChangeListener;
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

    //友盟授权接口
    UMSocialService controller = UMServiceFactory.getUMSocialService("com.umeng.login");
    public static UserInfoChangeListener userInfoListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_setting,container,false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        ButterKnife.inject(this,v);
        tvAboutUs.setOnClickListener(this);
        tvIdea.setOnClickListener(this);
        rlVersion.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    public static SettingFragment getInstance() {
        return new SettingFragment();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_setting_about_us:
                break;
            case R.id.tv_setting_your_idea:
                FeedbackAgent agent = new FeedbackAgent(getActivity());
                agent.startFeedbackActivity();
                break;
            case R.id.btn_logout:
                logout();
                break;
            case R.id.rl_setting_update_version:
                break;
        }
    }

    private void logout() {
        switch (getActivity().getSharedPreferences(Constants.SP_USER,0).getString(Constants.LOGIN_WAY, "")) {
            case Constants.QQ:
                Tencent tencent = Tencent.createInstance("1104326437",getActivity());
                tencent.logout(getActivity());
                clearLocal();
                Toast.makeText(getActivity(), "已登出",
                        Toast.LENGTH_SHORT).show();
                callUserInfoListener();
                break;
            case Constants.WX:
                controller.deleteOauth(getActivity(), SHARE_MEDIA.WEIXIN,
                        listeners);
                break;
            case Constants.SINA:
                controller.deleteOauth(getActivity(), SHARE_MEDIA.SINA,
                        listeners);
                break;
            default:
                break;
        }
    }

    private void callUserInfoListener() {
        if(userInfoListener != null) {
            userInfoListener.userInfoChanged();
        }else {
            Toast.makeText(getActivity(),"userInfoListener is null",Toast.LENGTH_SHORT).show();
        }
    }

    SocializeListeners.SocializeClientListener listeners = new SocializeListeners.SocializeClientListener() {
        @Override
        public void onStart() {

        }

        @Override
        public void onComplete(int status, SocializeEntity socializeEntity) {
            if (status == 200) {
                Toast.makeText(getActivity(), "已登出.",
                        Toast.LENGTH_SHORT).show();
                //清空用户信息
                clearLocal();
                callUserInfoListener();
            } else {
                Toast.makeText(getActivity(), "登出失败",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void clearLocal() {
        try {
            SharedPreferences.Editor editor = getActivity().getSharedPreferences(Constants.SP_USER,0).edit();
            editor.clear();
            editor.commit();
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
    }
}


