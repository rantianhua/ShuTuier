package weike.shutuier;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.data.BookItem;
import weike.data.BookOtherData;
import weike.data.CommentData;
import weike.data.ListBookData;
import weike.fragment.ContactDialogFragment;
import weike.fragment.ShareFragment;
import weike.util.Constants;
import weike.util.HttpManager;
import weike.util.HttpTask;
import weike.util.Mysingleton;

/**
 * Created by Rth on 2015/2/26.
 */
public class BookDetailActivity extends ActionBarActivity implements View.OnClickListener {

    @InjectView(R.id.pb_frament_detail)
    ProgressBar pb;
    @InjectView(R.id.img_bookcover)
    ImageView bookCover;
    @InjectView(R.id.img_book_details_user)
    ImageView userIcon;
    @InjectView(R.id.tv_book_name)
    TextView tvBookName;
    @InjectView(R.id.tv_book_author)
    TextView tvAuthor;
    @InjectView(R.id.tv_book_details_isbn)
    TextView tvIsbn;
    @InjectView(R.id.tv_book_details_publisher)
    TextView tvPublisher;
    @InjectView(R.id.tv_book_details_origin_price)
    TextView tvOPrice;
    @InjectView(R.id.tv_book_details_sell_price)
    TextView tvSPrice;
    @InjectView(R.id.tv_book_details_ownername)
    TextView tvOwner;
    @InjectView(R.id.tv_book_details_decription)
    TextView tvDetail;
    @InjectView(R.id.tv_book_details_remark)
    TextView tvRemark;
    @InjectView(R.id.ll_comments_list)
    LinearLayout ll;
    @InjectView(R.id.rl_detail_liuyan)
    RelativeLayout rlLiuyan;
    @InjectView(R.id.btn_want_buy)
    Button btnWantBuy;
    @InjectView(R.id.rl_detail_share)
    RelativeLayout rlShare;
    @InjectView(R.id.ll_bottom_buy_action)
    LinearLayout llBuyAction;
    @InjectView(R.id.ll_btn_want_buy)
    LinearLayout llWntBuy;
    @InjectView(R.id.ll_btn_have_sell)
    LinearLayout llHaveSell;
    @InjectView(R.id.btn_have_sell)
    Button haveSell;
    @InjectView(R.id.ll_btn_ask_give)
    LinearLayout llAskGive;
    @InjectView(R.id.btn_ask_give)
    Button askGive;
    @InjectView(R.id.ll_btn_give_he)
    LinearLayout llGiveHe;
    @InjectView(R.id.btn_give_he)
    Button giveHe;
    @InjectView(R.id.rl_bottom_commend)
    RelativeLayout rlBottomCommend;
    @InjectView(R.id.btn_back)
    Button btnBack;
    @InjectView(R.id.et_comments)
    EditText etCommend;
    @InjectView(R.id.btn_send_comment)
    Button btnSend;
    @InjectView(R.id.tv_send_condition)
    TextView tvSendCondition;

    private TextView tvLiuyanNumber,tvShareNumber;

