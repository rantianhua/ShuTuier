package weike.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.SyncListener;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.Reply;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.shutuier.R;

/**
 * Created by Rth on 2015/4/17.
 * 自定义反馈界面
 */
public class FeedbackFragment extends Fragment implements View.OnClickListener{

    @InjectView(R.id.swipe_feedback)
    SwipeRefreshLayout refreshLayout;
    @InjectView(R.id.listview_feedback)
    ListView listView;
    @InjectView(R.id.btn_feedback_send)
    Button btnSend;
    @InjectView(R.id.et_feedback)
    EditText etFeedback;

    private Conversation mComversation = null;
    private ReplyAdapter adapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mComversation = new FeedbackAgent(getActivity()).getDefaultConversation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_feedback,container,false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        ButterKnife.inject(this,v);
        btnSend.setOnClickListener(this);
        adapter = new ReplyAdapter();
        listView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sync();
            }
        });
    }

    @Override
    public void onClick(View v) {
        //发送反馈
        String content = etFeedback.getText().toString();
        etFeedback.getEditableText().clear();
        if(!TextUtils.isEmpty(content)) {
            //添加内容到会话列表
            mComversation.addUserReply(content);
            //刷新ListView
            adapter.notifyDataSetChanged();
            //数据同步
            sync();
        }
    }

    // 数据同步
    private void sync() {

        mComversation.sync(new SyncListener() {

            @Override
            public void onSendUserReply(List<Reply> replyList) {
            }

            @Override
            public void onReceiveDevReply(List<Reply> replyList) {
                // SwipeRefreshLayout停止刷新
                refreshLayout.setRefreshing(false);
                // 刷新ListView
                adapter.notifyDataSetChanged();
                scrollToBottom();
            }
        });
    }

    private void scrollToBottom() {
        listView.setSelection(adapter.getCount()-1);
    }

    //会话数据的Adapter
    class ReplyAdapter extends BaseAdapter {

        @Override
        public int getItemViewType(int position) {
            //获取单条回复
            Reply reply = mComversation.getReplyList().get(position);
            if(Reply.TYPE_DEV_REPLY.equals(reply.type)) {
                //开发者回复布局
                return 0;
            }else {
                return 1;
            }
        }

        @Override
        public int getViewTypeCount() {

            return 2;
        }

        @Override
        public int getCount() {
            return mComversation.getReplyList().size();
        }

        @Override
        public Object getItem(int position) {
            return mComversation.getReplyList().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            Reply reply = mComversation.getReplyList().get(position);
            if(convertView == null) {
                if(Reply.TYPE_DEV_REPLY.equals(reply.type)) {
                    //开发者
                    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_feeback_left,null);
                }else {
                    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_feeback_right,null);
                }
                holder = new ViewHolder();
                holder.replyContent = (TextView) convertView.findViewById(R.id.tv_feedback_content);
                holder.replyTime = (TextView) convertView.findViewById(R.id.tv_feedback_time);
                holder.replyProgressBar = (ProgressBar) convertView.findViewById(R.id.progressBar_feedback);
                holder.replyFailed = (ImageView) convertView.findViewById(R.id.img_reply_failed);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            //填充数据
            holder.replyContent.setText(reply.content);
            if(!Reply.TYPE_DEV_REPLY.equals(reply.type)) {
                //根据发送状态决定显示发送失败的图片
                if(Reply.STATUS_NOT_SENT.equals(reply.status)) {
                    holder.replyFailed.setVisibility(View.VISIBLE);
                }else {
                    holder.replyFailed.setVisibility(View.GONE);
                }
                //progressBar的状态
                if(Reply.STATUS_SENDING.equals(reply.status)){
                    holder.replyProgressBar.setVisibility(View.VISIBLE);
                }else {
                    holder.replyProgressBar.setVisibility(View.GONE);
                }
            }

            //显示回复的时间
            if((position + 1) < mComversation.getReplyList().size()) {
                Reply nextReply = mComversation.getReplyList().get(position+1) ;
                if(nextReply.created_at - reply.created_at > 100000) {
                    Date replyTime  =  new Date(reply.created_at);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    holder.replyTime.setText(format.format(replyTime));
                    holder.replyTime.setVisibility(View.VISIBLE);
                }
            }
            return convertView;
        }

        class ViewHolder {
            TextView replyContent;
            ProgressBar replyProgressBar;
            TextView replyTime;
            ImageView replyFailed;
        }
    }
}
