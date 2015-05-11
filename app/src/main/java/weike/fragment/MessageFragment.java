package weike.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.adapter.MessageAdapter;
import weike.data.BookMessageListData;
import myinterface.MessageChangeListener;
import weike.shutuier.MainActivity;
import weike.shutuier.MessageActivity;
import weike.shutuier.R;
import weike.util.Constants;
import weike.util.HttpManager;
import weike.util.HttpTask;

/**
 * Created by Rth on 2015/2/9.
 */
public class MessageFragment extends Fragment {

    @InjectView(R.id.listview_message)
    ListView listView;
    @InjectView(R.id.loading_message)
    RelativeLayout rlLoading;
    @InjectView(R.id.tv_loading)
    TextView tvLoading;
    @InjectView(R.id.progressBar_loading)
    ProgressBar pbLoading;

    private  Context context;
    private SharedPreferences sp;
    private BookMessageListData data = null;
    private MessageAdapter adapter;
    private Handler han;

    private MessageChangeListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = context.getSharedPreferences(Constants.SP_USER,0);
        data = BookMessageListData.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_message,container,false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        ButterKnife.inject(this,v);
        adapter = new MessageAdapter(context,data.getList());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ItemId = data.getList().get(position).getId();
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra(Constants.Book_Id,ItemId);
                startActivity(intent);
                intent = null;
                listener.messageNumChange();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int num = sp.getInt(Constants.MESSAGE_NUMBER,0);
        if(num > 0) {
            initData();
        }
    }

    private void initData() {
        if(MainActivity.netConnect) {
            rlLoading.setVisibility(View.VISIBLE);
            if(han == null) {
                initHandler();
            }
            try {
//                HttpTask task = new HttpTask(context,Constants.
//                        MESSAGELISTLINK+sp.getString(Constants.UID,""),han,null,null);
                HttpTask task = new HttpTask(context,Constants.MESSAGELISTLINK+"A19CB7051089EFF8C95C755E7F93008E",han,null,null);
                HttpManager.startTask(task);
            }catch (Exception  e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(context,"网络不可用",Toast.LENGTH_SHORT).show();
        }
    }

    private void initHandler() {
        han = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                rlLoading.setVisibility(View.INVISIBLE);
                adapter.updataData(data.getList());
                checkData();
            }
        };
    }

    private void checkData() {
        if(adapter.getCount() == 0) {
            rlLoading.setVisibility(View.VISIBLE);
            pbLoading.setVisibility(View.GONE);
            tvLoading.setText("未获取到消息，稍后再试");
        }
    }

    public static MessageFragment getInstance() {
        return new MessageFragment();
    }

    public void setContext(Context context) {
        this.context = null;
        this.context = context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sp = null;
    }

    public void setMessageChangListener(MessageChangeListener listener) {
        this.listener = null;
        this.listener = listener;
    }

}
