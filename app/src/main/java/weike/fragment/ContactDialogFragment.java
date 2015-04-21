package weike.fragment;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.shutuier.R;
import weike.util.Utils;

/**
 * Created by Rth on 2015/3/28.
 */
public class ContactDialogFragment extends DialogFragment implements View.OnClickListener{

    @InjectView(R.id.tv_contact_none)
    TextView tvNone;
    @InjectView(R.id.tv_contact_qq)
    TextView tvQQ;
    @InjectView(R.id.tv_contact_phone)
    TextView tvPhone;
    @InjectView(R.id.tv_contact_wx)
    TextView tvWx;
    @InjectView(R.id.tv_contact_email)
    TextView tvEmail;
    @InjectView(R.id.tv_contact_cancel)
    TextView tvCancel;
    @InjectView(R.id.tv_contact_end)
    TextView tvEnd;
    @InjectView(R.id.line_contact_one)
    View line1;
    @InjectView(R.id.line_contact_two)
    View line2;
    @InjectView(R.id.line_contact_three)
    View line3;
    @InjectView(R.id.img_contact_add)
    ImageView imgAdd;
    @InjectView(R.id.img_contact_call)
    ImageView imgCall;
    @InjectView(R.id.img_contact_sms)
    ImageView imgSms;

    private Status status = Status.CLOSE;
    private String TAG = "Contact";
    private Animation scaleBig = null,scaleSmall = null;
    private int drawableSize;