    private Handler hanGetDetail = null,hanComment=null;
    private ImageLoader imageLoader;
    private Map<String,Integer> map;
    private int itemId;
    private String comment = null;
    public static final String TAG = "BookDetailActivity";
    private BookItem item = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        initView();
    }

    private void initView() {
        ButterKnife.inject(this);

        tvShareNumber = (TextView) rlShare.findViewById(R.id.tv_share_number);
        tvLiuyanNumber = (TextView) rlLiuyan.findViewById(R.id.tv_liuyan_number);

        pb.setVisibility(View.VISIBLE);
        if(hanGetDetail == null) {
            initHandler();
        }

        Intent intent = getIntent();
        itemId = intent.getIntExtra(Constants.EXTRA_ITEM_ID, 0);
        String linkUrl = Constants.DetailLink + itemId;
        final String from = intent.getStringExtra(Constants.REQUEST_FROM_FRAGMENT);
        HttpTask task = new HttpTask(this,linkUrl, hanGetDetail,from,null);
        HttpManager.startTask(task);
        ListBookData data = ListBookData.getInstance(from);
        item = (BookItem) data.getBookItemById(itemId).get("item");

        if(imageLoader == null) {
            imageLoader = Mysingleton.getInstance(this).getImageLoader();
        }

        imageLoader.get(item.getImgUrl(),ImageLoader.getImageListener(bookCover,R.drawable.def,R.drawable.def));
        StringBuilder builder = new StringBuilder();
        builder.append("书名：");
        builder.append(item.getBookName());
        tvBookName.setText(builder.toString());
        builder.delete(0, builder.length());
        builder.append("作者：");
        builder.append(item.getAuthorName());
        tvAuthor.setText(builder.toString());
        builder.delete(0,builder.length());
        builder.append("出版社：");
        builder.append(item.getPublisher());
        tvPublisher.setText(builder);
        builder.delete(0,builder.length());
        builder.append("￥");
        builder.append(item.getOriginPrice());
        tvOPrice.setText(builder);
        builder.delete(0,builder.length());
        builder.append("￥");
        builder.append(item.getSellPrice());
        tvSPrice.setText(builder);
        tvDetail.setText(item.getDetail());
        tvRemark.setText(item.getRemark());
        builder = null;
        initBottomView();
    }

    //初始化底部视图
    private void initBottomView() {
        rlShare.setOnClickListener(this);
        rlLiuyan.setOnClickListener(this);
        //根据书的状态改变不同的状态
        String status  = item.getStatue();
        if(!status.contains("出售")) {
            llWntBuy.setVisibility(View.GONE);
        }
        if(!status.contains("求购")){
            llHaveSell.setVisibility(View.GONE);
        }
        if(!status.contains("赠送") || status.contains("求赠送")){
            llAskGive.setVisibility(View.GONE);
            tvSendCondition.setVisibility(View.INVISIBLE);
        }
        if(!status.contains("求赠送")) {
            llGiveHe.setVisibility(View.GONE);
        }
        btnBack.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        //初始状态下隐藏评论试图
        rlBottomCommend.setVisibility(View.GONE);
    }

    private void initHandler() {
        hanGetDetail = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                pb.setVisibility(View.GONE);
                btnWantBuy.setOnClickListener(BookDetailActivity.this);
                haveSell.setOnClickListener(BookDetailActivity.this);
                giveHe.setOnClickListener(BookDetailActivity.this);
                askGive.setOnClickListener(BookDetailActivity.this);
                switch (msg.what) {
                    case 0:
                        StringBuilder sb = new StringBuilder();
                        tvIsbn.setText(sb.append("ISBN号：").append(BookOtherData.getInstance().getISBN()));
                        sb = null;
                        tvOwner.setText(BookOtherData.getInstance().getOwnerName());
                        try {
                            imageLoader.get(BookOtherData.getInstance().getHeadUrl(), ImageLoader.getImageListener(userIcon, R.drawable.def, R.drawable.def));
                        } catch (Exception e) {
                            Log.e(TAG,"error in get UserIcon",e);
                        }
                        for(int i = 0;i<BookOtherData.getInstance().getList().size();i++) {
                            ll.addView(initCommentsView(BookOtherData.getInstance().getList().get(i).get("thirdName"),
                                    BookOtherData.getInstance().getList().get(i).get("mark1"),
                                    BookOtherData.getInstance().getList().get(i).get("Head"),
                                    BookOtherData.getInstance().getList().get(i).get("send_time")),0);
                        }
                        break;
                    case 1:
                        Toast.makeText(BookDetailActivity.this,"加载评论失败",Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        break;
                    default:
                        break;
                }
            }
        };
    }

    //评论成功后的操作
    private void afterComment() {
        ll.addView(initCommentsView("我",comment,
                "http://c.hiphotos.baidu.com/image/pic/item/f3d3572c11dfa9ec78e256df60d0f703908fc12e.jpg",getTime()),0);
        changeBottomView(1);
    }

    //绘制评论列表
    private View initCommentsView(String name,String content,String headUrl,String time){
        View v = LayoutInflater.from(this).inflate(R.layout.comments_list_item,null);
        ImageView imageView = (ImageView) v.findViewById(R.id.img_comment_photo);
        TextView tvName = (TextView) v.findViewById(R.id.tv_comment_name);
        TextView tvContent = (TextView) v.findViewById(R.id.tv_comment_content);
        TextView tvSendTime = (TextView) v.findViewById(R.id.tv_comment_time);
        tvName.setText(name);
        tvContent.setText(content);
        tvSendTime.setText(time);
        if(imageLoader == null) {
            imageLoader = Mysingleton.getInstance(this).getImageLoader();
        }
        try {
            imageLoader.get(headUrl,ImageLoader.getImageListener(imageView,R.drawable.def,R.drawable.def),20,20);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
        map = null;
        imageLoader = null;
        BookOtherData.getInstance().getList().clear();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_detail_liuyan:
                //替换底部View
               changeBottomView(0);
                break;
            case R.id.btn_back:
                changeBottomView(1);
                break;
            case R.id.btn_send_comment:
                CharSequence str = etCommend.getText();
                if(!TextUtils.isEmpty(str)) {
                    comment = str.toString();
                    str = null;
                    sendComment();
                }
                break;
            case R.id.rl_detail_share:
                ShareFragment.getInstance(item.getImgUrl(),itemId).show(getSupportFragmentManager(),"share");
                break;
            case R.id.btn_want_buy:
                new ContactDialogFragment().show(getSupportFragmentManager(),"contact");
                break;
            case R.id.btn_have_sell:
                new ContactDialogFragment().show(getSupportFragmentManager(),"contact");
                break;
            case R.id.btn_give_he:
                new ContactDialogFragment().show(getSupportFragmentManager(),"contact");
                break;
            case R.id.btn_ask_give:
                new ContactDialogFragment().show(getSupportFragmentManager(),"contact");
                break;
            default:
                break;
        }
    }

    //改变底部试图
    private void changeBottomView(int flag){
        if(flag == 0) {
            //从默认视图变成评论视图
            llBuyAction.setVisibility(View.GONE);
            rlBottomCommend.setVisibility(View.VISIBLE);
        }else {
            //从评论视图返回默认视图
            etCommend.setText("");
            rlBottomCommend.setVisibility(View.GONE);
            llBuyAction.setVisibility(View.VISIBLE);
        }
    }

    //发送评论
    private void sendComment() {
        //先隐藏软件盘
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etCommend.getWindowToken(),0);
        imm = null;
        CommentData data = CommentData.getInstance();
        data.setBookId(itemId);
        data.setSendTime(getTime());
        data.setContent(comment);
        if(hanComment == null) {
            initCommentHandler();
        }
        HttpTask task = new HttpTask(this,Constants.CommentsLink
                , hanComment,TAG,"post");
        try {
            HttpManager.startTask(task);
        } catch (Exception e) {
            Log.e(TAG,"error in sendComment",e);
        }
    }

    private void initCommentHandler() {
        hanComment = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        //评论提交成功
                        Toast.makeText(BookDetailActivity.this, "评论成功！", Toast.LENGTH_SHORT).show();
                        afterComment();
                        break;
                    case 1:
                        //评论失败
                        Toast.makeText(BookDetailActivity.this,"评论失败！稍后再试。",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
    }

    //得到当前时间并格式化
    private String getTime(){
        SimpleDateFormat format = new SimpleDateFormat("mm-dd  hh:mm");
        return format.format(new Date());
    }

}
