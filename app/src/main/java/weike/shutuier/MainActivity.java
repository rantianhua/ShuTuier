package weike.shutuier;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.fragment.HomeFragment;
import weike.fragment.MessageFragment;
import weike.fragment.ScanFragment;
import weike.fragment.SellFragment;
import weike.fragment.SettingFragment;
import weike.util.Constants;
import weike.util.Utils;
import weike.zing.CaptureActivity;
import weike.zing.Intents;


public class MainActivity extends ActionBarActivity implements View.OnClickListener{

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.drawer)
    DrawerLayout mDrawerLayout;
    @InjectView(R.id.user_photo)
    ImageView userPhoto;
    @InjectView(R.id.user_cover)
    ImageView userBg;
    @InjectView(R.id.user_name)
    TextView userName;
    @InjectView(R.id.icon_commit)
    ImageView iconCommit;
    @InjectView(R.id.icon_home)
    ImageView iconHome;
    @InjectView(R.id.icon_message)
    ImageView iconMessage;
    @InjectView(R.id.icon_swipe)
    ImageView iconSwipe;
    @InjectView(R.id.tv_section_commit)
    TextView tvCommit;
    @InjectView(R.id.tv_section_home)
    TextView tvHome;
    @InjectView(R.id.tv_section_message)
    TextView tvMessage;
    @InjectView(R.id.tv_section_message_number)
    TextView tvMessageNumber;
    @InjectView(R.id.tv_section_swipe)
    TextView tvSwipe;
    @InjectView(R.id.rl_section_home)
    RelativeLayout rlHome;
    @InjectView(R.id.rl_section_commit)
    RelativeLayout rlCommit;
    @InjectView(R.id.rl_section_message)
    RelativeLayout rlMessage;
    @InjectView(R.id.rl_section_setting)
    RelativeLayout rlSetting;
    @InjectView(R.id.rl_section_swipe)
    RelativeLayout rlSwipe;

    private Resources resources = null;
    private FragmentManager fm = null;
    private boolean havaItemSelected = false;   //标识是否有section处于选中状态
    private static final int REQUEST_CODE = 200;
    private final String TAG = "MainActivity";
    private int sectionIconSize ;   //左侧图片的大小
    private String userPhotoPath = Utils.getPicturePath() + Constants.USERICONFILE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resources = this.getResources();
        sectionIconSize = resources.getDimensionPixelSize(R.dimen.sectionIconSize);
        initView();
    }

    private void initView() {
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open,
                R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if(rlCommit.isSelected()) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.contain, SellFragment.getInstance()).commit();
                    return;
                }
                if(rlHome.isSelected()) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.contain, HomeFragment.getInstance()).commit();
                    return;
                }
                if(rlMessage.isSelected()) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.contain, MessageFragment.getInstance()).commit();
                    return;
                }
                if(rlSwipe.isSelected()) {
                    startScan();
                }
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_search:
                        Toast.makeText(MainActivity.this, "actionSearch", Toast.LENGTH_SHORT).show();
                    default:
                        break;
                }
                return true;
            }
        });

        //模糊背景图
        int width = Utils.getWindowWidth(this);
        int height = resources.getDimensionPixelSize(R.dimen.userSpace);
        Utils.loadBlurBitmap(this,userBg,R.drawable.user_bg,25,width,height);

        //显示DrawerLayout中的基本用户信息
        updateUserInfo();
        userPhoto.setOnClickListener(this);

        //初始化fragment
        fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.contain);
        if(fragment == null) {
            fragment = HomeFragment.getInstance();
            fm.beginTransaction().add(R.id.contain,fragment).commit();
        }

        rlSetting.setOnClickListener(this);
        rlCommit.setOnClickListener(this);
        rlHome.setOnClickListener(this);
        rlMessage.setOnClickListener(this);
        rlSetting.setOnClickListener(this);
        rlSwipe.setOnClickListener(this);

        updateIcon(iconHome,R.drawable.section_home_icon,1);
        updateIcon(iconCommit,R.drawable.section_sell_icon,1);
        updateIcon(iconMessage,R.drawable.calling_48,1);
        updateIcon(iconSwipe,R.drawable.scan,1);
    }

    private void updateUserInfo() {
        //检查用户是否已登陆
        SharedPreferences sp = getSharedPreferences(Constants.SP_USER,0);
        if(sp.getBoolean(Constants.USER_ONLINE_KEY,false)) {
            //先检查本地是否已有图片
            File f = new File(userPhotoPath);
            if(f.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(userPhotoPath);
                userPhoto.setImageBitmap(Utils.getCroppedBitmapDrawable(bitmap));
            }else {
                String url = sp.getString(Constants.USERURL,null) ;
                if(url != null) {
                    new GetBitmap().execute(url,null,null);
                }
            }
            userName.setText(sp.getString(Constants.NICNAME,""));
        }else {
            userPhoto.setImageBitmap(BitmapFactory.decodeResource(resources,R.drawable.user));
            userName.setText("您还未登陆！");
        }
    }

    private class GetBitmap extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            Bitmap bit = null;
            URL myUrl;
            HttpURLConnection conn = null;
            try {
                myUrl = new URL(url);
                conn = (HttpURLConnection) myUrl
                        .openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bit =  BitmapFactory.decodeStream(is);
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "error in getUsrPhoto", e);
            }finally {
                if(conn != null) {
                    conn.disconnect();
                    conn = null;
                }
                myUrl = null;
            }
            if(bit != null) {
                try {
                    File f =new File(userPhotoPath);
                    FileOutputStream out = new FileOutputStream(f);
                    bit.compress(Bitmap.CompressFormat.JPEG,100,out);
                    out.flush();
                    out.close();
                }catch (Exception e) {
                    Log.e(TAG,"error in save photo",e);
                }
            }
            return bit;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null) {
                userPhoto.setImageBitmap(Utils.getCroppedBitmapDrawable(bitmap));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //开启扫码
    private void startScan() {
        Intent intent = new Intent();
        intent.setAction(Intents.Scan.ACTION);
        intent.putExtra(Intents.Scan.MODE, Intents.Scan.PRODUCT_MODE);
        intent.putExtra(Intents.Scan.CHARACTER_SET, "UTF-8");
//        intent.putExtra(Intents.Scan.WIDTH, 800);
//        intent.putExtra(Intents.Scan.HEIGHT, 600);
        // intent.putExtra(Intents.Scan.PROMPT_MESSAGE, "type your prompt message");
        intent.setClass(this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_photo:
                Intent intent = new Intent(MainActivity.this,PersonalCenterActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_section_swipe:
                if(!rlSwipe.isSelected()) {
                    changeSection(rlSwipe,tvSwipe,iconSwipe,R.drawable.scan);
                    getSupportFragmentManager().beginTransaction().replace(R.id.contain, ScanFragment.getInstance()).commit();
                }else {
                    mDrawerLayout.closeDrawers();
                }
                break;
            case R.id.rl_section_setting:
                getSupportFragmentManager().beginTransaction().replace(R.id.contain, SettingFragment.getInstance()).commit();
                break;
            case R.id.rl_section_message:
                if(!rlMessage.isSelected()) {
                    changeSection(rlMessage,tvMessage,iconMessage,R.drawable.calling_48);
                    //getSupportFragmentManager().beginTransaction().replace(R.id.contain, MessageFragment.getInstance()).commit();
                }else {
                    mDrawerLayout.closeDrawers();
                }
                break;
            case R.id.rl_section_commit:
                if(!rlCommit.isSelected()) {
                    changeSection(rlCommit,tvCommit,iconCommit,R.drawable.section_sell_icon);
                    //getSupportFragmentManager().beginTransaction().replace(R.id.contain, SellFragment.getInstance()).commit();
                }else {
                    mDrawerLayout.closeDrawers();
                }
                break;
            case R.id.rl_section_home:
                if(!rlHome.isSelected()) {
                    changeSection(rlHome,tvHome,iconHome,R.drawable.section_home_icon);
                    //getSupportFragmentManager().beginTransaction().replace(R.id.contain, HomeFragment.getInstance()).commit();
                }else {
                    mDrawerLayout.closeDrawers();
                }
                break;
            default:
                break;
        }
    }

    //改变section的状态
    private void changeSection(RelativeLayout rl, TextView tv, ImageView img,int drawId) {
        //恢复选中的section
        if(!havaItemSelected) {
            havaItemSelected = true;
        }else {
            backToUnSelected();
        }
        updateIcon(img,drawId,0);
        rl.setSelected(true);
        tv.setActivated(true);
        mDrawerLayout.closeDrawers();
    }

    private void backToUnSelected() {
        if(rlHome.isSelected()) {
            updateIcon(iconHome,R.drawable.section_home_icon,1);
            rlHome.setSelected(false);
            tvHome.setActivated(false);
            return;
        }
        if(rlCommit.isSelected()) {
            updateIcon(iconCommit,R.drawable.section_sell_icon,1);
            rlCommit.setSelected(false);
            tvCommit.setActivated(false);
            return;
        }
        if(rlMessage.isSelected()) {
            updateIcon(iconMessage,R.drawable.calling_48,1);
            rlMessage.setSelected(false);
            tvMessage.setActivated(false);
            return;
        }
        if(rlSwipe.isSelected()) {
            updateIcon(iconSwipe,R.drawable.scan,1);
            rlSwipe.setSelected(false);
            tvSwipe.setActivated(false);
        }
    }

    //更新左侧图标的颜色，mode作为标识，1表示更改icon为未选中时的颜色，0相反
    private void updateIcon(ImageView icon,int drawableId, int mode) {
        if(mode == 0) {
            Utils.loadBitmap(resources,icon,drawableId,sectionIconSize,sectionIconSize,R.color.white);
        }else {
            Utils.loadBitmap(resources,icon,drawableId,sectionIconSize,sectionIconSize,R.color.section_selected);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != data && requestCode == REQUEST_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    data.setClass(this, CaptureResultActivity.class);
                    startActivity(data);
                    break;
                default:
                    break;
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
