package weike.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.adapter.GridShareAdapter;
import weike.shutuier.R;
import weike.util.Constants;
import weike.util.HttpManager;
import weike.util.HttpTask;

/**
 * Created by Rth on 2015/4/3.
 */
public class ShareFragment extends DialogFragment implements AdapterView.OnItemClickListener{

    @InjectView(R.id.gridView_share)
    GridView share;

    final UMSocialService controller  = UMServiceFactory.getUMSocialService("com.umeng.share");
    private static final String IMGURL = "imgUrl";
    private static final String ITEMID  = "itemId";
    private  final String TAG = getClass().getSimpleName();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.dialog_cover);
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_share,null);
        completeUmeng();
        initContentView(v);
        dialog.setContentView(v);
        return dialog;
    }

    private void initContentView(View v) {
        ButterKnife.inject(this,v);
        String[] tvs = {"QQ好友","QQ空间","微博","腾讯微博","微信","朋友圈"};
        Integer[] icons = {R.drawable.qq_round,
            R.drawable.q_zone,
            R.drawable.sina_round,
            R.drawable.qq_weibo,
            R.drawable.wx_round,
            R.drawable.wx_friends};
        GridShareAdapter adapter = new GridShareAdapter(getActivity(),tvs,icons);
        share.setAdapter(adapter);
        share.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void completeUmeng() {

        //添加QQ平台
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(getActivity(), "1104326437",
                "Jj4RMmh7LOSdOeSU");
        qqSsoHandler.addToSocialSDK();

        //添加QQ空间平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(getActivity(), "1104326437",
                "Jj4RMmh7LOSdOeSU");
        qZoneSsoHandler.addToSocialSDK();

        //设置新浪SSO handler
        controller.getConfig().setSsoHandler(new SinaSsoHandler());
        //设置腾讯微博SSO handler
        controller.getConfig().setSsoHandler(new TencentWBSsoHandler());

        String appId = "wxd2033153d9e5d21c";
        String appSecret = "9e07451fa60ee561bd688565aa5112cd";
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(getActivity(),appId,appSecret);
        wxHandler.addToSocialSDK();
        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(getActivity(),appId,appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = controller.getConfig().getSsoHandler(requestCode) ;
        if(ssoHandler != null){
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                //qq好友分享
                QQShareContent qqContent = new QQShareContent();
                //分享文字
                qqContent.setShareContent(Constants.ShareLink+getArguments().getInt(ITEMID));
                qqContent.setTitle("藤书坊");
                //分享的图片
                qqContent.setShareImage(new UMImage(getActivity(), getArguments().getString(IMGURL)));
                qqContent.setTargetUrl(Constants.ShareLink + getArguments().getInt(ITEMID));
                controller.setShareMedia(qqContent);
                shareMessage(SHARE_MEDIA.QQ);
                break;
            case 1:
                //qq空间分享
                QZoneShareContent qZoneContent = new QZoneShareContent();
                qZoneContent.setShareContent("以书会友，以书交友");
                qZoneContent.setTargetUrl(Constants.ShareLink + getArguments().getInt(ITEMID));
                qZoneContent.setTitle("藤书坊");
                qZoneContent.setShareImage(new UMImage(getActivity(), getArguments().getString(IMGURL)));
                controller.setShareMedia(qZoneContent);
                shareMessage(SHARE_MEDIA.QZONE);
                break;
            case 2:
                //新浪微博分享
                controller.setShareImage(new UMImage(getActivity(),getArguments().getString(IMGURL)));
                controller.setShareContent("以书会友，以书交友" +Constants.ShareLink + getArguments().getInt(ITEMID));
                shareMessage(SHARE_MEDIA.SINA);
                break;
            case 3:
               //腾讯微博分享
                controller.setShareImage(new UMImage(getActivity(),getArguments().getString(IMGURL)));
                controller.setShareContent("以书会友，以书交友" +Constants.ShareLink + getArguments().getInt(ITEMID));
                shareMessage(SHARE_MEDIA.TENCENT);
                break;
            case 4:
                //威信好友分享
                //设置微信好友分享内容
                WeiXinShareContent weixinContent = new WeiXinShareContent();
                //设置分享文字
                weixinContent.setShareContent("以书会友，以书交友");
                //设置title
                weixinContent.setTitle("藤书坊");
                //设置分享内容跳转URL
                weixinContent.setTargetUrl(Constants.ShareLink + getArguments().getInt(ITEMID));
                //设置分享图片
                weixinContent.setShareImage(new UMImage(getActivity(),getArguments().getString(IMGURL)));
                controller.setShareMedia(weixinContent);
                shareMessage(SHARE_MEDIA.WEIXIN);
                break;
            case 5:
                //朋友圈分享
                //设置微信朋友圈分享内容
                CircleShareContent circleMedia = new CircleShareContent();
                circleMedia.setShareContent("以书会友，以书交友");
                //设置朋友圈title
                circleMedia.setTitle("藤书坊");
                circleMedia.setShareImage(new UMImage(getActivity(),getArguments().getString(IMGURL)));
                circleMedia.setTargetUrl(Constants.ShareLink + getArguments().getInt(ITEMID));
                controller.setShareMedia(circleMedia);
                shareMessage(SHARE_MEDIA.WEIXIN_CIRCLE);
                break;
            default:
                break;
        }
    }

    private void shareMessage(SHARE_MEDIA media) {
        controller.postShare(getActivity(), media, new SocializeListeners.SnsPostListener() {
            @Override
            public void onStart() {
                notifyServer();
            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, SocializeEntity socializeEntity) {
            }
        });
        this.dismiss();
    }

    private void notifyServer() {
        String url = Constants.SHARENOTIFYLINK+getArguments().getInt(ITEMID);
        HttpTask task = new HttpTask(getActivity(),url,new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        },TAG, null);
        HttpManager.startTask(task);
    }

    public static ShareFragment getInstance(String iconUrl,int id) {
        ShareFragment fragment = new ShareFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IMGURL,iconUrl);
        bundle.putInt(ITEMID,id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