    //枚举类，表示电话的子菜单的状态
    public enum Status{
        CLOSE,OPEN
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawableSize = getResources().getDimensionPixelSize(R.dimen.dialog_contact_img_size);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity(),R.style.dialog_contacts);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v = inflater.inflate(R.layout.dialog_contacts,null,false);
        initView(v);
        dialog.setContentView(v);
        return dialog;
    }

    private void initView(View v) {
        ButterKnife.inject(this, v);
        tvEnd.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        Drawable qq = getResources().getDrawable(R.drawable.qq_round);
        qq.setBounds(0,0,drawableSize,drawableSize);
        tvQQ.setCompoundDrawables(qq,null,null,null);
        tvQQ.setCompoundDrawablePadding(20);
        tvQQ.setText("1348748184");
        Drawable phone = getResources().getDrawable(R.drawable.phone);
        phone.setBounds(0,0,drawableSize,drawableSize);
        tvPhone.setCompoundDrawables(phone,null,null,null);
        tvPhone.setCompoundDrawablePadding(20);
        tvPhone.setText("15929733174");
        Drawable wx = getResources().getDrawable(R.drawable.wx_round);
        wx.setBounds(0,0,drawableSize,drawableSize);
        tvWx.setCompoundDrawables(wx,null,null,null);
        tvWx.setCompoundDrawablePadding(20);
        tvWx.setText("rth");
        Drawable email = getResources().getDrawable(R.drawable.email);
        email.setBounds(0,0,drawableSize,drawableSize);
        tvEmail.setCompoundDrawables(email,null,null,null);
        tvEmail.setCompoundDrawablePadding(20);
        tvEmail.setText("15953163807@163.com");
        tvQQ.setOnClickListener(this);
        tvPhone.setOnClickListener(this);
        tvWx.setOnClickListener(this);
        tvEmail.setOnClickListener(this);
        imgCall.setOnClickListener(this);
        imgAdd.setOnClickListener(this);
        imgSms.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_contact_end:
                if(tvEnd.getText().equals("打开QQ")) {
                    startWPA(tvQQ.getText());
                }else if(tvEnd.getText().equals("复制并打开微信")) {
                    openWx();
                }else if(tvEnd.getText().equals("发送邮件")) {
                    sendEmail(tvEmail.getText().toString());
                }else {
                    ContactDialogFragment.this.dismiss();
                }
                break;
            case R.id.tv_contact_cancel:
                this.dismiss();
                break;
            case R.id.tv_contact_phone:
                unSelectContact();
                popMenuItems();
                tvEnd.setText("结束");
                break;
            case R.id.tv_contact_qq:
                updateContacts(tvQQ);
                tvEnd.setText("打开QQ");
                break;
            case R.id.tv_contact_email:
                updateContacts(tvEmail);
                tvEnd.setText("发送邮件");
                break;
            case R.id.tv_contact_wx:
                updateContacts(tvWx);
                tvEnd.setText("复制并打开微信");
                break;
            case R.id.img_contact_call:
                menuItemClicked(0);
                break;
            case R.id.img_contact_add:
                menuItemClicked(1);
                break;
            case R.id.img_contact_sms:
                menuItemClicked(2);
                break;
            default:
                break;
        }
    }

    private void sendEmail(String s) {
        Uri uri = Uri.parse("mailto:" + s);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        if(it.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(it);
        }else {
            Toast.makeText(getActivity(),"未安装可用的邮件应用",Toast.LENGTH_SHORT).show();
        }
    }

    private void openWx() {
        //复制微信号到剪贴板
        ClipboardManager cmb = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("wx_number", tvWx.getText().toString());
        cmb.setPrimaryClip(data);
        //打开微信
        if(checkInstallation("com.tencent.mm")) {
            Intent intent  = new Intent(Intent.ACTION_VIEW);
            ComponentName cmp = new ComponentName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            startActivity(intent);
        }else {
            Toast.makeText(getActivity(),"未安装微信应用",Toast.LENGTH_SHORT).show();
        }
    }

    //检查是否安装了某些应用
    private boolean checkInstallation(String packageName){
        try {
            getActivity().getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //开启QQ WPA会话
    private void startWPA(CharSequence text) {
        String url="mqqwpa://im/chat?chat_type=wpa&uin="+text.toString();
        Intent qqWpa = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if(qqWpa.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(qqWpa);
        }else {
            Toast.makeText(getActivity(),"未安装手机QQ",Toast.LENGTH_SHORT).show();
        }
    }

    private void updateContacts(TextView tvContact) {
        if(status == Status.OPEN) {
            //先隐藏tvPhone的子菜单选项
            popMenuItems();
        }
        unSelectContact();
        tvContact.setSelected(true);
    }

    private void unSelectContact() {
        if(tvQQ.isSelected()) {
            tvQQ.setSelected(false);
            return;
        }
        if(tvEmail.isSelected()) {
            tvEmail.setSelected(false);
            return;
        }
        if(tvWx.isSelected()) {
            tvWx.setSelected(false);
        }
    }

    //子菜单项被点击后的动画,item用来标致哪个子菜单被点击了
    private void menuItemClicked(final int item) {
        if(scaleBig == null) {
            scaleBig = scaleBig(300);
        }
        if(scaleSmall == null) {
            scaleSmall = scaleSmall(300);
        }
        scaleBig.setAnimationListener(null);
        scaleBig.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                switch (item) {
                    case 0:
                        //打电话
                        callPhone();
                        break;
                    case 1:
                        //添加新联系人
                        addContact();
                        break;
                    case 2:
                        //发送短信
                        sendSms();
                        break;
                    default:
                        break;
                }
                ContactDialogFragment.this.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        if(item == 0) {
            imgCall.startAnimation(scaleBig);
            imgSms.startAnimation(scaleSmall);
            imgAdd.startAnimation(scaleSmall);
        }else if(item == 1) {
            imgAdd.startAnimation(scaleBig);
            imgSms.startAnimation(scaleSmall);
            imgCall.startAnimation(scaleSmall);
        }else{
            imgSms.startAnimation(scaleBig);
            imgAdd.startAnimation(scaleSmall);
            imgCall.startAnimation(scaleSmall);
        }
        changeStatus();
    }

    private void addContact() {
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE,tvPhone.getText().toString());
        startActivity(intent);
    }

    private void sendSms() {
        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("smsto:"+tvPhone.getText().toString()));
        startActivity(intent);
    }

    private void callPhone() {
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tvPhone.getText().toString())));
    }

    //缩小消失
    private Animation scaleSmall(int duration) {
        Animation anim = new ScaleAnimation(1.0f, 0f, 1.0f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        anim.setDuration(duration);
        anim.setFillAfter(true);
        return anim;
    }

    //放大同时降低透明度
    private Animation scaleBig(int duration) {
        AnimationSet animationSet = new AnimationSet(true);
        Animation anim = new ScaleAnimation(1.0f,4.0f,1.0f,4.0f,
                Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        Animation alphaAnimation = new AlphaAnimation(1,0);
        animationSet.addAnimation(anim);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        return animationSet;
    }

    //展示或隐藏子菜单项
    private void popMenuItems() {
        for(int i = 0; i < 3;i++) {
            Animation anim = null;
            final int n = i;
            if(status == Status.CLOSE) {
                //展示子菜单项
                anim = AnimationUtils.loadAnimation(getActivity(),R.anim.translate_contact_add_show);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        if(n == 0) {
                            imgSms.setVisibility(View.VISIBLE);
                        }else if(n == 1) {
                            imgAdd.setVisibility(View.VISIBLE);
                        }else {
                            imgCall.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if(n == 2) {
                            imgSms.setClickable(true);
                            imgSms.setFocusable(true);
                            imgAdd.setClickable(true);
                            imgAdd.setFocusable(true);
                            imgCall.setClickable(true);
                            imgCall.setFocusable(true);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }else{
                anim = AnimationUtils.loadAnimation(getActivity(),R.anim.translate_contact_add_hide);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if(n == 0) {
                            imgSms.setClickable(false);
                            imgSms.setFocusable(false);
                            imgSms.setVisibility(View.INVISIBLE);
                        }else if(n == 1) {
                            imgAdd.setClickable(false);
                            imgAdd.setFocusable(false);
                            imgAdd.setVisibility(View.INVISIBLE);
                        }else {
                            imgCall.setClickable(false);
                            imgCall.setFocusable(false);
                            imgCall.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
            anim.setStartOffset(i*100);
            if(i == 0) {
                imgSms.startAnimation(anim);
            }else if(i == 1) {
                imgAdd.startAnimation(anim);
            }else {
                imgCall.startAnimation(anim);
            }
        }
        changeStatus();
    }

    //改变子菜单项的状态
    private void changeStatus() {
        if(status == Status.CLOSE) {
            status = Status.OPEN;
            return;
        }
        status = Status.CLOSE;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(Utils.getWindowWidth(getActivity())-40, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}
