package weike.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.Toast;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;

import org.json.JSONObject;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.data.UserInfoData;
import myinterface.UserInfoChangeListener;
import weike.shutuier.MainActivity;
import weike.shutuier.R;
import weike.util.Constants;
import weike.util.GetUserPhotoWork;
import weike.util.HttpManager;
import weike.util.HttpTask;
import weike.util.UploadPicture;
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
    @InjectView(R.id.et_center_address)
    EditText etAddress;

    private SharedPreferences sp = null;
    public static final String TAG = "BaseInfoFragment";
    private static PersonalFragment.UpdateToolbar toolbarListener = null;
    private final int REQUEST_PIC_WAY = 41; //打开选择图片方式的请求代码
    private final String iconPath = Utils.getPicturePath() + Constants.USERICONFILE; //头像存储路径
    private final int REQUEST_ALBUM = 42;    //打开系统图册的请求代码
    private String uploadPicPath = null;    //待上传图片的路径
    private String takePicPath = null;         //拍摄图片的存储路径
    private boolean changeMessage = false;    //判断有没有修改头像
    private Handler saveHan = null; //保存用户信息的异步处理
    private static String from = null;  //标记是谁启动的Fragment
    private ProgressDialog pd = null;
    public static UserInfoChangeListener userInfoListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        sp = getActivity().getSharedPreferences(Constants.SP_USER,0);
        uploadPicPath = sp.getString(Constants.USERURL,""); //初始化用户头像链接
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_info,container,false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        ButterKnife.inject(this,v);
        //显示头像
        new GetUserPhotoWork(userIcon,getActivity(),false).execute();

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
        etEmail.setText(sp.getString(Constants.Email, ""));
        etSex.addTextChangedListener(textWatcher);
        etNicName.addTextChangedListener(textWatcher);
        etBirthday.addTextChangedListener(textWatcher);
        etHobbit.addTextChangedListener(textWatcher);
        etQQ.addTextChangedListener(textWatcher);
        etPhone.addTextChangedListener(textWatcher);
        etEmail.addTextChangedListener(textWatcher);
        etWx.addTextChangedListener(textWatcher);
        etAddress.addTextChangedListener(textWatcher);
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
        switch (v.getId()) {
            case R.id.tv_user_icon_label:
                CoverDialogFragment cover = new CoverDialogFragment();
                cover.setTargetFragment(this,REQUEST_PIC_WAY);
                cover.show(getChildFragmentManager(),"pic_way");
                break;
            default:
                break;
        }

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
                    if(changeMessage) {
                        saveBaseInfo();
                        item.setTitle("编辑");
                        editAble(false);
                    }
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
        //先暂存基本信息
        cacheUserInfo();
        //根据启动来源做进一步判断
        if(from.equals(MainActivity.TAG)) {
            //此时检查联系方式是否完善
            if(!checkContact()) {
                return;
            }
        }
        if(pd == null) {
            pd = new ProgressDialog(getActivity());
        }
        pd.setMessage("正在保存...");
        pd.show();
        //检查头像有没有改变
        if(!uploadPicPath.equals(sp.getString(Constants.USERURL,""))) {
            //上传新头像到七牛
            try{
                new UploadPicture(uploadPicPath,upHandler).upToQiNiu();
            }catch (Exception e) {
                Log.e(TAG,"error in upload user icon" ,e);
                //删除暂存数据
                UserInfoData.recycle();
            }
        }else {
            postUserInfo();
        }
    }

    private boolean checkContact() {
        if(TextUtils.isEmpty(etPhone.getText()) && TextUtils.isEmpty(etQQ.getText())
            && TextUtils.isEmpty(etWx.getText()) && TextUtils.isEmpty(etEmail.getText())) {
            Toast.makeText(getActivity(),"至少填写一种联系方式",Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }


    //上传图片到七牛的异步Handler
    private UpCompletionHandler upHandler = new UpCompletionHandler() {
        @Override
        public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
            if(jsonObject != null) {
                //解析Json获得hash值
                if(jsonObject.has("hash")){
                    try{
                        String hash = jsonObject.getString("hash");
                        if(hash != null) {
                            String url  = Constants.PICLINK + hash;
                            UserInfoData.getInstance().setUserUrl(url);
                            url = null;
                            //保存该图片到本地文件
                            File newFile = new File(uploadPicPath);
                            File preFile = new File(iconPath);
                            if(preFile.exists()) {
                                preFile.delete();
                            }
                            if(newFile.exists()) {
                                newFile.renameTo(preFile);
                            }
                            postUserInfo();
                        }
                    }catch (Exception e) {
                        Log.e("SellFragment","error in get hash",e);
                    }
                }
            }else {
                if(pd.isShowing()) {
                    pd.dismiss();
                }
                Toast.makeText(getActivity(),"上传头像失败。",Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void postUserInfo() {
        if(saveHan == null) {
            initSaveHandler();
        }
        HttpTask task = new HttpTask(getActivity(),Constants.CHANGEUSERINFO,saveHan,TAG,"post");
        HttpManager.startTask(task);
    }

    private void initSaveHandler() {
        saveHan = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(pd.isShowing()) {
                    pd.dismiss();
                    pd = null;
                }
                if(msg.what == 0 && msg.obj.toString().equals("true")) {
                    //保存成功，将新数据写入SharePrefernce
                    Toast.makeText(getActivity(),"保存成功",Toast.LENGTH_SHORT).show();
                    updateLocal();
                    if(userInfoListener != null) {
                        userInfoListener.userInfoChanged();
                    }
                    if(from.equals(MainActivity.TAG)) {
                        getFragmentManager().beginTransaction().replace(R.id.contain,SellFragment.getInstance()).commit();
                    }
                }else {
                    Toast.makeText(getActivity(),"保存失败",Toast.LENGTH_SHORT).show();
                    UserInfoData.recycle();
                }
            }
        };
    }

    //暂存数据
    private void cacheUserInfo() {
        UserInfoData data = UserInfoData.getInstance();
        data.setNicName(etNicName.getText().toString());
        data.setSex(etSex.getText().toString());
        data.setBirthday(etBirthday.getText().toString());
        data.setHobbit(etHobbit.getText().toString());
        data.setAddress(etAddress.getText().toString());
        data.setQqNumber(etQQ.getText().toString());
        data.setPhoneNumber(etPhone.getText().toString());
        data.setWxNumber(etWx.getText().toString());
        data.setEmail(etEmail.getText().toString());
        data.setUserUrl(sp.getString(Constants.USERURL,""));
    }

    private void updateLocal() {
        //更新本地数据
        UserInfoData newData = UserInfoData.getInstance();
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.NICNAME,newData.getNicName());
        editor.putString(Constants.SEX,newData.getSex());
        editor.putString(Constants.Birthday,newData.getBirthday());
        editor.putString(Constants.Hobbit,newData.getHobbit());
        editor.putString(Constants.School,newData.getSchool());
        editor.putString(Constants.Address,newData.getAddress());
        editor.putString(Constants.QQNumber,newData.getQqNumber());
        editor.putString(Constants.PhoneNumber,newData.getPhoneNumber());
        editor.putString(Constants.WxNumber,newData.getWxNumber());
        editor.putString(Constants.Email,newData.getEmail());
        editor.putString(Constants.USERURL,newData.getUserUrl());
        editor.apply();
        UserInfoData.recycle();
    }

    public static BaseInfoFragment getInstance(PersonalFragment.UpdateToolbar listener,String tag) {
        if(listener != null) {
            toolbarListener = listener;
        }
        from = tag;
        return new BaseInfoFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(toolbarListener != null) {
            toolbarListener.changeTitle(5);
            toolbarListener = null;
        }
        if(userInfoListener != null) {
            userInfoListener = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG,"requestCode is " + requestCode + " resultCode is " + resultCode);
        if(requestCode == REQUEST_PIC_WAY && resultCode == Activity.RESULT_OK && data != null) {
            getCover(data.getStringExtra(Constants.EXTRA_PIC));
        }else if(requestCode == REQUEST_ALBUM && resultCode == Activity.RESULT_OK && data != null) {
            Uri seleectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(seleectedImage,filePathColumn,null,null,null) ;
            if(cursor != null && cursor.moveToFirst()) {
                int columIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                String picPath = cursor.getString(columIndex);
                cursor.close();
                cursor = null;
                updateCover(picPath);
            }
            data = null;
        }else if(requestCode == Constants.REQUEST_IMAGE_CAPTURE ) {
            updateCover(takePicPath);
        }
        else
         {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //显示新的头像
    private void updateCover(String path) {
        if(path != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            if(bitmap != null) {
                userIcon.setImageBitmap(bitmap);
                changeMessage = true;
                uploadPicPath = path;
            }
        }
        Log.e(TAG,"path is "  + uploadPicPath);
    }

    //根据选中的方式获取头像
    private void getCover(String extra) {
        if(extra.equals("拍照")) {
            openCamera();
        }else {
            openAlbum();
        }
    }

    //打开系统相册
    private void openAlbum() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        if (albumIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(albumIntent, REQUEST_ALBUM);
        }
    }

    private void openCamera() {
        //打开相机拍照
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //确保程序能处理返回的Intent
        if(takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            takePicPath = Utils.getPicturePath() +  "/" + System.currentTimeMillis() + ".jpg";
            Uri imageUri = Uri.fromFile(new File(takePicPath));
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);   //将照片存放在指定位置
            startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE);
        }
    }

    //监听EditText内容变化
    private TextWatcher textWatcher = new TextWatcher() {

        private String preText = "";

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            preText = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(!preText.equals(s.toString())) {
                changeMessage = true;
            }
        }
    };
}
