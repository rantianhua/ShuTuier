package weike.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.shutuier.R;
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

        Utils.loadBlurBitmap(getActivity(), imgBg, R.drawable.center_bg, 25, 0, 0);
    }

    public static PersonalFragment getInstance() {
       return new PersonalFragment();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_my_base_information:
                break;
            case R.id.tv_my_send:
                break;
            case R.id.tv_my_ask_send:
                break;
            case R.id.tv_my_buy:
                Toast.makeText(getActivity(),"我的求购",Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_my_sell:
                getFragmentManager().beginTransaction().replace(R.id.container,MySellFragment.getInstance()).commit();
                break;
        }
    }
}
