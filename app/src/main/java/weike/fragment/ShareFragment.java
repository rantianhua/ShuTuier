package weike.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.adapter.GridShareAdapter;
import weike.shutuier.R;
import weike.util.Constants;

/**
 * Created by Rth on 2015/4/3.
 */
public class ShareFragment extends DialogFragment implements AdapterView.OnItemClickListener{

    @InjectView(R.id.gridView_share)
    GridView share;

    final UMSocialService controller  = UMServiceFactory.getUMSocialService("com.umeng.share");
    private static final String IMGURL = "imgUrl";
    private static final String ITEMID  = "itemId";

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
        Integer[] icons = {R.drawable.umeng_socialize_qq_on,
            R.drawable.umeng_socialize_qzone_on,R.drawable.umeng_socialize_sina_on,
            R.drawable.umeng_socialize_tx_on,R.drawable.umeng_socialize_wechat,
            R.drawable.umeng_socialize_wxcircle};
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
                shareMessage(SHARE_MEDIA.WEIXIN);
                break;
            case 5:
                //朋友圈分享
                shareMessage(SHARE_MEDIA.WEIXIN_CIRCLE);
                break;
            default:
                break;
        }
    }

    private void shareMessage(SHARE_MEDIA media) {
//        // 图片分享内容
//        controller.setShareMedia(new UMImage(getActivity(),getArguments().getString(IMGURL)));
//        //文字分享内容
//        controller.setShareContent("测试分享");
        controller.postShare(getActivity(), media, mShareListener);
    }

    /**
     * 分享监听器
     */
    SocializeListeners.SnsPostListener mShareListener = new SocializeListeners.SnsPostListener() {

        @Override
        public void onStart() {

        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int stCode,
                               SocializeEntity entity) {
            if (stCode == 200) {
                Toast.makeText(getActivity(), "分享成功", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(getActivity(),
                        "分享失败 : error code : " + stCode, Toast.LENGTH_SHORT)
                        .show();
            }
        }
    };


    public static ShareFragment getInstance(String iconUrl,int id) {
        ShareFragment fragment = new ShareFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IMGURL,iconUrl);
        bundle.putInt(ITEMID,id);
        fragment.setArguments(bundle);
        return fragment;
    }
}
