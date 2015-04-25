package weike.fragment.hometabs;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import weike.adapter.BookListAdapter;
import weike.data.BookItem;
import weike.data.ListBookData;
import weike.shutuier.BookDetailActivity;
import weike.shutuier.MainActivity;
import weike.shutuier.R;
import weike.util.Constants;
import weike.util.HttpManager;
import weike.util.HttpTask;

/**
 * Created by Rth on 2015/2/14.
 */
public class GiveFragment extends Fragment   implements SwipeRefreshLayout.OnRefreshListener
    ,AdapterView.OnItemClickListener{

    @InjectView(R.id.rl_tab_loading)
    RelativeLayout rlLoading;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;
    @InjectView(R.id.listview)
    ListView listView;

    private static GiveFragment fragment  = null;
    private final String dataLink = Constants.OLink+"3";
    private BookListAdapter adapter = null;
    private ListBookData data = null;
    private Handler handler = null;
    private Boolean isDataInited = false;
    private ProgressBar pb = null;
    private TextView tvMessage = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tab,container,false);
        initView(v);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = ListBookData.getInstance(Constants.TYPE_4);
    }


    private void initView(View v) {
        ButterKnife.inject(this,v);

        rlLoading.setVisibility(View.VISIBLE);
        pb = (ProgressBar) rlLoading.findViewById(R.id.progressBar_loading);
        tvMessage = (TextView) rlLoading.findViewById(R.id.tv_loading);

        refreshLayout.setColorSchemeResources(R.color.section_selected);
        refreshLayout.setOnRefreshListener(this);
        adapter = new BookListAdapter(data.getList(),getActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(!isDataInited) {
            initData();
        }else {
            stopLoading();
        }
    }

    private void initData() {
        isDataInited = true;
        getData();
    }

    private void getData(){
        if(MainActivity.netConnect) {
            if(handler == null) {
                initHandler();
            }
            //开始网络任务，下载数据
            HttpTask textbookTask = new HttpTask(getActivity(),dataLink,handler,Constants.TYPE_4,null);
            HttpManager.startTask(textbookTask);
        }else  {
            stopLoading();
            Toast.makeText(getActivity(),"网络不可用",Toast.LENGTH_SHORT).show();
        }
    }

    private void initHandler() {
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                stopLoading();
                switch (msg.what) {
                    case 0:
                        adapter.updateData(data.getList());
                        checkNULLData();
                        break;
                    case 1:
                        Toast.makeText(getActivity(), "哎呀！下载出了问题", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(getActivity(),"哎呀！解析出了问题",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void checkNULLData() {
        //检查数据源是否为空
        if(adapter.getCount() == 0) {
            remindNullData();
        }
    }

    private void remindNullData() {
        //提示用户没有加载到数据
        if(rlLoading.getVisibility() == View.INVISIBLE) {
            rlLoading.setVisibility(View.VISIBLE);
        }
        pb.setVisibility(View.INVISIBLE);
        tvMessage.setText("暂时没有数据，稍后刷新试试。");
    }

    public static GiveFragment getInstance() {
        if(fragment == null) {
            fragment = new GiveFragment();
        }
        return fragment;
    }

    private void stopLoading() {
        if(rlLoading.getVisibility() == View.VISIBLE) rlLoading.setVisibility(View.INVISIBLE);
        if(refreshLayout.isRefreshing()) refreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        stopLoading();
        data.getList().clear();
        getData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BookItem item = (BookItem)listView.getAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), BookDetailActivity.class);
        intent.putExtra(Constants.EXTRA_ITEM_ID,item.getId());
        intent.putExtra(Constants.REQUEST_FROM_FRAGMENT,Constants.TYPE_4);
        startActivity(intent);
    }
}
