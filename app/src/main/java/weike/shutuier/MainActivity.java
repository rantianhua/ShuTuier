package weike.shutuier;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.fb.FeedbackAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import myinterface.UserInfoChangeListener;
import weike.fragment.BaseInfoFragment;
import weike.fragment.HomeFragment;
import weike.fragment.MessageFragment;
import weike.fragment.SellFragment;
import weike.fragment.SettingFragment;
import weike.util.ConnectReceiver;
import weike.util.Constants;
import weike.util.FragmentLabel;
import weike.util.GetUserPhotoWork;
import weike.util.Utils;
import weike.zing.CaptureActivity;
import weike.zing.Intents;


public class MainActivity extends ActionBarActivity implements View.OnClickListener
        ,ConnectReceiver.GetNetState,UserInfoChangeListener{

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
    FragmentManager fm = null;
    private boolean havaItemSelected = false;   //标识是否有section处于选中状态
    private static final int REQUEST_CODE = 200;
    public static final String TAG = "MainActivity";
    private int sectionIconSize ;   //左侧图片的大小
    //网络状态接收器
    private ConnectReceiver netReceiver = new ConnectReceiver(this);
    public static boolean netConnect = false;    //记录接收的网络状态
    private SharedPreferences sp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //注册广播接收器
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(netReceiver,filter);
        resources = this.getResources();
        sectionIconSize = resources.getDimensionPixelSize(R.dimen.sectionIconSize);
        sp = getSharedPreferences(Constants.SP_USER,0);
        initView();
        FeedbackAgent agent = new FeedbackAgent(this);
        agent.sync();
    }

    private void initView() {
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        setTitle(FragmentLabel.Home.getValue());
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open,
                R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if(fm  == null) {
                    fm = getSupportFragmentManager();
                }
                if(rlCommit.isSelected()) {
                    if(!getTitle().equals(FragmentLabel.Commit.getValue())) {
                        //检查用户有没有登陆
                        if(!sp.getBoolean(Constants.USER_ONLINE_KEY,false)) {
                            Toast.makeText(MainActivity.this,"请先点击头像登陆！",Toast.LENGTH_SHORT).show();
                        }else if(TextUtils.isEmpty(sp.getString(Constants.QQNumber,"")) &&
                                TextUtils.isEmpty(sp.getString(Constants.PhoneNumber,"")) &&
                                TextUtils.isEmpty(sp.getString(Constants.WxNumber,"")) &&
                                TextUtils.isEmpty(sp.getString(Constants.Email,""))) {
                            //先让用户完善联系方式
                            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setMessage("请点击头像到个人中心完善联系方式！")
                                    .setNegativeButton("知道啦", null).create();
                            dialog.setTitle("未完善联系方式");
                            dialog.show();
                        }else {
                            fm.beginTransaction().setCustomAnimations(R.anim.right_in,R.anim.left_out)
                                .replace(R.id.contain,SellFragment.getInstance()).commit();
                            setTitle(FragmentLabel.Commit.getValue());
                        }
                    }
                    return;
                }
                if(rlHome.isSelected()) {
                    if(!getTitle().equals(FragmentLabel.Home.getValue())) {
                        fm.beginTransaction().setCustomAnimations(R.anim.right_in,R.anim.left_out)
                            .replace(R.id.contain, HomeFragment.getInstance()).commit();
                        setTitle(FragmentLabel.Home.getValue());
                    }
                    return;
                }
                if(rlMessage.isSelected()) {
                    if(!getTitle().equals(FragmentLabel.Message.getValue())) {
                        fm.beginTransaction().setCustomAnimations(R.anim.right_in,R.anim.left_out)
                            .replace(R.id.contain, MessageFragment.getInstance()).commit();
                        setTitle(FragmentLabel.Message.getValue());
                    }
                }
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        //模糊背景图
        int width = Utils.getWindowWidth(this);
        int height = resources.getDimensionPixelSize(R.dimen.userSpace);
        Utils.loadBlurBitmap(this,userBg,R.drawable.user_bg,25,width,height);

        //显示DrawerLayout中的基本用户信息
        updateUserInfo();
        userPhoto.setOnClickListener(this);

        //初始化fragment
        if(fm == null) {
            fm = getSupportFragmentManager();
        }
        fm.beginTransaction().add(R.id.contain,HomeFragment.getInstance()).commit();

        rlSetting.setOnClickListener(this);
        rlCommit.setOnClickListener(this);
        rlHome.setOnClickListener(this);
        rlMessage.setOnClickListener(this);
        rlSetting.setOnClickListener(this);
        rlSwipe.setOnClickListener(this);

        updateIcon(iconHome,R.drawable.section_home_icon,1);
        updateIcon(iconCommit,R.drawable.section_sell_icon,1);
        updateIcon(iconMessage,R.drawable.calling,1);
        updateIcon(iconSwipe,R.drawable.scan,1);
    }

    private void updateUserInfo() {
        //检查用户是否已登陆
        if(sp.getBoolean(Constants.USER_ONLINE_KEY,false)) {
            new GetUserPhotoWork(userPhoto,this,true).execute();
            userName.setText(sp.getString(Constants.NICNAME,"获取昵称失败"));
        }else {
            userPhoto.setImageResource(R.drawable.user);
            userName.setText("您还未登陆！");
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
        overridePendingTransition(R.anim.right_in,R.anim.left_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_photo:
                Intent intent = new Intent();
                //判断用户有没有登陆
                if(!sp.getBoolean(Constants.USER_ONLINE_KEY,false)) {
                    //跳转到登陆界面
                    intent.setClass(MainActivity.this,LoginActivity.class);
                }else {
                    BaseInfoFragment.userInfoListener = MainActivity.this;
                    intent.setClass(MainActivity.this,PersonalCenterActivity.class);
                }
                startActivity(intent);
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
                break;
            case R.id.rl_section_swipe:
                if(!rlSwipe.isSelected()) {
                    changeSection(rlSwipe, tvSwipe, iconSwipe, R.drawable.scan);
                }
                startScan();
                break;
            case R.id.rl_section_setting:
                SettingFragment.userInfoListener = this;
                startActivity(new Intent(MainActivity.this,SettingActivity.class));
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
                break;
            case R.id.rl_section_message:
                if(!rlMessage.isSelected()) {
                    changeSection(rlMessage,tvMessage,iconMessage,R.drawable.calling);
                }else {
                    mDrawerLayout.closeDrawers();
                }
                break;
            case R.id.rl_section_commit:
                if(!rlCommit.isSelected()) {
                    changeSection(rlCommit,tvCommit,iconCommit,R.drawable.section_sell_icon);
                }else {
                    mDrawerLayout.closeDrawers();
                }
                break;
            case R.id.rl_section_home:
                if(!rlHome.isSelected()) {
                    changeSection(rlHome,tvHome,iconHome,R.drawable.section_home_icon);
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
            updateIcon(iconMessage,R.drawable.calling,1);
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

    @Override
    public void sendState(boolean state) {
        netConnect = state;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(netReceiver != null) {
            this.unregisterReceiver(netReceiver);
            netReceiver = null;
        }
    }

    @Override
    public void userInfoChanged() {
        updateUserInfo();
    }
}
