package weike.shutuier;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.data.BookOtherData;
import weike.util.Constants;
import weike.util.GetUserPhotoWork;
import weike.util.HttpManager;
import weike.util.HttpTask;
import weike.util.Mysingleton;

/**
 * Created by Rth on 2015/5/7.
 */
public class MessageActivity extends ActionBarActivity {

    @InjectView(R.id.toolbar_message)
    Toolbar toolbar;
    @InjectView(R.id.ll_message_list)
    LinearLayout llCommentList;
    @InjectView(R.id.rl_loading_message)
    RelativeLayout rlLoading;

    private Handler han;
    private ImageLoader imageLoader;
    private int ownerSize,commenterSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        ownerSize = getResources().getDimensionPixelSize(R.dimen.item_title_photo);
        commenterSize = getResources().getDimensionPixelSize(R.dimen.comment_list_user_size);

        setSupportActionBar(toolbar);
        setTitle("留言");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String id  = getIntent().getStringExtra(Constants.Book_Id);
        if(id != null) {
            getData(id);
        }
    }

    private void getData(String id) {
        if(!MainActivity.netConnect) {
            Toast.makeText(this,"网络不可用",Toast.LENGTH_SHORT).show();
            return;
        }
        rlLoading.setVisibility(View.VISIBLE);
        if(han == null) {
            initHandler();
        }
        String linkUrl = Constants.DetailLink + id;
        try {
            HttpTask task = new HttpTask(this,linkUrl, han,null,null);
            HttpManager.startTask(task);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initHandler() {
        han = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                rlLoading.setVisibility(View.INVISIBLE);
                switch (msg.what) {
                    case 0:
                        BookOtherData otherData = BookOtherData.getInstance();
                        for(int i = 0;i<otherData.getList().size();i++) {
                            llCommentList.addView(initCommentsView(otherData.getList().get(i).get("thirdName"),
                                    otherData.getList().get(i).get("mark1"),
                                    otherData.getList().get(i).get("Head"),
                                    otherData.getList().get(i).get("send_time")), 0);
                        }
                        break;
                    case 1:
                        Toast.makeText(MessageActivity.this, "加载留言失败", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private View initCommentsView(String name, String content, String head, String time) {
        View v = LayoutInflater.from(this).inflate(R.layout.comments_list_item,null);
        ImageView imageView = (ImageView) v.findViewById(R.id.img_comment_photo);
        TextView tvName = (TextView) v.findViewById(R.id.tv_comment_name);
        TextView tvContent = (TextView) v.findViewById(R.id.tv_comment_content);
        TextView tvSendTime = (TextView) v.findViewById(R.id.tv_comment_time);
        tvName.setText(name);
        tvContent.setText(content);
        tvSendTime.setText(time);
        if(head != null) {
            if(imageLoader == null) {
                imageLoader = Mysingleton.getInstance(this).getImageLoader();
            }
            try {
                imageLoader.get(head, ImageLoader.getImageListener(imageView, R.drawable.def, R.drawable.def)
                        ,commenterSize,commenterSize);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            new GetUserPhotoWork(imageView,this,false,commenterSize,commenterSize);
        }
        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BookOtherData.getInstance().getList().clear();
    }
}
