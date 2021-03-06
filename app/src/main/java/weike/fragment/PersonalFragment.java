package weike.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.shutuier.R;
import weike.util.Constants;
import weike.util.GetUserPhotoWork;

/**
 * Created by Rth on 2015/3/23.
 */
public class PersonalFragment extends Fragment implements View.OnClickListener{

    @InjectView(R.id.tv_my_sell)
    TextView tvMySell;
    @InjectView(R.id.tv_my_buy)
    TextView tvMyBuy;
    @InjectView(R.id.tv_my_ask_send)
    TextView tvMyAskSend;
    @InjectView(R.id.tv_my_send)
    TextView tvMySend;
    @InjectView(R.id.tv_my_base_information)
    TextView tvMyInfo;
    @InjectView(R.id.user_bg_personal)
    ImageView imgBg;
    @InjectView(R.id.img_user_photo)
    ImageView userPhoto;
    @InjectView(R.id.tv_personal_user_name)
    TextView tvUserName;

    private final String TAG = "PersonalFragment";
    private static UpdateToolbar toolbarListener = null;
    private static  Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_personal_center,container,false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        ButterKnife.inject(this,v);
        tvMyAskSend.setOnClickListener(this);
        tvMyBuy.setOnClickListener(this);
        tvMyInfo.setOnClickListener(this);
        tvMySell.setOnClickListener(this);
        tvMySend.setOnClickListener(this);

        tvUserName.setText(
               context.getSharedPreferences(Constants.SP_USER,0)
                .getString(Constants.NICNAME,"")
        );

        showUserPhoto();
    }

    private void showUserPhoto() {
        new GetUserPhotoWork(userPhoto,context,true,
                getResources().getDimensionPixelSize(R.dimen.img_center_user_size),
                getResources().getDimensionPixelSize(R.dimen.img_center_user_size))
                .execute();
    }

    public static PersonalFragment getInstance(UpdateToolbar listener,Context con) {
        context = con;
        toolbarListener = listener;
       return new PersonalFragment();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_my_base_information:
                changeFragment(BaseInfoFragment.getInstance(toolbarListener,context));
                toolbarListener.changeTitle(4);
                break;
            case R.id.tv_my_send:
                changeFragment(MySendFragment.getInstance(toolbarListener,context));
                toolbarListener.changeTitle(2);
                break;
            case R.id.tv_my_ask_send:
                changeFragment(MyAskSendFragment.getInstance(toolbarListener,context));
                toolbarListener.changeTitle(3);
                break;
            case R.id.tv_my_buy:
                changeFragment(MyAskBuyFragment.getInstance(toolbarListener,context));
                toolbarListener.changeTitle(1);
                break;
            case R.id.tv_my_sell:
                changeFragment(MySellFragment.getInstance(toolbarListener,context));
                toolbarListener.changeTitle(0);
                break;
        }
    }

    private void changeFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.right_in,R.anim.left_out,R.anim.left_in,R.anim.right_out)
                .replace(R.id.container,fragment)
                .addToBackStack(null).commit();
    }


    public interface UpdateToolbar {
        public  void changeTitle(int mode);
    }

}
