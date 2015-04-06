package weike.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.shutuier.R;
import weike.util.Constants;
import weike.util.Utils;

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

    private final String TAG = "PersonalFragment";
    private static UpdateToolbar toolbarListener = null;

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
        showUserPhoto();
        Utils.loadBlurBitmap(getActivity(), imgBg, R.drawable.center_bg, 25, 0, 0);
    }

    private void showUserPhoto() {
        Bitmap bitmap = null;
        try{
            String iconPath = Utils.getPicturePath()+ Constants.USERICONFILE;
            File f = new File(iconPath);
            if(f.exists()) {
                bitmap = BitmapFactory.decodeFile(iconPath);
                f = null;
            }else {
                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.user);
            }
            userPhoto.setImageBitmap(Utils.getCroppedBitmapDrawable(bitmap));
        }catch (Exception e) {
            Log.e(TAG, "error in get bitmap", e);
        }
    }

    public static PersonalFragment getInstance(UpdateToolbar listener) {
        toolbarListener = listener;
       return new PersonalFragment();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_my_base_information:
                getFragmentManager().beginTransaction().replace(R.id.container,BaseInfoFragment.getInstance(toolbarListener)).addToBackStack(null).commit();
                toolbarListener.changeTitle(4);
                break;
            case R.id.tv_my_send:
                break;
            case R.id.tv_my_ask_send:
                break;
            case R.id.tv_my_buy:
                break;
            case R.id.tv_my_sell:
                getFragmentManager().beginTransaction().replace(R.id.container,MySellFragment.getInstance(toolbarListener)).addToBackStack(null).commit();
                toolbarListener.changeTitle(0);
                break;
        }
    }

    public interface UpdateToolbar {
        public  void changeTitle(int mode);
    }

}
