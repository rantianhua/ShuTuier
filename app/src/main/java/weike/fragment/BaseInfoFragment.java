package weike.fragment;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.shutuier.R;
import weike.util.Constants;
import weike.util.Utils;

/**
 * Created by Rth on 2015/4/6.
 */
public class BaseInfoFragment extends Fragment implements View.OnClickListener{

    @InjectView(R.id.img_user_icon)
    ImageView userIcon;
    @InjectView(R.id.et_center_nicname)
    EditText etNicName;
    @InjectView(R.id.et_center_sex)
    EditText etSex;
    @InjectView(R.id.et_center_birthday)
    EditText etBirthday;
    @InjectView(R.id.et_center_hobbit)
    EditText etHobbit;
    @InjectView(R.id.et_center_school)
    EditText etSchool;
    @InjectView(R.id.et_center_phone)
    EditText etPhone;
    @InjectView(R.id.et_center_qq)
    EditText etQQ;
    @InjectView(R.id.et_center_wx)
    EditText etWx;
    @InjectView(R.id.et_center_email)
    EditText etEmail;
    @InjectView(R.id.tv_user_icon_label)
    TextView tvIcon;

    private SharedPreferences sp = null;
    private final String TAG = "BaseInfoFragment";
    private static PersonalFragment.UpdateToolbar toolbarListener = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_info,container,false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        ButterKnife.inject(this,v);
        if(sp == null) {
            sp = getActivity().getSharedPreferences(Constants.SP_USER,0);
        }
        //显示头像
        Bitmap bitmap = null;
        try{
            String iconPath = Utils.getPicturePath()+Constants.USERICONFILE;
            File f = new File(iconPath);
            if(f.exists()) {
                bitmap = BitmapFactory.decodeFile(iconPath);
                f = null;
            }else {
                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.user);
            }
            userIcon.setImageBitmap(bitmap);
        }catch (Exception e) {
            Log.e(TAG,"error in get bitmap",e);
        }

        etNicName.setText(sp.getString(Constants.NICNAME,""));
        etSex.setText(sp.getString(Constants.SEX,"男"));
        String birthday = sp.getString(Constants.Birthday,"");
        if(!TextUtils.isEmpty(birthday)) {
            etBirthday.setText(birthday);
        }
        etHobbit.setText(sp.getString(Constants.Hobbit,""));
        etQQ.setText(sp.getString(Constants.QQNumber,""));
        etPhone.setText(sp.getString(Constants.PhoneNumber,""));
        etWx.setText(sp.getString(Constants.WxNumber,""));
        etEmail.setText(sp.getString(Constants.Email,""));
        editAble(false);
    }

    private void editAble(boolean mode) {
        tvIcon.setOnClickListener(mode ? this : null);
        etNicName.setEnabled(mode);
        etSex.setEnabled(mode);
        etBirthday.setEnabled(mode);
        etHobbit.setEnabled(mode);
        etSchool.setEnabled(false);
        etPhone.setEnabled(mode);
        etQQ.setEnabled(mode);
        etWx.setEnabled(mode);
        etEmail.setEnabled(mode);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.center_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_edit:
                if(item.getTitle().equals("编辑")){
                    editAble(true);
                    item.setTitle("保存");
                }else{
                    saveBaseInfo();
                }
                break;
            case android.R.id.home:
                getFragmentManager().popBackStackImmediate();
                break;
            default:
                break;
        }
        return true;
    }

    private void saveBaseInfo() {


    }

    public static BaseInfoFragment getInstance(PersonalFragment.UpdateToolbar listener) {
        toolbarListener = listener;
        return new BaseInfoFragment();
    }

    @Override
    public void onStop() {
        super.onStop();
        toolbarListener.changeTitle(5);
    }
}
